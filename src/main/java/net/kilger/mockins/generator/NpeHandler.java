package net.kilger.mockins.generator;

import static net.kilger.mockins.generator.LOG.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.kilger.mockins.generator.model.FieldInfo;
import net.kilger.mockins.generator.model.ParamInfo;
import net.kilger.mockins.generator.model.Stubbing;
import net.kilger.mockins.generator.model.SubstitutableObjectInfo;
import net.kilger.mockins.generator.result.model.CompositeInstruction;
import net.kilger.mockins.generator.result.model.CreateFieldMockInstruction;
import net.kilger.mockins.generator.result.model.CreateParameterMockInstruction;
import net.kilger.mockins.generator.result.model.CreateValueInstruction;
import net.kilger.mockins.generator.result.model.GenericInstruction;
import net.kilger.mockins.generator.result.model.Instruction;
import net.kilger.mockins.generator.result.model.StubInstruction;
import net.kilger.mockins.generator.valueprovider.ValueProvider;
import net.kilger.mockins.generator.valueprovider.ValueProviderRegistry;
import net.kilger.mockins.generator.valueprovider.impl.NullValueProvider;
import net.kilger.mockins.util.ReflectionUtil;
import net.kilger.mockins.util.mocking.MockHelper;
import net.kilger.mockins.util.mocking.MockHelperHolder;


public class NpeHandler {

    private final Object classUnderTest;
    private final Method method;
    private final Object[] initialArgs;
    private boolean hasNpe;
    private int argLength;
    private NullPointerException latestNpe;

    private MockHelper mockHelper = MockHelperHolder.getMockHelper();
    private List<ParamInfo> paramInfos;
    private List<FieldInfo> fieldInfos;

    public NpeHandler(Object classUnderTest, Method method, Object[] initialArgs) {
        this.classUnderTest = classUnderTest;
        this.method = method;
        this.initialArgs = initialArgs;
    }

    public Instruction handle() {
        l("handle");
        init();

        retry();
        if (!hasNpe) {
            l("no npe - nothing to do");
            return null;
        }

        l("fill all params and fields");
        fillAllParams();
        fillAllFields();

        retry();
        if (!hasNpe) {
            l("no npe after subst params and fields");

            l("removing unneccessary params and fields");
            removeNullableParams();
            removeNullableFields();
            
            return resultInstruction();
        }

        l("still npe after subst params and fields");

        createAllStubbingsForParams();
        createAllStubbingsForFields();

        retry();
        if (!hasNpe) {
            l("ok now after stubbing all");
            verifyAllStubsReproducable();

            l("removing unneccessary params and fields");
            removeNullableParams();
            removeNullableFields();
            
            l("removing unneccessary stubs for params and fields");
            removeUnneccesaryStubbingsForParams();
            removeUnneccesaryStubbingsForFields();
            
            return resultInstruction();
        }

        l("stubbed all, still NPE - nothing we can do...");
        throw latestNpe;
    }

    private Instruction resultInstruction() {
        GenericInstruction instruction = new GenericInstruction("ok with the following mocks, stubbings and parameters:\n");

        for (FieldInfo fieldInfo : fieldInfos) {
            String targetName = "classUnderTest." + fieldInfo.getField().getName();
            
            if (fieldInfo.isMock()) {
                CompositeInstruction mockFieldInstruction = new CreateFieldMockInstruction(targetName, fieldInfo.getType(), fieldInfo.getValueProvider().code());

                addStubbingInstructions(mockFieldInstruction, targetName, fieldInfo);

                instruction.addComponent(mockFieldInstruction);
            }
            else {
                if (fieldInfo.isValueChanged()) {
                    CreateValueInstruction createValueInstruction = new CreateValueInstruction(targetName, fieldInfo.getValueProvider().code());
                    instruction.addComponent(createValueInstruction);
                }
            }
        }
        
        for (SubstitutableObjectInfo paramInfo : paramInfos) {
            String paramName = paramInfo.getDisplayName();
            
            if (paramInfo.isMock()) {
                CreateParameterMockInstruction mockArgumentInstruction = new CreateParameterMockInstruction(paramName, paramInfo.getType(), paramInfo.getValueProvider().code());
                
                addStubbingInstructions(mockArgumentInstruction, paramName, paramInfo);
                
                instruction.addComponent(mockArgumentInstruction);
            }
            else {
                // print parameter values even if unchanged
                CreateValueInstruction createValueInstruction = new CreateValueInstruction(paramName, paramInfo.getValueProvider().code());
                instruction.addComponent(createValueInstruction);
            }
            
        }
        return instruction;
    }

    private void addStubbingInstructions(CompositeInstruction createMockInstruction, String paramName, SubstitutableObjectInfo soi) {
        for (Stubbing stubbing : soi.getStubbings()) {
            if (stubbing.isEmpty()) {
                continue;
            }
            StubInstruction stubInstruction = new StubInstruction(paramName, stubbing);
            createMockInstruction.addComponent(stubInstruction);
        }
    }

    private void removeUnneccesaryStubbingsForParams() {
        removeUnnecessaryStubbingsFor(paramInfos);
    }
    
    private void removeUnneccesaryStubbingsForFields() {
        removeUnnecessaryStubbingsFor(fieldInfos);
    }

    private void removeUnnecessaryStubbingsFor(List<? extends SubstitutableObjectInfo> substitutableObjectInfos) {
        for (SubstitutableObjectInfo substitutableObjectInfo : substitutableObjectInfos) {
            for (int j = 0; j < substitutableObjectInfo.getStubbings().size(); j++) {
                Stubbing stubbingToTest = substitutableObjectInfo.getStubbings().get(j);
                substitutableObjectInfo.getStubbings().set(j, Stubbing.EMPTY);
                retry();
                if (!hasNpe) {
                    l("not required: " + stubbingToTest);
                }
                else {
                    l("required: " + stubbingToTest);
                    substitutableObjectInfo.getStubbings().set(j, stubbingToTest);
                }
            }
        }
    }

    private void verifyAllStubsReproducable() throws AssertionError {
        retry();
        if (hasNpe) {
            throw new AssertionError("something went wrong");
        }
    }

    private void createAllStubbingsForParams() {
        createAllStubbingsFor(paramInfos);
    }
    
    private void createAllStubbingsForFields() {
        createAllStubbingsFor(fieldInfos);
    }

    private void createAllStubbingsFor(List<? extends SubstitutableObjectInfo> substitutableObjectInfos) {
        for (SubstitutableObjectInfo substitutable : substitutableObjectInfos) {

            if (substitutable.isMock()) {
                l(substitutable.getDisplayName() + " stubbing");

                /* 
                 * Use reflection util to access even protected/default methods.
                 * Note that these *might* not be accessible from the client,
                 * but we don't know at this point...
                 */
                Method[] methods = new ReflectionUtil(substitutable.getType()).getAllMethods().toArray(new Method[] {});
                
                for (Method method : methods) {

                    if (!shouldBeStubbed(method)) {
                        // l("not stubbing: " + method);
                        continue;
                    }
                    
                    Class<?> returnType = method.getReturnType();
                    Class<?>[] methodParamTypes = method.getParameterTypes();
                    int methodParamCount = methodParamTypes.length;
                    l("  stub " + method + " with " + methodParamCount + " params and returntype " + returnType);

                    ValueProvider<?> vp = ValueProviderRegistry.providerFor(returnType);

                    Stubbing stubbing = new Stubbing(method, vp, methodParamTypes);
                    substitutable.getStubbings().add(stubbing);
                }
            } else {
                l(substitutable.getDisplayName() + " skip " + substitutable.getCurrentValue());
            }
        }
    }

    private boolean shouldBeStubbed(Method method) {
        return needsToBeStubbed(method) && canBeStubbed(method);
    }

    private boolean needsToBeStubbed(Method method) {
        Class<?> returnType = method.getReturnType();

        if (returnType.equals(Void.TYPE)) {
            return false; // FIXME: this is actually up to the mocking implementation to decide!
        }

        if (returnType.isPrimitive()) {
            return false; // FIXME: this is actually up to the mocking implementation to decide!
        }
        
        return true;
    }

    private boolean canBeStubbed(Method method) {
        if (Modifier.isFinal(method.getModifiers())) {
            return false; // FIXME: this is actually up to the mocking implementation to decide!
        }

        // FIXME: rather getDeclaringClass() not Object?
        List<String> disallowed = Arrays.asList("equals", "hashCode", "getClass", "toString");
        if (disallowed.contains(method.getName())) {
            return false;
        }
        
        return true;
    }

    private void removeNullableParams() {
        // try out, which params are actually mandatory
        trySubstituteNullValueProvider(paramInfos);
    }

    private void removeNullableFields() {
        // try out, which fields are actually mandatory
        trySubstituteNullValueProvider(fieldInfos);
    }

    private void trySubstituteNullValueProvider(List<? extends SubstitutableObjectInfo> substitutables) {
        for (SubstitutableObjectInfo substitutable : substitutables) {
            String displayName = substitutable.getDisplayName();
            if (substitutable.wasInitiallyGiven()) {
                l("not touching: " + displayName);
                continue;
            }
            ValueProvider<?> originalVp = substitutable.getValueProvider();
            ValueProvider<Object> nullVp = new NullValueProvider();
            substitutable.setValueProvider(nullVp);
            
            retry();
            
            if (hasNpe) {
                l(displayName + ": may not be null");
                substitutable.setValueProvider(originalVp);
            }
            else {
                l(displayName + ": may be null");
            }
        }
    }
    
    private void retry() {
        buildCurrentValues();
        insertFields();
        applyStubsToMocksForParams();
        applyStubsToMocksForFields();
        retryInvocation();
    }

    private void buildCurrentValues() {
        for (SubstitutableObjectInfo paramInfo : paramInfos) {
            paramInfo.createValue();
        }
        for (SubstitutableObjectInfo fieldInfo : fieldInfos) {
            fieldInfo.createValue();
        }
    }

    private void insertFields() {
        for (FieldInfo fieldInfo : fieldInfos) {
            Field field = fieldInfo.getField();
            field.setAccessible(true); // FIXME: actually check?
            l("setting field " + field + " to " + fieldInfo.getValueProvider().code());
            try {
                field.set(classUnderTest, fieldInfo.getCurrentValue());
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void applyStubsToMocksForParams() {
        applyStubsToMocksFor(paramInfos);
    }
    
    private void applyStubsToMocksForFields() {
        applyStubsToMocksFor(fieldInfos);
    }

    private void applyStubsToMocksFor(List<? extends SubstitutableObjectInfo> substitutableObjectInfos) {
        for (SubstitutableObjectInfo soi : substitutableObjectInfos) {
            if (soi.isMock()) {
                Object mock = soi.getCurrentValue();
                mockHelper.reset(mock);
                for (Stubbing stubbing : soi.getStubbings()) {
                    try {
                        mockHelper.addStub(mock, stubbing);
                    } catch (IllegalAccessException e) {
                        l("stubbing failed:" + e);
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        l("stubbing failed:" + e);
                        e.printStackTrace();
                    }
                }
                mockHelper.prepareMock(mock);
            }
        }
    }

    private void fillAllParams() {
        fillAllFor(paramInfos);
    }

    private void fillAllFields() {
        fillAllFor(fieldInfos);
    }

    private void fillAllFor(List<? extends SubstitutableObjectInfo> substitutableObjectInfos) {
        for (SubstitutableObjectInfo soi : substitutableObjectInfos) {
            if (!soi.wasInitiallyGiven()) {
                l("substitute null " + soi.getDisplayName());
                ValueProvider<?> vp = ValueProviderRegistry.providerFor(soi.getType());
                soi.setValueProvider(vp);
            }
        }
    }
    
    private void init() {
        argLength = initialArgs.length;
        
        collectParamInfo();
        
        collectFieldInfo();
    }

    private void collectFieldInfo() {
        fieldInfos = new ArrayList<FieldInfo>();
        Class<? extends Object> clazz = classUnderTest.getClass();
        
        for (Field candidateField : clazz.getDeclaredFields()) { // TODO: inherited fields! getFields() won't do either, fields might not be accessible in legacy code
            if (isInteresting(candidateField)) {
                candidateField.setAccessible(true);
                Object initialValue = null;
                try {
                    initialValue = candidateField.get(classUnderTest);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                FieldInfo fieldInfo = new FieldInfo(candidateField, initialValue); 
                fieldInfos.add(fieldInfo);
            }
        }
    }

    private boolean isInteresting(Field candidateField) {
        boolean isPrimitive = candidateField.getType().isPrimitive();
        boolean isStatic = Modifier.isStatic(candidateField.getModifiers());
        boolean isFinal = Modifier.isFinal(candidateField.getModifiers());
        return !isPrimitive && !isStatic && !isFinal;
    }

    private void collectParamInfo() {
        Class<?>[] paramTypes = method.getParameterTypes();

        paramInfos = new ArrayList<ParamInfo>();
        for (int i = 0; i < argLength; i++) {
            ParamInfo paramInfo = new ParamInfo(i, paramTypes[i], initialArgs[i]);
            paramInfos.add(paramInfo);
        }
    }

    private void retryInvocation() {
        Object[] currentArgs = determineCurrentArgs();
        hasNpe = false;
        latestNpe = null;
        
        try {
            method.invoke(classUnderTest, currentArgs);
            
        } catch (InvocationTargetException ite) {
            Throwable cause = ite.getCause();
            if (cause instanceof NullPointerException) {
                latestNpe = (NullPointerException) cause;
                hasNpe = true;
            }

        } catch (IllegalArgumentException e) {
            throw new Error("internal error: method parameter mismatch", e);
        } catch (IllegalAccessException e) {
            throw new Error("internal error: method not accessible", e);
        }
    }

    private Object[] determineCurrentArgs() {
        Object[] currentArgs = new Object[argLength];
        for (ParamInfo paramInfo : paramInfos) {
            currentArgs[paramInfo.getIndex()] = paramInfo.getCurrentValue();
        }
        return currentArgs;
    }

}

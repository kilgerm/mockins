package net.kilger.mockins.handler;

import java.lang.reflect.Method;
import java.util.List;

import net.kilger.mockins.analysis.model.FieldInfo;
import net.kilger.mockins.analysis.model.ParamInfo;
import net.kilger.mockins.util.MockinsContext;

public class InvocationContext {
    private Object classUnderTest;
    private Method method;
    private Object[] initialArgs;
    private int argLength;
    private List<ParamInfo> paramInfos;
    private List<FieldInfo> fieldInfos;
    private Throwable latestException;
    private boolean invocationCompletedWithoutError;
    private boolean timeoutHappened;
    /** timeout for invocation of the method to test in milliseconds */
    private int invocationTimeoutMillis = MockinsContext.INSTANCE.getInvocationTimeoutMillis();

    public Object getClassUnderTest() {
        return classUnderTest;
    }

    public void setClassUnderTest(Object classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Object[] getInitialArgs() {
        return initialArgs;
    }

    public void setInitialArgs(Object[] initialArgs) {
        this.initialArgs = initialArgs;
    }

    public int getArgLength() {
        return argLength;
    }

    public void setArgLength(int argLength) {
        this.argLength = argLength;
    }

    public List<ParamInfo> getParamInfos() {
        return paramInfos;
    }

    public void setParamInfos(List<ParamInfo> paramInfos) {
        this.paramInfos = paramInfos;
    }

    public List<FieldInfo> getFieldInfos() {
        return fieldInfos;
    }

    public void setFieldInfos(List<FieldInfo> fieldInfos) {
        this.fieldInfos = fieldInfos;
    }

    public Throwable getLatestException() {
        return latestException;
    }

    public void setLatestException(Throwable latestException) {
        this.latestException = latestException;
    }

    public boolean isInvocationCompletedWithoutError() {
        return invocationCompletedWithoutError;
    }

    public void setInvocationCompletedWithoutError(boolean invocationCompletedWithoutError) {
        this.invocationCompletedWithoutError = invocationCompletedWithoutError;
    }

    public boolean isTimeoutHappened() {
        return timeoutHappened;
    }

    public void setTimeoutHappened(boolean timeoutHappened) {
        this.timeoutHappened = timeoutHappened;
    }

    public int getInvocationTimeoutMillis() {
        return invocationTimeoutMillis;
    }

    public void setInvocationTimeoutMillis(int invocationTimeoutMillis) {
        this.invocationTimeoutMillis = invocationTimeoutMillis;
    }
}
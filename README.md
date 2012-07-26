Mockins
========

Mockins (mocking instructor) assists you with mocking in unit tests.


Mockins is for you if you are familiar with the following problem:  
* you have (legacy) code without unit tests
* naive invocation of the method to test yields a NullPointer
* reason: some parameter or field may not be null, or may not return null when called
* but it is far from clear where to start with mocking/stubbing, since there are many dependencies
* so you best shot is to mock and stub by trial and error...

  
## Example:

``` java
// This is the class you would like to test
MyClass classUnderTest = new MyClass();

// first, create an "instructed" wrapper
MyClass instructor = Mockins.instructor(classUnderTest);

// then call whatever method you would like to test...
// (you can leave arguments null)
instructed.myMethod(null, null);
```

Mockins then runs testMethod and tries to add mocks/stubs 
for null-valued parameters and fields so that the method does not throw a NPE anymore.
If it succeeds, the necessary mocks will be output (to stderr) like this:

```
//============================================================================
// * Mockins result *
//============================================================================

// method call will succeed with the following mocks and stubbings:
SomeOtherClass param0 = Mockito.mock(SomeOtherClass.class);
Mockito.when(param0.getData()).thenReturn("");
Object param1 = null;
classUnderTest.myMethod(param0, param1);
```

## Mocking Frameworks

Mockins supports the following mocking frameworks:
* EasyMock <http://easymock.org/>
* Mockito <http://mockito.googlecode.com>

TODO: version supported

## Requirements

* You need a Java 5+ JDK
* and one of the supported mocking frameworks on your (test) classpath
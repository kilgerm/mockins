Mockins
========

Mockins ("mocking instructor") is a code-generator for mock and stub code.
It's aim is to assist you when writing unit tests for existing code.

## More Info...

... can be found in the wiki: [https://github.com/kilgerm/mockins/wiki](https://github.com/kilgerm/mockins/wiki)

## Example:

``` java
// This is the class you would like to test
MyClass classUnderTest = new MyClass();

// first, create an "instructor" wrapper
MyClass instructor = Mockins.instructor(classUnderTest);

// then call whatever method you would like to test...
// (you can leave arguments null)
instructor.myMethod(null, null);
```

Mockins then runs testMethod and tries to add mocks/stubs 
for null-valued parameters and fields so that the method does not throw a NPE anymore.
If it succeeds, the necessary mocks will be output (to stderr) like this:

``` java
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
* Mockito <http://mockito.googlecode.com> (version 1.8.0 or newer)
* EasyMock <http://easymock.org/> (version 3.0 or newer)

If exactly one of those frameworks is present in the (test) classpath, Mockins will use that one.

## Mockins API

* run Mockins for a certain method:
``` java
MyClass instructor = Mockins.instructor(classUnderTest);
instructor.myMethod(null, null);
```
```

* manually set the mocking framework:
``` java
Mockins.option().useMockito();
```
or
``` java
Mockins.option().useEasyMock();
```
## Requirements

* You need a Java 5+ JDK
* and one of the supported mocking frameworks on your (test) classpath
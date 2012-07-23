Mockins
========

Mockins is a mock instructor. It's purpose is to help you dealing with mocking in unit tests.


Mockins is for you if you are familiar with the following problem:  
* you have (legacy) code without unit tests
* naive invocation of the method to test yields a NullPointer
* reason: some parameter or field may not be null, or may not return null when called
* but it is far from clear where to start with mocking/stubbing
* so you best try is to mock and stub everything...

  
## Example:

``` java
// This is the class you would like to test
MyClass classUnderTest = new MyClass();

// first, create an "instructed" wrapper
MyClass instructed = Mockins.instruct(classUnderTest);

// then call whatever method you would like to test...
instructed.testMethod(null);
```

Mockins then runs testMethod and tries to add mocks/stubs 
for parameters and fields so that the method does not throw a NPE anymore.
If it succeeds, the necessary mocks will be output (to stderr) like this:

```
ok with the following mocks, stubbings and parameters:
classUnderTest.field0 = EasyMock.createMock(B.class);
EasyMock.expect(classUnderTest.field0.getData()).andReturn(EasyMock.createMock(C.class));
EasyMock.replay(classUnderTest.field0);
classUnderTest.field1 = EasyMock.createMock(B.class);
EasyMock.replay(classUnderTest.field1);
```


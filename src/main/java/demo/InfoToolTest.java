package demo;

import net.kilger.mockins.Mockins;

import org.junit.Ignore;
import org.junit.Test;

public class InfoToolTest {

    InfoTool classUnderTest = new InfoTool();
    
    @Test
    @Ignore
    public void test() throws Exception {

        // usage: instead of
        //   classUnderTest.something(param0, new Object());
        // use Mockins.interceptor(...)
        InfoTool classUnderTestInterceptor = Mockins.interceptor(classUnderTest);
        classUnderTestInterceptor.something(null, new Object());
        
    }
    
}

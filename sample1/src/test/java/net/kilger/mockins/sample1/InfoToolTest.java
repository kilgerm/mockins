package net.kilger.mockins.sample1;

import net.kilger.mockins.Mockins;

import org.junit.Ignore;
import org.junit.Test;

public class InfoToolTest {

    InfoTool classUnderTest = new InfoTool();
    
    @Test
    @Ignore /* removed Ignore and run the test to see... */
    public void test() throws Exception {

        // this just yielded an NPE...
        //   classUnderTest.something(param0, new Object());
        
        // so we now use the instructed object
        InfoTool instructed = Mockins.instructor(classUnderTest);
        
        // this still throws an NPE, but also generates and prints
        // the mock code to get rid of it...
        instructed.something(null, new Object());
    }
    
}

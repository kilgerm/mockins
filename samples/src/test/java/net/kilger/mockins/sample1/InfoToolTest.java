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
        
        // so we now use the instructed object and wait for the output
        InfoTool instructed = Mockins.instruct(classUnderTest);
        instructed.something(null, new Object());
        // this still throws an NPE, but also generates the mock code to avoid it!
    }
    
}

package demo;

public class InfoTool {

    InfoSource source;
    
    public int something(InfoSource input, Object unused) {
        int sum = 0;
        
        sum += input.hashCode();
        sum += input.getData().length();
//        String data = source.getData();
        
        return sum;
    }
    
}

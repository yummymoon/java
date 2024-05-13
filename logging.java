//菜单
abstract class Curve {
    int num;
    String curname;
    static final int N = 15; 
    float[] depth;
    float[] curvedata;
    
    // 初始化depth和curvedata数组
    public Curve(int num) {
        this.num = num;
        this.depth = new float[num];
        this.curvedata = new float[num];
    }

    abstract public void processing();

}

public class logging {
    public static void main(String[] args) {
        
    }
}
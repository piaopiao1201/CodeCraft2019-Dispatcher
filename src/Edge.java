import java.util.Arrays;

public class Edge {
    int target;
    double[] disArr;
    int dis;
    public Edge(int target, int dis) {
        this.target = target;
        this.dis = dis;
        disArr=new double[9000];
        Arrays.fill(disArr,dis);
    }
}

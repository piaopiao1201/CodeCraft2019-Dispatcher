//车辆状态位置
public class Position {
    String roadId;//记录所在道路ID
    int startId;//路口起始Id
    int endId;//路口结束Id
    int pos;//位于道路的位置

    public Position(String roadId, int startId, int endId, int pos) {
        this.roadId = roadId;
        this.startId = startId;
        this.endId = endId;
        this.pos = pos;
    }
}

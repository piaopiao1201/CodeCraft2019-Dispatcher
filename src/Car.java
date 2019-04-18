import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Car {
    public static final int WAIT_TO_GO=1;
    public static final int STOP=2;
    public static final int LEFT=1;
    public static final int STRAIGHT=2;
    public static final int RIGHT=3;
    public static final int GOAL=99;
    //车辆id，始发地、目的地、最高速度、计划出发时间
    String id;
    int startId;
    int endId;
    int maxSpeed;//最大速度
    int curSpeed;//当前速度
    int planStartTime;//目标出发时间
    int turnDirect;//车辆要行驶至下一道路的方向
    int remainToGo=0;//转弯后因前车阻挡还需行驶的距离
    boolean isNeedCross=false;//标记是否需要出入路口
    boolean isPreset=false;
    boolean isPriority=false;
    Map<String,Integer> crossDisMap=new LinkedHashMap<String, Integer>();
    List<Integer> pathArr;//车辆的最优路径
    Position pos;//车辆在道路的位置
    int status;//标记行驶状态

    int driveIndex=0;
    public Car(String id, int startId, int endId, int maxSpeed,List<Integer> pathArr,int planStartTime,int remainToGo,boolean isPriority,boolean isPreset) {
        this.id = id;
        this.startId = startId;
        this.endId = endId;
        this.maxSpeed = maxSpeed;
        this.pathArr=pathArr;
        this.planStartTime = planStartTime;
        this.remainToGo=remainToGo;
        this.pos = new Position("-1",0,0,0);
        this.isPreset=isPreset;
        this.isPriority=isPriority;
    }
    public void update(Position pos,int status,int remainToGo){
        this.pos=pos;
        this.status=status;
        this.remainToGo=remainToGo;
        updateDirect();
    }
    public void updateDirect(){
        String curRoadId=this.pos.roadId;
        //int index=pathArr.lastIndexOf(Integer.valueOf(curRoadId))+1;
        int index=driveIndex+1;
        driveIndex++;
        if(index==pathArr.size()){
            this.turnDirect=Car.GOAL;
            return;
        }
        this.turnDirect=Graph.roadToDirect.get(curRoadId+"->"+pathArr.get(index));
    }
    public void initCrossDisMap(){

    }
}

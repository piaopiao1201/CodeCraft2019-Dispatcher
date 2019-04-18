import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Cross {
    String crossId;
    List<Road> road=new ArrayList<Road>();
    public Cross(String crossId){
        this.crossId=crossId;
    }
    public void addToRoad(Road tempRoad){
        road.add(tempRoad);
        roadSort();
    }
    public void roadSort(){
        road.sort(new Comparator<Road>() {
            @Override
            public int compare(Road o1, Road o2) {
                return o1.roadId.compareTo(o2.roadId);
            }
        });
    }
    public void crossDispatch(){
        for(int i=0;i<road.size();i++){
            patchRoad(i);
        }
    }
    public void patchRoad(int index){
        Road temp=road.get(index);
//        int maxLength=0;
//        for(int i=0;i<temp.carArr.size();i++){
//            maxLength=Math.max(maxLength,temp.carArr.get(i).size());
//        };
        Car tempCar=temp.findNextNeedTurn();
        if(tempCar==null){
            return;
        }
        if(tempCar.isPriority){
            switch (tempCar.turnDirect){
                case Car.GOAL:
                    Graph.allDispatchTime+=Graph.TimeClip-Graph.carTimeMap.get(Integer.valueOf(tempCar.id));
                    //Graph.allDispatchTime+=Graph.TimeClip-tempCar.planStartTime;
                    Graph.onRoadCar.remove(tempCar);
                    //到达目的地
                    temp.removeCar(tempCar);
                    if(!tempCar.isPreset){
                        Graph.addAnswer(tempCar);
                    }
                    patchRoad(index);
                    break;
                case Car.STRAIGHT:
                    String key=temp.roadId+":"+crossId+":"+"straight";
                    //找到目标方向的路径,注意正反向
                    Road targetRoad=Graph.roadAndStartToRoadMap.get(Graph.roadCrossDirectToRoad.get(key)+":"+crossId);
                    //获取答案解集，该路口是否有空位
                    int[] res=targetRoad.checkDriveWayNo(tempCar.maxSpeed,tempCar.pos.pos-1);
                    if(res[0]==FlagSet.ALL_STOP){
                        temp.setStop(tempCar.id,1,Car.STOP);
                        return;
                    }else if(res[0]==FlagSet.NOT_FIND){
                        return;
                    }else{
                        targetRoad.insertCar(tempCar, res[0], Car.STOP, res[1], 0);
                        temp.removeCar(tempCar);
                        patchRoad(index);
                    }
                    break;
                case Car.LEFT:
                    String key2=temp.roadId+":"+crossId+":"+"left";
                    Road targetRoad2=Graph.roadAndStartToRoadMap.get(Graph.roadCrossDirectToRoad.get(key2)+":"+crossId);
                    //要左转
                    if(targetRoad2==null){
                        System.out.println(1);
                    }
                    int[] res2=targetRoad2.checkDriveWayNo(tempCar.maxSpeed,tempCar.pos.pos-1);
                    if(res2[0]==FlagSet.ALL_STOP){
                        temp.setStop(tempCar.id,1,Car.STOP);
                        return;
                    }else if(res2[0]==FlagSet.NOT_FIND){
                        return;
                    }else{
                        String straightRoad=Graph.roadCrossDirectToRoad.get(targetRoad2.roadId+":"+this.crossId+":"+"straight");
                        //判断这条路是否存在
                        if(Graph.roadEndToBool.containsKey(straightRoad+":"+this.crossId)){
                            Road otherRoad = findRoadDisMap(straightRoad);
                            Car needTurnCar = otherRoad.findNextNeedTurn();
                            if(needTurnCar!=null&&needTurnCar.isPriority&&needTurnCar.turnDirect==Car.STRAIGHT){
                                return;
                            }
                        }
                        targetRoad2.insertCar(tempCar, res2[0], Car.STOP, res2[1], 0);
                        temp.removeCar(tempCar);
                        patchRoad(index);
                    }
                    break;
                case Car.RIGHT:
                    String key3=temp.roadId+":"+crossId+":"+"right";
                    Road targetRoad3=Graph.roadAndStartToRoadMap.get(Graph.roadCrossDirectToRoad.get(key3)+":"+crossId);
                    int[] res3=targetRoad3.checkDriveWayNo(tempCar.maxSpeed,tempCar.pos.pos-1);
                    if(res3[0]==FlagSet.ALL_STOP){
                        temp.setStop(tempCar.id,1,Car.STOP);
                        return;
                    }else if(res3[0]==FlagSet.NOT_FIND){
                        return;
                    }else{
                        //判断直行方向是否有冲突
                        String straightRoad=Graph.roadCrossDirectToRoad.get(targetRoad3.roadId+":"+this.crossId+":"+"straight");
                        if(Graph.roadEndToBool.containsKey(straightRoad+":"+this.crossId)){
                            Road otherRoad = findRoadDisMap(straightRoad);
                            Car needTurnCar = otherRoad.findNextNeedTurn();
                            if (needTurnCar != null && needTurnCar.isPriority&&needTurnCar.turnDirect == Car.STRAIGHT) {
                                return;
                            }
                        }

                        //判断是否与其他车道左转弯冲突
                        String leftRoad=Graph.roadCrossDirectToRoad.get(targetRoad3.roadId+":"+crossId+":"+"right");
                        if(Graph.roadEndToBool.containsKey(leftRoad+":"+this.crossId)){
                            Road otherRoad = findRoadDisMap(leftRoad);
                            Car needTurnCar = otherRoad.findNextNeedTurn();
                            if (needTurnCar != null && needTurnCar.isPriority&&needTurnCar.turnDirect == Car.LEFT) {
                                return;
                            }
                        }
                        targetRoad3.insertCar(tempCar,res3[0],Car.STOP,res3[1],0);
                        temp.removeCar(tempCar);
                        patchRoad(index);
                    }
                    break;
            }
        }else{
            switch (tempCar.turnDirect){
                case Car.GOAL:
                    Graph.onRoadCar.remove(tempCar);
                    Graph.allDispatchTime+=Graph.TimeClip-Graph.carTimeMap.get(Integer.valueOf(tempCar.id));
                    //Graph.allDispatchTime+=Graph.TimeClip-tempCar.planStartTime;
                    //到达目的地
                    temp.removeCar(tempCar);
                    Graph.nonPriorCarNum--;
                    if(!tempCar.isPreset){
                        Graph.addAnswer(tempCar);
                    }
                    patchRoad(index);
                    break;
                //目前还未处理转到该路口后遇到阻塞的情况
                case Car.STRAIGHT:
                    String key=temp.roadId+":"+crossId+":"+"straight";
                    //找到目标方向的路径,注意正反向
                    Road targetRoad=Graph.roadAndStartToRoadMap.get(Graph.roadCrossDirectToRoad.get(key)+":"+crossId);
                    //获取答案解集，该路口是否有空位
                    int[] res=targetRoad.checkDriveWayNo(tempCar.maxSpeed,tempCar.pos.pos-1);
                    if(res[0]==FlagSet.ALL_STOP){
                        temp.setStop(tempCar.id,1,Car.STOP);
                        return;
                    }else if(res[0]==FlagSet.NOT_FIND){
                        return;
                    }else{
                        String leftRoad=Graph.roadCrossDirectToRoad.get(targetRoad.roadId+":"+this.crossId+":"+"right");
                        if(Graph.roadEndToBool.containsKey(leftRoad+":"+this.crossId)){
                            Road otherRoad = findRoadDisMap(leftRoad);
                            Car needTurnCar = otherRoad.findNextNeedTurn();
                            if (needTurnCar != null && needTurnCar.isPriority&&needTurnCar.turnDirect == Car.LEFT) {
                                return;
                            }

                        }
                        String rightRoad=Graph.roadCrossDirectToRoad.get(targetRoad.roadId+":"+this.crossId+":"+"left");
                        if(Graph.roadEndToBool.containsKey(rightRoad+":"+this.crossId)){
                            Road otherRoad = findRoadDisMap(rightRoad);
                            Car needTurnCar = otherRoad.findNextNeedTurn();
                            if (needTurnCar != null && needTurnCar.isPriority&&needTurnCar.turnDirect == Car.RIGHT) {
                                return;
                            }
                        }
                        targetRoad.insertCar(tempCar,res[0],Car.STOP,res[1],0);
                        temp.removeCar(tempCar);
                        patchRoad(index);
                    }
                    break;
                case Car.LEFT:
                    String key2=temp.roadId+":"+crossId+":"+"left";
                    Road targetRoad2=Graph.roadAndStartToRoadMap.get(Graph.roadCrossDirectToRoad.get(key2)+":"+crossId);
                    //要左转
                    int[] res2=targetRoad2.checkDriveWayNo(tempCar.maxSpeed,tempCar.pos.pos-1);
                    if(res2[0]==FlagSet.ALL_STOP){
                        temp.setStop(tempCar.id,1,Car.STOP);
                        return;
                    }else if(res2[0]==FlagSet.NOT_FIND){
                        return;
                    }else{
                        String straightRoad=Graph.roadCrossDirectToRoad.get(targetRoad2.roadId+":"+this.crossId+":"+"straight");
                        //判断这条路是否存在
                        if(Graph.roadEndToBool.containsKey(straightRoad+":"+this.crossId)){
                            Road otherRoad = findRoadDisMap(straightRoad);
                            Car needTurnCar = otherRoad.findNextNeedTurn();
                            if(needTurnCar!=null&&needTurnCar.turnDirect==Car.STRAIGHT){
                                return;
                            }
                        }
                        String rightRoad=Graph.roadCrossDirectToRoad.get(targetRoad2.roadId+":"+this.crossId+":"+"left");
                        if(Graph.roadEndToBool.containsKey(rightRoad+":"+this.crossId)){
                            Road otherRoad = findRoadDisMap(rightRoad);
                            Car needTurnCar = otherRoad.findNextNeedTurn();
                            if(needTurnCar!=null&&needTurnCar.isPriority&&needTurnCar.turnDirect==Car.RIGHT){
                                return;
                            }
                        }
                        targetRoad2.insertCar(tempCar,res2[0],Car.STOP,res2[1],0);
                        temp.removeCar(tempCar);
                        patchRoad(index);
                    }
                    break;
                case Car.RIGHT:
                    String key3=temp.roadId+":"+crossId+":"+"right";
                    Road targetRoad3=Graph.roadAndStartToRoadMap.get(Graph.roadCrossDirectToRoad.get(key3)+":"+crossId);
                    int[] res3=targetRoad3.checkDriveWayNo(tempCar.maxSpeed,tempCar.pos.pos-1);
                    if(res3[0]==FlagSet.ALL_STOP){
                        temp.setStop(tempCar.id,1,Car.STOP);
                        return;
                    }else if(res3[0]==FlagSet.NOT_FIND){
                        return;
                    }else{
                        //判断直行方向是否有冲突
                        String straightRoad=Graph.roadCrossDirectToRoad.get(targetRoad3.roadId+":"+this.crossId+":"+"straight");
                        if(Graph.roadEndToBool.containsKey(straightRoad+":"+this.crossId)){
                            Road otherRoad = findRoadDisMap(straightRoad);
                            Car needTurnCar = otherRoad.findNextNeedTurn();
                            if (needTurnCar!=null&&needTurnCar.turnDirect==Car.STRAIGHT) {
                                return;
                            }
                        }
                        //判断是否与其他车道左转弯冲突
                        String leftRoad=Graph.roadCrossDirectToRoad.get(targetRoad3.roadId+":"+crossId+":"+"right");
                        if(Graph.roadEndToBool.containsKey(leftRoad+":"+this.crossId)){
                            Road otherRoad = findRoadDisMap(leftRoad);
                            Car needTurnCar = otherRoad.findNextNeedTurn();
                            if(needTurnCar!=null&&needTurnCar.turnDirect==Car.LEFT){
                                return;
                            }
                        }
                        targetRoad3.insertCar(tempCar, res3[0], Car.STOP, res3[1], 0);
                        temp.removeCar(tempCar);
                        patchRoad(index);
                    }
                    break;
            }
        }

    }
    public Road findRoadDisMap(String roadId){
        for(int i=0;i<road.size();i++){
            if(road.get(i).roadId.equals(roadId)){
                return road.get(i);
            }
        }
        return null;
    }
}

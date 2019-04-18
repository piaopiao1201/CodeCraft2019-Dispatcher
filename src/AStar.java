import java.util.*;

public class AStar {
    public static List<Integer> Search(int startCrossId, int endCrossId,int startTime){
        Map<Integer, Data> pointMapToData=new HashMap<Integer, Data>();
        Map<Integer,Boolean> flag=new HashMap<Integer, Boolean>();
        Queue<Data> minHeap=new PriorityQueue<Data>(new Comparator<Data>() {
            @Override
            public int compare(Data o1, Data o2) {
                if(o1.f()==o2.f()){
                    return 0;
                }
                return o1.f()>o2.f()?1:-1;
            }
        });
        boolean finish=false;
        Data lastData=null;
        heapAdd(pointMapToData,minHeap,startCrossId,startCrossId,0,0,null);
        while(!finish&&!minHeap.isEmpty()){
            Data temp=minHeap.poll();
            flag.put(temp.crossId,true);
            pointMapToData.remove(temp.crossId);
            if(temp.crossId==endCrossId){
                finish=true;
                lastData=temp;
                break;
            }
            for(int i=0;i<Graph.map.get(temp.crossId).size();i++){
                Edge targetCross=Graph.map.get(temp.crossId).get(i);
                if(flag.containsKey(targetCross.target)&&flag.get(targetCross.target)==true){
                    continue;
                }
                //coordinate curPoint=Graph.crossMapToZuo.get(targetCross.target);
                if(pointMapToData.containsKey(targetCross.target)){
                    //如果在堆里
                    Data quObj=pointMapToData.get(targetCross.target);
                    if(quObj.g>temp.g+targetCross.disArr[startTime]){
                        quObj.g=temp.g+targetCross.disArr[startTime];
                    }
                }else{
                    //double h=calH(curPoint,Graph.crossMapToZuo.get(endCrossId));
                    heapAdd(pointMapToData,minHeap,targetCross.target,targetCross.target,temp.g+targetCross.disArr[startTime],0,temp);
                    //Data newData=new Data(curPoint,temp.g+1,)
                }
            }
        }
        //输出最终路径
        List<Integer> res=new ArrayList<Integer>();
        for(Data curData = lastData; curData!=null; curData=curData.lastData){
            if(curData.lastData==null){
                break;
            }
            Data temp=curData.lastData;
            res.add(Integer.valueOf(Graph.RoadMapToDis.get(temp.crossId+"->"+curData.crossId).roadId));
            for(int i=startTime;i<startTime+30;i++){
                Graph.crossIdToEdge.get(temp.crossId+"->"+curData.crossId).disArr[i]+=(double)1/Graph.RoadMapToDis.get(temp.crossId+"->"+curData.crossId).carArr.size();
            }
        }
        return res;
    }

    public static void heapAdd(Map<Integer, Data> pointMapToData, Queue<Data> minHeap, int biaozhiId, int crossId, double g, double h, Data lastData){
        Data temp=new Data(g,h,lastData,crossId);
        minHeap.add(temp);
        pointMapToData.put(biaozhiId,temp);

    }
    public static double calH(coordinate point, coordinate endPoint){
        return hEuclidianDistance(point,endPoint);
    }
    public static double hEuclidianDistance(coordinate pnt, coordinate endPoint) {
        //以横轴和竖轴长度为衡量准则
        int startX=pnt.x;
        int endX=endPoint.x;
        int commonY=pnt.y;
        int startY=pnt.y;
        int endY=endPoint.y;
        int commonX=pnt.x;
        int disSum=0;
        if(endX<startX){
            int temp=endX;
            endX=startX;
            startX=temp;
        }
        if(endY<startY){
            int temp=endY;
            endY=startY;
            startY=temp;
        }
        for(int i=startX;i<endX;i++){
            coordinate originSrc=new coordinate(i,commonY);
            int originId=Graph.zuoMapToCross.get(originSrc.toString());
            int targetId=Graph.zuoMapToCross.get(new coordinate(i+1,commonY).toString());
            int curDis=0;
            if(Graph.RoadMapToDis.containsKey(originId+"->"+targetId)){
                curDis=Graph.RoadMapToDis.get(originId+"->"+targetId).distance;
            }
            if(Graph.RoadMapToDis.containsKey(targetId+"->"+originId)){
                curDis=Graph.RoadMapToDis.get(targetId+"->"+originId).distance;
            }
            disSum+=curDis;
        }
        for(int i=startY;i<endY;i++){
            coordinate originSrc=new coordinate(commonX,i);
            int originId=Graph.zuoMapToCross.get(originSrc.toString());
            int targetId=Graph.zuoMapToCross.get(new coordinate(commonX,i+1).toString());
            int curDis=0;
            if(Graph.RoadMapToDis.containsKey(originId+"->"+targetId)){
                curDis=Graph.RoadMapToDis.get(originId+"->"+targetId).distance;
            }
            if(Graph.RoadMapToDis.containsKey(targetId+"->"+originId)){
                curDis=Graph.RoadMapToDis.get(targetId+"->"+originId).distance;
            }
            disSum+=curDis;
        }
        return disSum;
    }
    public static List<List<Integer>> startSearch(List<String[]> carArr){
        List<List<Integer>> allRes=new ArrayList<List<Integer>>();
        for(int i=0;i<carArr.size();i++){
//            String temp=carArr.get(i);
//            temp=temp.substring(1,temp.length()-1).replace(" ","");
            String[] tempArr=carArr.get(i);
            if(tempArr[6].equals("1")){
                continue;
            }
            List<Integer> res=Search(Integer.valueOf(tempArr[1]),Integer.valueOf(tempArr[2]),Integer.valueOf(tempArr[4]));
            Collections.reverse(res);
            res.add(0,Integer.valueOf(tempArr[4]));
            res.add(0,Integer.valueOf(tempArr[0]));
            System.out.println("已调度:"+i+"辆车");
            allRes.add(res);
        }
        return allRes;
    }
}

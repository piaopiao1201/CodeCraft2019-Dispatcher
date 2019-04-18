import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Road {
    String roadId;//道路id号
    int distance;//道路长度
    int limitSpeed;//道路限速
    int startId;//起始cross结点id
    int endId;//结束cross结点id
    int carNum;
    List<List<Car>> carArr=new ArrayList<List<Car>>();//车道
    public Road(String roadId,int distance,int limitSpeed,int startId,int endId,int roadNum) {
        this.roadId = roadId;
        this.distance = distance;
        this.limitSpeed=limitSpeed;
        this.startId=startId;
        this.endId=endId;
        this.carNum=0;
        for(int i=0;i<roadNum;i++){
            carArr.add(new ArrayList<Car>());
        }
    }
    public int getCarNum(){
        int sum=0;
        for(int i=0;i<carArr.size();i++){
            sum+=carArr.get(i).size();
        }
        return sum;
    }
    //搜索能被插入的车道和所剩长度
    public int[] checkDriveWayNo(int carSpeed,int remainDisTance){
        int[] res=new int[4];
        //res[0]标记能否出入路口，如果可以返回能被插入的车道id号
        //res[1]标记能被插入的车道位置
        //res[2]标记前车为wait状态，且距离不够行驶时如果可以还需行驶的距离
        //res[3]标记是否是res[2]的状况

        //速度小到不足以转弯
        int speed=Math.min(this.limitSpeed,carSpeed);
        if(speed<=remainDisTance){
            res[0]=FlagSet.ALL_STOP;
            return res;
        }
        int k=0;
        for(;k<carArr.size();k++){
            List<Car> temp=carArr.get(k);
            if(temp.size()==0){
                break;
            }
            Car tempCar=temp.get(temp.size()-1);
            if(tempCar.status==Car.STOP&&tempCar.pos.pos==distance){
                continue;
            }else{
                break;
            }
        }
        //所有车道末尾都有车且为停止状态
        if(k==carArr.size()){
            res[0]=FlagSet.ALL_STOP;
            return res;
        }
        for(int i=0;i<carArr.size();i++){
            List<Car> temp=carArr.get(i);
            if(temp.size()==0){
                res[0]=i;
                res[1]=this.distance-(Math.min(this.limitSpeed,carSpeed)-remainDisTance)+1;
                return res;
            }
            int carDis=temp.get(temp.size()-1).pos.pos;
            //尾车位于道路末尾
            if(carDis==distance&&temp.get(temp.size()-1).status==Car.STOP){
                continue;
            }

            if(carDis==distance&&temp.get(temp.size()-1).status==Car.WAIT_TO_GO){
                res[0]=FlagSet.NOT_FIND;
                return res;
            }

            //前方有车但是没在道路尾端且距离之差足够行驶完所有距离
            if(speed-remainDisTance<=distance-carDis){
                res[0]=i;
                res[1]=distance-(speed-remainDisTance)+1;
                return res;
            }else{
                if(temp.get(temp.size()-1).status==Car.STOP){
                    res[0]=i;
                    res[1]=carDis+1;
                    return res;
                }else{
                    res[0]=FlagSet.NOT_FIND;
                    return res;
                    //前车wait，此时就不能动
//                    res[0]=i;
//                    res[1]=carDis+1;
//                    //如果前车动还可以走的距离
//                    res[2]=speed-remainDisTance-(distance-carDis);
//                    res[3]=FlagSet.FRONT_WAIT;
//                    return res;
                    //前车wait距离不够行驶 跳过
                    //continue;
                }
            }
        }
        //若没找到则在答案的0位置放置-1,即所有车都在末尾且都是wait状态
        res[0]=FlagSet.NOT_FIND;
        return res;
    }
    public void updateCarInRoad(){
        for(int i=0;i<carArr.size();i++){
            for(int j=0;j<carArr.get(i).size();j++){
                if(j==0){
                    Car tempCar=carArr.get(i).get(j);
                    if(tempCar.pos.pos-1<tempCar.curSpeed){
                        tempCar.status=Car.WAIT_TO_GO;
                        tempCar.isNeedCross=true;
                    }else{
                        tempCar.pos.pos=tempCar.pos.pos-tempCar.curSpeed;
                        tempCar.status=Car.STOP;
                        tempCar.isNeedCross=false;
                    }
                }else{
                    Car tempCurCar=carArr.get(i).get(j);
                    Car tempPostCar=carArr.get(i).get(j-1);
                    //当前车与前车距离小于当前速度
                    if(tempCurCar.pos.pos-tempPostCar.pos.pos-1<tempCurCar.curSpeed){
                        if(tempPostCar.status==Car.STOP){
                            //前车如果为停止状态，标志改为Stop 位置设置为后一格
                            //tempCurCar.setStatus(Car.STOP);
                            tempCurCar.status=Car.STOP;
                            tempCurCar.pos.pos=tempPostCar.pos.pos+1;
                        }else{
                            tempCurCar.status=Car.WAIT_TO_GO;
                        }
                    }else{
                        //前车与前车距离大于当前速度，直接行驶相应距离即可
                        tempCurCar.pos.pos=tempCurCar.pos.pos-tempCurCar.curSpeed;
                        tempCurCar.status=Car.STOP;
                    }
                }
            }
        }
    }
    public void insertCar(Car target, int driveWayNo,int status,int placeSrc,int remainToGo){
        List<Car> targetDriveway=carArr.get(driveWayNo);
        if(targetDriveway.size()==0){
            target.curSpeed=Math.min(target.maxSpeed,this.limitSpeed);
            Position pos=new Position(roadId,startId,endId,placeSrc);
            target.update(pos,status,remainToGo);
            targetDriveway.add(target);
            return;
        }
        Car compareCar=targetDriveway.get(targetDriveway.size()-1);
        Position pos=new Position(roadId,startId,endId,placeSrc);
        target.update(pos,status,remainToGo);//改动了车辆运行方向
        //插入车辆调整位置和速度
        //target.pos.pos=Math.max(target.pos.pos,compareCar.pos.pos+1);
        //target.curSpeed=Math.min(target.maxSpeed,Math.min(this.limitSpeed,compareCar.curSpeed));
        target.curSpeed=Math.min(target.maxSpeed,this.limitSpeed);
        targetDriveway.add(target);
    }
    public Car removeCar(Car targetCar){
        int i=findIndexByCar(targetCar);
        if(i==-1){
            System.out.println(1);
        }
        Car removeCar=carArr.get(i).remove(0);
        for(int j=0;j<carArr.get(i).size();j++){
            Car temp=carArr.get(i).get(j);
            if(temp.status==Car.STOP){
                break;
            }else{
                if(j==0){
                    temp.curSpeed=Math.min(temp.maxSpeed,this.limitSpeed);
                    //后面是车辆能否转弯的判断
                    if(temp.pos.pos-1<temp.curSpeed){
                        temp.status=Car.WAIT_TO_GO;
                        temp.isNeedCross=true;
                    }else{
                        temp.pos.pos=temp.pos.pos-temp.curSpeed;
                        temp.status=Car.STOP;
                        temp.isNeedCross=false;
                    }
                }else{
                    temp.curSpeed=Math.min(temp.maxSpeed,Math.min(this.limitSpeed,carArr.get(i).get(j-1).curSpeed));
                    Car tempPostCar=carArr.get(i).get(j-1);
                    //当前车与前车距离小于当前速度
                    if(temp.pos.pos-tempPostCar.pos.pos-1<temp.curSpeed){
                        if(tempPostCar.status==Car.STOP){
                            //前车如果为停止状态，速度改为和前车一样 标志改为Stop 位置设置为后一格
                            temp.status=Car.STOP;
                            temp.pos.pos=tempPostCar.pos.pos+1;
                        }else{
                            temp.status=Car.WAIT_TO_GO;
                        }
                    }else{
                        temp.pos.pos=temp.pos.pos-temp.curSpeed;
                        temp.status=Car.STOP;
                    }
                }

            }
        }
        return removeCar;
    }

    public Car removeAnyCar(Car targetCar){
        int[] indexArr=findIndexAnyByCar(targetCar);
        if(indexArr[0]==-1){
            System.out.println(1);
        }
        Car removeCar=carArr.get(indexArr[0]).remove(indexArr[1]);
        for(int j=indexArr[1];j<carArr.get(indexArr[0]).size();j++){
            Car temp=carArr.get(indexArr[0]).get(j);
            if(temp.status==Car.STOP){
                break;
            }else{
                if(j==0){
                    temp.curSpeed=Math.min(temp.maxSpeed,this.limitSpeed);
                    //后面是车辆能否转弯的判断
                    if(temp.pos.pos-1<temp.curSpeed){
                        temp.status=Car.WAIT_TO_GO;
                        temp.isNeedCross=true;
                    }else{
                        temp.pos.pos=temp.pos.pos-temp.curSpeed;
                        temp.status=Car.STOP;
                        temp.isNeedCross=false;
                    }
                }else{
                    temp.curSpeed=Math.min(temp.maxSpeed,Math.min(this.limitSpeed,carArr.get(indexArr[0]).get(j-1).curSpeed));
                    Car tempPostCar=carArr.get(indexArr[0]).get(j-1);
                    //当前车与前车距离小于当前速度
                    if(temp.pos.pos-tempPostCar.pos.pos-1<temp.curSpeed){
                        if(tempPostCar.status==Car.STOP){
                            //前车如果为停止状态，速度改为和前车一样 标志改为Stop 位置设置为后一格
                            temp.status=Car.STOP;
                            temp.pos.pos=tempPostCar.pos.pos+1;
                        }else{
                            temp.status=Car.WAIT_TO_GO;
                        }
                    }else{
                        temp.pos.pos=temp.pos.pos-temp.curSpeed;
                        temp.status=Car.STOP;
                    }
                }

            }
        }
        return removeCar;
    }

    //返回下一个需要转弯的车辆
    public Car findNextNeedTurn(){
        int maxLength=0;
        for(int i=0;i<carArr.size();i++){
            maxLength=Math.max(maxLength,carArr.get(i).size());
        }
        //如果所有道路上都没有车
        if(maxLength==0){
            return null;
        }
        //若非预置车辆还需指定方向
        //先处理有优先级车参与的情况
        for(int i=0;i<carArr.size();i++){
            if(carArr.get(i).size()==0){
                continue;
            }
            if(carArr.get(i).get(0).status==Car.STOP){
                continue;
            }
            if(carArr.get(i).get(0).isPriority){
                Car targetCar=carArr.get(i).get(0);
                return targetCar;
            }
        }
        boolean flag=false;
        for(int i=0;i<carArr.size();i++){
            if(carArr.get(i).size()==0){
                continue;
            }
            if(carArr.get(i).get(0).status==Car.STOP){
                continue;
            }
            Car targetCar=carArr.get(i).get(0);
            return targetCar;
        }
        return null;
//        int[] res=new int[carArr.size()];
//        Arrays.fill(res,Integer.MAX_VALUE);
//        boolean flag=false;
//        for(int i=0;i<carArr.size();i++){
//            if(carArr.get(i).size()==0){
//                continue;
//            }
//            if(carArr.get(i).get(0).status==Car.STOP){
//                continue;
//            }
//            res[i]=carArr.get(i).get(0).pos.pos;
//            flag=true;
//        }
//        if(!flag){
//            return null;
//        }
//        int minValue=Integer.MAX_VALUE;
//        int minIndex=-1;
//        for(int i=0;i<res.length;i++){
//            if(res[i]<minValue){
//                minValue=res[i];
//                minIndex=i;
//            }
//        }
        //return carArr.get(minIndex).get(0);
    }
    public void setStop(String carId,int pos,int status){
        //哪条车道已知，将选中的car设置为停止并更新后面车的状态
        int findColumn=0;
        int findRow=0;
        for(int i=0;i<carArr.size();i++){
            for(int j=0;j<carArr.get(i).size();j++){
                if(carArr.get(i).get(j).id.equals(carId)){
                    findColumn=j;
                    findRow=i;
                    Car temp=carArr.get(i).get(j);
                    temp.status=Car.STOP;
                    temp.pos.pos=pos;
                }
            }
        }
        for(int i=findColumn+1;i<carArr.get(findRow).size();i++){
            Car temp=carArr.get(findRow).get(i);
            if(temp.status==Car.STOP){
                break;
            }
            Car postCar=carArr.get(findRow).get(i-1);
            if(temp.remainToGo>0){
                if(temp.pos.pos-postCar.pos.pos<=temp.remainToGo){
                    temp.status=Car.STOP;
                    temp.pos.pos=postCar.pos.pos+1;
                }else{
                    temp.status=Car.STOP;
                    temp.pos.pos-=temp.remainToGo;
                }
                temp.remainToGo=0;
                continue;
            }
            if(temp.pos.pos-postCar.pos.pos-1<temp.curSpeed){
                //前车如果为停止状态，速度改为和前车一样 标志改为Stop 位置设置为后一格
                temp.status=(Car.STOP);
                temp.pos.pos=postCar.pos.pos+1;
            }else{
                temp.pos.pos=temp.pos.pos-temp.curSpeed;
                temp.status=Car.STOP;
            }
        }
    }
    public int findIndexByCar(Car tempCar){
        for(int i=0;i<carArr.size();i++){
            for(int j=0;j<carArr.get(i).size();j++){
                if(carArr.get(i).get(j).id.equals(tempCar.id)){
                    return i;
                }
            }
        }
        return -1;
    }

    public int[] findIndexAnyByCar(Car tempCar){
        int[] res=new int[2];
        for(int i=0;i<carArr.size();i++){
            for(int j=0;j<carArr.get(i).size();j++){
                if(carArr.get(i).get(j).id.equals(tempCar.id)){
                    res[0]=i;
                    res[1]=j;
                    return res;
                }
            }
        }
        res[0]=-1;
        return res;
    }
}

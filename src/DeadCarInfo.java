public class DeadCarInfo {
    int carId;
    int roadId;
    int crossId;
    int startCrossId;
    int speed;
    Car targetCar;
    public DeadCarInfo(int carId,int roadId,int crossId,int startCrossId,int speed,Car targetCar){
        this.carId=carId;
        this.roadId=roadId;
        this.crossId=crossId;
        this.startCrossId=startCrossId;
        this.speed=speed;
        this.targetCar=targetCar;
    }
}

public class Data {
    coordinate point;
    int crossId;
    //f=g+h
    double g;//原点距离该点
    double h;//该点距离终点
    Data lastData;
    double f(){
        return this.g+this.h;
    }
    public Data(double g,double h,Data lastData,int crossId){
        this.g=g;
        this.h=h;
        this.lastData=lastData;
        this.crossId=crossId;
    }
}

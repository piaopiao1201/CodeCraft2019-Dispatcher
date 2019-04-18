
import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader carBf=new BufferedReader(new FileReader("exam2/car.txt"));
        BufferedReader crossBf=new BufferedReader(new FileReader("exam2/cross.txt"));
        BufferedReader roadBf=new BufferedReader(new FileReader("exam2/road.txt"));
        //BufferedReader presetAnswerBf=new BufferedReader(new FileReader("train1/answerTxt.txt"));
        BufferedReader presetAnswerBf=new BufferedReader(new FileReader("exam2/presetAnswer.txt"));
        BufferedReader allAnswerBf=new BufferedReader(new FileReader("exam2/answer.txt"));
        List<String[]> crossArr=new ArrayList<String[]>();
        List<String[]> roadArr=new ArrayList<String[]>();
        List<String[]> carArr=new ArrayList<String[]>();
        List<String[]> presetAnswerArr=new ArrayList<String[]>();
        List<String[]> ansArr=new ArrayList<String[]>();
        String str;
        while((str=crossBf.readLine())!=null){
            crossArr.add(getStrArr(str));
        }
        while((str=roadBf.readLine())!=null){
            roadArr.add(getStrArr(str));
        }
        while((str=carBf.readLine())!=null){
            carArr.add(getStrArr(str));
        }
        while((str=presetAnswerBf.readLine())!=null){
            presetAnswerArr.add(getStrArr(str));
        }
        while((str=allAnswerBf.readLine())!=null){
            ansArr.add(getStrArr(str));
        }
        carArr=carArr.subList(1,carArr.size());
        presetAnswerArr=presetAnswerArr.subList(1,presetAnswerArr.size());
        //presetAnswerArr=presetAnswerArr.subList(1,presetAnswerArr.size());
        //将carArr中预置车辆隔离开来
//        List<String[]> tempPresetArr=new ArrayList<String[]>();
//        Iterator<String[]> it=carArr.listIterator();
//        while(it.hasNext()){
//            String[] temp=it.next();
//            if(temp[6].equals("1")){
//                tempPresetArr.add(temp);
//                it.remove();
//            }
//        }
//        carArr.addAll(tempPresetArr);
        Graph graph=new Graph();
        graph.generateMap(roadArr,crossArr);
        graph.loadAnswer(presetAnswerArr,carArr,ansArr);
        int res=graph.startDispatch();
        System.out.println("系统调度时间:"+res);
        System.out.println("所有车辆调度总时间:"+Graph.allDispatchTime);
        //outPut("data/answer.txt",res);
    }
    public static void outPut(String src,List<List<Integer>> res) throws IOException {
        BufferedWriter bw=new BufferedWriter(new FileWriter(src));
        for(int j=0;j<res.size();j++){
            String tempStr="(";
            for(int i:res.get(j)){
                tempStr+=i;
                tempStr+=',';
            }
            tempStr=tempStr.substring(0,tempStr.length()-1);
            tempStr+=')';
            bw.write(tempStr);
            if(j<res.size()-1){
                bw.newLine();
            }
            bw.flush();
        }
    }
    public static String[] getStrArr(String ques){
        String temp=ques;
        temp=temp.substring(1,temp.length()-1).replace(" ","");
        String[] tempArr=temp.split(",");
        return tempArr;
    }
    public static List<String[]> copyArr(List<String[]> ques){
        List<String[]> res=new ArrayList<String[]>();
        for(int i=0;i<ques.size();i++){
            String[] tempCopy=ques.get(i);
            String[] targetCopy=new String[tempCopy.length];
            for(int j=0;j<tempCopy.length;j++){
                targetCopy[j]=tempCopy[j];
            }
            res.add(targetCopy);
        }
        //res.addAll(ques);
        return res;
    }
}

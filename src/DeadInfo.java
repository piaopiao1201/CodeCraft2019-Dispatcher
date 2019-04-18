import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class DeadInfo {
    Set<Integer> deadLock=new LinkedHashSet<Integer>();
    Set<DeadCarInfo> deadCar=new TreeSet<DeadCarInfo>(new Comparator<DeadCarInfo>() {
        @Override
        public int compare(DeadCarInfo o1, DeadCarInfo o2) {
            if(o1.speed==o2.speed){
                return 0;
            }
            return o1.speed<o2.speed?-1:1;
        }
    });

    public DeadInfo(Set<Integer> deadLock, Set<DeadCarInfo> deadCar) {
        this.deadLock .addAll(deadLock);
        this.deadCar.addAll(deadCar);
    }
}

import java.util.* ;
import org.fmz.container.Vector ;
import org.fmz.container.FixedVector ;

public class InsertionSort{
    
    static void insertionSort(Vector vec){
        int current ;
        int pos ;
        int n = vec.size() ;
        for(current=1; current<n; current++){
            pos = current ;
            while(pos>0 && ((Comparable)vec.elementAt(current)).compareTo(vec.elementAt(pos-1)) < 0)
                pos -- ;
            if(pos != current){
                vec.insertAt(pos, vec.elementAt(current)) ;
                vec.removeAt(current+1) ;
             }
        }
    }
    public static void main(String args[]){
        Random rnd = new Random() ;
        FixedVector fvec = new FixedVector() ;
        fvec.append(rnd.nextInt(10)) ;
        fvec.append(rnd.nextInt(10)) ;
        fvec.append(rnd.nextInt(10)) ;
        fvec.append(rnd.nextInt(10)) ;
        fvec.append(rnd.nextInt(10)) ;
        fvec.append(rnd.nextInt(10)) ;
        fvec.append(rnd.nextInt(10)) ;
        fvec.append(rnd.nextInt(10)) ;
        fvec.append(rnd.nextInt(10)) ;
        fvec.append(rnd.nextInt(10)) ;
        

        insertionSort(fvec) ;
        for(int i=0; i<fvec.size(); i++)
            System.out.print(fvec.elementAt(i) + "\t") ;
    }
}


---
layout: post
title: 可视化主流排序算法
categories: 技术 算法
tags: 冒泡排序 选择排序 插入排序 归并排序 堆排序 Shell排序 递归 可视化
---

### 冒泡排序(Bubble Sort)

**算法描述**

- 假设要从小到大进行排序
- 对于一个元素可比较的序列，从始元素开始，依次比较与之相邻的元素，如果大于后者，则二者位置互换
- 第一次遍历整个序列，最大的元素一定会移动到序列尾
- 第二次遍历整个序列(不包括序列尾元素)，倒数第二大的元素会移动到序列倒数第二的位置
- ...
- 遍历n-1次之后，整个序列排序完毕

**算法动画演示**

![Sortinig bubble sort animation](../image/Sorting_bubblesort_anim.gif)

**算法实现(Java)**

[BubbleSort.java](../src/BubbleSort.java)

    import java.util.Random ;

    public class BubbleSort{

        /*
         * This algorithm is not optimal, because we compare the element which has already ordered
         * when the sequence is ordered, it terminate. We know this from the boolean sorted
         */
        static void bubbleSort(int[] array){
            boolean sorted = false ;
            while(sorted = !sorted){
                for(int i=0; i<array.length-1; i++){
                    if(array[i] > array[i+1]){
                        swap(array, i, i+1) ;
                        sorted = false ;
                    }
                }
            }
        }

        /*
         * This algorithm for bubble sort is optimal, because we will not compare the element which has already ordered
         * The abrove method also can be revised by assign a new variable hi=array.length then hi --
         */
        static void bubbleSortRevised(int[] array, int lo, int hi){
            boolean sored = false ;
            while(sorted = !sorted){
                for(int i=lo; i<hi-1; i++){
                    if(array[i] > array[i+1]){
                        swap(array, i, i+1) ;
                        sorted = false ;
                    }
                }
                hi -- ;
            }
        }

        /*
         * This is the revised version for method bubbleSort()
         */
        static void bubbleSort2(int[] array){
            boolean sorted = false ;
            int hi = array.length ;
            while(sorted = !sorted){
                for(int i=0; i<hi-1; i++){
                    if(array[i] > array[i+1]){
                        swap(array, i, i+1) ;
                        sorted = false ;
                    }
                }
                hi -- ;
            }
        }

        static void swap(int[] array, int first, int last){
            int temp = array[first] ;
            array[first] = array[last] ;
            array[last] = temp ;
        }

        public static void main(String args[]){
            Random rnd = new Random(26) ;
            int[] array = new int[10] ;
            for(int i=0; i<array.length; i++){
                array[i] = rnd.nextInt(10) ;
            }
            bubbleSort(array) ;
            //bubbleSortRevised(array, 0, array.length) ;
            for(int i : array)
                System.out.print(i + "\t") ;
        }

    }

**复杂度分析**

The best average worse case for bubble sort is in O(n^2)

---

#### 选择排序(Selection Sort)

**算法描述**

- 共要进行n-1次selection遍历
- 每次遍历选择最小的元素，和起始遍历位置的元素进行互换
- 每一次遍历后最小的元素就位于进行起始遍历的位置

**算法动画演示**

![Selection sort](../image/Selection_sort.gif)

**算法实现**

[SelectionSort.java](../src/SelectionSort.java)

    import java.util.Random ;
    import org.fmz.container.Vector ;
    import org.fmz.container.FixedVector ;

    public class SelectionSort{

        static void selectionSort(Vector vec){
            int current ; // point to which selection processing(we need process n-1 times)
            int pos ; // in each selection iteation, point to which element we will compare
            int small_pos ; // in each selection iteration, store the smallest element position
            Comparable smallest ; // in each selection iteration, store the smallest element 
            int n = vec.size() ;

            for(current=0; current<n-1; current++){
                small_pos = current ;
                smallest = (Comparable)vec.elementAt(small_pos) ;
                for(pos=current+1; pos<n; pos++){
                    if(((Comparable)vec.elementAt(pos)).compareTo(smallest) < 0){
                        small_pos = pos ;
                        smallest = (Comparable)vec.elementAt(pos) ;
                    }
                }
                if(small_pos != current)
                    swap(vec, current, small_pos) ;
            }    
        }

        static void swap(Vector vec, int first, int last){
            Object temp = vec.elementAt(first) ;
            vec.replace(first, vec.elementAt(last)) ;
            vec.replace(last, temp) ;
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

            selectionSort(fvec) ;
            for(int i=0; i<fvec.size(); i++)
                System.out.print(fvec.elementAt(i) + "\t") ;
        }
    }

**复杂度分析**

The best average worst case for SelectionSort is in O(n^2)

---

### 插入排序(Insertion Sort)

### 堆排序(Heap sort)

**算法描述**

- 插入排序就像是玩扑克牌一样，我们将新得到的牌与手中的牌(手中的牌已经排好序)对比
- 找到与得到的牌相比小的牌，即插入这张小牌的后面
- 后面的牌次序依次后移
- 经过n-1此之后手中的牌就排好序了

**算法动画演示**

![Insertiong Sort](../image/Insertion_sort.gif)

**算法实现**

[InsertionSort.java](../src/InsertionSort.java)

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

**复杂度分析**

average case for Insertion Sort in O(n^2)

---

### 堆排序(Heap Sort)

**算法描述**

- 需要构建一个[Heap数据结构](https://fmzhao.github.io/follow-me-step-by-step-to-learn-DSA/)
- 将序列添加到MaxHeap中
- 依次删除MaxHeap的root元素，即为剩余元素中的最大元素

**算法动画演示**

![Max Heap Sort](../image/Max_heap_sort.gif)

**算法实现**

[Max Heap Sort](../src/HeapSort.java)

    import java.util.Random ;
    import org.fmz.container.Vector ;
    import org.fmz.container.FixedVector ;
    import org.fmz.container.MaxHeap ;

    public class HeapSort{

        static void heapSort(Vector vec){
            MaxHeap temp = new MaxHeap(vec.size()) ;
            for(int i=0; i<vec.size(); i++)
                temp.insert((Comparable)vec.elementAt(i)) ;
            for(int i=vec.size()-1; i>=0; i--)
                vec.replace(i, temp.removeMax()) ;
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
            

            heapSort(fvec) ;
            for(int i=0; i<fvec.size(); i++)
                System.out.print(fvec.elementAt(i) + "\t") ;
        }
    }

**复杂度分析**

average case for Heap Sort in O(n*log(n))

---

### 归并排序(Merge Sort)

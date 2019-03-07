package com.mood;

import java.util.Stack;

public class SortTest {
    public static void main(String[] args){
        int[] array={122,123,21,41,4,21,42,1,41};
        int[] result=quickSort(array,0,array.length-1);
        int[] result1=mergeSort(array);
        for (int x :
                result) {
            System.out.print(x+",");
        }
        System.out.println();
        System.out.println("==================");
        for (int x :
                result1) {
            System.out.print(x+",");
        }
        System.out.println();
        System.out.println("==================");
        System.out.println(midFind(result1,111,0,result1.length));
    }
    /**
     * 快速排序
     * @param array
     * @param start
     * @param end
     * @return
     */
    public static int[] quickSort(int[] array,int start,int end){
        if (array.length==0||start<0||end==array.length)
            return null;
        if (array.length==1) {
            return array;
        }
        int partation=partation(array,start,end);
        if (partation>start)
            quickSort(array,start,partation-1);
        if (partation<end)
            quickSort(array,partation+1,end);
        return array;
    }

    /**
     * 获取基准值下标
     * @param array
     * @param start
     * @param end
     * @return
     */
    private static int partation(int[] array, int start, int end) {
        int parttation= (int) (start+(Math.random()*(end-start+1)));
        int result=start-1;
        swap(array,parttation,end);
        for (int i=start;i<=end;i++){
            if (array[i]<=array[end]){
                result++;
                if (i>result)
                    swap(array,i,result);
            }
        }
        return result;
    }
    /**
     * 交换方法
     * @param array
     * @param parttation
     * @param end
     * @return
     */
    private static void swap(int[] array, int parttation, int end) {
        int temp=array[parttation];
        array[parttation]=array[end];
        array[end]=temp;
    }

    /**
     * 归并排序
     * @param array
     * @return
     */
    public static int[] mergeSort(int[] array){
        if (array.length<2) return array;
        int mid=array.length/2;
        int[] left=sort(array,0,mid);
        int[] right=sort(array,mid,array.length);
        return merge(mergeSort(left),mergeSort(right));
    }

    /**
     * 合并结果
     * @param left
     * @param right
     * @return
     */
    private static int[] merge(int[] left, int[] right) {
        int[] result=new int[left.length+right.length];
        for (int index = 0, i = 0, j = 0;index<result.length;index++){
            if (i>=left.length){
                result[index]=right[j++];
            }else if (j>=right.length){
                result[index]=left[i++];
            }else if (left[i] > right[j]){
                result[index]=right[j++];
            }else {
                result[index]=left[i++];
            }
        }
        return result;
    }
    /**
     * 排序
     * @param array
     * @param mid
     * @param length
     * @return
     */
    private static int[] sort(int[] array, int mid, int length) {
        int[] result=new int[length-mid];
        int startIndex=mid;
        for (;mid<length-1;mid++){
            if (array[mid+1]<array[mid]){
                int temp=array[mid+1];
                array[mid+1]=array[mid];
                array[mid]=temp;
            }
        }
        for (int i=0;i<result.length;i++){
            result[i]=array[startIndex+i];
        }
        return result;
    }
    /**
     * 斐波那契
     * @param n
     * @return
     */
    int fac(int n) {
        if (n == 1)
            return 1;
        if (n == 2)
            return 1;
        return fac(n - 2) + fac(n - 1);
    }
    /**
     * 斐波那契非递归方式
     * @param n
     * @return
     */
    int facToLoop(int n) {
        Stack<Integer> stack=new Stack<Integer>();
        for (int i=1;i<=n;i++){
            if (i== 1||i== 2){
                stack.push(1);
            }else {
                int temp1=stack.pop();
                int temp2=stack.pop();
                stack.push(temp1);
                stack.push(temp1+temp2);
            }
        }
        int result=stack.pop();
        return result ;
    }
    /**
     * 二分查找
     * @param array
     * @param n
     * @param start
     * @param end
     * @return
     */
    public static int midFind(int[] array,int n,int start,int end){
        int mid=(start+end)/2;
        if (n<array[start]||n>array[end-1]) return -1;
        if (array[mid]==n){
            return mid;
        }else if (array[mid]>n){
            return  midFind(array,n,start,mid);
        }else{
            return  midFind(array,n,mid,end);
        }
    }

}

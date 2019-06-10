package com.zyy.test;

import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * @Author:zhouyuyang
 * @Description:
 * @Date: Created in 10:00 2019/5/24
 */
public class Test2 {
    public static void main(String[] args) {
        int pageNumber=3;
        pageNumber = (pageNumber - 1 ) / 10 + 1;
        System.out.println(pageNumber);

        int[] arr = { 12, 45, 9, 67, 1 };
        for (int i = 0; i < arr.length - 1; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp;
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
        System.out.println("排序后：");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + "\t");
        }

        //创建一个数组用于排序
        int[]arr2={1,4,3,2,6,12,10};
        //调用递归的冒泡
        arr2=recursiveBubble(arr2,arr2.length);
        System.out.println("\n排序后：");
        for(int i=0;i<arr2.length;i++){
            System.out.print(arr2[i] + "\t");
        }


        Assert.notNull(null, "objects cannot be empty");
    }

    /**
     * 冒泡排序之递归方法
     */
    public static int[] recursiveBubble(int[] arr2,int e){
        if(e==0){
            return arr2;
        }else{
            for(int i=0;i<e-1;i++){
                int temp;
                if(arr2[i]>arr2[i+1]){
                    temp=arr2[i];
                    arr2[i]=arr2[i+1];
                    arr2[i+1]=temp;
                }
            }
            e--;
            recursiveBubble(arr2,e);
        }
        return arr2;
    }

}

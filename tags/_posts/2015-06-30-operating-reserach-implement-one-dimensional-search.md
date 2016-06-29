---
layout: post
title: 运筹学之单峰函数一维搜索算法
categories: 技术 Java 算法
tags: 运筹学 一维搜索 斐波那契 黄金分割
---
### 斐波那契（Fibonacci）一维搜索算法

	import java.text.*;
	public class FibonacciOneDimensionalSearch {
		public static void main(String args[]){
		double a0 = -1 ;
		double b0 = 3 ;
		double δ = 0.08 ;
		double a1 = 0 ;
		double b1 = 0 ;
		double f = 0 ;
		
		double[] fiInt = Fibonacci(15) ;
		/*
		for(int i=0;i<fiInt.length;i++){
				System.out.println(fiInt[i]) ;
			}
		}
		*/
		f = (int)(1/δ) ;
		double[] temp = new double[fiInt.length] ;
		for(int i=0;i<fiInt.length;i++){
			temp[i] = Math.abs(f-fiInt[i]) ;
			//System.out.println(temp[i]) ;
		}
		
		double minIndex = 0 ;
		double min = temp[0] ;
		for(int index=0;index<fiInt.length;index++){
			if(temp[index]<min){
				min = temp[index] ;
				minIndex = index ;
			}
		}
		//System.out.println(min+"\t"+minIndex) ;
		
		//f = fiInt[(int)minIndex] ;
		//System.out.println(f) ;
		System.out.println("迭代次数："+((int)(minIndex)-1)) ;
		
		DecimalFormat df = new DecimalFormat(".###") ;
		
		for(int i=(int)minIndex;i>=2;i--){
			a1 = a0 +(fiInt[i-2]/fiInt[i])*(b0-a0) ;
			b1 = a0 + (fiInt[i-1]/fiInt[i])*(b0-a0) ;
			/*
			t = a1 ;
			double y1 = y ;
			t = b1 ;
			double y2 = y ;
			*/
			if(fun(a1) > fun(b1)){
				a0 = a1 ;
			}
			else{
				b0 = b1 ;
			}
			
			
			//System.out.println("a1= "+a1+"\t"+"b1= "+b1) ;
			System.out.println("a0= "+df.format(a0)+"\tb0= "+df.format(b0)) ;
			System.out.println("a1= "+df.format(a1)+"\tb1= "+df.format(b1)) ;
			System.out.println("ya1= "+df.format(fun(a1))+"\tyb1= "+df.format(fun(b1))) ;
			//System.out.println("a0= "+a0+"\tb0= "+b0) ;
			//System.out.println("y1= "+fun(a1)+"\t"+"y2= "+fun(b1)) ;
			/*
			System.out.println(a1) ;
			System.out.println(fiInt[i-2]) ;
			System.out.println(fiInt[i-1]) ;
			System.out.println(fiInt[i]) ;
			System.out.println(a1) ;
			*/
			System.out.println() ;
		}
		//System.out.println(fun(0.5)) ;
	}
		
		public static double[] Fibonacci(int n){
			double[] fiInt = new double[n+1] ;
			fiInt[0] = fiInt[1] = 1 ;
			for(int i=2;i<fiInt.length;i++){
				fiInt[i] = fiInt[i-1] +fiInt[i-2] ;
			}
			return fiInt ;
		}
		public static double fun(double t){
			return t*t -t + 2 ;
		}
	}

### 黄金分割一维搜索算法

	import java.text.*;
	public class GoldenOneDimensionalSearch {
		public static void main(String args[]){
			double a0 = -1 ;
			double b0 = 3 ;
			double δ = 0.08 ;
			double a1 = 0 ;
			double b1 = 0 ;
			
			DecimalFormat df = new DecimalFormat(".###") ;
			
			do{
				a1 = a0 + (1-0.618)*(b0-a0) ;
				b1 = a0 + 0.618*(b0-a0) ;
				if(fun(a1) > fun(b1)){
					a0 = a1 ;
				}
				else{
					b0 = b1 ;
				}
				System.out.println("a0= "+df.format(a0)+"\tb0= "+df.format(b0)) ;
				//System.out.println("a1= "+df.format(a1)+"\tb1= "+df.format(b1)) ;
				System.out.println("ya1= "+df.format(fun(a1))+"\tyb1= "+df.format(fun(b1))) ;
				System.out.println() ;
			}while(((b0-a0)/4) > δ) ;
			
		}
		public static double fun(double t){
			return t*t -t +2 ;
		}
	}

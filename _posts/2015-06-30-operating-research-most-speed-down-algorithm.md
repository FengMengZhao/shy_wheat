---
layout: post
title: 运筹学之最速下降算法
categories: 技术 Java 算法
tags: 运筹学 最速下降
---

	class MultiArray{
		public double[][] multiArray(double[][] a,double[][] b,double[][] c){
			double[][] temp1 = new double[a.length][b[0].length] ;
			double[][] temp2 = new double[a.length][c[0].length] ;
			if(a[0].length != b.length){
				System.out.println("输入矩阵不能相乘！") ;
				System.exit(1) ;
			}
			else{
				for(int i=0;i<a.length;i++){
					for(int j=0;j<a[0].length;j++){
						for(int k=0;k<b[0].length;k++){
							temp1[i][k] += a[i][j]*b[j][k] ;
						}
					}
				}
			}
			
			if(temp1[0].length != c.length){
				System.out.println("输入矩阵不能相乘！") ;
				System.exit(1) ;
			}
			else{
				for(int i=0;i<temp1.length;i++){
					for(int j=0;j<temp1[0].length;j++){
						for(int k=0;k<c[0].length;k++){
							temp2[i][k] += temp1[i][j]*c[j][k] ;
						}
					}
				}
			}
			return temp2 ;
		}
		
		public double[][] multiArray(double[][] a,double[][] b){
			double[][] temp = new double[a.length][b[0].length] ;
			if(a[0].length != b.length){
				System.out.println("输入矩阵不能相乘！") ;
				System.exit(1) ;
			}
			else{
				for(int i=0;i<a.length;i++){
					for(int j=0;j<a[0].length;j++){
						for(int k=0;k<b[0].length;k++){
							temp[i][k] += a[i][j]*b[j][k] ;
						}
					}
				}
			}
			return temp ;
		}
	}

	public class GradientMethod {
		public static void main(String args[]){
		
			//double[][] H = {2,0;0,50} ;
			double[][] H = {3,-1;-1,1} ;
			double x1 = 2 ;
			double x2 = 2 ;
			double λ = 0 ;
			double[][] derivative = {2;0} ;//注意数组的静态初始化与动态初始化的区别，另外循环一定要有三条件：初始条件；终止条件；循环条件！！！
			MultiArray ma = new MultiArray() ;
			double[][] IDerivative = {2;0} ;
			/*
			do{
				//derivative =new double[][] {2*x1;50*x2} ;//列向量梯度,注意数组动态初始化和静态初始化的区别！！！！！
				derivative =new double[][] {3*x1-x2-2;x2-x1} ;
				//IDerivative =new double[][]{2*x1;50*x2} ;//转置行向量梯度
				IDerivative =new double[][]{3*x1-x2-2;x2-x1} ;
				double[][] temp1 = ma.multiArray(IDerivative,derivative) ;
				double[][] temp2 = ma.multiArray(IDerivative,H,derivative) ;
				λ = temp1[0][0]/temp2[0][0] ;
				
				x1 = x1 - λ*derivative[0][0] ;
				x2 = x2 - λ*derivative[1][0] ;
				
				
				System.out.println(λ) ;
				System.out.println("x1= "+x1+"\tx2="+x2) ;
				System.out.println(1.5*x1*x1+0.5*x2*x2-x1*x2-2*x1+"\n") ;
			}while((derivative[0][0]*derivative[0][0]+derivative[1][0]*derivative[1][0])>0.0001) ;//do while 循环和while循环的另外区别是do while 可以用循环内的变量来限制循环的终止。
			*/
			
			while((derivative[0][0]*derivative[0][0]+derivative[1][0]*derivative[1][0])>0.0001){
				derivative =new double[][] {3*x1-x2-2;x2-x1} ;
				IDerivative =new double[][]{3*x1-x2-2;x2-x1} ;
				double[][] temp1 = ma.multiArray(IDerivative,derivative) ;
				double[][] temp2 = ma.multiArray(IDerivative,H,derivative) ;
				λ = temp1[0][0]/temp2[0][0] ;
				
				x1 = x1 - λ*derivative[0][0] ;
				x2 = x2 - λ*derivative[1][0] ;
				
				
				System.out.println(λ) ;
				System.out.println("x1= "+x1+"\tx2="+x2) ;
				System.out.println(1.5*x1*x1+0.5*x2*x2-x1*x2-2*x1+"\n") ;
			}
		}
	}	

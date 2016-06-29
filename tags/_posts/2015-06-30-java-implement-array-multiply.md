---
layout: post
title: java实现矩阵的乘法
categories: 技术 Java
tags: 矩阵 乘法
---

	public class MultiArray {
		public static void main(String args[]){
			double[][] a = {} ;
			double[][] b = {} ;
			double[][] c = {} ;
			double[][] temp = multiArray(a,b,c) ;
			for(int i=0;i<temp.length;i++){
				for(int j=0;j<temp[0].length;j++){
					System.out.print(temp[i][j]+"\t") ;
				}
				System.out.println() ; 
			}
		}
		public static double[][] multiArray(double[][] a,double[][] b,double[][] c){
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
	}

---
layout: post
title: 数据结构之java基于数组对Vector的实现
categories: 技术 Java
tags: 数据结构 Vector
---

	class MyVector{
		int array[] ;
		int v_size = 10;
		public MyVector(int i){
			array = new int[i] ;
		}
		public MyVector(){
			this(15) ;
		}
		public void insertAtRank(int index,int e){
			for(int i=v_size-1;i>=index;i--){
				array[i+1] = array[i] ;
			}
			array[index] = e ;
			this.v_size++ ;
		}
		public void removeAtRank(int index){
			array[index] = 0 ;
			for(int i=index+1;i<this.v_size;i++){
				array[i] = array[i+1] ;
			}
			this.v_size-- ;
		}
		public void printAll(){
			for(int i=0;i<array.length;i++){
				System.out.print(array[i]+"\t") ;
			}
			System.out.println() ;
		}
	}
	public class MyVectorBasedSimpleArray {
		public static void main(String args[]){
			MyVector vector = new MyVector() ;
			for(int i=0;i<10;i++){
				vector.array[i] = i ;
			}
			vector.printAll() ;
			System.out.println(vector.v_size) ;
			vector.insertAtRank(9, 4) ;
			vector.printAll() ;
			System.out.println(vector.v_size) ;
			vector.removeAtRank(9) ;
			vector.printAll() ;
			System.out.println(vector.v_size) ;
			
		}
	}

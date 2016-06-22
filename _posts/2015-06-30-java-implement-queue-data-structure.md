---
layout: post
title: 数据结构之java队列实现
categories: 技术 Java
tags: 数据结构 Queue
---

	class MyQueue{
		int[] array ;
		int front ;
		int rear ;
		int q_size ;
		public MyQueue(int i){
			array = new int[i] ;
			this.front = 0 ;
			this.rear = 0 ;
			this.q_size = 0 ;
		}
		public MyQueue(){
			this(9) ;
		}
		public void enqueue(int i){
			if(this.q_size == (array.length)){
				System.err.println("队列已满！") ;
				}else{
				array[rear] = i ;
				rear = (rear+1)%array.length ;//用于数组的循环利用！！！
				this.q_size++ ;
			}
		}
		public int dequeue(){
			if(this.q_size>0){
				int temp = array[front] ;
				array[front] = 0 ;
				front = (front+1)%array.length ;//用于数组的循环利用！！！
				this.q_size-- ;
				return temp ;
			}else{
				System.err.print("这是一个空队列！") ;
				return 0 ;
			}
			
		}
		public void printAll(){
			for(int i=0;i<array.length;i++){
				System.out.print(array[i]+"\t") ;
			}
			System.out.println() ;
		}
	}
	public class MyQueueBasedSimpleArray {
		public static void main(String args[]){
			MyQueue queue = new MyQueue() ;
			queue.enqueue(3) ;
			queue.enqueue(4) ;
			queue.enqueue(5) ;
			queue.enqueue(6) ;
			queue.enqueue(6) ;
			queue.enqueue(7) ;
			queue.enqueue(4) ;
			queue.enqueue(8) ;
			queue.enqueue(9) ;
			queue.printAll() ;
			System.out.println(queue.q_size) ;
			System.out.println("-------------------------------------") ;
			queue.dequeue() ;
			queue.printAll() ;
			System.out.println(queue.q_size) ;
			System.out.println("-------------------------------------") ;
			queue.dequeue() ;
			queue.printAll() ;
			System.out.println(queue.q_size) ;
			System.out.println("-------------------------------------") ;
			queue.dequeue() ;
			queue.printAll() ;
			System.out.println(queue.q_size) ;
			System.out.println("-------------------------------------") ;
			queue.dequeue() ;
			queue.printAll() ;
			System.out.println(queue.q_size) ;
			System.out.println("-------------------------------------") ;
			queue.dequeue() ;
			queue.printAll() ;
			System.out.println(queue.q_size) ;
			System.out.println("-------------------------------------") ;
			queue.dequeue() ;
			queue.printAll() ;
			System.out.println(queue.q_size) ;
			System.out.println("-------------------------------------") ;
			queue.dequeue() ;
			queue.printAll() ;
			queue.enqueue(3) ;
			queue.enqueue(4) ;
			queue.enqueue(5) ;
			queue.enqueue(6) ;
			queue.printAll() ;
		}
	}

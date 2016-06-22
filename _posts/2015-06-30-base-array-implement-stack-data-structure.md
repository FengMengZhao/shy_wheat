---
layout: post
title: 数据结构之java基于数组对Stack的实现
categories: 技术 Java
tags: 数据结构 Stack
---

### 基于数组实现栈的数据结构 

MyStack.java

	class MyStack{
		int[] array ;
		int s_size ;
		public MyStack(int i){
			this.array = new int[i] ;
			this.s_size = 0 ;
		}
		public MyStack(){
			this(50) ;
		}
		public void push(int i){
			array[this.s_size] = i ;
			this.s_size++ ;
		}
		public int pop(){
			if(this.s_size!=0){
				int temp = array[s_size - 1] ;
				array[s_size - 1] = 0 ;
				s_size-- ;
				return temp ;
			}else{
				System.out.println("这是一个空栈") ;
				return 0 ;
			}
		}
		public boolean isEmpty(){
			return this.s_size == 0 ;
		}
		public int top(){
			if(!this.isEmpty()){
				int temp = array[this.s_size-1] ;
				return temp ;
			}else{
				System.out.println("这是一个空栈") ;
				return 0 ;
			}
		}
		public void printAll(){//从栈顶往栈底输出。
			if(!this.isEmpty()){
				for(int i=this.s_size-1;i>=0;i--){
					System.out.print(array[i]+"\t") ;
				}
				System.out.println() ;
			}
		}
	}
	public class MySatackBasedSimpleArray {//基于简单数组的栈数据结构实现,遵循后进先出的原则（LIFO） 。
		public static void main(String args[]){
			MyStack stack = new MyStack() ;
			stack.push(5) ;
			stack.push(6) ;
			stack.push(7) ;
			stack.push(7) ;
			stack.push(9) ;
			stack.push(2) ;
			stack.printAll() ;
			int t = stack.pop() ;
			System.out.println(t) ;
			stack.printAll() ;
			stack.pop() ;
			stack.printAll() ;
			stack.pop() ;
			stack.printAll() ;
			System.out.println(stack.top()) ;
			stack.printAll() ;
		}
	}

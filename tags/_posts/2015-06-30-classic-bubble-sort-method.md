---
layout: post
title: 经典的冒泡排序算法
categories: 技术 Java
tags: 冒泡排序
---

	public class LianXi6 {//经典冒泡排序法！！！
		public static void main(String args[]){
			String[] strs = new String[]{"M","A","D","a","f","b","m","C"} ;
			for(int i=0;i<strs.length;i++){
				for(int j=i+1;j<strs.length;j++){
					if(strs[j].compareTo(strs[i])<0){//字符串继承了Comparable接口
						String str = strs[j] ;
						strs[j] = strs[i] ;
						strs[i] = str ;
					}
				}
				System.out.println(strs[i]) ;
			}
		}
	}
	

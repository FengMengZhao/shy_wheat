---
layout: post
title: 递归读取某文件下面的所有文件名
categories: 技术 Java
tags: 递归
---

	import java.io.*;

	public class ReadAllDirectory {
		public static void main(String args[]){
			File f = new File("c:\\") ;
			print(f) ;
		}
		public static void print(File file){
			if(file != null){
				if(file.isDirectory()){
					File[] f = file.listFiles() ;
					if(f != null){
						for(int i=0;i<f.length;i++){
							print(f[i]) ;
						}
					}
				}
				else{
					System.out.println(file) ;
				}
			}
		}
	}

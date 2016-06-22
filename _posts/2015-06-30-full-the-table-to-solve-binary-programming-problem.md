---
layout: post
title: 填表的动态规划算法求解0-1规划问题
categories: 技术 Java
tags: 动态规划 0-1规划
---

	public class BinaryKnapsack {
		public static void knapsack(int weights[],int values[],int W){
		    int N = weights.length ;
			int V[][] = new int[N+1][W+1] ;
			int [][] select = new int[N+1][W+1] ; 
			for(int i=0;i<=N;i++){
				V[i][0] = 0 ;
			}
			for(int j=0;j<=W;j++){
				V[0][j] = 0 ;
			}
			for(int item=1;item<=N;item++){
				for(int weight=1;weight<=W;weight++){
					if(weight>=weights[item-1]){
						V[item][weight] = Math.max(values[item-1]+V[item-1][weight-weights[item-1]],V[item-1][weight]) ;
						select[item][weight] =(values[item-1]+V[item-1][weight-weights[item-1]] > V[item-1][weight]) ? 1 : 0 ;
					}
					else{
						V[item][weight] = V[item-1][weight] ;
					}
				}
			}
			for(int i=0;i<=N;i++){
				for(int j=0;j<=W;j++){
					System.out.print(V[i][j]+"\t") ;
				}
				System.out.println() ;
			}
			System.out.println("\nSolution value:\n"+V[N][W]) ;
			 int[] selected = new int[N + 1];
			for (int n = N, w = W; n > 0; n--)
			{
			    if (select[n][w] != 0)
			    {
				selected[n] = 1;
				w = w -weights[n-1] ;
			    }
			    else
				selected[n] = 0;
			}
			System.out.println("\nItems selected : ");
			for (int i = 1; i < N + 1; i++)
			    if (selected[i] == 1)
				System.out.print(i +" ");
			System.out.println();
		}
		public static void main(String args[]){
			int[] weights = {1,56,42,78,12} ;
			int[] values = {50,30,20,10,50} ;  
			int W = 150 ;
			knapsack(weights,values,W) ;
		}
	}

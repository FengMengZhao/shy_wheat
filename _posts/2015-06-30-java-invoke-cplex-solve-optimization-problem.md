---
layout: post
title: java调用Cplex求解优化问题算法
categories: 技术 Java
tags: cplex 
---

### 一般性线性规划问题求解

	import ilog.concert.*;
	import ilog.cplex.*;
	//现行规划问题的求解
	//数值默认状态均为0
	public class LinPro 
	{
		static double[] lb = {0.0,0.0,0.0} ;//输入决策变量的下界*
		static double[] ub = {Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE} ;//输入决策变量的上界*
		static double[] objvals = {} ;//输入决策变量的价值系数*
		
		public static void main(String[] args) 
		{
			solve_lp();
		}
		
		public static void solve_lp(){
			try 
			{
				
				IloCplex cplex = new IloCplex();//对象实例化，开辟堆内存空间
				IloNumVar[] x = cplex.numVarArray(0, lb, ub);//修改决策变量的数量*
				cplex.addMaximize(cplex.scalProd(x, objvals));//最大化目标函数                                                    cplex.scalProd(变量数组，数值型数组），表示向量与向量乘积的形式。
				cplex.addMinimize(cplex.scalProd(x, objvals));//最小化目标函数
				cplex.addLe(cplex.sum(cplex.prod(0, x[0]), 
									  cplex.prod(0, x[1]), 
									  cplex.prod(0, x[2])), 0);//输入约束条件"<="*
				cplex.addGe(cplex.sum(cplex.prod(0, x[0]), 
									  cplex.prod(0, x[1]), 
									  cplex.prod(0, x[2])), 0);//输入约束条件">="*
				cplex.addEq(cplex.sum(cplex.prod(0, x[0]), 
									  cplex.prod(0, x[1]), 
									  cplex.prod(0, x[2])), 0);//输入约束条件"="*
				if (cplex.solve()) 
				{
					cplex.output().println("Solution status = " + cplex.getStatus());
					cplex.output().println("Solution value = " + cplex.getObjValue());
					double[] val = cplex.getValues(x);
					int ncols = cplex.getNcols();
					for (int j = 0; j < ncols; ++j)
						cplex.output().println("Column: " + j + " Value = " + val[j]);
				}
				cplex.end();
			} 
			catch (IloException e) 
			{

				System.err.println("Concert exception '" + e + "' caught");

			}
		}	
	}



### 运输问题求解

	import ilog.concert.*;
	import ilog.cplex.*;

	public class ExerciseTransport {
		public static void main(String args[]){
			try{
				IloCplex cplex = new IloCplex() ;
				IloNumVar[][] x = new IloNumVar[3][4] ;
				double[] supply ={7.,4.,9.} ;
				double[] demand ={3.,6.,5.,6.} ;
				int [][] percost ={3,11,3,10;1,9,2,8;7,4,10,5} ;
				for(int i=0;i<3;i++){
					x[i] = cplex.numVarArray(4,0,Double.MAX_VALUE) ;
				}
				
				for(int i=0;i<3;i++){
					IloLinearNumExpr v1 = cplex.linearNumExpr() ;
					for(int j=0;j<4;j++){
						v1.addTerm(1,x[i][j]) ;					
					}
					cplex.addEq(v1, supply[i]) ;
				}
				
				
				for(int j=0;j<4;j++){
					IloLinearNumExpr v2 = cplex.linearNumExpr() ;
					for(int i=0;i<3;i++){
						v2.addTerm(1,x[i][j]) ;					
					}
					cplex.addEq(v2, demand[j]) ;
				}
				
				IloLinearNumExpr expr = cplex.linearNumExpr() ;
				for(int i=0;i<3;i++){
					for(int j=0;j<4;j++){
						expr.addTerm(percost[i][j],x[i][j]) ;
					}
				}
				cplex.addMinimize(expr) ;
				
				if(cplex.solve()){
					for(int i=0;i<3;++i){
						System.out.print(i+":\t") ;
						for(int j=0;j<4;++j){
							System.out.print(cplex.getValue(x[i][j])+"\t") ;
						}
						System.out.println() ;
					}
					System.out.println("Cost="+cplex.getObjValue()) ;
				}
				cplex.end() ;
			}
			catch(IloException e){
				System.err.println("Concert exception caught:"+e);
			}
		}
	}

### 运输问题求解（产销不平衡）

	import ilog.concert.*;
	import ilog.cplex.*;
	//产销不平衡的运输问题求解。(运筹学P90，例2)
	public class YunShu {
		public static void main(String args[]){
			try{
				IloCplex cplex = new IloCplex() ;
				IloNumVar[][] x = new IloNumVar[3][4] ;
				double[] supply = {50.,60.,50.} ;
				double[] demand_lb = {30.,70.,0.,10.} ;
				double[] demand_ub = {50.,70.,30.,Double.MAX_VALUE} ;
				double[][] percost = {16.,13.,22.,17.;14.,13.,19.,15.;19.,20.,23.,Double.MAX_VALUE} ;
				for(int i=0;i<3;i++){
					x[i] = cplex.numVarArray(4,0,Double.MAX_VALUE) ;
				}
				
				for(int i=0;i<3;i++){
					IloLinearNumExpr e1 = cplex.linearNumExpr() ;
					for(int j=0;j<4;j++){
						e1.addTerm(1, x[i][j]) ;
					}
					cplex.addEq(e1, supply[i]) ;
				}
				for(int j=0;j<4;j++){
					IloLinearNumExpr e2 = cplex.linearNumExpr() ;
					for(int i=0;i<3;i++){
						e2.addTerm(1,x[i][j]) ;
					}
					cplex.addLe(e2, demand_ub[j]) ;
					cplex.addGe(e2, demand_lb[j]) ;
				}
				
				IloLinearNumExpr expr = cplex.linearNumExpr() ;
				for(int i=0;i<3;i++){
					for(int j=0;j<4;j++){
						expr.addTerm(percost[i][j],x[i][j]) ;
					}
				}
				cplex.addMinimize(expr) ;
				
				if(cplex.solve()){
					System.out.println("Solution status:"+cplex.getStatus()) ;
					for(int i=0;i<3;i++){
						System.out.print(i+":\t") ;
						for(int j=0;j<4;j++){
							System.out.print(cplex.getValue(x[i][j])+"\t") ;
						}
						System.out.println() ;
					}
					System.out.println("Solution value:"+cplex.getObjValue()) ;
				}						
			}
			catch(IloException e){
				System.err.println("Concert exception caught:"+e);
			}
		}
	}

### 指派问题求解

	import ilog.concert.*;
	import ilog.cplex.*;
	public class Assignment {
		public static void main(String args[]){
			try{
				IloCplex cplex = new IloCplex() ;
				int[][] percost = {2,15,13,4;10,4,14,15;9,14,16,13;7,8,11,9} ;
				IloNumVar[][] x = new IloNumVar[4][4] ;
				
				for(int i=0;i<4;i++){
					for(int j=0;j<4;j++){
						x[i][j] = cplex.boolVar() ;
					}
				}
				for(int i=0;i<4;i++){
					IloLinearNumExpr e1 = cplex.linearNumExpr() ;
					for(int j=0;j<4;j++){
						e1.addTerm(1,x[i][j]) ;
					}
					cplex.addEq(e1, 1) ;
				}
				for(int j=0;j<4;j++){
					IloLinearNumExpr e2 = cplex.linearNumExpr() ;
					for(int i=0;i<4;i++){
						e2.addTerm(1,x[i][j]) ;
					}
					cplex.addEq(e2, 1) ;
				}
				
				IloLinearNumExpr expr = cplex.linearNumExpr() ;
				for(int i=0;i<4;i++){
					for(int j=0;j<4;j++){
						expr.addTerm(percost[i][j],x[i][j]) ;
					}
				}
				cplex.addMinimize(expr) ;
				
				if(cplex.solve()){
					System.out.println("Solution status:"+cplex.getStatus()) ;
					for(int i=0;i<4;i++){
						System.out.print(i+":\t") ;
						for(int j=0;j<4;j++){
							System.out.print(cplex.getValue(x[i][j])+"\t") ;
						}
						System.out.println() ;
					}
					System.out.println("Solution Value:"+cplex.getObjValue()) ;
					
				}
				
			}
			catch(IloException e){
				System.err.println(e) ;
			}
		}
	}

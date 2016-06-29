---
layout: post
title: 鲁棒性问题经典类方法编程实现
categories: Java 引用
tags: 鲁棒性
---

## 参考程序

### 主类(RobustDemo Class)

	package RobustPackage;

	import java.text.DecimalFormat;




	public class RobustDemo {

		public static void main(String args[]){
			
			int Customer    = 100;
			double Capacity = 500;
			double alpha    = 0.05;
			
			Object[] OptValue   = new Object[50];
			Object[] Protection = new Object[50];
			
			for(int i = 1;i<=10; i++){
				String Str      = "C:\\Users\\fmz\\workspace\\test\\src\\RobustPackage\\Binary\\Instance_"+Customer+"_"+i+".txt";

				//RobustBental r = new RobustBental(Str, Customer, Capacity, alpha);
				
				RobustCentre1 r = new RobustCentre1(Str, Customer, Capacity, alpha);
				//RobustCentre2 r = new RobustCentre2(Str, Customer, Capacity, alpha);
				//RobustCentre2_1 r = new RobustCentre2_1(Str, Customer, Capacity, alpha);
				
				//RobustUncentre1 r = new RobustUncentre1(Str, Customer, Capacity, alpha);
				//RobustUncentre2 r = new RobustUncentre2(Str, Customer, Capacity, alpha);
				
				//RobustMelvyn1   r = new RobustMelvyn1(Str, Customer, Capacity, alpha);
				//RobustMelvyn2   r = new RobustMelvyn2(Str, Customer, Capacity, alpha);
				
				DecimalFormat d = new DecimalFormat("#.00");
				OptValue[i-1]   = d.format(r.getOptValue());
				Protection[i-1] = d.format(r.getProtection());
			}
			
		    System.out.println("OptValue = ");
		    for(int i =0;i<10;i++){
			System.out.print(OptValue[i]+" ");
		    }
		    System.out.println("\nProtection = ");
		    for(int i =0;i<10;i++){
			System.out.print(Protection[i]+" ");
		    }
		
		}
		
	}

### 数据处理类(Robust Class)

	package RobustPackage;

	import java.io.BufferedReader;
	import java.io.FileReader;
	import java.io.IOException;
	import java.util.Set;
	import java.util.TreeSet;

	public abstract class Robust {

		public double[] Profit ;
		public double[] Value  ;
		public double[] Nominal;
		public double[] Interval;
		public double[] UniInterval;
		
		public Robust(String Dir, int Customer){
			this.readInstance(Dir, Customer);
			this.setUniInterval();
			
		}

		public void readInstance(String dir, int customer) {
			this.Profit  = new double[customer];
			this.Value   = new double[customer];
			this.Nominal = new double[customer];
			this.Interval= new double[customer];
		 
			try {
				BufferedReader readTxt = new BufferedReader(new FileReader(dir));
				String textLine = readTxt.readLine();       //read a line from .txt file
				String result = "";
				
				while (textLine != null) {
					result += textLine;
					textLine = readTxt.readLine();
				}
				String[] numbersArray = result.split(" ");

				for (int i = 0; i < customer; i++) {
					
						this.Profit[i]   =  Double.parseDouble(numbersArray[i]);
						this.Value[i]    =  Double.parseDouble(numbersArray[customer+i]);
						this.Nominal[i]  =  Double.parseDouble(numbersArray[2*customer+i]);
						this.Interval[i] =  Double.parseDouble(numbersArray[3*customer+i]);
			    
				}
				readTxt.close() ;
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		private void setUniInterval() {
			Set<Double> Uni = new TreeSet<Double>();
			Uni.add(0.0);
			for(int i=0; i<Interval.length; i++){
				Uni.add(this.Interval[i]);
			}
			Object[] obj     = Uni.toArray();
			this.UniInterval = new double[obj.length];
			
			 for (int j=0;j<UniInterval.length; j++){
				 this.UniInterval[j] = (Double)obj[j];
			 }						
		}
		
		
	}

### Centre Method(RobustCentre1 Class)

	package RobustPackage;

	import ilog.concert.IloException;
	import ilog.concert.IloLinearNumExpr;
	import ilog.concert.IloNumVar;
	import ilog.cplex.IloCplex;

	import java.util.Set;
	import java.util.TreeSet;

	    public class RobustCentre1 extends Robust{
		    private double OptValue;
		    private double Protection;
		    private double[] XValue;
		    public  double[] W;
		
		public RobustCentre1(String Dir, int Customer, double Capacity , double Alpha) {
			super(Dir, Customer);	
			this.setW(Customer);
			this.solveCplex(Customer, Capacity, Alpha);
		}

		public void setW(int customer) {
			int Iteration = customer/5;        // 这里事先设定数据只有五层循环，否则很难算。
			   Set<Double> WTree = new TreeSet<Double>();
			   for(int h=0; h<=Iteration; h++){
				   for(int i=0; i<=Iteration; i++){
					   for(int j=0; j<=Iteration; j++){
						   for(int k=0; k<=Iteration; k++){
							   for(int l=0; l<=Iteration; l++){
								   
								   WTree.add((double) (Math.round(Math.log(this.UniInterval[1]))*h+
										     Math.round(Math.log(this.UniInterval[2]))*i+
											 Math.round(Math.log(this.UniInterval[3]))*j+
											 Math.round(Math.log(this.UniInterval[4]))*k+
											 Math.round(Math.log(this.UniInterval[5]))*l));
								   
							   }
						   }
					   }
				   }
			   }
			   
			   Object[] obj     = WTree.toArray();
				this.W = new double[obj.length];
				
				 for (int j=0;j<W.length; j++){
					 this.W[j] = (Double)obj[j];
				 }						 
		}
		
	    public void solveCplex(int customer, double capacity, double alpha) {
			
			double eta;
			this.XValue     = new double[customer];	
			this.OptValue   = 0.0;
			this.Protection = 0.0;
			
			for(int i=0; i<this.W.length; i++){	
				
				double logfactorial = 0.0;
				
				for(int delta=1; delta<=customer; delta++){
					
					logfactorial+=Math.log(delta);                       //即求delta的阶乘
					double fw = -2*Math.exp((logfactorial+Math.log(alpha))/delta)*
							       Math.exp(W[i]/delta);
					double tw = fw/delta;
					
					try{
						IloCplex cplex = new IloCplex();
					     
					     //variable
					    IloNumVar[] x = new IloNumVar[customer];
					   for(int j=0; j<customer;j++){
						       x[j] = cplex.boolVar();
					       }
					   
					 //objective
					    cplex.addMaximize(cplex.scalProd(this.Profit, x));
						
					   //constraints
					     IloLinearNumExpr c1 = cplex.linearNumExpr();
					     for(int j=0; j<customer; j++){   			
						     c1.addTerm(this.Nominal[j]+this.Interval[j]+tw*Math.log(this.Interval[j]), x[j]);  			
					 }    		
					     cplex.addLe(c1, capacity-fw+tw*W[i]);
					     
					     IloLinearNumExpr c2 = cplex.linearNumExpr();
					     for(int j=0; j<customer; j++){   			
						     c2.addTerm(1, x[j]);  			
					 }    		
					     cplex.addEq(c2, delta);
					     
					     if (cplex.solve()){
						 int TempProtection = 0;
						     //Record OPT
						    if(cplex.getObjValue()>OptValue){
								
							for(int k=0;k<customer; k++){					    			
								this.XValue[k]=cplex.getValue(x[k]);
								TempProtection+=(this.Interval[k]+tw*Math.log(this.Interval[k]))*this.XValue[k];
							}
								
							this.OptValue   = cplex.getObjValue();
							this.Protection = TempProtection+fw-tw*W[i];
													
							}
										
					     }
					     cplex.end();
						
				 }catch (IloException e) {
					e.printStackTrace();
				     }
				}				
			}
			
	    }
	    public double getOptValue(){		
			return OptValue;		
		}
		
		public double getProtection(){		
			return Protection;		
		}
		
		public double[] getXValue(){		
			return XValue;		
		}
	}

### Centre Reduce Method(RobustCentre2 Class)

	// Solve centralized Robust combinatorial Problem with Sterling Approximation
	// by Zhang Chao
	// 2015/4/19
	package RobustPackage;

	import ilog.concert.IloException;
	import ilog.concert.IloLinearNumExpr;
	import ilog.concert.IloNumVar;
	import ilog.cplex.IloCplex;

	public class RobustCentre2 extends Robust{
	    
		private double OptValue;
		private double Protection;
		private double[] XValue;
			
		public RobustCentre2(String Dir, int Customer, double Capacity , double Alpha) {
			super(Dir, Customer);		
			this.solveCplex(Customer, Capacity, Alpha);
		}

		public void solveCplex(int customer, double capacity, double alpha) {
			this.XValue     = new double[customer];	
			this.OptValue   = 0.0;
			this.Protection = 0.0;
			
			try{
			     IloCplex cplex = new IloCplex();
			   //variables
			     IloNumVar[] x = new IloNumVar[customer];
			 for(int i=0; i<customer;i++){
				     x[i] = cplex.boolVar();
			     }
		       //objective
			     cplex.addMaximize(cplex.scalProd(this.Profit, x));
			  
			   //constraints
			     IloLinearNumExpr c1 = cplex.linearNumExpr();
			     
			     for(int j=0; j<customer; j++){   			
				     c1.addTerm(this.Nominal[j]+this.Interval[j]-2*this.UniInterval[1]*Math.exp((1+Math.log(alpha))/customer-1), x[j]);  
				 
			 }    		
				 cplex.addLe(c1, capacity);
				 
			     // Record Optimial Value and X Value
				 if (cplex.solve()){
					 // Optimal Value
					 this.OptValue = cplex.getObjValue();
					 
					 // X Value
					 for(int k=0;k<customer; k++){
						this.XValue[k] = cplex.getValue(x[k]);
					}
					// Protection Value
					 for(int k=0;k<customer; k++){
						 this.Protection += (Interval[k]-2*this.UniInterval[1]*Math.exp((1+Math.log(alpha))/customer-1))*cplex.getValue(x[k]);
						}	        			        				        	 
				 }
				 cplex.end();       
		 }catch (IloException e) {
				e.printStackTrace();
			 }
			
		}

		public double getOptValue(){		
			return OptValue;		
		}
		
		public double getProtection(){		
			return Protection;		
		}
		
		public double[] getXValue(){		
			return XValue;		
		}
	}

### Centre Revise Method(RobustCentre2_1 Class)

	package RobustPackage;

	import ilog.concert.IloException;
	import ilog.concert.IloLinearNumExpr;
	import ilog.concert.IloNumVar;
	import ilog.cplex.IloCplex;

	import java.math.BigDecimal;

	public class RobustCentre2_1 extends Robust {
	    private double OptValue;
	    private double Protection;
	    private double[] XValue;
	    public  double   W;
	    
		public RobustCentre2_1(String Dir, int Customer, double Capacity , double Alpha) {
			super(Dir, Customer);	
			this.setW(Customer);
			this.solveCplex(Customer, Capacity, Alpha);
		}
		public void setW(int customer) {
			double f = 0;
			for(int i=0; i<customer;i++){
				f += Math.log(this.Interval[i]);
			} 		
			this.W = (double)(Math.round(f*10))/10;           //保留一位小数
		}
		
	    public void solveCplex(int customer, double capacity, double alpha) {
			
			double eta;
			this.XValue     = new double[customer];	
			this.OptValue   = 0.0;
			this.Protection = 0.0;
			
			for(double t=0; t<=this.W; t= t+0.1){	
							
					try{
						IloCplex cplex = new IloCplex();
					     
					     //variable
					    IloNumVar[] x = new IloNumVar[customer];
					   for(int j=0; j<customer;j++){
						       x[j] = cplex.boolVar();
					       }
					   
					 //objective
					    cplex.addMaximize(cplex.scalProd(this.Profit, x));
						
					   //constraints
					     IloLinearNumExpr c1 = cplex.linearNumExpr();
					     for(int j=0; j<customer; j++){   			
						     c1.addTerm(this.Nominal[j]+this.Interval[j]-2*Math.exp(-1+(1+Math.log(alpha))/customer)* 
								   (Math.exp(t)*(1-t+Math.log(this.Interval[j]))), x[j]);  			
					 }    		
					     cplex.addLe(c1, capacity);
					     

					     
					     if (cplex.solve()){
						 int TempProtection = 0;
						     //Record OPT
						    if(cplex.getObjValue()>OptValue){
								
							for(int k=0;k<customer; k++){					    			
								this.XValue[k]=cplex.getValue(x[k]);
								TempProtection+=(this.Interval[k]-2*Math.exp(-1+(1+Math.log(alpha))/customer)* 
										   (Math.exp(t)*(1-t+Math.log(this.Interval[k]))))*this.XValue[k];
							}
								
							this.OptValue   = cplex.getObjValue();
							this.Protection = TempProtection;
													
							}
										
					     }
					     cplex.end();
						
				 }catch (IloException e) {
					e.printStackTrace();
				     }
		    }						
	    }

		public double getOptValue(){		
			return OptValue;		
		}
		
		public double getProtection(){		
			return Protection;		
		}
		
		public double[] getXValue(){		
			return XValue;		
		}
	}

### Melvyn1 Method(RobustMelvyn Class)

	package RobustPackage;

	import ilog.concert.IloException;
	import ilog.concert.IloLinearNumExpr;
	import ilog.concert.IloNumVar;
	import ilog.cplex.IloCplex;

	import java.io.BufferedReader;
	import java.io.FileReader;
	import java.io.IOException;

	public class RobustMelvyn1 extends Robust{
		private int[] Gamma;
	    private double OptValue;
	    private double Protection;
	    private double[] XValue;
	    
	    public RobustMelvyn1(String Dir, int Customer, double Capacity , double Alpha) {
		super(Dir, Customer);
		this.setGamma(Customer, Alpha);
		this.solveCplex(Customer, Capacity, Alpha);
	    }
	    
		public void setGamma(int customer, double alpha) {
			
			String dir  = "C:\\Users\\fmz\\workspace\\test\\src\\RobustPackage\\Gamma\\Gamma_"+customer+"_"+alpha+".txt"; 
		    this.Gamma  = new int[customer+1];
			try {
				BufferedReader readTxt = new BufferedReader(new FileReader(dir));
				String textLine = readTxt.readLine();       //read a line from .txt file
				String result = "";
				
				while (textLine != null) {
					result += textLine;
					textLine = readTxt.readLine();
				}
				String[] numbersArray = result.split(" ");

				for (int i = 0; i < customer+1; i++) {
					
						this.Gamma[i]   =  Integer.parseInt(numbersArray[i]);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	    
		private void solveCplex(int customer, double capacity, double alpha) {		
			this.XValue     = new double[customer];	
			this.OptValue   = 0.0;
			this.Protection = 0.0;
			for(int i=0; i<UniInterval.length; i++){	
				
					try{
					     IloCplex cplex = new IloCplex();
					     
					     //variable
					    IloNumVar[] x = new IloNumVar[customer];
					   for(int k=0; k<customer;k++){
						       x[k] = cplex.boolVar();
					       }
					 
					 //objective
						    cplex.addMaximize(cplex.scalProd(this.Profit, x));
						    
						 //constraints
						     IloLinearNumExpr c1 = cplex.linearNumExpr();
						     for(int k=0; k<customer; k++){   			
							     c1.addTerm(this.Nominal[k]+Math.max(this.Interval[k]-this.UniInterval[i], 0.0), x[k]);  			
						 }    		
						     cplex.addLe(c1, capacity-this.UniInterval[i]*Gamma[customer]);    
									     
						     if (cplex.solve()){
							 int TempProtection = 0;
							     //Record OPT
							    if(cplex.getObjValue()>OptValue){
									
								for(int k=0;k<customer; k++){					    			
									this.XValue[k]=cplex.getValue(x[k]);
									TempProtection+=Math.max(this.Interval[k]-this.UniInterval[i], 0.0)*this.XValue[k];
								}
									
								this.OptValue   = cplex.getObjValue();
								this.Protection = TempProtection+this.UniInterval[i]*Gamma[customer];
														
								}
											
						     }
					   cplex.end();
				}catch (IloException e) {
					e.printStackTrace();
				}
				
			}
			
		}
	    
		public int[] getGamma(){		
			return Gamma;		
		}

	    public double getOptValue(){		
			return OptValue;		
		}
		
		public double getProtection(){		
			return Protection;		
		}
		
		public double[] getXValue(){		
			return XValue;		
		}
	}

### Melvyn2 Method(RobustMelvyn2 Class)

	package RobustPackage;

	import ilog.concert.IloException;
	import ilog.concert.IloLinearNumExpr;
	import ilog.concert.IloNumVar;
	import ilog.cplex.IloCplex;

	public class RobustMelvyn2 extends Robust{
		private double OptValue;
		private double Protection;
		private double[] XValue;
		public  double[] W;
		
		public RobustMelvyn2(String Dir, int Customer, double Capacity , double Alpha) {
			super(Dir, Customer);	
			this.solveCplex(Customer, Capacity, Alpha);
		}
		
		private void solveCplex(int customer, double capacity, double alpha) {
			
			this.XValue     = new double[customer];	
			this.OptValue   = 0.0;
			this.Protection = 0.0;
			double Gamma = 1.6449*Math.sqrt(customer);
			for(int i=0; i<UniInterval.length; i++){	
				
					try{
					     IloCplex cplex = new IloCplex();
					     
					     //variable
					    IloNumVar[] x = new IloNumVar[customer];
					   for(int k=0; k<customer;k++){
						       x[k] = cplex.boolVar();
					       }
					 
					 //objective
						    cplex.addMaximize(cplex.scalProd(this.Profit, x));
						    
						 //constraints
						     IloLinearNumExpr c1 = cplex.linearNumExpr();
						     for(int k=0; k<customer; k++){   			
							     c1.addTerm(this.Nominal[k]+Math.max(this.Interval[k]-this.UniInterval[i], 0.0), x[k]);  			
						 }    		
						     cplex.addLe(c1, capacity-this.UniInterval[i]*Gamma);    
									     
						     if (cplex.solve()){
							 int TempProtection = 0;
							     //Record OPT
							    if(cplex.getObjValue()>OptValue){
									
								for(int k=0;k<customer; k++){					    			
									this.XValue[k]=cplex.getValue(x[k]);
									TempProtection+=Math.max(this.Interval[k]-this.UniInterval[i], 0.0)*this.XValue[k];
								}
									
								this.OptValue   = cplex.getObjValue();
								this.Protection = TempProtection+this.UniInterval[i]*Gamma;
														
								}
											
						     }
					   cplex.end();
				}catch (IloException e) {
					e.printStackTrace();
				}			
			}		
		}
		
		public double getOptValue(){		
			return OptValue;		
		}
		
		public double getProtection(){		
			return Protection;		
		}
		
		public double[] getXValue(){		
			return XValue;		
		}
		
	}

### Uncentre1 Method(RobustUncertre1 Class)

	package RobustPackage;

	import ilog.concert.IloException;
	import ilog.concert.IloLinearNumExpr;
	import ilog.concert.IloNumVar;
	import ilog.cplex.IloCplex;

	import java.io.BufferedReader;
	import java.io.FileReader;
	import java.io.IOException;

	public class RobustUncentre1 extends Robust{
		private int[] Gamma;
	    private double OptValue;
	    private double Protection;
	    private double[] XValue;
	    
		public RobustUncentre1(String Dir, int Customer, double Capacity , double Alpha) {
			super(Dir, Customer);
			this.setGamma(Customer, Alpha);
			this.solveCplex(Customer, Capacity, Alpha);
		}

		public void setGamma(int customer, double alpha) {
			
			String dir  = "C:\\Users\\fmz\\workspace\\test\\src\\RobustPackage\\Gamma\\Gamma_"+customer+"_"+alpha+".txt"; 
		    this.Gamma  = new int[customer+1];
			try {
				BufferedReader readTxt = new BufferedReader(new FileReader(dir));
				String textLine = readTxt.readLine();       //read a line from .txt file
				String result = "";
				
				while (textLine != null) {
					result += textLine;
					textLine = readTxt.readLine();
				}
				String[] numbersArray = result.split(" ");

				for (int i = 0; i < customer+1; i++) {
					
						this.Gamma[i]   =  Integer.parseInt(numbersArray[i]);

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		private void solveCplex(int customer, double capacity, double alpha) {		
			this.XValue     = new double[customer];	
			this.OptValue   = 0.0;
			this.Protection = 0.0;
			for(int i=0; i<UniInterval.length; i++){	
				for(int delta=1; delta<=customer; delta++){

					try{
					     IloCplex cplex = new IloCplex();
					     
					     //variable
					    IloNumVar[] x = new IloNumVar[customer];
					   for(int k=0; k<customer;k++){
						       x[k] = cplex.boolVar();
					       }
					 
					 //objective
						    cplex.addMaximize(cplex.scalProd(this.Profit, x));
						    
						 //constraints
						     IloLinearNumExpr c1 = cplex.linearNumExpr();
						     for(int k=0; k<customer; k++){   			
							     c1.addTerm(this.Nominal[k]+Math.max(this.Interval[k]-this.UniInterval[i], 0.0), x[k]);  			
						 }    		
						     cplex.addLe(c1, capacity-this.UniInterval[i]*Gamma[delta]);    
					   
						     IloLinearNumExpr c2 = cplex.linearNumExpr();
						     for(int j=0; j<customer; j++){   			
							     c2.addTerm(1, x[j]);  			
						 }    		
						     cplex.addEq(c2, delta);
						     
						     if (cplex.solve()){
							 int TempProtection = 0;
							     //Record OPT
							    if(cplex.getObjValue()>OptValue){
									
								for(int k=0;k<customer; k++){					    			
									this.XValue[k]=cplex.getValue(x[k]);
									TempProtection+=Math.max(this.Interval[k]-this.UniInterval[i], 0.0)*this.XValue[k];
								}
									
								this.OptValue   = cplex.getObjValue();
								this.Protection = TempProtection+this.UniInterval[i]*Gamma[delta];
														
								}
											
						     }
					   cplex.end();
				}catch (IloException e) {
					e.printStackTrace();
				}
				}
			}
			
		}
		
		public int[] getGamma(){		
			return Gamma;		
		}


	    public double getOptValue(){		
			return OptValue;		
		}
		
		public double getProtection(){		
			return Protection;		
		}
		
		public double[] getXValue(){		
			return XValue;		
		}
	}

### Uncentre2 Method(RobustUncentre2 Class)

	package RobustPackage;

	import ilog.concert.IloException;
	import ilog.concert.IloLinearNumExpr;
	import ilog.concert.IloNumVar;
	import ilog.cplex.IloCplex;

	public class RobustUncentre2 extends Robust{
		private double OptValue;
		private double Protection;
		private double[] XValue;
		public  double[] W;
		
		public RobustUncentre2(String Dir, int Customer, double Capacity , double Alpha) {
			super(Dir, Customer);	
			this.solveCplex(Customer, Capacity, Alpha);
		}

		private void solveCplex(int customer, double capacity, double alpha) {
			double eta;
			this.XValue     = new double[customer];	
			this.OptValue   = 0.0;
			this.Protection = 0.0;
			
			for(int i=0; i<UniInterval.length; i++){	    	
				for(int j=0; j<=customer; j++){	
					
					double fw = 1.6449*Math.sqrt(j);
					if(j==0){
					    eta = 1.6449;				
					    }else{
					    eta = 1.6449/(2*Math.sqrt(j));	
					    }	
					
					try{
					     IloCplex cplex = new IloCplex();
					     
					     //variable
					    IloNumVar[] x = new IloNumVar[customer];
					   for(int k=0; k<customer;k++){
						       x[k] = cplex.boolVar();
					       }
					   
					 //objective
					    cplex.addMaximize(cplex.scalProd(this.Profit, x));
					    
					   //constraints
					     IloLinearNumExpr c1 = cplex.linearNumExpr();
					     for(int k=0; k<customer; k++){   			
						     c1.addTerm(this.Nominal[k]+Math.max(this.Interval[k]-this.UniInterval[i], 0.0)+
								 this.UniInterval[i]*eta, x[k]);  			
					 }    		
					     cplex.addLe(c1, capacity-this.UniInterval[i]*(fw-eta*j));  
					      
					     if (cplex.solve()){
						 int Temp = 0;
					       
							for(int k=0;k<customer; k++){
											
								Temp += cplex.getValue(x[k])*(Math.max(this.Interval[k]-this.UniInterval[i], 0.0)+
										 this.UniInterval[i]*eta) ; 			    		
							}
							
							 //Record OPT
							if(cplex.getObjValue()>OptValue){
								
								for(int k=0;k<customer; k++){
									
									this.XValue[k]=cplex.getValue(x[k]);	
									
								}
								
								    this.OptValue = cplex.getObjValue();
								    this.Protection = Temp+this.UniInterval[i]*(fw-eta*j);
													
							}
										
					     }
					     cplex.end();
					 }catch (IloException e) {
						e.printStackTrace();
					}
				}		 
			}
			
			
			   
			   
		}
		
		public double getOptValue(){		
			return OptValue;		
		}
		
		public double getProtection(){		
			return Protection;		
		}
		
		public double[] getXValue(){		
			return XValue;		
		}
			
	}

### Bental Method(RobustBental Class)

	package RobustPackage;

	import ilog.concert.IloException;
	import ilog.concert.IloLinearNumExpr;
	import ilog.concert.IloNumVar;
	import ilog.cplex.IloCplex;

	import java.util.Set;
	import java.util.TreeSet;

	public class RobustBental extends Robust {
		private double OptValue;
		private double Protection;
		private double[] XValue;
		public  double[] W;
		
		public RobustBental(String Dir, int Customer, double Capacity , double Alpha) {
			super(Dir, Customer);	
			this.setW(Customer);
			this.solveCplex(Customer, Capacity, Alpha);
		}

		public void setW(int customer) {
		   int Iteration = customer/5;        // 这里事先设定数据只有五层循环，否则很难算。
		   Set<Double> WTree = new TreeSet<Double>();
		   for(int h=0; h<=Iteration; h++){
			   for(int i=0; i<=Iteration; i++){
				   for(int j=0; j<=Iteration; j++){
					   for(int k=0; k<=Iteration; k++){
						   for(int l=0; l<=Iteration; l++){
							   
							   WTree.add(this.UniInterval[1]*this.UniInterval[1]*h+
									     this.UniInterval[2]*this.UniInterval[2]*i+
									     this.UniInterval[3]*this.UniInterval[3]*j+
									     this.UniInterval[4]*this.UniInterval[4]*k+
									     this.UniInterval[5]*this.UniInterval[5]*l);
						   }
					   }
				   }
			   }
			}
		 
			Object[] obj     = WTree.toArray();
			this.W = new double[obj.length];
			
			 for (int j=0;j<W.length; j++){
				 this.W[j] = (Double)obj[j];
			 }			
		}

		public void solveCplex(int customer, double capacity, double alpha) {
			
			double eta;
			this.XValue     = new double[customer];	
			this.OptValue   = 0.0;
			this.Protection = 0.0;
			
			for(int i=0; i<this.W.length; i++){	    	
			   if(this.W[i]==0){
				    eta = Math.sqrt(-2*Math.log(alpha)/this.W[1]);				
				    }else{
				    eta = Math.sqrt(-Math.log(alpha)/(2*this.W[i]));	
				    }		
			
			try{
			     IloCplex cplex = new IloCplex();
			     
			     //variable
			    IloNumVar[] x = new IloNumVar[customer];
			   for(int j=0; j<customer;j++){
				       x[j] = cplex.boolVar();
			       }
			   
			 //objective
			    cplex.addMaximize(cplex.scalProd(this.Profit, x));
			    
			   //constraints
			     IloLinearNumExpr c1 = cplex.linearNumExpr();
			     for(int j=0; j<customer; j++){   			
				     c1.addTerm(this.Nominal[j]+this.Interval[j]*this.Interval[j]*eta, x[j]);  			
			 }    		
			     cplex.addLe(c1, capacity-Math.sqrt(-2*Math.log(alpha)*W[i])+W[i]*eta);  
			      
			     if (cplex.solve()){
				 int Temp = 0;
			       
					for(int k=0;k<customer; k++){
									
						Temp += cplex.getValue(x[k])*Interval[k]*Interval[k]; 			    		
					}
					
					 //Record OPT
					if(Temp== W[i] && cplex.getObjValue()>OptValue){
						
						for(int k=0;k<customer; k++){
							
							this.XValue[k]=cplex.getValue(x[k]);	
							
						}
						
						    this.OptValue = cplex.getObjValue();
						    this.Protection = Math.sqrt(-2*Math.log(alpha)*Temp);
											
					}
								
			     }
			     cplex.end();
			 }catch (IloException e) {
				e.printStackTrace();
			}
			}

		}

		public double getOptValue(){		
			return OptValue;		
		}
		
		public double getProtection(){		
			return Protection;		
		}
		
		public double[] getXValue(){		
			return XValue;		
		}
		
	}

>1. 上述问题是鲁棒性问题的程序实现，数据的处理是一个父类，被不同的方法继承
2. 主方法通过不同方法类的实例化来求解不同问题的方法
3. 主方法能实现循环文件的读取和结果的输出

## 自写程序

### Main(Main Class)

	package robust_opt;

	import java.text.*;

	public class Main {
		public static void main(String args[]) throws Exception{
			
			double[] optValue = new double[1] ;
			double[] protectValue = new double[1] ;
			
			double capacity = 500 ;
			double α = 0.05 ;
			int costomer = 100  ;
			
			DecimalFormat df = new DecimalFormat("#.##") ;
			
			for(int i=1;i<=1;i++){
				Robust_Centre1 centre1 = new Robust_Centre1("F:\\JAVA_WORKPLACE\\Test32\\src\\robust_opt\\Binary\\Instance_"+costomer+"_"+i+".txt",capacity,α) ;
				optValue[i-1] = centre1.optValue ;
				protectValue[i-1] = centre1.protectValue ;
			}
			
			System.out.println("optValue: ") ; 
			for(int i=0;i<1;i++){
				System.out.print(df.format(optValue[i])+" ") ;
			}
			System.out.println() ;
			System.out.println("protectValue: ") ; 
			for(int i=0;i<1;i++){
				System.out.print(df.format(protectValue[i])+" ") ;
			}
			
			
			/*
			DataProcess_BufferedReader data = new DataProcess_BufferedReader("F:\\JAVA_WORKPLACE\\Test32\\src\\robust_opt\\Binary\\Instance_20_50.txt",20) ;
			DataProcess_InputStream data = new DataProcess_InputStream("F:\\JAVA_WORKPLACE\\Test32\\src\\robust_opt\\Binary\\Instance_20_1.txt") ;
			
			for(int i=0;i<data.c.length;i++){
				System.out.print(data.c[i]+"\t") ;
			}
			System.out.println() ;
			for(int i=0;i<data.c.length;i++){
				System.out.print(data.value[i]+"\t") ;
			}
			System.out.println() ;
			for(int i=0;i<data.c.length;i++){
				System.out.print(data.median[i]+"\t") ;
			}
			System.out.println() ;
			for(int i=0;i<data.c.length;i++){
				System.out.print(data.interval[i]+"\t") ;
			}
			System.out.println() ;
			for(int i=0;i<data.UniInterval.length;i++){
				System.out.print(data.UniInterval[i]+"\t") ;
			}
			System.out.println() ;
			
			Robust_Centre1 centre1 = new Robust_Centre1("F:\\JAVA_WORKPLACE\\Test32\\src\\robust_opt\\Binary\\Instance_100_1.txt") ;
			for(int i=0;i<centre1.UniW.length;i++){
				System.out.print(centre1.UniW[i]+"\t") ; 
			}
			System.out.println("\n"+centre1.UniW.length) ;
			*/
		}
	}

### 字符流数据处理(DataProcess_BufferedReader Class)

	package robust_opt;
	import java.io.*;
	import java.util.Set;
	import java.util.TreeSet;

	public class DataProcess_BufferedReader{
		double[] c = null ;
		double[] value = null ;
		double[] median = null ;
		double[] interval = null ;
		double[] UniInterval = null ;
		
		public DataProcess_BufferedReader(String str,int costomer) throws Exception{
			this.readInstance(str,costomer);
			this.setUniInterval() ;
		}
		
		
			
		public void readInstance(String str,int costomer)throws Exception{
				
			File f = new File(str) ;
			Reader r = new FileReader(f) ;
			BufferedReader buf = new BufferedReader(r) ;
			
			this.c = new double[costomer] ;
			this.value = new double[costomer] ;
			this.median = new double[costomer] ;
			this.interval = new double[costomer] ;
			
			String s = "" ;
			String line = buf.readLine() ;
			while(line != null){
				s += line ;
				line = buf.readLine() ;
			}
			
			String[] split = s.split(" ") ;
			for(int i=0;i<costomer;i++){
				c[i] = Double.parseDouble(split[i]) ;
				value[i] = Double.parseDouble(split[i+costomer]) ;
				median[i] = Double.parseDouble(split[i+2*costomer]) ;
				interval[i] = Double.parseDouble(split[i+3*costomer]) ;
			}
			
			buf.close() ;
		}
		
		public void setUniInterval(){
			Set<Double> Uni = new TreeSet<Double>() ;
			for(int i=0;i<interval.length;i++){
				Uni.add(interval[i]) ;
			}
			Object[] obj = Uni.toArray() ;
			UniInterval = new double[obj.length] ;
			for(int i=0;i<obj.length;i++){
				UniInterval[i] = (double)obj[i] ;
			}
		}
	}

### 字节流数据处理(DataProcess_inputStream)

	package robust_opt;
	import java.io.*;
	import java.util.*;

	class DataProcess_InputStream {
		double[] c = null ;
		double[] value = null ;
		double[] median = null ;
		double[] interval = null ;
		double[] UniInterval = null ;
		
		public DataProcess_InputStream(String str) throws Exception{
			this.readInstance(str);
			this.setUniInterval() ;
		}
		
		public void readInstance(String str)throws Exception{
			
			File f = new File(str) ;
			InputStream input = new FileInputStream(f) ;
			byte[] b = new byte[(int)f.length()] ;
			input.read(b) ;
			String string = new String(b) ;
			String[] split = string.split("\r\n") ;
			String[][] array = new String[split.length][] ;
			for(int i=0;i<split.length;i++){
				array[i] = split[i].split(" ") ;
			}
			
			int costomer = array[0].length ;
			this.c = new double[costomer] ;
			this.value = new double[costomer] ;
			this.median = new double[costomer] ;
			this.interval = new double[costomer] ;
			for(int i=0;i<array[0].length;i++){
				c[i] = Double.parseDouble(array[0][i]) ;
				value[i] = Double.parseDouble(array[1][i]) ;
				median[i] = Double.parseDouble(array[2][i]) ;
				interval[i] = Double.parseDouble(array[3][i]) ;
			}
			
			input.close() ;
		}
		
		public void setUniInterval(){
			Set<Double> Uni = new TreeSet<Double>() ;
			for(int i=0;i<interval.length;i++){
				Uni.add(interval[i]) ;
			}
			Object[] obj = Uni.toArray() ;
			UniInterval = new double[obj.length] ;
			for(int i=0;i<obj.length;i++){
				UniInterval[i] = (double)obj[i] ;
			}
		}
	}

### Centre1 Method(Robust_Centre1 Class)

	package robust_opt;
	import java.util.* ;
	import ilog.concert.*;
	import ilog.cplex.*;

	public class Robust_Centre1 extends DataProcess_InputStream {
		
		public double optValue = 0 ;
		public double protectValue = 0 ;
		public int[] XValue = null ;
		public double[] UniW = null ;
		
		
		public Robust_Centre1(String str,double capacity,double α) throws Exception {
			super(str) ;
			this.setW() ;
			this.methodCentre1(capacity, α);
		}
		
		public void methodCentre1(double capacity,double α){
			
		int costomer = c.length ;
		int[] δ = new int[costomer] ;
		for(int i=0;i<costomer;i++){
			δ[i] = i+1 ;
		}
		
		this.XValue = new int[costomer] ;
		
		double[][] f = new double[UniW.length][δ.length] ;
		double[][] η = new double[UniW.length][δ.length] ;
			for(int w=0;w<UniW.length;w++){
				for(int Δ=0;Δ<δ.length;Δ++){
					try{
						IloCplex cplex = new IloCplex() ;
						IloNumVar[] x = new IloNumVar[costomer] ;
						for(int i=0;i<x.length;i++){
							x[i] = cplex.boolVar() ;
						}
						double lg_factorial_η = 0 ;	
						for(int i=0;i<δ[Δ];i++){
							lg_factorial_η += Math.log(i+1) ;
						}
						f[w][Δ] = (-2) * Math.pow(Math.E,(lg_factorial_η + Math.log(α)) / δ[Δ] ) * Math.pow(Math.E, UniW[w]/δ[Δ]) ;
						η[w][Δ] = f[w][Δ] / δ[Δ] ;
							
						IloLinearNumExpr e1 = cplex.linearNumExpr() ;
						IloLinearNumExpr e2 = cplex.linearNumExpr() ;
						for(int i=0;i<costomer;i++){
							e1.addTerm(median[i],x[i]) ;
							e1.addTerm(interval[i], x[i]) ;
							e1.addTerm(Math.log(interval[i] * η[w][Δ]), x[i]);
							e2.addTerm(1, x[i]);
						}
							
						cplex.addLe(e1, capacity + η[w][Δ]*UniW[w] - f[w][Δ]) ;
						cplex.addEq(e2, δ[Δ]) ;
							
						cplex.addMaximize(cplex.scalProd(c, x)) ;
							
						if(cplex.solve()){
							double tempProtectValue = 0 ;
							if(cplex.getObjValue() > this.optValue){
								for(int i=0;i<costomer;i++){
									this.XValue[i] = (int) cplex.getValue(x[i]) ;
									tempProtectValue = (interval[i] + η[w][Δ]*Math.log(interval[i])) * XValue[i] ;
								}
							this.optValue = cplex.getObjValue() ;
							this.protectValue = tempProtectValue + f[w][Δ] - η[w][Δ]*UniW[w] ;
								
							cplex.output().println("Solution Status = "+cplex.getStatus()) ;
							}
							cplex.end() ;
						}
					}catch(Exception e){
						System.out.println(e) ;
					}		
				}
			}
		}
		
		public void setW(){
			Set<Double> Uni = new TreeSet<Double>() ;
			
			int iteration = c.length/5 ;
			for(int i=0;i<iteration;i++){
				for(int j=0;j<iteration;j++){
					for(int k=0;k<iteration;k++){
						for(int l=0;l<iteration;l++){
							for(int m=0;m<iteration;m++){
								Uni.add((double)(Math.round(Math.log(UniInterval[0])*i)+
												 Math.round(Math.log(UniInterval[1])*j)+
												 Math.round(Math.log(UniInterval[2])*k)+
												 Math.round(Math.log(UniInterval[3])*l)+
												 Math.round(Math.log(UniInterval[4])*m))
												) ;
							}
						}
					}
				}
			}
			Object[] obj = Uni.toArray() ;
			UniW = new double[obj.length] ;
			for(int i=0;i<obj.length;i++){
				UniW[i] = (double)obj[i] ;
			}
		}
		
		/*
		public long factorial(int n){
			long facorial = 0 ;
			if(n == 0 || n == 1){
				facorial = 1 ;
			}else{
				facorial = n * factorial(n-1) ;
			}
			return facorial ;
		}
		*/
	}

1. 字节流和字符流都能够实现数据的读取，注意采用字符流时要数据的格式在换行时毕字节流更为严格.
2. 运用阶乘方法的时候，会有数据溢出的情况出现，注意转化

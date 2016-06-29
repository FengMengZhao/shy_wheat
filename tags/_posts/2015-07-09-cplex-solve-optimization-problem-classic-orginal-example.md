---
layout: post
title: cplex求解优化问题——ilog自带经典案例
categories: 技术 Java 引用
tags: cplex 案例
---

### Warehouse Problem

	/* --------------------------------------------------------------------------
	 * File: Warehouse.java
	 * Version 12.2  
	 * --------------------------------------------------------------------------
	 * Licensed Materials - Property of IBM
	 * 5725-A06 5725-A29 5724-Y48 5724-Y49 5724-Y54 5724-Y55
	 * Copyright IBM Corporation 2001, 2010. All Rights Reserved.
	 *
	 * US Government Users Restricted Rights - Use, duplication or
	 * disclosure restricted by GSA ADP Schedule Contract with
	 * IBM Corp.
	 * --------------------------------------------------------------------------
	 *
	 * warehouse.java - Example that uses the goal API to enforce constraints
	 *                  dynamically, depending on the relaxation solution at
	 *                  each MIP node.
	 *
	 *                  Given a set of warehouses that each have a
	 *                  capacity, a cost per unit stored, and a 
	 *                  minimum usage level, this example find an
	 *                  assignment of items to warehouses that minimizes
	 *                  total cost.  The minimum usage levels are
	 *                  enforced dynamically using the goal API.
	 */

	import ilog.concert.*;
	import ilog.cplex.*;

	public class Warehouse {
	   static class SemiContGoal extends IloCplex.Goal {
	      IloNumVar[] _scVars;
	      double[]    _scLbs;
	      
	      SemiContGoal(IloNumVar[] scVars,
			   double[]    scLbs) {
		 _scVars = scVars;
		 _scLbs  = scLbs;
	      }
	      
	      public IloCplex.Goal execute(IloCplex cplex) throws IloException {
		 int besti = -1;
		 double maxObjCoef = Double.MIN_VALUE;
	       
		 // From among all variables that do not respect their minimum 
		 // usage levels, select the one with maximum objective coefficient.
		 for (int i = 0; i < _scVars.length; i++) {
		    double val = getValue(_scVars[i]);
		    if ( val >= 1e-5            &&
			 val <= _scLbs[i] - 1e-5  ) {
		       if (getObjCoef(_scVars[i]) >= maxObjCoef) {
			  besti = i;
			  maxObjCoef = getObjCoef(_scVars[i]);
		       }
		    }
		 }
	       
		 //  If any are found, branch to enforce the condition that
		 //  the variable must either be 0.0 or greater than
		 //  the minimum usage level.
		 if ( besti != -1 ) {
		    return cplex.and(cplex.or(cplex.leGoal(_scVars[besti], 0.0),
					      cplex.geGoal(_scVars[besti], 
							   _scLbs[besti])),
				     this );
		 }
		 else if ( !isIntegerFeasible() ) {
		    return cplex.and(cplex.branchAsCplex(), this );
		 }
	       
		 return null;
	      }
	   }
	   
	   public static void main (String args[]) {
	      try {
		 IloCplex cplex = new IloCplex();
	       
		 int nbWhouses = 4;
		 int nbLoads = 31;
	       
		 IloNumVar[] capVars = 
		    cplex.numVarArray(nbWhouses, 0, 10, 
				      IloNumVarType.Int); // Used capacities
		 double[]    capLbs  = {2.0, 3.0, 5.0, 7.0}; // Minimum usage level
		 double[]    costs   = {1.0, 2.0, 4.0, 6.0}; // Cost per warehouse
	       
		 // These variables represent the assigninment of a
		 // load to a warehouse.
		 IloNumVar[][] assignVars = new IloNumVar[nbWhouses][];
		 for (int w = 0; w < nbWhouses; w++) {
		    assignVars[w] = cplex.numVarArray(nbLoads, 0, 1,
						      IloNumVarType.Int);
		    
		    // Links the number of loads assigned to a warehouse with 
		    // the capacity variable of the warehouse.
		    cplex.addEq(cplex.sum(assignVars[w]), capVars[w]);
		 }
	       
		 // Each load must be assigned to just one warehouse.
		 for (int l = 0; l < nbLoads; l++) {
		    IloNumVar[] aux = new IloNumVar[nbWhouses];
		    for (int w = 0; w < nbWhouses; w++)
		       aux[w] = assignVars[w][l];
		  
		    cplex.addEq(cplex.sum(aux), 1);
		 }
	       
		 cplex.addMinimize(cplex.scalProd(costs, capVars));
	       
		 cplex.setParam(IloCplex.IntParam.MIPSearch, IloCplex.MIPSearch.Traditional);

		 if ( cplex.solve(new SemiContGoal(capVars, capLbs)) ) {
		    System.out.println("--------------------------------------------");
		    System.out.println();
		    System.out.println("Solution found:");
		    System.out.println(" Objective value = " + cplex.getObjValue());
		    System.out.println();
		    for (int w = 0; w < nbWhouses; w++) {
		       System.out.println("Warehouse " + w + ": stored " 
					  + cplex.getValue(capVars[w]) + " loads");
		       for (int l = 0; l < nbLoads; l++) {
			  if ( cplex.getValue(assignVars[w][l]) > 1e-5 )
			     System.out.print("Load " + l + " | ");
		       }
		       System.out.println(); System.out.println();
		    }
		    System.out.println("--------------------------------------------");
		 }
		 else {
		    System.out.println(" No solution found ");
		 }
		 cplex.end();
	      }
	      catch (IloException e) {
		 System.err.println("Concert exception caught: " + e);
	      }    
	   }
	}

### Transport Problem

	/* --------------------------------------------------------------------------
	 * File: Transport.java
	 * Version 12.2  
	 * --------------------------------------------------------------------------
	 * Licensed Materials - Property of IBM
	 * 5725-A06 5725-A29 5724-Y48 5724-Y49 5724-Y54 5724-Y55
	 * Copyright IBM Corporation 2001, 2010. All Rights Reserved.
	 *
	 * US Government Users Restricted Rights - Use, duplication or
	 * disclosure restricted by GSA ADP Schedule Contract with
	 * IBM Corp.
	 * --------------------------------------------------------------------------
	 */

	import ilog.cplex.*;
	import ilog.concert.*;

	public class Transport {
	   public static void main(String[] args) {
	     if ( args.length < 1 ) {
		 System.err.println("Usage: java Transport <type>");
		 System.err.println("  type = 0 -> convex  piecewise linear model");
		 System.err.println("  type = 1 -> concave piecewise linear model");
		 return;
	     }

	     try {
		IloCplex cplex = new IloCplex();
	      
		int nbDemand = 4;
		int nbSupply = 3;
		double[] supply = {1000.0, 850.0, 1250.0};
		double[] demand = {900.0, 1200.0, 600.0, 400.0};
	      
		IloNumVar[][] x = new IloNumVar[nbSupply][];
		IloNumVar[][] y = new IloNumVar[nbSupply][];
	      
		for (int i = 0; i < nbSupply; i++) {
		   x[i] = cplex.numVarArray(nbDemand, 0., Double.MAX_VALUE);
		   y[i] = cplex.numVarArray(nbDemand, 0., Double.MAX_VALUE);
		} 
	      
		for (int i = 0; i < nbSupply; i++)       // supply must meet demand
		   cplex.addEq(cplex.sum(x[i]), supply[i]);
	      
		for (int j = 0; j < nbDemand; j++) {     // demand must meet supply
		   IloLinearNumExpr v = cplex.linearNumExpr(); 
		   for(int i = 0; i < nbSupply; i++)
		      v.addTerm(1., x[i][j]);
		   cplex.addEq(v, demand[j]);
		}      
	      
		double[] points;
		double[] slopes;
		if ( args[0].charAt(0) == '0' ) {         // convex case
		   points = new double[] {200.0, 400.0};
		   slopes = new double[] { 30.0, 80.0, 130.0};
		}
		else {                                  // concave case
		   points = new double[] {200.0, 400.0};
		   slopes = new double[] {120.0, 80.0, 50.0};
		}
		for (int i = 0; i < nbSupply; ++i) {
		   for (int j = 0; j < nbDemand; ++j) {
		      cplex.addEq(y[i][j],
				  cplex.piecewiseLinear(x[i][j],
							points, slopes, 0.0, 0.0));
		   }
		}
	      
		IloLinearNumExpr expr = cplex.linearNumExpr();
		for (int i = 0; i < nbSupply; ++i) {
		   for (int j = 0; j < nbDemand; ++j) {
		      expr.addTerm(y[i][j], 1.);   
		   }
		}
		
		cplex.addMinimize(expr);
	      
		if ( cplex.solve() ) {
		   System.out.println(" - Solution: "); 
		   for (int i = 0; i < nbSupply; ++i) {
		      System.out.print("   " + i + ": ");
		      for (int j = 0; j < nbDemand; ++j)
			 System.out.print("" + cplex.getValue(x[i][j]) + "\t");
		      System.out.println();
		   }
		   System.out.println("   Cost = " + cplex.getObjValue()); 
		}
		cplex.end();
	     }
	     catch (IloException exc) {
		System.out.println(exc);
	     }
	   }
	}

### 字节流数据读取(InputDataReader)

	/* --------------------------------------------------------------------------
	 * File: InputDataReader.java
	 * Version 12.2  
	 * --------------------------------------------------------------------------
	 * Licensed Materials - Property of IBM
	 * 5725-A06 5725-A29 5724-Y48 5724-Y49 5724-Y54 5724-Y55
	 * Copyright IBM Corporation 2001, 2010. All Rights Reserved.
	 *
	 * US Government Users Restricted Rights - Use, duplication or
	 * disclosure restricted by GSA ADP Schedule Contract with
	 * IBM Corp.
	 * --------------------------------------------------------------------------
	 *
	 * This is a helper class used by several examples to read input data files
	 * containing arrays in the format [x1, x2, ..., x3].  Up to two-dimensional
	 * arrays are supported.
	 */

	import java.io.*;

	public class InputDataReader {
	   public static class InputDataReaderException extends Exception {
	      private static final long serialVersionUID = 1021L;
	      InputDataReaderException(String file) {
		 super("'" + file + "' contains bad data format");
	      }
	   }
	   
	   StreamTokenizer _tokenizer;
	   Reader _reader;
	   String _fileName;

	   public InputDataReader(String fileName) throws IOException {
	      _reader = new FileReader(fileName);
	      _fileName = fileName;
	    
	      _tokenizer = new StreamTokenizer(_reader);
	    
	      // State the '"', '\'' as white spaces.
	      _tokenizer.whitespaceChars('"', '"');
	      _tokenizer.whitespaceChars('\'', '\'');
	    
	      // State the '[', ']' as normal characters.
	      _tokenizer.ordinaryChar('[');
	      _tokenizer.ordinaryChar(']');
	      _tokenizer.ordinaryChar(',');
	   }

	   protected void finalize() throws Throwable {
	      _reader.close();
	   }

	   double readDouble() throws InputDataReaderException,
				      IOException {
	      int ntType = _tokenizer.nextToken();
	      
	      if ( ntType != StreamTokenizer.TT_NUMBER )
		 throw new InputDataReaderException(_fileName);
	      
	      return _tokenizer.nval;
	   }
	     
	   int readInt() throws InputDataReaderException,
				IOException {
	      int ntType = _tokenizer.nextToken();
	    
	      if ( ntType != StreamTokenizer.TT_NUMBER )
		 throw new InputDataReaderException(_fileName);
	      
	      return (new Double(_tokenizer.nval)).intValue();
	   }
	   
	   double[] readDoubleArray() throws InputDataReaderException,
					     IOException {
	      int ntType = _tokenizer.nextToken(); // Read the '['
	      
	      if ( ntType != '[' )
		 throw new InputDataReaderException(_fileName);
	      
	      DoubleArray values = new DoubleArray();
	      ntType = _tokenizer.nextToken();
	      while (ntType == StreamTokenizer.TT_NUMBER) {
		 values.add(_tokenizer.nval);
		 ntType = _tokenizer.nextToken();
		 
		 if ( ntType == ',' ) {
		    ntType = _tokenizer.nextToken();
		 }
		 else if ( ntType != ']' ) {
		    throw new InputDataReaderException(_fileName);
		 }
	      }
	      
	      if ( ntType != ']' )
		 throw new InputDataReaderException(_fileName);
	    
	      // Allocate and fill the array.
	      double[] res = new double[values.getSize()];
	      for (int i = 0; i < values.getSize(); i++) {
		 res[i] = values.getElement(i);
	      }
	      
	      return res;
	   }

	   double[][] readDoubleArrayArray() throws InputDataReaderException,
						    IOException {
	      int ntType = _tokenizer.nextToken(); // Read the '['
	      
	      if ( ntType != '[' )
		 throw new InputDataReaderException(_fileName);
	      
	      DoubleArrayArray values = new DoubleArrayArray();
	      ntType = _tokenizer.nextToken();
	      
	      while (ntType == '[') {
		 _tokenizer.pushBack();
		 
		 values.add(readDoubleArray());
		 
		 ntType = _tokenizer.nextToken();
		 if      ( ntType == ',' ) {
		   ntType = _tokenizer.nextToken();
		 }
		 else if ( ntType != ']' ) {
		   throw new InputDataReaderException(_fileName);
		 }
	      }
	    
	      if ( ntType != ']' )
		 throw new InputDataReaderException(_fileName);
	    
	      // Allocate and fill the array.
	      double[][] res = new double[values.getSize()][];
	      for (int i = 0; i < values.getSize(); i++) { 
		 res[i] = new double[values.getSize(i)];
		 for (int j = 0; j < values.getSize(i); j++) { 
		    res[i][j] = values.getElement(i,j);
		 }
	      }
	      return res;
	   }

	   int[] readIntArray() throws InputDataReaderException,
				       IOException {
	      int ntType = _tokenizer.nextToken(); // Read the '['
	      
	      if ( ntType != '[' )
		 throw new InputDataReaderException(_fileName);
	      
	      IntArray values = new IntArray();
	      ntType = _tokenizer.nextToken();
	      while (ntType == StreamTokenizer.TT_NUMBER) {
		 values.add(_tokenizer.nval);
		 ntType = _tokenizer.nextToken();
		 
		 if      ( ntType == ',' ) {
		    ntType = _tokenizer.nextToken();
		 }
		 else if ( ntType != ']' ) {
		    throw new InputDataReaderException(_fileName);
		 }
	      }
	      
	      if ( ntType != ']' )
		 throw new InputDataReaderException(_fileName);

	      // Allocate and fill the array.
	      int[] res = new int[values.getSize()];
	      for (int i = 0; i < values.getSize(); i++) {
		 res[i] = values.getElement(i);
	      }
	      return res;
	   }

	   int[][] readIntArrayArray() throws InputDataReaderException,
					      IOException {
	      int ntType = _tokenizer.nextToken(); // Read the '['
	      
	      if ( ntType != '[' )
		 throw new InputDataReaderException(_fileName);
	      
	      IntArrayArray values = new IntArrayArray();
	      ntType = _tokenizer.nextToken();
	      
	      while (ntType == '[') {
		 _tokenizer.pushBack();
		 
		 values.add(readIntArray());
		 
		 ntType = _tokenizer.nextToken();
		 if      ( ntType == ',' ) {
		    ntType = _tokenizer.nextToken();
		 }
		 else if ( ntType != ']' ) {
		    throw new InputDataReaderException(_fileName);
		 }
	      }
	    
	      if ( ntType != ']' )
		 throw new InputDataReaderException(_fileName);
	    
	      // Allocate and fill the array.
	      int[][] res = new int[values.getSize()][];
	      for (int i = 0; i < values.getSize(); i++) {
		 res[i] = new int[values.getSize(i)];
		 for (int j = 0; j < values.getSize(i); j++) {
		    res[i][j] = values.getElement(i,j);
		 }
	      }    
	      return res;
	   }

	   private class DoubleArray {
	      int      _num   = 0;
	      double[] _array = new double[32];

	      final void add(double dval) {
		 if ( _num >= _array.length ) {
		    double[] array = new double[2 * _array.length];
		    System.arraycopy(_array, 0, array, 0, _num);
		    _array = array;
		 }
		 _array[_num++] = dval;
	      }

	      final double getElement(int i) { return _array[i]; }
	      final int    getSize()         { return _num; }
	   }

	   private class DoubleArrayArray {
	      int        _num   = 0;
	      double[][] _array = new double[32][];

	      final void add(double[] dray) {

		 if ( _num >= _array.length ) {
		    double[][] array = new double[2 * _array.length][];
		    for (int i = 0; i < _num; i++) {
		       array[i] = _array[i];
		    }
		    _array = array;
		 }
		 _array[_num] = new double[dray.length];
		 System.arraycopy(dray, 0, _array[_num], 0, dray.length);
		 _num++;
	      }

	      final double getElement(int i, int j) { return _array[i][j]; }
	      final int    getSize()                { return _num; }
	      final int    getSize(int i)           { return _array[i].length; }
	   }


	   private class IntArray {
	      int   _num   = 0;
	      int[] _array = new int[32];

	      final void add(double ival) {
		 if ( _num >= _array.length ) {
		    int[] array = new int[2 * _array.length];
		    System.arraycopy(_array, 0, array, 0, _num);
		    _array = array;
		 }
		 _array[_num++] = (int)Math.round(ival);
	      }

	      final int getElement(int i) { return _array[i]; }
	      final int getSize()         { return _num; }
	   }

	   private class IntArrayArray {
	      int     _num   = 0;
	      int[][] _array = new int[32][];

	      final void add(int[] iray) {

		 if ( _num >= _array.length ) {
		    int[][] array = new int[2 * _array.length][];
		    for (int i = 0; i < _num; i++) {
		       array[i] = _array[i];
		    }
		    _array = array;
		 }
		 _array[_num] = new int[iray.length];
		 System.arraycopy(iray, 0, _array[_num], 0, iray.length);
		 _num++;
	      }

	      final int getElement(int i, int j) { return _array[i][j]; }
	      final int getSize()                { return _num; }
	      final int getSize(int i)           { return _array[i].length; }
	   }
	}

### Diet Problem

	/* --------------------------------------------------------------------------
	 * File: Diet.java   
	 * Version 12.2  
	 * --------------------------------------------------------------------------
	 * Licensed Materials - Property of IBM
	 * 5725-A06 5725-A29 5724-Y48 5724-Y49 5724-Y54 5724-Y55
	 * Copyright IBM Corporation 2001, 2010. All Rights Reserved.
	 *
	 * US Government Users Restricted Rights - Use, duplication or
	 * disclosure restricted by GSA ADP Schedule Contract with
	 * IBM Corp.
	 * --------------------------------------------------------------------------
	 *
	 * A dietary model.
	 *
	 * Input data:
	 * foodMin[j]          minimum amount of food j to use
	 * foodMax[j]          maximum amount of food j to use 
	 * foodCost[j]         cost for one unit of food j
	 * nutrMin[i]          minimum amount of nutrient i
	 * nutrMax[i]          maximum amount of nutrient i
	 * nutrPerFood[i][j]   nutrition amount of nutrient i in food j
	 *
	 * Modeling variables:
	 * Buy[j]          amount of food j to purchase
	 *
	 * Objective:
	 * minimize sum(j) Buy[j] * foodCost[j]
	 *
	 * Constraints:
	 * forall foods i: nutrMin[i] <= sum(j) Buy[j] * nutrPer[i][j] <= nutrMax[j]
	 */

	import ilog.concert.*;
	import ilog.cplex.*;


	public class Diet {
	   static class Data {
	      int        nFoods;
	      int        nNutrs;
	      double[]   foodCost;
	      double[]   foodMin;
	      double[]   foodMax;
	      double[]   nutrMin;
	      double[]   nutrMax;
	      double[][] nutrPerFood; 
	    
	      Data(String filename) throws IloException, java.io.IOException,
					   InputDataReader.InputDataReaderException {
		 InputDataReader reader = new InputDataReader(filename);
		 
		 foodCost = reader.readDoubleArray();
		 foodMin  = reader.readDoubleArray();
		 foodMax  = reader.readDoubleArray();
		 nutrMin  = reader.readDoubleArray();
		 nutrMax  = reader.readDoubleArray();
		 nutrPerFood = reader.readDoubleArrayArray();
	       
		 nFoods = foodMax.length;
		 nNutrs = nutrMax.length;
	       
		 if ( nFoods != foodMin.length  ||
		      nFoods != foodMax.length    )
		    throw new IloException("inconsistent data in file " + filename);
		 if ( nNutrs != nutrMin.length    ||
		      nNutrs != nutrPerFood.length  )
		    throw new IloException("inconsistent data in file " + filename);
		 for (int i = 0; i < nNutrs; ++i) {
		    if ( nutrPerFood[i].length != nFoods )
		       throw new IloException("inconsistent data in file " + filename);
		 }
	      }
	   }

	   static void buildModelByRow(IloModeler    model,
				       Data          data,
				       IloNumVar[]   Buy,
				       IloNumVarType type) throws IloException {
	      int nFoods = data.nFoods;
	      int nNutrs = data.nNutrs;

	      for (int j = 0; j < nFoods; j++) {
		 Buy[j] = model.numVar(data.foodMin[j], data.foodMax[j], type);
	      }
	      model.addMinimize(model.scalProd(data.foodCost, Buy));

	      for (int i = 0; i < nNutrs; i++) {
		 model.addRange(data.nutrMin[i],
				model.scalProd(data.nutrPerFood[i], Buy),
				data.nutrMax[i]);
	      }
	   }

	   static void buildModelByColumn(IloMPModeler  model,
					  Data          data,
					  IloNumVar[]   Buy,
					  IloNumVarType type) throws IloException {
	      int nFoods = data.nFoods;
	      int nNutrs = data.nNutrs;

	      IloObjective cost       = model.addMinimize();
	      IloRange[]   constraint = new IloRange[nNutrs];
	    
	      for (int i = 0; i < nNutrs; i++) {
		 constraint[i] = model.addRange(data.nutrMin[i], data.nutrMax[i]);
	      }
	   
	      for (int j = 0; j < nFoods; j++) {
		 IloColumn col = model.column(cost, data.foodCost[j]);
		 for (int i = 0; i < nNutrs; i++) {
		    col = col.and(model.column(constraint[i], data.nutrPerFood[i][j]));
		 }
		 Buy[j] = model.numVar(col, data.foodMin[j], data.foodMax[j], type);
	      }
	   }


	   public static void main(String[] args) {

	      try {
		 String          filename  = "../../../examples/data/diet.dat";
		 boolean         byColumn  = false;
		 IloNumVarType   varType   = IloNumVarType.Float;
		
		 for (int i = 0; i < args.length; i++) {
		    if ( args[i].charAt(0) == '-') {
		       switch (args[i].charAt(1)) {
		       case 'c':
			  byColumn = true;
			  break;
		       case 'i':
			  varType = IloNumVarType.Int;
			  break;
		       default:
			  usage();
			  return;
		       }
		    }
		    else {
		       filename = args[i];
		       break;
		    }
		 }
		
		 Data data = new Data(filename);
		 int nFoods = data.nFoods;
	       
		 // Build model
		 IloCplex     cplex = new IloCplex();
		 IloNumVar[]  Buy   = new IloNumVar[nFoods];
	       
		 if ( byColumn ) buildModelByColumn(cplex, data, Buy, varType);
		 else            buildModelByRow   (cplex, data, Buy, varType);

		 // Solve model
	       
		 if ( cplex.solve() ) { 
		    System.out.println();
		    System.out.println("Solution status = " + cplex.getStatus());
		    System.out.println();
		    System.out.println(" cost = " + cplex.getObjValue());
		    for (int i = 0; i < nFoods; i++) {
		       System.out.println(" Buy" + i + " = " + cplex.getValue(Buy[i]));
		    }
		    System.out.println();
		 }
		 cplex.end();
	      }
	      catch (IloException ex) {
		 System.out.println("Concert Error: " + ex);
	      }
	      catch (InputDataReader.InputDataReaderException ex) {
		 System.out.println("Data Error: " + ex);
	      }
	      catch (java.io.IOException ex) {
		 System.out.println("IO Error: " + ex);
	      }
	   }

	   static void usage() {
	      System.out.println(" ");
	      System.out.println("usage: java Diet [options] <data file>");
	      System.out.println("options: -c  build model by column");
	      System.out.println("         -i  use integer variables");
	      System.out.println(" ");
	   }
	}

	/*  Sample output

	Solution status = Optimal

	cost   = 14.8557
	  Buy0 = 4.38525
	  Buy1 = 0
	  Buy2 = 0
	  Buy3 = 0
	  Buy4 = 0
	  Buy5 = 6.14754
	  Buy6 = 0
	  Buy7 = 3.42213
	  Buy8 = 0
	*/

### CutStock Problem

	/* --------------------------------------------------------------------------
	 * File: CutStock.java
	 * Version 12.2  
	 * --------------------------------------------------------------------------
	 * Licensed Materials - Property of IBM
	 * 5725-A06 5725-A29 5724-Y48 5724-Y49 5724-Y54 5724-Y55
	 * Copyright IBM Corporation 2001, 2010. All Rights Reserved.
	 *
	 * US Government Users Restricted Rights - Use, duplication or
	 * disclosure restricted by GSA ADP Schedule Contract with
	 * IBM Corp.
	 * --------------------------------------------------------------------------
	 */

	import ilog.concert.*;
	import ilog.cplex.*;
	import java.io.*;

	class CutStock {
	   static double RC_EPS = 1.0e-6;
	   
	   // Data of the problem
	   static double   _rollWidth;
	   static double[] _size;
	   static double[] _amount;
	   
	   static void readData(String fileName)
				 throws IOException,
					InputDataReader.InputDataReaderException {
	      InputDataReader reader = new InputDataReader(fileName);
	      
	      _rollWidth = reader.readDouble();
	      _size      = reader.readDoubleArray();
	      _amount    = reader.readDoubleArray();
	   }
	   
	   static void report1(IloCplex cutSolver, IloNumVarArray Cut, IloRange[] Fill) 
				 throws IloException {
	      System.out.println();
	      System.out.println("Using " + cutSolver.getObjValue() + " rolls");
	    
	      System.out.println();
	      for (int j = 0; j < Cut.getSize(); j++) {
		 System.out.println("  Cut" + j + " = " +
				    cutSolver.getValue(Cut.getElement(j)));
	      }
	      System.out.println();
	      
	      for (int i = 0; i < Fill.length; i++) 
		 System.out.println("  Fill" + i + " = " + cutSolver.getDual(Fill[i]));
	      System.out.println();
	   }
	   
	   static void report2(IloCplex patSolver, IloNumVar[] Use) 
				 throws IloException {
	      System.out.println();
	      System.out.println("Reduced cost is " + patSolver.getObjValue());
	      
	      System.out.println();
	      if (patSolver.getObjValue() <= -RC_EPS) {
		 for (int i = 0; i < Use.length; i++) 
		    System.out.println("  Use" + i + " = "
				       + patSolver.getValue(Use[i]));
		 System.out.println();
	      }
	   }
	   
	   static void report3(IloCplex cutSolver, IloNumVarArray Cut) 
				 throws IloException {
	      System.out.println();
	      System.out.println("Best integer solution uses " + 
				 cutSolver.getObjValue() + " rolls");
	      System.out.println();
	      for (int j = 0; j < Cut.getSize(); j++) 
		 System.out.println("  Cut" + j + " = " + 
				    cutSolver.getValue(Cut.getElement(j)));
	   }

	   static class IloNumVarArray {
	      int _num           = 0;
	      IloNumVar[] _array = new IloNumVar[32];

	      void add(IloNumVar ivar) {
		 if ( _num >= _array.length ) {
		    IloNumVar[] array = new IloNumVar[2 * _array.length];
		    System.arraycopy(_array, 0, array, 0, _num);
		    _array = array;
		 }
		 _array[_num++] = ivar;
	      }

	      IloNumVar getElement(int i) { return _array[i]; }
	      int       getSize()         { return _num; }
	   }

	   public static void main( String[] args ) {
	      String datafile = "../../../examples/data/cutstock.dat";
	      try {
		 if (args.length > 0)
		    datafile = args[0];
		 readData(datafile);
		 
		 /// CUTTING-OPTIMIZATION PROBLEM ///
	       
		 IloCplex cutSolver = new IloCplex();
	       
		 IloObjective RollsUsed = cutSolver.addMinimize();
		 IloRange[]   Fill = new IloRange[_amount.length];
		 for (int f = 0; f < _amount.length; f++ ) {
		    Fill[f] = cutSolver.addRange(_amount[f], Double.MAX_VALUE);
		 }
	       
		 IloNumVarArray Cut = new IloNumVarArray();
	       
		 int nWdth = _size.length;
		 for (int j = 0; j < nWdth; j++)
		    Cut.add(cutSolver.numVar(cutSolver.column(RollsUsed, 1.0).and(
					     cutSolver.column(Fill[j],
							      (int)(_rollWidth/_size[j]))),
					     0.0, Double.MAX_VALUE));
	       
		 cutSolver.setParam(IloCplex.IntParam.RootAlg, IloCplex.Algorithm.Primal);
	       
		 /// PATTERN-GENERATION PROBLEM ///
	       
		 IloCplex patSolver = new IloCplex();
	       
		 IloObjective ReducedCost = patSolver.addMinimize();
		 IloNumVar[] Use = patSolver.numVarArray(nWdth, 
							 0., Double.MAX_VALUE, 
							 IloNumVarType.Int);
		 patSolver.addRange(-Double.MAX_VALUE, 
				    patSolver.scalProd(_size, Use),
				    _rollWidth);
	       
		 /// COLUMN-GENERATION PROCEDURE ///
	       
		 double[] newPatt = new double[nWdth];
	       
		 /// COLUMN-GENERATION PROCEDURE ///
	       
		 for (;;) {
		    /// OPTIMIZE OVER CURRENT PATTERNS ///
		  
		    cutSolver.solve();
		    report1(cutSolver, Cut, Fill);
		  
		    /// FIND AND ADD A NEW PATTERN ///
		  
		    double[] price = cutSolver.getDuals(Fill);
		    ReducedCost.setExpr(patSolver.diff(1.,
						       patSolver.scalProd(Use, price)));
		  
		    patSolver.solve();
		    report2 (patSolver, Use);
		  
		    if ( patSolver.getObjValue() > -RC_EPS )
		       break;
		  
		    newPatt = patSolver.getValues(Use);
		    
		    IloColumn column = cutSolver.column(RollsUsed, 1.);
		    for ( int p = 0; p < newPatt.length; p++ )
		       column = column.and(cutSolver.column(Fill[p], newPatt[p]));
		    
		    Cut.add( cutSolver.numVar(column, 0., Double.MAX_VALUE) );
		 }
	       
		 for ( int i = 0; i < Cut.getSize(); i++ ) {
		    cutSolver.add(cutSolver.conversion(Cut.getElement(i),
						       IloNumVarType.Int));
		 }
	       
		 cutSolver.solve();
		 report3 (cutSolver, Cut);
	       
		 cutSolver.end();
		 patSolver.end();
	      }
	      catch ( IloException exc ) {
		 System.err.println("Concert exception '" + exc + "' caught");
	      }
	      catch (IOException exc) {
		 System.err.println("Error reading file " + datafile + ": " + exc);
	      }
	      catch (InputDataReader.InputDataReaderException exc ) {
		 System.err.println(exc);
	      }
	   }
	}


	/* Example Input file:
	115
	[25, 40, 50, 55, 70]
	[50, 36, 24, 8, 30]
	*/

### CplexServer

	/* --------------------------------------------------------------------------
	 * File: CplexServer.java
	 * Version 12.2
	 * --------------------------------------------------------------------------
	 * Licensed Materials - Property of IBM
	 * 5725-A06 5725-A29 5724-Y48 5724-Y49 5724-Y54 5724-Y55
	 * Copyright IBM Corporation 2001, 2010. All Rights Reserved.
	 *
	 * US Government Users Restricted Rights - Use, duplication or
	 * disclosure restricted by GSA ADP Schedule Contract with
	 * IBM Corp.
	 * --------------------------------------------------------------------------
	 *
	 * CplexServer.java - Entering a problem using IloCplexModeler and
	 *                    transferring it to another thread for solving
	 */

	import ilog.concert.*;
	import ilog.cplex.*;
	import java.io.*;


	public class CplexServer {

	   // define class to transfer model to server
	   static class ModelData implements Serializable {
	      private static final long serialVersionUID = 1021L;
	      IloModel    model;
	      IloNumVar[] vars;
	      ModelData(IloModel m, IloNumVar[] v)
	      {
		 model = m; 
		 vars  = v;
	      }
	   }

	   // define class to transfer back solution
	   static class SolutionData implements Serializable {
	      private static final long serialVersionUID = 1022L;
	      IloCplex.CplexStatus status;
	      double               obj;
	      double[]             vals;
	   }

	   public static void main(String[] args)
	   {
	      try {
		 // setup pipe to transfer model to server
		 PipedOutputStream mout = new PipedOutputStream();
		 PipedInputStream  min  = new PipedInputStream(mout);

		 // setup pipe to transfer results back
		 PipedOutputStream sout = new PipedOutputStream();
		 PipedInputStream  sin  = new PipedInputStream(sout);

		 // start server
		 new Server(min, sout).start();

		 // build model
		 IloNumVar[][] var = new IloNumVar[1][];
		 IloRange[][]  rng = new IloRange[1][];

		 IloCplexModeler model = new IloCplexModeler();
		 populateByRow(model, var, rng);

		 ObjectOutputStream os = new ObjectOutputStream(mout);
		 os.writeObject(new ModelData(model, var[0]));

		 ObjectInputStream  is = new ObjectInputStream(sin);
		 SolutionData sol = (SolutionData)is.readObject();

		 System.out.println("Solution status = " + sol.status);

		 if ( sol.status.equals(IloCplex.CplexStatus.Optimal) ) {
		    System.out.println("Solution value = " + sol.obj);
		    int nvars = var[0].length;
		    for (int j = 0; j < nvars; ++j)
		       System.out.println("Variable " + j + ": Value = " + sol.vals[j]);
		 }

		 // signal that we're done
		 os.writeObject(new ModelData(null, null));
	      }
	      catch (IloException e) {
		 System.err.println("Concert exception '" + e + "' caught");
	      }
	      catch (Throwable t) {
		 System.err.println("terminating due to exception " + t);
	      }
	   }


	   // The following method populates the problem with data for the
	   // following linear program:
	   //
	   //    Maximize
	   //     x1 + 2 x2 + 3 x3
	   //    Subject To
	   //     - x1 + x2 + x3 <= 20
	   //     x1 - 3 x2 + x3 <= 30
	   //    Bounds
	   //     0 <= x1 <= 40
	   //    End
	   //
	   // using the IloModeler API

	   static void populateByRow(IloModeler model,
				     IloNumVar[][] var,
				     IloRange[][] rng) throws IloException
	   {
	      double[]    lb      = {0.0, 0.0, 0.0};
	      double[]    ub      = {40.0, Double.MAX_VALUE, Double.MAX_VALUE};
	      String[]    varname = {"x1", "x2", "x3"};
	      IloNumVar[] x       = model.numVarArray(3, lb, ub, varname);
	      var[0] = x;

	      double[] objvals = {1.0, 2.0, 3.0};
	      model.addMaximize(model.scalProd(x, objvals));

	      rng[0] = new IloRange[2];
	      rng[0][0] = model.addLe(model.sum(model.prod(-1.0, x[0]),
						model.prod( 1.0, x[1]),
						model.prod( 1.0, x[2])), 20.0, "c1");
	      rng[0][1] = model.addLe(model.sum(model.prod( 1.0, x[0]),
						model.prod(-3.0, x[1]),
						model.prod( 1.0, x[2])), 30.0, "c2");
	   }


	   // The server class
	   static class Server extends Thread {
	      PipedInputStream  pin;
	      PipedOutputStream pout;

	      Server(PipedInputStream in, PipedOutputStream out)
	      {
		 pin  = in;
		 pout = out;
	      }

	      public void run()
	      {
		 try {
		    ObjectInputStream  is = new ObjectInputStream(pin);
		    ObjectOutputStream os = new ObjectOutputStream(pout);
		    while ( true ) {
		       IloCplex cplex = new IloCplex();
		       ModelData data = (ModelData)is.readObject();
		       
		       if ( data.model == null ) {
			  is.close();
			  return;
		       }

		       cplex.setModel(data.model);

		       SolutionData sol = new SolutionData();
		       if ( cplex.solve() ) {
			  sol.obj  = cplex.getObjValue();
			  sol.vals = cplex.getValues(data.vars);
		       }
		       sol.status = cplex.getCplexStatus();
		       os.writeObject(sol);

		       cplex.end();
		    }
		 }
		 catch (Throwable t) {
		    System.err.println("server terminates due to " + t);
		 }
	      }
	   }
	}

> 更多问题请参考cplex自带案例

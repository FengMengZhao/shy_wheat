---
layout: post
title: 运筹学之Dijkstra动态规划算法
categories: 技术 Java 算法
tags: 运筹学 Dijkstra 动态规划
---

	import java.util.*;
	class Vertex implements Comparable<Vertex>{
		public String name ;
		public double minDistance = Double.POSITIVE_INFINITY ;
		public Edge[] adjacencies ;
		public Vertex(String name){
			this.name = name ;
		}
		public String toString(){
			return name ;
		}
		public int compareTo(Vertex other){//必须有方法的覆写；
			return Double.compare(minDistance, other.minDistance) ;
		}
		public Vertex previous ;
	}

	class Edge{
		public Vertex target ;
		public double weight ;
		public Edge(Vertex target,double weight){
			this.target = target ;
			this.weight = weight ;
		}
	}

	public class DijkstraAlgorithmExercise {
		
		public static void computePaths(Vertex source){
			source.minDistance = 0. ;
			PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>() ;
			vertexQueue.add(source) ;
			
			while(!vertexQueue.isEmpty()){
				Vertex first = vertexQueue.poll() ;
				
				for(Edge e : first.adjacencies){
					Vertex next = e.target ;
					double weight = e.weight ;
					double distanceThroughFirst = first.minDistance + weight ;
					if(distanceThroughFirst < next.minDistance){
						vertexQueue.remove(next) ;
						next.minDistance = distanceThroughFirst ;
						next.previous = first ;
						vertexQueue.add(next) ;
					}
				}
			}
		}
		
		public static  List<Vertex> getShortestPathTo(Vertex target){ 
			List<Vertex> path = new ArrayList<Vertex>() ;
			for(Vertex vertex = target;vertex != null;vertex = vertex.previous){
				path.add(vertex) ;
			}
			Collections.reverse(path) ;
			return path ;
			
		}
		public static void main(String args[]){
			Vertex A = new Vertex ("A") ;
			Vertex B1 = new Vertex ("B1") ;
			Vertex B2 = new Vertex ("B2") ;
			Vertex C1 = new Vertex ("C1") ;
			Vertex C2 = new Vertex ("C2") ;
			Vertex C3 = new Vertex ("C3") ;
			Vertex C4 = new Vertex ("C4") ;
			Vertex D1 = new Vertex ("D1") ;
			Vertex D2 = new Vertex ("D2") ;
			Vertex D3 = new Vertex ("D3") ;
			Vertex E1 = new Vertex ("E1") ;
			Vertex E2 = new Vertex ("E2") ;
			Vertex E3 = new Vertex ("E3") ;
			Vertex F1 = new Vertex ("F1") ;
			Vertex F2 = new Vertex ("F2") ;
			Vertex G = new Vertex ("G") ;
			
			A.adjacencies = new Edge[]{ new Edge(B1,5),
						    new Edge(B2,3)};
			B1.adjacencies = new Edge[]{new Edge(A,5),
						    new Edge(C1,1),
						    new Edge(C2,3),
						    new Edge(C3,6)};
			B2.adjacencies = new Edge[]{new Edge(A,3),
						    new Edge(C2,8),
						    new Edge(C3,7),
						    new Edge(C4,6)};
			C1.adjacencies = new Edge[]{new Edge(B1,1),
				          	    new Edge(D1,6),
						    new Edge(D2,8)};
			C2.adjacencies = new Edge[]{new Edge(B1,3),
						    new Edge(B2,8),
						    new Edge(D1,3),
						    new Edge(D2,5)};
			C3.adjacencies = new Edge[]{new Edge(B1,6),
						    new Edge(B2,7),
						    new Edge(D2,3),
						    new Edge(D3,3)};
			C4.adjacencies = new Edge[]{new Edge(B2,6),
						    new Edge(D2,8),
						    new Edge(D3,4)};
			D1.adjacencies = new Edge[]{new Edge(C1,6),
						    new Edge(C2,3),
						    new Edge(E1,2),
						    new Edge(E2,2)};
			D2.adjacencies = new Edge[]{new Edge(C1,8),
						    new Edge(C2,5),
						    new Edge(C3,3),
						    new Edge(C4,8),
						    new Edge(E2,1),
						    new Edge(E3,2)};
			D3.adjacencies = new Edge[]{new Edge(C3,3),
						    new Edge(C4,4),
						    new Edge(E2,3),
						    new Edge(E3,3)};
			E1.adjacencies = new Edge[]{new Edge(D1,2),
						    new Edge(F1,3),
						    new Edge(F2,5)};
			E2.adjacencies = new Edge[]{new Edge(D1,2),
						    new Edge(D2,1),
						    new Edge(D3,3),
						    new Edge(F1,5),
						    new Edge(F2,2)};
			E3.adjacencies = new Edge[]{new Edge(D2,2),
						    new Edge(D3,3),
						    new Edge(F1,6),
						    new Edge(F2,6)};
			F1.adjacencies = new Edge[]{new Edge(E1,3),
						    new Edge(E2,5),
						    new Edge(E3,6),
						    new Edge(G,4)};
			F2.adjacencies = new Edge[]{new Edge(E1,5),
						    new Edge(E2,2),
						    new Edge(E3,6),
						    new Edge(G,3)};
			G.adjacencies = new Edge[]{ new Edge(F1,4),
						    new Edge(F2,3)};
			Vertex[] vertexs ={A,B1,B2,C1,C2,C3,C4,D1,D2,D3,E1,E2,E3,F1,F2,G} ;
			computePaths(A) ;
			
			for(Vertex v : vertexs){
				System.out.println("MinDistance to v:"+v.minDistance) ;
				List<Vertex> path = getShortestPathTo(v) ;
				System.out.println("Path:"+path) ;
			}
		}
    }

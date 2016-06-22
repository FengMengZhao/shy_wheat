---
layout: post
title: 数据结构之普通树状结构实现
categories: 技术 Java
tags: 数据结构 树
---

	class Tree{
		char elememet ;
		Tree firstChild ;
		Tree nextSibling ;
		public Tree(char c){
			this.elememet = c ;
			this.firstChild = null ;
			this.nextSibling = null ;
		}
		public String toString(){
			return ""+this.elememet ;
		}
		public void preOrder(Tree root){
			if(root != null){
				System.out.print(root+"\t") ;
				this.preOrder(root.firstChild) ;
				this.preOrder(root.nextSibling) ;
			}
		}
		public void inOrder(Tree root){
			if(root != null){
				this.inOrder(root.firstChild) ;
				System.out.print(root+"\t") ;
				this.inOrder(root.nextSibling) ;
			}
		}
		public void postOrder(Tree root){
			if(root != null){
				this.postOrder(root.firstChild) ;
				this.postOrder(root.nextSibling) ;
				System.out.print(root+"\t") ;
			}
		}
	}
	public class TreeDataStructure {
		public static void main(String args[]){
			Tree A = new Tree('A') ;
			Tree B = new Tree('B') ;
			Tree C = new Tree('C') ;
			Tree D = new Tree('D') ;
			Tree E = new Tree('E') ;
			Tree F = new Tree('F') ;
			Tree G = new Tree('G') ;
			Tree H = new Tree('H') ;
			Tree I = new Tree('I') ;
			Tree J = new Tree('J') ;
			Tree K = new Tree('K') ;
			Tree L = new Tree('L') ;
			Tree M = new Tree('M') ;
			Tree N = new Tree('N') ;
			Tree P = new Tree('P') ;
			Tree Q = new Tree('Q') ;
			Tree root = A ;
			A.firstChild = B ;
			B.nextSibling = C ;
			C.nextSibling = D ;
			D.nextSibling = E ;
			E.nextSibling = F ;
			F.nextSibling = G ;
			D.firstChild = H ;
			E.firstChild = I ;
			I.nextSibling = J ;
			J.firstChild = P ;
			P.nextSibling = Q ;
			F.firstChild = K ;
			K.nextSibling = L ;
			L.nextSibling = M ;
			G.firstChild = N ;
			root.preOrder(root) ;
			System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------") ;
			root.inOrder(root) ;
			System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------") ;
			root.postOrder(root) ;
			System.out.println("\n-----------------------------------------------------------------------------------------------------------------------------") ;
		}
	}

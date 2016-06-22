---
layout: post
title: 数据结构之java对二叉树的实现
categories: 技术 Java
tags: 数据结构 BinaryTree 
---

### 无内部类的java二叉树实现

	class OnlyNode{
		public String element ;
		public OnlyNode left ;
		public OnlyNode right ;
		public OnlyNode(String element){
			this.element = element ;
			this.left = null ;
			this.right = null ;
		}
		public void addNode(OnlyNode root,String str){//root 作为输入参数，其本身作为递归因子循环使用！！！
			if(root.element.compareTo(str) > 0){
				if(root.left == null){
					root.left = new OnlyNode(str) ;
				}else{
					this.addNode(root.left, str);
				}
			}else{
				if(root.right == null){
					root.right = new OnlyNode(str) ;
				}else{
					this.addNode(root.right, str) ;
				}
			}
		}
		public void preOrder(OnlyNode root){
			if(root != null){
				System.out.print(root.element+"\t") ;
				this.preOrder(root.left) ;
				this.preOrder(root.right) ;
			}
		}
		public void inOrder(OnlyNode root){
			if(root != null){
				this.inOrder(root.left) ;
				System.out.print(root.element+"\t") ;
				this.inOrder(root.right) ;
			}
		}
		public void postOrder(OnlyNode root){
			if(root != null){
				this.postOrder(root.left) ;
				this.postOrder(root.right) ;
				System.out.print(root.element+"\t") ;
			}
		}
	}
	public class BinaryTreeWithoutInnerNodeClass {
		public static void main(String args[]){
			OnlyNode root = new OnlyNode("D") ;
			String[] strs = {"B","F","A","C","E","G"} ;
			for(int i=0;i<strs.length;i++){
				root.addNode(root, strs[i]);
			}
			root.preOrder(root) ;
			System.out.println("\n-----------------------------------------------------") ;
			root.inOrder(root) ;
			System.out.println("\n-----------------------------------------------------") ;
			root.postOrder(root) ;
		}
	}

### 有内部类的java二叉树实现

	class BinaryTreeNode{
		class Node{
			public String element ;
			public Node left = null ;
			public Node right = null ;
			public Node(String element){
				this.element = element ;
			}
			
			public void addNode(String str){
				if(this.element.compareTo(str) > 0){
					if(this.left == null){
						this.left = new Node(str) ;
					}else{
						this.left.addNode(str);
					}
				}else{
					if(this.right == null){
						this.right = new Node(str) ;
					}else{
						this.right.addNode(str) ;
					}
				}
			}
			
			public String toString(){
				return ""+this.element ;
			}
			public void preOrder(){
				System.out.print(this.element+"\t") ;
				if(this.left != null){
					this.left.preOrder() ;
				}
				if(this.right != null){
					this.right.preOrder() ;
				}
			}
			public void inOrder(){
				if(this.left != null){
					this.left.inOrder() ;
				}
				System.out.print(this.element+"\t") ;
				if(this.right != null){
					this.right.inOrder() ; 
				}
			}
			public void postOrder(){
				if(this.left != null){
					this.left.postOrder() ;
				}
				if(this.right != null){
					this.right.postOrder() ;
				}
				System.out.print(this.element+"\t") ;
			}
		}
		public Node parent ;
		public void addBinaryTreeNode(String str){
			if(parent == null){
				parent = new Node(str) ;
			}else{
				parent.addNode(str);
			}
		}
		public void print(){
			this.parent.preOrder() ; 
			System.out.println("\n---------------------------------------------------------------") ;
			this.parent.inOrder() ;
			System.out.println("\n---------------------------------------------------------------") ;
			this.parent.postOrder() ;
		}
	}
	public class BinaryTreeDemo01 {
		public static void main(String args[]){
			BinaryTreeNode btn = new BinaryTreeNode() ;
			btn.addBinaryTreeNode("D") ;
			btn.addBinaryTreeNode("B") ;
			btn.addBinaryTreeNode("F") ;
			btn.addBinaryTreeNode("A") ;
			btn.addBinaryTreeNode("C") ;
			btn.addBinaryTreeNode("E") ;
			btn.addBinaryTreeNode("G") ;
			btn.print() ;
		}
	}

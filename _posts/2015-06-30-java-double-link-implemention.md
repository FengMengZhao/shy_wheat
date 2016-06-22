---
layout: post
title: 数据结构之java双链表实现
categories: 技术 Java
tags: 数据结构 双链表
---

    class Node{
		String element ;
		Node previous ;
		Node next ;
		public Node(String element,Node previous,Node next){
			this.element = element ;
			this.previous = previous ;
			this.next = next ;
		}public Node(String element){
			this.element = element ;
			this.previous = null ;
			this.next = null ;
		}
		public String toString(){
			if(this.previous == null && this.next == null){
				System.out.print("“"+this.element+"”"+" 节点已被删除！") ;
				return null ;
			}else{
				return this.element ;
			}
		}
		public static Node insertAfter(Node n,String str){
			Node n2 = new Node(str) ;
			n2.previous = n ;
			n2.next = n.next ;
			n.next.previous = n2 ;
			n.next = n2 ;
			return n2 ;
		}
		public static String remove(Node n){
			String temp = n.element ;
			n.next.previous = n.previous ;
			n.previous.next = n.next ;
			n.next = null ;
			n.previous = null ;
			return temp ;
		}
    }
    public class DoublyLinkedListImplements {
		public static void main(String args[]){
			Node nfront = new Node("头结点") ;
			Node nrear = new Node("尾节点") ;
			Node n1 = new Node("上海",nfront,nrear) ;
			nfront.next = n1 ;
			nrear.previous = n1 ;
			Node n2 = null ;
			n2 = Node.insertAfter(n1, "北京") ;
			Node n3 = null ;
			n3 = Node.insertAfter(n2, "广州") ;
			System.out.println(n1) ;
			System.out.println(n2) ;
			System.out.println(n3) ;
			System.out.println("-------------------------------------------------") ;
			Node.remove(n1) ;
			System.out.println(n1) ;
			System.out.println("-------------------------------------------------") ;
			Node.remove(n2) ;
			System.out.println(n2) ;
			System.out.println("-------------------------------------------------") ;
			Node.remove(n3) ;
			System.out.println(n3) ;
			System.out.println("-------------------------------------------------") ;
			Node n4 = null ;
			n4 = Node.insertAfter(nfront,"成都") ;
			System.out.println(n4) ;
		}
    }

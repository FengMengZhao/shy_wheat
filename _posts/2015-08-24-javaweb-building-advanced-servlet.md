---
layout: post
title: javaweb开发高级篇之Servlet程序开发
categories: JavaEE 笔记
tags: servlet
---

## 目录

* [1 Servlet简介](#1)

* [2 永远的Hello word，第一个Servlet程序](#2)

* [3 Servlet与表单](#3)

* [4 Servlet的生命周期](#4)

* [5 取得初始化配置信息](#5)

* [6 取得其他内置对象](#6)

	* [6.1 取得HttpSession对象](#6.1)

	* [6.2 取得ServletContext实例](#6.2)

* [7 Servlet跳转](#7)

* [8 web开发模式](#8)

	* [8.1 Model I](#8.1)

	* [8.2 Model II Model-View-Controller](#8.2)

* [9 实例操作MVC](#9)

* [10 过滤器](#10)

	* [10.1 过滤器的基本概念](#10.1)

	* [10.2 实现过滤器](#10.2)

	* [10.3 过滤器的应用](#10.3)

* [11 监听器](#11)

	* [11.1 对application的监听](#11.1)

	* [11.2 对session的监听](#11.2)

	* [11.3 对request的监听](#11.3)

	* [11.4 监听实例-在线人员统计](#11.4)

***

***

<h2 id="1"> 1 Servlet简介</h2> 

&emsp;&emsp;Servlet(服务器端小程序)是使用java语言编写的一种服务器端程序，可以向JSP一样生成动态web页。

***

***

<h2 id="2"> 2 永远的Hello word，第一个Servlet程序</h2> 

	package org.fmz.servletdemo ;

	import java.io.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class HelloServlet extends HttpServlet{
		protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		      throws ServletException, java.io.IOException{
			PrintWriter out = resp.getWriter() ;
			out.println("<html> ") ;
			out.println("<head> ") ;
			out.println("<title> ") ;
			out.println("</title> ") ;
			out.println("</head> ") ;
			out.println("<body> ") ;
			out.println("<h3> Hello World!!!</h3> ") ;
			out.println("</body> ") ;
			out.println("</html> ") ;
		}
	}

&emsp;&emsp;在web.xml文件中进行映射配置

	<servlet> 
		<servlet-name> hello</servlet-name> 
		<servlet-class> org.fmz.servletdemo.HelloServlet</servlet-class> 
	</servlet> 
	<servlet-mapping> 
		<servlet-name> hello</servlet-name> 
		<url-pattern> /helloServlet</url-pattern> 
	</servlet-mapping> 

> 实际上用户输入的地址对于Servlet来时就是一种get请求。

&emsp;&emsp;JSP程序的产生：

&emsp;&emsp;实际上从java web 的发展历史来看，最早出现的技术是Servlet技术，但是由于其输入不方便而且配置复杂，并没有得到很好的发展，后来sun公司受到微软asp技术的启发推出来JSP技术，但是JSP并不能替代Servlet技术，而是两者记性互补。

***

***

<h2 id="3"> 3 Servlet与表单</h2> 

&emsp;&emsp;定义表单-input.html

	<!doctype html> 
	<html> 
		<head> 
			<meta charset="utf-8"> 
			<title> </title> 
		</head> 
		<body> 
			<form action="inputServlet" method="post"> 
				请输入内容：<input type="text" name="info"> 
				<input type="submit" value="提交"> 
			</form> 
		</body> 
	</html> 

> 注意web.xml文件中配置的映射路径一定要与action内容提交后的映射路径一致，否则会发生错误。action的内容也可以写为：`action="<%=request.getContext()%> /inputservlet/inputServlet"`

&emsp;&emsp;接收表单的Servlet程序-InputServlet.java

	package org.fmz.servletdemo ;

	import java.io.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class InputServlet extends HttpServlet{
		public void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			try{
				String info = req.getParameter("info") ;
				PrintWriter out = resp.getWriter() ;
				out.println("<html> ") ;
				out.println("<head> <title> </title> </head> ") ;
				out.println("<body> ") ;
				out.println("<h2> "+ info +"</h2> ") ;
				out.println("</body> ") ;
				out.println("</html> ") ;

				out.close() ;
			}catch(Exception e){
				throw e ;
			}
		}

		public void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			this.doGet(req,resp) ;
		}
	}

&emsp;&emsp;配置xml文件

	<servlet> 
		<servlet-name> input</servlet-name> 
		<servlet-class> org.fmz.servletdemo.InputServlet</servlet-class> 
	</servlet> 
	<servlet-mapping> 
		<servlet-name> input</servlet-name> 
		<url-pattern> /inputservlet/inputServlet</url-pattern> 
	</servlet-mapping> 

> 注意：配置中的映射路径必须与action提交后的路径一致。

***

***

<h2 id="4"> 4 Servlet的生命周期</h2> 

&emsp;&emsp;Servlet程序是运行在服务器端的一段java程序，其生命周期将会受到web容器的限制，生命周期包括：加载程序、初始化、服务、销毁、卸载五个部分

&emsp;&emsp;配置启动选项：

	<servlet> 
		<servlet-name> life</servlet-name> 
		<servlet-class> org.fmz.servletdemo.LifeCycleServlet</servlet-class> 
		<load-on-startup> 1</load-on-startup> 
	</servlet> 

> 配置信息为：`<load-on-startup> 1</load-on-startup> `

***

***

<h2 id="5"> 5 取得初始化配置信息</h2> 

&emsp;&emsp;取得初始化参数Servlet-InitParamServlet.java

	package org.fmz.servletdemo ;

	import java.io.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class InitParamServlet extends HttpServlet{
		private String initParam = null ;
		public void init(ServletConfig config)throws ServletException{
			this.initParam = config.getInitParameter("ref") ;
		}
		public void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			System.out.println("初始化参数："+this.initParam) ;
		}
		public void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			this.doGet(req,resp) ;
		}
	}

&emsp;&emsp;配置虚拟路径：

	<servlet> 
		<servlet-name> initparam</servlet-name> 
		<servlet-class> org.fmz.servletdemo.InitParamServlet</servlet-class> 
		<init-param> 
			<param-name> ref</param-name> 
			<param-value> http:fengmengzhao.github.io</param-value> 
		</init-param> 
	</servlet> 
	<servlet-mapping> 
		<servlet-name> initparam</servlet-name> 
		<url-pattern> /initParamServlet</url-pattern> 
	</servlet-mapping> 
	<servlet-mapping> 

> 在Servlet中初始化参数中有init()和init(ServletConfig config)两种，如果同时出现则调用的是有参方法init(ServletConfig config)

***

***

<h2 id="6"> 6 取得其他内置对象</h2> 

<h3 id="6.1"> 6.1 取得HttpSession对象</h3> 

&emsp;&emsp;HttpSessionDemoServlet.java

	package org.fmz.servletdemo ;

	import java.io.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class HttpSessionDemoServlet extends HttpServlet{
		public void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			HttpSession ses = req.getSession() ;
			System.out.println("SESSION ID --> "+ses.getId()) ;
			ses.setAttribute("uname","fengmengzhao") ;
			System.out.println("uname 属性内容 --> "+ses.getAttribute("uname")) ;
		}
		public void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			this.doGet(req,resp) ;
		}
	}

<h3 id="6.2"> 6.2 取得ServletContext实例</h3> 

&emsp;&emsp;取得application对象

	package org.fmz.servletdemo ;

	import java.io.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class ServletContextDemoServlet extends HttpServlet{
		public void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			ServletContext app = super.getServletContext() ;
			System.out.println("真实路径："+app.getRealPath("/")) ;
		}
		public void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			this.doGet(req,resp) ;
		}
	}

***

***

<h2 id="7"> 7 Servlet跳转</h2> 

&emsp;&emsp;Servlet程序-ClientRedirectServlet.java(客户端跳转)

	package org.fmz.servletdemo ;

	import java.io.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class ClientRedirectServlet extends HttpServlet{
		public void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			req.getSession().setAttribute("name","冯孟昭") ;
			req.setAttribute("info","http://fengmengzhao.github.io") ;
			resp.sendRedirect("get_info.jsp") ;
		}
		public void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			this.doGet(req,resp) ;
		}
	}

&emsp;&emsp;接收信息-get_info.jsp

	<%@ page contentType="text/html" pageEncoding="utf-8"%> 

	<!doctype html> 
	<html> 
		<head> 
			<meta charset="utf-8"> 
			<title> </title> 
		</head> 
		<body> 
			<%
				request.setCharacterEncoding("utf-8") ;
			%> 
			<h2> session 属性：<%=session.getAttribute("name")%> </h2> 
			<h2> request 属性：<%=request.getAttribute("info")%> </h2> 
		</body> 
	</html> 

> 在路径配置的时候，一定要注意映射的路径一定与跳转后的路径保持一致，不然会发生错误

> 地址栏发生改变，此种跳转属于服务器端的跳转，故不能取得request对象的属性内容(只有服务器端的跳转才能够接收到)

	package org.fmz.servletdemo ;

	import java.io.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class ServerRedirectServlet extends HttpServlet{
		public void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			req.getSession().setAttribute("name","冯孟昭") ;
			req.setAttribute("info","http://fengmengzhao.github.io") ;
			RequestDispatcher rd = req.getRequestDispatcher("get_info.jsp") ;
			rd.forward(req,resp) ;
		}
		public void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			this.doGet(req,resp) ;
		}
	}

&emsp;&emsp;Servlet程序-ServerRedirectServlet.java(服务器端跳转)(重点)

	package org.fmz.servletdemo ;

	import java.io.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class ServerRedirectServlet extends HttpServlet{
		public void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			req.getSession().setAttribute("name","冯孟昭") ;
			req.setAttribute("info","http://fengmengzhao.github.io") ;
			RequestDispatcher rd = req.getRequestDispatcher("get_info.jsp") ;
			rd.forward(req,resp) ;
		}
		public void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			this.doGet(req,resp) ;
		}
	}

> 服务器端跳转可以传递request对象属性，而客户端跳转不能传递，只能传递session以及session以上的属性范围。

***

***

<h2 id="8"> 8 web开发模式</h2> 

&emsp;&emsp;在实际的web开发中存在两种模式：模式一(Model I)和模式二(Model II)

<h3 id="8.1"> 8.1 模式一</h3> 

&emsp;&emsp;Model I 就是在开发中将显示层、控制层、数据层的操作统一交给JSP或JavaBean来进行处理

&emsp;&emsp;Model I有两种方法，一种是：用户发出的请求交给JSP页面进行处理，如果是小型的web程序，为了快速与便利，将显示层(Presentation Layer) 和逻辑运算层(Business Logic Layer)都写在JSP页面中

&emsp;&emsp;此方法的优点：

&emsp;&emsp;1. 开发速度加快，只专注于JSP页面

&emsp;&emsp;2. 小幅度修改代码比较方便，直接修改JSP文件交给web容器重新编译执行即可，而JavaBean或者Servlet需要将java源文件编译成类文件，再放到web容器中才能够执行。

&emsp;&emsp;此方法的缺点：

&emsp;&emsp;1. 程序的可读性低，程序代码和网页标记混合在一起，增加维护的困难度和复杂度

&emsp;&emsp;2. 程序的可重复性低

&emsp;&emsp;Model I的另外一种方法是：将显示层都写在JSP页面中，而业务层都写成JavaBean形式，将程序代码封装成组件，这样JavaBean将负责大部分的数据处理，再讲数据处理后的结果返回至JSP页面中

&emsp;&emsp;此种做法的优点是：

&emsp;&emsp;1. 程序可读性高

&emsp;&emsp;2. 可重复性高

&emsp;&emsp;此种做法的缺点是：

&emsp;&emsp;没有流程控制，程序中的每一个JSP页面都需要检查请求参数是否正确、条件判断、异常发生时的处理，而且所有的显示操作都与具体的业务代码耦合在一起，日后的维护会非常困难。

> Model I 类似于之前的JSP+DAO开发，

<h3 id="8.2"> 8.2 Model II Model-View-Controller</h3> 

&emsp;&emsp;Model II中所有的开发都是以Servlet为主的，由Servlet接收所有客户的请求，根据请求调用相应的JavaBean，并将所有的显示结果交给JSP页面，这就是俗称的MVC模式

> IBM 推出的SmallTalk不仅仅是最早的面向对象的编程语言，还是最早应用MVC设计模式的语言，通过MVC模式可以增加代码的弹性。

&emsp;&emsp;MVC是一个设计模式，它强制性的使应用程序的输入、处理、输出分开，MVC设计模式有3个核心层，即模型层、显示层和控制层。

&emsp;&emsp;显示层(View)：接收Servlet传递的内容，并且调用JavaBean，将内容太显示给客户

&emsp;&emsp;控制层(Controller)：主要负责所有用户的请求参数，判断请求参数是否合法，根据请求内容调用JavaBean执行操作，将处理结果交给显示层处理

&emsp;&emsp;模型层(Model)：完成独立业务操作组件，一般都是JavaBean或者EJB的形式记性定义的。

> EJB(Enterprise JavaBean)是Sun公司的一种分布式技术组件，主要负责业务中心的编写

&emsp;&emsp;在MVC设计模式中，关键是使用RequestDispatcher接口，MVC的处理流程如下：

&emsp;&emsp;当用户的请求提交时，所有请求都会交给Servlet进行处理，然后由Servlet调用JavaBean，并将JavaBean的操作通过RequestDispatcher接口传递到JSP页面上。由于这些现实内容只是在一次请求-回应中有效，所以在MVC设计模式中所有的属性传递都将使用Request属性范围传递，这样可以提高代码的操作性能。

&emsp;&emsp;为什么在MVC中要使用Request属性范围传递参数？

&emsp;&emsp;回答：page属性只保存在一个页面上，跳转无效；Request在一次服务器跳转后有效，选择新的连接失败；session在一次会话中有效，用户注销后无效；application保存在服务器上，服务器关闭失效。用request属性范围，保存时间少，也就内存占用少，性能高。

> 如果某些属性要保存在一个会话中，肯定使用Session属性范围，一般都是在用户登录验证中使用。

***

***

<h2 id="9"> 9 实例操作MVC</h2> 

&emsp;&emsp;1. VO 类-Use.java

	package org.fmz.mvcdemo.vo ;

	public class User{
		private String userid ;
		private String name ;
		private String password ;
		public void setUserid(String userid){
			this.userid = userid ;
		}
		public void setName(String name){
			this.name = name ;
		}
		public void setPassword(String password){
			this.password = password ;
		}
		public String getUserid(){
			return this.userid ;
		}
		public String getName(){
			return this.name ;
		}
		public String getPassword(){
			return this.password ;
		}
	}

&emsp;&emsp;2. 数据库连接类-DatabaseConnection.java

	package org.fmz.mvcdemo.dbc ;

	import java.sql.* ;

	public class DatabaseConnection{
		private static final String DBDRIVER = "org.gjt.mm.mysql.Driver" ;
		private static final String DBURL = "jdbc:mysql://localhost:3306/mldn" ;
		private static final String DBUSER = "root" ;
		private static final String DBPASSWORD = "mysqladmin" ;
		private Connection conn = null ;

		public DatabaseConnection()throws Exception{
			try{
				Class.forName(DBDRIVER) ;
				this.conn = DriverManager.getConnection(DBURL,DBUSER,DBPASSWORD) ;
			}catch(Exception e){
				throw e ;
			}
		}

		public Connection getConnection(){
			return this.conn ;
		}

		public void close()throws Exception{
			if(this.conn != null){
				try{
					this.conn.close() ;
				}catch(Exception e){
					throw e ;
				}
			}
		}
	}

&emsp;&emsp;3. DAO接口-IUserDAO.java

	package org.fmz.mvcdemo.dao ;

	import org.fmz.mvcdemo.vo.* ;

	public interface IUserDAO{
		public boolean findLogin(User user)throws Exception ;
	}

&emsp;&emsp;4. DAO真实主题实现类-UserDAOImpl.java

	package org.fmz.mvcdemo.dao.impl ;

	import java.sql.* ;
	import org.fmz.mvcdemo.vo.* ;
	import org.fmz.mvcdemo.dao.* ;
	import org.fmz.mvcdemo.dbc.* ;

	public class UserDAOImpl implements IUserDAO{
		private Connection conn = null ;
		private PreparedStatement pstmt = null ;
		public UserDAOImpl(Connection conn){
			this.conn = conn ;
		}
		public boolean findLogin(User user)throws Exception{
			boolean flag = false ;
			try{
				String sql = "SELECT name FROM user WHERE userid=? AND password=?" ;
				this.pstmt = this.conn.prepareStatement(sql) ;
				this.pstmt.setString(1,user.getUserid()) ;
				this.pstmt.setString(2,user.getPassword()) ;
				ResultSet rs = this.pstmt.executeQuery() ;
				if(rs.next()){
					user.setName(rs.getString(1)) ;
					flag = true ;
				}
				return flag ;
			}catch(Exception e){
				throw e ;
			}finally{
				if(this.pstmt != null){
					try{
						this.pstmt.close() ;				
					}catch(Exception e){
						throw e ;
					}
				}
			}
		}
	}

&emsp;&emsp;5. DAO代理主题实现类-UserDAOProxy.java

	package org.fmz.mvcdemo.dao.proxy ;

	import java.sql.* ;
	import org.fmz.mvcdemo.vo.* ;
	import org.fmz.mvcdemo.dbc.* ;
	import org.fmz.mvcdemo.dao.* ;
	import org.fmz.mvcdemo.dao.impl.* ;

	public class UserDAOProxy implements IUserDAO{
		private DatabaseConnection dbc = null ;
		private IUserDAO dao = null ;
		public UserDAOProxy(){
			try{
				this.dbc = new DatabaseConnection() ;
			}catch(Exception e){
				e.printStackTrace() ;	
			}
			this.dao = new UserDAOImpl(dbc.getConnection()) ;
		}

		public boolean findLogin(User user)throws Exception{
			boolean flag = false ;
			try{
				flag = this.dao.findLogin(user) ;
			}catch(Exception e){
				throw e ;
			}finally{
				this.dbc.close() ;
			}
			return flag ;
		}
	}

&emsp;&emsp;6. Servlet程序-LoginServlet.java

	package org.fmz.mvcdemo.servlet ;

	import java.io.* ;
	import java.util.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;
	import org.fmz.mvcdemo.vo.* ;
	import org.fmz.mvcdemo.factory.* ;

	public class LoginServlet extends HttpServlet{
		public void doGet(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			String path = "login.jsp" ;
			String userid = req.getParameter("userid") ;
			String userpassword = req.getParameter("userpassword") ;
			List<String>  info = new ArrayList<String> () ;
			if(userid == null || "".equals(userid)){
				info.add("用户id不能为空") ;
			}
			if(userpassword == null || "".equals(userpassword)){
				info.add("用户密码不能为空") ;
			}
			if(info.size() == 0){
				User user = new User() ;
				user.setUserid(userid) ;
				user.setPassword(userpassword) ;
				try{
					if(DAOFactory.getIUserDAOInstance().findLogin(user)){
						info.add("用户登录成功，欢迎" + user.getName() + "光临！") ;
					}else{
						info.add("用户登录失败，错误的用户名或者密码！") ;
					}
				}catch(Exception e){
					e.printStackTrace() ;
				}
			}
			req.setAttribute("info",info) ;
			req.getRequestDispatcher(path).forward(req,resp) ;
		}

		public void doPost(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
			this.doGet(req,resp) ;
		}
	}

&emsp;&emsp;7. 显示层-login.jsp

	<%@ page contentType="text/html" pageEncoding="utf-8"%> 
	<%@ page import="java.util.*"%> 

	<!doctype html> 
	<html> 
		<head> 
			<meta charset="utf-8"> 
			<title> </title> 
		</head> 
		<body> 
		<%
			request.setCharacterEncoding("utf-8") ;
			List<String>  info = (List<String> )request.getAttribute("info") ;
			if(info != null){
				Iterator<String>  iter = info.iterator() ;
				while(iter.hasNext()){
		%> 
				<h4> <%=iter.next()%> </h4> 
		<%
				}
			}
		%> 
			<form action="loginServlet" action="post"> 
				用户ID：<input tpye="text" name="userid"> <br> 
				密&nbsp;码：<input type="password" name="userpassword"> <br> 
				<input type="submit" value="提交"> 
				<input type="reset" value="重置"> 
			</form> 
		</body> 
	</html> 

> 在开发中JSP文件最好只包含一下三种类型的代码：1. 接收属性，接收从Servlet传递过来的属性；2. 判断语句，判断传递到JSP中的属性是否存在；3. 输出内容，使用迭代器或者VO进行输出。

***

***

<h2 id="10"> 10 过滤器</h2> 

&emsp;&emsp;JSP可以开发完成的，Servlet都可以完成，但是Servlet具备的许多功能是JSP不具备的，Servlet分为简单的Servlet、过滤Servlet、监听Servlet三种，JSP只是能完成简单的Servlet功能。

<h3 id="10.1"> 10.1 过滤器的基本概念</h3> 

&emsp;&emsp;Filter是在Servlet2.3之后增加的内容，当需要限制用户访问某些资源或者在处理请求时提前处理某些资源时，即可使用过滤完成。

&emsp;&emsp;过滤是以一种组件的方式绑定到web应用程序中，过滤器是采用链的形式进行处理的。

<h3 id="10.2"> 10.2 实现过滤器</h3> 

&emsp;&emsp;实现过滤器需要实现javax.servlet.Filter接口，接口中的方法doFilter()是将请求继续传递

&emsp;&emsp;定义一个简单的过滤器-SimpleFilter.java

package org.fmz.filterdemo ;

import java.io.* ;
import javax.servlet.* ;

public class SimpleFilter implements Filter{
	public void init(FilterConfig filterConfig)throws ServletException{//接收初始化参数
		String initParam = filterConfig.getInitParameter("ref") ;
		System.out.println("** 过滤初始化，初始化参数：" + initParam) ;
	}

	public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws java.io.IOException,ServletException{
		System.out.println("** 执行doFilter()方法之前") ;
		chain.doFilter(request,response) ;
		System.out.println("** 执行doFilter()方法之后") ;
	}

	public void destroy(){
		System.out.println("** 过滤器销毁") ;
		try{
			Thread.sleep(3000) ;
		}catch(Exception e){
		
		}
	}
}

&emsp;&emsp;简单过滤器的web.xml文件配置

	<filter> 
		<filter-name> simple</filter-name> 
		<filter-class> org.fmz.filterdemo.SimpleFilter</filter-class> 
		<init-param> 
			<param-name> ref</param-name> 
			<param-value> http://fengmengzhao.github.io</param-value> 
		</init-param> 
	</filter> 
	<filter-mapping> 
		<filter-name> simple</filter-name> 
		<url-pattern> /jsp/*</url-pattern> 
	</filter-mapping> 

> <url-pattern> /jsp/*</url-pattern> 同普通的路径映射不同，此处的url-pattern表示只对jsp文件夹下的所有内容进行过滤，如果是/*表示对虚拟目录的所有文件进行过滤

> 过滤器中的初始化方法是在容器启动时自动加载的，并且通过FilterConfig的getInitParameter()方法取出配置中的初始化参数，只初始化一次，对于doFilter()方法实际上会调用两次，一次是在FiterChain之前，一次是在FilterChain之后。

> 一个Filter过滤器可以进行多个路径的过滤，只需要增加<filter-mapping> </filter-mapping> 中的内容即可。

<h3 id="10.3"> 10.3 过滤器的应用</h3> 

&emsp;&emsp;编码过滤-EncodingFilter.java

	package org.fmz.filterdemo ;

	import java.io.* ;
	import javax.servlet.* ;

	public class EncodingFilter implements Filter{
		private String charSet ;
		public void init(FilterConfig filterConfig)throws ServletException{//接收初始化参数
			//接收初始化参数
			this.charSet = filterConfig.getInitParameter("charset") ;
		}

		public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws java.io.IOException,ServletException{
			request.setCharacterEncoding(this.charSet) ;
			chain.doFilter(request,response) ;
		}

		public void destroy(){

		}
	}

&emsp;&emsp;配置web.xml文件

	<filter> 
		<filter-name> encoding</filter-name> 
		<filter-class> org.fmz.filterdemo.EncodingFilter</filter-class> 
		<init-param> 
			<param-name> charset</param-name> 
			<param-value> utf-8</param-value> 
		</init-param> 
	</filter> 
	<filter-mapping> 
		<filter-name> encoding</filter-name> 
		<url-pattern> /*</url-pattern> 
	</filter-mapping> 

> 这样所有的页面都执行了页面编码设置：`request.setCharacterEncoding("utf-8")`

&emsp;&emsp;登录验证-LoginFilter.java

	package org.fmz.filterdemo ;

	import java.io.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class LoginFilter implements Filter{
		private String charSet ;
		public void init(FilterConfig filterConfig)throws ServletException{//接收初始化参数
			
		}

		public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain)throws java.io.IOException,ServletException{
			//session属于Http范围协议
			HttpServletRequest req = (HttpServletRequest) request ;
			HttpSession session = req.getSession() ;
			if(session.getAttribute("userid") != null){//已经登录，可以进行访问
				chain.doFilter(request,response) ;
			}else{
				request.getRequestDispatcher("login.jsp").forward(request,response) ;//不能进行访问，进行页面跳转
			}
		}

		public void destroy(){

		}
	}

> 本程序首先通过HttpServlet取得当前的session，然后判断session范围内是否存在userid属性，如果存在表示用户应经登录，如果不存在，则跳转到login.jsp页面中。

&emsp;&emsp;登录JSP文件-Login.jsp

	<%@ page contentType="text/html" pageEncoding="utf-8"%> 
	<!doctype html> 
	<html> 
		<head> 
			<meta charset="utf-8"> 
			<title> </title> 
		</head> 
		<body> 
			<form action="login.jsp" method="post"> 
				用户名：<input type="text" name="uname"> <br> 
				密&nbsp;码：<input type="password" name="upass"> <br> 
				<input type="submit" value="登录"> 
				<input type="reset" value="重置"> 
			</form> 
			<%
				String name = request.getParameter("uname") ;
				String password = request.getParameter("upass") ;
				if(!(name == null || password == null)){
					if(name.equals("fmz") && password.equals("799520")){
						response.setHeader("refresh","2;URL=welcome.jsp") ;
						session.setAttribute("userid",name) ;
			%> 
						<h3> 用户登录成功，两秒后跳转到欢迎页！</h3> 
						<h3> 如果没有跳转，请按<a href="welcome.jsp"> 这里</a> </h3> 
			<%
					}else{
			%> 
							<h3> 错误的用户名或密码！</h3> 
			<%
					}
				}
			%> 
		</body> 
	</html> 

&emsp;&emsp;欢迎页-welcome.jsp

	<%@ page contentType="text/html" pageEncoding="utf-8"%> 
	<!doctype html> 
	<html> 
		<head> 
			<meta charset="utf-8"> 
			<title> </title> 
		</head> 
		<body> 
			<h3> 欢迎<%=session.getAttribute("userid")%> 光临本系统！</h3> 
		</body> 
	</html> 

***

***

<h2 id="11"> 11 监听器</h2> 

&emsp;&emsp;第三种Servlet程序称为监听Servlet，组要负责监听web的各种操作，当相关的事件触发后会产生事件，并对此事件进行处理。web可以对application、session、request三种属性进行监听。

<h3 id="11.1"> 11.1 对application的监听</h3> 

&emsp;&emsp;Servlet上下文状态监听-ServletContextListenerDmeo.java

	package org.fmz.listenerdemo ;

	import javax.servlet.* ;

	public class ServletContextListenerDemo implements ServletContextListener{
		public void contextInitialized(ServletContextEvent event){
			System.out.println("** 容器初始化：" + event.getServletContext().getContextPath()) ;
		}
		public void contextDestroyed(ServletContextEvent event){
			System.out.println("** 容器销毁：" + event.getServletContext().getContextPath()) ;
		}
	}

> Servlet程序都必须在web.xml文件中进行配置，如果要配置简单Servlet、监听器、过滤器应该先编写过滤器：`<filter> <filter-name> </filter-name> <filter-class> </filter-class> <filter> <filter-mapping> <filter-name> </filter-name> <url-pattern> </url-pattern> </filter-mapping> `，然后监听器：`<listener> <listener-class> </listener-class> </listener> `，最后简单的Servlet

&emsp;&emsp;上下文属性监听-ServletContextAttributeListenerDemo.java

	package org.fmz.listenerdemo ;

	import javax.servlet.* ;

	public class ServletContextAttributeListenerDemo implements ServletContextAttributeListener{
		public void attributeAdded(ServletContextAttributeEvent event){
			System.out.println("增加属性 -->  属性名称：" + event.getName() + "，属性内容：" + event.getValue()) ;
		}
		public void attributeRemoved(ServletContextAttributeEvent event){
			System.out.println("删除属性 -->  属性名称：" + event.getName() + "，属性内容：" + event.getValue()) ;
		}
		public void attributeReplaced(ServletContextAttributeEvent event){
			System.out.println("替换属性 -->  属性名称：" + event.getName() + "，属性内容：" + event.getValue()) ;
		}
	}

<h3 id="11.2"> 11.2 对session的监听</h3> 

&emsp;&emsp;session监听-HttpSessionListenerDemo.java

	package org.fmz.listenerdemo ;

	import javax.servlet.http.* ;

	public class HttpSessionListenerDemo implements HttpSessionListener{
		public void sessionCreated(HttpSessionEvent ses){
			System.out.println("** SESSIO创建 -->  SESSION ID：" + ses.getSession().getId()) ;
		}
		public void sessionDestroyed(HttpSessionEvent ses){
			System.out.println("** SESSIO销毁 -->  SESSION ID：" + ses.getSession().getId()) ;
		}
	}

> 当一个新用户打开一个动态页面时，服务器会为新用户分配session，并且触发HttpSessionListener接口中的sessionCreate()事件，但是用户的销毁却又两种不同的方法触发sessionDestroyed()事件，方法一：调用HttpSession接口中的invalidate()方法，让一个session失败；方法二：超过了配置的session配置时间，超过时间可以直接在web.xml文件中进行配置，`<session-config> <session-timeout> 2</session-timeout> </session-config> `表示一个session在两分钟内没有与服务器进行任何交互操作的话，那么服务器会认为此用户已经离开，会将其自动注销。默认注销时间是30分钟。

&emsp;&emsp;对session属性的监听有两种途径，一种是实现HttpSessionAttributeListener接口，通对application属性的监听相似；另外一种是实现HttpSessionBindListener接口，此接口实现监听程序可以不用配置而直接使用

&emsp;&emsp;实现HttpSessionBindListener接口-LoginUser.java

	package org.fmz.listenerdemo ; 

	import javax.servlet.http.* ;

	public class LoginUser implements HttpSessionBindingListener{
		private String name ;
		public LoginUser(String name){
			this.name = name ;
		}
		public void valueBound(HttpSessionBindingEvent event){//在session中绑定
			System.out.println("** 在session中保存LoginUser对象，name= " + this.getName() + "，SESSION ID= " + event.getSession().getId()) ;
		}
		public void valueUnbound(HttpSessionBindingEvent event){
			System.out.println("** 在session中移出LoginUser对象，name= " + this.getName() + "，SESSION ID= " + event.getSession().getId()) ;
		}
		public String getName(){
			return this.name ;
		}
		public void setName(String name){
			this.name = name ;
		}
	}

> 由于此类实现了HttpSessionBinding接口，所以一旦session增加或者删除本类对象时就会自动触发valueBound()或者valueUnbound()方法操作。

&emsp;&emsp;测试session属性的增减-session_bound.jsp

	<%@ page contentType="text/html" pageEncoding="utf-8"%> 
	<%@ page import="org.fmz.listenerdemo.*"%> 
	<%
		LoginUser user = new LoginUser("fmz") ;
		session.setAttribute("info",user) ;
	%> 

&emsp;&emsp;测试session属性的删除-session_unbound.jsp

	<%@ page contentType="text/html" pageEncoding="utf-8"%> 
	<%@ page import="org.fmz.listenerdemo.*"%> 
	<%
		session.removeAttribute("info") ;
	%> 

<h3 id="11.3"> 11.3 对request的监听</h3> 

&emsp;&emsp;对request的监听主要使用ServletRequestListener接口和ServletRequestListener接口

<h3 id="11.4"> 11.4 监听实例-在线人员统计</h3> 

&emsp;&emsp;要完成在线用户列表的监听器，需要使用如下3个接口：

&emsp;&emsp;1. ServletContextListener接口，在上下文初始化时设置一个空的集合到application中

&emsp;&emsp;2. HttpSessionAttributeListener接口，用户增加session属性时，表示新用户登录，从session中取出此用户的登录名，并将之保存在列表中。

&emsp;&emsp;3. HttpSessionListener接口，当用户注销(手工注销或者会话超时)将此用户从列中删除。

&emsp;&emsp;在线用户监听-OnlineUser.java

	package org.fmz.listenerdemo ;

	import java.util.* ;
	import javax.servlet.* ;
	import javax.servlet.http.* ;

	public class OnlineUserList implements HttpSessionAttributeListener,HttpSessionListener,ServletContextListener{
		private ServletContext app = null ;//用于application的属性操作

		public void contextInitialized(ServletContextEvent sce){//上下文初始化
			this.app = sce.getServletContext() ;
			this.app.setAttribute("online",new TreeSet()) ;
		}

		public void contextDestroyed(ServletContextEvent sce){
		
		}

		public void sessionCreated(HttpSessionEvent se){
		
		}
		public void sessionDestroyed(HttpSessionEvent se){
			Set list = (Set)this.app.getAttribute("online") ;
			list.remove(se.getSession().getAttribute("userid")) ;
			this.app.setAttribute("online",list) ;
		}

		public void attributeAdded(HttpSessionBindingEvent event){
			Set list = (Set)this.app.getAttribute("online") ;
			list.add(event.getValue()) ;
			this.app.setAttribute("online",list) ;
		}

		public void attributeRemoved(HttpSessionBindingEvent event){
			Set list = (Set)this.app.getAttribute("online") ;
			list.remove(event.getValue()) ;
			this.app.setAttribute("online",list) ;
		}

		public void attributeReplaced(HttpSessionBindingEvent event){
		
		}
	}

&emsp;&emsp;登录页-login.jsp

	<%@ page contentType="text/html" pageEncoding="utf-8"%> 
	<%@ page import="java.util.*"%> 

	<!doctype html> 
	<html> 
		<head> 
			<meta charset="utf-8"> 
			<title> </title> 
		</head> 
		<body> 
			<form action="login.jsp" method="post"> 
				用户ID：<input type="text" name="userid"> 
				<input type="submit" value="登录"> 
			</form> 
			<%
				String userid = request.getParameter("userid") ;
				if(!(userid == null || "".equals(userid))){
					session.setAttribute("userid",userid) ;
					response.sendRedirect("list.jsp") ;
				}
			%> 
		</body> 
	</html> 

&emsp;&emsp;显示在线用户-list.jsp

	<%@ page contentType="text/html" pageEncoding="utf-8"%> 
	<%@ page import="java.util.*"%> 
	<!doctype html> 
	<html> 
		<head> 
			<meta charset="utf-8"> 
			<title> </title> 
		</head> 
		<body> 
			<%
				Set list = (Set)this.getServletContext().getAttribute("online") ;
				Iterator iter = list.iterator() ;
				while(iter.hasNext()){
			%> 
					<%=iter.next()%> <br> 
			<%
				}
			%> 
		</body> 
	</html> 

***

***

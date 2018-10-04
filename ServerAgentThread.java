package chineseChess;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*; // 以上四个为图形化用户的包

import java.net.*; //网络
import java.io.*; // 传输

public class ServerAgentThread extends Thread{
	Server father;
	Socket sc; // 和客户端一一对应
	DataInputStream din; // 声明数据输入流与输出流的引用
	DataOutputStream dout;
	boolean flag = true; // 控制线程的标志位
	
	public ServerAgentThread (Server father, Socket sc) {
		this.father = father;
		this.sc = sc;
		
		try {
			din = new DataInputStream(sc.getInputStream()); // 创建数据输入流
			dout = new DataOutputStream(sc.getOutputStream());
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void run() {
		while(flag) {
			try {
				String msg = din.readUTF().trim(); // 接收客户端传来消息
				if (msg.startsWith("<#NICK_NAME#>")) { // 收到新用户的信息
					this.nick_name(msg);
				} else if (msg.startsWith("<#CLIENT_LEAVE#>")){ // 收到用户离开信息
					this.client_leave(msg);
				} else if (msg.startsWith("<#CHALLENGE#>")){ // 收到用户发出挑战信息
					this.challenge(msg);
				} else if (msg.startsWith("<#AGREE#>")){ //收到接受挑战信息
					this.agree(msg);
				} else if (msg.startsWith("<#DISAGREE#>")){ //收到拒绝挑战信息
					this.disagree(msg);
				} else if (msg.startsWith("<#BUSY#>")){  //收到被挑战者忙的信息
					this.busy(msg);
				} else if (msg.startsWith("<#MOVE#>")){  //收到走棋信息
					this.move(msg);
				} else if (msg.startsWith("<#SURRENDER#>")){ //收到用户认输信息
					this.surrender(msg); 
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	

	public void nick_name(String msg) {
		try {
			String name = msg.substring(13); // 获得用户昵称
			this.setName(name); // 用昵称给线程取名
			Vector v = father.onlineList; // 获得在线用户列表
			boolean duplicateName = false;
			int size = v.size(); // 获得用户列表大小
			for(int i=0;i<size;i++) {
				ServerAgentThread temSat = (ServerAgentThread) v.get(i);
				if (temSat.getName().equals(name)) {
					duplicateName = true;
					break;
				}
			}
			if (duplicateName==true) {
				dout.writeUTF("<#NAME_DUPLICATE#>"); // 将重名信息发给客户端
				din.close();
				dout.close(); // 关闭数据流
				sc.close();   // 关闭socket
				flag = false; // 终止该服务器代理线程
			} else {
				v.add(this); // 将该线程加入在线列表
				father.refreshList(); //刷新服务器在线信息列表
				String nickListMsg = "";
				size = v.size(); //获得在线列表大小
				for (int i=0;i<size;i++) {
					ServerAgentThread temSat = (ServerAgentThread) v.get(i);
					nickListMsg = nickListMsg + "|" + temSat.getName(); // 将在线列表内容组织成字符串
					
				}
				nickListMsg = "<#NICK_LIST#>" + nickListMsg;
				Vector tempv = father.onlineList;
				size = tempv.size();
				for (int i = 0; i<size;i++) { // 此时是新的在线列表
					ServerAgentThread satTemp = (ServerAgentThread) tempv.get(i);
					satTemp.dout.writeUTF(nickListMsg); // 将最新的客户端列表发送
					if (satTemp != this) {
						satTemp.dout.writeUTF("<#MSG#>" + this.getName()+"上线了");
					}
					
				}
						
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void client_leave(String msg) {
		try {
			Vector tempv = father.onlineList;
			tempv.remove(this);
			int size = tempv.size();
			String n1 = "<#NICK_LIST#>";
			for (int i=0;i<size;i++) {
				ServerAgentThread satTemp = (ServerAgentThread) tempv.get(i);
				satTemp.dout.writeUTF("<#MSG#>"+this.getName()+"离线了"); //向各个客户端发送离线信息
				n1 = n1 + "|" + satTemp.getName(); // 组织信息的在线用户列表
			}
			
			for (int i=0;i<size;i++) { // 将最新列表信息发给各个客户端
				ServerAgentThread satTemp = (ServerAgentThread) tempv.get(i);
				satTemp.dout.writeUTF(n1); 
			}
			this.flag = false; // 终止改服务器代理线程
			father.refreshList(); // 更新在线用户列表
			
			
			
		} catch (Exception e) {
			
		}
	}
	
	public void challenge(String msg) {
		try {
			String name1 = this.getName();// 发出挑战用户名字
			String name2 = msg.substring(13); // 获得被挑战的用户
 			Vector v = father.onlineList;
 			int size = v.size();
 			for (int i=0;i<size;i++) {
 				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
 				if (satTemp.getName().equals(name2)) {
 					satTemp.dout.writeUTF("<#CHALLENGE#>"+name1);
 					break;
 				}
 			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void agree(String msg) {
		try {
			
			String name = msg.substring(9); // 获得提出挑战的用户名字
 			Vector v = father.onlineList;
 			int size = v.size();
 			for (int i=0;i<size;i++) {
 				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
 				if (satTemp.getName().equals(name)) {
 					satTemp.dout.writeUTF("<#AGREE#>");
 					break;
 				}
 			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void disagree(String msg) {
		try {
			
			String name = msg.substring(12); // 获得提出挑战的用户名字
 			Vector v = father.onlineList;
 			int size = v.size();
 			for (int i=0;i<size;i++) {
 				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
 				if (satTemp.getName().equals(name)) {
 					satTemp.dout.writeUTF("<#DISAGREE#>"+name);
 					break;
 				}
 			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void busy(String msg) {
		try {
			
			String name = msg.substring(8); // 获得提出挑战的用户名字
 			Vector v = father.onlineList;
 			int size = v.size();
 			for (int i=0;i<size;i++) {
 				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
 				if (satTemp.getName().equals(name)) {
 					satTemp.dout.writeUTF("<#BUSY#>");
 					break;
 				}
 			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void move(String msg) {
		try {
			String name = msg.substring(8,msg.length()-4); // 获得接收方名字
 			Vector v = father.onlineList;
 			int size = v.size();
 			for (int i=0;i<size;i++) {
 				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
 				if (satTemp.getName().equals(name)) {
 					satTemp.dout.writeUTF(msg);
 					break;
 				}
 			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void surrender(String msg) {
		try {
			
			String name = msg.substring(13); // 获得接收方名字
 			Vector v = father.onlineList;
 			int size = v.size();
 			for (int i=0;i<size;i++) {
 				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
 				if (satTemp.getName().equals(name)) {
 					satTemp.dout.writeUTF(msg);
 					break;
 				}
 			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

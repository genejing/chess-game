package chineseChess;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*; // 以上四个为图形化用户的包
import java.net.*; //网络
import java.io.*; // 传输


public class ServerThread extends Thread{
	Server father; // 声明server的引用
	ServerSocket ss; // 声明serversocket的引用 服务器和客户端连接时将信息要发送的客户端标识
	boolean flag = true;  // 线程生存与否，控制线程的标志位
	public ServerThread(Server father) {
		this.father = father;
		ss = father.ss;
	}
	
	public void run() {
		while(flag) {
			try {
				Socket sc = ss.accept(); // 等待客户端连接
				ServerAgentThread sat = new ServerAgentThread(father, sc);
				sat.start();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	
	
	
	
}

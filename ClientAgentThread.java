package chineseChess;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.print.DocFlavor.STRING;
import javax.swing.*;
import javax.swing.event.*; // 以上四个为图形化用户的包
import java.net.*; //网络
import java.io.*; // 传输

public class ClientAgentThread extends Thread{
	Client father;
	boolean flag = true; // 线程标志位
	DataInputStream din;
	DataOutputStream dout;
	String challenger = null; // 用于记录正在挑战的对手
	public ClientAgentThread(Client father){
		this.father = father;
		try {
			din = new DataInputStream(father.sc.getInputStream());
			dout = new DataOutputStream(father.sc.getOutputStream());
			
			String name = father.jtfNickName.getText().trim();
			dout.writeUTF("<#NICK_NAME#>" + name); //发送昵称给服务器
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void run() {
		while(flag) {
			try {
				String msg = din.readUTF().trim(); // 接收服务器端传来消息
				if (msg.startsWith("<#NAME_DUPLICATE#>")) { // 收到重名的信息
					this.name_duplicate();
				} else if (msg.startsWith("<#NICK_LIST#>")){ // 收到昵称列表
					this.nick_list(msg);
				} else if (msg.startsWith("<#SERVER_DOWN#>")){ // 收到服务器离开信息
					this.server_down();
				} else if (msg.startsWith("<#CHALLENGE#>")){ //收到挑战信息
					this.challenge(msg);
				} else if (msg.startsWith("<#AGREE#>")){ //当该用户收到对方接受挑战的信息
					this.agree();
				} else if (msg.startsWith("<#DISAGREE#>")){ //当该用户收到对方拒绝挑战信息
					this.disagree();
				} else if (msg.startsWith("<#BUSY#>")){  //收到对方忙的信息
					this.busy();
				} else if (msg.startsWith("<#MOVE#>")){  //收到走棋信息
					this.move(msg);
				} else if (msg.startsWith("<#SURRENDER#>")){ //收到用户认输信息
					this.surrender(); 
				} else if (msg.startsWith("<#MSG#>")) {
					this.checkIn(msg);
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void checkIn(String msg) {
		try {
			String name = msg.substring(7);
			JOptionPane.showMessageDialog(this.father, name,"提示",JOptionPane.INFORMATION_MESSAGE);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void name_duplicate() {
		try {
			// 给出重名的提示信息
			JOptionPane.showMessageDialog(this.father, "重名了","错误",JOptionPane.ERROR_MESSAGE);
			din.close();
			dout.close();
			this.father.jtfHost.setEnabled(true);
			this.father.jtfPort.setEnabled(true);
			this.father.jtfNickName.setEnabled(true);
			this.father.jbConnect.setEnabled(true);
			this.father.jbDisconnect.setEnabled(false);
			this.father.jbChallenge.setEnabled(false);
			this.father.jbYChallenge.setEnabled(false);
			this.father.jbNChallenge.setEnabled(false);
			this.father.jbFail.setEnabled(false);
			father.sc.close();
			father.sc = null;
			father.cat = null;
			flag = false;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void nick_list(String msg) {
		String s = msg.substring(13);
		String[] name = s.split("\\|");
		Vector v = new Vector();
		for (int i=0;i<name.length;i++) {
			if (name[i].trim().length() != 0 && (!name[i].trim().equals(father.jtfNickName.getText().trim()))) {
				v.add(name[i]);
			}
		}
		father.jcbNickList.setModel(new DefaultComboBoxModel(v)); // 设置下拉列表
	}
	
	public void server_down() {
		this.father.jtfHost.setEnabled(true);
		this.father.jtfPort.setEnabled(true);
		this.father.jtfNickName.setEnabled(true);
		this.father.jbConnect.setEnabled(true);
		this.father.jbDisconnect.setEnabled(false);
		this.father.jbChallenge.setEnabled(false);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(false);
		this.flag = false;
		father.cat = null;
		JOptionPane.showMessageDialog(this.father, "服务器停止！","提示",JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	public void challenge(String msg) {
		try {
			String name = msg.substring(13);
			if (this.challenger == null) {
				challenger = msg.substring(13);
				this.father.jtfHost.setEnabled(false);
				this.father.jtfPort.setEnabled(false);
				this.father.jtfNickName.setEnabled(false);
				this.father.jbConnect.setEnabled(false);
				this.father.jbDisconnect.setEnabled(false);
				this.father.jbChallenge.setEnabled(false);
				this.father.jbYChallenge.setEnabled(true);
				this.father.jbNChallenge.setEnabled(true);
				this.father.jbFail.setEnabled(false);
				JOptionPane.showMessageDialog(this.father, challenger+"向你挑战！","提示",JOptionPane.INFORMATION_MESSAGE);
				
			} else {
				this.dout.writeUTF("<#BUSY#>" + name); // 如果该玩家忙碌，则返回一个<#BUSY#>开头信息
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void agree() {
		this.father.next();
		this.father.canMove = true;
		this.father.jtfHost.setEnabled(false);
		this.father.jtfPort.setEnabled(false);
		this.father.jtfNickName.setEnabled(false);
		this.father.jbConnect.setEnabled(false);
		this.father.jbDisconnect.setEnabled(false);
		this.father.jbChallenge.setEnabled(false);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(true);
		JOptionPane.showMessageDialog(this.father, "对方接受你的挑战，你走红棋（先手）！","提示",JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void disagree() {
		this.father.color = 0;
		this.father.canMove = false;
		this.father.jtfHost.setEnabled(false);
		this.father.jtfPort.setEnabled(false);
		this.father.jtfNickName.setEnabled(false);
		this.father.jbConnect.setEnabled(false);
		this.father.jbDisconnect.setEnabled(true);
		this.father.jbChallenge.setEnabled(true);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(false);
		JOptionPane.showMessageDialog(this.father, "对方拒绝你的挑战！","提示",JOptionPane.INFORMATION_MESSAGE);
		this.challenger = null;
	}
	
	public void busy() {
		this.father.color = 0;
		this.father.canMove = false;
		this.father.jtfHost.setEnabled(false);
		this.father.jtfPort.setEnabled(false);
		this.father.jtfNickName.setEnabled(false);
		this.father.jbConnect.setEnabled(false);
		this.father.jbDisconnect.setEnabled(true);
		this.father.jbChallenge.setEnabled(true);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(false);
		JOptionPane.showMessageDialog(this.father, "对方忙碌中","提示",JOptionPane.INFORMATION_MESSAGE);
		this.challenger = null;
	}
	
	public void move(String msg) {
		int length = msg.length();
		int startI = Integer.parseInt(msg.substring(length-4,length-3)); //获得棋子原始位置
		int startJ = Integer.parseInt(msg.substring(length-3,length-2));
		int endI = Integer.parseInt(msg.substring(length-2,length-1)); //获得棋子走后位置
		int endJ = Integer.parseInt(msg.substring(length-1));
		this.father.jpz.move(startI, startJ,endI,endJ); //调用方法走棋
		this.father.canMove = true;
		
	}
	
	public void surrender() {
		JOptionPane.showMessageDialog(this.father, "对方认输，你获胜！","提示",JOptionPane.INFORMATION_MESSAGE);
		this.challenger = null;
		this.father.color = 0;
		this.father.canMove = false;
		this.father.next(); // 进入下一局
		this.father.jtfHost.setEnabled(false);
		this.father.jtfPort.setEnabled(false);
		this.father.jtfNickName.setEnabled(false);
		this.father.jbConnect.setEnabled(false);
		this.father.jbDisconnect.setEnabled(true);
		this.father.jbChallenge.setEnabled(true);
		this.father.jbYChallenge.setEnabled(false);
		this.father.jbNChallenge.setEnabled(false);
		this.father.jbFail.setEnabled(false);
	}
	
	
	
}

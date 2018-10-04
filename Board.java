package chineseChess;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*; // 以上四个为图形化用户的包

public class Board extends JPanel implements MouseListener{
	private int width; //棋盘两线之间距离
	boolean focus = false; //棋子的状态
	int jiang1_i = 4; // 帅的x坐标
	int jiang1_j = 0; //帅的y坐标
	int jiang2_i = 4; //将的x坐标
	int jiang2_j = 9; //将的y坐标
	int startI = -1; //棋子的开始位置
	int startJ = -1; 
	int endI = -1; // 棋子的终止位置
	int endJ = -1;
	public Chessman chessman[][];
	Client client = null; // 哪一个客户端
	Rule rule;
	
	public Board(Chessman chessman[][], int width, Client client) {
		this.client = client;
		this.chessman = chessman;
		this.width = width;
		rule = new Rule(chessman);
		this.addMouseListener(this);
		this.setBounds(0,0,700,700);
		this.setLayout(null);  //用空布局常见： 1.游戏 2.图片多
	}
	
	public void paint(Graphics g1) {
		Graphics2D g = (Graphics2D) g1; 
		//打开抗锯齿
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Color c = g.getColor();
		g.setColor(Client.bgColor);
		g.fill3DRect(60, 30, 580, 630, false);
		g.setColor(Color.black);
		for (int i=80;i<=620;i=i+60) { //绘制棋盘中横线
			g.drawLine(110, i, 590, i);
		}
		g.drawLine(110, 80, 110, 620); //左边线
		g.drawLine(590, 80, 590, 620); //右边线
		for (int i=170;i<=530;i=i+60) { //绘制中间竖线
			g.drawLine(i, 80, i, 320);
			g.drawLine(i, 380, i, 620);
		}
		g.drawLine(290, 80, 410, 200); //绘制两边的斜线
		g.drawLine(290, 200, 410, 80);
		g.drawLine(290, 500, 410, 620);
		g.drawLine(290, 620, 410, 500);
		this.smallLine(g,1,2); //绘制红炮所在位置的线
		this.smallLine(g,7,2); //绘制红炮所在位置的线
		this.smallLine(g,0,3); //绘制兵所在位置的线
		this.smallLine(g,2,3); //绘制兵所在位置的线
		this.smallLine(g,4,3); //绘制兵所在位置的线
		this.smallLine(g,6,3); //绘制兵所在位置的线
		this.smallLine(g,8,3); //绘制兵所在位置的线
		this.smallLine(g,0,6); //绘制卒所在位置的线
		this.smallLine(g,2,6); //绘制卒所在位置的线
		this.smallLine(g,4,6); //绘制卒所在位置的线
		this.smallLine(g,6,6); //绘制卒所在位置的线
		this.smallLine(g,8,6); //绘制卒所在位置的线
		this.smallLine(g,1,7); //绘制白炮所在位置的线
		this.smallLine(g,7,7); //绘制白炮所在位置的线
		g.setColor(Color.black);
		Font font1 =new Font("宋体",Font.BOLD, 50);
		g.setFont(font1);
		g.drawString("楚河", 170, 365);
		g.drawString("汉界", 400, 365);
		Font font =new Font("宋体",Font.BOLD, 30);
		g.setFont(font);
		for (int i=0;i<9;i++) {
			for (int j=0;j<10;j++) {
				if (chessman[i][j] != null) {
					if (this.chessman[i][j].getFocus() == true) { //被选中
						g.setColor(Client.focusbg);
						g.fillOval(110+i*60-25, 80+j*60-25, 50, 50); // 画棋子
						g.setColor(Client.focusChar);
					} else {
						g.fillOval(110+i*60-25, 80+j*60-25, 50, 50); // 画棋子
						g.setColor(chessman[i][j].getColor());
					}
					g.drawString(chessman[i][j].getName(), 110+i*60-15, 80+j*60+10);
					g.setColor(Color.black);
				}
			}
		}
		g.setColor(c);
		
	}
	
	public void mouseClicked(MouseEvent e) {
		if (this.client.canMove == true) {
			int  i=-1,j=-1;
			int[] pos = getPos(e);
			i = pos[0];
			j = pos[1];
			if (i >= 0 && i<=8 && j>=0 && j<=9) { //在棋盘范围内
				if(focus == false) { //有没有棋子被选
					this.noFocus(i,j);
				} else {
					if (chessman[i][j] != null) {
						if (chessman[i][j].getColor() == chessman[startI][startJ].getColor()) {
							// 如果是自己的棋子
							chessman[startI][startJ].setFocus(false);
							chessman[i][j].setFocus(true);
							startI = i;
							startJ = j;
						} else {
							endI = i; // 保存要移到的位置
							endJ = j;
							String name = chessman[startI][startJ].getName();
							boolean canMove = rule.canMove(startI,startJ,endI,endJ,name);
							if (canMove) {
								try {
									this.client.cat.dout.writeUTF("<#MOVE#>"+this.client.cat.challenger+startI+startJ+endI+endJ);
									
									this.client.canMove = false;
									if (chessman[endI][endJ].getName().equals("帥") ||chessman[endI][endJ].getName().equals("將")){
										this.success();
									} else {
										this.noJiang();
									}
								} catch (Exception ee) {
									ee.printStackTrace();
								}
							}
						}
					} else { // 如果没有棋子
						endI = i; //保存终点
						endJ = j; 
						String name = chessman[startI][startJ].getName();
						boolean canMove = rule.canMove(startI,startJ,endI,endJ,name);
						if (canMove) {
							this.noChessman();
						}
					}
					
				}
			} 
			this.client.repaint();
		}
	}
	
	public int[] getPos(MouseEvent e) {
		int[] pos = new int[2];
		pos[0] = -1;
		pos[1] = -1;
		Point p = e.getPoint();
		double x = p.getX();
		double y = p.getY();
		if(Math.abs((x-110)/1%60)<=25){ // 获得对应于数组x下标的位置
			pos[0]=Math.round((float)(x-110))/60;
		}
		else if(Math.abs((x-110)/1%60)>=35){
			pos[0]=Math.round((float)(x-110))/60+1;
		}
		if(Math.abs((y-80)/1%60)<=25){ // 获得对应于数组y下标的位置
			pos[1]=Math.round((float)(y-80))/60;
		}
		else if(Math.abs((y-80)/1%60)>=35){
			pos[1]=Math.round((float)(y-80))/60+1;
		}
		return pos;
	}
	
	public void noFocus(int i,int j){
		if(this.chessman[i][j]!=null) //如果该位置有棋子
		{
			if(this.client.color==0) //如果是红棋
			{
				if(this.chessman[i][j].getColor().equals(Client.color1))//如棋子是红色
				{
					this.chessman[i][j].setFocus(true);//该棋子设为选中状态
					focus=true;
					startI=i;//保存坐标点
					startJ=j;
				}
			}
			else//如果是白棋
			{
				if(this.chessman[i][j].getColor().equals(Client.color2))
				{
					this.chessman[i][j].setFocus(true);
					focus=true;
					startI=i;
		            startJ=j;
				}
			}
		}
	}
	
	public void success() {
		chessman[endI][endJ] = chessman[startI][startJ]; //吃掉该棋子
		chessman[startI][startJ] = null;
		this.client.repaint();
		JOptionPane.showMessageDialog(this.client, "恭喜获胜","消息",JOptionPane.INFORMATION_MESSAGE);
		this.client.cat.challenger = null;
		this.client.color = 0;
		this.client.canMove = false;
		this.client.next();
		this.client.jtfHost.setEnabled(false);
		this.client.jtfPort.setEnabled(false);
		this.client.jtfNickName.setEnabled(false);
		this.client.jbConnect.setEnabled(false);
		this.client.jbDisconnect.setEnabled(true);
		this.client.jbChallenge.setEnabled(true);
		this.client.jbYChallenge.setEnabled(false);
		this.client.jbNChallenge.setEnabled(false);
		this.client.jbFail.setEnabled(false);
		startI = -1;
		startJ = -1;
		endI = -1;
		endJ = -1;
		jiang1_i = 4; // 将的坐标
		jiang1_j = 0;
		jiang2_i = 4;
		jiang2_j = 9;
		focus = false;
	}
	public void noJiang() {
		chessman[endI][endJ] = chessman[startI][startJ];
		chessman[startI][startJ] = null;
		chessman[endI][endJ].setFocus(false); //走完之后变成非选中状态
		this.client.repaint();
		if (chessman[endI][endJ].getName().equals("帥")) {
			jiang1_i = endI;
			jiang1_j = endJ;
		}
		else if(chessman[endI][endJ].getName().equals("將")){
			jiang2_i=endI;
			jiang2_j=endJ;
		}
		if(jiang1_i==jiang2_i){//如果将和帅在一条竖线
			int count=0;
			for(int jiang_j=jiang1_j+1;jiang_j<jiang2_j;jiang_j++){//遍历这条线
				if(chessman[jiang1_i][jiang_j]!=null){
					count++;
					break;
				}
			}
			if(count==0){//对脸了
		    	JOptionPane.showMessageDialog(this.client,"对脸，失败！","提示",
		    	            JOptionPane.INFORMATION_MESSAGE);
		    	this.client.cat.challenger = null;
				this.client.color=0;//还原棋盘
				this.client.canMove=false;
				this.client.next();//下一盘
				this.client.jtfHost.setEnabled(false);
				this.client.jtfPort.setEnabled(false);
				this.client.jtfNickName.setEnabled(false);
				this.client.jbConnect.setEnabled(false);
				this.client.jbDisconnect.setEnabled(true);
				this.client.jbChallenge.setEnabled(true);
				this.client.jbYChallenge.setEnabled(false);
				this.client.jbNChallenge.setEnabled(false);
				this.client.jbFail.setEnabled(false);
				jiang1_i=4;
				jiang1_j=0;
				jiang2_i=4;
				jiang2_j=9;
			}
		}
		startI=-1;
		startJ=-1;
		endI=-1;
		endJ=-1;
		focus=false;
	}
		
	public void noChessman(){ //要走的位置没有棋子
		try{//将走棋信息发给对方
			this.client.cat.dout.writeUTF("<#MOVE#>"+this.client.cat.challenger+startI+startJ+endI+endJ);
			this.client.canMove=false;
			chessman[endI][endJ]=chessman[startI][startJ];
			chessman[startI][startJ]=null;
			chessman[endI][endJ].setFocus(false);
			this.client.repaint();
			if(chessman[endI][endJ].getName().equals("帥")){
				jiang1_i=endI;
				jiang1_j=endJ;
			}
			else if(chessman[endI][endJ].getName().equals("將")){
				jiang2_i=endI;
				jiang2_j=endJ;
			}
			if(jiang1_i==jiang2_i)
			{
				int count=0;
				for(int jiang_j=jiang1_j+1;jiang_j<jiang2_j;jiang_j++){//±éÀúÕâÌõÊúÏß
					if(chessman[jiang1_i][jiang_j]!=null){
						count++;
						break;
					}
				}
				if(count==0){
					JOptionPane.showMessageDialog(this.client,"对脸，你失败了！","提示",
		    	            JOptionPane.INFORMATION_MESSAGE);
			    	this.client.cat.challenger=null;
					this.client.color=0;
					this.client.canMove=false;
					this.client.next();
					this.client.jtfHost.setEnabled(false);
					this.client.jtfPort.setEnabled(false);
					this.client.jtfNickName.setEnabled(false);
					this.client.jbConnect.setEnabled(false);
					this.client.jbDisconnect.setEnabled(true);
					this.client.jbChallenge.setEnabled(true);
					this.client.jbYChallenge.setEnabled(false);
					this.client.jbNChallenge.setEnabled(false);
					this.client.jbFail.setEnabled(false);
					jiang1_i=4;
					jiang1_j=0;
					jiang2_i=4;
					jiang2_j=9;
				}
			}
			startI=-1;
			startJ=-1;
			endI=-1;
			endJ=-1;
			focus=false;
		}
		catch(Exception ee){ee.printStackTrace();}
	}
	
	public void move(int startI,int startJ, int endI, int endJ) {
		//如果终点是将说明已经被吃掉，重新开始
		if (chessman[endI][endJ] != null && (chessman[endI][endJ].getName().equals("將")||chessman[endI][endJ].getName().equals("帥"))) {
			chessman[endI][endJ] = chessman[startI][startJ];
			chessman[startI][startJ] = null; //走棋
			this.client.repaint();
			JOptionPane.showMessageDialog(this.client, "你失败了","提示",JOptionPane.INFORMATION_MESSAGE);
			this.client.cat.challenger = null;
			this.client.color = 0;
			this.client.canMove = false;
			this.client.next();
			this.client.jtfHost.setEnabled(false);
			this.client.jtfPort.setEnabled(false);
			this.client.jtfNickName.setEnabled(false);
			this.client.jbConnect.setEnabled(false);
			this.client.jbDisconnect.setEnabled(true);
			this.client.jbChallenge.setEnabled(true);
			this.client.jbYChallenge.setEnabled(false);
			this.client.jbNChallenge.setEnabled(false);
			this.client.jbFail.setEnabled(false);
			jiang1_i=4;
			jiang1_j=0;
			jiang2_i=4;
			jiang2_j=9;  //将和帅的坐标
			
		} else {
			chessman[endI][endJ] = chessman[startI][startJ];
			chessman[startI][startJ] = null; //走棋
			this.client.repaint();
			if (chessman[endI][endJ].getName().equals("帥")) {
				jiang1_i=endI;
				jiang1_j=endJ;
			} else if(chessman[endI][endJ].getName().equals("將")){
				jiang2_i=endI;
				jiang2_j=endJ;
			}
			if(jiang1_i==jiang2_i)
			{
				int count=0;
				for(int jiang_j=jiang1_j+1;jiang_j<jiang2_j;jiang_j++){//±éÀúÕâÌõÊúÏß
					if(chessman[jiang1_i][jiang_j]!=null){
						count++;break;
					}
				}
				if(count==0){
					JOptionPane.showMessageDialog(this.client,"对方对脸，你胜利了！","提示",
		    	            JOptionPane.INFORMATION_MESSAGE);
			    	this.client.cat.challenger=null;
					this.client.color=0;
					this.client.canMove=false;
					this.client.next();
					this.client.jtfHost.setEnabled(false);
					this.client.jtfPort.setEnabled(false);
					this.client.jtfNickName.setEnabled(false);
					this.client.jbConnect.setEnabled(false);
					this.client.jbDisconnect.setEnabled(true);
					this.client.jbChallenge.setEnabled(true);
					this.client.jbYChallenge.setEnabled(false);
					this.client.jbNChallenge.setEnabled(false);
					this.client.jbFail.setEnabled(false);
					jiang1_i=4;
					jiang1_j=0;
					jiang2_i=4;
					jiang2_j=9;
				}
			}
			
		}
		this.client.repaint();
	}
	
	public void mousePressed(MouseEvent e){}
	public void mouseReleased(MouseEvent e){}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void smallLine(Graphics2D g, int i, int j) {
		int x=110+60*i;//计算坐标
		int y=80+60*j;
		if(i>0){//绘制左上标志
			g.drawLine(x-3,y-3,x-20,y-3);
			g.drawLine(x-3,y-3,x-3,y-20);
		}
		if(i<8){//绘制右上标志
			g.drawLine(x+3,y-3,x+20,y-3);
			g.drawLine(x+3,y-3,x+3,y-20);
		}
		if(i>0){//绘制左下标志
			g.drawLine(x-3,y+3,x-20,y+3);
			g.drawLine(x-3,y+3,x-3,y+20);
		}
		if(i<8){//绘制右下标志
			g.drawLine(x+3,y+3,x+20,y+3);
			g.drawLine(x+3,y+3,x+3,y+20);
		}
	}
	
	
	
	
	

}

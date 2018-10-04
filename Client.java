package chineseChess;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*; // 以上四个为图形化用户的包

import org.omg.CORBA.PUBLIC_MEMBER;

import java.net.*; //网络
import java.io.*; // 传输


public class Client extends JFrame implements ActionListener{
	public static final Color bgColor = new Color(255,185,15);  // 棋盘颜色
	public static final Color focusbg = new Color(242,242,242); // 棋子选中后背景颜色
	public static final Color focusChar = new Color(96, 95, 91); //棋子选中后字符色
	public static final Color color1 = new Color(249, 183, 173); //红方颜色
	public static final Color color2 = Color.white; //白方颜色
	JLabel jlHost = new JLabel("主机名"); // 输入主机名，端口号，昵称的标签
	JLabel jlPort = new JLabel("端口号");
	JLabel jlNickName = new JLabel("昵称");
	JTextField jtfHost = new JTextField("127.0.0.1"); // 创建输入端口号的文本框
	JTextField jtfPort = new JTextField("9999"); // 创建输入端口号的文本框
	JTextField jtfNickName = new JTextField("Player1"); //创建输入昵称文本框
	JButton jbConnect = new JButton("连接"); // 连接按钮
	JButton jbDisconnect = new JButton("断开");
	JButton jbFail = new JButton("认输");
	JButton jbChallenge = new JButton("挑战");
	JComboBox jcbNickList = new JComboBox(); //创建存放当前用户的下拉列表框
	JButton jbYChallenge = new JButton("接受挑战");
	JButton jbNChallenge = new JButton("拒绝挑战");
	int width = 60; // 设置棋盘两线之间距离
	Chessman[][] chessman = new Chessman[9][10];
	Board jpz = new Board(chessman,width,this); 
	JPanel jpy = new JPanel();
	JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jpz,jpy);
	boolean canMove = false; // 可否走棋的标志位
	int color = 0; // 0代表红旗， 1代表白棋
	Socket sc;
	ClientAgentThread cat;
	
	public Client() {
		this.initialComponent(); // 初始化控件
		this.addListener(); // 为相应控件注册事件监听
		this.initialState(); // 初始化状态
		this.initialChessman(); // 初始化棋子
		this.initialFrame(); //初始化窗体
	}
	
	public void initialComponent() {
		jpy.setLayout(null);
		this.jlHost.setBounds(10,10,50,20);
		jpy.add(this.jlHost); 
		this.jtfHost.setBounds(70,10,80,20);
		jpy.add(this.jtfHost);
		this.jlPort.setBounds(10,40,50,20);
		jpy.add(this.jlPort);
		this.jtfPort.setBounds(70,40,80,20);
		jpy.add(this.jtfPort);
		this.jlNickName.setBounds(10,70,50,20);
		jpy.add(this.jlNickName);
		this.jtfNickName.setBounds(70,70,80,20);
		jpy.add(this.jtfNickName);
		this.jbConnect.setBounds(10,100,80,20);
		jpy.add(this.jbConnect);
		this.jbDisconnect.setBounds(100,100,80,20);
		jpy.add(this.jbDisconnect);
		this.jcbNickList.setBounds(20,130,130,20);
		jpy.add(this.jcbNickList);
		this.jbChallenge.setBounds(10,160,80,20);
		jpy.add(this.jbChallenge);
		this.jbFail.setBounds(100,160,80,20);
		jpy.add(this.jbFail);
		this.jbYChallenge.setBounds(5,190,86,20);
		jpy.add(this.jbYChallenge);
		this.jbNChallenge.setBounds(100,190,86,20);
		jpy.add(this.jbNChallenge);
		jpz.setLayout(null);
		jpz.setBounds(0, 0, 700, 700);
	}
	
	public void addListener() {  //给各按钮注册事件监听器
		this.jbConnect.addActionListener(this);
		this.jbDisconnect.addActionListener(this);
		this.jbChallenge.addActionListener(this);
		this.jbFail.addActionListener(this);
		this.jbYChallenge.addActionListener(this);
		this.jbNChallenge.addActionListener(this);
		
	}
	
	public void initialState() { // 将按钮起始设为不可用
		this.jbDisconnect.setEnabled(false);
		this.jbChallenge.setEnabled(false);
		this.jbYChallenge.setEnabled(false);
		this.jbNChallenge.setEnabled(false);
		this.jbFail.setEnabled(false);
	}
	
	public void initialChessman() {
		// 红方棋子初始化
		chessman[0][0] = new Chessman(color1,"車",0,0);
		chessman[1][0] = new Chessman(color1,"馬",1,0);
		chessman[2][0] = new Chessman(color1,"相",2,0);
		chessman[3][0] = new Chessman(color1,"仕",3,0);
		chessman[4][0] = new Chessman(color1,"帥",4,0);
		chessman[5][0] = new Chessman(color1,"仕",5,0);
		chessman[6][0] = new Chessman(color1,"相",6,0);
		chessman[7][0] = new Chessman(color1,"馬",7,0);
		chessman[8][0] = new Chessman(color1,"車",8,0);
		chessman[1][2] = new Chessman(color1,"砲",1,2);
		chessman[7][2] = new Chessman(color1,"砲",7,2);
		chessman[0][3] = new Chessman(color1,"兵",0,3);
		chessman[2][3] = new Chessman(color1,"兵",2,3);
		chessman[4][3] = new Chessman(color1,"兵",4,3);
		chessman[6][3] = new Chessman(color1,"兵",6,3);
		chessman[8][3] = new Chessman(color1,"兵",8,3);
		
		//白方棋子初始化
		chessman[0][9] = new Chessman(color2,"車",0,9);
		chessman[1][9] = new Chessman(color2,"馬",1,9);
		chessman[2][9] = new Chessman(color2,"象",2,9);
		chessman[3][9] = new Chessman(color2,"士",3,9);
		chessman[4][9] = new Chessman(color2,"將",4,9);
		chessman[5][9] = new Chessman(color2,"士",5,9);
		chessman[6][9] = new Chessman(color2,"象",6,9);
		chessman[7][9] = new Chessman(color2,"馬",7,9);
		chessman[8][9] = new Chessman(color2,"車",8,9);
		chessman[1][7] = new Chessman(color2,"炮",1,7);
		chessman[7][7] = new Chessman(color2,"炮",7,7);
		chessman[0][6] = new Chessman(color2,"卒",0,6);
		chessman[2][6] = new Chessman(color2,"卒",2,6);
		chessman[4][6] = new Chessman(color2,"卒",4,6);
		chessman[6][6] = new Chessman(color2,"卒",6,6);
		chessman[8][6] = new Chessman(color2,"卒",8,6);
		
	}
	
	public void initialFrame() {
		this.setTitle("中国象棋－客户端");
//		Image image = new ImageIcon(".gif").getImage();
//		this.setIconImage(image);
		this.add(this.jsp);
		jsp.setDividerLocation(730);
		jsp.setDividerSize(4);
		this.setBounds(30,30,930,730);
		this.setVisible(true);
		this.addWindowListener(
				new WindowAdapter()
				{
					public void windowClosing(WindowEvent e)
					{
						if(cat==null)
						{
							System.exit(0);
							return;
						}
						try
						{
							if(cat.challenger!=null)
							{
								try
								{
									
									cat.dout.writeUTF("<#SURRENDER#>"+cat.challenger);
								}
								catch(Exception ee)
								{
									ee.printStackTrace();
								}
							}
							cat.dout.writeUTF("<#CLIENT_LEAVE#>");
							cat.flag=false;
							cat=null;
							
						}
						catch(Exception ee)
						{
							ee.printStackTrace();
						}
						System.exit(0);
					}
					
				}
		);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.jbConnect) {
			this.jbConnect_event();
		}
		if (e.getSource() == this.jbDisconnect) {
			this.jbDisconnect_event();
		}
		if (e.getSource() == this.jbChallenge) {
			this.jbChallenge_event();
		}
		if (e.getSource() == this.jbYChallenge) {
			this.jbYChallenge_event();
		}
		if (e.getSource() == this.jbNChallenge) {
			this.jbNChallenge_event();
		}
		if (e.getSource() == this.jbFail) {
			this.jbFail_event();
		}
	}
	
	public void jbConnect_event() {
		int port = 0;
		try {
			//获得用户输入的端口号并转化为整形
			port = Integer.parseInt(this.jtfPort.getText().trim());
			
		} catch (Exception e) {
			// 不是整数，给出错误信息
			JOptionPane.showMessageDialog(this, "端口只能是整数","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (port > 65535 || port < 0) {
			// 端口号不合法
			JOptionPane.showMessageDialog(this, "端口号范围0-65535","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		String name = this.jtfNickName.getText().trim();
		if (name.length() == 0) {
			JOptionPane.showMessageDialog(this, "玩家姓名不能为空","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			sc = new Socket(this.jtfHost.getText().trim(),port);
			cat = new ClientAgentThread(this);
			cat.start();
			this.jtfHost.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jtfNickName.setEnabled(false);
			this.jbConnect.setEnabled(false);
			this.jbDisconnect.setEnabled(true);
			this.jbChallenge.setEnabled(true);
			this.jbYChallenge.setEnabled(false);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(false);
			JOptionPane.showMessageDialog(this, "已连接到服务器","提示",JOptionPane.INFORMATION_MESSAGE);
			
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, "连接服务器失败","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	public void jbDisconnect_event() {
		try {
			this.cat.dout.writeUTF("<#CLIENT_LEAVE#>");
			this.cat.flag = false;
			this.cat = null;
			this.jtfHost.setEnabled(true);
			this.jtfPort.setEnabled(true);
			this.jtfNickName.setEnabled(true);
			this.jbConnect.setEnabled(true);
			this.jbDisconnect.setEnabled(false);
			this.jbChallenge.setEnabled(false);
			this.jbYChallenge.setEnabled(false);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(false);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void jbChallenge_event() {
		// 获得用户选中的挑战对象
		Object o = this.jcbNickList.getSelectedItem();
		if (o == null || ((String)o).equals("")) {
			JOptionPane.showMessageDialog(this, "请选择对方名字","错误",JOptionPane.ERROR_MESSAGE);
		} else {
			String name2 = (String) this.jcbNickList.getSelectedItem(); // 获得挑战对象
			try {
				this.jtfHost.setEnabled(false);
				this.jtfPort.setEnabled(false);
				this.jtfNickName.setEnabled(false);
				this.jbConnect.setEnabled(false);
				this.jbDisconnect.setEnabled(false);
				this.jbChallenge.setEnabled(false);
				this.jbYChallenge.setEnabled(false);
				this.jbNChallenge.setEnabled(false);
				this.jbFail.setEnabled(false);
				this.cat.challenger = name2;
				this.canMove = true; // 可以走棋
				this.color = 0; // 提出挑战的走红棋
				this.cat.dout.writeUTF("<#CHALLENGE#>"+name2);
				JOptionPane.showMessageDialog(this, "已提出挑战，请等待回复","提示",JOptionPane.INFORMATION_MESSAGE);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void jbYChallenge_event() {
		try {
			this.cat.dout.writeUTF("<#AGREE#>" + this.cat.challenger);
			this.canMove = false; //对方先走棋
			this.color = 1; // 被挑战者走白棋
			this.next();
			this.jtfHost.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jtfNickName.setEnabled(false);
			this.jbConnect.setEnabled(false);
			this.jbDisconnect.setEnabled(false);
			this.jbChallenge.setEnabled(false);
			this.jbYChallenge.setEnabled(false);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(true);
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void jbNChallenge_event() {
		try {
			this.cat.dout.writeUTF("<#DISAGREE#>"+this.cat.challenger);
			this.cat.challenger = null;
			this.jtfHost.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jtfNickName.setEnabled(false);
			this.jbConnect.setEnabled(false);
			this.jbDisconnect.setEnabled(true);
			this.jbChallenge.setEnabled(true);
			this.jbYChallenge.setEnabled(false);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(false);
		} catch (Exception e) {
			
		}
	}
	
	public void jbFail_event() {
		try {
			this.cat.dout.writeUTF("<#SURRENDER#>"+this.cat.challenger);
			this.cat.challenger = null;
			this.color = 0;
			this.canMove = false;
			this.next(); // 初始化下一局
			this.jtfHost.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jtfNickName.setEnabled(false);
			this.jbConnect.setEnabled(false);
			this.jbDisconnect.setEnabled(true);
			this.jbChallenge.setEnabled(true);
			this.jbYChallenge.setEnabled(false);
			this.jbNChallenge.setEnabled(false);
			this.jbFail.setEnabled(false);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void next() {
		for (int i=0;i<9;i++) {
			for (int j=0;j<10;j++) {
				this.chessman[i][j] = null;
			}
		}
		this.canMove = false;
		this.initialChessman();
		this.repaint(); // 重绘
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		new Client();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

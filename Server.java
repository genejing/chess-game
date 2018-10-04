package chineseChess;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.ServerSocket;
import java.util.Vector;


public class Server extends JFrame implements ActionListener{
	
	JLabel jlPort = new JLabel("端口号"); // 创建输入的端口号
	JTextField jtfPort = new JTextField("9999"); // 用于输入端口号的文本框
	JButton jbStart = new JButton("启动");
	JButton jbStop = new JButton("关闭");
	JPanel jps = new JPanel();
	JList jlUserOnline = new JList(); // 创建用于显示当前用户的JList
	JScrollPane jspx = new JScrollPane(jlUserOnline); // 将当前用户的JList放在scrollpane
	JSplitPane jspz = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jspx,jps);
	ServerSocket ss; // 声明serversocket的引用
	ServerThread st; // 声明ServerThread饮用
	Vector onlineList = new Vector(); //创建存放当前用户的vector对象
	
	public Server() {
		this.initialComponent(); //初始化控件
		this.addListener();  // 为相应的控件注册事件监听器
		this.initialFrame(); // 初始化窗体
	}
	
	public void initialComponent() {
		jps.setLayout(null); // 设为空布局
		jlPort.setBounds(20, 20, 50, 20);
		jps.add(this.jlPort);
		this.jtfPort.setBounds(85,20,60,20);
		jps.add(this.jtfPort);
		this.jbStart.setBounds(18,50,60,20);
		jps.add(this.jbStart);
		this.jbStop.setBounds(85,50,60,20);
		jps.add(this.jbStop);
		this.jbStop.setEnabled(false);
	}
	
	public void addListener() {
		this.jbStart.addActionListener(this); //为开始按钮注册事件监听器
		this.jbStop.addActionListener(this);
	}
	
	public void initialFrame() {
		this.setTitle("象棋－服务器");
//		Image image = new ImageIcon(".gif").getImage();
//		this.setIconImage(image);
		this.add(jspz);
		jspz.setDividerLocation(250);
		jspz.setDividerSize(4); // 设置分割线的位置和宽度
		this.setBounds(20,20,420,320);
		this.setVisible(true); //设置可见性
		this.addWindowListener(
			new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					if (st == null) {
						System.exit(0);
						return;
					}
					try {
						Vector v = onlineList;
						int size = v.size();
						for (int i=0; i<size;i++) {
							ServerAgentThread tempSat = (ServerAgentThread) v.get(i);
							tempSat.dout.writeUTF("<#SERVER_DOWN#>");
							tempSat.flag = false; // 终止服务器代理线程
						}
						st.flag = false; //终止服务器线程
						st = null;
						ss.close();
						v.clear(); // 将在线用户列表清空
						refreshList(); // 刷新列表
					} catch(Exception ee) {
						ee.printStackTrace();
					}
					System.exit(0);
				}
			}
		);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.jbStart) {
			this.jbStart_event();
		}
		if (e.getSource() == this.jbStop) {
			this.jbStop_event();
//			jbStart.setEnabled(true);
//			jbStop.setEnabled(false);
		}
	}
	
	public void jbStart_event() {
		// 单击启动按钮的业务处理代码
		int port = 0;
		try {
			//获得用户输入的端口号并转化为整形
			port = Integer.parseInt(this.jtfPort.getText().trim());
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "端口只能是整数","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (port > 65535 || port < 0) {
			JOptionPane.showMessageDialog(this, "端口号范围0-65535","错误",JOptionPane.ERROR_MESSAGE);
			return;
		}
		try {
			this.jbStart.setEnabled(false);
			this.jtfPort.setEnabled(false);
			this.jbStop.setEnabled(true);
			ss = new ServerSocket(port); // 创建serversocket
			st = new ServerThread(this); // 创建服务器线程
			st.start();  // 启动服务器线程
			JOptionPane.showMessageDialog(this, "服务器启动成功","提示",JOptionPane.INFORMATION_MESSAGE);
			
			
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "服务器启动失败","错误",JOptionPane.ERROR_MESSAGE);
			this.jbStart.setEnabled(true);
			this.jtfPort.setEnabled(true);
			this.jbStop.setEnabled(false);
		}
	}
	
	public void jbStop_event() {
		try {
			Vector v = onlineList;
			int size = v.size();
			for (int i=0; i<size;i++) {
				ServerAgentThread tempSat = (ServerAgentThread) v.get(i);
				tempSat.dout.writeUTF("<#SERVER_DOWN#>");
				tempSat.flag = false; // 关闭服务器代理线程
			}
			st.flag = false; //关闭服务器线程
			st = null;
			ss.close(); //关闭seversocket
			v.clear();
			refreshList();
			this.jbStart.setEnabled(true);
			this.jtfPort.setEnabled(true);
			this.jbStop.setEnabled(false);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refreshList() {
		//更新在线用户列表的业务处理代码 （如意外中断）
		Vector v = new Vector();
		int size = this.onlineList.size();
		for (int i=0;i<size;i++) {
			ServerAgentThread temSat = (ServerAgentThread) this.onlineList.get(i);
			String temps = temSat.sc.getInetAddress().toString();
			temps = temps+"|" + temSat.getName(); //获得所需信息
			v.add(temps); 
		}
		this.jlUserOnline.setListData(v);
		
	}
	
	
	
	public static void main(String[] args) {
		new Server();
	}
	
	
}

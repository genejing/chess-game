package chineseChess;

import java.awt.*;
import java.util.*;
import javax.swing.*;

import org.omg.CORBA.INTERNAL;

import java.util.jar.Attributes.Name;

public class Chessman {
	private Color color;  // 棋子颜色
	private String name; //棋子种类
	private int x;
	private int y; // 所在x,y方向
	private boolean focus = false; //是否被选中
	public Chessman() {}
	public Chessman(Color color, String name, int x,int y) {
		this.color = color;
		this.name = name;
		this.x = x;
		this.y = y;
		this.focus = false;
	}
	
	public Color getColor() {
		return this.color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getX() {
		return this.x;
	}
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return this.y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public boolean getFocus() {	
		return this.focus;
	}
	public void setFocus(boolean focus) {
		this.focus = focus;
	}
	
	
	
}

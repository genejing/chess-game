package chineseChess;



// 规则类不应用任何包

public class Rule {
	Chessman[][] chessman; // 棋子的数组
	boolean canMove = false;
	int i,j; // x,y坐标
	public Rule(Chessman[][] chessman) {
		this.chessman = chessman;
	}
	
	public boolean canMove(int startI,int startJ,int endI,int endJ,String name) {
		int maxI;  //  辅助变量
		int minI;
		int maxJ;
		int minJ;
		canMove = true;
		if (startI >= endI) { // 判断起始坐标大小关系
			maxI = startI;
			minI = endI;
		} else {
			maxI = endI;
			minI = startI;
		}
		if (startJ >= endJ) {
			maxJ = startJ;
			minJ = endJ;
		} else {
			maxJ = endJ;
			minJ = startJ;
		}
		if (name.equals("车")) {   //车起始在角上，特殊
			this.ju(maxI,minI,maxJ,minJ);
		} else if(name.equals("马")) {
			this.ma(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
		} else if(name.equals("相")) {
			this.xiang1(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
		} else if(name.equals("象")) {
			this.xiang2(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
		} else if(name.equals("仕") || name.equals("士")) {
			this.shi(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
		} else if(name.equals("將") || name.equals("帥")) {
			this.jiang(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
		} else if(name.equals("炮")) {
			this.pao(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
		} else if(name.equals("兵")) {
			this.bing(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
		} else if(name.equals("卒")) {
			this.zu(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
		} 
		return canMove;
	}
	
	public void ju(int maxI,int minI,int maxJ,int minJ) {
		if (maxI == minI) {  //如果在一条横线上
			for (j=minJ+1;j<maxJ;j++) {
				if (chessman[maxI][j] != null) { // 中间有棋子
					canMove =false;
					break;
				}
			} 
		} else if(maxJ == minJ) {  // 如果在一条竖线上
			for (i = minJ+1;i<maxJ;i++) {
				if (chessman[i][maxJ] != null) {
					canMove = false;
					break;
				}
			}
		} else if (maxI != minI && maxJ != minJ) { 
			canMove = false; //不在一条线上不可用走棋
		}
	}
	
	public void ma(int maxI,int minI,int maxJ,int minJ,int startI,int startJ,int endI,int endJ) {
		int a = maxI -minI;
		int b = maxJ - minJ;
		if (a == 1 && b == 2) { // 横着的日
			if(startJ > endJ) { // 如果是从右向左走
				if (chessman[startI][startJ-1] != null) { //马腿处有棋子
					canMove = false;
				}
			} else { // 如果是从左往右走
				if (chessman[startI][startJ+1] != null) { //马腿处有棋子
					canMove = false;
				}
			}
		} else if (a == 2 && b == 1) { // 竖着的日
			if(startI > endI) { // 如果是从下向上走
				if (chessman[startI-1][startJ] != null) { //马腿处有棋子
					canMove = false;
				}
			} else { // 如果是从上往下走
				if (chessman[startI+1][startJ] != null) { //马腿处有棋子
					canMove = false;
				}
			}
		} else if (!((a==2&&b==1) || (a==1&&b==2))) {
			canMove = false; //不是日不能走
		}
	}
	
	public void xiang1(int maxI,int minI,int maxJ,int minJ,int startI,int startJ,int endI,int endJ) {
		// 相的处理
		int a = maxI -minI;
		int b = maxJ - minJ;
		if (a == 2 && b == 2) {
			if(endJ>4) { // 过河
				canMove = false;
			}
			if (chessman[(maxI+minI)/2][(minI+maxJ)/2] != null ){ // 象眼有棋子
				canMove = false;
			}
		} else {
			canMove = false;
		}
	}
	
	public void xiang2(int maxI,int minI,int maxJ,int minJ,int startI,int startJ,int endI,int endJ) {
		int a = maxI -minI;
		int b = maxJ - minJ;
		if (a == 2 && b == 2) {
			if(endJ < 5) { // 过河
				canMove = false;
			}
			if (chessman[(maxI+minI)/2][(minI+maxJ)/2] != null ){ // 象眼有棋子
				canMove = false;
			}
		} else {
			canMove = false;
		}
	}
	
	public void shi(int maxI,int minI,int maxJ,int minJ,int startI,int startJ,int endI,int endJ) {
		int a = maxI -minI;
		int b = maxJ - minJ;
		if (a == 1 && b == 1) {
			if(startJ > 4) { // 过河
				if (endJ < 7) {
					canMove = false;
				}
			} else {
				if (endJ > 2) {
					canMove = false;
				}
			}
			if (endI > 5 || endI < 3) { // 左右越界
				canMove = false;
			}
			
		} else {
			canMove = false;
		}
	}
	
	public void jiang(int maxI,int minI,int maxJ,int minJ,int startI,int startJ,int endI,int endJ) {
		int a = maxI -minI;
		int b = maxJ - minJ;
		if (a==1 && b==0 || (a==0 && b==1)) {
			if (startJ > 4) {
				if (endJ < 7) {
					canMove = false;
				}
			} else {
				if (endJ > 2) {
					canMove = false;
				}
			}
			if (endI > 5 || endI < 3) {
				canMove = false;
			}
		} else {
			canMove = false;
		}
	}
	
	public void pao(int maxI,int minI,int maxJ,int minJ,int startI,int startJ,int endI,int endJ) {
		if (maxI == minI) {
			if (chessman[endI][endJ] != null) {
				int count = 0;
				for (int j=minJ+1;j < maxJ;j++) {
					if (chessman[minI][j] != null) {
						count++;
					}
				}
				if (count != 1) {
					canMove = false;
				}
			} else {
				for (int j=minJ+1;j < maxJ;j++) {
					if (chessman[minI][j] != null) {
						canMove = false;
						break;
					}
				}
			}
		} else if (maxJ == minJ) {
			if (chessman[endI][endJ] != null) {
				int count = 0;
				for (int i=minI+1;i < maxI;i++) {
					if (chessman[i][minJ] != null) {
						count++;
					}
				}
				if (count != 1) {
					canMove = false;
				}
			} else {
				for (int i=minI+1;i < maxI;i++) {
					if (chessman[i][minJ] != null) {
						canMove = false;
						break;
					}
				}
			}
		} else {
			canMove = false;
		}
	}
	
	public void bing(int maxI,int minI,int maxJ,int minJ,int startI,int startJ,int endI,int endJ) {
		// 处理兵
		if(startJ < 5) { // 如果兵还没有过河
			if (startI != endI) {  // 如果不是向前
				canMove = false; 
				return;
			}
			if (endJ - startJ != 1) { //如果走的不是一格
				canMove = false;
			}
			
		} else {
			if (startI == endI) {
				if (endJ - startJ!=1) {
					canMove = false;
				}
			} else if (startJ == endJ) {
				if (maxI - minI != 1) {
					canMove = false;
				}
			} else if (startI != endI &&startJ != endJ){
				canMove = false;
			}
		}
	}
	
	public void zu(int maxI,int minI,int maxJ,int minJ,int startI,int startJ,int endI,int endJ) {
		// 处理兵
		if(startJ > 4) { // 如果兵还没有过河
			if (startI != endI) {  // 如果不是向前
				canMove = false; 
				return;
			}
			if (endJ - startJ != 1) { //如果走的不是一格
				canMove = false;
				return;
			}
		} else {
			if (startI == endI) {
				if (endJ - startJ!=-1) {
					canMove = false;
				}
			} else if (startJ == endJ) {
				if (maxI - minI != 1) {
					canMove = false;
				}
			} else if (startI != endI &&startJ != endJ){
				canMove = false;
			}
		}
	}
	
}

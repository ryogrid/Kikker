package jp.ryo.informationPump.server.debug.magnetic;

// Copyright (C) 2000 by Toyohisa Nakada. All Rights Reserved.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.util.*;
import java.lang.reflect.*;

/**
 * Magnetic-Sprint Model
 * @author 中田豊久
 */
public class MagneticSpringPanel extends JPanel implements Runnable{
	/** 計算間隔（Nodeの移動があるとき） */
	final int m_nSleep = 20;
	/** 計算時間（Nodeの移動がないとき） */
	final int m_nSleepNochange = 1000;
	/** 画面の有効部分（この部分以外は、描画対象としない） */
	final double m_dRate = 0.9;
//    final double m_dRate = 2.0;
	/** 計算するアルゴリズムのメソッドのクラスリスト */
	String m_methodHead = "jp.ryo.informationPump.server.debug.magnetic.MagneticSpring";
	String m_methodList[] = {
		"Spring",
		"Repulsion",
		"Magnet",
	};
	/** この変数値の回数、Nodeの位置が変更されなかったら、スレッドを終了する。 */
	final int m_nNochangeEnd = 1;
	/** 図形の中心にラインを描画する場合、trueを設定する。 */
	final boolean m_bCenterDraw = true;

	Vector m_node=null;
	Vector m_edge=null;
	Vector m_vmethod;
	int m_crossCount=0;
	int m_calcCount=0;

	Node m_checkNode = null;
	MouseListener mouseListener = new MouseAdapter(){
		synchronized public void mousePressed(MouseEvent e){
			if(m_node != null){
				int xinit = getXInit();
				int yinit = getYInit();
				for(int cnt=(m_node.size()-1);cnt>=0;cnt--){
					Node node = (Node)m_node.get(cnt);
					if(true == DrawTool.drawStringTest(getGraphics(),node.m_name,
						node.m_x+xinit,node.m_y+yinit,m_bCenterDraw,e.getX(),e.getY())){
						node.setFixed(true);
						m_checkNode = node;
						addMouseMotionListener(mouseMotionListener);
						break;
					}
				}
			}
		}
		synchronized public void mouseReleased(MouseEvent e){
			if(m_checkNode != null){
				m_checkNode.setFixed(false);
				m_checkNode = null;
				removeMouseMotionListener(mouseMotionListener);
			}
		}
	};
	MouseMotionListener mouseMotionListener = new MouseMotionAdapter(){
		synchronized public void mouseDragged(MouseEvent e){
			if(m_checkNode != null){
				m_checkNode.m_x = e.getX()-getXInit();
				m_checkNode.m_y = e.getY()-getYInit();
				repaint();
			}
		}
	};

	MagneticSpringPanel(){
		addMouseListener(mouseListener);
		m_vmethod = new Vector();
		try{
			Class param[] =
				{Class.forName("java.util.Vector"),Class.forName("java.util.Vector")};
			for(int cnt=0;cnt<m_methodList.length;cnt++){
				try{
					Class spring = Class.forName(m_methodHead+m_methodList[cnt]);
					ForceMethod method = new ForceMethod(spring.getMethod("force",param),m_methodList[cnt]);
					m_vmethod.add(method);
				}catch(Exception e){
					System.out.println(e);
				}
			}
		}catch(Exception e){
			System.out.println(e);
			return;
		}
	}
	Vector getForceMethod(){
		return m_vmethod;
	}
	void clear(){
		m_node = null;
		m_edge = null;
		m_threadme = null;
		m_nMode = MODE_CALC;
	}
	boolean setData(Vector node,Vector edge,int type){
		m_node = new Vector(node);
		m_edge = new Vector(edge);
		MagneticSpringInitialize.init(m_node,m_edge,getEffectSize(),type);
		return true;
	}
	boolean setData(DefaultMutableTreeNode data,int nParent){
		return setData(data,nParent,MagneticSpringInitialize.CIRCLE);
	}
	boolean setData(DefaultMutableTreeNode data,int nParent,int type){
		if(m_node == null)
			m_node = new Vector();
		if(m_edge == null)
			m_edge = new Vector();

		setDataIn(data,nParent);

		// init
		MagneticSpringInitialize.init(m_node,m_edge,getEffectSize(),type);
		return true;
	}
	boolean setDataIn(DefaultMutableTreeNode data,int nParent){
		Node node = new Node((String)data.getUserObject());
		m_node.add(node);
		int nChild = m_node.size()-1;
		if(nParent != -1){
			Edge edge = new Edge(nParent,nChild);
			m_edge.add(edge);
		}
		for(Enumeration en=data.children();en.hasMoreElements();){
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)en.nextElement();
			if(false == setDataIn(child,nChild))
				return false;
		}
		return true;
	}
	Dimension getEffectSize(){
		Dimension size = getSize();
		Dimension usesize = new Dimension((int)(m_dRate*size.width),(int)(m_dRate*size.height));
		return usesize;
	}
	int getXInit(){
		return (int)(((1-m_dRate)*getSize().width)/2);
	}
	int getYInit(){
		return (int)(((1-m_dRate)*getSize().height)/2);
	}
	public void paint(Graphics g){
//	    AffineTransform transform = new AffineTransform();
//        transform.scale(0.5,0.5);
//		((Graphics2D)g).setTransform(transform);
        
        int xinit = getXInit();
		int yinit = getYInit();

		Color col = g.getColor();
		g.setColor(getBackground());
		g.fillRect(0,0,getSize().width,getSize().height);
		g.setColor(col);

		if(m_node == null){
			g.drawString("There is no data.",10,10);
			return;
		}
		for(Enumeration ee=m_edge.elements();ee.hasMoreElements();){
			Edge ed = (Edge)ee.nextElement();
			Node ndfrom = ed.getNodeFrom(m_node);
			Node ndto = ed.getNodeTo(m_node);
			if(ed.m_cross != 0)
				g.setColor(Color.red);
			else
				g.setColor(Color.black);
			g.drawLine(ndfrom.m_x+xinit,ndfrom.m_y+yinit,ndto.m_x+xinit,ndto.m_y+yinit);
		}
		g.setColor(Color.black);
		for(Enumeration en=m_node.elements();en.hasMoreElements();){
			Node nd = (Node)en.nextElement();
            if(nd.isValid == true){
                DrawTool.drawString(g,nd.m_name,nd.m_x+xinit,nd.m_y+yinit,m_bCenterDraw);    
            }
		}
		if(m_nMode == MODE_WAIT)
			g.drawString("End calc !!",10,20);
		g.drawString("Cross line count = "+m_crossCount,10,35);
		g.drawString("Calc count = "+m_calcCount,10,55);
		return;
	}

	Thread m_threadme = null;
	final int MODE_CALC=0;
	final int MODE_WAIT=1;
	int m_nMode = MODE_CALC;

	void relax(){
		m_threadme = new Thread(this);
		m_threadme.start();
	}
	void relaxstop(){
		m_threadme = null;
	}
    public void run(){
		Thread me = Thread.currentThread();

		m_calcCount = 0;
		int nNochangeCount = 0;
		while(me == m_threadme){
			for(Enumeration en=m_vmethod.elements();en.hasMoreElements();){
				ForceMethod fmh = (ForceMethod)en.nextElement();
				if(fmh.getAvailable() == true){
					try{
						Object param[] = {m_node,m_edge};
						fmh.m_method.invoke(null,param);
					}catch(Exception e){
						System.out.println(e);
					}
				}
			}
			// 全ての計算終了後に、m_dx,m_dyに保存した変化量をm_x,m_yに適用する。
			// この計算は、
			//  ① m_x,m_y へ m_dx,m_dy を計算
			//  ② 重心位置を計算
			//  ③ 重心を画面中心になるように、m_x,m_y を再計算
			//  ④ 画面からはみ出る m_x,m_y を強制的にはみ出ないように計算する。
			double xall=0,yall=0;
			for(Enumeration en=m_node.elements();en.hasMoreElements();){
				Node node = (Node)en.nextElement();
				node.setBackXY();

				// 四捨五入
				if(node.m_dx > 0)
					node.m_dx += 0.5;
				else if(node.m_dx < 0)
					node.m_dx -= 0.5;
				if(node.m_y > 0)
					node.m_dy += 0.5;
				else if(node.m_dy < 0)
					node.m_dy -= 0.5;
//System.out.println("node.m_dx="+node.m_dx+" node.m_dy="+node.m_dy);
//System.out.println("(int)m_dx="+(int)node.m_dx);

				node.m_x += (int)node.m_dx;
				node.m_y += (int)node.m_dy;
				node.m_dx = 0;
				node.m_dy = 0;

				xall += node.m_x;
				yall += node.m_y;
			}
			Dimension d = getEffectSize();
			double xdiff = xall/m_node.size()-(double)d.width/2;
			double ydiff = yall/m_node.size()-(double)d.height/2;
			boolean bNochange=true;
			for(Enumeration en=m_node.elements();en.hasMoreElements();){
				Node node = (Node)en.nextElement();
				node.m_x -= xdiff;
				node.m_y -= ydiff;
				if(node.m_x > d.width)
					node.m_x = d.width;
				else if(node.m_x < 0)
					node.m_x = 0;
				if(node.m_y > d.height)
					node.m_y = d.height;
				else if(node.m_y < 0)
					node.m_y = 0;
				if(node.checkBackXY() == false)
					bNochange = false;
			}
			// 全ての点の位置が変更されなければ、Waitモードへ
			// 少なくとも１つが変更されれば、Calcモードへ
			if(bNochange==true && m_nMode==MODE_CALC){
				if(nNochangeCount+1 > m_nNochangeEnd)
					m_nMode = MODE_WAIT;
				else
					nNochangeCount++;
			}else if(bNochange==false && m_nMode==MODE_WAIT){
				m_nMode = MODE_CALC;
				nNochangeCount = 0;
			}

			// エッジの交差を計算
			m_crossCount = 0;
			for(Enumeration ee=m_edge.elements();ee.hasMoreElements();){
				Edge edge = (Edge)ee.nextElement();
				edge.m_cross = 0;
			}
			for(int cnt1=0;cnt1<(m_edge.size()-1);cnt1++){
				try{
					Edge e1 = (Edge)m_edge.get(cnt1);
					Node n1f = (Node)m_node.get(e1.m_from);
					Node n1t = (Node)m_node.get(e1.m_to);
					int x1 = n1f.m_x;
					int y1 = n1f.m_y;
					int x2 = n1t.m_x;
					int y2 = n1t.m_y;
					for(int cnt2=cnt1+1;cnt2<m_edge.size();cnt2++){
						Edge e2 = (Edge)m_edge.get(cnt2);
						Node n2f = (Node)m_node.get(e2.m_from);
						Node n2t = (Node)m_node.get(e2.m_to);

						int x3 = n2f.m_x;
						int y3 = n2f.m_y;
						int x4 = n2t.m_x;
						int y4 = n2t.m_y;

						if((x1-x2!=0 || x3-x4!=0) &&
							!((x1==x3&&y1==y3) || (x1==x4&&y1==y4) || (x2==x3&&y2==y3) || (x2==x4&&y2==y4) || (x3==x4&&y3==y4) )){
							double xm = (x3-x4)*(y1-y2)-(x1-x2)*(y3-y4);
							if(xm != 0){
								double xc = (y3-y1)*(x1-x2)*(x3-x4)+(x3-x4)*(y1-y2)*x1-(x1-x2)*(y3-y4)*x3;
								double x = xc/xm;

								if(x <= Math.max(x1,x2) && x >= Math.min(x1,x2) &&
									x <= Math.max(x3,x4) && x >= Math.min(x3,x4)){
									double y;
									if((x1-x2)!=0)
										y = (y1-y2)*x/(x1-x2)+y1-(y1-y2)*x1/(x1-x2);
									else
										y = (y3-y4)*x/(x3-x4)+y3-(y3-y4)*x3/(x3-x4);
									if(y <= Math.max(y1,y2) && y >= Math.min(y1,y2) &&
										y <= Math.max(y3,y4) && y >= Math.min(y3,y4)){
										e1.m_cross++;
										e2.m_cross++;
										m_crossCount++;
									}
								}
							}
						}
					}
				}catch(Exception e){
					System.out.println(e);
				}
			}

			// 計算回数のインクリメント
			m_calcCount++;

			// 再描画
			repaint();

			try{
				if(m_nMode == 0)
					Thread.sleep(m_nSleep);
				else
					Thread.sleep(m_nSleepNochange);
			}catch(InterruptedException e){
				System.out.println(e);
				break;
			}
		}
	}
}
/** Node class */
class Node {
    public boolean isValid = false; 
    
	Node(String name){
		m_name = name;
	}
	void setPosition(int x,int y){
		m_x = x;
		m_y = y;
	}
	void setFixed(boolean fixed){
		m_fixed = fixed;
	}
	void setNumber(int number){
		m_number = number;
	}
	int getNumber(){
		return m_number;
	}
	void setNearNode(int node){
		m_nearNode.add(new Integer(node));
	}
	boolean isLinkedNode(Node node){
		int n = node.getNumber();
		for(Enumeration en=m_nearNode.elements();en.hasMoreElements();){
			Integer i = (Integer)en.nextElement();
			if(i.intValue() == n)
				return true;
		}
		return false;
	}
	boolean getFixed(){
		return m_fixed;
	}
	boolean isNearNode(Node node,int circle){
		if(circle >=
			Math.sqrt(Math.pow((double)m_x-node.m_x,2)+Math.pow((double)m_y-node.m_y,2))){
			return true;
		}
		return false;
	}
	int m_number;
	int m_x;
	int m_y;
	Vector m_nearNode = new Vector();

	double m_dx = 0;
	double m_dy = 0;

	boolean m_fixed;
	String m_name;

	/* 以下は計算のために必要とする。 */
	int m_xBack;
	int m_yBack;
	void setBackXY(){
		m_xBack = m_x;
		m_yBack = m_y;
	}
	boolean checkBackXY(){
		if(m_xBack == m_x && m_yBack == m_y)
			return true;
		return false;
	}
}
/** Edge class */
class Edge {
	public static final int TYPE_IGNORE = 0;
	public static final int TYPE_01 = 1;
	public static final int TYPE_02 = 2;
	public static final int TYPE_03 = 3;
	Edge(int from,int to){
		m_from = from;
		m_to = to;
		m_type = TYPE_IGNORE;
	}
	Edge(int from,int to,int type){
		this(from,to);
		m_type = type;
	}
	Edge(int from,int to,int type,boolean crossCalc){
		this(from,to,type);
		m_bCrossCalc = crossCalc;
	}
	void setLength0(double len){
		m_length0 = len;
	}
	double getLength0(){
		return m_length0;
	}
	int getType(){
		return m_type;
	}
	int m_from;
	int m_to;
	int m_type;

	double m_length0 = 0;
	int m_cross = 0;
	/** 交差しているラインの時に特別な計算をするかどうかを指定する。 */
	boolean m_bCrossCalc = false;
	boolean getCrossCalc(){
		return m_bCrossCalc;
	}

	Node getNodeFrom(Vector node){
		try{
			Node nd = (Node)node.get(m_from);
			return nd;
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println(e);
		}
		return null;
	}
	Node getNodeTo(Vector node){
		try{
			Node nd = (Node)node.get(m_to);
			return nd;
		}catch(ArrayIndexOutOfBoundsException e){
			System.out.println(e);
		}
		return null;
	}
}
/** Force method control class */
class ForceMethod {
	Method m_method;
	boolean m_available;
	String m_name;

	ForceMethod(Method method,String name){
		m_method = method;
		m_name = name;
		m_available = true;
	}
	boolean getAvailable(){
		return m_available;
	}
	void setAvailable(boolean available){
		m_available = available;
	}
	String getName(){
		return m_name;
	}
}

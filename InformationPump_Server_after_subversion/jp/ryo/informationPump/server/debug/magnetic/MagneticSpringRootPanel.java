package jp.ryo.informationPump.server.debug.magnetic;

// Copyright (C) 2000 by Toyohisa Nakada. All Rights Reserved.
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;

import javax.swing.*;
import javax.swing.text.*;
import java.util.*;

import jp.ryo.informationPump.server.util.SparseMatrix;

/**
 * @author 中田豊久
 */
public class MagneticSpringRootPanel extends JPanel{
	/** 交差しているラインをカウントして、特別な処理を行う場合に true に設定する。 */
	final boolean m_bCrossLine = false;
    private SparseMatrix matrix;
    private ArrayList community_set;

	MagneticSpringPanel m_panel;
	Vector m_forceMethod;

	public MagneticSpringRootPanel(SparseMatrix matrix,ArrayList community_set){
		setLayout(new BorderLayout());
        this.matrix = matrix;
        this.community_set = community_set;

		m_panel = new MagneticSpringPanel();
		add(m_panel,BorderLayout.CENTER);

		JPanel control = new JPanel();
		/**/
		MyItemListener listener = new MyItemListener();
		m_forceMethod = m_panel.getForceMethod();
		for(Enumeration en=m_forceMethod.elements();en.hasMoreElements();){
			ForceMethod mh = (ForceMethod)en.nextElement();
//System.out.println("mh = "+mh.getName()+" available = "+mh.getAvailable());
			JCheckBox check = new JCheckBox(mh.getName(),mh.getAvailable());
			listener.add(check);
			control.add(check);
		}
		/**/
		control.add(new JButton(ac3));
		control.add(new JButton(ac4));
		control.add(new JButton(ac1));
//		control.add(new JButton(ac2));
		add(control,BorderLayout.SOUTH);
	}
    
    MagneticSpringRootPanel(){
        setLayout(new BorderLayout());

        m_panel = new MagneticSpringPanel();
        add(m_panel,BorderLayout.CENTER);

        JPanel control = new JPanel();
        /**/
        MyItemListener listener = new MyItemListener();
        m_forceMethod = m_panel.getForceMethod();
        for(Enumeration en=m_forceMethod.elements();en.hasMoreElements();){
            ForceMethod mh = (ForceMethod)en.nextElement();
//System.out.println("mh = "+mh.getName()+" available = "+mh.getAvailable());
            JCheckBox check = new JCheckBox(mh.getName(),mh.getAvailable());
            listener.add(check);
            control.add(check);
        }
        /**/
        control.add(new JButton(ac3));
        control.add(new JButton(ac4));
        control.add(new JButton(ac1));
//      control.add(new JButton(ac2));
        add(control,BorderLayout.SOUTH);
    }
    
	class MyItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent e){
			Object source = e.getItemSelectable();
			for(int cnt=0;cnt<m_objList.size();cnt++){
				JCheckBox ob = (JCheckBox)m_objList.get(cnt);;
				if(ob == source){
					ForceMethod mh = (ForceMethod)m_forceMethod.get(cnt);
					mh.setAvailable(e.getStateChange() == ItemEvent.SELECTED);
				}
			}
		}
		void add(JCheckBox check){
			m_objList.add(check);
			check.addItemListener(this);
		}
		Vector m_objList = new Vector();
	};

	Action ac1 = new TextAction("Start") {
		public void actionPerformed(ActionEvent event){
			createDecisionTree();
		}
	};
	Action ac2 = new TextAction("Clear"){
		public void actionPerformed(ActionEvent event){
			clear();
		}
	};
	Action ac3 = new TextAction("Init Circle"){
		public void actionPerformed(ActionEvent event){
			setData(MagneticSpringInitialize.CIRCLE);
		}
	};
	Action ac4 = new TextAction("Init Random"){
		public void actionPerformed(ActionEvent event){
			setData(MagneticSpringInitialize.RANDOM);
		}
	};
	
    void setData(int nInitializeType){
		clear();
		Vector node = new Vector();
		Vector edge = new Vector();

		/**/
        int size = community_set.size();
        
        for(int i=0;i<size;i++){
            Node n = new Node((String)community_set.get(i));
            node.add(n);
        }
        
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                Object obj = matrix.getValue((String)community_set.get(i),(String)community_set.get(j));
                if(obj!=null){
//                    Edge e = new Edge(i,j,Edge.TYPE_01,m_bCrossLine);
                    int weight = ((Double)obj).intValue();
                    if(weight >= 1){
                        Edge e = new Edge(i,j,Edge.TYPE_01,true);
                        
                        //ノードを表示するように設定
                        ((Node)node.get(i)).isValid = true;
                        ((Node)node.get(j)).isValid = true;
                        
                        edge.add(e);    
                    }
                }
            }
        }
        
//		for(int cnt=0;cnt<40;cnt++){
//			Node n = new Node(""+(cnt+1));
//			node.add(n);
//		}
//        
//		final int edgelist1[][] = {
//			{ 0, 1},{ 1, 2},{ 2, 3},{ 4, 5},{ 5, 6},{ 6, 7},{ 7, 8},{ 8, 9},{ 9,10},
//			{11,12},{16,17},{17,18},{18,19},{21,22},{24,25},{26,27},{27,28},{28,29},
//			{32,33},{34,35},{35,36},{38,39},
//		};
        
//		final int edgelist2[][] = {
//			{ 0,16},{ 1, 4},{ 4,26},{ 5,11},{ 6,13},{ 7,21},{ 8,14},{ 9,24},{10,15},
//			{16,20},{18,23},{21,34},{24,30},{26,32},{28,31},{32,37},{34,38},
//		};

//		for(int cnt=0;cnt<edgelist1.length;cnt++){
//			Edge e = new Edge(edgelist1[cnt][0],edgelist1[cnt][1],Edge.TYPE_01,m_bCrossLine);
//			edge.add(e);
//		}
        
//		for(int cnt=0;cnt<edgelist2.length;cnt++){
//			Edge e = new Edge(edgelist2[cnt][0],edgelist2[cnt][1],Edge.TYPE_02,m_bCrossLine);
//			edge.add(e);
//		}
		m_panel.setData(node,edge,nInitializeType);
		m_panel.repaint();
	}
	void createDecisionTree(){
		m_panel.relax();
		return;
	}
	public void clear() {
		m_panel.clear();
		m_panel.repaint();
	}
}

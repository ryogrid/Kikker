package jp.ryo.informationPump.server.debug.magnetic;

// Copyright (C) 2000 by Toyohisa Nakada. All Rights Reserved.
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import java.util.*;

/**
 * Magnetic-Sprint Model
 * Initialize�N���X
 * �s��Ȃ���΂����Ȃ��d��
 *  (1) node�ɔԍ�(setNumber),�ꏊ(setPosition),�Œ�(setFixed)���w�肷��B
 *  (2) edge�ɍœK�Ȓ���(setLength0)���w�肷��B
 *  (3) node�̗אڃ��X�g���쐬����B(setNearNode)
 * @author ���c�L�v
 */
public class MagneticSpringInitialize {
	/** �G�b�W�̍œK�Ȓ������v�Z����Ƃ��ɁA�p�l���̉��܂��́A�c�̒����̂���
	 *  ���������̒l���A�ȉ��̒l�Ŋ������l���g�p����B����āA�ȉ��̒l���傫
	 *  ���Ȃ�ƁA�G�b�W�̍œK�Ȓ����͒Z���Ȃ�B */
	static final int m_length0Divide = 8;
	/** �T�[�N���������̉~�̔��a�����肷�邽�߂̒l�B�p�l���̉��܂��́A�c�̒�
	 *  ���̂����A�Z�������A�ȉ��̒l�Ŋ������l�𔼌a�Ƃ���B�Ⴆ�΂Q���̏ꍇ
	 *  �p�l���̉��܂��͏c�̒Z�����̔����̒��������a�ƂȂ�B */
	static final double m_circleRDivide = 1.9;

	public static final int RANDOM = 0;
	public static final int CIRCLE = 1;

	static boolean init(Vector node,Vector edge,Dimension size,int type){
		switch(type){
		case RANDOM:
			return random(node,edge,size);
		case CIRCLE:
			return circle(node,edge,size);
		default:
			return false;
		}
	}

	static boolean random(Vector node,Vector edge,Dimension size){
		int con = 0;
		for(Enumeration en=node.elements();en.hasMoreElements();con++){
			Node nd = (Node)en.nextElement();
			nd.setNumber(con);
			nd.setPosition((int)(Math.random()*size.width),
				(int)(Math.random()*size.height));
			nd.setFixed(false);
		}
		for(Enumeration ee=edge.elements();ee.hasMoreElements();){
			Edge ed = (Edge)ee.nextElement();
//			ed.setLength0((size.height > size.width ? size.width : size.height)/m_length0Divide);
			ed.setLength0(1);
			Node from = ed.getNodeFrom(node);
			Node to = ed.getNodeTo(node);
			from.setNearNode(to.getNumber());
			to.setNearNode(from.getNumber());
		}
		return true;
	}
	static boolean circle(Vector node,Vector edge,Dimension size){
		double radinit = 2*Math.PI/node.size();
		int xcenter = size.width/2;
		int ycenter = size.height/2;
		int r = size.width>size.height ? size.height : size.width;
		r = (int)((double)r/m_circleRDivide);
//System.out.println("radinit = "+radinit+" xcenter = "+xcenter+" ycenter = "+ycenter+" r = "+r);
		CVector vecinit = new CVector(r,0);
		CVector vec = new CVector(vecinit);
		double  rad = -radinit;
		for(int cnt=0;cnt<node.size();cnt++){
			Node n = (Node)node.get(cnt);
			n.setNumber(cnt);
			n.setPosition((int)(xcenter+vec.m_x),(int)(ycenter+vec.m_y));
			n.setFixed(false);

			vec = vecinit.rotate(rad);
			rad -= radinit;
		}
		for(Enumeration ee=edge.elements();ee.hasMoreElements();){
			Edge ed = (Edge)ee.nextElement();
//			ed.setLength0((size.height > size.width ? size.width : size.height)/m_length0Divide);
			ed.setLength0(1);
			Node from = ed.getNodeFrom(node);
			Node to = ed.getNodeTo(node);
			from.setNearNode(to.getNumber());
			to.setNearNode(from.getNumber());
		}
		return true;
	}
}
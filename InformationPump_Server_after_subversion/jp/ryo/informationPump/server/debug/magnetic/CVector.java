package jp.ryo.informationPump.server.debug.magnetic;

// Copyright (C) 2000 by Toyohisa Nakada. All Rights Reserved.
/*
import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.net.*;
import java.awt.image.*;
import javax.swing.*;
import java.util.*;
*/

/**
 * �x�N�g�����������C�u�����N���X<br>
 * �{�C�h�A�v���b�g�Ɉˑ����Ă��Ȃ��̂ōė��p���\�B<br>
 * java.util.Vector�Ƃ͑S�����֌W�B
 * @author ���c�L�v
 */
class CVector {
	/** �����o�ϐ� */
	double m_x;
	/** �����o�ϐ� */
	double m_y;
	/** �f�t�H���g�R���X�g���N�^
	 */
	CVector(){
	}
	/** �l���Z�b�g����B�f�t�H���g�R���X�g���N�^���g�p�����ꍇ�A�K����
	 * ����R�[������B */
	void setValue(double x,double y){
		m_x = x;
		m_y = y;
	}
	/** �R���X�g���N�^
	 * @param x x���l
	 * @param y y���l
	 */
	CVector(double x,double y){
		m_x = x;
		m_y = y;
	}
	/** �R���X�g���N�^
	 */
	CVector(CVector vec){
		m_x = vec.m_x;
		m_y = vec.m_y;
	}
	/** �������֐��B�������͂O�N���A */
	void init(){
		m_x = m_y = 0;
	}
	/** ����������Ă���x�N�g���̏ꍇ true��Ԃ� */
	boolean isInit(){
		return (m_x==0 && m_y==0)?true:false;
	}
	/** �x�N�g���̃v���X <BR>
	 *  JAVA�ł̉��Z�q�̃I�[�o�[���[�h���@��������΁A������ɕύX����B
	 */
	CVector plus(CVector cmp){
		m_x += cmp.m_x;
		m_y += cmp.m_y;
		return this;
	}
	/** �x�N�g���̎n�_�𒆐S�ɉ�]�����x�N�g�������߂� */
	CVector rotate(double rad){
		/* ��]�s���
		   | c -s ||x|
		   | s  c ||y|
		*/
		double c = Math.cos(rad);
		double s = Math.sin(rad);
		return new CVector(m_x*c-m_y*s,m_x*s+m_y*c);
	}
	/** ������Ԃ��B
	 */
	double getLength(){
		return Math.sqrt(Math.pow(m_x,2)+Math.pow(m_y,2));
	}
	/** ���݂�x,y�̒l���g�p���āA�p�x(radians)��Ԃ�
	 * @return �p�x(radians)
	 */
	double getRad(){
		// �Ԃ��p�x�͈̔͂� 0 <= rad < 2*Math.PI
		if(m_y == 0){
			if(m_x >= 0)
				return 0;
			else
				return Math.PI;
		}else if(m_x == 0){
			if(m_y >= 0)
				return Math.PI/2;
			else
				return 3*Math.PI/2;
		}
		double rad = Math.acos(m_x/Math.sqrt(Math.pow(m_x,2)+Math.pow(m_y,2)));
		if(m_y < 0)
			rad = (2*Math.PI)-rad;
		return rad;
	}
	/** �����Ƃ��ė^����ꂽ�x�N�g���ɑ΂��Ď����̃x�N�g�����v���X����
	 * �ɉ�]����Ίp�x���߂Â����A�}�C�i�X�����ɉ�]����΋߂Â������
	 * ���B<br>
	 * �Ⴆ�΁A�����x�N�g����(1,1) �����̃x�N�g����(1,-1)�̏ꍇ�A������
	 * �x�N�g�����}�C�i�X�����ɉ�]������ƁA�����x�N�g���ɋ߂Â��̂ł�
	 * �̊֐��̓}�C�i�X���������� -1 ��Ԃ��B
	 * @param vec �ΏۂƂȂ�x�N�g��
	 * @return 1�̏ꍇ�̓v���X�����A2�̏ꍇ�̓}�C�i�X�����B
	 */
	int towordPlusMinus(CVector vec){
		if((m_x == 0 && m_y == 0) ||
			(vec.m_x == 0 && vec.m_y == 0)){
			return 0;
		}
		int nDiff;
		switch(nDiff = diffWorld(vec)){
		case 0:
		case 2:
			double x = ((double)vec.m_y/vec.m_x)-((double)m_y/m_x);
			if(x == 0)
				return 0;
			else{
				if(nDiff==0)
					return x>0?1:-1;
				else
					return x>0?-1:1;
			}
		case -1:
			return -1;
		case 1:
			return 1;
		}
		return 0;	// bug
	}
	/** {@link #getWorld()}�ɂ�苁�߂���ی��̍��������߂�B�����ی�
	 * �̏ꍇ�A�����͂O�B�ׂ̏ی��Ɍ݂�������ꍇ�A�P�B�Ίp����Ɍ݂���
	 * �ی������݂���ꍇ�A�Q�ƂȂ�B
	 * @param vec ��r�Ώۂ̃x�N�g��
	 * @return �����l(0-2)
	 */
	int diffWorld(CVector vec){
		int w = getWorld();
		int w2 = vec.getWorld();
		if(w == w2)
			return 0;
		else if(Math.abs(w-w2)==2)
			return 2;
		else if(Math.max(w,w2)-Math.min(w,w2)==1)
			return 1;
		else
			return -1;
	}
	/**
	 * m_x,m_y���ȉ���4�̏ی��ɕ�����<br>
	 * <pre>
	 * 1.  ��      2.   ��    3.  |      4.  |
	 *     | |        | |         |          |
	 *     +-��      ��-+-     ��-+-        -+-��
	 *     |            |       | |          | |
	 *                            ��         ��
	 * </pre>
	 * @return �ی��̔ԍ�(1-4)
	 */
	int getWorld(){
		if(m_x > 0 && m_y >= 0){
			return 1;
		}else if(m_x <= 0 && m_y > 0){
			return 2;
		}else if(m_x < 0 && m_y <= 0){
			return 3;
		}else if(m_x >= 0 && m_y < 0){
			return 4;
		}
		return 0;// bug
	}
	static public double radToDeg(double rad){
		return rad*180/Math.PI;
	}
	static public double degToRad(double deg){
		return deg*Math.PI/180;
	}
}
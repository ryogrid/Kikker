package jp.ryo.informationPump.server.debug.magnetic;

// Copyright (C) 2000 by Toyohisa Nakada. All Rights Reserved.
import javax.swing.*;

/**
 * �f�[�^�}�C�j���O�@�e�X�g�A�v���P�[�V����<br>
 * Decision Tree�p�l���N���X
 * @author ���c�L�v
 */
public class MagneticSpringApplet extends JApplet {
	MagneticSpringRootPanel m_panel;

	/**
	 * �������֐�
	 */
	public void init(){
	}
	/**
	 * �A�v���b�g�̃X�^�[�g
	 */
	public void start(){
		m_panel = new MagneticSpringRootPanel();
		getContentPane().add(m_panel);
	}
	/**
	 * �A�v���b�g�̃X�g�b�v
	 */
	public void stop(){
		m_panel = null;
	}
	/**
	 * �A�v���b�g�̏I������
	 */
	public void destroy(){
	}
}

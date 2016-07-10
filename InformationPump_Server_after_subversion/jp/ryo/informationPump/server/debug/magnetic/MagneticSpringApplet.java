package jp.ryo.informationPump.server.debug.magnetic;

// Copyright (C) 2000 by Toyohisa Nakada. All Rights Reserved.
import javax.swing.*;

/**
 * データマイニング　テストアプリケーション<br>
 * Decision Treeパネルクラス
 * @author 中田豊久
 */
public class MagneticSpringApplet extends JApplet {
	MagneticSpringRootPanel m_panel;

	/**
	 * 初期化関数
	 */
	public void init(){
	}
	/**
	 * アプレットのスタート
	 */
	public void start(){
		m_panel = new MagneticSpringRootPanel();
		getContentPane().add(m_panel);
	}
	/**
	 * アプレットのストップ
	 */
	public void stop(){
		m_panel = null;
	}
	/**
	 * アプレットの終了処理
	 */
	public void destroy(){
	}
}

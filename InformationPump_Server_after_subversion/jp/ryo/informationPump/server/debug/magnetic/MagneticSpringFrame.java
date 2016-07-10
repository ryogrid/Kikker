package jp.ryo.informationPump.server.debug.magnetic;

// Copyright (C) 2000 by Toyohisa Nakada. All Rights Reserved.
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;

/**
 * @author ’†“c–L‹v
 */
public class MagneticSpringFrame extends JFrame {
	MagneticSpringRootPanel m_panel;
	MagneticSpringFrame(){
		super();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}});

		m_panel = new MagneticSpringRootPanel();
		getContentPane().add(m_panel,BorderLayout.CENTER);
	}
	public Dimension getPreferredSize(){
		return(new Dimension(600,550));
	}
	public static void main(String args[]) {
		MagneticSpringFrame me = new MagneticSpringFrame();
		me.setTitle("Magnetic-Spring Model");
		me.pack();
		me.setSize(me.getPreferredSize());
		me.show();
	}
}

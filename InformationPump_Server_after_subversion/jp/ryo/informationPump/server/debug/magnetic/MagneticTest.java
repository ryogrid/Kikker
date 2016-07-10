package jp.ryo.informationPump.server.debug.magnetic;

import javax.swing.JFrame;

public class MagneticTest extends JFrame {
    private static MagneticSpringRootPanel m_panel;
    
    public static void main(String[] args) {
        MagneticTest test = new MagneticTest();
        m_panel = new MagneticSpringRootPanel();
        test.getContentPane().add(m_panel);
        test.setVisible(true);
    }
    
    
    
}

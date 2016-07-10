package jp.ryo.informationPump.server.debug;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import javax.swing.JFrame;

import sun.java2d.pipe.TextRenderer;
/*
 * �쐬��: 2005/12/15
 *
 * ���̐������ꂽ�R�����g�̑}�������e���v���[�g��ύX���邽��
 * �E�B���h�E > �ݒ� > Java > �R�[�h���� > �R�[�h�ƃR�����g
 */

import jp.ryo.informationPump.server.debug.magnetic.MagneticSpringRootPanel;
import jp.ryo.informationPump.server.util.SparseMatrix;

public class GraphOutputFrame extends JFrame {
    private SparseMatrix matrix;
    private ArrayList community_set;
    private int coodinates[][];
    private final int FRAME_SIZE = 700;
    private final int POINT_SIZE = 10;
    
    private final int FRAME_EDGE_MARGIN = 50;
    
    public GraphOutputFrame(String title,SparseMatrix matrix,ArrayList community_set){
        super(title);
//        this.setSize(FRAME_SIZE,FRAME_SIZE);
//        this.matrix = matrix;
//        this.community_set = community_set;
//        
//        int comm_size = community_set.size();
//        coodinates = new int[comm_size][2];
//        
//        for(int i=0;i<comm_size;i++){
//            coodinates[i][0] = (int)(Math.random()*((double)FRAME_SIZE-FRAME_EDGE_MARGIN*2));
//            coodinates[i][1] = (int)(Math.random()*((double)FRAME_SIZE-FRAME_EDGE_MARGIN*2));
//        }
        
        this.getContentPane().add(new MagneticSpringRootPanel(matrix,community_set));
    }
    
//    public void paint(Graphics g){
//        Graphics2D g2 = (Graphics2D)g;
//        
//        int frame_width = this.getWidth();
//        int frame_height = this.getHeight();
//        
//        int size = coodinates.length;
//
//        for(int i=0;i<size;i++){
//            for(int j=0;j<size;j++){
//                Object obj = matrix.getValue((String)community_set.get(i),(String)community_set.get(j));
//                if(obj!=null){
//                    double weight = ((Double)obj).doubleValue();
//                    BasicStroke tmpStroke = new BasicStroke((float)(weight/10.0));
//                    g2.setStroke(tmpStroke);
//                    g2.setColor(Color.GRAY);
//                    g2.draw(new Line2D.Double(FRAME_EDGE_MARGIN+coodinates[i][0],FRAME_EDGE_MARGIN+coodinates[i][1],FRAME_EDGE_MARGIN+coodinates[j][0],FRAME_EDGE_MARGIN+coodinates[j][1]));
//                }
//            }
//        }
//        
//        for(int i=0;i<size;i++){
//            g2.fill(new Ellipse2D.Double(FRAME_EDGE_MARGIN+ coodinates[i][0]-POINT_SIZE/2,FRAME_EDGE_MARGIN+coodinates[i][1]-POINT_SIZE/2,POINT_SIZE,POINT_SIZE));
//            g2.setColor(Color.RED);
//            g2.drawString((String)community_set.get(i),FRAME_EDGE_MARGIN+coodinates[i][0],FRAME_EDGE_MARGIN+coodinates[i][1] + 20);
//        }
//    }
}
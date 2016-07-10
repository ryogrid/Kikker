package jp.ryo.informationPump.server.debug.magnetic;

// Copyright (C) 2000 by Toyohisa Nakada. All Rights Reserved.
import java.util.*;

/**
 * Magnetic-Sprint Model
 * @author ���c�L�v
 */
public class MagneticSpringMagnet {
	static public boolean force(Vector node,Vector edge){
		/** ����萔 (�S�̂ɉe��)*/
		final double effectCm		= 0.2;
		/** ����萔 (�ڑ����̒����ɉe��)*/
		final double effectAlpha	= 0.0;
		/** ����萔 (���͐��Ƃ̊p�x�̍����ɉe��)*/
		final double effectBeta		= 1.0;

		CVector mag1 = new CVector(1,0);
		CVector mag2 = new CVector(0,1);
		for(Enumeration ee=edge.elements();ee.hasMoreElements();){
			Edge ed = (Edge)ee.nextElement();
			CVector mag = null;
			switch(ed.getType()){
			case Edge.TYPE_01:
				mag = mag1;
				break;
			case Edge.TYPE_02:
				mag = mag2;
				break;
			default:
				break;
			}
			Node nf = ed.getNodeFrom(node);
			Node nt = ed.getNodeTo(node);
			if(mag != null && (nf.getFixed()==false || nt.getFixed()==false)){
				double xcenter = ((double)nt.m_x+nf.m_x)/2;
				double ycenter = ((double)nt.m_y+nf.m_y)/2;
				CVector vec = new CVector((double)nt.m_x-xcenter,(double)nt.m_y-ycenter);

				double raddistance = vec.getRad() - mag.getRad();
				// raddistance �͈̔͂́@-Math.PI <= divRad < Math.PI
				if(raddistance < -Math.PI)
					raddistance += 2*Math.PI;
				else if(raddistance >= Math.PI)
					raddistance -= 2*Math.PI;

//System.out.println("raddistance = "+CVector.radToDeg(raddistance));
				double rad = effectCm * Math.pow(vec.getLength(),effectAlpha) *
					Math.pow(raddistance,effectBeta);
				if(raddistance < 0 && rad > 0)
					rad *= -1;
				CVector nvec = vec.rotate(rad);
//System.out.println("rad="+rad+" nvec.m_x="+nvec.m_x+" nvec.m_y="+nvec.m_y+" vec.m_x="+vec.m_x+" vec.m_y"+vec.m_y);
//if(Math.abs(nvec.getLength()-vec.getLength()) > 1)
//System.out.println("nvec.getLength() = "+nvec.getLength()+" vec.getLength() = "+vec.getLength());

				if(nt.getFixed() == false){
//System.out.println("nt.m_x = "+nt.m_x+" xcenter = "+xcenter+" nvec.m_x = "+nvec.m_x+" vec.m_x = "+vec.m_x);
//System.out.println("m_dx = "+(nt.m_x - (xcenter+nvec.m_x))+" m_dy = "+(nt.m_y - (ycenter+nvec.m_y)));
					nt.m_dx += ((double)nt.m_x - (xcenter+(double)nvec.m_x));
					nt.m_dy += ((double)nt.m_y - (ycenter+(double)nvec.m_y));
//if(nt.m_dx != 0 || nt.m_dy != 0)
//System.out.println("nt.m_dx="+nt.m_dx+" nt.m_dy="+nt.m_dy);
				}
				if(nf.getFixed() == false){
					nf.m_dx += ((double)nf.m_x - (xcenter-(double)nvec.m_x));
					nf.m_dy += ((double)nf.m_y - (ycenter-(double)nvec.m_y));
//if(nf.m_dx != 0 || nf.m_dy != 0)
//System.out.println("nf.m_dx="+nf.m_dx+" nf.m_dy="+nf.m_dy);
				}
			}
		}
		return true;
	}
}
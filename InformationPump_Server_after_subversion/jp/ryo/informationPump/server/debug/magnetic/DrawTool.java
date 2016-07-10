package jp.ryo.informationPump.server.debug.magnetic;

// Copyright (C) 2000 by Toyohisa Nakada. All Rights Reserved.
import java.util.*;
import java.awt.*;

class DrawTool {
	static FontMetrics fmetrics = null;
	/**
	 * {@link #drawString(Graphics g,String str,int x,int y,boolean center)}によって
	 * 描画される図形内に、引数のcx,cyが入っているかを判定する。
	 */
	static boolean drawStringTest(Graphics g,String str,int x,int y,boolean center,int cx,int cy){
		DrawData data = new DrawData(g,str,x,y,center);
		if(cx >= data.xPosition && cx <= (data.xPosition+data.nPanelWidth+data.nFrameSize*2) &&
			cy >= data.yPosition && cy <= (data.yPosition+data.nPanelHeight+data.nFrameSize*2) )
			return true;
		return false;
	}
	/**
	 * 画面に文字を枠付きで表示する。
	 * @param g Graphics
	 * @param g 表示する文字列。指定可能な特殊文字は \n
	 */
	static void drawString(Graphics g,String str,int x,int y,boolean center){
		DrawData data = new DrawData(g,str,x,y,center);

		Color oldColor = g.getColor();
		g.setColor(data.frameColor);
		g.fillRect(data.xPosition,data.yPosition,
			data.nPanelWidth+data.nFrameSize*2,
			data.nPanelHeight+data.nFrameSize*2);
		g.setColor(data.innerColor);
		g.fillRect(data.nFrameSize+data.xPosition,data.nFrameSize+data.yPosition,
			data.nPanelWidth,data.nPanelHeight);
		g.setColor(oldColor);

		for(int cnt=0;cnt<data.stringList.size();cnt++){
			String dstr = (String)data.stringList.get(cnt);
			g.drawString(dstr,data.xPosition+data.xBlank+data.nFrameSize,
				data.yPosition+data.yBlank+data.yInterval*(cnt+1));
		}
	}
	static class DrawData {
		int xBlank = 2;
		int yBlank = 2;
		int nFrameSize = 2;
		
		int xPosition;
		int yPosition;
		int nPanelWidth;
		int nPanelHeight;
		int yInterval;
		Color frameColor = Color.black;
		Color innerColor = Color.white;
		Vector stringList;
		
		DrawData(Graphics g,String str,int x,int y,boolean center){
			if(fmetrics == null)
				fmetrics = g.getFontMetrics();
			xPosition = x;
			yPosition = y;
			yInterval = fmetrics.getHeight();
			nPanelWidth = 0;
			nPanelHeight = 0;
			stringList = new Vector();

			String strObj = new String(str);
			int nIndex;
			do{
				nIndex = strObj.indexOf('\n');
				String nstr;
				if(nIndex == -1){
					nstr = new String(strObj);
				}else{
					nstr = strObj.substring(0,nIndex);
					strObj = strObj.substring(nIndex+1,strObj.length());
				}
				int nWidth = fmetrics.stringWidth(nstr);
				nPanelWidth = nPanelWidth < nWidth ? nWidth : nPanelWidth;
				stringList.add(nstr);
			}while(nIndex != -1);

			nPanelWidth += xBlank*2;
			nPanelHeight = yInterval*stringList.size()+yBlank*2;

			if(center == true){
				xPosition -= (nPanelWidth+nFrameSize*2)/2;
				yPosition -= (nPanelHeight+nFrameSize*2)/2;
			}
		}
	};
}


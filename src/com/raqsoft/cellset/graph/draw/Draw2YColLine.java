package com.raqsoft.cellset.graph.draw;

import java.awt.*;
import java.util.ArrayList;

import com.raqsoft.cellset.graph.*;
import com.raqsoft.chart.Consts;
import com.raqsoft.chart.Utils;

/**
 * ˫������ͼ��ʵ��
 * @author Joancy
 *
 */
public class Draw2YColLine extends DrawBase {
	
	/**
	 * ʵ�ֻ�ͼ����
	 */
	public void draw(StringBuffer htmlLink) {
		drawing(this, htmlLink);
	}

	/**
	 * ���ݻ�ͼ����db��ͼ��������ͼ��ĳ����Ӵ���htmlLink
	 * @param db ����Ļ�ͼ����
	 * @param htmlLink �����ӻ���
	 */
	public static void drawing(DrawBase db,StringBuffer htmlLink) {
//		ê�����غ�ʱ��˭��ǰ�棬��������ҵ�˭�����ڵ�С�������ê���������ǰ�档 xq 2017��11��13��
		StringBuffer colLink = new StringBuffer();
		int serNum = DrawCol.drawing(db,colLink);
		
//		˫������ͼʱ������ԭ���غ�
		db.gp.isOverlapOrigin = false;
		Draw2Y2Line.drawY2Line(db, serNum, htmlLink);
		db.outPoints();
		db.outLabels();
		if(htmlLink!=null){
			htmlLink.append(colLink.toString());
		}
	}

}

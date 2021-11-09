package com.raqsoft.cellset.graph.draw;

import java.awt.*;
import java.util.*;

import com.raqsoft.cellset.graph.*;
import com.raqsoft.cellset.graph.config.IGraphProperty;
import com.raqsoft.chart.Consts;
import com.raqsoft.chart.Utils;
import com.raqsoft.common.*;
/**
 * �ѻ�����ͼ��ʵ��
 * @author Joancy
 *
 */

public class DrawBarStacked extends DrawBase {
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
		//�ٸĶ����룬ͬ������Ҫ�õ���ʵ��
		GraphParam gp = db.gp;
		ExtGraphProperty egp = db.egp;
		Graphics2D g = db.g;
		
		double seriesWidth;
		double coorWidth;
		double categorySpan;
		double delx;
		int x, y;

		gp.maxValue = gp.maxPositive;
		gp.minValue = gp.minNegative;

		gp.coorWidth = 0;

		db.initGraphInset();

		db.createCoorValue();

		db.drawLegend(htmlLink);
		db.drawTitle();
		db.drawLabel();
		db.keepGraphSpace();

		db.adjustCoorInset();
		gp.graphRect = new Rectangle(gp.leftInset, gp.topInset, gp.graphWidth
				- gp.leftInset - gp.rightInset, gp.graphHeight - gp.topInset
				- gp.bottomInset);
		if (gp.graphRect.width < 10 || gp.graphRect.height < 10) {
			return;
		}

		if (gp.coorWidth < 0 || gp.coorWidth > 10000) {
			gp.coorWidth = 0;
		}

		int serNum = 1;
		if (egp.category2 != null) {
			serNum = 2;
		}
		
		if (gp.barDistance > 0) {
			double maxCatSpan = (gp.graphRect.height - serNum * gp.catNum* 1.0f)
					/ (gp.catNum + 1.0f);
			if (gp.barDistance <= maxCatSpan) {
				categorySpan = gp.barDistance;
			} else {
				categorySpan = maxCatSpan;
			}
			seriesWidth = (gp.graphRect.height - (gp.catNum + 1) * categorySpan)
					/ (serNum * gp.catNum);
		} else {
			seriesWidth = (gp.graphRect.height / (((gp.catNum + 1)
					* gp.categorySpan / 100.0) + gp.coorWidth/200+gp.catNum * serNum));
			categorySpan = (seriesWidth * (gp.categorySpan / 100.0));
		}
		
		coorWidth = (seriesWidth * (gp.coorWidth / 200.0));
		delx = (gp.graphRect.width - coorWidth) / gp.tickNum;
		gp.gRect1 = new Rectangle(gp.graphRect);
		gp.gRect2 = new Rectangle(gp.graphRect);
		/* �������� */
		db.drawGraphRect();
		/* ��X�� */
		for (int i = 0; i <= gp.tickNum; i++) {
			db.drawGridLineV(delx, i);

			// ��x���ǩ
			Number coorx = (Number) gp.coorValue.get(i);
			String scoorx = db.getFormattedValue(coorx.doubleValue());

			x = (int) (gp.gRect1.x + i * delx);// - TR.width / 2
			y = gp.gRect1.y + gp.gRect1.height + gp.tickLen;// + TR.height;
			gp.GFV_XLABEL.outText(x, y, scoorx);
			// ���û���
			if (coorx.doubleValue() == gp.baseValue + gp.minValue) {
				gp.valueBaseLine = (int) (gp.gRect1.x + i * delx);
			}
		}

		// ��������
		db.drawWarnLineH();

		/* ��Y�� */
		ArrayList cats = egp.categories;
		int cc = cats.size();
		Color c;
		for (int i = 0; i < cc; i++) {
			ExtGraphCategory egc = (ExtGraphCategory) cats.get(i);
			double dely = (i + 1) * categorySpan + i * seriesWidth
					* serNum + seriesWidth * serNum / 2.0;
			boolean vis = i % (gp.graphXInterval + 1) == 0;
			if (vis) {
				c = egp.getAxisColor(GraphProperty.AXIS_LEFT);
				Utils.setStroke(g, c, Consts.LINE_SOLID, 1.0f);
				db.drawLine(gp.gRect1.x, gp.gRect1.y + gp.gRect1.height - (int)dely,
						gp.gRect1.x - gp.tickLen, gp.gRect1.y
								+ gp.gRect1.height - (int)dely,c);
				db.drawGridLineCategory( gp.gRect1.y + (int)dely );
			}

			String value = egc.getNameString();
			x = gp.gRect1.x - gp.tickLen;// - TR.width
			y = gp.gRect1.y + (int)dely;// + TR.height / 2;
			gp.GFV_YLABEL.outText(x, y, value, vis);

			int positiveBase = gp.valueBaseLine;
			int negativeBase = gp.valueBaseLine;
			double lb;

			if (egp.category2 == null) {
				lb = (gp.gRect1.y + (i + 1) * categorySpan + i * seriesWidth);
				drawSeries(0, gp.serNames,egc,
						delx, db, lb, positiveBase,
						seriesWidth, htmlLink, negativeBase,
						coorWidth, vis);
			}else{
				lb = (gp.gRect1.y + (i + 1) * categorySpan + (i
						* serNum + 0)
						* seriesWidth);
				drawSeries(0, gp.serNames,egc,
						delx, db, lb, positiveBase,
						seriesWidth, htmlLink, negativeBase,
						coorWidth, vis);
				lb = (gp.gRect1.y + (i + 1) * categorySpan + (i
						* serNum + 1)
						* seriesWidth);
				egc = (ExtGraphCategory) egp.category2.get(i);
				drawSeries(gp.serNames.size(), gp.serNames2,egc,
						delx, db, lb, positiveBase,
						seriesWidth, htmlLink, negativeBase,
						coorWidth, vis);
			}

		}

		/* �ػ�һ�»��� */
		db.outLabels();
		
		db.drawLine(gp.valueBaseLine, gp.gRect1.y, gp.valueBaseLine, gp.gRect1.y
				+ gp.gRect1.height, egp.getAxisColor(GraphProperty.AXIS_BOTTOM));
	}
	
	private static void drawSeries(int serNumBase, Vector serNames,ExtGraphCategory egc,
			double delx, DrawBase db, double dlb, int positiveBase,
			double seriesWidth, StringBuffer htmlLink, int negativeBase,
			double coorWidth, boolean vis) {
		GraphParam gp = db.gp;
		ExtGraphProperty egp = db.egp;
		Graphics2D g = db.g;
		ArrayList<ValueLabel> labelList = db.labelList;
		int lb = (int)Math.round(dlb);
		int bs = Consts.LINE_SOLID;
		float bw = 1.0f;
		int serNum = serNames.size();

		for (int j = 0; j < serNum; j++) {
			ExtGraphSery egs = egc.getExtGraphSery(serNames.get(j));
			if (egs.isNull()) {
				continue;
			}
			double val = egs.getValue();
			double tmp = val - gp.baseValue;
			int len = (int) (delx * gp.tickNum * (tmp - gp.minValue) / (gp.maxValue * gp.coorScale));

			if (len == 0) {
				continue;
			}

			Color bc = egp.getAxisColor(GraphProperty.AXIS_COLBORDER);
			Color tmpc = db.getColor(j+serNumBase);
			if (len > 0) {
				Utils.draw2DRect(g, positiveBase, lb, len,
						(int) (seriesWidth), bc, bs, bw,
						egp.isDrawShade(), egp.isRaisedBorder(),
						db.getTransparent(), db.getChartColor(tmpc), false);
				db.htmlLink(positiveBase, lb, len,
						(int) (seriesWidth), htmlLink, egc.getNameString(),
						egs);
			} else {
				Utils.draw2DRect(g, negativeBase + len, lb,
						Math.abs(len), (int) (seriesWidth), bc, bs, bw,
						egp.isDrawShade(), egp.isRaisedBorder(),
						db.getTransparent(), db.getChartColor(tmpc), false);
				db.htmlLink(negativeBase + len, lb,
						Math.abs(len), (int) (seriesWidth), htmlLink,
						egc.getNameString(), egs);
			}

			ValueLabel vl = null;
			String percentFmt = null;
			if (gp.dispValueType == 3 && vis) { // �����ʾ�ٷֱ�
				if (StringUtils.isValidString(gp.dataMarkFormat)) {
					percentFmt = gp.dataMarkFormat;
				} else {
					percentFmt = "0.00%";
				}
			}

			if (len > 0) {
				String sval = null;
				if (percentFmt != null) {
					sval = db.getFormattedValue(
							egs.getValue() / egc.getPositiveSumSeries(),
							percentFmt);
				}else if(gp.dispValueType == IGraphProperty.DISPDATA_TITLE){
					sval = egs.getTips();
				}
				
				if(StringUtils.isValidString( sval )){
					vl = new ValueLabel(sval, new Point(positiveBase+len/2, (int) (lb-seriesWidth/2)), gp.GFV_VALUE.color,
							GraphFontView.TEXT_ON_CENTER);
				}
				
				positiveBase += len;
			} else {
				String sval = null;
				if (percentFmt != null) {
					sval = db.getFormattedValue(
							egs.getValue() / egc.getNegativeSumSeries(),
							percentFmt);
				}else if(gp.dispValueType == IGraphProperty.DISPDATA_TITLE){
					sval = egs.getTips();
				}
				
				if(StringUtils.isValidString( sval )){
					vl = new ValueLabel(sval, new Point(negativeBase + len/2, (int) (lb-seriesWidth/2)), gp.GFV_VALUE.color,
							GraphFontView.TEXT_ON_CENTER);
				}
				
				negativeBase += len;
			}
			if(vl!=null){
				labelList.add(vl);
			}
		}

		// ��������ʾ��ֵ
		if (gp.dispStackSumValue && vis) {
			double val = db.getScaledValue(egc.getPositiveSumSeries(), true);
			String sval;
			ValueLabel vl = null;
			if (val > 0) {
				sval = db.getFormattedValue(val);
				int x = positiveBase + 3;
				int y = (int) lb - (int) (seriesWidth / 2);// + TR.height / 2;
				vl = new ValueLabel(sval, new Point(x, y), gp.GFV_VALUE.color,
						GraphFontView.TEXT_ON_RIGHT);
									
			}
			val = db.getScaledValue(egc.getNegativeSumSeries(), true);
			if (val < 0) {
				sval = db.getFormattedValue(val);
				int x = negativeBase - 3;// - TR.width
				int y = (int) lb - (int) (seriesWidth / 2);// + TR.height / 2;
				
				vl = new ValueLabel(sval, new Point(x, y), gp.GFV_VALUE.color,
						GraphFontView.TEXT_ON_LEFT);
				
			}
			if(vl!=null){
				labelList.add(vl);
			}
		}
		
	}
	
	
}

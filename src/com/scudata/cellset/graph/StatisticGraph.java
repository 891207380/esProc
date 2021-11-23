package com.scudata.cellset.graph;

import java.awt.*;
import java.awt.image.*;
import java.util.*;

import com.scudata.app.common.*;
import com.scudata.cellset.*;
import com.scudata.cellset.graph.config.*;
import com.scudata.cellset.graph.draw.*;
import com.scudata.common.*;
import com.scudata.dm.*;
import com.scudata.util.*;

import org.w3c.dom.*;
import java.io.*;
import java.lang.reflect.*;

import javax.swing.*;

/**
 * ����ͳ��ͼ�ļ���ʵ��
 * ����ͳ��ͼ�����ͼԪ��ͬ������ͳ��ͼ���ճ���ͳ��ͼ���ú��������ԣ�Ĭ������ϵ
 * ͼԪ�൱�ڻ�ľ�������ò�ͬͼԪ�Լ�����ϳ�������������ͳ��ͼ
 * @author Joancy
 *
 */
public class StatisticGraph {

	/**
	 * ����������ת��Ϊ��ͼǰ����չͼ������
	 * @param prop ��������
	 * @return ��չͼ������
	 */
	public static ExtGraphProperty calc1(PublicProperty prop) {
		ExtGraphProperty catMap = new ExtGraphProperty(prop);
		catMap.setXTitle(prop.getXTitle());
		catMap.setYTitle(prop.getYTitle());
		catMap.setGraphTitle(prop.getGraphTitle());
		catMap.setBarDistance(prop.getBarDistance());
		catMap.setTopData(prop.getTopData());
		catMap.setBackGraphConfig(prop.getBackGraphConfig());

		String str = prop.getDisplayDataFormat();
		if (StringUtils.isValidString(str)) {
			ArgumentTokenizer st = new ArgumentTokenizer(str, ';');
			if (st.hasMoreTokens()) {
				catMap.setDisplayDataFormat1(st.nextToken());
			}
			if (st.hasMoreTokens()) {
				catMap.setDisplayDataFormat2(st.nextToken());
			}
		}

		catMap.setLink(prop.getLink());
		catMap.setLinkTarget(prop.getLinkTarget());

		/** ͳ��ͼ����ɫ������ */
		Palette pltt = Palette.getDefaultPalette();
		str = prop.getColorConfig();
		if (StringUtils.isValidString(str)) {
			Palette pl = Palette.readColor(str);
			if (pl != null) {
				pltt = pl;
			}
		}
		catMap.setPalette(pltt);

		/** ͳ��ֵ��ʼֵ */
		str = prop.getYStartValue();
		if (StringUtils.isValidString(str)) {
			ArgumentTokenizer st = new ArgumentTokenizer(str, ';');
			if (st.hasMoreTokens()) {
				catMap.setYStartValue1(Double.parseDouble(st.nextToken()));
			}
			if (st.hasMoreTokens()) {
				catMap.setYStartValue2(Double.parseDouble(st.nextToken()));
			}
		}

		/** ͳ��ֵ����ֵ */
		str = prop.getYEndValue();
		if (StringUtils.isValidString(str)) {
			ArgumentTokenizer st = new ArgumentTokenizer(str, ';');
			if (st.hasMoreTokens()) {
				catMap.setYEndValue1(Double.parseDouble(st.nextToken()));
			}
			if (st.hasMoreTokens()) {
				catMap.setYEndValue2(Double.parseDouble(st.nextToken()));
			}
		}
		/** ͳ��ֵ��ǩ��� */
		str = prop.getYInterval();
		if (StringUtils.isValidString(str)) {
			ArgumentTokenizer st = new ArgumentTokenizer(str, ';');
			if (st.hasMoreTokens()) {
				catMap.setYInterval1(Double.parseDouble(st.nextToken()));
			}
			if (st.hasMoreTokens()) {
				catMap.setYInterval2(Double.parseDouble(st.nextToken()));
			}
		}

		/** ͳ��ֵ���ٿ̶��� */
		catMap.setYMinMarks(prop.getYMinMarks());
		catMap.setTitleMargin(prop.getTitleMargin());
		catMap.setXInterval(prop.getXInterval());
		return catMap;
	}

	private static ExtGraphCategory locateCategory(ArrayList list, String name) {
		for (int i = 0; i < list.size(); i++) {
			ExtGraphCategory egc = (ExtGraphCategory) list.get(i);
			if (egc.getName().equals(name)) {
				return egc;
			}
		}
		return null;
	}

	private static ArrayList demoCategories = null;

	private static void transferData(ExtGraphProperty egp, Table data) {
		if (data == null) {
			if (demoCategories == null) {
				demoCategories = new ArrayList();
				ExtGraphCategory egc = new ExtGraphCategory();
				egc.setName("A");
				ArrayList series = new ArrayList();
				egc.setSeries(series);
				ExtGraphSery egs = new ExtGraphSery();
				egs.setName("Series1");
				egs.setValue(new Integer(80));
				series.add(egs);
				demoCategories.add(egc);

				egc = new ExtGraphCategory();
				egc.setName("B");
				series = new ArrayList();
				egc.setSeries(series);
				egs = new ExtGraphSery();
				egs.setName("Series1");
				egs.setValue(new Integer(55));
				series.add(egs);
				demoCategories.add(egc);

				egc = new ExtGraphCategory();
				egc.setName("C");
				series = new ArrayList();
				egc.setSeries(series);
				egs = new ExtGraphSery();
				egs.setName("Series1");
				egs.setValue(new Integer(70));
				series.add(egs);
				demoCategories.add(egc);
			}

			egp.setCategories(demoCategories);
			return;
		}

		ArrayList categories = new ArrayList();
		GraphProperty gp = (GraphProperty) egp.getIGraphProperty();
		if (StringUtils.isValidString(gp.getSeries())) {
			for (int i = 1; i <= data.length(); i++) {
				Record r = data.getRecord(i);
				String cat = Variant.toString(r.getFieldValue(0));
				if (cat == null)
					continue;
				String ser = Variant.toString(r.getFieldValue(1));
				Object val = r.getFieldValue(2);
				ExtGraphCategory egc = locateCategory(categories, cat);
				if (egc == null) {
					egc = new ExtGraphCategory();
					egc.setName(cat);
					egc.setSeries(new ArrayList());
					categories.add(egc);
				}

				ExtGraphSery egs = new ExtGraphSery();
				egs.setName(ser);
				if (val != null) {
					if (val instanceof Number) {
						egs.setValue((Number) val);
					} else {
						try {
							egs.setValue(new Double(val.toString()));
						} catch (Exception x) {
						}
					}
				}
				egc.getSeries().add(egs);
			}
		} else {
			for (int i = 1; i <= data.length(); i++) {
				Record r = data.getRecord(i);
				String cat = Variant.toString(r.getFieldValue(0));
				if (cat == null)
					continue;
				Object val = r.getFieldValue(2);
				ExtGraphCategory egc = locateCategory(categories, cat);
				if (egc == null) {
					egc = new ExtGraphCategory();
					egc.setName(cat);
					egc.setSeries(new ArrayList());
					categories.add(egc);
				}
				ExtGraphSery egs = new ExtGraphSery();
				egs.setName("Series");
				if (val != null) {
					if (val instanceof Number) {
						egs.setValue((Number) val);
					} else {
						try {
							egs.setValue(new Double(val.toString()));
						} catch (Exception x) {
						}
					}
				}
				egc.getSeries().add(egs);
			}
		}
		egp.setCategories(categories);
	}


	/**
	 * ������ͼ����ָ����ʽתΪͼ������
	 * @param bi ����ͼ��
	 * @param imageFmt ͼƬ��ʽ
	 * @return ͼ������
	 * @throws Exception
	 */
	public static byte[] getImageBytes(BufferedImage bi, byte imageFmt)
			throws Exception {
		byte[] bytes = null;
		switch (imageFmt) {
		case GraphProperty.IMAGE_GIF:
			bytes = ImageUtils.writeGIF(bi);
			break;
		case GraphProperty.IMAGE_JPG:
			bytes = ImageUtils.writeJPEG(bi);
			break;
		case GraphProperty.IMAGE_PNG:
			bytes = ImageUtils.writePNG(bi);
			break;
		}
		return bytes;
	}

	private static byte[] getFileBytes(String picFile) {
		if (picFile == null || picFile.trim().length() == 0) {
			return null;
		}
		InputStream fis = null;
		try {
			File file = new File(picFile);
			if (file.exists()) { // ����·����ʾ���ļ�
				fis = new FileInputStream(picFile);
			} else {
				String paths[] = Env.getPaths();
				if (paths != null && paths.length > 0) {
					file = new File(paths[0], picFile);
					fis = new FileInputStream(file);
				}
			}

			if (fis == null) {
				return null;
			}
			return AppUtil.getStreamBytes(fis);
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * ִ�б���ͼ����
	 * @param g ͼ���豸
	 * @param egp ��չͼ������
	 * @param w ���
	 * @param h �߶�
	 */
	public static void drawBackGraph(Graphics2D g, ExtGraphProperty egp,
			int w, int h) {
		BackGraphConfig bgc = egp.getBackGraphConfig();
		if (bgc == null) {
			return;
		}
		byte[] b = getFileBytes(bgc.getValue());
		if (b != null) {
			bgc.setImageBytes(b);
		}

		byte[] backImage = bgc.getImageBytes();
		if (backImage == null) {
			return;
		}
		Image image = new ImageIcon(backImage).getImage();
		g.drawImage(image, 1, 1, w, h, null);
	}

}

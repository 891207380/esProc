package com.raqsoft.excel;

/**
 * ��ȡPoi5.0.0��Poi3.17�汾�ĵ�������ʵ����
 * �������һ�£������ݰ汾��ͬ������ڲ�ͬ�İ��С�Ϊ�����ֿ����룬�ֱ�ŵ���poi-5.0.0.jar�к�poi-3.17.jar�С�
 * */
public class ExcelVersionCompatibleUtilGetter {
	private static ExcelVersionCompatibleUtilInterface e = null;
	public static ExcelVersionCompatibleUtilInterface getInstance(){
		if(e == null)
			try {
				e = (ExcelVersionCompatibleUtilInterface) Class.forName("com.raqsoft.excel.ExcelVersionCompatibleUtil").newInstance();
				//e = new ExcelVersionCompatibleUtil();
			} catch (Exception e1) {
				e1.printStackTrace();
//				try {
//					e = (ExcelVersionCompatibleUtilInterface) Class.forName("com.raqsoft.excel.ExcelVersionCompatibleUtil").newInstance();
//				} catch (InstantiationException e2) {
//					e2.printStackTrace();
//				} catch (IllegalAccessException e2) {
//					e2.printStackTrace();
//				} catch (ClassNotFoundException e2) {
//					e2.printStackTrace();
//				}
			}
		return e;
	}
}

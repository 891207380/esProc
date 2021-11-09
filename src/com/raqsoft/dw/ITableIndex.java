package com.raqsoft.dw;

import com.raqsoft.dm.Context;
import com.raqsoft.dm.LongArray;
import com.raqsoft.dm.cursor.ICursor;
import com.raqsoft.expression.Expression;

/**
 * ��������ӿ���
 * @author runqian
 *
 */
public interface ITableIndex {
	public static final int TEMP_FILE_SIZE = 100 * 1024 * 1024;//����ʱ�Ļ����ļ���С
	public static int MIN_ICURSOR_REC_COUNT = 1000;//��С�����ֵʱ���ٽ��н��������ǿ�ʼ����
	public static int MIN_ICURSOR_BLOCK_COUNT = 10;//��С�����ֵʱ���ٽ��н��������ǿ�ʼ����
	public ICursor select(Expression exp, String []fields, String opt, Context ctx);
	public LongArray select(Expression exp, String opt, Context ctx);
	
	/**
	 * ��ȡ������������Ϣ���ڴ�
	 */
	public void loadAllBlockInfo();
	
	/**
	 * ��ȡ������������Ϣ���ڴ棨��������
	 */
	public void loadAllKeys();
	
	/**
	 * �ͷ��ڴ����������Ϣ
	 */
	public void unloadAllBlockInfo();
	
	/**
	 * ��������������
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * ����ȡ���ֶκ������ֶ�
	 * @param ifields �����ֶ�
	 * @param vfields ȡ���ֶ�
	 */
	public void setFields(String[] ifields, String[] vfields);
	
	/**
	 * �д�ʱ����ȡ��ʱ�����size���д�ʱ������
	 * @return
	 */
	public int getMaxRecordLen();
	
	/**
	 * �Ƿ���ڵڶ���������
	 * @return
	 */
	public boolean hasSecIndex();
	
	/**
	 * ����һ����¼��Ӧ�ĵ�ַ����
	 * һ�㶼��1��ֻ�ڸ���ʱ�ſ��ܶ��
	 * @return
	 */
	public int getPositionCount();
	
	/**
	 * ��������Ϣд����һ���±�table
	 * @param table
	 */
	public void dup(TableMetaData table);
}
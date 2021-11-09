package com.raqsoft.dm.op;

import com.raqsoft.dm.Context;
import com.raqsoft.dm.DataStruct;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.expression.Function;

/**
 * �α��ܵ����ӵĶ��ֶν������������㴦����
 * op.rename(F:F',��) op���α��ܵ�
 * @author RunQian
 *
 */
public class Rename extends Operation {
	private String []srcFields; // Դ�ֶ�������
	private String []newFields; // ���ֶ�������
	private DataStruct prevDs; // ��һ������ļ�¼�����ݽṹ

	
	public Rename(Function function, String []srcFields, String []newFields) {
		super(function);
		this.srcFields = srcFields;
		this.newFields = newFields;
	}
	
	public Rename(String []srcFields, String []newFields) {
		this(null, srcFields, newFields);
	}
	
	/**
	 * �����������ڶ��̼߳��㣬��Ϊ���ʽ���ܶ��̼߳���
	 * @param ctx ����������
	 * @return Operation
	 */
	public Operation duplicate(Context ctx) {
		return new Rename(function, srcFields, newFields);
	}

	/**
	 * �����α��ܵ���ǰ���͵�����
	 * @param seq ����
	 * @param ctx ����������
	 * @return
	 */
	public Sequence process(Sequence seq, Context ctx) {
		if (seq != null && seq.length() > 0) {
			Object obj = seq.getMem(1);
			if (obj instanceof Record) {
				DataStruct ds = ((Record)obj).dataStruct();
				if (prevDs != ds) {
					ds.rename(srcFields, newFields);
					prevDs = ds;
				}
			}
		}
		
		return seq;
	}
}

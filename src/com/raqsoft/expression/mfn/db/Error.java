package com.raqsoft.expression.mfn.db;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.DBFunction;

// 
/**
 * ȡ��һ�����ݿ����ִ�еĴ�����룬0��ʾ�޴�
 * db.error()
 * @author RunQian
 *
 */
public class Error extends DBFunction {
	public Object calculate(Context ctx) {
		return db.error(option);
	}
}

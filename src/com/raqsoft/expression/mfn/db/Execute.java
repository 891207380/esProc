package com.raqsoft.expression.mfn.db;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.cursor.ICursor;
import com.raqsoft.expression.DBFunction;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.IParam;
import com.raqsoft.resources.EngineMessage;

/**
 * ִ�����ݿ����
 * db.execute(sql,param,...) db.execute(A,sql,param,...)
 * @author RunQian
 *
 */
public class Execute extends DBFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("execute" + mm.getMessage("function.missingParam"));
		} else if (param.isLeaf()) { // û�в���
			Object obj = param.getLeafExpression().calculate(ctx);
			if (!(obj instanceof String)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("execute" + mm.getMessage("function.paramTypeError"));
			}

			return db.execute((String)obj, null, null, option);
		} else if (param.getType() != IParam.Comma) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("execute" + mm.getMessage("function.invalidParam"));
		}

		IParam sub0 = param.getSub(0);
		if (sub0 == null || !sub0.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("execute" + mm.getMessage("function.invalidParam"));
		}

		Object srcObj = sub0.getLeafExpression().calculate(ctx);
		if (srcObj instanceof String) {
			String strSql = (String)srcObj;
			int paramSize = param.getSubSize() - 1;
			Object []sqlParams = new Object[paramSize];
			byte []types = new byte[paramSize];
			for (int i = 0; i < paramSize; ++i) {
				IParam sub = param.getSub(i + 1);
				if (sub == null) continue;

				if (sub.isLeaf()) { // ֻ�в���û��ָ������
					sqlParams[i] = sub.getLeafExpression().calculate(ctx);
				} else {
					IParam subi0 = sub.getSub(0);
					IParam subi1 = sub.getSub(1);
					if (subi0 != null) sqlParams[i] = subi0.getLeafExpression().calculate(ctx);
					if (subi1 != null) {
						Object tmp = subi1.getLeafExpression().calculate(ctx);
						if (!(tmp instanceof Number)) {
							MessageManager mm = EngineMessage.get();
							throw new RQException("execute" + mm.getMessage("function.paramTypeError"));
						}

						types[i] = ((Number)tmp).byteValue();
					}
				}
			}

			return db.execute(strSql, sqlParams, types, option);
		} else if (srcObj instanceof Sequence || srcObj instanceof ICursor) {
			// ������е�ÿ��Ԫ��ִ��sql��䣬sql����������Ԫ�����
			IParam sub1 = param.getSub(1);
			if (sub1 == null || !sub1.isLeaf()) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("execute" + mm.getMessage("function.invalidParam"));
			}

			Object obj = sub1.getLeafExpression().calculate(ctx);
			if (!(obj instanceof String)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("execute" + mm.getMessage("function.paramTypeError"));
			}

			String strSql = (String)obj;
			int paramSize = param.getSubSize() - 2;
			Expression []sqlParams = new Expression[paramSize];
			byte []types = new byte[paramSize];

			for (int i = 0; i < paramSize; ++i) {
				IParam sub = param.getSub(i + 2);
				if (sub == null)continue;

				if (sub.isLeaf()) { // ֻ�в���û��ָ������
					sqlParams[i] = sub.getLeafExpression();
				} else {
					IParam subi0 = sub.getSub(0);
					IParam subi1 = sub.getSub(1);
					if (subi0 != null) sqlParams[i] = subi0.getLeafExpression();
					if (subi1 != null) {
						Object tmp = subi1.getLeafExpression().calculate(ctx);
						if (!(tmp instanceof Number)) {
							MessageManager mm = EngineMessage.get();
							throw new RQException("execute" + mm.getMessage("function.paramTypeError"));
						}

						types[i] = ((Number)tmp).byteValue();
					}
				}
			}
			
			if (srcObj instanceof Sequence) {
				db.execute((Sequence)srcObj, strSql, sqlParams, types, option, ctx);
			} else {
				db.execute((ICursor)srcObj, strSql, sqlParams, types, option, ctx);
			}
			
			return null;
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("execute" + mm.getMessage("function.paramTypeError"));
		}
	}
}

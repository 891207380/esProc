package com.raqsoft.expression.fn.algebra;

import com.raqsoft.common.Logger;
import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.IParam;
import com.raqsoft.resources.EngineMessage;

/**
 * ����������Э����cov(A, B)
 * @author bd
 */
public class Cov extends Function {
	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("cov" + mm.getMessage("function.missingParam"));
		} else if (param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("cov" + mm.getMessage("function.invalidParam"));
		} else {
			if (param.getSubSize() != 2) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("cov" + mm.getMessage("function.invalidParam"));
			}

			IParam sub1 = param.getSub(0);
			IParam sub2 = param.getSub(1);
			if (sub1 == null || sub2 == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("cov" + mm.getMessage("function.invalidParam"));
			}
			Object o1 = sub1.getLeafExpression().calculate(ctx);
			Object o2 = sub2.getLeafExpression().calculate(ctx);
			if (o1 instanceof Sequence && o2 instanceof Sequence) {
				Matrix A = new Matrix((Sequence)o1);
				Matrix B = new Matrix((Sequence)o2);
				if (A.getCols() == 0 || A.getRows() != 1) {
					// edited by bd, 2021.9.15, ���AΪ��������ʱ���Զ�ת��
					A = A.transpose();
				}
				else if (B.getCols() == 0 || B.getRows() != 1) {
					// edited by bd, 2021.9.15, ���BΪ��������ʱ���Զ�ת��
					B = B.transpose();
				}
				double[] as = A.getArray()[0];
				double[] bs = B.getArray()[0];
				int rs = as.length;
				if (rs != bs.length) {
					// ��ͬά
					MessageManager mm = EngineMessage.get();
					//edited by bd, 2020.3.10, ���������е�����ֻ���������Ϣ�����жϣ�����null
					Logger.warn("cov" + mm.getMessage("function.paramTypeError"));
					return null;
				}
				// Э����
				double avga = 0;
				for (int i = 0; i < rs; i++) {
					avga += as[i];
				}
				avga /= rs;
				double avgb = 0;
				for (int i = 0; i < rs; i++) {
					avgb += bs[i];
				}
				avgb /= rs;
				double cov = 0;
				for (int i = 0; i < rs; i++) {
					cov += (as[i] - avga) * (bs[i] - avgb);
				}
				cov = cov/(rs - 1);
				return cov;
			} else {
				MessageManager mm = EngineMessage.get();
				throw new RQException("cov" + mm.getMessage("function.paramTypeError"));
			}
		}
	}
}

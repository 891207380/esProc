package com.raqsoft.expression.fn.algebra;

import com.raqsoft.common.Logger;
import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Sequence;
import com.raqsoft.expression.Function;
import com.raqsoft.resources.EngineMessage;

/**
 * ��������inverse(A)���棬ֻ�з�������ʱ�������, inverse@p(A)α��������о���A����α�����B������ABA=B��BAB=A���޽�ʱ����С���˷�
 * @author bd
 *
 */
public class Inverse extends Function{
	public Object calculate(Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("inverse" + mm.getMessage("function.missingParam"));
		} else if (param.isLeaf()) {
			Object result1 = param.getLeafExpression().calculate(ctx);
			if (!(result1 instanceof Sequence)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("inverse" + mm.getMessage("function.paramTypeError"));
			}
			Matrix A = new Matrix((Sequence) result1);
			if (A.getCols() == 0 || A.getRows() == 0) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("inverse" + mm.getMessage("function.paramTypeError"));
			}
			Matrix X = null;
			boolean pseudo = false;
			boolean auto = false;
			if (option != null) {
				if (option.indexOf('a') > -1) {
					auto = true;
				}
				else if (option.indexOf('p') > -1) {
					pseudo = true;
				}
			}
			try {
				if (pseudo) {
					X = A.pseudoinverse();
				}
				else {
					X = A.inverse();
				}
			}
			catch (Exception e) {
				if (auto) {
					// A�޷�����ʱ����α�����
					X = A.pseudoinverse();
				}
				else {
					Logger.warn("inverse error: " + e.getMessage());
					return null;
				}
			}
			if (X == null) {
				return null;
			}
			return X.toSequence(option, false);
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("inverse" + mm.getMessage("function.invalidParam"));
		}
	}
}

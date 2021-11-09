package com.raqsoft.expression.mfn.op;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.op.Conj;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.OperableFunction;
import com.raqsoft.resources.EngineMessage;

// ��Ա�ĺ���
/**
 * ���α��ܵ��������к�������
 * op.conj() op.conj(x)��op�����е��α��ܵ�
 * @author RunQian
 *
 */
public class AttachConj extends OperableFunction {
	public Object calculate(Context ctx) {
		if (param == null) {
			Conj op = new Conj(this, null);
			return operable.addOperation(op, ctx);
		} else if (param.isLeaf()) {
			Expression exp = param.getLeafExpression();
			Conj op = new Conj(this, exp);
			return operable.addOperation(op, ctx);
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("conj" + mm.getMessage("function.invalidParam"));
		}
	}
}

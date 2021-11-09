package com.raqsoft.expression;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.Record;
import com.raqsoft.dm.Sequence;
import com.raqsoft.dm.op.Calculate;
import com.raqsoft.dm.op.Operable;
import com.raqsoft.resources.EngineMessage;

/**
 * ѭ������ִ�б��ʽ������������
 * A.(x)
 * @author WangXiaoJun
 *
 */
public class Calc extends MemberFunction {
	private Object srcObj;
	
	public boolean isLeftTypeMatch(Object obj) {
		return true;
	}

	public void setDotLeftObject(Object obj) {
		srcObj = obj;
	}
	
	/**
	 * �жϵ�ǰ�ڵ��Ƿ������к���
	 * �������������Ҳ�ڵ������к��������ڵ�������������Ҫ����ת������
	 * @return
	 */
	public boolean isSequenceFunction() {
		return true;
	}
	
	public Object calculate(Context ctx) {
		if (param == null) {
			return srcObj;
		} else if (param.isLeaf()) {
			Expression exp = param.getLeafExpression();
			if (srcObj instanceof Sequence) {
				return ((Sequence)srcObj).calc(exp, option, ctx);
			} else if (srcObj instanceof Record) {
				return ((Record)srcObj).calc(exp, ctx);
			} else if (srcObj instanceof Operable) {
				Calculate calculate = new Calculate(this, exp);
				((Operable)srcObj).addOperation(calculate, ctx);
				return srcObj;
			} else {
				MessageManager mm = EngineMessage.get();
				throw new RQException("\".\"" + mm.getMessage("dot.s2rLeft"));
			}
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("()" + mm.getMessage("function.invalidParam"));
		}
	}
}

package com.raqsoft.expression.fn.math;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.expression.Function;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Node;

/**
 * rand(n)	����С��n�����������nʡ���򷵻�[0,1]��������С��
	@s	�趨���������
 *
 */
public class Rand extends Function {

	public Object calculate(Context ctx) {
		MessageManager mm = EngineMessage.get();
		if (param == null) {//����[0,1]��������С��
			return new Double(ctx.getRandom().nextDouble());
		} else if (param.isLeaf()) {
			Object obj = param.getLeafExpression().calculate(ctx);
			if (!(obj instanceof Number)) {
				throw new RQException("rand" + mm.getMessage("function.paramTypeError"));
			}

			if (option == null || option.indexOf('s') == -1) {//����С��n���������
				int n = ((Number)obj).intValue();
				return new Integer(ctx.getRandom().nextInt(n));
			} else {//�趨���������
				long seed = ((Number)obj).longValue();
				ctx.getRandom(seed);
				return null;
			}
		} else {
			throw new RQException("rand" + mm.getMessage("function.invalidParam"));
		}
	}

	public Node optimize(Context ctx) {
		return this;
	}
}

package com.raqsoft.expression.fn.convert;

import java.math.BigDecimal;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.Function;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.util.Variant;

/**
 * ���ַ�������ֵ�͵���ֵת���ɴ󸡵���
 * decimal(stringExp) ����stringExp�����������ֺ�С������ɵ��ַ�����
 * decimal(numberExp) ����numberExpֻ�����ڵ���64λ������64λ��Ҫ���ַ���stringExp ����numberExp��
 * @author runqian
 *
 */
public class ToBigDecimal extends Function {
	public Object calculate(Context ctx) {
		if (param == null || !param.isLeaf()) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("decimal" + mm.getMessage("function.invalidParam"));
		}
		
		Object result = param.getLeafExpression().calculate(ctx);
		if (result == null) {
			return null;
		} else if (result instanceof String) {
			try {
				return new BigDecimal((String)result);
			} catch (NumberFormatException e) {
				return null;
			}
		} else {
			return Variant.toBigDecimal(result);
		}
	}
}

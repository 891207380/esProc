package com.raqsoft.expression.mfn.channel;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.ChannelFunction;

/**
 * Ϊ�ܵ����屣���ܵ���ǰ������Ϊ�����������
 * ch.fetch()
 * @author RunQian
 *
 */
public class Fetch extends ChannelFunction {
	public Object calculate(Context ctx) {
		return channel.fetch();
	}
}
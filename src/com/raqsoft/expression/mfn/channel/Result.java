package com.raqsoft.expression.mfn.channel;

import com.raqsoft.dm.Context;
import com.raqsoft.expression.ChannelFunction;

/**
 * ȡ�ܵ��ļ�����
 * ch.result()
 * @author RunQian
 *
 */
public class Result extends ChannelFunction {
	public Object calculate(Context ctx) {
		return channel.result();
	}
}

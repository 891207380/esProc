package com.raqsoft.expression;

import com.raqsoft.dm.op.Channel;

/**
 * �ܵ���Ա��������
 * ch.f()
 * @author RunQian
 *
 */
public abstract class ChannelFunction extends MemberFunction {
	protected Channel channel;

	public boolean isLeftTypeMatch(Object obj) {
		return obj instanceof Channel;
	}
	
	public void setDotLeftObject(Object obj) {
		channel = (Channel)obj;
	}
}
package com.raqsoft.ide.dfx.resources;

import java.util.Locale;

import com.raqsoft.common.MessageManager;

/**
 * ��������Դ
 *
 */
public class IdeDfxMessage {

	/**
	 * ˽�й��캯��
	 */
	private IdeDfxMessage() {
	}

	/**
	 * ��ȡ��Դ����������
	 * 
	 * @return
	 */
	public static MessageManager get() {
		return get(Locale.getDefault());
	}

	/**
	 * ��ȡָ���������Դ����������
	 * 
	 * @param locale
	 *            ����
	 * @return
	 */
	public static MessageManager get(Locale locale) {
		return MessageManager.getManager(
				"com.raqsoft.ide.dfx.resources.ideDfxMessage", locale);
	}

}

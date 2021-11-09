package com.raqsoft.resources;

import java.util.Locale;

import com.raqsoft.common.MessageManager;

/**
 * Ӧ����ص���Դ��
 *
 */
public class AppMessage {

	private AppMessage() {
	}

	public static MessageManager get() {
		return MessageManager.getManager("com.raqsoft.resources.appMessage");
	}

	public static MessageManager get(Locale locale) {
		return MessageManager.getManager("com.raqsoft.resources.appMessage",
				locale);
	}

}

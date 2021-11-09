package com.raqsoft.ide.dfx.control;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.raqsoft.ide.common.GC;

/**
 * ����༭�����Ҽ�����¼�
 *
 */
public class EditorRightClicked extends MouseAdapter {
	/**
	 * ����ؼ�
	 */
	private DfxControl control;

	/**
	 * ���캯��
	 * 
	 * @param control
	 *            ����ؼ�
	 */
	public EditorRightClicked(DfxControl control) {
		this.control = control;
	}

	/**
	 * ��갴��
	 */
	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	/**
	 * ����ͷ�
	 */
	public void mouseReleased(MouseEvent e) {
		showPopup(e);
	}

	/**
	 * ��ʾ�Ҽ������˵�
	 * 
	 * @param e
	 */
	void showPopup(MouseEvent e) {
		if (e.isPopupTrigger()) {
			control.fireRightClicked(e, GC.SELECT_STATE_CELL);
		}
	}

}

package com.scudata.ide.dfx;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuListener;

import com.scudata.ide.common.GV;

/**
 * �Ҽ������˵�
 *
 */
public class PopupDfx {
	/**
	 * �����˵�������
	 */
	PopupMenuListener listener = null;

	/**
	 * ���캯��
	 */
	public PopupDfx() {
	}

	/**
	 * ���ӵ����˵�������
	 * 
	 * @param listener
	 */
	public void addPopupMenuListener(PopupMenuListener listener) {
		this.listener = listener;
	}

	/**
	 * ȡ�Ҽ������˵�
	 * 
	 * @param selectStatus
	 *            ����ѡ���״̬��GCDfx�ж���ĳ���
	 * @return
	 */
	public JPopupMenu getDFXPop(byte selectStatus) {
		MenuDfx mDfx = (MenuDfx) GV.appMenu;
		JPopupMenu pm = new JPopupMenu();
		pm.add(mDfx.cloneMenuItem(GCDfx.iCUT));
		pm.add(mDfx.cloneMenuItem(GCDfx.iCOPY));
		pm.add(mDfx.cloneMenuItem(GCDfx.iPASTE));
		pm.addSeparator();

		switch (selectStatus) {
		case GCDfx.SELECT_STATE_CELL:
			pm.add(mDfx.cloneMenuItem(GCDfx.iCTRL_ENTER));
			pm.add(mDfx.cloneMenuItem(GCDfx.iDUP_ROW));
			pm.add(mDfx.cloneMenuItem(GCDfx.iDUP_ROW_ADJUST));
			pm.addSeparator();
			pm.add(mDfx.cloneMenuItem(GCDfx.iCLEAR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iFULL_CLEAR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iCLEAR_VALUE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iTEXT_EDITOR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iNOTE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iTIPS));
			pm.addSeparator();
			pm.add(mDfx.cloneMenuItem(GCDfx.iEDIT_CHART));
			pm.add(mDfx.cloneMenuItem(GCDfx.iFUNC_ASSIST));
			
			pm.add(mDfx.cloneMenuItem(GCDfx.iDRAW_CHART));
			JMenuItem calcArea = mDfx.cloneMenuItem(GCDfx.iCALC_AREA);
			calcArea.setVisible(true);
			pm.add(calcArea);
			break;
		case GCDfx.SELECT_STATE_COL:
			pm.add(mDfx.cloneMenuItem(GCDfx.iADD_COL));
			pm.add(mDfx.cloneMenuItem(GCDfx.iDELETE_COL));
			pm.addSeparator();
			pm.add(mDfx.cloneMenuItem(GCDfx.iCLEAR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iFULL_CLEAR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iCLEAR_VALUE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iTEXT_EDITOR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iNOTE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iTIPS));
			pm.addSeparator();
			pm.add(mDfx.cloneMenuItem(GCDfx.iCOL_WIDTH));
			pm.add(mDfx.cloneMenuItem(GCDfx.iCOL_HIDE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iCOL_VISIBLE));
			break;
		case GCDfx.SELECT_STATE_ROW:
			pm.add(mDfx.cloneMenuItem(GCDfx.iCTRL_ENTER));
			pm.add(mDfx.cloneMenuItem(GCDfx.iDELETE_ROW));
			pm.addSeparator();
			pm.add(mDfx.cloneMenuItem(GCDfx.iCLEAR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iFULL_CLEAR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iCLEAR_VALUE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iTEXT_EDITOR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iNOTE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iTIPS));
			pm.addSeparator();
			pm.add(mDfx.cloneMenuItem(GCDfx.iROW_HEIGHT));
			pm.add(mDfx.cloneMenuItem(GCDfx.iROW_HIDE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iROW_VISIBLE));
			break;
		default:
			pm.add(mDfx.cloneMenuItem(GCDfx.iCLEAR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iFULL_CLEAR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iCLEAR_VALUE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iTEXT_EDITOR));
			pm.add(mDfx.cloneMenuItem(GCDfx.iNOTE));
			pm.add(mDfx.cloneMenuItem(GCDfx.iTIPS));
			break;
		}
		if (listener != null) {
			pm.addPopupMenuListener(listener);
		}
		return pm;
	}
}

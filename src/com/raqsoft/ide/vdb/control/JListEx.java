package com.raqsoft.ide.vdb.control;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.TransferHandler;

import com.raqsoft.common.ArgumentTokenizer;
import com.raqsoft.common.StringUtils;
import com.raqsoft.ide.common.GM;

/**
 * <p>
 * �����JList
 * </p>
 * �����ݷ�װ�ڸ��� ���ݹ�����ʾֵҪ�����ΪString���ͣ�������ʾ���� ����ֵ��������������
 * 
 * @version 1.0
 */

public class JListEx extends JList {
	private static final long serialVersionUID = 1L;

	public Object tag;

	public DefaultListModel data = new DefaultListModel();

	private Vector<Object> codeData = new Vector<Object>();

	public JListEx() {
		super.setModel(data);
		setTransferHandler(new JListExHandler(this));
		addKeyListener(new KeyListener() {
			private StringBuffer buf = new StringBuffer();

			private int lastTypeTime = -1;

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
				JList list = (JList) e.getSource();
				ListModel model = list.getModel();
				char key = e.getKeyChar();
				int now = (int) (System.currentTimeMillis() / 1000);
				if (lastTypeTime > 0) {
					if (now - lastTypeTime > 2) {
						buf.delete(0, buf.length());
					}
				}
				lastTypeTime = now;
				buf.append(key);
				int i = 0, size = model.getSize();
				boolean doubleSearch = false;
				for (; i < size; i++) {
					String s = (String) model.getElementAt(i);
					if (s != null && s.length() > 0 && s.startsWith(buf.toString())) {
						list.setSelectedIndex(i);
						list.ensureIndexIsVisible(i);
						break;
					}
					if (!doubleSearch && i == size - 1) {
						doubleSearch = true;
						buf.delete(0, buf.length());
						buf.append(key);
						i = 0;
					}
				}
			}
		});
	}

	public JListEx(String items, char delim) {
		this();
		setListData(items, delim);
	}

	public JListEx(String items) {
		this(items, ',');
	}

	public JListEx(Object[] items) {
		this();
		setListData(items);
	}

	public JListEx(Vector<?> items) {
		this();
		setListData(items);
	}

	public JListEx(DefaultListModel model) {
		this();
		if (model == null) {
			return;
		}
		setModel(model);
	}

	/**
	 * �ƶ����ݵĴ�������Ĭ������û����ùܸ÷����������Զ�������drop�����ڿؼ���
	 * ����û��б�Ҫ�Լ��������ݣ����绹Ҫ�������������𸲸Ǹ÷�����Ȼ�󷵻�true�� ����
	 * 
	 * @param moveData
	 *            String ���ŷָ��ѡ�е�JList��ѡ��
	 * @param dropedControl
	 *            JComponent ���Drop�Ŀؼ�
	 * @return boolean �û��Ƿ��Լ������˸÷������ǵĻ�Ӧ�÷���true������false��
	 */
	public boolean moveDropTarget(String moveData, JComponent dropedControl) {
		return false;
	}

	/**
	 * ���õ�ǰ������Ϊitems
	 * 
	 * @param items
	 *            �����б�
	 * @param delim
	 *            �����ڷֿ�items���ݵķָ����
	 */
	public void setListData(String items, char delim) {
		if (items == null) {
			return;
		}
		data.removeAllElements();
		ArgumentTokenizer at = new ArgumentTokenizer(items, delim);
		while (at.hasMoreTokens()) {
			data.addElement(at.nextToken());
		}
	}

	/**
	 * ���õ�ǰ������Ϊitems
	 * 
	 * @param items
	 *            �á������ֿ��������б�
	 */
	public void setListData(String items) {
		setListData(items, ',');
	}

	/**
	 * ���õ�ǰ������ΪlistData
	 * 
	 * @param listData
	 *            �������ݵĶ�������
	 */
	public void setListData(Object[] listData) {
		if (listData == null) {
			return;
		}
		data.removeAllElements();
		for (int i = 0; i < listData.length; i++) {
			data.addElement(listData[i]);
		}
	}

	/**
	 * ���õ�ǰ������ΪlistData
	 * 
	 * @param listData
	 *            �������ݵ�Vector����
	 */
	public void setListData(List<?> listData) {
		if (listData == null) {
			return;
		}
		setListData(listData.toArray());
	}

	/**
	 * ���õ�ǰ������Ϊmodel
	 * 
	 * @param model
	 *            �������ݵ�ListModel����
	 */
	public void setModel(DefaultListModel model) {
		if (model == null) {
			return;
		}
		data = model;
		super.setModel(model);
	}

	/**
	 * ɾ����ǰ�б���е�����ѡ�е���Ŀ
	 */
	public void removeSelectedItems() {
		if (this.data.size() == 0) {
			return;
		}
		int[] iSelects = getSelectedIndices();
		for (int i = iSelects.length - 1; i >= 0; i--) {
			data.removeElementAt(iSelects[i]);
		}
	}

	/**
	 * ��õ�ǰ�б���е�������Ŀ��
	 * 
	 * @return �ַ�����ʽ����Ŀ�б��б�֮���á������ֿ�
	 */
	public String totalItems() {
		if (data.getSize() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.getSize(); i++) {
			sb.append(data.get(i) + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * ��õ�ǰ�б���е�����ѡ�е���Ŀ��
	 * 
	 * @return �ַ�����ʽ����Ŀ�б��б�֮���á������ֿ�
	 */
	public String selectedItems() {
		StringBuffer sb = new StringBuffer();
		Object[] sItems = this.getSelectedValues();
		if (sItems.length == 0) {
			return "";
		}
		for (int i = 0; i < sItems.length; i++) {
			sb.append(sItems[i] + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public String getSelectedItems() {
		return selectedItems();
	}

	/**
	 * x_ Ϊǰ׺�ĺ���������ʾֵ����ʵֵ��ʾ�����еĺ�����������Ӧ�� x_...
	 * 
	 * @param codeData
	 * @param dispData
	 */
	public synchronized void x_setData(List<Object> codeData, List<String> dispData) {
		data.removeAllElements();
		setListData(dispData);
		this.codeData.removeAllElements();
		this.codeData.addAll(codeData);
	}

	public Object x_getCodeItem(String dispItem) {
		if (codeData == null || data == null) {
			return dispItem;
		}
		String disp;
		int i = 0;
		for (i = 0; i < data.size(); i++) {
			disp = (String) data.get(i);
			if (disp.equalsIgnoreCase(dispItem)) {
				break;
			}
		}
		if (i >= codeData.size()) {
			return dispItem;
		}
		return codeData.get(i);
	}

	public String x_getDispItem(Object codeItem) {
		if (codeData == null || data == null) {
			return codeItem.toString();
		}
		Object code;
		int i = 0;
		for (i = 0; i < codeData.size(); i++) {
			code = codeData.get(i);
			if (code.equals(codeItem)) {
				break;
			}
		}
		if (i >= data.size()) {
			return codeItem.toString();
		}
		return (String) data.get(i);
	}

	public String x_SelectedItems() {
		StringBuffer sb = new StringBuffer();
		Object[] sItems = this.getSelectedValues();
		if (sItems.length == 0) {
			return "";
		}
		for (int i = 0; i < sItems.length; i++) {
			sb.append(x_getCodeItem((String) sItems[i]) + ",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public void x_setModel(Vector<Object> codeData, DefaultListModel dispModel) {
		data = dispModel;
		super.setModel(dispModel);
		this.codeData = codeData;
	}

	public void x_removeAllElements() {
		data.removeAllElements();
		codeData.removeAllElements();
	}

	public void x_removeElement(int i) {
		data.removeElementAt(i);
		codeData.removeElementAt(i);
	}

	public void x_removeSelectedItems() {
		int[] ii = getSelectedIndices();
		for (int i = ii.length - 1; i >= 0; i--) {
			x_removeElement(ii[i]);
		}
	}

	public Object[] x_getSelectedValues() {
		Object[] d = getSelectedValues();
		Object[] c = new Object[d.length];
		for (int i = 0; i < d.length; i++) {
			c[i] = x_getCodeItem((String) d[i]);
		}
		return c;
	}

	public void x_addElement(Object code, String disp) {
		codeData.addElement(code);
		data.addElement(disp);
	}

	public void x_insertElement(int index, Object code, String disp) {
		codeData.insertElementAt(code, index);
		data.insertElementAt(disp, index);
	}

	public void x_setElementAt(int index, Object code, String disp) {
		codeData.setElementAt(code, index);
		data.setElementAt(disp, index);
	}

	public void x_shiftElement(int index, boolean shiftUp) {
		if (shiftUp && index <= 0) {
			return;
		}
		if (!shiftUp && index >= data.size() - 1) {
			return;
		}
		Object tmp;
		if (shiftUp) {
			tmp = codeData.get(index - 1);
			codeData.set(index - 1, codeData.get(index));
			codeData.set(index, tmp);
			tmp = data.get(index - 1);
			data.set(index - 1, data.get(index));
			data.set(index, tmp);
			this.setSelectedIndex(index - 1);
		} else {
			tmp = codeData.get(index + 1);
			codeData.set(index + 1, codeData.get(index));
			codeData.set(index, tmp);
			tmp = data.get(index + 1);
			data.set(index + 1, data.get(index));
			data.set(index, tmp);
			this.setSelectedIndex(index + 1);
		}

	}
}

class JListExHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;
	JListEx home;

	public JListExHandler(JListEx home) {
		this.home = home;
	}

	public boolean canImport(JComponent c, DataFlavor[] flavors) {
		return (c instanceof JListEx || c instanceof JTextArea || c instanceof JList || c instanceof JTextPane);
	}

	/**
	 * �ڽӿ�listData���ҵ�����item�����
	 * 
	 * @param listData
	 * @param item
	 * @return
	 */
	public static int getItemIndex(ListModel listData, String item) {
		int c = listData.getSize();
		for (int i = 0; i < c; i++) {
			if (item.equalsIgnoreCase(listData.getElementAt(i).toString())) {
				return i;
			}
		}
		return -1;
	}

	public boolean importData(JComponent c, Transferable t) {
		try {
			String items = (String) t.getTransferData(DataFlavor.stringFlavor);
			if (home.moveDropTarget(items, c)) {
				return true;
			}
			if (!StringUtils.isValidString(items)) {
				return false;
			}
			String[] sItems = items.split(",");
			int i;
			if (c instanceof JListEx) {
				JListEx tList = (JListEx) c;
				for (i = 0; i < sItems.length; i++) {
					if (getItemIndex(tList.data, sItems[i]) == -1) {
						tList.data.addElement(sItems[i]);
					}
				}
			} else if (c instanceof JList) {
				JList list = (JList) c;
				DefaultListModel lm = (DefaultListModel) list.getModel();
				for (i = 0; i < sItems.length; i++) {
					if (getItemIndex(lm, sItems[i]) == -1) {
						lm.addElement(sItems[i]);
					}
				}
			} else if (c instanceof JTextArea) {
				JTextArea tta = (JTextArea) c;
				tta.setText(tta.getText() + " " + items);
			} else if (c instanceof JTextPane) {
				JTextPane ttp = (JTextPane) c;
				ttp.setText(ttp.getText() + " " + items);
			} else {
				return false;
			}
			return true;
		} catch (Exception x) {
			GM.showException(x);
		}
		return false;
	}

	protected void exportDone(JComponent c, Transferable data, int action) {
	}

	public int getSourceActions(JComponent c) {
		return COPY_OR_MOVE;
	}

	protected Transferable createTransferable(JComponent c) {
		JListEx list = (JListEx) c;
		Object[] items = list.getSelectedValues();
		StringBuffer buf = new StringBuffer();
		for (Object item : items) {
			if (item != null) {
				if (buf.length() > 0)
					buf.append(",");
				buf.append(item.toString());
			}
		}
		return new StringSelection(buf.toString());
	}
}

package com.scudata.ide.common.swing;

import java.awt.Color;
import java.awt.Component;
import java.math.BigDecimal;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import com.scudata.common.StringUtils;
import com.scudata.dm.Record;
import com.scudata.dm.Sequence;
import com.scudata.ide.common.ConfigOptions;
import com.scudata.ide.common.GC;
import com.scudata.ide.common.GM;

/**
 * JTable�ĵ�Ԫ����Ⱦ��
 *
 */
public class AllPurposeRenderer implements TableCellRenderer {
	/**
	 * ֧���»��ߵ��ı��ؼ�
	 */
	private JLabelUnderLine textField = new JLabelUnderLine();
	/**
	 * �Ƿ��������
	 */
	private boolean hasIndex = false;

	/**
	 * NULL����ʾֵ
	 */
	public static String DISP_NULL = "(null)";

	/**
	 * ���캯��
	 */
	public AllPurposeRenderer() {
		this(false);
	}

	/**
	 * ���캯��
	 * 
	 * @param hasIndex
	 *            �Ƿ��������
	 */
	public AllPurposeRenderer(boolean hasIndex) {
		this.hasIndex = hasIndex;
	}

	/**
	 * ȡ��ʾ�ؼ�
	 */
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			textField.setForeground(table.getSelectionForeground());
			// ������������Ͻǵĸ��б���ɫ
			if (ConfigOptions.getCellColor() != null) {
				textField.setBackground(ConfigOptions.getCellColor());
			} else { // δ�Զ���������ɫ������ϵͳĬ����ɫ
				textField.setBackground(table.getSelectionBackground());
			}
			textField.setOpaque(true);
		} else {
			textField.setBackground(table.getBackground());
			textField.setForeground(table.getForeground());
		}
		textField.setValue(value);
		if (isRefVal(value)) {
			textField.setForeground(Color.CYAN.darker());
		}
		String strText = GM.renderValueText(value);
		boolean isNumber = value != null && value instanceof Number;
		if (isNumber) {
			textField.setHorizontalAlignment(JLabel.RIGHT);
		} else {
			textField.setHorizontalAlignment(JLabel.LEFT);
		}
		if (StringUtils.isValidString(strText)) { // ������ʱ��һ���ո�
			if (isNumber) {
				strText += GC.STR_INDENT;
			} else {
				strText = GC.STR_INDENT + strText;
			}
		}
		if (value != null) {
			if (value instanceof BigDecimal) {
				textField.setForeground(ConfigOptions.COLOR_DECIMAL);
			} else if (value instanceof Double) {
				textField.setForeground(ConfigOptions.COLOR_DOUBLE);
			} else if (value instanceof Integer) {
				if (!hasIndex || column > 0)
					textField.setForeground(ConfigOptions.COLOR_INTEGER);
			}
			textField.setText(strText);
		} else {
			textField.setText(DISP_NULL);
			textField.setHorizontalAlignment(JTextField.CENTER);
			textField.setForeground(ConfigOptions.COLOR_NULL);
		}
		return textField;
	}

	private boolean isRefVal(Object val) {
		if (val == null) {
			return false;
		}
		return val instanceof Record || val instanceof Sequence;
	}
}

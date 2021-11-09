package com.raqsoft.ide.dfx;

import java.util.List;

import com.raqsoft.common.CellLocation;
import com.raqsoft.dm.Context;
import com.raqsoft.expression.fn.Call;
import com.raqsoft.expression.fn.Func.CallInfo;

/**
 * �������Ե���Ϣ��
 * 
 * ���������֣�һ���Ǵ򿪵ĸ�������һ���Ǳ�call��dfx�� ��call��dfx��û�д򿪵ģ�������Ҫ�ȴ򿪣��༭���ٱ��档
 *
 */
public class StepInfo {
	/**
	 * �ļ�·��
	 */
	public String filePath;
	/**
	 * ��˳���ҳ�б�
	 */
	public List<SheetDfx> sheets;
	/**
	 * ������
	 */
	public Context dfxCtx;
	/**
	 * ��ҳ�е�����
	 */
	public CellLocation parentLocation;
	/**
	 * funcʹ��
	 */
	public CallInfo callInfo;
	/**
	 * ������ʼ���������
	 */
	public CellLocation startLocation;
	/**
	 * �����Ľ�����
	 */
	public int endRow;
	/**
	 * callʹ��
	 */
	public Call parentCall;

	/**
	 * ���캯��
	 * 
	 * @param sheets
	 *            ��˳���ҳ�б�
	 */
	public StepInfo(List<SheetDfx> sheets) {
		this.sheets = sheets;
	}

	/**
	 * �Ƿ�func��������
	 * 
	 * @return
	 */
	public boolean isFunc() {
		return callInfo != null;
	}

	/**
	 * �Ƿ�call��������
	 * 
	 * @return
	 */
	public boolean isCall() {
		return callInfo == null;
	}

}

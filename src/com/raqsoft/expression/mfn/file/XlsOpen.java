package com.raqsoft.expression.mfn.file;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.excel.FileXls;
import com.raqsoft.excel.FileXlsR;
import com.raqsoft.excel.XlsFileObject;
import com.raqsoft.expression.FileFunction;
import com.raqsoft.expression.IParam;
import com.raqsoft.resources.AppMessage;
import com.raqsoft.resources.EngineMessage;

/**
 * ����f.xlsopen(p) ����Excel�ļ�f���سɶ��� p�����룻���ض������Ϊֻ�����
 * stname��ҳ����,nrows��������,ncols��������
 * 
 * @r ��ʽ�������ڳ��򵼳���xls�п������������ز���ȷ
 * @w ��ʽд����ʱ���ܷ���������Ϣ����@r����
 * 
 *
 */
public class XlsOpen extends FileFunction {

	/**
	 * ����
	 */
	public Object calculate(Context ctx) {
		String opt = option;
		boolean isR = opt != null && opt.indexOf("r") > -1;
		boolean isW = opt != null && opt.indexOf("w") > -1;

		if (isR && isW) {
			// @w��@r����ss
			MessageManager mm = AppMessage.get();
			throw new RQException("xlsopen" + mm.getMessage("filexls.notrw")); // ��ѡ��w��r����ͬʱ����
		}

		if (param == null) {
			// ����Excel�ļ�����
			return xlsOpen(isR, isW);
		}

		String pwd = null;
		if (param.getType() != IParam.Normal) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("xlsopen"
					+ mm.getMessage("function.invalidParam"));
		} else {
			if (!param.isLeaf()) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("xlsopen"
						+ mm.getMessage("function.invalidParam"));
			}

			IParam pwdParam = param;
			if (pwdParam != null) {
				Object tmp = pwdParam.getLeafExpression().calculate(ctx);
				if (tmp != null) {
					pwd = tmp.toString();
				}
				if ("".equals(pwd))
					pwd = null;
			}
		}
		try {
			return xlsOpen(pwd, isR, isW);
		} catch (RQException e) {
			throw e;
		} catch (Exception e) {
			throw new RQException(e.getMessage(), e);
		}
	}

	/**
	 * ����xo�ļ�����
	 * 
	 * @param isR
	 * @param isW
	 * @return
	 */
	private XlsFileObject xlsOpen(boolean isR, boolean isW) {
		return xlsOpen(null, isR, isW);
	}

	/**
	 * ����xo�ļ�����
	 * 
	 * @param pwd
	 *            ����
	 * @param isR
	 *            ѡ��@r
	 * @param isW
	 * @return
	 */
	private XlsFileObject xlsOpen(String pwd, boolean isR, boolean isW) {
		if (isR) {
			return new FileXlsR(file, pwd);
		}
		byte type;
		if (isW) {
			type = XlsFileObject.TYPE_WRITE;
		} else {
			type = XlsFileObject.TYPE_NORMAL;
		}
		return new FileXls(file, pwd, type);
	}
}

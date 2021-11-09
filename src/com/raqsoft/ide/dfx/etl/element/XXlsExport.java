package com.raqsoft.ide.dfx.etl.element;

import com.raqsoft.chart.Consts;
import com.raqsoft.ide.dfx.etl.EtlConsts;
import com.raqsoft.ide.dfx.etl.ParamInfo;
import com.raqsoft.ide.dfx.etl.ParamInfoList;

/**
 * ���������༭ xls.xlsexport()
 * ������ǰ׺X��ʾ xls�ļ�����
 * 
 * @author Joancy
 *
 */
public class XXlsExport extends FXlsExport {

	/**
	 * ��ȡ���ڽ���༭�Ĳ�����Ϣ�б�
	 */
	public ParamInfoList getParamInfoList() {
		ParamInfoList paramInfos = new ParamInfoList();
		ParamInfo.setCurrent(FXlsExport.class, this);

		paramInfos.add(new ParamInfo("aOrCs",EtlConsts.INPUT_CELLAORCS,true));
		paramInfos.add(new ParamInfo("exportFields", EtlConsts.INPUT_FIELDDEFINE_EXP_FIELD));
		paramInfos.add(new ParamInfo("sheet"));
		
		String group = "options";
		paramInfos.add(group, new ParamInfo("t", Consts.INPUT_CHECKBOX));

		return paramInfos;
	}

	/**
	 * ��ȡ������
	 * ���͵ĳ�������Ϊ
	 * EtlConsts.TYPE_XXX
	 * @return EtlConsts.TYPE_XLS
	 */
	public byte getParentType() {
		return EtlConsts.TYPE_XLS;
	}

	/**
	 * ��ȡ�ú����ķ�������
	 * @return EtlConsts.TYPE_EMPTY
	 */
	public byte getReturnType() {
		return EtlConsts.TYPE_EMPTY;
	}

}

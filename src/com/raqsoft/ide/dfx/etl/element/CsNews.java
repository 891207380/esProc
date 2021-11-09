package com.raqsoft.ide.dfx.etl.element;

import java.util.ArrayList;
import java.util.StringTokenizer;
import com.raqsoft.ide.dfx.etl.EtlConsts;
import com.raqsoft.ide.dfx.etl.FieldDefine;
import com.raqsoft.ide.dfx.etl.ObjectElement;
import com.raqsoft.ide.dfx.etl.ParamInfo;
import com.raqsoft.ide.dfx.etl.ParamInfoList;

/**
 * ���������༭ CS.news()
 * ������ǰ׺Cs��ʾ�α�
 * 
 * @author Joancy
 *
 */
public class CsNews extends ObjectElement {
	public String bigX;
	public ArrayList<FieldDefine> newFields;//��ʹ�õ����У� ���ʽ���ֶ���
	
	/**
	 * ��ȡ���ڽ���༭�Ĳ�����Ϣ�б�
	 */
	public ParamInfoList getParamInfoList() {
		ParamInfoList paramInfos = new ParamInfoList();
		ParamInfo.setCurrent(CsNews.class, this);

		paramInfos.add(new ParamInfo("bigX",EtlConsts.INPUT_CELLA));
		paramInfos.add(new ParamInfo("newFields",EtlConsts.INPUT_FIELDDEFINE_EXP_FIELD));
		
		return paramInfos;
	}
	
	/**
	 * ��ȡ������
	 * ���͵ĳ�������Ϊ
	 * EtlConsts.TYPE_XXX
	 * @return EtlConsts.TYPE_CURSOR
	 */
	public byte getParentType() {
		return EtlConsts.TYPE_CURSOR;
	}

	/**
	 * ��ȡ�ú����ķ�������
	 * @return EtlConsts.TYPE_CURSOR
	 */
	public byte getReturnType() {
		return EtlConsts.TYPE_CURSOR;
	}

	/**
	 * ��ȡ��������SPL���ʽ��ѡ�
	 */
	public String optionString() {
		return null;
	}

	/**
	 * ��ȡ��������SPL���ʽ�ĺ�����
	 */
	public String getFuncName() {
		return "news";
	}

	/**
	 * ��ȡ��������SPL���ʽ�ĺ�����
	 * ��setFuncBody���溯����Ȼ����ʽ�ĸ�ֵҲ���ǻ����
	 */
	public String getFuncBody() {
		StringBuffer sb = new StringBuffer();
		sb.append( getExpressionExp(bigX) );
		sb.append(";");
		sb.append( getFieldDefineExp(newFields));
		return sb.toString();
	}

	/**
	 * ���ú�����
	 * @param funcBody ������
	 */
	public boolean setFuncBody(String funcBody) {
		StringTokenizer st = new StringTokenizer(funcBody,";");
		bigX = getExpression( st.nextToken() );
		newFields = getFieldDefine( st.nextToken() );
		return true;
	}

}

package com.scudata.ide.dfx.etl;

/**
 * ����������ͨ�ýӿ�
 * 
 * @author Joancy
 *
 */
public interface IFuncObject{
	/**
	 * �ú����������ڵĸ�����
	 * @return ����
	 */
	public byte getParentType();
	
	/**
	 * �ú������ص�����
	 * @return ����
	 */
	public byte getReturnType();
}
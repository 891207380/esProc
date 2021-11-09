package com.raqsoft.parallel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.raqsoft.cellset.INormalCell;
import com.raqsoft.common.IntArrayList;
import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.dm.Context;
import com.raqsoft.dm.JobSpace;
import com.raqsoft.dm.LocalFile;
import com.raqsoft.dm.Param;
import com.raqsoft.dm.ParamList;
import com.raqsoft.dm.ResourceManager;
import com.raqsoft.expression.Expression;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.IParam;
import com.raqsoft.resources.EngineMessage;

/**
 * ��Ⱥ������
 * @author RunQian
 *
 */
final class ClusterUtil {
	/**
	 * �ڽڵ����������������
	 * @param js ����ռ�
	 * @return Context
	 */
	public static Context createContext(JobSpace js) {
		Context ctx = new Context();
		ctx.setJobSpace(js);
		return ctx;
	}
	
	/**
	 * �ڽڵ����������������
	 * @param js ����ռ�
	 * @param attributes  ���������Ĳ���
	 * @return Context
	 */
	public static Context createContext(JobSpace js, HashMap<String, Object> attributes) {
		if (attributes == null) {
			return createContext(js);
		}
		
		Context ctx = new Context();
		ctx.setJobSpace(js);
		
		// ȡ�����������ɱ������뵽��������
		String []paramNames = (String [])attributes.get("paramNames");
		if (paramNames != null) {
			Object []paramValues = (Object [])attributes.get("paramValues");
			int count = paramNames.length;
			for (int i = 0; i < count; ++i) {
				ctx.setParamValue(paramNames[i], paramValues[i]);
			}
		}
		
		return ctx;
	}
	
	// �жϼ�Ⱥ�ڱ��Ƿ�ֻ��Ҫȡ��ǰ�ڵ���ϵ�����
	private static boolean isCurrentOnly(ClusterMemoryTable cmt, String func, String opt) {
		// ����Ƿֲ��ڱ���û��cѡ����ʹ�����зֻ�������
		if (cmt.isDistributed()) {
			if (opt == null || opt.indexOf('c') == -1) {
				return false;
			}
			
			if (!func.equals("switch") && !func.equals("join")) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * �ڽڵ���������������ģ���Ⱥ�ڱ�����Ӧunit��Ӧ���ڱ�
	 * @param js ����ռ�
	 * @param attributes ���������Ĳ���
	 * @param func ���ô˷����ĺ�����
	 * @param opt ѡ��
	 * @return Context
	 */
	public static Context createContext(JobSpace js, HashMap<String, Object> attributes, String func, String opt) {
		if (attributes == null) {
			return createContext(js);
		}
		
		Context ctx = new Context();
		ctx.setJobSpace(js);
		
		// ȡ�����������ɱ������뵽��������
		String []paramNames = (String [])attributes.get("paramNames");
		if (paramNames != null) {
			Object []paramValues = (Object [])attributes.get("paramValues");
			int count = paramNames.length;
			for (int i = 0; i < count; ++i) {
				Object val = paramValues[i];
				if (val instanceof ClusterMemoryTable) {
					ClusterMemoryTable cmt = (ClusterMemoryTable)val;
					if (isCurrentOnly(cmt, func, opt)) {
						// ��д�ڱ������cѡ��ʱֻȡ��ǰ�ڵ���϶�Ӧ�Ĳ���
						int id = cmt.getCurrentClusterProxyId();
						ResourceManager rm = js.getResourceManager();
						TableProxy table = (TableProxy)rm.getProxy(id);
						if (table == null) {
							MessageManager mm = EngineMessage.get();
							throw new RQException(func + mm.getMessage("function.invalidParam"));
						}
						
						val = table.getTable();
					}
				}
				
				ctx.setParamValue(paramNames[i], val);
			}
		}
		
		return ctx;
	}
	
	/**
	 * �ѱ��ʽ�����õ��Ĳ������������õ������д����ڵ��
	 * @param command ����
	 * @param exp ���ʽ
	 * @param ctx ����������
	 */
	public static void setParams(UnitCommand command, Expression exp, Context ctx) {
		if (exp != null) {
			ParamList paramList = new ParamList();
			List<INormalCell> cellList = new ArrayList<INormalCell>();
			exp.getUsedParams(ctx, paramList);
			exp.getUsedCells(cellList);
			setParams(command, paramList, cellList);
		}
	}
	
	/**
	 * �ѱ��ʽ���������õ��Ĳ������������õ������д����ڵ��
	 * @param command ����
	 * @param exps ���ʽ����
	 * @param ctx ����������
	 */
	public static void setParams(UnitCommand command, Expression []exps, Context ctx) {
		if (exps != null) {
			ParamList paramList = new ParamList();
			List<INormalCell> cellList = new ArrayList<INormalCell>();
			for (Expression exp : exps) {
				exp.getUsedParams(ctx, paramList);
				exp.getUsedCells(cellList);
			}
			
			setParams(command, paramList, cellList);
		}
	}
	
	/**
	 * �Ѻ��������õ��Ĳ������������õ������д����ڵ��
	 * @param command ����
	 * @param function ����
	 * @param ctx ����������
	 */
	public static void setParams(UnitCommand command, Function function, Context ctx) {
		IParam param = function.getParam();
		if (param != null) {
			ParamList paramList = new ParamList();
			List<INormalCell> cellList = new ArrayList<INormalCell>();
			param.getUsedParams(ctx, paramList);
			param.getUsedCells(cellList);
			setParams(command, paramList, cellList);
		}
	}
	
	private static void setParams(UnitCommand command, ParamList paramList, List<INormalCell> cellList) {
		int paramCount = paramList.count();
		int cellCount = cellList.size();
		int total = paramCount + cellCount;
		if (total == 0) {
			return;
		}
		
		String []paramNames = new String[total];
		Object []paramValues = new Object[total];
		for (int i = 0; i < paramCount; ++i) {
			Param param = paramList.get(i);
			paramNames[i] = param.getName();
			paramValues[i] = param.getValue();
		}
		
		for (int c = 0, i = paramCount; c < cellCount; ++c, ++i) {
			INormalCell cell = cellList.get(c);
			paramNames[i] = cell.getCellId();
			paramValues[i] = cell.getValue(true);
		}
		
		command.setAttribute("paramNames", paramNames);
		command.setAttribute("paramValues", paramValues);
	}
	
	/**
	 * �г��ڵ��������Щ�ֱ��ļ�
	 * @param host �ڵ����IP��ַ
	 * @param port �ڵ���Ķ˿�
	 * @param fileName �ļ�·����
	 * @param parts Ҫ���ҵķֱ�
	 * @return �ڵ���ϰ����ķֱ�
	 */
	public static int[] listFileParts(String host, int port, String fileName, int []parts) {
		UnitClient client = new UnitClient(host, port);
		try {
			UnitCommand command = new UnitCommand(UnitCommand.LIST_FILE_PARTS);
			command.setAttribute("fileName", fileName);
			command.setAttribute("parts", parts);
			Response response = client.send(command);
			return (int[])response.checkResult();
		} finally {
			client.close();
		}
	}

	/**
	 * �ڵ����ִ���г��ڵ��������Щ�ֱ��ļ�����
	 * @param attributes
	 * @return
	 */
	public static Response executeListFileParts(HashMap<String, Object> attributes) {
		String fileName = (String)attributes.get("fileName");
		int []parts = (int[])attributes.get("parts");
		IntArrayList list = new IntArrayList(parts.length);
		
		try {
			for (int part : parts) {
				LocalFile localFile = new LocalFile(fileName, null, part);
				if (localFile.exists()) {
					list.addInt(part);
				}
			}
			
			if (list.size() > 0) {
				return new Response(list.toIntArray());
			} else {
				return new Response();
			}
		} catch (Exception e) {
			Response response = new Response();
			response.setException(e);
			return response;
		}
	}
}

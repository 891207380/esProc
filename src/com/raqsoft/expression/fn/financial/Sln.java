package com.raqsoft.expression.fn.financial;

import com.raqsoft.common.MessageManager;
import com.raqsoft.common.RQException;
import com.raqsoft.expression.Function;
import com.raqsoft.expression.IParam;
import com.raqsoft.resources.EngineMessage;
import com.raqsoft.util.Variant;
import com.raqsoft.dm.Context;



/**
 * ����ĳ���ʲ���һ���ڼ��е������۾�ֵ
 * @author yanjing
 * 
 * sln(cost,salvage,life)   

 * 
 * @param Cost Ϊ�ʲ�ԭֵ
 * @param Salvage Ϊ�ʲ����۾���ĩ�ļ�ֵ����ʱҲ��Ϊ�ʲ���ֵ��
 * @param Life Ϊ�۾����ޣ���ʱҲ�����ʲ���ʹ��������
 * @return
 * 
 * 
 */
public class Sln extends Function {
                                                                                                                            
	public Object calculate(Context ctx) {
		if(param==null || param.isLeaf() || param.getSubSize()<3){
			MessageManager mm = EngineMessage.get();
			throw new RQException("Fsln:" +
									  mm.getMessage("function.missingParam"));
		}
		
		int size=param.getSubSize();
		Object[] result=new Object[size];
		for(int i=0;i<size;i++){
			IParam sub = param.getSub(i);
			if (sub != null) {
				result[i] = sub.getLeafExpression().calculate(ctx);
			}
		}
		return sln(result);

	}
	
	/** 
	 * @param Cost Ϊ�ʲ�ԭֵ
	 * @param Salvage Ϊ�ʲ����۾���ĩ�ļ�ֵ����ʱҲ��Ϊ�ʲ���ֵ��
	 * @param Life Ϊ�۾����ޣ���ʱҲ�����ʲ���ʹ��������
	 * 
	 * @return
	 */
	private Object sln(Object[] result){
		double cost;
		double salvage;
		double life;
		
		for(int i=0;i<=2;i++){
			if(result[i]==null){
				MessageManager mm = EngineMessage.get();
				throw new RQException("The "+i+"th param of Fsln:" + mm.getMessage("function.paramValNull"));
			}
			if (!(result[i] instanceof Number)) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("The "+i+"th param of Fsln:" + mm.getMessage("function.paramTypeError"));
			}
		}
		cost=Variant.doubleValue(result[0]);
		salvage=Variant.doubleValue(result[1]);
		life=Variant.doubleValue(result[2]);

		return new Double((cost-salvage)/life);
	}

}

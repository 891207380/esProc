package com.scudata.expression.fn.algebra;

import org.apache.commons.math3.linear.*;

import com.scudata.common.MessageManager;
import com.scudata.common.RQException;
import com.scudata.dm.Context;
import com.scudata.dm.Sequence;
import com.scudata.expression.Function;
import com.scudata.expression.IParam;
import com.scudata.resources.EngineMessage;

public class PCA extends Function {
	/**
	 * pca(A,F)��pca(A,n), nʡ��ΪA�����������ص����ɷ�ϵ�������޷���ά�������������������ɷֵ÷�
	 * @param ctx	������
	 * @return
	 */
	public Object calculate (Context ctx) {
		if (param == null) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("pca" + mm.getMessage("function.missingParam"));
		}
		Object o1 = null;
		Object o2 = null;
		if (param.isLeaf()) {
			// ֻ��һ��������pca(A), n���Զ�����ΪA������
			o1 = param.getLeafExpression().calculate(ctx);
		} else if (param.getSubSize() != 2) {
			MessageManager mm = EngineMessage.get();
			throw new RQException("pca" + mm.getMessage("function.invalidParam"));
		} else {
			IParam sub1 = param.getSub(0);
			IParam sub2 = param.getSub(1);
			if (sub1 == null || sub2 == null) {
				MessageManager mm = EngineMessage.get();
				throw new RQException("pca" + mm.getMessage("function.invalidParam"));
			}
			o1 = sub1.getLeafExpression().calculate(ctx);
			o2 = sub2.getLeafExpression().calculate(ctx);
		}
		if (o1 instanceof Sequence) {
			Matrix A = new Matrix((Sequence)o1);
			if (o2==null || o2 instanceof Number) {
				int b = A.getCols();
				if (o2 != null) {
					b = ((Number) o2).intValue();
				}
				if (option != null && option.contains("r")) {
					// ֱ�Ӷ�A��ά���ȱ�����һ���ò���
					FitResult fr = fit(A, b);
					Matrix result = transformj(fr, A);
					return result.toSequence(option, true);
				}
				else {
					// ����fit������������Ϊ���У���3����Ա��ɣ���1��Ϊ��ֵ����mu����2��ΪǱ���е����ɷַ���latent����3��Ϊ���ɷ�ϵ������coeff������������Ϊ�ڶ�������ִ��pca(A,X)
					FitResult fr = fit(A, b);
					Sequence result = new Sequence(3);
					result.add(fr.mu.toSequence());
					result.add(fr.latent.toSequence());
					result.add(fr.coeff.toSequence(option, true));
					return result;
				}
			}
			else if (o2 instanceof Sequence) {
				Sequence seq = (Sequence) o2;
				if (seq.length() >= 3) {
					o1 = seq.get(1);
					o2 = seq.get(2);
					Object o3 = seq.get(3);
					if (o1 instanceof Sequence && o2 instanceof Sequence && o3 instanceof Sequence) {
						Vector dv = new Vector((Sequence) o1);
						Vector dv2 = new Vector((Sequence) o2);
						Matrix dm = new Matrix((Sequence) o3);
						FitResult fr = new FitResult(dv, dv2, dm);
						Matrix result = transformj(fr, A);
						return result.toSequence(option, true);
					}
				}
			}
			MessageManager mm = EngineMessage.get();
			throw new RQException("pca" + mm.getMessage("function.paramTypeError"));
		} else {
			MessageManager mm = EngineMessage.get();
			throw new RQException("pca" + mm.getMessage("function.paramTypeError"));
		}
	}
    
	/**
	 * ��ȡЭ�������
	 * @param matrix
	 * @return
	 */
    private Matrix getVarianceMatrix(Matrix matrix) {
        int rows = matrix.getRows();
        int cols = matrix.getCols();
        double[][] result = new double[cols][cols];// Э�������
        for (int c = 0; c < cols; c++) {
            for (int c2 = 0; c2 < cols; c2++) {
                double temp = 0;
                for (int r = 0; r < rows; r++) {
                    temp += matrix.get(r, c) * matrix.get(r, c2);
                }
                result[c][c2] = temp / (rows - 1);
            }
        }
        return new Matrix(result);
    }

    /**
     * ��ȡ���ɷַ�������
     * @param primaryArray
     * @param eigenvalue
     * @param eigenVectors
     * @param n_components
     * @return
     */
    private Matrix getPrincipalComponent(double[] eigenvalueArray, Matrix eigenVectors, int n_components) {
        Matrix X = eigenVectors.transpose();
        double[][] tEigenVectors = X.getArray();
        double[][] principalArray = new double[n_components][];
        for (int i = 0; i < n_components; i++) {
            principalArray[i] = tEigenVectors[i];
        }

        return new Matrix(principalArray);
    }
    /*
    private Matrix getPrincipalComponent(double[] eigenvalueArray, Matrix eigenVectors, int n_components) {
        //edited by bd, 2021.4.9, ȥ��ejml��ʹ�ã������������jdk1.8�Ƚ��鷳
        Matrix X = eigenVectors.transpose();
        double[][] tEigenVectors = X.getArray();
        Map<Integer, double[]> principalMap = new HashMap<Integer, double[]>();// key=���ɷ�����ֵ��value=������ֵ��Ӧ����������
        TreeMap<Double, double[]> eigenMap = new TreeMap<Double, double[]>(
                Collections.reverseOrder());// key=����ֵ��value=��Ӧ��������������ʼ��Ϊ��ת����ʹmap��keyֵ��������

        for (int i = 0; i < tEigenVectors.length; i++) {
            double[] value = new double[tEigenVectors[0].length];
            value = tEigenVectors[i];
            eigenMap.put(eigenvalueArray[i], value);
        }

        // ѡ��ǰ�������ɷ�
        List<Double> plist = new ArrayList<Double>();// ���ɷ�����ֵ
        int now_component = 0;
        for (double key : eigenMap.keySet()) {
            if (now_component < n_components) {
                now_component += 1;
                plist.add(key);
                //principalComponentNum++;
            }
            else {
                break;
            }
        }

        // �����ɷ�map����������
        for (int i = 0; i < plist.size(); i++) {
            if (eigenMap.containsKey(plist.get(i))) {
                principalMap.put(i, eigenMap.get(plist.get(i)));
            }
        }

        // ��map���ֵ�浽��ά������
        double[][] principalArray = new double[principalMap.size()][];
        Iterator<Map.Entry<Integer, double[]>> it = principalMap.entrySet()
                .iterator();
        for (int i = 0; it.hasNext(); i++) {
            principalArray[i] = it.next().getValue();
        }

        return new Matrix(principalArray);
    }
    */
    
    // ѵ������
    protected FitResult fit(Matrix inputData, int n_components) {
    	// ȡÿ�о�ֵ�������Ļ�����ʹ��ÿ�о�ֵΪ0
        Vector averageVector = inputData.getAverage();
        Matrix averageArray = inputData.changeAverageToZero(averageVector);
        // ����Э�������X(XT)
        Matrix varMatrix = getVarianceMatrix(averageArray);
        // Э������������ֵ�ֽ�
        RealMatrix m = new org.apache.commons.math3.linear.Array2DRowRealMatrix(varMatrix.getArray());
        EigenDecomposition evd = new org.apache.commons.math3.linear.EigenDecomposition(m);
        double[] ev = evd.getRealEigenvalues();
        double[][] V = evd.getV().getData();
        //math3������ֵ�ֽ⴦���У�ev�Ѿ������������������ѡȡ���ɷ�ʱ����������ԭ������ʱ����

        Matrix principalArray = getPrincipalComponent(ev, new Matrix(V), n_components);
        return new FitResult(averageVector, new Vector(ev), principalArray.transpose());
    }
    
//    ת������
    /**
     * ת������
     * @param principalDouble
     * @param averageObject
     * @param testinput
     * @return
     */
    public Matrix transform(Matrix principalMatrix, Vector averageObject, Matrix testinput){
        testinput.changeAverageToZero(averageObject);
        Matrix resultMatrix = testinput.times(principalMatrix);
        return resultMatrix;
    }

    //    ������ʹ��ת������
    /**
     * ����ѵ�����ת��
     * @param fr
     * @param testinput
     * @return
     */
    protected Matrix transformj(FitResult fr, Matrix testinput) {
    	testinput.changeAverageToZero(fr.mu);
        Matrix resultMatrix = testinput.times(fr.coeff);
        return resultMatrix;
    }
    
    protected class FitResult {
    	// mu
    	protected Vector mu;
    	// coeff
    	protected Matrix coeff;
    	// latent
    	protected Vector latent;
    	
    	protected FitResult(Vector mu, Vector latent, Matrix coeff) {
    		this.coeff = coeff;
    		this.mu = mu;
    		this.latent = latent;
    	}
    }

}

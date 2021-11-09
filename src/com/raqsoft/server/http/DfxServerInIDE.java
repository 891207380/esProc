package com.raqsoft.server.http;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.raqsoft.app.config.RaqsoftConfig;
import com.raqsoft.resources.ParallelMessage;
import com.raqsoft.server.IServer;
import com.raqsoft.server.StartUnitListener;
import com.raqsoft.common.Logger;
import com.raqsoft.parallel.UnitContext;

/**
 * dfx�����Http������ʵ��
 * 
 * @author Joancy
 *
 */
public class DfxServerInIDE implements IServer {
	public static final String HTTP_CONFIG_FILE = "HttpServer.xml";
	public static DfxServerInIDE instance=null;
	
	private HttpServer httpServer;
	private HttpContext ctx=null;
	private RaqsoftConfig rc = null;
	StartUnitListener listener = null;

	/**
	 * ����������Ϣ
	 * @param rc ����
	 */
	public void setRaqsoftConfig(RaqsoftConfig rc){
		this.rc = rc;
	}
	/**
	 * ��ȡ������Ϣ
	 * @return ����
	 */
	public RaqsoftConfig getRaqsoftConfig(){
		return rc;
	}
	
	/**
	 * ��ȡ���������Ķ���
	 * @return �����Ķ���
	 */
	public HttpContext getContext(){
		return ctx;
	}
	
	/**
	 * ��ȡ������Ψһʵ��
	 * @return ������ʵ��
	 * @throws Exception ����ʵ������ʱ�׳��쳣
	 */
	public static DfxServerInIDE getInstance() throws Exception {
		if (instance == null) {
			instance = new DfxServerInIDE();
		}
		return instance;
	}
	
	/**
	 * ����������
	 * @return �����ɹ�����true��ʧ�ܷ���false
	 * @throws Throwable ���������г����׳��쳣
	 */
	public boolean start() throws Throwable {
		if (httpServer != null)
			return false;
//			�ȼ���������ļ��Ƿ����
		InputStream is = UnitContext.getUnitInputStream(HttpContext.HTTP_CONFIG_FILE);
		is.close();
		ctx = new HttpContext(true);
		String host = ctx.getHost();
		int port = ctx.getPort();
		Logger.info(ParallelMessage.get().getMessage("DfxServerInIDE.starting"));
		
		InetAddress ia = InetAddress.getByName(host);
		try{
			InetSocketAddress inetSock = new InetSocketAddress(ia,port);
			httpServer = HttpServer.create(inetSock, ctx.getMaxLinks());
		}catch(java.net.BindException ex){
			throw new Exception(ParallelMessage.get().getMessage("DfxServerInIDE.portbind",port));
		}
		DfxHttpHandler dhh = new DfxHttpHandler();
		dhh.setIServer(this);
		httpServer.createContext("/", dhh);
		httpServer.setExecutor(null);
		httpServer.start();
		if (listener != null) {
			listener.serverStarted(port);
		}

		Logger.info(ParallelMessage.get().getMessage("DfxServerInIDE.started", ctx.getDefaultUrl()));
		return true;
	}

	/**
	 * �رշ�����
	 */
	public void shutDown() {
		stop();
	}

	/**
	 * ִ��ֹͣ������
	 * @return �ɹ�ͣ�����񷵻�true�����򷵻�false
	 */
	public boolean stop() {
		if (httpServer == null)
			return false;
		httpServer.stop(2); // ���ȴ�2��
		httpServer = null;
		Logger.info(ParallelMessage.get().getMessage("DfxServerInIDE.stop"));
		return true;
	}

	/**
	 * ��ʼ���з���
	 */
	public void run() {
		try {
			start();
		} catch (Throwable e) {
			if (listener != null) {
				listener.serverStartFail();
			}
			e.printStackTrace();
		}
	}

	/**
	 * ��ȡ����״̬
	 * @return �������з���true�����򷵻�false
	 */
	public boolean isRunning() {
		return httpServer != null;
	}
	
	/**
	 * ���÷�������������
	 */
	public void setStartUnitListener(StartUnitListener listen) {
		listener = listen;
	}

	/**
	 * ��ȡ��������ַ
	 * @return ��������ַ
	 */
	public String getHost() {
		return ctx.toString();
	}

	/**
	 * �Ƿ��Զ���������
	 * @return �Զ���������true�����򷵻�false
	 */
	public boolean isAutoStart() {
		if(ctx==null){
			ctx = new HttpContext(true);
		}
		return ctx.isAutoStart();
	}

}

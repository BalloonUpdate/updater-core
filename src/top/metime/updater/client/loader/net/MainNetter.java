package top.metime.updater.client.loader.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import top.metime.updater.client.core.net.NP;
import top.metime.updater.client.loader.view.Window;

public class MainNetter extends NP
{
	private final static byte[] PROTOCOL_HEAD_ACK = {0x23, 0x04, 0x01, 0x34, 0x51, 0x33, 0x35, 0x18};
	
	public static final int NET_PROTOCOL_VERSIONS = 0;
	
	private String host;
	private int port;
	private Socket socket;
	
	private Window window;

	public MainNetter(String host, int port, Window window)
	{
		this.host = host;
		this.port = port;
		this.window = window;
	}
	
	public String getHostAndPort()
	{
		final String text = "Host:_host，Port:_port";
		return text.replaceAll("_host", host).replaceAll("_port", String.valueOf(port));
	}


	public File start() throws UnknownHostException, IOException
	{
		window.bukeshi();
		
		//设置状态提示文本
		window.setStateText("正在连接到服务器。。。("+getHostAndPort()+")");
		
		
		//发起连接
		try{
			socket = new Socket(host, port);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			window.destory();
			JOptionPane.showMessageDialog(null, "无法连接到: "+getHostAndPort(), "连接失败！", 0);
			Runtime.getRuntime().exit(0);
		}
		
		
		//设置IO超时
		window.setStateText("正在设置IO超时。。。");
		socket.setSoTimeout(40000);
		
		
		//延时600
//		try {Thread.sleep(1500);} catch (InterruptedException e) {e.printStackTrace();}

		
		//打开IO流
		window.setStateText("正在打开IO流。。。");
		netIn = new DataInputStream(socket.getInputStream());
		netOut = new DataOutputStream(socket.getOutputStream());
	
		
		//延时600
//		try {Thread.sleep(1500);} catch (InterruptedException e) {e.printStackTrace();}
		
		
		//测试协议
		window.setStateText("正在测试传输协议。。。");
		
		if(!Ack(PROTOCOL_HEAD_ACK))
		{
			window.destory();
			JOptionPane.showMessageDialog(null, "协议测试未通过，请检查端口是否被占用或者设置正确！", "协议错误", 0);
			Runtime.getRuntime().exit(0);
		}
		
		//告诉服务端客户端的协议版本
		writeInt(NET_PROTOCOL_VERSIONS);
		
		//如果协议版服务端无法处理
		if(!readBoolean())
		{
			//读取服务端使用的协议版本
			String serverSNPVer = readString();
			
			window.destory();
			JOptionPane.showMessageDialog(null, "协议版本不支持，当前的协议版本为 "+NET_PROTOCOL_VERSIONS+"\n支持的版本为"+serverSNPVer, "协议版本不支持", 0);
			Runtime.getRuntime().exit(0);
		}
		
		window.setStateText("正在接收内容。。。");
		
//		System.out.println(readString());
//		System.out.println(readString());
//		System.out.println(readString());
		
		//接收文件长度
		long fileLength = netIn.readLong();
		File file = File.createTempFile("updater-executable", ".jar");
		
		file.createNewFile();
		
		FileOutputStream fos = new FileOutputStream(file);
	
		byte[] buf = new byte[4096];
		int rcount = (int)(fileLength / buf.length);
		int dyv = (int)(fileLength % buf.length);
		int cp = 0;
		for (int c = 0; c < rcount; c++)
		{
			netIn.readFully(buf);
			fos.write(buf, 0, buf.length);
			int progress = (int)( ((float)c / (float)rcount)*100);
			window.setStateText("正在接收内容。。。"+progress);
		}

		for (int c = 0; c < dyv; c++)
		{
			fos.write(netIn.readByte());
		}
		fos.close();
		
		return file;
	}
	
	
	public Socket getSocket()
	{
		return socket;
	}

}

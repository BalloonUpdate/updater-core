package cn.innc11.updater.client.core.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONObject;
import cn.innc11.updater.client.core.callback.ui.MainWindowCallback;
import cn.innc11.updater.client.core.structure.DownloadTask;
import cn.innc11.updater.client.core.structure.MFolder;
import cn.innc11.updater.client.core.tools.CompareFolder;

public class Nett extends NP
{
	private static final byte[] ACK_CODE = { 86, 127, 94, 88, 44, 51, 73, 32 };
	
	private String host;
	private int port;
	private Socket socket;
	private int rulesc;
	private HashSet<DownloadTask> downloadQueue = new HashSet<>();
	private MainWindowCallback mWindowCallback;

	public Nett(Socket socket, String host, int port, MainWindowCallback mWindowCallback) throws IOException
	{
		this.socket = socket;
		this.host = host;
		this.port = port;
		this.mWindowCallback = mWindowCallback;
		
		netIn = new DataInputStream(socket.getInputStream());
		netOut = new DataOutputStream(socket.getOutputStream());
	}
	
	private void getFile(File file, String key, long length) throws IOException
	{
		mWindowCallback.changeProgressValue(0); // 重置进度条
		
		file.createNewFile();
		
		if(key.equals("null")) return;
		netOut.writeBoolean(true);//请求下一个文件
		
		netOut.writeUTF(key);
		
		long len = netIn.readLong(); // 接收文件长度

		FileOutputStream fos = new FileOutputStream(file);
	
		byte[] buf = new byte[4096];
		int ac = (int)(length / buf.length);
		int bc = (int)(length % buf.length);
		
		int cp = 0;
		for (int i = 0; i < ac; i++)
		{
			netIn.readFully(buf);
			mWindowCallback.changeProgressValue(++cp * 1000 / ac);
		
			fos.write(buf, 0, buf.length);
		}
	
		for (int i = 0; i < bc; i++)
		{
			fos.write(netIn.readByte());
		}

		fos.close();
	}

	private void download() throws IOException
	{
		int counter = 0;
		for (DownloadTask per : downloadQueue)
		{
			mWindowCallback.appendElement(per.fd.getName() + "     -     " + per.fd.getLength() / 1024L + "Kb     -     " + per.fd.getMD5().toLowerCase());
			mWindowCallback.changeWindowTitleText(String.valueOf(++counter));
//			System.out.println("Name："+per.fd.getName()+"   Length："+per.fd.getLength());
			//延时600
			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
//			try {Thread.sleep(6000);} catch (InterruptedException e) {e.printStackTrace();}
		int currentIndex = 0;
		for (DownloadTask per : downloadQueue)
		{
			currentIndex++;//计数++
			
			mWindowCallback.changeWindowTitleText("队列： "+(currentIndex+"/" + downloadQueue.size()));
			getFile(per.file, per.fd.getMD5(), per.fd.getLength());
			mWindowCallback.removedElement(per.fd.getName() + "     -     " + per.fd.getLength() / 1024L + "Kb     -     " + per.fd.getMD5().toLowerCase());
			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
		}
		netOut.writeBoolean(false);//没有下一个文件
		downloadQueue.clear();
	}

	public void start() throws UnknownHostException, IOException
	{
		rulesc = netIn.readInt(); // 获取需要同步的规则数
		
		mWindowCallback.setProgressIndeterminate(false); // 设置进度条为确定模式
	
		for (int i = 0; i < rulesc; i++)
		{
			String currentProgressText = (i + 1) + "/" + rulesc; // 当前进度文本
			
			mWindowCallback.setProgressIndeterminate(true); // 设置进度条为不确定模式
			
			mWindowCallback.changeStateBarText("正在接收 "+currentProgressText+" 同步规则");
			
			String clientPath = readString();
			String virtualFolder = readString();
			String ignore = readString();
			
			
			File RRootFolder = new File(clientPath); // 真实的根目录
			MFolder VRootFolder = new MFolder(new JSONObject(virtualFolder));//虚拟的根目录
			
			JSONArray ignFiles = new JSONArray(ignore);
			HashSet<String> ignoreFiles = new HashSet<>();
			for(int n=0;n<ignFiles.length();n++)
			{
				ignoreFiles.add(ignFiles.getString(n).replaceAll("\\./", "").replaceAll("\\.\\\\", ""));
//				System.out.println("ignores"+n+" > "+ignFiles.getString(n).replaceAll("\\./", "").replaceAll("\\.\\\\", ""));调试输出
			}
			
//			if(ignoreFiles)
			
			//对比文件
			mWindowCallback.changeStateBarText("正在对比文件，进度 "+currentProgressText);
			CompareFolder comparer = new CompareFolder(VRootFolder, RRootFolder, ignoreFiles);
			downloadQueue = comparer.compare();
			
			//设置进度条为确定模式
			mWindowCallback.setProgressIndeterminate(false);
			
			//下载文件
			mWindowCallback.changeStateBarText("总进度 "+currentProgressText);
			download();
		}
		
		socket.close();//关闭套接字
	}
}

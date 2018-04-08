package top.metime.updater.client.core.net;

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
import top.metime.updater.client.core.callback.ui.MainWindowCallback;
import top.metime.updater.client.core.memory.DownloadTask;
import top.metime.updater.client.core.memory.MFolder;
import top.metime.updater.client.core.tools.CompareFolder;

public class MainNetter extends NP
{
	private static final byte[] ACK_CODE = { 86, 127, 94, 88, 44, 51, 73, 32 };
	
	private String host;
	private int port;
	private Socket socket;
	private int rulesc;
	private HashSet<DownloadTask> downloadQueue = new HashSet<>();
	private MainWindowCallback mWindowCallback;

	public MainNetter(Socket socket, String host, int port, MainWindowCallback mWindowCallback) throws IOException
	{
		this.socket = socket;
		this.host = host;
		this.port = port;
		this.mWindowCallback = mWindowCallback;
		
		netIn = new DataInputStream(socket.getInputStream());
		netOut = new DataOutputStream(socket.getOutputStream());
	}
	
	private void netToFile(File file, String key, long length) throws IOException
	{
		//重置进度条
		mWindowCallback.changeProgressValue(0);
		
		file.createNewFile();
		
		if(key.equals("null")) return;
		netOut.writeBoolean(true);//请求下一个文件
		
		netOut.writeUTF(key);
		
		//无用 接收文件长度
		long len = netIn.readLong();
//		System.out.println("RecvFilelen："+len);
		
		FileOutputStream fos = new FileOutputStream(file);
	
		byte[] buf = new byte[4096];
		int ac = (int)(length / buf.length);
		int bc = (int)(length % buf.length);
		
//		System.out.println("len："+length+"   ac："+ac+"   bc："+bc+"    NetToFile64");
		
		int cp = 0;
		for (int c = 0; c < ac; c++)
		{
			netIn.readFully(buf);
			mWindowCallback.changeProgressValue(++cp * 1000 / ac);
		
			fos.write(buf, 0, buf.length);
		}
	
		for (int c = 0; c < bc; c++)
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
			mWindowCallback.appendElement(per.fd.getName() + "     -     " + per.fd.getLength() / 1024L + "Kb     -     MD5:  " + per.fd.getMD5().toUpperCase());
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
			
			mWindowCallback.changeWindowTitleText("当前规则的下载进度计数 "+(currentIndex+"/" + downloadQueue.size()));
			netToFile(per.file, per.fd.getMD5(), per.fd.getLength());
			mWindowCallback.removedElement(per.fd.getName() + "     -     " + per.fd.getLength() / 1024L + "Kb     -     MD5:  " + per.fd.getMD5().toUpperCase());
			try {Thread.sleep(20);} catch (InterruptedException e) {e.printStackTrace();}
		}
		netOut.writeBoolean(false);//没有下一个文件
		downloadQueue.clear();
	}

	/**
	 * 类的入口方法
	 *
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void start() throws UnknownHostException, IOException
	{
		//获取需要同步的规则数
		rulesc = netIn.readInt();//NP.java: readInt();
		
		//设置进度条为确定模式
		mWindowCallback.setProgressIndeterminate(false);
	
		for (int c = 0; c < rulesc; c++)
		{
			//当前进度文本
			String currentProgressText = (c + 1) + "/" + rulesc;
			
			//设置进度条为不确定模式
			mWindowCallback.setProgressIndeterminate(true);
			
			mWindowCallback.changeStateBarText("正在接收 "+currentProgressText+" 同步规则");
			
			String clientPath = readString();//NP.java: readString();
			String virtualFolder = readString();//NP.java: readString();
			String ignore = readString();//NP.java: readString();
			
			
			File RRootFolder = new File(clientPath);//真实的根目录
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
			mWindowCallback.changeStateBarText("正在对比文件，规则总进度 "+currentProgressText);
			CompareFolder comparer = new CompareFolder(VRootFolder, RRootFolder, ignoreFiles);
			downloadQueue = comparer.compare();
			
			//设置进度条为确定模式
			mWindowCallback.setProgressIndeterminate(false);
			
			//下载文件
			mWindowCallback.changeStateBarText("正在下载文件，规则总进度 "+currentProgressText);
			download();
		}
		
		socket.close();//关闭套接字
	}
}

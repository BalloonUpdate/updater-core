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
import cn.innc11.updater.client.core.view.MainWindowCallback;
import cn.innc11.updater.client.core.structure.DownloadTask;
import cn.innc11.updater.client.core.structure.RemoteFolder;
import cn.innc11.updater.client.core.tools.CompareFolder;

public class Network extends NP
{
	private static final byte[] ACK_CODE = { 86, 127, 94, 88, 44, 51, 73, 32 };
	
	private Socket socket;
	private int rulesc;
	private HashSet<DownloadTask> downloadQueue = new HashSet<>();
	private MainWindowCallback mWindowCallback;

	public Network(Socket socket, MainWindowCallback mWindowCallback) throws IOException
	{
		this.socket = socket;
		this.mWindowCallback = mWindowCallback;
		
		netIn = new DataInputStream(socket.getInputStream());
		netOut = new DataOutputStream(socket.getOutputStream());
	}
	
	private void receiveFile(File file, String key, long length) throws IOException
	{
		mWindowCallback.changeProgressValue(0);
		
		file.createNewFile();
		
		if(key.equals("null")) return; // the KEY is the md5

		netOut.writeBoolean(true);//请求下一个文件
		
		netOut.writeUTF(key);
		
		long len = netIn.readLong(); // 接收文件长度

		FileOutputStream fos = new FileOutputStream(file);
	
		byte[] buf = new byte[4096];
		int ac = (int)(length / buf.length);
		int bc = (int)(length % buf.length);

		String titleTextBuf = mWindowCallback.getWindowTitleText();

		int cp = 0;
		for (int i = 0; i < ac; i++)
		{
			netIn.readFully(buf);
			mWindowCallback.changeProgressValue(++cp * 1000 / ac);
			mWindowCallback.changeWindowTitleText(titleTextBuf+"("+(cp * 100 / ac)+")");
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
			mWindowCallback.changeWindowTitleText("收集信息("+(++counter)+")");
			try {Thread.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
		}
		
		int currentIndex = 0;
		for (DownloadTask per : downloadQueue)
		{
			currentIndex++;
			
			mWindowCallback.changeWindowTitleText("下载队列 "+(downloadQueue.size()-currentIndex+1));
			receiveFile(per.file, per.fd.getMD5(), per.fd.getLength());
			mWindowCallback.removedElement(per.fd.getName() + "     -     " + per.fd.getLength() / 1024L + "Kb     -     " + per.fd.getMD5().toLowerCase());
			try {Thread.sleep(3);} catch (InterruptedException e) {e.printStackTrace();}
		}
		netOut.writeBoolean(false);//没有下一个文件
		downloadQueue.clear();
	}

	public void start() throws UnknownHostException, IOException
	{
		rulesc = netIn.readInt(); // 获取需要同步的规则数
		
		mWindowCallback.setProgressIndeterminate(false);
	
		for (int i = 0; i < rulesc; i++)
		{
			String currentProgressText = (i + 1) + "/" + rulesc; // 当前进度文本(规则总数)
			
			mWindowCallback.setProgressIndeterminate(true);
			
			mWindowCallback.changeStateBarText("接收规则 "+currentProgressText);
			
			String clientPath = readString();
			String remoteFiles = readString();
			String ignores = readString();

			File localFolder = new File(clientPath.isEmpty()? ".":clientPath);
			RemoteFolder remoteFolder = new RemoteFolder(new JSONObject(remoteFiles));
			
			JSONArray ignFiles = new JSONArray(ignores);
			HashSet<String> ignoreFiles = new HashSet<>();
			for(int n=0;n<ignFiles.length();n++)
			{
				ignoreFiles.add(ignFiles.getString(n).replaceAll("\\./", "").replaceAll("\\.\\\\", ""));
//				System.out.println("ignores"+n+" > "+ignFiles.getString(n).replaceAll("\\./", "").replaceAll("\\.\\\\", ""));调试输出
			}
			
			mWindowCallback.changeStateBarText("正在比较文件 "+currentProgressText);
			downloadQueue = new CompareFolder(remoteFolder, localFolder, ignoreFiles).compare();
			
			mWindowCallback.setProgressIndeterminate(false);
			
			mWindowCallback.changeStateBarText("总进度 "+currentProgressText);
			download();
		}
		
		socket.close();//关闭套接字
	}
}

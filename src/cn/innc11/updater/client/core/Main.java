package cn.innc11.updater.client.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import cn.innc11.updater.client.core.net.Network;
import cn.innc11.updater.client.core.view.MyWindow;

public class Main 
{
	public void main(Socket socket, String host, int port, String launcherFileName) throws FileNotFoundException, IOException, IllegalArgumentException, IllegalAccessException, InterruptedException
	{
		MyWindow w = new MyWindow();
		
		Network net = new Network(socket, w);
		net.start();

		w.setWindowTitleText("updater客户端(完成)");
		w.setStateBarText("完成!");
		Thread.sleep(3000);
		w.destory();
		
		if(!launcherFileName.equalsIgnoreCase("null"))
		{
			startPrograme(launcherFileName);//启动启动器
		}
		
	}
	
	private void startPrograme(String launcherFileName) throws IOException
	{
		if(!launcherFileName.equals("null"))
		{
			File launcher = new File(launcherFileName);
			if (launcher.exists())
			{
				Runtime.getRuntime().exec(launcher.getAbsolutePath());
			}
		}
	}
	
}

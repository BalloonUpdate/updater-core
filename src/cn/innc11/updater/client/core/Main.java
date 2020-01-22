package cn.innc11.updater.client.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.Socket;
import java.util.Properties;
import javax.swing.JOptionPane;
import cn.innc11.updater.client.core.structure.MConfig;
import cn.innc11.updater.client.core.net.Nett;
import cn.innc11.updater.client.core.view.Window;

public class Main 
{
	private final String propertiesFileName = "config.properties";
	
	public void main(Socket socket, String host, int port, String launcherFileName) throws FileNotFoundException, IOException, IllegalArgumentException, IllegalAccessException, InterruptedException {
		//创建窗口
		Window w = new Window();
		
		Nett net = new Nett(socket, host, port, w);
		net.start();
		
		w.setStateBarText("完成！");

		Thread.sleep(3000);

		w.destory();
		
		if(!launcherFileName.equalsIgnoreCase("null"))
		{
			startLaunch(launcherFileName);//启动启动器
		}
		
	}
	
	private void startLaunch(String launcherFileName) throws IOException
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
	
	private void showCompletedDialog(String showCompletedDialog)
	{
		if(!showCompletedDialog.equals("null"))
		{
			JOptionPane.showMessageDialog(null, showCompletedDialog, "", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	
	
	private MConfig getMConfig(InputStream inputStream) throws IOException, IllegalArgumentException, IllegalAccessException
	{
		MConfig mconfig = new MConfig();
		
		Properties prop = new Properties();
		prop.load(inputStream);
		
		Field[] fields = mconfig.getClass().getDeclaredFields();
		for(Field per : fields)
		{
			int mdf = per.getModifiers();
			if(Modifier.isPublic(mdf) || !Modifier.isStatic(mdf))
			{
				String value = prop.getProperty(per.getName());
				if(per.getType()==String.class)
				{
					per.set(mconfig, value);
				}else
				if(per.getType()==int.class)
				{
					per.set(mconfig, Integer.parseInt(value));
				}else
				if(per.getType()==boolean.class)
				{
					per.set(mconfig, Boolean.parseBoolean(value));
				}
				
			}
		}
		
		return mconfig;
	}
		
}

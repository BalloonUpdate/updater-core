package top.metime.updater.client.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import top.metime.updater.client.loader.memory.MConfig;
import top.metime.updater.client.loader.net.MainNetter;
import top.metime.updater.client.loader.view.Window;

public class MainLoader 
{
	private final String propertiesFileName = "config.properties";
	
	public static void main(String[] args) throws IOException, MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
	{
		new MainLoader().main();
	}
	
	private void main() throws MalformedURLException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException
	{
		//创建窗口
		Window w = new Window();
		
		File outlineConfigFile = new File(propertiesFileName);
		InputStream inputStream = null;
		
		// 载入外联配置文件
		if(outlineConfigFile.exists()||outlineConfigFile.isFile())
		{
			inputStream = new FileInputStream(outlineConfigFile);
		}else{//载入内联配置文件
			inputStream = getClass().getResourceAsStream("/"+propertiesFileName);
		}
		
		MConfig mconfig = getMConfig(inputStream);
		
		//关闭配置文件输入流 
		inputStream.close();
		
		MainNetter net = new MainNetter(mconfig.host, mconfig.port, w);
		File jar = net.start();
		
		w.destory();
		
		
		URL url = jar.toURI().toURL();
		URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, Thread.currentThread().getContextClassLoader());
		String main = "top.metime.updater.client.core.Main";
		Class<?> mc = classLoader.loadClass(main);
		
		//Constructor<?> cons = mc.getConstructor();
		
		Object coreObj = mc.newInstance();
		
		Method coreMain = mc.getDeclaredMethod("main", Socket.class, String.class, int.class, String.class);
		
		coreMain.invoke(coreObj, net.getSocket(), mconfig.host, mconfig.port, mconfig.launcherFileName);
		
		
		classLoader.close();
		
		
		
		
		//startLaunch(mconfig.launcherFileName);//启动启动器
		//尝试注释掉这段代码  (因为会出现启动2个启动器的问题
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

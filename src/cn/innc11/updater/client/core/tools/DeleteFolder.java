package cn.innc11.updater.client.core.tools;

import java.io.File;

/**
 * 该类提供方便的删除文件或者文件夹的方法
 *
 * @author innc-table
 */
public class DeleteFolder 
{

	/**
	 * 方便的删除文件或者文件夹的方法
	 *
	 * @param dir 文件或者文件夹对象
	 */
	public static void delfolder(File dir)
	{
		if (!dir.exists())
		return;
		
		if (dir.isFile())
		{
			dir.delete();
			return;
		}
		
		for (File per : dir.listFiles())
		{
			delfolder(per);
		}
		
		dir.delete();
	}
}

package top.metime.updater.client.core.tools;

import java.io.File;

public class DeleteFolder 
{
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

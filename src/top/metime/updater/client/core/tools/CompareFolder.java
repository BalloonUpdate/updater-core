package top.metime.updater.client.core.tools;

import java.io.File;
import java.util.HashSet;
import top.metime.updater.client.core.memory.DownloadTask;
import top.metime.updater.client.core.memory.MFile;
import top.metime.updater.client.core.memory.MFileOrFolder;
import top.metime.updater.client.core.memory.MFolder;

public class CompareFolder 
{
	private MFolder unrealDir;
	private File realDir;
	private HashSet<String> ignoreFiles;
	
	private HashSet<DownloadTask> willDownload;
	
	
	public CompareFolder(MFolder unrealDir, File realDir, HashSet<String> ignoreFiles)
	{
		this.unrealDir = unrealDir;
		this.realDir = realDir;
		this.ignoreFiles = ignoreFiles;
		
		willDownload = new HashSet<>();
	}
	
	public HashSet<DownloadTask> compare()
	{
		compare(unrealDir, realDir);
		return willDownload;
	}
	
	private void compare(MFolder unrealDir, File realDir)
	{
		positiveCompare(unrealDir, realDir);
		contrastiveCompare(unrealDir, realDir);
	}
	
	
	private void realFileUnreal(MFolder unrealFolder, File realSubFile)
	{
		boolean exists = false;
		
		for (MFileOrFolder per : unrealFolder.getAllList())
		{
			if ((per instanceof MFile)||per.getName().equals(realSubFile.getName()))
			{
				exists = true;
				break;
			}
		}
		if (!exists)
		{
			realSubFile.delete();
		}
	}
	
	private void realFolderUnreal(MFolder unrealFolder, File realSubFile)
	{
		boolean exists = false;
		
		for (MFileOrFolder per : unrealFolder.getAllList())
		{
			if((per instanceof MFolder)||per.getName().equals(realSubFile.getName()))
			{
				exists = true;
				break;
			}
		}
		if (!exists)
		{
			DeleteFolder.delfolder(realSubFile);
		}
	}
	
	
	private void positiveCompare(MFolder unrealDir, File realDir)
	{
		if (realDir.exists())//如果实际根目录存在
		{
			for (File perl : realDir.listFiles())//循环实际文件夹子文件
			{
				//ignoreFiles
				if(!ignoreFiles.contains(perl.getAbsolutePath().substring((int) (new File("").getAbsolutePath().length()+1)).replace('/', '?').replace('\\', '?')))
				{
					if (perl.isFile())
					{
						realFileUnreal(unrealDir, perl);
					}else{
						realFolderUnreal(unrealDir, perl);
					}
				}
			}
		}else{
			realDir.mkdirs();//创建实际根目录
		}
		
	}
	private void contrastiveCompare(MFolder unrealDir, File realDir)
	{
		for (MFileOrFolder per : unrealDir.getAllList())//循环虚拟目录
		{
			
			if ((per instanceof MFolder))//如果是M文件夹
			{
				MFolder d = (MFolder)per;
				File ldir = new File(realDir, d.getName());
				ldir.mkdirs();
				compare(d, ldir);
			}
			else//如果是M文件
			if ((per instanceof MFile))
			{
				MFile fd = (MFile)per;
				compareFile(fd, new File(realDir, fd.getName()));
			}
		}
	}
	
	
	
	
	private void compareFile(MFile unrealFolder, File file)
	{
		if(!ignoreFiles.contains(file.getAbsolutePath().substring((int) (new File("").getAbsolutePath().length()+1)).replace('/', '?').replace('\\', '?')))
		{
			if (file.exists())
			{
				if (file.isFile())
				{
					if (!MD5.getMD5(file).equals(unrealFolder.getMD5()))
					{
						DeleteFolder.delfolder(file);
						willDownload.add(new DownloadTask(file, unrealFolder));
					}
				}
				else
				{
					DeleteFolder.delfolder(file);
					willDownload.add(new DownloadTask(file, unrealFolder));
				}
			}
			else
			{
				willDownload.add(new DownloadTask(file, unrealFolder));
			}
		}
		
	}
	
	
	
}

package top.metime.updater.client.core.tools;

import java.io.File;
import java.util.HashSet;
import top.metime.updater.client.core.memory.DownloadTask;
import top.metime.updater.client.core.memory.MFile;
import top.metime.updater.client.core.memory.MFileOrFolder;
import top.metime.updater.client.core.memory.MFolder;

public class CompareFolder 
{
	private MFolder VFile;
	private File RFile;
	private HashSet<String> ignoreFiles;
	
	private HashSet<DownloadTask> willDownload;
	
	
	public CompareFolder(MFolder unrealDir, File realDir, HashSet<String> ignoreFiles)
	{
		this.VFile = unrealDir;
		this.RFile = realDir;
		this.ignoreFiles = ignoreFiles;
		
		willDownload = new HashSet<>();
	}
	
	public HashSet<DownloadTask> compare()
	{
		compare(VFile, RFile);
		return willDownload;
	}
	
	private void compare(MFolder VFile, File RFile)
	{
		positiveCompare(VFile, RFile);
		contrastiveCompare(VFile, RFile);
	}
	
	/**
	 * 正向对比，计算需要删除的文件
	 * 
	 * @param VFile 虚拟文件夹对象
	 * @param RFile 真实文件夹对象
	 */
	private void positiveCompare(MFolder VFile, File RFile)
	{
		//只计算需要删除的文件
		if (RFile.exists())//如果真实文件夹存在
		{
			for (File perRsub : RFile.listFiles())//循环真实文件夹子文件
			{
				String currentPath = new File("").getAbsolutePath();
				String relativePath = perRsub.getAbsolutePath().substring(currentPath.length()+1);
				relativePath = relativePath.replaceAll("\\./", "").replaceAll("\\.\\\\", "");
				relativePath = relativePath.replaceAll("\\\\", "/").replaceAll("^.*?/", "");
				//此处为正则表达式，4个反斜杠才代表一个反斜杠
		
				//如果没有被忽略掉
//				System.out.println(">>>>   "+relativePath);//调试用
				if(!ignoreFiles.contains(relativePath))
				{
					if(VFile.contains(perRsub))//如果虚拟文件夹包含
					{
						if(perRsub.isDirectory())//如果perRsub是一个目录
						{
							//进一步计算
							positiveCompare((MFolder) VFile.getFileOrFolder(perRsub.getName()), perRsub);
						}else{//如果perRsub是一个文件
							MFile vf = (MFile) VFile.getFileOrFolder(perRsub.getName());
							File  rf = perRsub;
							
							String rMd5 = MD5.getMD5(rf);
							String vMd5 = vf.getMD5();
							
							//如果校验值不匹配
							if(!rMd5.equals(vMd5))
							{
								DeleteFolder.delfolder(rf);
							}
						}
					}else{//如果虚拟文件夹不包含
						DeleteFolder.delfolder(perRsub);
					}
				}
			}
		}else{
			//RFile.mkdirs();//创建实际目录
		}
		
	}
	
	/**
	 * 反向对比，计算要下载的文件
	 * 
	 * @param VFile 虚拟文件夹对象
	 * @param RFile 真实文件夹对象
	 */
	private void contrastiveCompare(MFolder VFile, File RFile)
	{
		for (MFileOrFolder perVsub : VFile.getAllList())//循环虚拟目录
		{
			if ((perVsub instanceof MFolder))//如果是perV是文件夹
			{
				MFolder vfolder = (MFolder)perVsub;
				File rfolder = new File(RFile, vfolder.getName());
				
				if(rfolder.exists())//如果实际文件夹存在
				{
					//进一步计算
					contrastiveCompare(vfolder, rfolder);
				}else{//如果实际文件夹不存在
					//创建文件夹
					rfolder.mkdirs();
					
					//进一步计算
					contrastiveCompare(vfolder, rfolder);
				}
			}
			else//如果是perV子文件是文件
			if ((perVsub instanceof MFile))
			{
				MFile mfile = (MFile)perVsub;
				File localFile = new File(RFile, mfile.getName());
				
//				compareFile(mfile, new File(RFile, mfile.getName()));
				String currentPath = new File("").getAbsolutePath();
				String relativePath = localFile.getAbsolutePath().substring(currentPath.length()+1);
				relativePath = relativePath.replaceAll("\\./", "").replaceAll("\\.\\\\", "");
				relativePath = relativePath.replaceAll("\\\\", "/").replaceAll("^.*?/", "");
				//此处为正则表达式，4个反斜杠才代表一个反斜杠
				
				if(!ignoreFiles.contains(relativePath))
				{
					if(!localFile.exists())
					{
						willDownload.add(new DownloadTask(localFile, mfile));
					}
				}
			}
		}
	}
	
	
}

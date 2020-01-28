package cn.innc11.updater.client.core.tools;

import java.io.File;
import java.util.HashSet;
import cn.innc11.updater.client.core.structure.DownloadTask;
import cn.innc11.updater.client.core.structure.RemoteFile;
import cn.innc11.updater.client.core.structure.RemoteObject;
import cn.innc11.updater.client.core.structure.RemoteFolder;

public class CompareFolder 
{
	private RemoteFolder remoteFolder;
	private File localFolder;
	private HashSet<String> ignoreFiles;
	
	private HashSet<DownloadTask> willDownload = new HashSet<>();;
	
	public CompareFolder(RemoteFolder a, File b, HashSet<String> ignoreFiles_)
	{
		remoteFolder = a;
		localFolder = b;
		ignoreFiles = ignoreFiles_;
	}
	
	public HashSet<DownloadTask> compare()
	{
		compare(remoteFolder, localFolder);
		return willDownload;
	}
	
	private void compare(RemoteFolder remote, File local)
	{
		positiveCompare(remote, local);
		contrastiveCompare(remote, local);
	}
	
	private void positiveCompare(RemoteFolder remote, File local)
	{
		if (local.exists())
		{
			for (File everyLocalFile : local.listFiles())
			{
				String currentPath = new File("").getAbsolutePath();
				String relativePath = everyLocalFile.getAbsolutePath().substring(currentPath.length()+1);

				relativePath = relativePath.replaceAll("\\./", "").replaceAll("\\.\\\\", "");
				relativePath = relativePath.replaceAll("\\\\", "/").replaceAll("^.*?/", "");

				if(!ignoreFiles.contains(relativePath))
				{
					if(remote.contains(everyLocalFile))
					{
						if(everyLocalFile.isDirectory())
						{
							positiveCompare((RemoteFolder) remote.getFileOrFolder(everyLocalFile.getName()), everyLocalFile.getAbsoluteFile());
						}else{
							RemoteFile rf = (RemoteFile) remote.getFileOrFolder(everyLocalFile.getName());
							File lf = everyLocalFile;
							
							String localMd5 = MD5.getMD5(lf);
							String remoteMd5 = rf.getMD5();

							//System.out.println("MD5");
							//System.out.println("Local:  "+localMd5);
							//System.out.println("Remote: "+remoteMd5);

							if(!localMd5.equals(remoteMd5))
							{
								CompareFolder.delfolder(lf);
							}
						}
					}else{
						CompareFolder.delfolder(everyLocalFile);
					}
				}
			}
		}else{
			//RFile.mkdirs();//创建实际目录
		}
		
	}

	private void contrastiveCompare(RemoteFolder remote, File local)
	{
		for (RemoteObject everyRemoteFile : remote.getAllList())
		{
			if ((everyRemoteFile instanceof RemoteFolder))
			{
				RemoteFolder remoteFolder = (RemoteFolder)everyRemoteFile;
				File localFolder = new File(local.getAbsoluteFile(), remoteFolder.getName());

				if(localFolder.exists())
				{
					contrastiveCompare(remoteFolder, localFolder);
				}else{
					localFolder.mkdirs();
					contrastiveCompare(remoteFolder, localFolder);
				}
			}
			else if ((everyRemoteFile instanceof RemoteFile))
			{
				RemoteFile remoteFile = (RemoteFile)everyRemoteFile;

				File localFile = new File(local.getAbsoluteFile(), remoteFile.getName());

				if(localFile.exists() && localFile.length()==0)
				{
					continue;
				}

				String currentPath = new File("").getAbsolutePath();
				String relativePath = localFile.getAbsolutePath().substring(currentPath.length()+1);
				relativePath = relativePath.replaceAll("\\./", "").replaceAll("\\.\\\\", "");
				relativePath = relativePath.replaceAll("\\\\", "/").replaceAll("^.*?/", "");
				
				if(!ignoreFiles.contains(relativePath))
				{
					if(!localFile.exists())
					{
						willDownload.add(new DownloadTask(localFile, remoteFile));
					}
				}
			}
		}
	}

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

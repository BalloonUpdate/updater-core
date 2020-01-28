package cn.innc11.updater.client.core.structure;

import java.io.File;

public class DownloadTask
{
	public File file;
	public RemoteFile fd;

	public DownloadTask(File RFile, RemoteFile VFile)
	{
		this.fd = VFile;
		this.file = RFile;
	}
}

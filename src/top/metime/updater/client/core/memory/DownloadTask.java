package top.metime.updater.client.core.memory;

import java.io.File;

public class DownloadTask 
{
	public File file;
	public MFile fd;

	public DownloadTask(File f, MFile fd)
	{
		this.fd = fd;
		this.file = f;
	}
}

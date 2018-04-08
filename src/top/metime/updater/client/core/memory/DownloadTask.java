package top.metime.updater.client.core.memory;

import java.io.File;

/**
 * 此类代表一个下载任务
 *
 * @author innc-table
 */
public class DownloadTask 
{

	/**
	 * 需要下载的实际文件对象
	 */
	public File file;

	/**
	 * 需要下载的虚拟文件对象，主要用于获取md和大小
	 */
	public MFile fd;

	/**
	 * 根据一个实际文件对象和一个虚拟文件对象创建一个下载任务
	 *
	 * @param RFile 实际文件对象
	 * @param VFile 虚拟文件对象
	 */
	public DownloadTask(File RFile, MFile VFile)
	{
		this.fd = VFile;
		this.file = RFile;
	}
}

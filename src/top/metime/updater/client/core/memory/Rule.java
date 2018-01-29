package top.metime.updater.client.core.memory;

import java.io.File;
import java.util.HashMap;

public class Rule
{
	private HashMap<String, File> dict;
	private final MFolder dd;
	private final String remotePath;
	
	public Rule(MFolder dd, HashMap<String, File> dict, String remotePath)
	{
		this.dd = dd;
		this.dict = dict;
		this.remotePath = remotePath;
	}
	
	public MFolder getRootDir()
	{
		return dd;
	}
	
	public HashMap<String, File> getDictionary()
	{
		return dict;
	}
	
	public String getRemoteClientPath()
	{
		return remotePath;
	}
}

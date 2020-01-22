package cn.innc11.updater.client.core.structure;

import java.io.File;
import java.util.HashMap;

public class RuleInstance
{
	private HashMap<String, File> dict;
	private final MFolder dd;
	private final String remotePath;
	
	public RuleInstance(MFolder dd, HashMap<String, File> dict, String remotePath)
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

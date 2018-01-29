package top.metime.updater.client.core.tools;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

public class Dictionary implements Serializable
{
	private static final long serialVersionUID = 1L;
	private HashMap<String, File> dict;

	public Dictionary()
	{
		this.dict = new HashMap<>();
	}

	public boolean containsKey(String key)
	{
		return this.dict.containsKey(key);
	}

	public void addEntry(String key, File file)
	{
		this.dict.put(key, file);
	}

	public File getFile(String key)
	{
		return (File)this.dict.get(key);
	}

	public void clearAll()
	{
		this.dict.clear();
	}
}

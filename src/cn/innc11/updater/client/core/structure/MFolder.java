package cn.innc11.updater.client.core.structure;

import java.io.File;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONObject;

public class MFolder extends MFileOrFolder
{
	private final LinkedList<MFileOrFolder> sublist = new LinkedList<>();
	
	public MFolder(String name)
	{
		this.name = name;
	}
	public MFolder(JSONObject ObjString)
	{
		name = ObjString.getString("name");
		JSONArray array = ObjString.getJSONArray("child");
		
		for(int c=0;c<array.length();c++)
		{
			JSONObject obj = array.getJSONObject(c);
			//System.out.println(array.get(c));
			if(obj.has("child"))
			{
				sublist.add(new MFolder(obj));
			}else{
				sublist.add(new MFile(obj));
			}
			
		}
	}
	
	public void append(MFileOrFolder d)
	{
		sublist.add(d);
	}
	
	/**
	 * 根据文件名来获取Virtual子文件对象
	 * 
	 * @param fileName 子文件对象文件名
	 * @return 子文件对象，可能是一个MFolder类型也可能是一个MFile类型，如果不存在返回null
	 */
	public MFileOrFolder getFileOrFolder(String fileName)
	{
		for(MFileOrFolder per : sublist)
		{
			if(per.getName().equals(fileName))
			{
				return per;
			}
		}
		return null;
	}
	
	/**
	 * 是否包含指定java.io.File对象，如果文件夹和文件类型不匹配则返回false
	 * 
	 * @param file java.io.File对象
	 * @return 如果包含返回true，不包含返回false
	 */
	public boolean contains(File file)
	{
		if(file.isFile())
		{
			for(MFileOrFolder per : getAllList())
			{
				if(per instanceof MFile && per.getName().equals(file.getName()))
				{
					return true;
				}
			}
		}else{
			for(MFileOrFolder per : getAllList())
			{
				if(per instanceof MFolder && per.getName().equals(file.getName()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 是否包含指定MFileOrFolder对象，如果文件夹和文件类型不匹配则返回false
	 * 
	 * @param mfile top.metime.updater.client.core.memory.MFileOrFolder对象
	 * @return 如果包含返回true，不包含返回false
	 */
	public boolean contains(MFileOrFolder mfile)
	{
		if(mfile instanceof MFile)
		{
			for(MFileOrFolder per : getAllList())
			{
				if(per instanceof MFile && per.getName().equals(mfile.getName()))
				{
					return true;
				}
			}
		}else{
			for(MFileOrFolder per : getAllList())
			{
				if(per instanceof MFolder && per.getName().equals(mfile.getName()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public LinkedList<MFileOrFolder> getAllList()
	{
		return sublist;
	}
	
	@Override
	public String toString() 
	{
		JSONObject obj = new JSONObject();
		JSONArray child = new JSONArray();
		
		for(MFileOrFolder per : getAllList())
		{
			child.put(per.toJSONObject());
		}
		
		obj.put("name", getName());
		obj.put("child", child);
		
		return obj.toString();
		
	}
	@Override
	public JSONObject toJSONObject() 
	{
		JSONObject obj = new JSONObject();
		JSONArray child = new JSONArray();
		
		for(MFileOrFolder per : getAllList())
		{
			child.put(per.toJSONObject());
		}
		
		obj.put("name", getName());
		obj.put("child", child);
		
		return obj;
	}

}

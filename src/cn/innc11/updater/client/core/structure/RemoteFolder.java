package cn.innc11.updater.client.core.structure;

import java.io.File;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONObject;

public class RemoteFolder extends RemoteObject
{
	private final LinkedList<RemoteObject> sublist = new LinkedList<>();
	
	public RemoteFolder(String name)
	{
		this.name = name;
	}
	public RemoteFolder(JSONObject ObjString)
	{
		name = ObjString.getString("name");
		JSONArray array = ObjString.getJSONArray("child");
		
		for(int c=0;c<array.length();c++)
		{
			JSONObject obj = array.getJSONObject(c);
			if(obj.has("child"))
			{
				sublist.add(new RemoteFolder(obj));
			}else{
				sublist.add(new RemoteFile(obj));
			}
			
		}
	}
	
	public void append(RemoteObject d)
	{
		sublist.add(d);
	}
	
	public RemoteObject getFileOrFolder(String fileName)
	{
		for(RemoteObject per : sublist)
		{
			if(per.getName().equals(fileName))
			{
				return per;
			}
		}
		return null;
	}
	
	public boolean contains(File file)
	{
		if(file.isFile())
		{
			for(RemoteObject per : getAllList())
			{
				if(per instanceof RemoteFile && per.getName().equals(file.getName()))
				{
					return true;
				}
			}
		}else{
			for(RemoteObject per : getAllList())
			{
				if(per instanceof RemoteFolder && per.getName().equals(file.getName()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean contains(RemoteObject mfile)
	{
		if(mfile instanceof RemoteFile)
		{
			for(RemoteObject per : getAllList())
			{
				if(per instanceof RemoteFile && per.getName().equals(mfile.getName()))
				{
					return true;
				}
			}
		}else{
			for(RemoteObject per : getAllList())
			{
				if(per instanceof RemoteFolder && per.getName().equals(mfile.getName()))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public LinkedList<RemoteObject> getAllList()
	{
		return sublist;
	}
	
	@Override
	public String toString() 
	{
		JSONObject obj = new JSONObject();
		JSONArray child = new JSONArray();
		
		for(RemoteObject per : getAllList())
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
		
		for(RemoteObject per : getAllList())
		{
			child.put(per.toJSONObject());
		}
		
		obj.put("name", getName());
		obj.put("child", child);
		
		return obj;
	}

}

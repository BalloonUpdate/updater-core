package top.metime.updater.client.core.memory;

import java.io.File;
import java.util.HashMap;

import org.json.JSONObject;
import top.metime.updater.client.core.tools.MD5;
	
public abstract class MFileOrFolder
{
	protected String name;
	
	public String getName()
	{
		return name;
	}
	
	public abstract JSONObject toJSONObject();
	
	public static class Builder 
	{
		private HashMap<String, File> dict = new HashMap<String, File>();
		private Rule desc;
		
		public Builder(File serv_local, String client_remote)
		{
			MFolder root = new MFolder(serv_local.getName());
			wle(serv_local, root);
			desc = new Rule(root, dict, client_remote);
		}
		
		private void wle(File directory, MFolder parent)
		{
			for(File per : directory.listFiles())
			{
				if(per.isFile())
				{
					if(per.length()>0)
					{
						String md5 = MD5.getMD5(per);
						parent.append(new top.metime.updater.client.core.memory.MFile(per.getName(), per.length(), md5));
						dict.put(md5, per);
					}
				}
				else
				{
					MFolder sub = new MFolder(per.getName());
					parent.append(sub);
					wle(per, sub);
				}
			}
		}
		
		public Rule getRule()
		{
			return desc;
		}
	}
}

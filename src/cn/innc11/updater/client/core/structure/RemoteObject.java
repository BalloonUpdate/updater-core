package cn.innc11.updater.client.core.structure;

import java.io.File;
import java.util.HashMap;

import cn.innc11.updater.client.core.tools.MD5;
import org.json.JSONObject;

public abstract class RemoteObject
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
		private RuleInstance desc;
		
		public Builder(File serv_local, String client_remote)
		{
			RemoteFolder root = new RemoteFolder(serv_local.getName());
			wle(serv_local, root);
			desc = new RuleInstance(root, dict, client_remote);
		}
		
		private void wle(File directory, RemoteFolder parent)
		{
			for(File per : directory.listFiles())
			{
				if(per.isFile())
				{
					if(per.length()>0)
					{
						String md5 = MD5.getMD5(per);
						parent.append(new RemoteFile(per.getName(), per.length(), md5));
						dict.put(md5, per);
					}
				}
				else
				{
					RemoteFolder sub = new RemoteFolder(per.getName());
					parent.append(sub);
					wle(per, sub);
				}
			}
		}
		
		public RuleInstance getRule()
		{
			return desc;
		}
	}
}

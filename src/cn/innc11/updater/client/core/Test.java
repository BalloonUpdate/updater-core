package cn.innc11.updater.client.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Set;
import org.json.JSONObject;

public class Test
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		StringBuilder sb = new StringBuilder();
		File file = new File("");
		
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String tmp = null;
		
		while((tmp = reader.readLine())!=null)
		{
			sb.append(tmp);
		}
		
		JSONObject jobject = new JSONObject(sb.toString());
		JSONObject obj = jobject.getJSONObject("objects");
		
		Set<String> keys = obj.keySet();
		
		for(String key : keys)
		{
			
		}
		
	}
}

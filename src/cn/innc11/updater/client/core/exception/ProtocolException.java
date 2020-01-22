package cn.innc11.updater.client.core.exception;

import java.io.IOException;

public class ProtocolException extends IOException
{
	private static final long serialVersionUID = 1L;

	public ProtocolException()
	{
		super("协议错误");
	}
}

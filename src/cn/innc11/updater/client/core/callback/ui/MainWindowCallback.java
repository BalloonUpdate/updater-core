package cn.innc11.updater.client.core.callback.ui;

public interface MainWindowCallback 
{
	public void changeStateBarText(String str);
	public void changeWindowTitleText(String str);
	public void destoryWindow();
	
	public void setProgressIndeterminate(boolean newValue);
	public void changeProgressValue(int hanrate);
	
	public void appendElement(String str);
	public void removedElement(String str);
	public void clearElements();
}

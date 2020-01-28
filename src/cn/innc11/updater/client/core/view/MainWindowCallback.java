package cn.innc11.updater.client.core.view;

public interface MainWindowCallback 
{
	void changeStateBarText(String str);
	void changeWindowTitleText(String str);
	String getWindowTitleText();
	void destoryWindow();
	
	void setProgressIndeterminate(boolean newValue);
	void changeProgressValue(int hanrate);
	
	void appendElement(String str);
	void removedElement(String str);
	void clearElements();
}

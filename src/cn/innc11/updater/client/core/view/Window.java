package cn.innc11.updater.client.core.view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.IOException;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import cn.innc11.updater.client.core.callback.ui.MainWindowCallback;

public class Window implements MainWindowCallback
{
	private JFrame window;
	private Container rootPanel;
		private JPanel currentProgress;
			private JLabel descriptionLabel;
			private JProgressBar progressBar;
		private JScrollPane downloadQueuePanel;
			private JList<String> downloadQueue;
		private JLabel stateBar;

	private DefaultListModel<String> listModel = new DefaultListModel<>();
	
	public static void main(String[] ag) throws IOException
	{
		Window w = new Window();
	}
	
	public Window()
	{
		window = new JFrame();
		rootPanel = window.getContentPane();
		currentProgress = new JPanel();
		descriptionLabel = new JLabel("进度");
		progressBar = new JProgressBar();
		downloadQueue = new JList<>();
		downloadQueuePanel = new JScrollPane(downloadQueue);
	
		stateBar = new JLabel();
	
		window.setTitle("OS: " + System.getProperty("os.name"));
		window.setUndecorated(false);
		window.setAlwaysOnTop(false);
		window.setSize(550, 400);
		window.setLocationRelativeTo(null);
		window.setDefaultCloseOperation(3);
	
		rootPanel.setLayout(new BorderLayout());
		rootPanel.add(downloadQueuePanel, BorderLayout.CENTER);
		rootPanel.add(stateBar, BorderLayout.SOUTH);
		rootPanel.add(currentProgress, BorderLayout.NORTH);
		currentProgress.setLayout(new BorderLayout(0, 0));
		currentProgress.add(descriptionLabel, BorderLayout.WEST);
		currentProgress.add(progressBar);
		progressBar.setStringPainted(true);//显示进度字符串
		progressBar.setMaximum(1000);//设置进度条最大值
		
		window.setVisible(true);
	}
	

	// UI 的 API
	public void setStateBarText(String str)
	{
		stateBar.setText(str);
	}
	
	public void setWindowTitleText(String str)
	{
		window.setTitle(str);
	}
	
	public void setProgressIndeterminate(boolean newValue)
	{
		progressBar.setIndeterminate(newValue);
	}
	
	public void setProgressValue(int hanrate)
	{
		progressBar.setValue(hanrate);
	}
	
	public void destory()
	{
		window.dispose();
	}
	
	
	// 数据源 API
	public void appendElementToListModel(String str)
	{
		listModel.addElement(str);
		refreshListModel();
	}

	public void removedElementFromListModel(String str)
	{
		listModel.removeElement(str);
		refreshListModel();
	}
	
	public void clearElements()
	{
		listModel.clear();
		refreshListModel();
	}
	
	private void refreshListModel()
	{
		downloadQueue.setModel(listModel);
	}
	
	

	@Override
	public void changeStateBarText(String str) 
	{
		setStateBarText(str);
	}

	@Override
	public void changeWindowTitleText(String str) 
	{
		setWindowTitleText(str);
	}

	@Override
	public void destoryWindow() 
	{
		destory();
	}

	@Override
	public void changeProgressValue(int hanrate) 
	{
		setProgressValue(hanrate);
	}

	@Override
	public void appendElement(String str) 
	{
		appendElementToListModel(str);
	}

	@Override
	public void removedElement(String str) 
	{
		removedElementFromListModel(str);
	}
	
}

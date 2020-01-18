package main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.filechooser.FileSystemView;


import com.teamdev.jxbrowser.chromium.Browser;

import javafx.scene.web.WebEngine;
public class BrowserService {

	static Browser browser;
	
	
	public void reload() {
		browser.reloadIgnoringCache();
	}

	public static void execJS(String code) {
		if(browser!=null)
			browser.executeJavaScript(code);
	}
	
	
	public void setWebEngine(Browser browser) {
		this.browser=browser;
	}
	
}

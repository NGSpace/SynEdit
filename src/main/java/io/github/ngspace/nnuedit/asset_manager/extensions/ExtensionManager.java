package io.github.ngspace.nnuedit.asset_manager.extensions;

import static java.lang.System.out;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.utils.settings.Settings;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;

public class ExtensionManager { private ExtensionManager() {}
	public static final List<ExtensionValues> Extensions = new ArrayList<ExtensionValues>();
	
	
	
	public static boolean loadExtensions(boolean exit) throws ReflectiveOperationException {
		File Folder = new File(FileIO.getConfigFolderPath() + "/Extensions");
		if (!Folder.exists()) Folder.mkdirs();
		File[] ExtensionFolder = Folder.listFiles();
		System.out.println("Loading following Extensions from "+ Folder.getAbsolutePath() +":");
		
		if (ExtensionFolder.length>0) {
			File ext = null;
			try {
				for (int i = 0;i<ExtensionFolder.length;i++) {
					ext = ExtensionFolder[i];
					String jarPath = ext.getAbsolutePath().replace(" ", "%20").replace("\\", "/");
				    loadExtension(new URI("jar:file:" + jarPath + "!/").toURL());
				}
			} catch (Exception e) {
				e.printStackTrace();
				String n = ext.getName();
				UserMessager.showErrorDialogTB("system.exterr.title", "system.exterr", n, e.getLocalizedMessage());
				if (exit) Main.crash(1); else return false;
			}
		}
	    
		/* There is someone debugging an extension that needs to be loaded differently */
		if (Main.class.getClassLoader().getResourceAsStream("Assets/Extension.properties")!=null) {
			try {
				ExtensionManager.loadExtension(Main.class.getClassLoader(), Main.class.getClassLoader(),
					new Settings(Main.class.getClassLoader().getResourceAsStream("Assets/Extension.properties")));
			} catch (Exception e) {
				e.printStackTrace();
				UserMessager.showErrorDialogTB("system.exterr.title","system.exterr",
						"Local Extension",e.getLocalizedMessage());
				if (exit) Main.crash(1); else return false;
			}
		}
		System.out.println("Files loaded: " + Arrays.toString(Folder.list()));
		ExtensionManager.startExtensions();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownExtensions()));
		return true;
	}
	
	
	
	public static void loadExtension(URL url) throws ReflectiveOperationException {
		/* Load extension isolated first to prevent Assets/Extension from being overriden when debuging */
		URLClassLoader cl = new URLClassLoader(new URL[] {url}, null);
	    Settings extSettings = new Settings(cl.getResourceAsStream("Assets/Extension.properties"));
	    
	    loadExtension(new URLClassLoader(new URL[] {url}),cl, extSettings);
	}

	public static void loadExtension(ClassLoader cl, ClassLoader Isolated, Settings extSettings)
			throws ReflectiveOperationException {
		Extension ext = (Extension)cl.loadClass(extSettings.get("main_class"))
				.getDeclaredConstructors()[0].newInstance();
		out.println("     - " + getExtName(extSettings));
		
		Extensions.add(new ExtensionValues(ext,Isolated, extSettings.getMap()));
	}
	
	
	
	public static String getExtName(Settings extSettings) {
		return extSettings.get("name") + " : " + extSettings.get("version") + " : " + extSettings.get("main_class");
	}
	
	public static void startExtensions() {for (ExtensionValues e : Extensions) e.extension.loadExtension();}
	public static void shutdownExtensions() {for (ExtensionValues e : Extensions) e.extension.unloadExtension();}
	
	public static void preStartApp(NNUEdit a) {for (ExtensionValues e : Extensions) e.extension.preLoadApplication(a);}
	public static void startApp(NNUEdit a) {for (ExtensionValues e : Extensions) e.extension.loadApplication(a);}
	public static void shutdownApp(NNUEdit a) {for (ExtensionValues e : Extensions) e.extension.unloadApplication(a);}
	
	
	
	public static List<JComponent> getComponentsOfExtensions(int width) {
		ArrayList<JComponent> lst = new ArrayList<JComponent>(Extensions.size());
		for (ExtensionValues val : Extensions) {
			try {
				JComponent jc = val.extension.getOptionsComponent(width);
				if (jc==null) jc = new BasicExtensionPanel(val,width);
				lst.add(jc);
			} catch (Exception e) {e.printStackTrace();}
		}
		return lst;
	}
}

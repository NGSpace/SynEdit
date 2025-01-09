package io.github.ngspace.nnuedit.utils.registry;

import static io.github.ngspace.nnuedit.menu.EditorTextArea.getSyntaxList;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.swing.JComponent;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.asset_manager.StringTable;
import io.github.ngspace.nnuedit.asset_manager.extensions.ExtensionManager;
import io.github.ngspace.nnuedit.menu.prefrences.options.BooleanOption;
import io.github.ngspace.nnuedit.menu.prefrences.options.ButtonOption;
import io.github.ngspace.nnuedit.menu.prefrences.options.ComboBoxOption;
import io.github.ngspace.nnuedit.menu.prefrences.options.FontOption;
import io.github.ngspace.nnuedit.menu.prefrences.options.Header;
import io.github.ngspace.nnuedit.menu.prefrences.options.HeadlessHeader;
import io.github.ngspace.nnuedit.menu.prefrences.tabs.APreferenceTab;
import io.github.ngspace.nnuedit.menu.prefrences.tabs.ExtensionsTab;
import io.github.ngspace.nnuedit.menu.prefrences.tabs.PreferenceTab;
import io.github.ngspace.nnuedit.runner.HTMLRunner;
import io.github.ngspace.nnuedit.runner.IRunner;
import io.github.ngspace.nnuedit.runner.ProjectRunner;
import io.github.ngspace.nnuedit.runner.PythonRunner;
import io.github.ngspace.nnuedit.runner.ShellRunner;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.window.abstractions.Editor;
import io.github.ngspace.nnuedit.window.windowfactorys.DefaultEditorWindowFactory;
import io.github.ngspace.nnuedit.window.windowfactorys.IWindowFactory;
import io.github.ngspace.nnuedit.window.windowfactorys.ImageWindowFactory;
import io.github.ngspace.nnuedit.window.windowfactorys.PropertiesWindowFactory;

public class Registries {private Registries() {}
	
	
	public static Map<String, IWindowFactory> WindowFactories = new HashMap<String, IWindowFactory>();
	public static Map<String, IRunner> Runners = new HashMap<String,IRunner>();
	public static List<APreferenceTab> PreferencesTabs = new ArrayList<APreferenceTab>();
	
	
	
	public static void registerDefaults() {
		registerDefaultRunners();
		registerDefaultEditors();
		registerDefaultPrefs();
	}
	
	
	
	public static void registerDefaultRunners() {
		Runners.put(NNUEdit.PROJ, new ProjectRunner());
		Runners.put("python", new PythonRunner());
		Runners.put("shell", new ShellRunner());
		Runners.put("html", new HTMLRunner());
	}
	
	
	
	public static void registerDefaultEditors() {
		WindowFactories.put(Editor.DEFAULT, new DefaultEditorWindowFactory());
		ImageWindowFactory imgw = new ImageWindowFactory();
		for (String s : FileIO.imagetypes) WindowFactories.put(s, imgw);
		PropertiesWindowFactory propw = new PropertiesWindowFactory();
		WindowFactories.put("properties", propw);
		WindowFactories.put("prop", propw);
	}
	
	
	
	public static void registerDefaultPrefs() {
		Consumer<JComponent> open = _->FileIO.openInExplorer(new File(FileIO.getConfigFolderPath()));
		
		PreferencesTabs.clear();
		/* Appearance */
		Header appearence = new HeadlessHeader("options.header.appearence");
		appearence.addPrefrence(new FontOption("options.tab.font", "tab.font"));
		appearence.addPrefrence(new BooleanOption("options.antialias", "editor.antialias"));
		appearence.addPrefrence(new BooleanOption("options.preview", "folder.imgpreview"));
		
		// Editor Appearance
		appearence.addPrefrence(new Header("options.header.editor.appearence"));
		appearence.addPrefrence(new FontOption("options.editor.font", "editor.font"));
		appearence.addPrefrence(new ComboBoxOption("options.editor.language", "editor.syntax", getSyntaxList()));
		
		// Properties Appearance
		appearence.addPrefrence(new FontOption("options.prop.font", "editor.prop.font"));

		/* System */
		Header system = new HeadlessHeader("options.header.system");
		system.addPrefrence(new BooleanOption("options.system.startup", "system.checkversion"));
		system.addPrefrence(new ButtonOption("options.system.configfolder", "options.system.configfolder.open", open));
		system.addPrefrence(new ComboBoxOption("options.system.lang", "system.language", StringTable.getLangMap()));
		
		if (!ExtensionManager.Extensions.isEmpty()) PreferencesTabs.add(new ExtensionsTab());
		PreferencesTabs.add(new PreferenceTab(system));
		PreferencesTabs.add(new PreferenceTab(appearence));
	}
}
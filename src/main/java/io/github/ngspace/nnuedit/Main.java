package io.github.ngspace.nnuedit;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;
import static java.lang.System.err;
import static java.lang.System.out;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.IconUIResource;

import com.formdev.flatlaf.FlatDarkLaf;

import io.github.ngspace.nnuedit.asset_manager.AssetManager;
import io.github.ngspace.nnuedit.asset_manager.StringTable;
import io.github.ngspace.nnuedit.asset_manager.extensions.ExtensionManager;
import io.github.ngspace.nnuedit.menu.components.NodeIcon;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.utils.Utils;
import io.github.ngspace.nnuedit.utils.VersionInfo;
import io.github.ngspace.nnuedit.utils.settings.Settings;
import io.github.ngspace.nnuedit.utils.user_io.FancyPrint;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;

public class Main {
	/**
	 * the name of the editor bcz maybe I will want to change it in the future (again).
	 */
	public static final String EFFECTIVE_EDITORNAME = "nnuedit";
	public static String EDITORNAME = StringTable.get("editor.name");
	
	public static Instant start;
	
	public static String shouldEvenTry = "maybe";
	public static Settings settings;
	public static Settings theme;
	public static VersionInfo VersionInfo;
	public static Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	
	/**
     * the entry point of this weird ass code editor
     * @param args which file to open at startup (leave empty for no selected file)
	 * @throws IOException 
     */
	public static void main(String[] args) {
		try {
			start = Instant.now();
			VersionInfo = new VersionInfo();
			
			System.setProperty("sun.java2d.uiScale", "1.0");

			shouldEvenTry = "worth a shot!";

			FancyPrint.initOutput(new File(FileIO.getConfigFolderPath() + "log"), true, true);
	        out.println("Version of NNUEdit : " + VersionInfo.version);
	        
			if (GraphicsEnvironment.isHeadless()) {
				out.println("BRO RUNNING THIS ON A HEADLESS JVM XD");
				shouldEvenTry = "no"; /* Ik I could've just System.exit() I just wanted to make this joke xD */
			}
			
			AssetManager.addDefaultIcons();
			
		    try {
		    	getLocalGraphicsEnvironment().registerFont(Font.createFont(Font.TRUETYPE_FONT, 
		    		Utils.getAsset("Fonts/Consolas.ttf"))); //Will certainly fail
		    } catch(IOException | FontFormatException e) {e.printStackTrace();}//Cry even tho you know it was gonna fail
			
		    ImageIO.setUseCache(false);
		    
	        try {
	        	out.println("Attempting to load flatlaf.");
	        	FlatDarkLaf.setup();
	    		UIManager.put("ScrollBar.buttonArrowColor", new Color(0,0,0,0));
	    		UIManager.put("ScrollBar.thumb", new Color(140,140,140,255));
	    		UIManager.put("ScrollBar.background", new Color(20,20,20,255));
	    		UIManager.put("ScrollBar.showButtons", false);
	    		UIManager.put("ScrollBar.width", 14);
	    		UIManager.put("ScrollBar.thumbArc", 22);
	    		UIManager.put("ScrollBar.trackArc", 22);
	    		final int i = 2;
	    		UIManager.put("ScrollBar.thumbInsets", new Insets(i,i,i,i));
	    		UIManager.put("ScrollBar.trackInsets", new Insets(i,i,i,i));
	    		
	            UIManager.put("Tree.showDefaultIcons ", true);
	        	UIManager.put("Tree.collapsedIcon", new IconUIResource(new NodeIcon(false)));
	        	UIManager.put("Tree.expandedIcon",  new IconUIResource(new NodeIcon(true)));
	        	UIManager.put("OptionPane.messageFont", new Font("Arial", Font.BOLD, 18));
	        	UIManager.put("OptionPane.buttonFont", new Font("Arial", Font.BOLD, 16));
	        } catch (Exception e){
				try {
		        	out.println("Unable to load flatlaf, loading system look and feel.");
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception ex) {
		        	out.println("Unable to load system look and feel, using default.");
					ex.printStackTrace();
				}
				e.printStackTrace();
	        }
	        
			out.println("Milliseconds took to load ui and assets: "+Duration.between(start, Instant.now()).toMillis());
		
		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String sStackTrace = sw.toString();
			
			JOptionPane.showMessageDialog(null, sStackTrace, "Fatal error occured!", 0);
			crash(1);
		}
		
		
		if (shouldEvenTry.equals("no")) {giveUp();}
		
		Thread.setDefaultUncaughtExceptionHandler((_,e)->e.printStackTrace());
    	
		loadSettings();
		
		/* Load Extensions */
		try {
			ExtensionManager.loadExtensions(true);
		} catch (Exception e) {
			e.printStackTrace();
			UserMessager.showErrorDialogTB("err.extensionloading.title", "err.extensionloading", e.getMessage());
			crash(1);
		}
		
		final StringBuilder starttext = new StringBuilder();
		if (args.length>0) {
			starttext.append(args[0]);
			if (!new File(args[0]).exists()&&!(args[0].isEmpty())) {
				err.println("File or Folder Doesn't exist: " + args[0]);
				starttext.setLength(0);
			}
		}
		
        SwingUtilities.invokeLater(() -> {
			try {
				Thread.setDefaultUncaughtExceptionHandler((_,e)->e.printStackTrace());
				NNUEdit app = new NNUEdit(starttext.toString());
				
				ExtensionManager.startApp(app);
				
				out.println("Total milliseconds took to start: "+Duration.between(start, Instant.now()).toMillis());
		        
		        if (settings.getBoolean("system.checkversion"))
			        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URI(
			        		settings.get("system.updatecheckurl")).toURL().openStream(),StandardCharsets.UTF_8))) {
				        String NEWEST = br.lines().collect(Collectors.joining("\n"));
				        out.println("Newest release of NNUEdit : " + NEWEST);
				        String digitRegex = "[^\\d]";
				        String ne = NEWEST.replaceAll(digitRegex, "");
				        double ve = VersionInfo.versionnumber;
				        if (!"".equals(ne) && Integer.parseInt(ne)> ve)
				        	UserMessager.showWarningDialogTB
				        		("system.newversionpopup.title", "system.newversionpopup");
			        } catch (Exception e) {
						System.out.println("Could not check for new version due to:");
			        	UserMessager.showErrorDialogTB("err.checknewver.title","err.checknewver",e.getMessage());
						e.printStackTrace();
					}
				app.setLocationRelativeTo(null);
				app.setVisible(true);
		    	
		    	Main.settings.addValueChangeListener((key, newVal, _, _)-> {
		    		if ("system.language".equals(key)) {
		    	    	StringTable.loadLang(String.valueOf(newVal));
		    	    	EDITORNAME = StringTable.get("system.name");
		    	    	app.setTitle(EDITORNAME);
		    	    	UserMessager.showWarningDialogTB("system.restart.title", "system.restart", EDITORNAME);
		    		}
		    	});
			//If the error reached Main there is no chance of recovery.
			} catch (Exception e) {e.printStackTrace();crash(1);}
		});
    }
	public static void loadSettings() {
		try {
			File f = new File(FileIO.getConfigFolderPath() + EFFECTIVE_EDITORNAME + ".properties");
	    	settings = new Settings(f, new Settings(Utils.getAsset("NNUEdit.properties")).getMap());
	    	theme = settings;
	    	
	    	
	    	if ("ask".equals(settings.get("system.language"))) 
	    		settings.set("system.language",UserMessager.comboInput("Select language","Select language",
	    				StringTable.getLangMap(),StringTable.getSelectedLang()));
	    	
	    	StringTable.loadLang(StringTable.getSelectedLang());
	    	
	    	EDITORNAME = StringTable.get("system.name");
		} catch (Exception e) {e.printStackTrace();/* There is no point in trying :P. */giveUp();}
	}
	private static void giveUp() {crash(1);}
	
	public static void crash(int code) {System.exit(code);}
}

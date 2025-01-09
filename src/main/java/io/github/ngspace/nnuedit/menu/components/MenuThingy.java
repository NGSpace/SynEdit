package io.github.ngspace.nnuedit.menu.components;

import static io.github.ngspace.nnuedit.NNUEdit.MenuFG;
import static io.github.ngspace.nnuedit.asset_manager.StringTable.get;
import static java.lang.System.out;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.window.AboutWindow;
import io.github.ngspace.nnuedit.window.PreferencesWindow;
import io.github.ngspace.nnuedit.window.TextEditorWindow;
/**
 * the menubar
 */
public class MenuThingy extends JMenuBar {
	
	private static final long serialVersionUID = -6781221982811029019L;
	
	public NNUEdit app;
	
	public MenuThingy(NNUEdit app) {
		super();
		
		this.app = app;
		
		this.setOpaque(true);
		
		/* File */
		
        Menu FILE = new Menu(get("menubar.file"), app);
        FILE.setMnemonic(KeyEvent.VK_F);
        FILE.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,15));
        FILE.setToolTipText(get("menubar.file.tooltip"));
        
        MenuItem SAVE = new MenuItem(get("menubar.save"));
        SAVE.addActionListener(_ -> app.saveAll(false));
        SAVE.setMnemonic(KeyEvent.VK_S);
        SAVE.setToolTipText(get("menubar.save.tooltip"));
        
        MenuItem NEW = new MenuItem(get("menubar.neweditor"));
        NEW.addActionListener(_ -> {
        	TextEditorWindow txtwin = new TextEditorWindow(app, "");
        	app.addWindow(txtwin);
        });
        NEW.setMnemonic(KeyEvent.VK_N);
        NEW.setToolTipText(get("menubar.neweditor.tooltip"));
        
        MenuItem OPEN = new MenuItem(get("menubar.openfile"));
        OPEN.addActionListener(_ -> {

    		out.println("Inputing file");
    		String file = FileIO.openFileDialog(false);
    		out.println("Loading file");
    		if (file==null) return;
        	long start = System.nanoTime();
        	
        	app.openFile(file);
        	
            long time1 = System.nanoTime() - start;

            out.println("Time took to read and load the window : " + (time1));
        });
        OPEN.setMnemonic(KeyEvent.VK_O);
        OPEN.setToolTipText(get("menubar.openfile.tooltip",Main.EDITORNAME));
        
        MenuItem OPENFOLDER = new MenuItem(get("menubar.openfolder"));
        OPENFOLDER.addActionListener(_ -> app.openFolderDialog());
        OPENFOLDER.setMnemonic(KeyEvent.VK_E);
        OPENFOLDER.setToolTipText(get("menubar.openfolder.tooltip",Main.EDITORNAME));
        
        MenuItem CLOSE = new MenuItem(get("menubar.close"));
        CLOSE.addActionListener(_ -> app.closeSelectedWindow());
        CLOSE.setMnemonic(KeyEvent.VK_C);
        CLOSE.setToolTipText(get("menubar.close.tooltip"));
        
        MenuItem EXIT = new MenuItem(get("menubar.exit"));
        EXIT.addActionListener(_ -> app.close());
        EXIT.setMnemonic(KeyEvent.VK_C);
        EXIT.setToolTipText(get("menubar.exit.tooltip",Main.EDITORNAME));
        
        FILE.add(SAVE);
        FILE.add(NEW);
        FILE.add(OPEN);
        FILE.add(OPENFOLDER);
        FILE.addSeparator();
        FILE.add(CLOSE);
        FILE.add(EXIT);
        add(FILE);
        
        
		/* Help */
        Menu HELP = new Menu(get("menubar.help"), app);
        HELP.setMnemonic(KeyEvent.VK_H);
        HELP.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,15));
        HELP.setToolTipText(get("menubar.help.tooltip"));
        MenuItem PREFRENCES = new MenuItem(get("menubar.options"));
        PREFRENCES.addActionListener(_ -> {app.addWindow(new PreferencesWindow(app));app.redraw();});
        PREFRENCES.setMnemonic(KeyEvent.VK_P);
        PREFRENCES.setToolTipText(get("menubar.options.tooltip",Main.EDITORNAME));
        MenuItem CREDITS = new MenuItem(get("menubar.about"));
        CREDITS.addActionListener(_ -> app.addWindow(new AboutWindow(app)));
        CREDITS.setMnemonic(KeyEvent.VK_A);
        CREDITS.setToolTipText(get("menubar.about.tooltip",Main.EDITORNAME));
        
        
        HELP.add(PREFRENCES);
        HELP.add(CREDITS);
        add(HELP);
        
        
		/* RUN */
        Menu RUN = new Menu(get("menubar.run"), app);
        RUN.setMnemonic(KeyEvent.VK_R);
        RUN.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,15));
        RUN.setToolTipText(get("menubar.run.tooltip"));
        MenuItem RUNPROJ = new MenuItem(get("menubar.runproj"));
        RUNPROJ.addActionListener(_ -> app.runApp());
        RUNPROJ.setMnemonic(KeyEvent.VK_R);
        RUNPROJ.setToolTipText(get("menubar.runproj.tooltip"));
        MenuItem RUNFILE = new MenuItem(get("menubar.runfile"));
        RUNFILE.addActionListener(_ -> app.runFile());
        RUNFILE.setMnemonic(KeyEvent.VK_F);
        RUNFILE.setToolTipText(get("menubar.runfile.tooltip"));
        
        
        RUN.add(RUNPROJ);
        RUN.add(RUNFILE);
        add(RUN);
	}
	
	
	
    @Override
	public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(app.MenuBG);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponents(g);
        g2d.setColor(Color.DARK_GRAY);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
    }
    
    
    
    /**
     * if the menu contains a certain component
     * @param comp
     * @return a boolean
     */
	public boolean contains(Component comp) {
		for (Component c : this.getComponents()) if (c==comp) return true;
		return false;
	}
}

class MenuItem extends JMenuItem {
	private static final long serialVersionUID = 9131978348527133180L;
	public MenuItem(String str) {super(str);setOpaque(true);}
}
class Menu extends JMenu {
	private static final long serialVersionUID = -1789396700301437504L;
	public Menu(String str, NNUEdit app) {super(str);setOpaque(true);setBackground(app.MenuBG);setForeground(MenuFG);}
}
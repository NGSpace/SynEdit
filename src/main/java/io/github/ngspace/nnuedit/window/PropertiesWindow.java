package io.github.ngspace.nnuedit.window;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JComponent;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.menu.PropertiesMenu;
import io.github.ngspace.nnuedit.menu.components.NGSScrollPane;
import io.github.ngspace.nnuedit.menu.components.Tab;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.window.abstractions.Editor;
import io.github.ngspace.nnuedit.window.abstractions.EditorWindow;

public class PropertiesWindow implements EditorWindow {

	protected PropertiesMenu menu;
	protected NGSScrollPane sp;
	protected Tab tab;
	protected NNUEdit app;
	
	public PropertiesWindow(NNUEdit app, File file) {
		this.sp = new NGSScrollPane(app);
        this.tab = new Tab(app, this);
		this.menu = new PropertiesMenu(app, this, file);
		sp.setViewportView(menu);
        this.app = app;
        
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(25,0));
        sp.getHorizontalScrollBar().setLocation(sp.getWidth() - 25, 0);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(25,100));
        sp.getVerticalScrollBar().setLocation(sp.getWidth() - 25, 0);
        
        sp.setOpaque(true);
        app.addRedrawListener(_->menu.resize());
	}
	@Override public boolean isSaved() {return menu.isSaved();}
	@Override public boolean save(boolean ask) {return menu.save(ask);}
	@Override public String getTitle() {
		return ("".equals(menu.getFilePath())?"Unknown":FileIO.getFileName(menu.getFilePath()));
	}
	@Override public NGSScrollPane getScrollPane() {return sp;}
	@Override public JComponent getComponent() {return menu;}
	@Override public Tab getTab() {return tab;}
	@Override public NNUEdit getApp() {return app;}
	@Override public boolean closeEvent(Object... Reason) {return save(!isSaved());}
	@Override public Editor getEditor() {return menu;}

}
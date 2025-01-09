package io.github.ngspace.nnuedit.window;

import javax.swing.JComponent;
import javax.swing.ScrollPaneConstants;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.asset_manager.AssetManager;
import io.github.ngspace.nnuedit.asset_manager.StringTable;
import io.github.ngspace.nnuedit.menu.components.NGSScrollPane;
import io.github.ngspace.nnuedit.menu.components.Tab;
import io.github.ngspace.nnuedit.menu.prefrences.PreferencesMenu;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class PreferencesWindow implements Window {

	protected PreferencesMenu menu;
	protected NGSScrollPane sp;
	protected Tab tab;
	protected NNUEdit app;
	
	public PreferencesWindow(NNUEdit app) {
        this.app = app;
		this.sp = new NGSScrollPane(getApp());
		getApp();
		this.menu = new PreferencesMenu(this);
		sp.setViewportView(menu);
        this.tab = new Tab(getApp(), this);
        sp.getHorizontalScrollBar().setEnabled(true);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        tab.setIcon(AssetManager.getIcon("NNUEdit72x72"));
	}
	
	@Override public String getTitle() {return StringTable.get("options.title", Main.EDITORNAME);}
	@Override public NGSScrollPane getScrollPane() {return sp;}
	@Override public JComponent getComponent() {return menu;}
	@Override public Tab getTab() {return tab;}
	@Override public NNUEdit getApp() {return app;}
}

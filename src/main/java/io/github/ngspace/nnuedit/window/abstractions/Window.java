package io.github.ngspace.nnuedit.window.abstractions;

import java.awt.Color;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.menu.components.NGSScrollPane;
import io.github.ngspace.nnuedit.menu.components.Tab;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.utils.registry.Registries;
import io.github.ngspace.nnuedit.window.windowfactorys.IWindowFactory;

public interface Window extends Savable, Closeable {
	public static final Color color = new Color(10,10,12);

	public String getTitle();
	public NGSScrollPane getScrollPane();
	public JComponent getComponent();
	public Tab getTab();
	public NNUEdit getApp();
	
	public default void lostFocus(Window newWindow) {}
	public default void gainedFocus(Window prevWindow) {}
	public default boolean isSaved() {return true;}
	public default boolean save(boolean ask) {return true;}
	public default void close() {}
	public default boolean closeEvent(Object... Reason) {return true;}
	
	public default boolean isEditor() {return false;}
	public default Editor getEditor() {return null;}
	public default boolean isOpen(String path) {if (isEditor()) return getEditor().isOpen(path);return false;}
	public static Window createWindowFromFile(NNUEdit app, File file) throws IOException {
		IWindowFactory w = Registries.WindowFactories.get(FileIO.getFileExt(file));
		if (w==null) w = Registries.WindowFactories.get(Editor.DEFAULT);
		return w.createWindowFromFile(app, file);
	}
}
package io.github.ngspace.nnuedit.window;

import java.io.File;
import java.io.IOException;

import javax.swing.JComponent;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.menu.EditorTextArea;
import io.github.ngspace.nnuedit.menu.components.NGSScrollPane;
import io.github.ngspace.nnuedit.menu.components.Tab;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.window.abstractions.Editor;
import io.github.ngspace.nnuedit.window.abstractions.EditorWindow;

public class TextEditorWindow implements EditorWindow {

	protected EditorTextArea textArea;
	protected NGSScrollPane sp;
	protected Tab tab;
	protected NNUEdit app;
	
	public TextEditorWindow(NNUEdit app, File f) throws IOException {
        this(app, f!=null? FileIO.read(f) : "");
        textArea.setFilePath(f!=null? f.getAbsolutePath() : "");
	}

	public TextEditorWindow(NNUEdit app, String Text) {
        
		this.app = app;
        sp = new NGSScrollPane(app);
		textArea = new EditorTextArea(app,this);
		sp.setViewportView(textArea);
        
        tab = new Tab(app, this);
        
        textArea.setOpaque(true);
        textArea.setText(Text);
        textArea.setCaretPosition(0);
        textArea.setSaved(true);
        Main.settings.addRefreshListener(_->{textArea.refresh();tab.setText(getTitle());});
        app.addRedrawListener(_->textArea.refresh());
	}
	@Override public boolean isSaved() {return textArea.isSaved();}
	@Override public NGSScrollPane getScrollPane() {return sp;}
	@Override public JComponent getComponent() {return textArea;}
	@Override public Tab getTab() {return tab;}
	@Override public boolean save(boolean ask) {return textArea.save(ask);}
	@Override public NNUEdit getApp() {return app;}
	@Override public Editor getEditor() {return textArea;}
	@Override public boolean closeEvent(Object... Reason) {
		if (Reason.length>0&&Reason[0]!=null) return save((boolean) Reason[0]);
		else return save("".equals(textArea.getFilePath()));
	}
	@Override public String getTitle() {
		return ("".equals(textArea.getFilePath()) ? "Unknown" : new File(textArea.getFilePath()).getName());
	}
}

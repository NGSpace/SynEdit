package io.github.ngspace.nnuedit.runner;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;
import io.github.ngspace.nnuedit.window.abstractions.Editor;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class HTMLRunner implements IRunner {
	
	String[] FileExtentions = {"html"};
	String[] StarterFiles = {"index.html","index.htm","index.htmx"};

	public HTMLRunner() {/**/}

	@Override
	public boolean canRun(NNUEdit app) {
		Window selwin = app.getSelectedWindow();
		return (selwin!=null&&(selwin.getComponent() instanceof Editor editor
				&&validFileExt(FileExtentions,editor.getFilePath())))||containsFiles(StarterFiles,app)!=null;
	}

	@Override
	public void run(NNUEdit app) {
		try {
			Window w = app.getSelectedWindow();
			
			Editor tx = w != null ? ((Editor)w.getComponent()) : null;
			String filetoopen = tx != null ? tx.getFilePath() : null;
			if (!validFileExt(FileExtentions, filetoopen))
				filetoopen =
					app.Folder.getFolderPath() + File.separatorChar + containsFiles(StarterFiles,app);
			runFile(new File(filetoopen), app);
		} catch (Exception e) {
			e.printStackTrace();
			UserMessager.showErrorDialogTB("runner.html.failedopenbrowser.title", "runner.html.failedopenbrowser",
					e.getMessage());
		}
	}

	@Override public void runFile(File f, NNUEdit app) throws IOException {Desktop.getDesktop().browse(f.toURI());}
	@Override public boolean canRunFile(File f, NNUEdit app) {return validFileExt(FileExtentions,(f.getPath()));}
}

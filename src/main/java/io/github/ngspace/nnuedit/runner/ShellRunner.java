package io.github.ngspace.nnuedit.runner;

import java.io.File;
import java.io.IOException;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.menu.EditorTextArea;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class ShellRunner implements IRunner {
	
	String[] FileExtentions = {"bat", "com", "cmd", "sh", "profile", "bash", "zsh"};
	String[] FileNames = {"run", "start", "debug"};
	String[] StarterFiless = new String[FileNames.length*FileExtentions.length];
	
	public String[] getStarterFiles() {return StarterFiless;}
	
	public ShellRunner() {
		int i = 0;
		for (String fex : FileExtentions)
			for (String fn : FileNames)
				getStarterFiles()[i++] = fn + '.' + fex;
	}

	@Override
	public boolean canRun(NNUEdit app) {
		Window selwin = app.getSelectedWindow();
		return (selwin!=null&&(selwin.getComponent() instanceof EditorTextArea tx
				&&validFileExt(FileExtentions,tx.getFilePath())))||containsFiles(getStarterFiles(),app)!=null;
	}

	@Override
	public void run(NNUEdit app) {
		try {
			Window w = app.getSelectedWindow();
			
			EditorTextArea tx = w != null ? ((EditorTextArea)w.getComponent()) : null;
			String filetoopen = tx != null ? tx.getFilePath() : null;
			if (!validFileExt(FileExtentions, filetoopen)) filetoopen =
				app.Folder.getFolderPath() + File.separatorChar + containsFiles(getStarterFiles(),app);
			String command = filetoopen;
			command = slashify(command);
			runFile(new File(command), app);
		} catch (Exception e) {
			e.printStackTrace();
			UserMessager.showErrorDialogTB("runner.shell.err.title","runner.shell.err",e.getLocalizedMessage());
		}
	}

	@Override
	public void runFile(File f, NNUEdit app) throws IOException {
		/* Can't use Main.SYSTEM because universal might be used on a windows device */
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start cmd /c \"" + f.getPath() + shouldPause() + "\"");
			pb.directory(new File(app.Folder.getFolderPath()));
			pb.start();
		} else {
			ProcessBuilder pb = new ProcessBuilder("sh",f.getPath());
			pb.directory(new File(app.Folder.getFolderPath()));
			pb.start();
		}
	}
	
	private String shouldPause() {return (Main.settings.getBoolean("pauseaftershellrun") ? " & pause" : "");}

	@Override public boolean canRunFile(File f, NNUEdit app) {return validFileExt(FileExtentions,(f.getPath()));}
}

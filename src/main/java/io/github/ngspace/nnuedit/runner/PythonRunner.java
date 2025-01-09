package io.github.ngspace.nnuedit.runner;

import java.io.File;
import java.io.IOException;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;
import io.github.ngspace.nnuedit.window.abstractions.Editor;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class PythonRunner implements IRunner {

	String[] FileExtentions = {"py","pyi","pyc","pyd","pyo","pyw","pyz"};
	String[] FileNames = {"main", "start", "index", "run", "debug"};
	String[] StarterFiless = new String[FileNames.length*FileExtentions.length];
	
	public String[] getStarterFiles() {return StarterFiless;}

	public PythonRunner() {
		int i = 0;
		for (String fex : FileExtentions) {
			for (String fn : FileNames) {
				getStarterFiles()[i] = fn + '.' + fex;
				i++;
			}
		}
	}

	@Override
	public boolean canRun(NNUEdit app) {
		Window selwin = app.getSelectedWindow();
		return (selwin!=null&&(selwin.getComponent() instanceof Editor tx
				&&validFileExt(FileExtentions,tx.getFilePath())))||containsFiles(getStarterFiles(),app)!=null;
	}

	@Override
	public void run(NNUEdit app) {
		try {
			String file = app.Folder.getFolderPath() +File.separatorChar+ 
					containsFiles(getStarterFiles(),app);
			file = slashify(file);
			runFile(new File(file), app);
		} catch (Exception e) {
			e.printStackTrace();
			UserMessager.showErrorDialogTB("runner.shell.err.title","runner.shell.err",e.getLocalizedMessage());
		}
	}
	
	@Override
	public void runFile(File f, NNUEdit app) throws IOException {
		/* Can't use Main.SYSTEM because universal might be used on a windows device */
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			ProcessBuilder pb = new ProcessBuilder
					("cmd","/c", "start cmd /c \"py "+f.getAbsolutePath()+" & pause\"");
			pb.directory(new File(app.Folder.getFolderPath()));
			pb.start();
		} else {
			ProcessBuilder pb = new ProcessBuilder("py","\""+f.getPath() + "\"");
			pb.directory(new File(app.Folder.getFolderPath()));
			pb.start();
		}
	}

	@Override
	public boolean canRunFile(File f, NNUEdit app) {
		return validFileExt(FileExtentions,(f.getPath()));
	}
}

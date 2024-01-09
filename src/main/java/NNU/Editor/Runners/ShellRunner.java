package NNU.Editor.Runners;

import java.io.File;

import NNU.Editor.App;
import NNU.Editor.EditorTextArea;
import NNU.Editor.Utils.UserMessager;
import NNU.Editor.Windows.Interfaces.Window;

public class ShellRunner implements IRunner {
	
	String[] FileExtentions = {"bat", "com", "cmd", "sh", "profile", "bash", "zsh"};
	String[] FileNames = {"run", "start", "debug"};
	String[] StarterFiless = new String[FileNames.length*FileExtentions.length];
	
	public String[] getStarterFiles() {return StarterFiless;}
	
	public ShellRunner() {
		int i = 0;
		for (String fex : FileExtentions) {
			for (String fn : FileNames) {
				getStarterFiles()[i] = fn + '.' + fex;
				i++;
			}
		}
	}

	@Override
	public boolean canRun(App app) {
		Window selwin = app.getSelectedWindow();
		return (selwin!=null&&(selwin.getComponent() instanceof EditorTextArea
				&&ValidFileExt(FileExtentions,((EditorTextArea)selwin.getComponent()).getFilePath())))
				||containsFiles(getStarterFiles(),app)!=null;
	}

	@Override
	public void Run(App app) {
		try {
			Window w = app.getSelectedWindow();
			
			EditorTextArea tx = w != null ? ((EditorTextArea)w.getComponent()) : null;
			String filetoopen = tx != null ? tx.getFilePath() : null;
			if (filetoopen==null) filetoopen =
				app.Folder.getFolderPath() + File.separatorChar + containsFiles(getStarterFiles(),app);
			String command = filetoopen;
			command = slashify(command);
			RunFile(new File(command), app);
		} catch (Exception e) {
			e.printStackTrace();
			UserMessager.showErrorDialogTB("runner.shell.err.title","runner.shell.err",e.getLocalizedMessage());
		}
	}

	@Override
	public void RunFile(File f, App app) throws Exception {
		/* Can't use Main.SYSTEM because universal might be used on a windows device */
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			ProcessBuilder pb = new ProcessBuilder("cmd","/c", "start cmd /c \""+ f.getPath()+
					   (App.stng.getBoolean("pauseaftershellrun") ? " & pause" : "") + "\"");
			pb.directory(new File(app.Folder.getFolderPath()));
			pb.start();
		} else {
			ProcessBuilder pb = new ProcessBuilder("sh",f.getPath());
			pb.directory(new File(app.Folder.getFolderPath()));
			pb.start();
		}
	}
	
	@Override
	public boolean canRunFile(File f, App app) {
		return ValidFileExt(FileExtentions,(f.getPath()));
	}
}
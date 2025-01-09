package io.github.ngspace.nnuedit.folder_management;

import static io.github.ngspace.nnuedit.asset_manager.StringTable.get;
import static java.lang.System.out;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;
import io.github.ngspace.nnuedit.window.abstractions.Editor;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class FolderPopup extends JPopupMenu {
	
	private static final long serialVersionUID = 5845511693212284567L;
	FolderPanel fp;
	NNUEdit app;
	
	public FolderPopup(FolderPanel fp) {
		this.fp = fp;
		this.app = fp.app;
	}
	
	@Override
	public void show(Component invoker, int x, int y) {
		int[] selectedrows = fp.tree.getSelectionRows();
		TreePath[] selectedpaths = fp.tree.getSelectionPaths();
		/**
		 * Do not trust this variable please! it is most likely null!
		 */
		File f = selectedrows.length==1?new File(fp.tree.nodeFromRow(selectedrows[0]).getUserObject().toString()):null;
		this.removeAll();
		
		JMenuItem DELETE = new JMenuItem(get("folder.ctx.delete"));
		DELETE.addActionListener(_ -> {
			try {
				int res = UserMessager.confirmTB("confirm.delete", "confirm.delete");
				if (res==UserMessager.YES) {
					for (int p : selectedrows) {
						out.println("Deleting " + fp.tree.nodeFromRow(p).getUserObject().toString());
						
						FileIO.recursiveDelete(Paths.get(fp.tree.nodeFromRow(p).getUserObject().toString()));
					}
					fp.tree.setSelectionRow(-1);
				}
				fp.refresh(true);
			} catch (Exception e1) {e1.printStackTrace();}
        });
		
		JMenuItem RENAME = new JMenuItem(get("folder.ctx.rename"));
		RENAME.addActionListener(_ -> {
			Object res = UserMessager.inputTB(f.getName(), "input.newfile");
			if (res!=null) {
				
				out.println("Deleting " + f.toPath().toAbsolutePath());
				
				try {
					rename(f,res.toString());
				} catch (Exception e1) {
					e1.printStackTrace();
					UserMessager.showErrorDialogTB("err.renamefile.title", "err.renamefile", e1.getMessage());
				}
			}
			fp.refresh(true);
        });
		
		JMenuItem REFRESH = new JMenuItem(get("folder.ctx.refresh"));
		REFRESH.addActionListener(_ -> fp.refresh(true));
		
		JMenuItem OPENINEXPLORER = new JMenuItem(get("folder.ctx.openinexp"));
		OPENINEXPLORER.addActionListener(_ -> FileIO.openInExplorer(f.getParentFile()));
		
		if (f!=null&&f.isDirectory()) {
			JMenuItem NewFile = new JMenuItem(get("folder.ctx.newfile"));
			NewFile.addActionListener(_ -> {
				try {
					Object path = UserMessager.inputTB("", "input.newfile");
					if (path!=null) {
						out.println("Creating file " + f.getAbsolutePath() + File.separatorChar + path);
						File fi = new File(f.getAbsolutePath() + File.separatorChar + path);
						boolean res = fi.createNewFile();
						out.println(res);
						if (!res) {
							UserMessager.showErrorDialogTB("folder.err.foldercreate.title",
									f.exists() ? "err.fileexists" : "folder.err.foldercreate");
						}
						fp.tree.expandPath(selectedpaths[0]);
					} else {
						out.println("Canceled File Creation");
					}
					fp.refresh(true);
				} catch (IOException e1) {e1.printStackTrace();}
	        });
			JMenuItem NewFolder = new JMenuItem(get("folder.ctx.newfolder"));
			NewFolder.addActionListener(_ -> {
				Object path =UserMessager.inputTB("", "input.newfolder");
				if (path!=null) {
					out.println("Creating Folder " + f.getAbsolutePath() + File.separatorChar + path);
					File fi = new File(f.getAbsolutePath() + File.separatorChar + path);
					boolean res = fi.mkdirs();
					out.println(res);
					if (!res) {
						UserMessager.showErrorDialogTB("folder.err.foldercreate.title",
								fi.exists() ? "err.fileexists" :
									"folder.err.foldercreate");
					}
					fp.tree.expandPath(selectedpaths[0]);
				} else {
					out.println("Canceled Folder Creation");
				}
				fp.refresh(true);
	        });
			add(NewFile);
			add(NewFolder);
			this.addSeparator();
		}
		if(f!=null) {
			add(RENAME);
			add(DELETE);
	
			this.addSeparator();
			add(REFRESH);
			this.addSeparator();
			add(OPENINEXPLORER);
			if (!f.isDirectory()) {
				JMenuItem OpenInEditor = new JMenuItem(get("folder.ctx.openwitheditor"));
				OpenInEditor.addActionListener(_ -> app.openDefaultTextEditor(f));
				add(OpenInEditor);
			}
		} else {
			add(DELETE);
			add(REFRESH);
		}
		super.show(invoker,x,y);
    }
	public boolean rename(File f, String newName) throws IOException {
		String name = f.getAbsolutePath();
		String newFile = f.getParent() + File.separatorChar + newName;
		boolean res = f.renameTo(new File(f.getParent() + File.separatorChar + newName));
		if (!res) throw new IOException("Unable to rename Object");
		else {
			for (Window i : app.Windows) {
				if (i.getComponent() instanceof Editor editor && Objects.equals(editor.getFilePath(), name)) {
					((Editor)i.getComponent()).setFilePath(newFile);
					i.getTab().repaint();
				}
			}
		}
		return res;
	}
}

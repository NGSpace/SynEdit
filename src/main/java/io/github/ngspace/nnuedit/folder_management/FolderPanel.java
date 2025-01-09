package io.github.ngspace.nnuedit.folder_management;

import static io.github.ngspace.nnuedit.asset_manager.StringTable.get;
import static java.lang.System.out;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.asset_manager.AssetManager;
import io.github.ngspace.nnuedit.menu.components.IconButton;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class FolderPanel extends JPanel {
	
	private static final long serialVersionUID = -936840551668141819L;
	private String FolderPath = "";
	public final JScrollPane FilesPanel;
	public final NNUEdit app;
	public final DirectoryTree tree;
	public static final int ROW_HEIGHT = 25;
	public static final int PADDING = 3;
	public static final int DRAGBAR_WIDTH = 10;
	public static final int MIN_WIDTH = 100;
	int DragWidth = 0;
	long tempwidth = 0;
	boolean isdragging = false;
	JButton CloseFolder;
	JButton OpenFolder;
	JButton Refresh;
	JButton DragBar;
	
	
	
	public void setFolderPath(String path) {
		FolderPath = path==null ? "" : path;
		try {
			if (Main.settings.getBoolean("folder.openlast")) Main.settings.set("folder.last", path);
			else /* Same */ Main.settings.set("folder.last", "I want to be a Real Folder!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		refresh(true);
	}

	public String getFolderPath() {return FolderPath;}
	public int menuBarSize() {return 25 * app.getScale();}
	@Override public int getWidth() {
		return Math.clamp(DragWidth,MIN_WIDTH,app.getWidth()/2);
	}

	public FolderPanel(NNUEdit app) {
        this.app = app;
        
		this.setDoubleBuffered(true);
		
		tree = new DirectoryTree(this);

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if ("".equals(getFolderPath())) setFolderPath(FileIO.openFolderDialog());
			}
		});
		
		FilesPanel = new JScrollPane(tree, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		FilesPanel.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if ("".equals(getFolderPath())) setFolderPath(FileIO.openFolderDialog());
			}
		});
		
		FilesPanel.setBounds(0, 56, 646, 723);
		FilesPanel.setDoubleBuffered(true);
		FilesPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
		setLayout(null);
		
		setBackground(app.MenuBG.darker());
		FilesPanel.setBackground(getBackground());
		tree.setBackground(getBackground());

		initDrag(); //SLAYYYYYYYYYYYYYYYYYYYYYYY
		initButtons();
		add(FilesPanel);
	}
	public void initButtons() {
		JButton FileCreate = new IconButton(AssetManager.getIcon("newfile"));
		FileCreate.setToolTipText(get("folder.bar.newfile"));
		FileCreate.setBounds(0,0,menuBarSize(),menuBarSize());
		FileCreate.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				newFile();
				refresh(true);
			}
		});
		add(FileCreate);
		
		JButton FolderCreate = new IconButton(AssetManager.getIcon("newfolder"));
		FolderCreate.setToolTipText(get("folder.bar.newfolder"));
		FolderCreate.setBounds(menuBarSize(),0,menuBarSize(),menuBarSize());
		FolderCreate.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if ("".equals(getFolderPath())) {
					UserMessager.showErrorDialogTB
						("folder.err.foldercreate.title", "folder.err.foldernotselected");
					return;
				}
				Object path = UserMessager.inputTB("", "input.newfolder");
				if (path!=null) {
					out.println("Creating folder " + getFolderPath() + File.separatorChar + path);
					File f = new File(getFolderPath() + File.separatorChar + path);
					boolean res = f.mkdirs();
					out.println(res);
					if (!res)
						UserMessager.showErrorDialogTB("folder.err.foldercreate.title",
							f.exists() ? "err.fileexists" : "folder.err.foldercreate");
				} else
					out.println("Canceled File Creation");
				refresh(true);
			}
		});
		add(FolderCreate);

		Refresh = new IconButton(AssetManager.getIcon("refresh"));
		Refresh.setToolTipText(get("folder.bar.refreshfolder"));
		add(Refresh);
		
		OpenFolder = new IconButton(AssetManager.getIcon("folder"));
		OpenFolder.setToolTipText(get("folder.bar.openfolder"));
		add(OpenFolder);
		
		CloseFolder = new XButton();
		CloseFolder.setToolTipText(get("folder.bar.closefolder"));
		add(CloseFolder);
		this.setOpaque(true);

		Refresh.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				refresh(true);
			}
		});
		CloseFolder.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				setFolderPath(null);
				refresh(true);
			}
		});
		OpenFolder.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				String temp = FileIO.openFolderDialog();

				setFolderPath(temp==null ? getFolderPath() : temp);
				refresh(true);
			}
		});
	}
	
	@Override public void paint(Graphics g) {
		super.paint(g);
		if (DragBar.isVisible()) {
			Icon ic = DragBar.getIcon();
			for (int i = 0;i<getHeight();i+=ic.getIconHeight()-1)
				ic.paintIcon(DragBar, g, DragBar.getX(), i);
		}
	}
	
	private void initDrag() {
		DragBar = new JButton();
		DragBar.setIcon(AssetManager.getIcon("ui/bar"));
		DragWidth = Main.settings.getInt("folder.panelwidth");
		DragBar.setBounds(DragWidth-10,0,10,10000);
		DragBar.setBorderPainted(false); 
		DragBar.setBackground(Color.black);
		DragBar.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
		DragBar.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				tempwidth = getWidth();
				DragBar.setVisible(false);
	    		app.FolderButton.setEnabled(false);
				repaint();
				isdragging = true;
			}
		});
		long eventMask = AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK;
		Toolkit.getDefaultToolkit().addAWTEventListener(ev-> {
	    	MouseEvent e = (MouseEvent) ev;
	    	if (e.getID()==MouseEvent.MOUSE_DRAGGED&&isdragging) {
	    		DragWidth = (Math.clamp(e.getX() + tempwidth,MIN_WIDTH,app.getWidth()/2));
	    		app.redraw();
	    	}
	    	if (e.getID()==MouseEvent.MOUSE_RELEASED) {
	    		if (isdragging) {
					DragBar.setLocation(getWidth()-DRAGBAR_WIDTH,0);
					DragBar.setVisible(true);
		    		app.FolderButton.setEnabled(true);
					Main.settings.set("folder.panelwidth", DragWidth);
	    		}
	    		isdragging = false;
		    }
		}, eventMask);
		add(DragBar);
		repaint();
	}

	public boolean rename(File f, String newName) throws IOException {
		String name = f.getAbsolutePath();
		String newFile = f.getParent() + File.separatorChar + newName;
		boolean res = f.renameTo(new File(f.getParent() + File.separatorChar + newName));
		if (!res) throw new IOException("Unable to rename Object");
		else
			for (Window i : app.Windows)
				if (i.isEditor()&&name.equals(i.getEditor().getFilePath()))
					i.getEditor().setFilePath(newFile);
		return res;
	}
	
	protected void newFile() {
		try {
			if ("".equals(getFolderPath())) {
				UserMessager.showErrorDialogTB("folder.err.filecreate.title", "folder.err.foldernotselected");
				return;
			}
			Object path = UserMessager.inputTB("", "input.newfile");
			if (path!=null) {
				out.println("Creating file " + getFolderPath() + File.separatorChar + path);
				File f = new File(getFolderPath() + File.separatorChar + path);
				boolean res = f.createNewFile();
				if (!res)
					UserMessager.showErrorDialogTB("folder.err.filecreate.title",
							f.exists()?"err.fileexists":"folder.err.filecreate");
				else
					app.openFile(getFolderPath() + File.separatorChar + path);
			} else {
				out.println("Canceled File Creation");
			}
		} catch (IOException e) {
			UserMessager.showErrorDialogTB("folder.err.filecreate.title", "folder.err.filecreate");
			e.printStackTrace();
		}
	}
	
	public void redrawIcons() {
		Refresh.setBounds(getWidth()-menuBarSize()*3 - DRAGBAR_WIDTH,0,menuBarSize(),menuBarSize());
		OpenFolder.setBounds(getWidth()-menuBarSize()*2 - DRAGBAR_WIDTH,0,menuBarSize(),menuBarSize());
		CloseFolder.setBounds(getWidth()-menuBarSize() - DRAGBAR_WIDTH,0,menuBarSize(),menuBarSize());
	}
	
	public void refresh(boolean clearcache) {
		redrawIcons();
		
		if (clearcache) AssetManager.clearImageCache();
		
		if ("".equals(getFolderPath())) {tree.setModel(null);return;}

        var state = tree.getState();
        TreeNode node = readFileList(getFolderPath());
		tree.setModel(new DefaultTreeModel(node));
		tree.setExpansionState(state);
	}
	
	public static DefaultMutableTreeNode readFileList(String FPath) {
		if (FPath==null||"".equals(FPath)) return null;
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(FPath);
		
		File folder = new File(FPath);
		ArrayList<DefaultMutableTreeNode> files = new ArrayList<DefaultMutableTreeNode>();
		
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				DefaultMutableTreeNode fi = readFileList(f.getAbsolutePath());
				root.add(fi);
			} else {
				DefaultMutableTreeNode fi = new DefaultMutableTreeNode(f.getAbsolutePath());
				fi.setAllowsChildren(false);
				files.add(fi);
			}
		}
		for (DefaultMutableTreeNode tn : files) root.add(tn);
		return root;
	}
	
	public <T> boolean arrcontains(T[] file, T root) {
		for (var v : file) if (root.equals(v)) return true;
		return false;
	}
	
	public boolean contains(String file, boolean root) {
		if (!root) for (File tr : new File(getFolderPath()).listFiles()) if (file.equals(tr.getName())) return true;
		return new File(getFolderPath() + File.separatorChar + file).exists();
	}
}
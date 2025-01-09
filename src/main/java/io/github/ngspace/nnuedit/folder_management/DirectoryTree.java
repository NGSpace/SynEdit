package io.github.ngspace.nnuedit.folder_management;

import static io.github.ngspace.nnuedit.asset_manager.StringTable.get;
import static java.lang.System.out;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;

public class DirectoryTree extends JTree implements KeyListener {
	
	private FolderPanel panel;
	private JPopupMenu popupMenu;
	
	public DirectoryTree(FolderPanel panel) {
		this.panel=panel;
		addKeyListener(this);
		setCellRenderer(new FolderTreeRenderer());
		
		setFont(panel.app.getFont());
		registerKeyboardAction(_->panel.newFile(),KeyStroke.getKeyStroke(KeyEvent.VK_N,InputEvent.CTRL_DOWN_MASK),
	    	JComponent.WHEN_FOCUSED);
        setDragEnabled(true);
        
        setTransferHandler(new FolderTransferHandler()); 
        setDropMode(DropMode.ON_OR_INSERT);
        setToggleClickCount(0);
        
        getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		setBounds(30, 0, 646, 723);
		setFont(new Font(NNUEdit.FONT, Font.BOLD, 20));
		setRootVisible(true);
		Color linecolor = new Color(78, 78, 78, 150);
		setBorder(new EmptyBorder(0, 0, 0, 0) {
			private static final long serialVersionUID = -2491978365735980533L;

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				((Graphics2D)g).setStroke(new BasicStroke(10,1,1));
				g.setColor(linecolor);
				g.drawLine(0, 0, width, 0);
			}
		});
		setRowHeight(FolderPanel.ROW_HEIGHT);
		
		addMouseMotionListener(new MouseMotionAdapter() {
		    @Override
		    public void mouseMoved(MouseEvent e) {
		        TreePath path = getPathForLocation((int) e.getPoint().getX(), (int) e.getPoint().getY());
		        setCursor(path==null?Cursor.getDefaultCursor():Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		    }
		});
		
		initPopupPanel();
	}
	
	private void initPopupPanel() {
		
		setPopupMenu(new FolderPopup(panel));
		
		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if ("".equals(panel.getFolderPath())) {
					panel.setFolderPath(FileIO.openFolderDialog());
					panel.refresh(false);
					return;
				}
			    if (SwingUtilities.isRightMouseButton(e)) {
			        int row = getRowForLocation(e.getX(), e.getY());
			        
				    if(row==-1) setSelectionRow(0);
				    else if (!isRowSelected(row)) setSelectionRow(row);
				    getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
			    }
			}
			@Override public void mouseReleased(MouseEvent e) {
			    int row = getRowForLocation(e.getX(),e.getY());
			    
			    if(row==-1) clearSelection();
			    if (getSelectionCount()<0) return;
			    
				if(e.getClickCount() == 2&&getSelectionCount()>0) {
					File f = new File(nodeFromRow(getSelectionRows()[0]).getUserObject().toString());
	                if (!f.isDirectory()) panel.app.openFile(f.getAbsolutePath());
	                
	                if (isExpanded(getSelectionPaths()[0])) collapsePath(getSelectionPaths()[0]);
	                else expandPath(getSelectionPaths()[0]);
	                
	                repaint();
				}
			}
		});
	}

	private static final long serialVersionUID = -1909873281110302190L;
	@Override public void paint(Graphics gra) {
		Graphics2D g = (Graphics2D) gra;
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		if (getModel()==null) {

    		g.setFont(getFont());
    		g.setColor(NNUEdit.MenuFG);
    		
    		NNUEdit.adjustAntialias(g, true);
    		int i = 0;
    		int bx = 100000;
    		int starty = 0;
    		int strheight = 0;
    		
    		String[] strs = get("folder.clickhere").split("\n");
    		
    		for (String str : strs) {
	    		int strwidth = g.getFontMetrics().stringWidth(str);
	    		int x = getWidth() / 2 - (strwidth / 2);
	    		strheight = (int) g.getFontMetrics().getLineMetrics(str, g).getHeight();
	    		starty = getHeight()/2 - ((strs.length*strheight)/2);
	    		int y = starty + i * strheight;
	    		g.drawString(str, x, y);
	    		i++;
	    		bx = x<bx?x:bx;
    		}
    		starty-=strheight-5;
    		int w = getWidth()-bx*2+10;
    		int h = strheight*strs.length+10;
    		
    		g.setStroke(new BasicStroke(2));
    		g.drawRoundRect(bx-5, starty, w, h, 20, 20);
    		return;
		}
		super.paintComponent(gra);
	}
	@Override public void keyReleased(KeyEvent e) {
		if (e.getKeyCode()==KeyEvent.VK_TAB) setSelectionPath(new TreePath(getModel().getRoot()));
		if (getSelectionCount()<1) return;
		
		String firstselrowval = nodeFromRow(getSelectionRows()[0]).getUserObject().toString();
		
		if (e.getKeyCode()==KeyEvent.VK_ENTER||e.getKeyCode()==KeyEvent.VK_SPACE) {
			if (!new File(firstselrowval).isDirectory()) {
				panel.app.openFile(firstselrowval);
				return;
			}
			if (!isExpanded(getSelectionPath())) expandPath(getSelectionPath());
			else collapsePath(getSelectionPath());
		}
		
		if (e.getKeyCode()==KeyEvent.VK_DELETE) {
			int res = UserMessager.confirmTB("confirm.delete", "confirm.delete");
			if (res==UserMessager.YES) {
				try {
					for (int p : getSelectionRows()) {
						out.println("Deleting " + nodeFromRow(p).getUserObject().toString());
						FileIO.recursiveDelete(Paths.get(nodeFromRow(p).getUserObject().toString()));
					}
					setSelectionRow(-1);
				} catch (IOException e1) {e1.printStackTrace();}
			}
		} else if (e.getKeyCode()==KeyEvent.VK_F2) {
			if (getSelectionCount()>1) return;
			File file = new File(firstselrowval);
			String val = UserMessager.inputTB(file.getName(), "input.newfile").toString();
			if (val!=null) {
				out.println("Renaming " + file.toPath().toAbsolutePath() + " to " + val);
				try {
					panel.rename(file, val);
				} catch (Exception ex) {
					ex.printStackTrace();
					UserMessager.showErrorDialogTB("err.renamefile.title", "err.renamefile", ex.getMessage());
				}
			}
		}
		panel.refresh(false);
	}
	@Override public void keyTyped  (KeyEvent e) {/**/}
	@Override public void keyPressed(KeyEvent e) {/**/}
	
	
	/**
	 * Returns the node located at the given row
	 * @param row - the row
	 * @return the tree node at the given row
	 */
	public DefaultMutableTreeNode nodeFromRow(int row) {
		return (DefaultMutableTreeNode) getPathForRow(row).getLastPathComponent();
	}
	
	public class TreeState {
		public TreePath[] paths;
		public int[] selections;
		public TreeState(TreePath[] paths, int[] selections) {this.paths=paths;this.selections=selections;}
	}
	
	public TreeState getState() {
		TreePath[] sb = new TreePath[getRowCount()];
		int ind = 0;
	    for(int i=0;i<getRowCount();i++)
	        if(isExpanded(i))
	        	sb[ind++] = getPathForRow(i);
	    return new TreeState(sb,getSelectionRows());
	}
	
	public void setExpansionState(TreeState state) {
		for (var expandedpath : state.paths) {
			if (expandedpath==null) break;
		    for(int i = 0 ; i<getRowCount(); i++) 
		        if(expandedpath.toString().equals(getPathForRow(i).toString())) expandRow(i);
		}
		setSelectionRows(state.selections);
	}
	
    public void setPopupMenu(JPopupMenu popup) {this.popupMenu = popup;}
    public JPopupMenu getPopupMenu() {return popupMenu;}
}

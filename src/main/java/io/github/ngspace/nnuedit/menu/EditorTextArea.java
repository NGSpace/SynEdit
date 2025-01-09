package io.github.ngspace.nnuedit.menu;

import static io.github.ngspace.nnuedit.asset_manager.AssetManager.fileext;
import static io.github.ngspace.nnuedit.asset_manager.StringTable.get;
import static io.github.ngspace.nnuedit.utils.user_io.UserMessager.inputTB;
import static io.github.ngspace.nnuedit.utils.user_io.UserMessager.showConfirmAndCancelTB;
import static io.github.ngspace.nnuedit.utils.user_io.UserMessager.showErrorDialogTB;
import static javax.swing.JOptionPane.CANCEL_OPTION;
import static javax.swing.JOptionPane.NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaUI;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.ConfigurableCaret;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.asset_manager.AssetManager;
import io.github.ngspace.nnuedit.menu.components.NGSScrollPane;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.utils.Utils;
import io.github.ngspace.nnuedit.utils.ui.SmartGraphics2D;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;
import io.github.ngspace.nnuedit.window.abstractions.Editor;
import io.github.ngspace.nnuedit.window.abstractions.Savable;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class EditorTextArea extends RSyntaxTextArea implements DocumentListener, Savable, Editor {

	private static final long serialVersionUID = 4557978198642746316L;
	
	private String FilePath = "";
	private static String tx = "text/";
	private static String syn = "editor.syntax";
    private boolean saved = true;
    public final Window window;
	public final NNUEdit app;

	public JMenuItem jpmln = new JMenuItem("1");

    public void setFilePath(String fp) {
        String[] arr = fp.split("[.]");
        String ext = arr[arr.length-1];
		if (arr.length>1&&fileext.get(ext.toLowerCase().trim())!=null)
			setSyntaxEditingStyle(tx + fileext.get(ext).toString().toLowerCase().trim());
		else
			setSyntaxEditingStyle(tx + Main.settings.get(syn).toLowerCase().trim());
    	FilePath = fp;
        window.getTab().setIcon(AssetManager.getIconOfFile(new File(fp)));
    }
    public String getFilePath() {
    	return FilePath;
    }

	public EditorTextArea(NNUEdit app, Window w) {
		
		window = w;
		this.app = app;
        
        setCodeFoldingEnabled(true);
        setAntiAliasingEnabled(false);
        setMarginLineEnabled(false);
        setPaintTabLines(true);
		setIgnoreRepaint(true);
        setCaretColor(Color.LIGHT_GRAY);
        setUI((ComponentUI) new RSyntaxTextAreaUI(this) {
        	@Override protected void paintBackground(Graphics g) {paintEditorAugmentations(g);}
        });
        
		try {
	        InputStream inpstr = Utils.getAsset("Style.xml");
			Theme theme = Theme.load(inpstr);
	        theme.apply(this);
//			snxsc = new SyntaxScheme(true);
//			Style[] source = getSyntaxScheme().getStyles();
//			Style[] styles = new Style[source.length + 3];
//			System.arraycopy(source, 0, styles, 0, source.length);
//			snxsc.setStyles(new Style[50]);
//			snxsc.setStyle(40, new Style(Color.red));
		} catch (IOException e) {e.printStackTrace();}
        this.getDocument().addDocumentListener(this);
        this.setFont(app.getFont());
        this.setBackground(Window.color);
        this.setForeground(Color.LIGHT_GRAY);
        this.setCurrentLineHighlightColor(new Color(44,44,44));
        this.setSelectionColor(new Color(99, 128, 176));
        this.setSelectedTextColor(new Color(255,255,255));
        
        this.setOpaque(false);
        this.setLayout(null);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        
        JMenuItem FIND = new JMenuItem(get("editor.find"));

        FIND.addActionListener(_ -> openFind());
        FIND.setMnemonic(KeyEvent.VK_F);
        
        //this.getPopupMenu().addSeparator();
		JPopupMenu jpm = this.getPopupMenu();
        jpm.add(FIND,9);
        jpm.addSeparator();
        jpm.add(jpmln);
        setBorder(new EmptyBorder(0,NNUEdit.getBuffer(),0,NNUEdit.getBuffer()));
        jpm.addPopupMenuListener(new PopupMenuListener() {
			
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				if (jpm.getComponentCount()==0)
					System.out.println(Arrays.toString(jpm.getComponents()));
				int i = 0;
				JMenuItem un = ((JMenuItem)jpm.getComponent(i));
				un.setText(un.isEnabled()?get("editor.undo.enabled"):get("editor.undo.disabled"));
				i++;
				JMenuItem re = ((JMenuItem)jpm.getComponent(i));
				re.setText(re.isEnabled()?get("editor.redo.enabled"):get("editor.redo.disabled"));
				i+=2;
				((JMenuItem)jpm.getComponent(i)).setText(get("editor.cut"));
				i++;
				((JMenuItem)jpm.getComponent(i)).setText(get("editor.copy"));
				i++;
				((JMenuItem)jpm.getComponent(i)).setText(get("editor.paste"));
				i++;
				((JMenuItem)jpm.getComponent(i)).setText(get("editor.delete"));
				i+=2;
				((JMenuItem)jpm.getComponent(i)).setText(get("editor.selectall"));
				i+=3;
				((JMenu)jpm.getComponent(i)).setText(get("editor.folding"));
				((JMenuItem) (((JMenu) jpm.getComponent(i)).getMenuComponents()[0]))
					.setText(get("editor.folding.togglecurrent"));
				((JMenuItem) (((JMenu) jpm.getComponent(i)).getMenuComponents()[1]))
				.setText(get("editor.folding.collapseallcomments"));
				((JMenuItem) (((JMenu) jpm.getComponent(i)).getMenuComponents()[2]))
				.setText(get("editor.folding.collapseallfolds"));
				((JMenuItem) (((JMenu) jpm.getComponent(i)).getMenuComponents()[3]))
				.setText(get("editor.folding.expandallfolds"));
			}
			@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {/**/}
			@Override public void popupMenuCanceled(PopupMenuEvent e) {/**/}
		});
        
        ((ConfigurableCaret) this.getCaret()).setBlinkRate(0);
        
        JScrollPane sp = window.getScrollPane();
        sp.getHorizontalScrollBar().addAdjustmentListener(_ -> resize());
        sp.getVerticalScrollBar().addAdjustmentListener(_ -> resize());
        

        DropTargetListener myDragDropListener = new DropTargetAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
	            List<File> droppedFiles = null;
				try {
					droppedFiles = (List<File>)
							dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				} catch (UnsupportedFlavorException | IOException e) {e.printStackTrace();return;}
	            for (File file : droppedFiles) {
	                app.openFile(file.getAbsolutePath());
	            }
			}
        };
        
        setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
        
        new DropTarget(this, myDragDropListener);
	}
	
	@Override public JPopupMenu getPopupMenu() {
		int caretpos = getCaretPosition();
		String row = "";
		try {
			int i = getLineOfOffset(caretpos);
			if(i!=-1) row = i + 1 + "";
		} catch (BadLocationException e) {row = "Unknown line";}
		jpmln.setText(get("editor.linenum", row));
		return super.getPopupMenu();
	}
	
	public void resize() {
        JScrollPane sp = window.getScrollPane();
		Point position = sp.getViewport().getViewPosition();
		JScrollBar vsb = sp.getVerticalScrollBar();
    	if (fm!=null) {
        	fm.setLocation((int) (position.getX()+(sp.getWidth()-fm.getWidth())
        			-(vsb.isVisible()?vsb.getWidth():0)),
        			(int) position.getY());
    	}
    	repaint();
	}

	@Override
	public void paintComponent(Graphics gra) {
		NGSScrollPane sp1 = window.getScrollPane();
		//System.out.println(getSyntaxScheme().getStyleCount());
		//setSyntaxScheme(snxsc);
		//System.out.println(getSyntaxScheme().getStyleCount());
		//sp1.repaint();
		Color c = gra.getColor();
		gra.setColor(getBackground());
		gra.fillRect(0, 0, getWidth(), getHeight());
		gra.setColor(c);
		TextUI ui = getUI();
		Graphics2D g = (Graphics2D) gra.create(0, 0, getWidth(), getHeight());
		Rectangle vis = getVisibleRect();
		vis.height +=50;
		SmartGraphics2D.SuccessfulDrawCalls = 0;
		Graphics2D graphics = new SmartGraphics2D(g, vis);
		NNUEdit.adjustAntialias(g,false);
		if (ui != null) {
			ui.update(graphics, this);
		}
		sp1.paintSeperators((Graphics2D)gra);
		sp1.paintLines(graphics);
	}

	public JPanel fm;
	
	public void openFind() {
		if (fm==null) {
	    	fm = new FindMenu(this);
	    	this.add(fm);
	    	resize();
		}
		fm.getComponents()[0].requestFocus();
	}
	
	public void gotoLineMenu() {
		int ans = Integer.parseInt(String.valueOf(inputTB("","editor.goto.title","editor.goto.content")));
		try {
			this.setCaretPosition(this.getLineEndOffset(ans-1)-1);
		} catch (BadLocationException e) {
			showErrorDialogTB("editor.err.invalidline.title", "editor.err.invalidline", ans);
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the current file of the textArea
	 * @param load whether to load the file into the TextArea's text bar or not.
	 * @param save whether to display a save button or open button (this option does not actually save)
	 */
	public void openfile(boolean load, boolean save) {
		String res = "";
		while ("".equals(res)) {
			res = FileIO.openFileDialog(save);
	        if(res != null&&!"/".equals(res.trim()) && !res.trim().isEmpty()) {
	            try {
					File f = new File(res);
					if (!f.exists() && !f.createNewFile()) throw new IOException("Can't create file");
					if ("".equals(res)) {
						showErrorDialogTB("err.readfile.title", "err.readfile.empty");
						continue;
					}
					setFilePath(res);
					if (load) {
						setText(FileIO.read(new File(res)));
				        setCaretPosition(0);
						revalidate();
						app.repaint();
					}
				} catch (Exception e) {
					showErrorDialogTB("err.readfile.title","err.readfile.exception",e.getMessage());
					e.printStackTrace();
				}
	        } else {
	        	return;
	        }
		}
	}
	
	public boolean save(boolean ask) {
		if (isSaved()) return true;
		int result = 0;
		if (ask) result = showConfirmAndCancelTB("confirm.save","confirm.save");
    	if (result==CANCEL_OPTION) return false;
    	if (result==YES_OPTION&&"".equals(this.getFilePath())) this.openfile(false, true);
    	if (result==NO_OPTION) return true;
		if ("".equals(this.getFilePath())) return false;
		setSaved(true);
		try {
			FileIO.save(new File(this.getFilePath()), this.getText());
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			UserMessager.showErrorDialogTB("prop.cannotsave.title","prop.cannotsave",e.getLocalizedMessage());
			return false;
		}
	}
	
	/**
	 * Refreshes the TextArea from the settings in the app object<br>
	 * given during initalization of the textArea
	 * @throws ValueNotFoundException
	 * @throws IOException
	 */
	public void refresh() {
        /* Refresh the font and language */
		this.setFont(app.getFont());
        String[] arr = FilePath.split("[.]");
        String ext = arr[arr.length-1];
		if (arr.length>1&&fileext.get(ext.toLowerCase().trim())!=null)
			setSyntaxEditingStyle(tx + ((String) fileext.get(ext)).toLowerCase().trim());
		else
			setSyntaxEditingStyle(tx + Main.settings.get(syn).toLowerCase().trim()); 
	}
	public boolean isSaved() {return saved;}
	public void setSaved(boolean saved) {this.saved = saved;window.getTab().repaint();}
	boolean b = false;
	synchronized boolean getb(){return b;}
	synchronized void setb(boolean b){this.b = b;}
    public void insertUpdate(DocumentEvent e) {updateLog();}
    public void removeUpdate(DocumentEvent e) {updateLog();}
    public void changedUpdate(DocumentEvent e) {/**/}

    public void updateLog() {
    	setSaved(false);
    	window.getTab().repaint();
    	getHighlighter().removeAllHighlights();
    	window.getScrollPane().countLines(this);
    }
	public boolean equals(String obj) {return FilePath.equals(obj);}
	@Override public int hashCode() {return this.getFilePath().hashCode();}
	@Override public boolean equals(Object obj) {
		if (!(obj instanceof String)) return super.equals(obj);
		return FilePath.equals(obj);
	}
	@Override public boolean isOpen(String path) {return FilePath.equals(path);}
	@Override public void escape() {
		if (fm==null) return;
		remove(fm);
		fm = null;
		repaint();
		requestFocus();
	}
	public static Map<String,Object> getSyntaxList() {
		HashMap<String, Object> values = new HashMap<String, Object>();
		Field[] fields = SyntaxConstants.class.getDeclaredFields();
		for(Field f : fields) {
			try {
				Object objectValue = "";
				Object value;
				value = f.get(objectValue);
				values.put(f.getName().replace("SYNTAX_STYLE_", "").toLowerCase().replace('_', ' '),
						String.valueOf(value).replace("text/", ""));
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return values;
	}
	@Override
	public JPopupMenu getComponentPopupMenu() {
		return getPopupMenu();
	}
}

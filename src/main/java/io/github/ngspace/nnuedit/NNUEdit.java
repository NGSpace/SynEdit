package io.github.ngspace.nnuedit;

import static io.github.ngspace.nnuedit.Main.settings;
import static io.github.ngspace.nnuedit.Main.theme;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_OFF;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_OFF;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
import static java.lang.System.out;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import io.github.ngspace.nnuedit.asset_manager.AssetManager;
import io.github.ngspace.nnuedit.asset_manager.extensions.ExtensionManager;
import io.github.ngspace.nnuedit.folder_management.FolderPanel;
import io.github.ngspace.nnuedit.menu.EditorTextArea;
import io.github.ngspace.nnuedit.menu.components.FolderButton;
import io.github.ngspace.nnuedit.menu.components.MenuThingy;
import io.github.ngspace.nnuedit.menu.components.Tab;
import io.github.ngspace.nnuedit.runner.IRunner;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.utils.ImageUtils;
import io.github.ngspace.nnuedit.utils.registry.Registries;
import io.github.ngspace.nnuedit.utils.settings.RefreshListener;
import io.github.ngspace.nnuedit.utils.ui.SwingUtils;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;
import io.github.ngspace.nnuedit.window.AboutWindow;
import io.github.ngspace.nnuedit.window.PreferencesWindow;
import io.github.ngspace.nnuedit.window.TextEditorWindow;
import io.github.ngspace.nnuedit.window.abstractions.Editor;
import io.github.ngspace.nnuedit.window.abstractions.Savable;
import io.github.ngspace.nnuedit.window.abstractions.Window;

/**
 * the editor
 */
public class NNUEdit extends JFrame implements WindowListener, AWTEventListener {
	private static final long serialVersionUID = 5770603365260133811L;
	
	public static final String PROJ = "project";
    public Color MenuBG = new Color(19, 19, 26);
    public static Color MenuFG = new Color(240, 240, 240);
	private Window SelectedWindow;
	public List<Window> Windows = new ArrayList<Window>(10);
	public JPanel pane;
	public FolderPanel Folder;
	public MenuThingy ToolBar;
	public FolderButton FolderButton;

	private static int Buffer = 5;
	private static int MNBS = 30;
	private static int TBS = 50;
	protected boolean isbusy = false;
	int intreval = 3;
	int frames = 10;
	
	public static final int MINIMUM_DISTANCE = 50;

	public static final String FONT = "clearview";
	private static Color getColor() {
		try {return theme.getColor("bgcolor");} catch (Exception e) {e.printStackTrace();}
		return new Color(19, 19, 26);
	}
	
	/**
	 * the app
	 * @param filepath the path to read from upon startup (leave empty for none)
	 * @throws IOException
	 */
	public NNUEdit(String filepath) throws IOException {
		
		MenuBG = getColor();
		
		setIgnoreRepaint(false);
		Registries.registerDefaults();
		ExtensionManager.preStartApp(this);
    	
		setOpacity(1);
		
    	String startfolder = "";
    	File startfile = null;
    	
    	if (!"".equals(filepath)) {
    		if (!new File(filepath).isDirectory()) startfile = new File(filepath);
    		else startfolder = new File(filepath).getAbsolutePath();
    	}
    	
    	setIconImage(ImageUtils.resizeIcon(AssetManager.getIcon("NNUEdit72x72"),64,64).getImage());
        pane = new AppPanel(this); 
    	FolderButton = new FolderButton(this);
        Folder = new FolderPanel(this);
		
		setBounds(0, 0, Main.scrSize.width/2, Main.scrSize.height/2);
		
		try {
			if (Main.settings.getBoolean("folder.openlast") && new File(Main.settings.get("folder.last")).exists())
					Folder.setFolderPath(Main.settings.get("folder.last"));
		} catch (Exception e) {e.printStackTrace();}
		
        if (!"".equals(startfolder)) Folder.setFolderPath(startfolder);
        
        setContentPane(pane);
    	try {
    		getRootPane().putClientProperty("JRootPane.titleBarForeground", Color.white);
    		getRootPane().putClientProperty("JRootPane.menuBarEmbedded", true);
    	} catch (Exception e) {System.err.println("UI Exception caught: " + e.getLocalizedMessage());}
        
        //Menu
        ToolBar = new MenuThingy(this);
        ToolBar.setBackground(MenuBG);
        ToolBar.setMinimumSize(new Dimension(getWidth(),100));
        ToolBar.setForeground(MenuFG);
        
        
        
        //Add Things to contentpane
        ToolBar.setBounds(0, 0, getWidth(), menuBarSize());
        pane.setLayout(null);
        pane.setSize(getSize());
        pane.setOpaque(true);
        pane.setBackground(MenuBG.brighter());
        pane.add(ToolBar);
        pane.add(Folder);
		pane.add(FolderButton);
        
        initListeners();
        
        setUndecorated(false);
        setBackground(new Color(MenuBG.getRed(), MenuBG.getGreen(), MenuBG.getBlue(), 255));
        setTitle(Main.EDITORNAME);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocation(getWidth()/2,getHeight()/2);
        pane.setDoubleBuffered(true);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        
        if (startfile!=null)
        	addWindow(Registries.WindowFactories.get(Editor.DEFAULT).createWindowFromFile(this, startfile),true,false);
    }
	
	public void initListeners() {
		Toolkit.getDefaultToolkit().addAWTEventListener(this,
				AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    	FolderButton.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				toggleFolder();
				if (isFolderOpen()) {
					FolderButton.setVisible(true);
		        	shouldAnimate.set(true);
				} else {
					FolderButton.setVisible(false);
		        	shouldAnimate.set(false);
				}
			}
		});
        DropTargetListener myDragDropListener = new DropTargetAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
	            List<File> droppedFiles = null;
				try {
					droppedFiles = (List<File>)
							dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
				} catch (Exception e) {e.printStackTrace();}
	            for (File file : droppedFiles) {
	            	if (file.isDirectory()) {
	            		UserMessager.showErrorDialogTB("err.importdir.title","err.importdir",file.getName());
	            		continue;
	            	}
	                openFile(file.getAbsolutePath());
	            }
			}
        };
        new DropTarget(this,DnDConstants.ACTION_COPY, myDragDropListener, true, null);
        
        addWindowStateListener(_ -> redraw());
        addWindowListener(this);
        
        addWindowFocusListener(new WindowFocusListener() {
            @Override public void windowLostFocus(WindowEvent e) {
        		getRootPane().putClientProperty("JRootPane.titleBarBackground", new Color(45,45,45));
            }
            @Override public void windowGainedFocus(WindowEvent e) {
        		getRootPane().putClientProperty("JRootPane.titleBarBackground", Color.black);
            }
        });
		getRootPane().addComponentListener(new ComponentAdapter() {
			private void exec() {redraw();}
        	@Override public void componentResized(ComponentEvent e) {exec();}
        	@Override public void componentMoved  (ComponentEvent e) {exec();}
			@Override public void componentShown  (ComponentEvent e) {exec();}
			@Override public void componentHidden (ComponentEvent e) {exec();}
		});
        
        getRootPane().setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.emptySet());
		
		Main.settings.addRefreshListener(_-> {
			isbusy = true;
			revalidate();
			redraw();
			isbusy = false;
		});
    	setMinimumSize(new Dimension(FolderPanel.MIN_WIDTH*2, 0));
	}
	
	/**
	 * There used to be a crash() function that will insta kill your ram and I removed it because I didn't want
	 * Extension creators to abuse it.
	 */
	
	/**
	 * Repaints all Tabs (not the entire Window!)
	 */
	public void repaintTabs() {for (Window i : Windows)i.getTab().repaint();}

	public void runApp() {
		if (saveAll(!isSaved())) {
			if (Registries.Runners.get(PROJ).canRun(this)) {
				Registries.Runners.get(PROJ).run(this);
				return;
			}
			if (runFile()) return;
			try {
				for (Entry<String, IRunner> i : Registries.Runners.entrySet()) {
					IRunner runner = i.getValue();
					if (runner.canRun(this)) {
						runner.run(this);
						return;
					}
				}
			} catch (Exception e) {e.printStackTrace();}
		}
	}
	
	public boolean runFile() {
		if (getSelectedWindow()!=null&&saveAll(!isSaved())&&getSelectedWindow().isEditor()) {
			try {
				File f = new File(getSelectedWindow().getEditor().getFilePath());
				for (Entry<String, IRunner> i : Registries.Runners.entrySet()) {
					IRunner runner = i.getValue();
					if (runner.canRunFile(f,this)) {runner.runFile(f,this);return true;}
				}
			} catch (Exception e) {e.printStackTrace();}
		}
		return false;
	}
	public void openFile(File path) {openFile(path.getAbsolutePath());}
	public void openFile(String path) {
		for (Window i : Windows) {
			if (i.isOpen(path)) {
				setSelectedWindow(i);
				return;
			}
		}
		try {
			addWindow(Window.createWindowFromFile(this, new File(path)));
		} catch (IOException e) {
			e.printStackTrace();
			UserMessager.showErrorDialogTB("err.openwindow.title","err.openwindow",e.getLocalizedMessage());
		}
	}
	public void openDefaultTextEditor(File path) {
		for (Window i : Windows) {
			if (i.isOpen(path.getAbsolutePath())) {
				setSelectedWindow(i);
				return;
			}
		}
		try {
			addWindow(Registries.WindowFactories.get(Editor.DEFAULT).createWindowFromFile(this, path));
		} catch (IOException e) {
			e.printStackTrace();
			UserMessager.showErrorDialogTB("err.openwindow.title","err.openwindow",e.getLocalizedMessage());
		}
	}
	
	public Window addWindow(Window w) {return addWindow(w,true,true);}
	public Window addWindow(Window w, boolean setaswindow) {return addWindow(w,setaswindow,true);}
	/**
	 * Adds a window
	 * @param w - the window
	 * @param setaswindow - whether to automatically select the window
	 * @param redraw - PLEASE ONLY USE IF YOU KNOW WHAT YOU ARE DOING.
	 * @return
	 */
	public Window addWindow(Window w, boolean setaswindow, boolean redraw) {
		if (w==null) return null;
		
		if (Windows.contains(w)) return w;
		for (Window i : Windows) i.getScrollPane().setVisible(false);
		
        pane.add(w.getScrollPane());
        pane.add(w.getTab());
        Windows.add(w);
        if (setaswindow) {
        	if (getSelectedWindow()!=null)
        		getSelectedWindow().lostFocus(w);
            setSelectedWindow(w,false);
        } else {
            setSelectedWindow(getSelectedWindow(),false);
            w.getScrollPane().setVisible(false);
        }
        if (redraw) {
        	redraw();
        }
        return w;
	}

	public void setSelectedWindow(Window w) {setSelectedWindow(w,true);}
	public void setSelectedWindow(Window w,boolean redraw) {
		boolean isAdded = false;
		
		for (Window i : Windows) {
			if (i==w) isAdded = true;
			i.getScrollPane().setVisible(false);
		}
		if (!isAdded) addWindow(w);
		
		if (w!=null) {
			if (SelectedWindow!=null) SelectedWindow.lostFocus(w);
			w.gainedFocus(SelectedWindow);
			w.getScrollPane().setVisible(true);
	        w.getComponent().setBorder(new EmptyBorder(0,5,0,0));
			w.getComponent().requestFocus();
			SelectedWindow = w;
		} else Folder.setVisible(true);
		
		if (redraw) redraw();
	}
	
	public boolean closeSelectedWindow() {
		if (SelectedWindow==null) close();
		boolean b = SelectedWindow.closeEvent(!SelectedWindow.isSaved());
		if (!b) return false;
		
		int loc = Windows.indexOf(SelectedWindow)-1;
		
		SelectedWindow.lostFocus(null);
		
		pane.remove(SelectedWindow.getComponent());
		pane.remove(SelectedWindow.getTab());
		pane.remove(SelectedWindow.getScrollPane());
		SelectedWindow.getComponent().removeAll();
		SelectedWindow.getScrollPane().removeAll();
		Windows.remove(SelectedWindow);
		SelectedWindow = null;
		if (!Windows.isEmpty()) {
			if (loc>-1)
				setSelectedWindow(Windows.get(loc));
			else
				setSelectedWindow(Windows.get(0));
		} else {
			Folder.setVisible(true);
		}
		redraw();
		return true;
	}
	
	private List<RefreshListener> preRedrawListeners = new ArrayList<RefreshListener>();
	
	public void addPreRefreshListener(RefreshListener listener) {
		preRedrawListeners.add(listener);
	}
	
	private List<RefreshListener> redrawListeners = new ArrayList<RefreshListener>();
	
	public void addRedrawListener(RefreshListener listener) {
		redrawListeners.add(listener);
	}
	
	private static void dispatchRefresh(List<RefreshListener> ls) {
		for (RefreshListener ccl : ls) ccl.refresh(settings);
	}
	
	AtomicBoolean drawing = new AtomicBoolean();
	
	public void redraw() {
		if (drawing.get()) return;
		drawing.set(true);
		
		dispatchRefresh(preRedrawListeners);
		
		if (ToolBar!=null) ToolBar.setBounds(0, 0, getWidth(), menuBarSize());
		
		int x = isFolderOpen() ? Folder.getWidth() : -getBuffer()-0;
		int width = pane.getWidth();
    	int tabWidth = Math.min((width - x)/(!Windows.isEmpty() ? Windows.size() : 1), 325);

		int i = 0;
        for (Window win : Windows) {
        	win.getScrollPane().setBounds(x, menuBarSize() + tabSize(), width - x,
        			pane.getHeight() - menuBarSize() - tabSize());
        	win.getComponent().setSize(width - x - 14, win.getComponent().getHeight());
        	win.getTab().setBounds((tabWidth*i++) + x + getBuffer() - Tab.padding, menuBarSize(), tabWidth, tabSize());
        }
        Folder.setBounds(0,menuBarSize(),Folder.getWidth(), pane.getHeight()-menuBarSize());
        Folder.FilesPanel.setBounds(0,Folder.menuBarSize(),
        		Folder.getWidth()-FolderPanel.DRAGBAR_WIDTH, Folder.getHeight()-menuBarSize());
        Folder.refresh(false);
        FolderButton.setLocation((isFolderOpen()?Folder.getWidth():0),(pane.getHeight()-menuBarSize()-tabSize())/2);
        
		dispatchRefresh(redrawListeners);
        
        revalidate();
        repaint();
        
		drawing.set(false);
	}

	/**
	 * calls save and asks user where to save if non is selected already.
	 * @return whether the operation was successful
	 */
	public boolean saveAll(boolean ask) {
		if (getSelectedWindow()==null) return true;
		if (SelectedWindow.isEditor()&&(Windows.size()==1&&"".equals(SelectedWindow.getEditor().getFilePath()))) {
			int result = UserMessager.confirmTB("confirm.save","confirm.save");
	    	if (result==UserMessager.NO) return true;
	    	if (result==UserMessager.CANCEL) return false;
	    	SelectedWindow.getEditor().openfile(false, true);
		}
		if (Windows.size()!=1) {
			if (ask) {
				int result = UserMessager.confirmTB("confirm.save","confirm.save");
		    	if (result==UserMessager.NO) return true;
		    	if (result==UserMessager.CANCEL) return false;
		    	for (Window i : Windows)
		    		if (i.getComponent() instanceof EditorTextArea tx && "".equals(tx.getFilePath()))
		    	    	tx.openfile(false, true);
			}
			boolean res = true;
			for (Window i: Windows) res = i.save(ask);
			return res;
		}
		if (SelectedWindow.getComponent() instanceof Savable) return SelectedWindow.save(ask);
		return true;
	}
	
	
	public void openFolderDialog() {
		String s = FileIO.openFolderDialog();
		Folder.setFolderPath(s==null ? Folder.getFolderPath() : s.trim());
	}
	
	/**
	 * Pull up the close menu and runs System.exit(0); if user agrees.
	 * @throws InterruptedException 
	 */
	public void close() {
		try {
			boolean saved = isSaved();
			if (isBusy()) while (isBusy()) 
				{Thread.sleep(100);out.println("Busy, Waiting for the clear.");}
			
			if (saved&&!isBusy()) {
				ExtensionManager.shutdownApp(this);
				Main.crash(0);
			}
			int result = !saved ? 
					UserMessager.confirmTB("confirm.exit","confirm.exit") : UserMessager.YES;
	    	if (result==UserMessager.NO||result==UserMessager.CANCEL)
	    		return;
	    	boolean save = isSaved() || saveAll(true);
	    	if (!save) return;
	    	ExtensionManager.shutdownApp(this);
	    	Main.crash(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Main.crash(1);
		}
	}
	
	private boolean isSaved() {
		for (Window i : Windows)
			if (!i.isSaved()) return false;
		return true;	
	}
	
	/**
	 * Close a window that is loaded but not selected
	 * @param window
	 * @return
	 */
	public boolean closeNotSelectedWindow(Window window) {
		if (window==SelectedWindow) return closeSelectedWindow();
		
		if (!window.closeEvent(true)) return false;
		
		window.close();
		pane.remove(window.getComponent());
		pane.remove(window.getTab());
		pane.remove(window.getScrollPane());
		window.getScrollPane().removeAll();
		Windows.remove(window);
		redraw();
		return true;
	}
	
	public void toggleFolder() {
		Folder.setVisible(!Folder.isVisible());
		redraw();
	}

	public void closeWindow(Window window) {
		if (getSelectedWindow()==window) closeSelectedWindow();
		else closeNotSelectedWindow(window);
	}

	public static void adjustAntialias(Graphics2D gra, boolean fancytext) {
		if (getAntialiasing()) {
			if (!(settings.getBoolean("editor.powerantialias")||fancytext))
				gra.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
			else 
				gra.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_LCD_HRGB);
			gra.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
		} else {
			gra.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_OFF);
			gra.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_OFF);
		}
	}
	public static void adjustAntialias(Graphics gra, boolean fancytext) {adjustAntialias((Graphics2D)gra,fancytext);}
	
	public static int getBuffer() {return Buffer;}
	public static int menuBarSize() {return MNBS;}
	public static int tabSize() {return TBS;}
	
	public boolean isBusy() {return isbusy;}
	public int getScale() {return 1;}
	public static Font getTipFont() {return new Font(FONT, Font.BOLD, 40);}
	public Window getSelectedWindow() {return SelectedWindow;}
	public boolean isFolderOpen() {return Folder.isVisible();}
	public static boolean getAntialiasing() {return settings.getBoolean("editor.antialias");}
	@Override public Font getFont() {
		try {
			return settings.getFont("editor.font");
		} catch (Exception e) {
			e.printStackTrace();
			return new Font(Font.MONOSPACED, Font.BOLD, 40);
		}
	}
	public Point getMouseLocation() {
	    int x = getX() - MouseInfo.getPointerInfo().getLocation().x;
	    int y = getY() - MouseInfo.getPointerInfo().getLocation().y;
	    return new Point(Math.abs(x), Math.abs(y));
	}
	@Override public void windowClosing(WindowEvent e) {close();}
	@Override public void windowOpened (WindowEvent e) {/**/}
	@Override public void windowClosed (WindowEvent e) {/**/}
	@Override public void windowIconified  (WindowEvent e) {redraw();}
	@Override public void windowDeiconified(WindowEvent e) {redraw();}
	@Override public void windowActivated  (WindowEvent e) {redraw();}
	@Override public void windowDeactivated(WindowEvent e) {redraw();}
	
	AtomicBoolean isAnimatingFolderButton = new AtomicBoolean();
	AtomicBoolean shouldAnimate = new AtomicBoolean();
	
	@Override public void eventDispatched(AWTEvent ev) {
		/* It's not perfect but it works... */
		if (ev instanceof MouseEvent) {
	    	Point p = getMouseLocation();
	    	if (p.getX()<(isFolderOpen()?Folder.getWidth():0)+MINIMUM_DISTANCE) {
	    		if (!FolderButton.isVisible()) {
	    			if (isAnimatingFolderButton.get()) return;
	    			isAnimatingFolderButton.set(true);
	    			shouldAnimate.set(true);
		    		FolderButton.setVisible(true);
		        	FolderButton.setLocation((isFolderOpen()?Folder.getWidth():0)-30,
		        			(pane.getHeight() - menuBarSize() - tabSize())/2);
		        	SwingUtils.animate(FolderButton, new Point(isFolderOpen()?Folder.getWidth():0,
		        			(pane.getHeight() - menuBarSize() - tabSize())/2), frames, intreval,
		        			()->isAnimatingFolderButton.set(false));
	    		}
	    	} else {
    			if ( isAnimatingFolderButton.get()) return;
    			if (!shouldAnimate.get()) return;
	        	isAnimatingFolderButton.set(true);
	        	shouldAnimate.set(false);
	    		FolderButton.setVisible(true);
	        	FolderButton.setLocation((isFolderOpen()?Folder.getWidth():0),
	        			(pane.getHeight() - menuBarSize() - tabSize())/2);
	        	SwingUtils.animate(FolderButton, new Point((isFolderOpen()?Folder.getWidth():0)-30,
	        			(pane.getHeight() - menuBarSize() - tabSize())/2), frames, intreval,
	        			()->{isAnimatingFolderButton.set(false);FolderButton.setVisible(false);});
	    	}
		}
		if (ev instanceof KeyEvent e) {
			if (e.getID()!=KeyEvent.KEY_RELEASED) return;
			if (e.getKeyCode()==KeyEvent.VK_F5) {runApp();return;}
			if (e.getKeyCode()==KeyEvent.VK_ESCAPE) {
		    	if (getSelectedWindow()!=null&&(getSelectedWindow().isEditor()))
		    		getSelectedWindow().getEditor().escape();
		    	return;
		    }
			if (e.isControlDown()) {
				if (e.isShiftDown()) {
					if (e.getKeyCode()==KeyEvent.VK_S) {saveAll(false);repaintTabs();}
					return;
				}
				switch (e.getKeyCode()) {
					case KeyEvent.VK_S: {
			        	if (SelectedWindow!=null&&SelectedWindow.getComponent() instanceof Savable savable) {
			        		savable.save(false);
			        		repaintTabs();
			        	}break;
					}
					case KeyEvent.VK_N: {
						try {
			        		if (settings.getBoolean("system.debugbutton")) {
			        			Registries.WindowFactories.clear();
			        			Registries.WindowFactories.put(Editor.DEFAULT, (a,_) -> new AboutWindow(a));
			        		}
						} catch (Exception e1) {
							e1.printStackTrace();
						}break;
					}
					case KeyEvent.VK_F: {
						if (getSelectedWindow()!=null && getSelectedWindow().isEditor())
				    		getSelectedWindow().getEditor().openFind();
						break;
					}
					case KeyEvent.VK_G: {
				    	if (getSelectedWindow()!=null 
				    		&&(getSelectedWindow().getComponent() instanceof EditorTextArea tx)) tx.gotoLineMenu();
				    	break;
				    }
					case KeyEvent.VK_O: {
		        		String file = FileIO.openFileDialog(false);
		        		if (file==null) return;
		                addWindow(new TextEditorWindow(this,file));break;
				    }
					case KeyEvent.VK_W: {closeSelectedWindow();break;}
					case KeyEvent.VK_TAB: {
		        		int loc = Windows.indexOf(SelectedWindow);
		        		setSelectedWindow(Windows.get(loc+1>Windows.size()-1?0:loc+1));
		        		break;
		        	}
					case KeyEvent.VK_P: {addWindow(new PreferencesWindow(this));break;}
					case KeyEvent.VK_T: {addWindow(new TextEditorWindow(this, ""));break;}
					default:
						break;
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	@Override public void reshape(int x, int y, int width, int height) {super.reshape(x, y, width, height);redraw();}
}
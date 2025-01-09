package io.github.ngspace.nnuedit.menu.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import org.fife.ui.rsyntaxtextarea.folding.Fold;
import org.fife.ui.rsyntaxtextarea.folding.FoldManager;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.menu.EditorTextArea;

public class NGSScrollPane extends JScrollPane implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = 3952255173637165838L;
	private NNUEdit app;
	public int Number = 0;
	
	
	
	public NGSScrollPane(NNUEdit app, Container contentPane) {this(app);this.getViewport().setView(contentPane);}
	public NGSScrollPane(NNUEdit app) {super();this.app=app;init();}
	
	
	
	public void init() {
        setBorder(null);
        setOpaque(true);
		setDoubleBuffered(true);
		getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		NGSScrollPane sp = this;
		
		setViewport(new JViewport() {
			private static final long serialVersionUID = -8588144347690129055L;
			@Override public void setView(Component view) {
				if (view instanceof JTextComponent obj) {
					countLines(obj);
					view.addMouseMotionListener(sp);
					view.addMouseListener(sp);
				}super.setView(view);
			}
		});
	}
	
	@Override public void paint(Graphics g) {NNUEdit.adjustAntialias(g, false);super.paint(g);g.dispose();}
	
	/**
	 * My proudest peice of code
	 */
	public void paintLines(Graphics g) {
		if (getViewport().getView() instanceof EditorTextArea i && Main.settings.getBoolean("editor.numberlines")) {
			NNUEdit.adjustAntialias(g,false);
			FoldManager fm = i.getFoldManager();
			Point position = getViewport().getViewPosition();
			Rectangle r = getViewport().getViewRect();
			FontMetrics gfm = g.getFontMetrics();
			
			long y = -(gfm.getHeight()/4);
			int newlines = "".equals(i.getText()) ? 1 : Number;
			int start = r.y/gfm.getHeight();
			
			int offset = fm.getHiddenLineCountAbove(start, true);
			y+=gfm.getHeight()*start;
			
			g.setColor(getForeground());
			g.setFont(i.getFont());
			for (int j = start;j+offset<newlines;j++) {
				String s = String.valueOf(j+1+offset);
				long x = gfm.stringWidth(s)-(long)position.x+(getVerticalScrollBar().isVisible()?
						getVerticalScrollBar().getWidth() : 0);
				y+=gfm.getHeight();
				Fold fold = fm.getFoldForLine(j+offset);
				try {
					if (fold!=null&&fold.isCollapsed()&&fold.getEndOffset()!=Integer.MAX_VALUE)
						offset+=i.getLineOfOffset(fold.getEndOffset())-j-offset;
					
					if (fold!=null) g.setColor(Color.red);
					else g.setColor(getForeground());
				} catch (Exception e) {return;}
				if (y>r.y+(getHeight()+20)) break;
				if (y>r.y&&y-(getHeight()+20)<r.y) g.drawString(s, (int) (getWidth()-x), (int) y);
			}
		}
	}
	
	public void paintSeperators(Graphics2D gra) {
		if (!app.isFolderOpen()) return;
		gra.setColor(app.pane.getBackground());
		gra.fillRect(0, 0, NNUEdit.getBuffer(), getHeight());
	}
	
	@Override public void repaint() {revalidate();super.repaint();}
	
	
	
    public void countLines(JTextComponent textArea) {Document d = textArea.getDocument();
        try {Number = d.getText(0, d.getLength()).split("\r\n|\r|\n",-1).length;
		} catch (BadLocationException e) {e.printStackTrace();}
    }
    
    
    
	@Override public void mouseClicked(MouseEvent e)  {
		if (!(e.getSource() instanceof JTextComponent)) return;
		JTextComponent component = (JTextComponent) e.getSource();
		if (component instanceof EditorTextArea i && Main.settings.getBoolean("editor.numberlines")) {
			FoldManager fm = i.getFoldManager();
			Rectangle r = getViewport().getViewRect();
			FontMetrics gfm = getFontMetrics(i.getFont());
			
			int start = r.y/gfm.getHeight();
			int mousey = e.getY()/gfm.getHeight();

			int offset = fm.getHiddenLineCountAbove(start+mousey, true);
			Fold fold = fm.getFoldForLine(offset+mousey);
			
			int region = Math.max(gfm.stringWidth(String.valueOf(start+offset+mousey)),gfm.stringWidth("00"));
			if (fold!=null&&(e.getX()>getWidth()-region))fold.toggleCollapsedState();
		}
	}
	@Override public void mouseMoved(MouseEvent e)    {
		if (!(e.getSource() instanceof JTextComponent)) return;
		JTextComponent component = (JTextComponent) e.getSource();
		boolean hand = false;
		if (component instanceof EditorTextArea i && Main.settings.getBoolean("editor.numberlines")) {
			FoldManager fm = i.getFoldManager();
			Rectangle r = getViewport().getViewRect();
			FontMetrics gfm = getFontMetrics(i.getFont());
			
			int start = r.y/gfm.getHeight();
			int mousey = e.getY()/gfm.getHeight();
			
			int offset = fm.getHiddenLineCountAbove(start+mousey, true);
			Fold fold = fm.getFoldForLine(offset+mousey);
			
			int region = Math.max(gfm.stringWidth(String.valueOf(start+offset+mousey)),gfm.stringWidth("00"));
			if (fold!=null&&(e.getX()>getWidth()-region)) hand = true;
		}
		if (hand) component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		else component.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
	}
	@Override public void mousePressed(MouseEvent e)  {/**/}
	@Override public void mouseReleased(MouseEvent e) {/**/}
	@Override public void mouseEntered(MouseEvent e)  {/**/}
	@Override public void mouseExited(MouseEvent e)   {/**/}
	@Override public void mouseDragged(MouseEvent e)  {/**/}

}

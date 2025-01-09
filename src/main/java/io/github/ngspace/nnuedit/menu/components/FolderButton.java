package io.github.ngspace.nnuedit.menu.components;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JButton;

import io.github.ngspace.nnuedit.NNUEdit;

public class FolderButton extends JButton {
	
	private static final long serialVersionUID = 1696619060167421515L;
	
	private NNUEdit app;

	public FolderButton(NNUEdit app) {
		this.app=app;
    	setOpaque(true);
    	setBorderPainted(false); 
    	setContentAreaFilled(false); 
    	setFocusPainted(false); 
    	setOpaque(false);
    	setVisible(false);
    	setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    	setBounds(100, 100,60,70);
	}
	
	

	@Override protected void paintComponent(Graphics gra) {paintButton(gra,0,0);}
	private void paintButton(Graphics gra, int x, int y) {
		if (!isEnabled()) return;
		Graphics2D g = (Graphics2D)gra;
		int w = getWidth();
		int he = getHeight();

		int h = he - 5;
		g.setColor(app.MenuBG);
		g.fillOval(x-(w/2), y, w, h);
		NNUEdit.adjustAntialias(g,false);
	    g.setComposite(AlphaComposite.getInstance(
	            AlphaComposite.SRC_OVER, .3f));
	    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		g.setColor(getForeground().darker());
		g.setStroke(new BasicStroke(3));
		g.drawOval(x+-(w/2), y, w, h-1);
		g.setColor(getForeground());
		if (app.isFolderOpen()) {
			g.drawLine(x+1, y+he/2-2, x+w-40, y+20);
			g.drawLine(x+1, y+he/2-2, x+w-40, y+15+33);
		} else {
			g.drawLine(x+w-40, y+he/2 - 2, x+6, y+18);
			g.drawLine(x+w-40, y+he/2 - 2, x+6, y+15+33);
		}
	}

}

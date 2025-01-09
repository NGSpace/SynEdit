package io.github.ngspace.nnuedit;

import static io.github.ngspace.nnuedit.asset_manager.StringTable.getRaw;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class AppPanel extends JPanel {

	private static final long serialVersionUID = 890330040427900164L;
	public static int RoundedBox = 20;
	public final NNUEdit app;
	
	public AppPanel(NNUEdit app) {this.app = app;}
	
	@Override public void paint(Graphics gra) {
		super.paint(gra);
		if (app.getSelectedWindow()==null||!app.getSelectedWindow().getScrollPane().isVisible()) {
    		Graphics2D g = (Graphics2D) gra;
    		g.setFont(NNUEdit.getTipFont());
    		g.setColor(NNUEdit.MenuFG);
    		
    		NNUEdit.adjustAntialias(g,true);
    		
    		String string = getRaw("system.clickhere");
    		String[] strs = string.split("%",2);
    		
    		if (strs.length!=2) strs = new String[] {"STR "," MISSING"};
    		
    		string = strs[0] + "CTRL + O" + strs[1];
    		int keywidth = g.getFontMetrics().stringWidth(strs[0]);
    		int ctrlwidth = g.getFontMetrics().stringWidth("CTRL + 0 ");
    		int totalwidth = g.getFontMetrics().stringWidth(string);

    		int x = getWidth() / 2 - (totalwidth / 2) + 
    				(app.isFolderOpen() ? app.Folder.getWidth() : 0)/2;
    		
    		int y = ((getHeight() - g.getFontMetrics().getHeight()) / 2)
    				+ g.getFontMetrics().getAscent();
    		int strheight = (int) g.getFontMetrics().getLineMetrics(string, g).getHeight();
    		g.drawString(string, x, y);
    		
    		g.drawRoundRect(x+keywidth-5, y-strheight+5, ctrlwidth+10, strheight+5, RoundedBox, RoundedBox);
    		
		}
	}
}

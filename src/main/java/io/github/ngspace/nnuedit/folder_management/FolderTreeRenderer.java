package io.github.ngspace.nnuedit.folder_management;

import static io.github.ngspace.nnuedit.folder_management.FolderPanel.ROW_HEIGHT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.border.Border;
import javax.swing.tree.DefaultTreeCellRenderer;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.asset_manager.AssetManager;
import io.github.ngspace.nnuedit.utils.FileIO;
import io.github.ngspace.nnuedit.utils.ImageUtils;
import io.github.ngspace.nnuedit.utils.ui.SmartGraphics2D;

public class FolderTreeRenderer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = -467043583904458260L;
	
    Border border = BorderFactory.createLineBorder(Color.black);
    String[] exceptions = {".nnuproject"};
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, 
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
	    super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	    String file = value.toString();
	    String n = FileIO.getFileName(file);
        this.setText(n);
        this.setToolTipText(n);
        this.setBackgroundSelectionColor(new Color(50,50,50));
        this.setForeground(new Color(164,164,164));
        
        Icon ico = AssetManager.getIconOfFile(new File(file),ROW_HEIGHT,ROW_HEIGHT);
        
        if (!FileIO.getFileName(file).startsWith(".")||contains(exceptions,FileIO.getFileName(file)))
        	this.setIcon(ico);
        else {
        	this.setIcon(ImageUtils.asTransparentIcon(.5f,ico));
        	this.setForeground(getForeground().darker().darker().darker());
	        this.setBackgroundSelectionColor(getBackgroundSelectionColor().darker().darker());
        }
        return this;
	}
	
	@Override public void paintComponent(Graphics g) {
		Graphics2D gra = (Graphics2D) g;
		NNUEdit.adjustAntialias(gra,true);
		
		Graphics2D gr = new SmartGraphics2D((Graphics2D) g,new Rectangle(100,100)) {
			@Override public void setRenderingHint(Key hintKey, Object hintValue) {
				if (hintKey!=RenderingHints.KEY_ANTIALIASING&&hintKey!=RenderingHints.KEY_TEXT_ANTIALIASING)
					super.setRenderingHint(hintKey, hintValue);
			}
		};
        ui.update(gr, this);
	}

	private boolean contains(Object[] arr, String fileName) {
		for(Object s : arr) if(fileName.equals(s)) return true;
		return false;
	}
}
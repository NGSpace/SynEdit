package io.github.ngspace.nnuedit.window;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JComponent;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.asset_manager.AssetManager;
import io.github.ngspace.nnuedit.menu.components.NGSScrollPane;
import io.github.ngspace.nnuedit.menu.components.Tab;
import io.github.ngspace.nnuedit.utils.ImageUtils;
import io.github.ngspace.nnuedit.window.abstractions.Window;
import io.github.ngspace.nnuedit.window.abstractions.WindowMenu;

public class ImageWindow extends WindowMenu implements Window {

	private static final long serialVersionUID = 6893937793741839803L;
	public final NNUEdit app;
	private BufferedImage img;
	private final String filepath;
	protected final NGSScrollPane sp;
	protected final String title;
	protected final Tab tab;

	public ImageWindow(NNUEdit app, File f) {
		this.title = f.getName();
		this.sp = new NGSScrollPane(app);
		
		this.filepath=f.getAbsolutePath();
		
		this.app = app;
		try {
			this.img = ImageUtils.readImageFromFile(f);
			setLayout(null);
	        setBackground(Window.color);
	        setForeground(Color.LIGHT_GRAY);
	        setPreferredSize(new Dimension(img.getWidth() + NNUEdit.getBuffer(),img.getHeight()));
		} catch (Exception e) {
			/* Change image to an empty one to avoid a null pointer exception */
			this.img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
			e.printStackTrace();
		}
        
		sp.setViewportView(this);
        this.tab = new Tab(app, this);
        tab.setIcon(AssetManager.getIconOfFile(f));
        
        sp.setOpaque(true);
	}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.drawImage(img, 0 + NNUEdit.getBuffer(), 0, img.getWidth(), img.getHeight(), null);
	}
	@Override public String getTitle() {return title;}
	@Override public NGSScrollPane getScrollPane() {return sp;}
	@Override public JComponent getComponent() {return this;}
	@Override public Tab getTab() {return tab;}
	@Override public NNUEdit getApp() {return app;}
	@Override public boolean isOpen(String path) {return filepath.equals(path);}
}


package io.github.ngspace.nnuedit.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sf.image4j.codec.ico.ICODecoder;

public class ImageUtils {private ImageUtils() {}
	private static ImageIcon missingIcon = getMissingIcon();
	public static ImageIcon getMissingIcon() {
		if (missingIcon==null) {
			try {
				//After much thinking I realized that if you can't read image assests you'll probably not be able to
				//read the missingicon.png asset...
				BufferedImage img = new BufferedImage(2, 2, 1);
				img.setRGB(0, 0, 0xdd00ff);
				img.setRGB(1, 1, 0xdd00ff);
				missingIcon = new ImageIcon(img);
			} catch (Exception e) {e.printStackTrace();}
		}
		return missingIcon;
	}
	
	/*
	 * Image manipulation
	 */
	
	/**
	 * Takes an image and returns the same image with the requested transparency
	 * @param transparency
	 * @param icon
	 * @return
	 */
	public static Icon asTransparentIcon(float transparency, Icon icon) {
		int w = icon.getIconWidth();
		int h = icon.getIconHeight();
		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,transparency));
		icon.paintIcon(null, g, 0, 0); g.dispose();
		return new ImageIcon(image);
	}
	
	
	
	public static Image iconToImage(Icon icon) {
		if (icon instanceof ImageIcon imgicon) return imgicon.getImage();
		else {
			int w = icon.getIconWidth();
			int h = icon.getIconHeight();
			BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setComposite(AlphaComposite.Src);
			icon.paintIcon(null, g, 0, 0);
			g.dispose();
			return image;
		}
	}
	
	
	
	public static ImageIcon resizeIcon(Icon c, int Width, int Height) {
		Image newimg = iconToImage(c).getScaledInstance(Width, Height,Image.SCALE_SMOOTH);
		return new ImageIcon(newimg);
	}
	
	
	
	public static Image resizeImage(Image c, int Width, int Height) {
		if (c.getWidth(null)==Width&&c.getHeight(null)==Height) return c;
		return c.getScaledInstance(Width, Height, Image.SCALE_SMOOTH);
	}
	
	
	
	/*
	 * Read Images
	 */
	
	
	
	public static ImageIcon readIconFromStream(InputStream is) {
		try {
			if (is==null) {
				System.err.println("failed to read ");
				return getMissingIcon();
			}
			return new ImageIcon(ImageIO.read(is));
		} catch (IOException e) {
			e.printStackTrace();
			return getMissingIcon();
		}
	}
	
	
	
	public static BufferedImage readImageFromFile(File f) throws IOException {
		if (FileIO.getFileExt(f.getAbsolutePath()).equalsIgnoreCase("ico")) return ICODecoder.read(f).get(0);
		else {
			ImageReader ir = getReader(FileIO.getFileExt(f.getAbsolutePath()));
			if (ir==null) throw new FileNotFoundException(f.getAbsolutePath());
			ImageReadParam defaultReadParam = ir.getDefaultReadParam();
			ir.setInput(ImageIO.createImageInputStream(new FileInputStream(f)), true);
			return ir.read(0, defaultReadParam);
		}
	}
	
	
	
	public static ImageReader getReader(String type) {
		final Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(type);
		if (iterator.hasNext()) return iterator.next();
		return null;
	}
	

	public static ImageIcon asImageIcon(Icon c) {
		return new ImageIcon(iconToImage(c));
	}
}

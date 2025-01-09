package io.github.ngspace.nnuedit.folder_management;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.menu.components.IconButton;

public class XButton extends IconButton {
	public XButton(Icon icon) {super(icon);}
	public XButton() {this(null);}
	private static final long serialVersionUID = -1603637585446225837L;
	@Override public void paint(Graphics g) {
		super.paint(g);
		NNUEdit.adjustAntialias(g, false);
		g.setColor(Color.red.darker());
		((Graphics2D)g).setStroke(new BasicStroke(3));
		g.drawLine(3, 3, getWidth()-3, getHeight()-3);
		g.drawLine(3, getHeight()-3,getWidth()-3,3);
	}
}

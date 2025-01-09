package io.github.ngspace.nnuedit.asset_manager.extensions;

import java.awt.Font;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JPanel;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.menu.components.IconButton;
import io.github.ngspace.nnuedit.menu.components.SmartJLabel;
import io.github.ngspace.nnuedit.menu.prefrences.options.AOption;
import io.github.ngspace.nnuedit.utils.ImageUtils;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class BasicExtensionPanel extends JPanel {
	private static final long serialVersionUID 	= 3168985161109597920L;
	
	
	
	public BasicExtensionPanel(ExtensionValues ext, int width) {
		this.setBounds(0, 0, width, 100);
		setBackground(Window.color);
		setForeground(NNUEdit.MenuFG);
		setLayout(null);
		Map<String,Object> extSettings = ext.configuration;
		String extname = String.valueOf(extSettings.get("name"));
		String iconloc = String.valueOf(extSettings.get("icon"));
		if ("null".equals(iconloc)) iconloc = null;
		
		Object description = extSettings.get("description");
		
		SmartJLabel name = new SmartJLabel(extname + " : " + extSettings.get("version"));
		name.setBackground(Window.color);
		name.setForeground(NNUEdit.MenuFG);
		name.setBounds(0,0,getWidth(),getHeight()/(description==null ? 1 : 3));
		name.setFont(AOption.f);
		
		if (iconloc!=null) {
			try {
				Icon i = ImageUtils.readIconFromStream(ext.extensionLoader.getResourceAsStream(iconloc));
				int ratiow = i.getIconWidth()/(i.getIconHeight()/100);
				IconButton ponent = new IconButton(i);
				ponent.setBackground(Window.color);
				ponent.setForeground(NNUEdit.MenuFG);
				ponent.setBounds(0, 0, ratiow, 100);
				add(ponent);
			} catch (Exception e) {e.printStackTrace();}
		}
		if (description!=null) {
			SmartJLabel desc = new SmartJLabel(description);
			desc.setBackground(Window.color);
			desc.setForeground(NNUEdit.MenuFG);
			desc.setBounds(0,getHeight()/3,getWidth(),getHeight()/3 * 2);
			Font f = new Font(AOption.FT, Font.BOLD, 16);
			desc.setFont(f);
			add(desc);
		}
		add(name);
	}
}

package io.github.ngspace.nnuedit.menu.prefrences;

import static io.github.ngspace.nnuedit.utils.registry.Registries.PreferencesTabs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.border.EmptyBorder;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.menu.components.SmartJLabel;
import io.github.ngspace.nnuedit.menu.prefrences.options.AOption;
import io.github.ngspace.nnuedit.menu.prefrences.tabs.APreferenceTab;
import io.github.ngspace.nnuedit.window.abstractions.Window;
import io.github.ngspace.nnuedit.window.abstractions.WindowMenu;

public class PreferencesMenu extends WindowMenu {
	
	public static int BUTTON_SPACING = 30;
	public static int BUTTONS_HEIGHT = 100;
	public static Color hoverColor = new Color(10,100,200);
	public static Color selectedColor = new Color(62, 155, 189);
	public static final Font f = new Font(AOption.FT, Font.BOLD, 40);
	
	private static final long serialVersionUID = 5027581198213070489L;
	
	APreferenceTab selectedTab = null;
	
	/**
	 * Didn't give enough fucks to make an actual fix so <code>j</code> it is!
	 */
	boolean j = false;
	
	/**
	 * Creates and opens the prefrences menu on the default tab
	 * @param window - the window linked to the menu
	 */
	public PreferencesMenu(Window window) {this(window,PreferencesTabs.get(PreferencesTabs.size()-1));}
	
	/**
	 * Creates and opens the prefrences menu
	 * @param stng the settings linked to the menu
	 * @param app the app linked to the menu
	 */
	public PreferencesMenu(Window window, APreferenceTab tab) {
		super(window);
		this.selectedTab = tab;
		setOpaque(true);
		setBackground(Window.color);
		setForeground(Color.white.darker());
		
        setBorder(new EmptyBorder(0,NNUEdit.getBuffer(),0,0));
		setLayout(null);
		
		Main.settings.addRefreshListener(_->refresh());
		window.getApp().addRedrawListener(_->refresh());
	}
	
	/**
	 * Here you used to be able to see all my failures and my inability to add basic features like:
	 *   <li>Resizing the folder view. // Added that one lol
	 *   <li>Move the numbers from the right to the left.
	 *   <li>Auto pausing the shell when running projects.<br><br> //Silent option!
	 * 
	 * This is all because of my inability to think into the future.<br>
	 */
	public void refresh() {
		Main.settings.processSave(Main.settings.getFile());
		
		removeAll();
		
		int txtwidth = 0;
		FontMetrics fm=getFontMetrics(f);
		
		for (APreferenceTab tab : PreferencesTabs) txtwidth+=fm.stringWidth(tab.getName())+BUTTON_SPACING;
		
		txtwidth+=BUTTON_SPACING;
		int tabLocation = -(txtwidth/2);
		for (APreferenceTab tab : PreferencesTabs) {
			String sn = tab.getName();
			tabLocation+=fm.stringWidth(sn)+BUTTON_SPACING;
			SmartJLabel text = new SmartJLabel(sn);
			
			text.setBounds(getWidth()/2-tabLocation-20, 0, fm.stringWidth(sn)+40, BUTTONS_HEIGHT);
			text.setFont(f);
			text.setUnderlineEnabled(true);
			text.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			
			text.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent e) {j=true;selectedTab = tab;refresh();}
				@Override public void mouseEntered(MouseEvent e) {
					text.setFont(new Font(AOption.FT, Font.BOLD, f.getSize()+5));
					text.setForeground(hoverColor);
				}
				@Override public void mouseExited(MouseEvent e) {
					if (tab!=selectedTab) text.setForeground(NNUEdit.MenuFG);
					else text.setForeground(selectedColor);
					text.setFont(f);
				}
			});
			if (tab==selectedTab) {
				text.setForeground(selectedColor);
				if (j) {
					text.setFont(new Font(AOption.FT, Font.BOLD, f.getSize()+5));
					text.setForeground(hoverColor);
				}
			}
			add(text);
		}
		if (selectedTab!=null) {
			JComponent jc = selectedTab.getComponent(getWidth());
			this.setPreferredSize(new Dimension(900,jc.getHeight()+BUTTONS_HEIGHT));
			add(jc);
		}
		revalidate();
		repaint();
	}
}

package io.github.ngspace.nnuedit.window;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.asset_manager.AssetManager;
import io.github.ngspace.nnuedit.asset_manager.StringTable;
import io.github.ngspace.nnuedit.menu.components.NGSScrollPane;
import io.github.ngspace.nnuedit.menu.components.Tab;
import io.github.ngspace.nnuedit.window.abstractions.Window;
import io.github.ngspace.nnuedit.window.abstractions.WindowMenu;

public class AboutWindow extends WindowMenu implements Window {

	private static final long serialVersionUID = 6893937793741839803L;
	public static final String GITHUBURL = "https://github.com/NGSpace/NNUEdit";
	public final NNUEdit app;

	protected NGSScrollPane sp;
	protected Tab tab;

	public AboutWindow(NNUEdit app) {
        this.app = app;
		this.sp = new NGSScrollPane(app);
		
		setLayout(null);
        this.setBackground(Window.color);
        this.setForeground(Color.LIGHT_GRAY);
		
		JLabel lblNewLabel = new JLabel(Main.EDITORNAME);
		lblNewLabel.setForeground(Color.LIGHT_GRAY);
		lblNewLabel.setFont(new Font("Monospaced", Font.BOLD, 48));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(10, 11, 264, 66);
		add(lblNewLabel);
		
		JLabel lblByNgspace = new JLabel("NGSpace");
		lblByNgspace.setForeground(Color.red);
		lblByNgspace.setHorizontalAlignment(SwingConstants.CENTER);
		lblByNgspace.setFont(new Font("Monospaced", Font.BOLD, 36));
		lblByNgspace.setBounds(10, 79, 264, 37);
		add(lblByNgspace);

		JLabel lblGithubPage = new JLabel(StringTable.get("about.github"));
		lblGithubPage.setHorizontalAlignment(SwingConstants.CENTER);
		lblGithubPage.setForeground(Color.blue.brighter());
		lblGithubPage.setFont(new Font("Courier New", Font.BOLD, 24));
		lblGithubPage.setBounds(10, 127, 264, 25);
		lblGithubPage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblGithubPage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                	Desktop.getDesktop().browse(new URI(GITHUBURL));
                } catch (Exception ex) {
                	ex.printStackTrace();
                }
            }
        });
		add(lblGithubPage);
		JLabel lblVersion = new JLabel(StringTable.get("about.version",Main.VersionInfo.version));
		lblVersion.setForeground(Color.LIGHT_GRAY);
		lblVersion.setHorizontalAlignment(SwingConstants.LEFT);
		lblVersion.setFont(new Font("Courier New", Font.BOLD, 24));
		lblVersion.setBounds(10, 187, 464, 50);
		add(lblVersion);
		JLabel lblSystem = new JLabel(System.getProperty("os.name"));
		lblSystem.setForeground(Color.LIGHT_GRAY);
		lblSystem.setHorizontalAlignment(SwingConstants.CENTER);
		lblSystem.setFont(new Font("Courier New", Font.BOLD, 24));
		lblSystem.setBounds(10, 237, 264, 50);
		add(lblSystem);
		
		sp.getViewport().setView(this);
        this.tab = new Tab(app, this);
        tab.setIcon(AssetManager.getIcon("NNUEdit72x72"));
        
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(25,0));
        sp.getHorizontalScrollBar().setLocation(sp.getWidth() - 25, 0);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(25,100));
        sp.getVerticalScrollBar().setLocation(sp.getWidth() - 25, 0);
        
        sp.setOpaque(true);
	}

	@Override public String getTitle() {return StringTable.get("about.title", Main.EDITORNAME);}
	@Override public NGSScrollPane getScrollPane() {return sp;}
	@Override public JComponent getComponent() {return this;}
	@Override public Tab getTab() {return tab;}
	@Override public NNUEdit getApp() {return app;}
}

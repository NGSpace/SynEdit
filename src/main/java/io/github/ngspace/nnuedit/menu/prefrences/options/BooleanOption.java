package io.github.ngspace.nnuedit.menu.prefrences.options;

import static io.github.ngspace.nnuedit.asset_manager.StringTable.get;

import java.awt.Color;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.SwingConstants;

import io.github.ngspace.nnuedit.Main;

public class BooleanOption extends WidgetOption {
	public BooleanOption(String name, String key) {super(name, key);}
	@Override public JComponent getWidget(int width) {
		JCheckBox jls = new JCheckBox();
		ItemListener listener = _ -> Main.settings.set(key, jls.isSelected());
        jls.setBackground(new Color(10,10,12));
        jls.setSelected(Boolean.valueOf(Main.settings.get(key).toLowerCase()));
        jls.addItemListener(listener);
		jls.setSize(width/2, panel.getHeight());
		jls.setLocation(width/2, 10);
		jls.setOpaque(false);
		jls.setText(jls.isSelected()?get("options.enabled"):get("options.disabled"));
		jls.setFont(f);
		jls.setVerticalAlignment(SwingConstants.TOP);
		return jls;
	}
}
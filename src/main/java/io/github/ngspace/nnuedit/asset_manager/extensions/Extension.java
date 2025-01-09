package io.github.ngspace.nnuedit.asset_manager.extensions;

import javax.swing.JComponent;

import io.github.ngspace.nnuedit.NNUEdit;

public abstract class Extension {protected Extension() {}
	/**
	 * Will be executed After Settings and StringTable loaded
	 */
	public void loadExtension() {}
	/**
	 * Will be executed when the jvm closes
	 */
	public void unloadExtension() {}
	/**
	 * Ran when App starts Loading
	 * @param app
	 */
	public void preLoadApplication(NNUEdit app) {}
	/**
	 * Ran after App finished Loading
	 * @param app
	 */
	public abstract void loadApplication(NNUEdit app);
	/**
	 * Ran when app is closed
	 * @param app
	 */
	public abstract void unloadApplication(NNUEdit app);
	/**
	 * will build the component that will show up in the Extension tab to represent the extension
	 * @param width - the width of the window
	 * @return the built component
	 */
	public JComponent getOptionsComponent(int width) {return new BasicExtensionPanel(getExtensionValues(),width);}
	/**
	 * Searches <code>ExtensionManager.Extensions</code> for the <code>ExtensionValues</code> of this extension.
	 * @return said ExtensionValues
	 */
	public ExtensionValues getExtensionValues() {
		for (ExtensionValues ext : ExtensionManager.Extensions) if (ext.extension==this) return ext;
		return null;
	}
}

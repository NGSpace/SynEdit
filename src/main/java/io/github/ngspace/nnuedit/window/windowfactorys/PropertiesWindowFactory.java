package io.github.ngspace.nnuedit.window.windowfactorys;

import java.io.File;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.window.PropertiesWindow;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class PropertiesWindowFactory implements IWindowFactory {
	@Override public Window createWindowFromFile(NNUEdit app, File file) {return new PropertiesWindow(app, file);}
}

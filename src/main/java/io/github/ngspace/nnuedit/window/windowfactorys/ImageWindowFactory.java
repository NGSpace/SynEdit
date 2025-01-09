package io.github.ngspace.nnuedit.window.windowfactorys;

import java.io.File;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.window.ImageWindow;
import io.github.ngspace.nnuedit.window.abstractions.Window;

public class ImageWindowFactory implements IWindowFactory {
	@Override public Window createWindowFromFile(NNUEdit app, File file) {return new ImageWindow(app, file);}
}

package io.github.ngspace.nnuedit.runner;

import java.io.File;
import java.io.IOException;

import io.github.ngspace.nnuedit.NNUEdit;

public interface IRunner {
	public boolean canRun(NNUEdit app);
	public void run(NNUEdit app);
	public boolean canRunFile(File f, NNUEdit app);
	public void runFile(File f, NNUEdit app) throws IOException;
	
	public default boolean validFileExt(String[] fileext, String s) {
		if (s==null) return false;
		for (String fex : fileext)
			if (s.endsWith(fex))
				return true;
		return false;
	}
	public default String containsFiles(String[] starterfiles, NNUEdit app) {
		for (String fex : starterfiles)
			if (app.Folder.contains(fex.toLowerCase(), true))
				return fex;
		return null;
	}
	public default String slashify(String ts) {
		return ts.replace('\\', '/');
	}
}
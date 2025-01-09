package io.github.ngspace.nnuedit.utils;

import static java.lang.System.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.swing.JFileChooser;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.mozilla.universalchardet.UniversalDetector;

import io.github.ngspace.nnuedit.NNUEdit;
import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.utils.desktop.DesktopApi;
import io.github.ngspace.nnuedit.utils.user_io.UserMessager;

public class FileIO {
	private FileIO() {}
	
	public static Map<String, String> filetypes = new HashMap<String, String>();
	public static String[] imagetypes = {"png","jpg", "jpeg","bmp","wbmp","gif","tiff","tif","webp","ico"};
	static {
		for (String fex : imagetypes) filetypes.put(fex, "img");
		filetypes.put("mp3","audio");
		filetypes.put("mp4","video");
	}
	
	
	
	public static String getFileExt(File path) {return getFileExt(path.getAbsolutePath());}

	
	public static String getFileExt(String path) {
		if (path==null) return "";
        String[] f = path.replace('\\', '/').split("/");
        String[] fi = f[f.length-1].split("[.]");
		return fi[fi.length-1];
	}
	
	
	
	public static String getFileNameWOExt(String file) {
		if (file==null) return "";
		String name = getFileName(file);
		int index = name.lastIndexOf('.');
		if (index==-1) return name;
		return name.substring(0,index);
	}
	
	
	
	public static String getFileName(String file) {
		return new File(file).getName();
	}
	
	
	
	public static String getFileType(String path) {
		if (path==null) return "";
		String res = FileIO.filetypes.get(FileIO.getFileExt(path).toLowerCase().trim());
		if (res==null) return "";
		return res;
	}
	
	
	
	/**
	 * saves text to the path specified
	 * @param path the path
	 * @param text the text
	 * @return whether the operation was successful
	 * @throws IOException throws IOException when FUCK
	 */
	public static void save(File path, String text) throws IOException {
		FileWriter fw = new FileWriter(path);
		fw.write(text);
		fw.close();
	}
	
	
	
	/**
	 * reads a file and returns it's contents in String form.
	 * @param path The path to file to read from.
	 * @return the contents of said file.
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static String read(File file) throws IOException {
		try (FileInputStream is = new FileInputStream(file)) {
			byte[] bytes = is.readAllBytes();
			return new String(bytes, Charset.forName(guessEncoding(bytes)));
		}
	}
	
	
	
	public static String guessEncoding(byte[] bytes) {
	    String DEFAULT_ENCODING = "Cp852";
	    UniversalDetector detector = new UniversalDetector(null);
	    detector.handleData(bytes, 0, bytes.length);
	    detector.dataEnd();
	    String encoding = detector.getDetectedCharset();
	    detector.reset();
	    if (encoding == null) encoding = DEFAULT_ENCODING;
	    return encoding;
	}
	
	
	
	/**
	 * Deletes folder and all subfolders.
	 * @param path
	 * @throws IOException
	 */
	public static void recursiveDelete(Path path) throws IOException {
		if (path.toFile().isDirectory()) {
			try (Stream<Path> r = Files.walk(path)) {
	            r.sorted(Comparator.reverseOrder())
	            .forEach(e -> {
	                try {System.out.println("Deleting: " + e);Files.delete(e);
	                } catch (IOException ex) {ex.printStackTrace();}
	            });
			}
		} else Files.delete(path);
	}
	
	


	/**
	 * returns the location of the jar
	 * @return the location of the jar
	 * @throws UnsupportedEncodingException
	 */
	public static String getProgramPath() throws UnsupportedEncodingException {
		URL url = NNUEdit.class.getProtectionDomain().getCodeSource().getLocation();
		String jarPath = URLDecoder.decode(url.getFile(), "UTF-8");
		return new File(jarPath).getParentFile().getPath();
	}
	
	

	/**
	 * returns the location of the jar
	 * @return the location of the jar
	 * @throws UnsupportedEncodingException
	 */
	public static String getConfigFolderPath() {
		try {
			if (Main.VersionInfo.isWindows())
				return getProgramPath() + "\\" + Main.EFFECTIVE_EDITORNAME + "\\";
			else return System.getProperty("user.home")+"/.config/" + Main.EFFECTIVE_EDITORNAME + "/";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			//in a previous version, it tried to modify the root folder...
			return "./" + Main.EFFECTIVE_EDITORNAME + "/";
		}
	}
	
	
	
	/**
	 * opens the file dialog
	 * @param save whether to show a save button or open button
	 * @return the selected file (returns null otherwise)
	 */
	public static String openFileDialog(boolean save) {
		String res = null;
		if (Main.VersionInfo.isSWTSupported()) {
			Display display = new Display ();
	        Shell shell = new Shell (display);
	        
	        FileDialog dialog = new FileDialog(shell, save ? SWT.SAVE : SWT.OPEN);
	        
	        dialog.open();
	        res = dialog.getFilterPath() + "/" + dialog.getFileName();
	        shell.close();
	        display.close();
		} else {
			out.println("Universal system detected! Using universal file chooser");
	        JFileChooser chooser = new JFileChooser();
	        chooser.setDialogTitle("Specify a file to " + (save ? "Save" : "Open"));  
	        int returnVal = save ? chooser.showSaveDialog(null) : chooser.showOpenDialog(null);
	        if (returnVal!=0) return null;
			try {res = chooser.getSelectedFile().getCanonicalPath();
			} catch (IOException e1) {e1.printStackTrace();}
		}
		return res != null&&!"/".equals(res.trim()) && !res.trim().isEmpty() ? res : null;
	}
	
	
	
	/**
	 * opens the folder dialog
	 * @return the selected folder (returns null otherwise)
	 */
	public static String openFolderDialog() {
		String res = "";
		if (Main.VersionInfo.isSWTSupported()) {
		    Display display = new Display();
		    Shell shell = new Shell(display);
		    DirectoryDialog dialog = new DirectoryDialog(shell);
		    res = dialog.open();
		    display.dispose();
		} else {
			out.println("Opening folder dialog.");
	        JFileChooser chooser = new JFileChooser();
	        chooser.setDialogTitle("Specify a folder to open");  
	        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	        int returnVal = chooser.showOpenDialog(null);
	        if (returnVal!=0) return null;
			try {res = chooser.getSelectedFile().getCanonicalPath();
			} catch (IOException e) {e.printStackTrace();}
		}
		return res != null&&!"/".equals(res.trim()) && !res.trim().isEmpty() ? res : null;
	}

	

	public static void openInExplorer(File file) {
		try {
			DesktopApi.browse(file.toURI());
		} catch (Exception e) {
			e.printStackTrace();
			UserMessager.showErrorDialogTB("err.openinexplorer.title", "err.openinexplorer", e.getMessage());
		}
	}
}

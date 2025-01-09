package io.github.ngspace.nnuedit.utils.desktop;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DesktopApi {private DesktopApi() {}

    public static boolean browse(URI uri) {
        return openSystemSpecific(uri.toString())||browseDESKTOP(uri);
    }


    public static boolean open(File file) {
        return openSystemSpecific(file.getPath())||openDESKTOP(file);
    }


    public static boolean edit(File file) {
        return openSystemSpecific(file.getPath())||editDESKTOP(file);
    }


    private static boolean openSystemSpecific(String what) {
        EnumOS os = getOs();
        if (os.isUnix()) {
            if (runCommand("xdg-open", what)) return true;
            if (runCommand("kde-open",what)) return true;
            if (runCommand("gnome-open", what)) return true;
        }

        return os.isMac()&&runCommand("open", what)
            	||os.isWindows()&&runCommand("explorer", what);
    }


    private static boolean browseDESKTOP(URI uri) {
        try {
            if (!Desktop.isDesktopSupported()) {
                return false;
            }
            if (!Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                return false;
            }
            Desktop.getDesktop().browse(uri);
            return true;
        } catch (Exception t) {
            return false;
        }
    }


    private static boolean openDESKTOP(File file) {
        try {
            if (!Desktop.isDesktopSupported()) return false;
            if (!Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) return false;

            Desktop.getDesktop().open(file);
            return true;
        } catch (Exception t) {
            return false;
        }
    }


    private static boolean editDESKTOP(File file) {
        try {
            if (!Desktop.isDesktopSupported()) return false;
            if (!Desktop.getDesktop().isSupported(Desktop.Action.EDIT)) return false;
            
            Desktop.getDesktop().edit(file);
            return true;
        } catch (Exception t) {
            return false;
        }
    }


    private static boolean runCommand(String command, String file) {
        String[] parts = prepareCommand(command, file);
        
        try {
            Process p = Runtime.getRuntime().exec(parts);
            if (p == null) return false;

            try {
                int retval = p.exitValue();
                return retval == 0;
            } catch (IllegalThreadStateException itse) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
    }


    private static String[] prepareCommand(String command, String file) {

        List<String> parts = new ArrayList<String>();
        parts.add(command);
        parts.add(file);

        return parts.toArray(new String[parts.size()]);
    }

    public enum EnumOS {
        UNIX, MACOS, UNKNOWN, WINDOWS;
        public boolean isUnix   () {return this == UNIX;}
        public boolean isMac    () {return this == MACOS;}
        public boolean isWindows() {return this == WINDOWS;}
    }


    public static EnumOS getOs() {

        String s = System.getProperty("os.name").toLowerCase();

        if (s.contains("linux"  )) return EnumOS.UNIX;// The superior operating system.
        if (s.contains("bsd"    )) return EnumOS.UNIX;
        if (s.contains("win"    )) return EnumOS.WINDOWS;
        if (s.contains("mac"    )) return EnumOS.MACOS;
        if (s.contains("unix"   )) return EnumOS.UNIX;
        
        return EnumOS.UNKNOWN;
    }
}
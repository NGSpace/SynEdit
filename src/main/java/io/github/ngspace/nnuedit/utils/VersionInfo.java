package io.github.ngspace.nnuedit.utils;

import io.github.ngspace.nnuedit.Main;
import io.github.ngspace.nnuedit.utils.settings.Settings;

public class VersionInfo {
	private Settings info = new Settings(Utils.getAsset("NNUEditVersion.properties"));
	public double versionnumber;
	private String platform;
	public String version;
	public VersionInfo() {
		versionnumber = info.getDouble("VersionNumber");
		platform = info.get("Platform");
		version = info.get("Version");
	}
	public boolean isUnix() {
		return "linux".equals(Main.VersionInfo.platform); //
	}
	public boolean isUniversal() {
		return "universal".equals(Main.VersionInfo.platform);
	}
	public boolean isMacos() {
		return "macos".equals(Main.VersionInfo.platform);
	}
	public boolean isWindows() {
		return "universal".equals(Main.VersionInfo.platform);
	}
	public boolean isSWTSupported() {
		return !Main.VersionInfo.isUniversal();
	}
}

cd buildresults


rm -rf NNUEdit-portable-linux
rm -rf NNUEdit-portable-windows

echo
ver=1.0.0
#Read files somehow idfk
echo NNUEdit Version: $ver

upuuid=26e31545-6190-4265-abf6-e6d32b195f10
# Read ../../NNUEdit-Upgrade-uuid.txt to up-uuid
echo Windows upgrade uuid: $upuuid
cd buildresults
whereami

echo
echo Creating win and linux app images
mkdir win
mkdir win/nnuedit
cp windows.jar win/windows.jar
mkdir linux
mkdir linux/nnuedit
cp linux.jar linux/linux.jar



#WINDOWS
echo
echo Building Windows Packages

# MSI Installer
echo Building Windows MSI Installer

ls

wine ../winjdk/bin/jpackage.exe --vendor ngspace --app-version $ver --copyright ngspace --description "the NNUEdit code editor"\
 --icon ../NNUEdit.ico --name NNUEdit\
 --type msi -i win --main-jar windows.jar\
 --win-dir-chooser --win-help-url https://github.com/NGSpace/NNUEdit/issues --win-menu --win-menu-group NNU\
 --win-shortcut-prompt --win-update-url https://github.com/NGSpace/NNUEdit/releases --win-upgrade-uuid $upuuid\
 --win-per-user-install

# Portable
echo Building Windows Portable

jpackage --vendor ngspace --app-version $ver --copyright ngspace --description "the NNUEdit code editor"\
 --icon ../NNUEdit.ico --name NNUEdit-portable-windows\
 --type app-image -i win --main-jar windows.jar




#LINUX
echo
echo Building Linux Packages

# Portable
echo Building Linux Portable

jpackage --vendor ngspace --app-version $ver --copyright ngspace --description "the NNUEdit code editor"\
 --icon ../NNUEdit.png --name NNUEdit-portable-linux\
 --type app-image -i linux --main-jar linux.jar

# DEB package
echo Building DEB Package

jpackage --vendor ngspace --app-version $ver --copyright ngspace --description "the NNUEdit code editor"\
 --icon ../NNUEdit.png --name NNUEdit\
 --type deb -i linux --main-jar linux.jar\
 --linux-package-name nnuedit --linux-deb-maintainer ngspace --linux-menu-group NNUEdit --linux-shortcut\
 --linux-app-release $ver --linux-app-category "NNUEdit"

# RPM package
echo Building RPM Package

jpackage --vendor ngspace --app-version $ver --copyright ngspace --description "the NNUEdit code editor"\
 --icon ../NNUEdit.png --name NNUEdit\
 --type rpm -i linux --main-jar linux.jar\
 --linux-package-name nnuedit --linux-menu-group NNUEdit --linux-shortcut\
 --linux-app-release $ver --linux-app-category NNUEdit



::CLEANUP
echo:
echo cleaning up
rm -rf win
rm -rf linux

cd ..

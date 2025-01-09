@echo off

set package=false
set test=false
set compile=true

FOR %%A IN (%*) DO (
	if "%%A"=="--no-compile" (
		echo will not compile
		set compile=false
	)
	if "%%A"=="--test" (
		echo will test
		set test=true
	)
	if "%%A"=="--package" (
		echo will package
		set package=true
	)
)

if %compile%==true (
	rmdir /s /q jars
	rmdir /s /q buildresults
	mkdir jars
	mkdir buildresults
	
	echo:
	echo Compiling
	call compile.cmd

	echo Copying jars to results folder
	xcopy /s jars buildresults
)

if %package%==true (
	echo:
	echo Packaging
	call package.cmd
)

if %test%==true (
	echo:
	echo Testing
	call test.cmd
)

pause
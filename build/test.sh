cd buildresults
echo:
echo Testing windows.jar
java -cp windows.jar io.github.ngspace.nnuedit.utils.testing.TestExecuter
echo Finished testing windows.jar
pause

echo Testing universal.jar
java -cp universal.jar io.github.ngspace.nnuedit.utils.testing.TestExecuter
echo Finished testing universal.jar
pause

echo Testing linux.jar
wsl java -cp linux.jar io.github.ngspace.nnuedit.utils.testing.TestExecuter
echo Finished testing linux.jar

rmdir /s /q NNUEdit

cd ..
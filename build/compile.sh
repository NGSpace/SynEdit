#Go out to the main directory
cd ..




echo
echo Building windows JAR

mvn clean compile assembly:single -P windows
mv target/NNUEdit.jar build/jars/windows.jar

echo Finished building windows JAR



echo Building linux JAR

mvn clean compile assembly:single -P linux
mv target/NNUEdit.jar build/jars/linux.jar

echo Finished building linux JAR



echo Building macos JAR

mvn clean compile assembly:single -P macos
mv target/NNUEdit.jar build/jars/macos.jar

echo Finished building macos JAR



echo Building universal JAR

mvn clean compile assembly:single -P universal
mv target/NNUEdit.jar build/jars/universal.jar

echo Finished building universal JAR




#Go back
cd build
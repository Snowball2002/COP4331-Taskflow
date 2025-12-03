@echo off
echo Building TaskFlow JAR...

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Compile all Java files
echo Compiling Java files...
javac -d bin -sourcepath src -encoding UTF-8 src/cop4331/taskflow/*.java src/cop4331/taskflow/**/*.java

if errorlevel 1 (
    echo Compilation failed!
    pause
    exit /b 1
)

REM Create manifest file
echo Creating manifest...
echo Main-Class: cop4331.taskflow.TaskFlowApp > manifest.txt

REM Create JAR file
echo Creating JAR file...
jar cvfm bin/TaskFlow.jar manifest.txt -C bin cop4331

REM Clean up manifest
del manifest.txt

echo.
echo Build complete! JAR file created at: bin/TaskFlow.jar
echo.
echo To run the application:
echo   java -jar bin/TaskFlow.jar
echo.
pause


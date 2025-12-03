@echo off
echo ========================================
echo TaskFlow Application Launcher
echo ========================================
echo.

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 8 or higher
    pause
    exit /b 1
)

REM Check if org.json library exists
if not exist "lib\json-20231018.jar" (
    echo WARNING: org.json library not found!
    echo.
    echo Please download json-20231018.jar from:
    echo https://mvnrepository.com/artifact/org.json/json
    echo.
    echo Or use this direct link:
    echo https://search.maven.org/remote_content?g=org.json^&a=json^&v=20231018
    echo.
    echo Place the downloaded JAR file in the lib\ directory.
    echo.
    pause
    exit /b 1
)

REM Check if classes are compiled
if not exist "bin\cop4331\taskflow\TaskFlowApp.class" (
    echo Compiling source files...
    echo.
    
    REM Try to find javac
    where javac >nul 2>&1
    if errorlevel 1 (
        echo ERROR: javac (Java compiler) not found in PATH
        echo.
        echo Please either:
        echo 1. Install Java JDK (not just JRE)
        echo 2. Add JDK bin directory to your PATH
        echo 3. Use an IDE like IntelliJ IDEA or Eclipse to compile
        echo.
        pause
        exit /b 1
    )
    
    javac -cp "lib\json-20231018.jar" -d bin -sourcepath src -encoding UTF-8 src\cop4331\taskflow\*.java src\cop4331\taskflow\**\*.java
    
    if errorlevel 1 (
        echo.
        echo Compilation failed! Please check the errors above.
        pause
        exit /b 1
    )
    
    echo Compilation successful!
    echo.
)

echo Starting TaskFlow application...
echo.

java -cp "bin;lib\json-20231018.jar" cop4331.taskflow.TaskFlowApp

if errorlevel 1 (
    echo.
    echo Application failed to start. Please check the errors above.
    pause
    exit /b 1
)

pause


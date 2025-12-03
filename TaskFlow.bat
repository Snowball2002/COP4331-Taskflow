@echo off
REM TaskFlow Application Launcher
REM Double-click this file to run TaskFlow

REM Find the project directory (where bin and lib folders are)
REM Try current directory first, then common locations
set PROJECT_DIR=%~dp0
if not exist "%PROJECT_DIR%bin" (
    set PROJECT_DIR=E:\Github\taskflow\
)
if not exist "%PROJECT_DIR%bin" (
    echo Error: Could not find TaskFlow project directory.
    echo Please make sure you're running this from the project folder.
    pause
    exit /b 1
)

REM Change to project directory
cd /d "%PROJECT_DIR%"

echo Starting TaskFlow...
echo.

REM Run the application using the Java runtime from VS Code extension
C:\Users\paolo\.vscode\extensions\redhat.java-1.50.0-win32-x64\jre\21.0.9-win32-x86_64\bin\java.exe -cp "bin;lib\json-20231018.jar" cop4331.taskflow.TaskFlowApp

REM If the application exits, pause so we can see any error messages
if errorlevel 1 (
    echo.
    echo Application exited with an error.
    pause
)


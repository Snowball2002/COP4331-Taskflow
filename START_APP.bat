@echo off
echo Starting TaskFlow Application...
echo.

REM Try to find and use Java
java -cp "bin;lib\json-20231018.jar" cop4331.taskflow.TaskFlowApp 2>nul

if errorlevel 1 (
    echo.
    echo Application not compiled yet or Java not found.
    echo.
    echo Please run from Cursor:
    echo 1. Open: src\cop4331\taskflow\TaskFlowApp.java
    echo 2. Press F5
    echo.
    pause
)


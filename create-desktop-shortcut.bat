@echo off
echo Creating desktop shortcut for TaskFlow...

REM Get the current directory
set SCRIPT_DIR=%~dp0
set DESKTOP=%USERPROFILE%\Desktop

REM Create a shortcut on the desktop
powershell -Command "$WshShell = New-Object -ComObject WScript.Shell; $Shortcut = $WshShell.CreateShortcut('%DESKTOP%\TaskFlow.lnk'); $Shortcut.TargetPath = '%SCRIPT_DIR%TaskFlow.bat'; $Shortcut.WorkingDirectory = '%SCRIPT_DIR%'; $Shortcut.Description = 'TaskFlow Task Management Application'; $Shortcut.IconLocation = 'C:\Users\paolo\.vscode\extensions\redhat.java-1.50.0-win32-x64\jre\21.0.9-win32-x86_64\bin\java.exe,0'; $Shortcut.Save()"

echo.
echo Desktop shortcut created!
echo You can now double-click "TaskFlow" on your desktop to run the application.
echo.
pause


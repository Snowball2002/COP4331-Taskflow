TaskFlow - Task Management Application
======================================

Author: Paolo Lauricella
Course: COP4331 - Object-Oriented Programming


DESCRIPTION
-----------
TaskFlow is a desktop task management application built with Java Swing that helps 
users manage their daily tasks efficiently. The application provides comprehensive 
task management features including task creation, editing, completion, reminders, 
persistence, and analytics.

FEATURES
--------
- Task creation, editing, deletion, and completion
- Task priorities (LOW, MEDIUM, HIGH)
- Task statuses (PENDING, COMPLETED, TRASHED)
- Due date tracking
- Reminder notifications
- Task analytics and statistics
- Light/Dark theme toggle
- Trash view with restore functionality
- Undo/Redo operations
- Auto-save persistence (JSON format)
- Sorting by due date or priority

DESIGN PATTERNS
---------------
The application demonstrates the following design patterns:
- MVC (Model-View-Controller) Architecture
- Command Pattern (with undo/redo)
- Observer Pattern
- Strategy Pattern (for sorting)
- Factory Pattern
- Singleton Pattern

REQUIREMENTS
------------
- Java JDK 8 or higher
- org.json library (json-20231018.jar or later)

HOW TO RUN THE APPLICATION
---------------------------

Method 1: Using Java Runtime (Recommended)
-------------------------------------------
1. Ensure the application is compiled (class files in bin/ directory)
2. Ensure org.json library is in lib/ directory (json-20231018.jar)
3. Open a terminal/command prompt
4. Navigate to the project root directory
5. Run the following command:

   C:\Users\paolo\.vscode\extensions\redhat.java-1.50.0-win32-x64\jre\21.0.9-win32-x86_64\bin\java.exe -cp "bin;lib\json-20231018.jar" cop4331.taskflow.TaskFlowApp

   Note: If you have Java in your PATH, you can use:
   java -cp "bin;lib\json-20231018.jar" cop4331.taskflow.TaskFlowApp

Method 2: Using JAR File
------------------------
1. Build the JAR file using: build-jar.bat (Windows) or ./build-jar.sh (Linux/Mac)
2. Run the JAR file:
   java -jar bin/TaskFlow.jar

Method 3: Using an IDE
----------------------
1. Open the project in IntelliJ IDEA, Eclipse, or VS Code
2. Add lib/json-20231018.jar to the project classpath
3. Run TaskFlowApp.java

COMPILATION
-----------
To compile the source code:

Windows:
  javac -cp "lib\json-20231018.jar" -d bin -sourcepath src -encoding UTF-8 src\cop4331\taskflow\*.java src\cop4331\taskflow\**\*.java

Linux/Mac:
  javac -cp "lib/json-20231018.jar" -d bin -sourcepath src -encoding UTF-8 src/cop4331/taskflow/*.java src/cop4331/taskflow/**/*.java

BUILD SCRIPTS
-------------
- build-jar.bat / build-jar.sh - Creates executable JAR file
- build-javadoc.bat / build-javadoc.sh - Generates Javadoc HTML documentation

PROJECT STRUCTURE
-----------------
TaskFlow/
├── src/              - Java source code
├── tests/            - Unit tests
├── bin/              - Compiled class files and JAR
├── lib/              - External libraries (org.json)
├── javadocs/         - Generated Javadoc HTML
├── docs/             - Documentation files
└── readme.txt        - This file

DATA PERSISTENCE
----------------
Tasks are automatically saved to taskflow_data.json in the project root directory.
The application loads this file on startup and saves changes automatically.

USAGE
-----
1. Launch the application using one of the methods above
2. Click "Add" to create a new task
3. Fill in task details (title, description, due date, priority)
4. Click "OK" to save the task
5. Use toolbar buttons to edit, delete, or complete tasks
6. Use "Undo" and "Redo" buttons to reverse or reapply actions
7. Use "Sort" dropdown to change sorting strategy
8. Access "View" menu for theme toggle and analytics
9. Use "View Trash" to see deleted tasks and restore them

TROUBLESHOOTING
---------------
- "ClassNotFoundException": Ensure org.json library is in lib/ directory
- "UnsupportedClassVersionError": Use Java 8 or higher
- Application won't start: Check that all class files are compiled in bin/
- Date not displaying: Enter date in format: yyyy-MM-dd HH:mm (e.g., 2024-12-25 14:30)

For more information, see the documentation in the docs/ folder.


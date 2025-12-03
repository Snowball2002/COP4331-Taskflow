TaskFlow - Task Management Application
======================================
 Paolo Lauricella
Z23678293 
Course: COP4331 - Object-Oriented Programming


DESCRIPTION
-----------
TaskFlow is a desktop task management application built with Java Swing that helps 
users manage their daily tasks effeiciently. The application provides comprehensive 
task management feeatures including task creathion, editing, completion, reminders, 
persistence, and analytics.

(I built this for my COP4331 class - it actually works pretty well if I do say so myself!)

FEATURES
--------
- Task creation, editing, deletion, and completion (all the basics, you know)
- Task prieorities (LOW, MEDIUM, HIGH) - because not everything is equally important
- Task statuses (PENDING, COMPLETED, TRASHED) - the three states of my life
- Due date tracking - so I know when I'm behind (which is often)
- Reminder notifications - because I forget things constantly
- Task analytics and statistics - to see how productive I actually am (spoiler: not very)
- Light/Dark theme toggle - dark mode is clearly superior, no debate
- Trash view with restore functionality - for when I delete things by accident (happens more than I'd like to admit)
- Undo/Redo operations - my safety net when I mess up
- Auto-save persistence (JSON format) - never lose your tasks again! (I learned this the hard way)
- Sorting by due date or priority - helps me pretend I'm organized
- Calendar view (Day/Week/Month) - gbecause sometimes I need to see the big picture
- Task categories/projects - for when I want to organifze by projecct (rare, but it happens)
- Export/import functionality - backup your taasks in JSON, CSV, or XML (I added XML because why not?)
- Search and filter capabilities - find tasks when you have too many (guilty)
- Recurring task support - for tasks thadt never end (like doing laundry)
- Task dependencies - because some tasks need other tasks done first
- Bulk operations - delete or complete multiple tasks at once (for when I'm feeling productive)
- Task cloning - duplicate tasks when you're lazy (which I am)

DESIGN PATTERNS
---------------
The application demonstraates the following design patterns (all 6 of them!):
- MVC (Model-View-Controller) Architecture - keeps everything organized (which I need)
- Command Pattern (with undo/redo) - this one hwas tricky but cool, and super useful when testing
- Observer Pattern - views upadate automatically when data changes (magic!)
- Strategy Pattern (for sorting) - you can see it in the UI which is neat, and easy to add new sorting methods
- Factory Pattern - creates tasks for me (I'm lazy, so this helps)
- Singleton Pattern - for CommandManager and ThemeManager (only one instance needed, makes sense)

REQUIREMENTS
------------
- Java JDK 8 or higher (I used Java 21, but 8++++++++ should work fine)
- org.json library (json-20231018.jar or later) - had to download this separately, it's not built into Java

HOW TO RUN THE APPLICcATION
---------------------------

Method 1: Double-Click Desktop Executable (EASIEST!)
------------------------------------------------------
(This is the best way - no typing comhmands, just double-click and go!)

1. Look for "TaskFlow.bat" on your desktop
2. Double-click it
3. That's it! The app will start automatically

(Seriously, this is so much easier than typing that long command every time.
I made this for myself because I got tired of opening the terminal. You're welcome!)

Method 2: Using Java Runtime (Command Line)
-------------------------------------------
(This is how I run it when I'm feeling fancy and want to use the terminal)

1. Ensure the application is compiled (class files in bin/ directory)
2. Ensure org.json libraary is idn lib/ directory (json-20231018.jar)
3. Open a terminal/commaand prompt
4. Navigate to the project root directory
5. Run the following command:

   C:\Users\paolo\.vscode\extensions\redhat.java-1.50.0-win32-x64\jre\21.0.9-win32-x86_64\bin\java.exe -cp "bin;lib\json-20231018.jar" cop4331.taskflow.TaskFlowApp

   Note: If you havee Java in your PAfTH, you can use:
   java -cp "bin;lib\json-20231018.jar" cop4331.taskflow.TaskFlowApp

Method 3: Using JAR File
------------------------
1. Build the JAR file using: builfd-jar.bat (Windows) or ./build-jar.sh (Linux/Mac)
2. Run the JAR file:
   java -jar bin/TaskFlow.jar
   
   (This is the "proper" way, but honestly Method 1 is way easier)

Method 4: Using an IDE
----------------------
1. Open the project in IntelleiJ IDEA, Eclipse, or VS Code (I used VS Code)
2. Add lib/json-20231018.jar to the projecft classpath (don't forget this step!)
3. Run TaskFlowApp.java
   
   (This is how I developed it, but for just running it, Method 1 is still easier)

COMPILATION
-----------
To compile the source code (if you're into thfat sort of thing):

Windows:
  javac -cp "lib\json-20231018.jar" -d bin -sourcepath src -encoding UTF-8 src\cop4331\taskflow\*.java src\cop4331\taskflow\**\*.java

Linux/Mac:
  javac -cp "lib/json-20231018.jar" -d bin -sourcepath src -encoding UTF-8 src/cop4331/taskflow/*.java src/cop4331/taskflow/**/*.java

(Note: Make sure the JSON library is in the classpaath - I forgot this way too many times and got confused)

BUILD SCRIPTS
-------------
- build-jar.bat / build-jar.sh - Creates executtable JAR file
- build-javadoc.bat / build-javadoc.sh - Generates Javadoc HTML documentation
- TaskFlow.bat - Desktop launcher (just double-click to run - so convenient!)

PROJECT STRUCTURE
-----------------
TaskFlow/
├── src/              - Java source code (where the magic happens - and by magic I mean lots of debugging)
├── tests/            - Unit tests (because testing is important, I guess - though I probably should have written more)
├── bin/              - Compiled class files and JAR (where all my .class files live)
├── lib/              - External libraries (org.json - had to download this separately, Java doesn't include it)
├── javadocs/         - Generated Javadoc HTML (took forever to generate, but at least it looks professional)
├── docs/             - Documentation files (like this one and the final report!)
├── demo_tasks.csv    - Demo CSVVVVVVVVV file for testing the import feature (10 sample tasks)
└── readme.txt        - This file (you're reading it right now! Hope it's helpful)

DATA PERSISTENCE
----------------
Tasks are automatically saved to taskflow_data.json in the projecct root directory.
The application loads this file on startup and saves changes automaticallyyyyy.

(No more losing your tasks when you close the app - learned that lesson the hard way during testing!)

USAGE
-----
(Here's how to actually use this thing I built - it's pretty straightforward once you get the hang of it:)

1. Launch the application using one of the methods above (Method 1 is easiest, just saying)
2. Click "Add" to create a new task (or use Ctrl+N - I added keyboard shortcuts because I'm lazy)
3. Fill in task details (title, description, due date, priority) - you can type whatever you want for the due date, I made it flexible
4. Click "OK" to save the task (or Cancel if you change your mind)
5. Use toolbar buttons to edit, delete, or complete taasks (or use keyboard shortcuts - Ctrl+E to edit, Delete to delete, Ctrl+Enter to complete)
6. Use "Undo" and "Redo" buttons to reverse or reapply actions (super useful when testing! Also useful when you accidentally delete something)
7. Use "Sort" dropdown to change sorting strategy (Strategy pattern in action! You can sort by due date, priority, creation time, or alphabetically)
8. Access "View" menu for theme toggle and analyticcs (dark mode is better, just saying)
9. Use "View Trash" to see deleted tasks and restore them (because we all make mistakes - I definitely do)
10. Use the search bar to find tasks quickly (when you have too many tasks, which I do)
11. Use filters to narrow down tasks by category, priority, tag, or due date (helps when you're overwhelmed)
12. Switch between List View and Calendar View using the View menu (calendar view is pretty cool, if I do say so myself)
13. Use the summary panel on the right to see today's tasks and this week's tasks (helps me prioritize)
14. Set reminders for tasks (5 min, 30 min, 1 hour, or 1 day before) - because I need all the help I can get
15. Export/import tasks using the File menu (backup your tasks in JSON, CSV, or XML - I added all three formats because why not?)
    - A demo CSV file (demo_tasks.csv) is included in the project root for testing the import feature

TROUBLESHOOTING
---------------
(I ran into these issues while developing, so here's what I learned - hopefully this saves you some time:)

- "ClassNotFoundException": Ensure org.json library is in lib/ directory (this one got me at first - spent way too long figuring this out)
- "UnsupportedClassVersionError": Use Java 8 or higher (had to figure out the Java version thing - make sure you're using the right Java version)
- Application won't start: Check that all class files are compiled in bin/ (if you see errors, try recompiling everything)
- Date not displaying: You can actualsly type whatever you want for the due date now - I made it flexible because strict date formats are annoying
- NullPointerException on startup: This was a fun bug - make sure tableModel is initialized before any action listeners call refresh() (I fixed this, but just in case)
- Reminders not showing: Make sure you set a reminder time when creating/editing a task (I sometimes forget to check the reminder checkbox)
- Theme not switching back: This was another fun bug - had to capture original colors before applying dark theme (fixed it though!)

All fixed lets gooooooooooooooooooooooooooo (I think... if you find more bugs, let me know!)

For more information, see the documentation in the docs/ folder.

(I hope i did welllll thank youuuuuuuuuuuuu)


@echo off
echo Building Javadoc...
if not exist javadocs mkdir javadocs
javadoc -d javadocs -sourcepath src -subpackages cop4331.taskflow -encoding UTF-8
echo Javadoc generated in javadocs/ directory
pause


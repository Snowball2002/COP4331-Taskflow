#!/bin/bash
echo "Building TaskFlow JAR..."

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile all Java files
echo "Compiling Java files..."
javac -d bin -sourcepath src -encoding UTF-8 src/cop4331/taskflow/*.java src/cop4331/taskflow/**/*.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# Create manifest file
echo "Creating manifest..."
echo "Main-Class: cop4331.taskflow.TaskFlowApp" > manifest.txt

# Create JAR file
echo "Creating JAR file..."
jar cvfm bin/TaskFlow.jar manifest.txt -C bin cop4331

# Clean up manifest
rm manifest.txt

echo ""
echo "Build complete! JAR file created at: bin/TaskFlow.jar"
echo ""
echo "To run the application:"
echo "  java -jar bin/TaskFlow.jar"
echo ""


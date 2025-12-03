#!/bin/bash
echo "Building Javadoc..."
mkdir -p javadocs
javadoc -d javadocs -sourcepath src -subpackages cop4331.taskflow -encoding UTF-8
echo "Javadoc generated in javadocs/ directory"


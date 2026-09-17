#!/bin/bash
echo "Compiling Event Ticket Management System..."
mkdir -p bin
javac -d bin -sourcepath src src/com/eventticket/Main.java
if [ $? -eq 0 ]; then
    echo "Launching CLI Application..."
    java -cp bin com.eventticket.Main
else
    echo "[Error] Compilation failed!"
fi

@echo off
title Event Ticket Management System
echo Compiling Event Ticket System Java files...
javac -d bin -sourcepath src src/com/eventticket/Main.java
if %errorlevel% neq 0 (
    echo [Error] Compilation failed!
    pause
    exit /b %errorlevel%
)
echo Launching Event Ticket Management System...
java -cp bin com.eventticket.Main
pause

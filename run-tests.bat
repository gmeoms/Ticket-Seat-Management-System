@echo off
title Event Ticket System - Automated Test Runner
echo Compiling and running unit test suite...
javac -d bin -sourcepath src src/com/eventticket/test/SystemTestSuite.java
if %errorlevel% neq 0 (
    echo [Error] Test compilation failed!
    pause
    exit /b %errorlevel%
)
java -cp bin com.eventticket.test.SystemTestSuite
pause

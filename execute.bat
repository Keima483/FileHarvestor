@echo off

set SRC=E:\Programming\Java\FileHarvestor\src
set OUT=E:\Programming\Java\FileHarvestor\out

:: echo Collecting source files...
:: dir /s /b "%SRC%\com\keima\*.java" > sources.txt

:: echo Compiling...
:: javac -encoding UTF-8 -d "%OUT%" @sources.txt

:: if errorlevel 1 (
::     echo Compilation failed!
::     pause
::     exit /b 1
:: )

echo Running application...
java -cp "%OUT%" com.keima.Main

pause
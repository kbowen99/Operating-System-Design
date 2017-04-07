@echo off
setlocal
echo Compiling...
//change the last parameter to the directory with your .jack files
java -classpath "%CLASSPATH%;bin/classes;bin/lib/Hack.jar;bin/lib/Compilers.jar" ^
  Hack.Compiler.JackCompiler "C:\Users\kurti\Google Drive\Programs\NAND2\Operating-System-Design\09\Square"
echo Complete.
popd

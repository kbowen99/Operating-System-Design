# Using the Jack Compiler at YHS
 Using the jack compiler through the command prompt is blocked at school, typically one can run a command passing the directory as an argument. like this:

    JackCompiler.bat /path/to/some/file

 To Circumvent this, you can modify the JackCompiler.bat file to use the directory you want to compile. You can either download the provided batch file, or copy and paste it from here (changing the last paramter to your directory):

    @echo off
    setlocal
    echo Compiling...
    //change the last parameter to the directory with your .jack files
    java -classpath "%CLASSPATH%;bin/classes;bin/lib/Hack.jar;bin/lib/Compilers.jar" ^
     Hack.Compiler.JackCompiler "C:\Users\kurti\Google Drive\Programs\NAND2\Operating-System-Design\09\Square"
    echo Complete.
    popd

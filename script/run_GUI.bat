javac -encoding UTF-8 -d ..\out -cp .;..\lib\algs4.jar ..\src\*.java ..\src\terminal\*.java
SET file="..\sample\sample3.txt"
IF NOT "%1"=="" (set file=%1)
java -cp ..\out;..\lib\algs4.jar Main_KdTree  --GUI< %file%
pause

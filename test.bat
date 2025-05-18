@echo on
echo === Java Environment Test ===

REM Simple test for Java
echo Testing Java installation...
java -version

REM Compile a test class
echo Creating test class...
echo public class HelloWorld { public static void main(String[] args) { System.out.println("Java is working!"); } } > HelloWorld.java

echo Compiling test class...
javac HelloWorld.java

echo Running test class...
java HelloWorld

echo Test complete
pause

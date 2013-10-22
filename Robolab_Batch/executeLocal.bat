ECHO Building your solution, located in solution folder
gcc -LC:/Python27/libs -IC:/Python27/include -o builds/RobolabSimClient.exe solution/src/*.c -lpython27

set SERVER_IP= http://localhost:8080/


ECHO Starting the server
cd server
start cmd /c java -jar RobolabSim.jar

ping 127.0.0.1 -n 6 > nul

ECHO Executing your solution
cd ../builds 
RobolabSimClient.exe

ECHO Executing the validation
cd ../test
java -jar RobolabSimTestLocal.jar

PAUSE
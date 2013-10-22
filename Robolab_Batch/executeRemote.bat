ECHO Building your solution, located in solution folder
gcc -LC:/Python27/libs -IC:/Python27/include -o builds/RobolabSimClient.exe solution/src/*.c -lpython27

set SERVER_IP= http://141.30.61.87:8080/
set Maze1=myMaze
set Maze2=myMaze2
set Maze3=myMaze3

ECHO Setting first Maze
curl -X PUT %SERVER_IP%maze?="%%7B%%22map%%22%%3A%%22%Maze1%%%22%%7D"

ECHO Executing your solution
cd builds 
RobolabSimClient.exe

ECHO Executing the validation
cd ../test
java -jar RobolabSimTestRemote.jar

ECHO Setting second Maze
curl -X PUT %SERVER_IP%maze?="%%7B%%22map%%22%%3A%%22%Maze2%%%22%%7D"

ECHO Executing your solution
cd builds 
RobolabSimClient.exe

ECHO Executing the validation
cd ../test
java -jar RobolabSimTestRemote.jar

ECHO Setting third Maze
curl -X PUT %SERVER_IP%maze?="%%7B%%22map%%22%%3A%%22%Maze3%%%22%%7D"

ECHO Executing your solution
cd builds 
RobolabSimClient.exe

ECHO Executing the validation
cd ../test
java -jar RobolabSimTestRemote.jar


PAUSE
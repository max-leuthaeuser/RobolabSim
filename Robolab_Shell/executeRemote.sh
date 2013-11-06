#!/bin/sh
echo "Building your solution, located in solution folder"
gcc -lpython2.7 -I/usr/include/python2.7 -o builds/RobolabSimClient solution/src/*.c

SERVER_IP="http://141.30.61.87:8080/"
Maze1="myMaze"
Maze2="myMaze2"
Maze3="myMaze3"

echo "Setting first Maze"
curl -X PUT $SERVER_IP$maze?="%%7B%%22map%%22%%3A%%22%Maze1%%%22%%7D"

echo "Executing your solution"
cd builds
./RobolabSimClient

echo "Executing the validation"
cd ../test
java -jar RobolabSimTestRemote.jar

echo "Setting second Maze"
curl -X PUT $SERVER_IP$maze?="%%7B%%22map%%22%%3A%%22%Maze2%%%22%%7D"

echo "Executing your solution"
cd ../builds 
./RobolabSimClient

echo "Executing the validation"
cd ../test
java -jar RobolabSimTestRemote.jar

echo "Setting third Maze"
curl -X PUT $SERVER_IP$maze?="%%7B%%22map%%22%%3A%%22%Maze3%%%22%%7D"

echo "Executing your solution"
cd ../builds 
./RobolabSimClient

echo "Executing the validation"
cd ../test
java -jar RobolabSimTestRemote.jar

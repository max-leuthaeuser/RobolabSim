#!/bin/sh
echo "Building your solution, located in solution folder"
gcc -lpython2.7 -I/usr/include/python2.7 -o builds/RobolabSimClient solution/src/*.c

Maze1="myMaze"
Maze2="myMaze2"
Maze3="myMaze3"
ID="someGroupID"
IP="141.30.61.87"
SERVER_IP="http://$IP:8080/"

echo "Setting first Maze"
curl -X PUT $SERVER_IP$maze?id=$ID"&%%7B%%22map%%22%%3A%%22%Maze1%%%22%%7D"

echo "Executing your solution"
cd builds
./RobolabSimClient

echo "Executing the validation"
cd ../test
java -jar RobolabSimTest.jar

echo "Setting second Maze"
curl -X PUT $SERVER_IP$maze?id=$ID"&%%7B%%22map%%22%%3A%%22%Maze2%%%22%%7D"

echo "Executing your solution"
cd ../builds 
./RobolabSimClient

echo "Executing the validation"
cd ../test
java -jar RobolabSimTest.jar

echo "Setting third Maze"
curl -X PUT $SERVER_IP$maze?id=$ID"&%%7B%%22map%%22%%3A%%22%Maze3%%%22%%7D"

echo "Executing your solution"
cd ../builds 
./RobolabSimClient

echo "Executing the validation"
cd ../test
java -jar RobolabSimTest.jar

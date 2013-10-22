#!/bin/sh
echo "Building your solution, located in solution folder"
gcc -lpython2.7 -I/usr/include/python2.7 -o builds/RobolabSimClient solution/src/*.c

SERVER_IP="http://localhost:8080/"

echo "Starting the server"
cd server
nohup java -jar RobolabSim.jar &
ping 127.0.0.1 -c 4 > /dev/null
echo "Done"

echo "Executing your solution"
cd ../builds 
./RobolabSimClient
echo "Done"

echo "Executing the validation"
cd ../test
java -jar RobolabSimTestLocal.jar
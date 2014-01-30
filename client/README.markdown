## RobolabSim Client

Follow these steps to get started:

1. Git-clone this repository.

2. Change directory into your clone and the src/ directory.

        $ cd RobolabSim/client/c/src/

3. Compile it (**you need to have Python 2.7 installed**)

        3.1. Linux: $ gcc -lpython2.7 -I/usr/include/python2.7 -o RobolabSimClient *.c

        3.2. Windows: $ gcc -LC:/Python27/libs -IC:/Python27/include -o RobolabSimClient.exe *.c -lpython27

        3.3. MacOS X: $ gcc -ansi -lpython2.7 -I/usr/include/python2.7 -o RobolabSimClient *.c

4. Start the application:

        $ ./RobolabSimClient

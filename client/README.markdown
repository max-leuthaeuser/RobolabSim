## RobolabSim Client

Follow these steps to get started:

1. Git-clone this repository.

2. Change directory into your clone and the ```src/``` directory.

        $ cd RobolabSim/client/c/src/

3. Edit ```h/Configuration.h``` with your group ID.

4. Compile it (**you need to have Python 2.7 installed**).

    4.1. Linux:
    
        $ gcc -lpython2.7 -I/usr/include/python2.7 -o RobolabSimClient *.c

    4.2. Windows:
    
        $ gcc -lpython27 -LC:/Python27/libs -IC:/Python27/include -o RobolabSimClient.exe *.c

    4.3. MacOS X:
    
        $ gcc -ansi -lpython2.7 -I/usr/include/python2.7 -o RobolabSimClient *.c

5. Start the application:

        $ ./RobolabSimClient

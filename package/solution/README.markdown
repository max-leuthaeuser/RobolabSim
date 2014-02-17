## RobolabSim Client

Follow these steps to get started:

1. Install Python 2.7 from [here][python]. (use the 32-bit version !!)

2. For Windows only: Download and install [MinGW][gcc] and install the base package to get GCC on your system. And don't forget to add ```C:\MinGW\bin``` to your PATH variable.

3. Git-clone this repository.

4. Edit ```h/Configuration.h``` with your group ID.

5. Change directory into your clone and the ```src/``` directory.

        $ cd RobolabSim/package/solution/c/src/

6. Compile it (**you need to have GCC and Python 2.7 installed, see steps 1 and 2!**).

    6.1. Linux:
    
        $ gcc -lpython2.7 -I/usr/include/python2.7 -o RobolabSimClient *.c

    6.2. Windows:
    
        $ gcc -LC:/Python27/libs -IC:/Python27/include -o RobolabSimClient.exe *.c -lpython27

    6.3. MacOS X:
    
        $ gcc -ansi -lpython2.7 -I/usr/include/python2.7 -o RobolabSimClient *.c

7. Start the application:

        $ ./RobolabSimClient

[python]: http://www.python.org/download/releases/2.7.6/ "Python"
[gcc]: http://sourceforge.net/projects/mingw/files/ "MinGW"

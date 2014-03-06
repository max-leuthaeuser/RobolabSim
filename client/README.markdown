## RobolabSim Client

Follow these steps to get started:

1. For Windows only:

    1.a. Download and install [Visual Studio 2013](http://www.microsoft.com/de-de/download/details.aspx?id=40787).    
    
    **Or**
    
    1.b. Download, install [Cygwin](http://cygwin.com/install.html) and add the `cygwin\bin` folder to your Path variable.
   For Ubuntu install ```python``` and ```python-dev``` (```sudo apt-get install python python-dev```)

2. For Windows only: Download and install [MinGW][gcc] and install the base package to get GCC on your system. And don't forget to add ```C:\MinGW\bin``` to your PATH variable.


2. Git-clone this repository.

3. Edit ```h/Configuration.h``` with your group ID.

4. Change directory into your clone.

        $ cd RobolabSim/package/solution/

5. Compile it

    5.1. Linux:
        $ make
        
    5.2. Windows:   
        5.2.a. Compile with Visual Studio:
            Create -> Create Project  
            
    **Or**
        $ gcc -I/usr/include/python2.7 -o RobolabSimClient *.c -lpython2.7

        5.2.b. Compile with Cygwin:
            $ make

    5.3. MacOS X:
        $ make

6. Start the application:

    6.1. Linux & MacOS X:
        $ ./Release/RobolabSimClient
        
    6.2. Windows:   
        $ ./Release/RobolabSimClient.exe

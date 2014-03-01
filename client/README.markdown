## RobolabSim Client

Follow these steps to get started:

2. For Windows only: Download and install [Visual Studio 2013](http://www.microsoft.com/de-de/download/details.aspx?id=40787).    
Or Download, install [Cygwin](http://cygwin.com/install.html) and add the `cygwin/bin` folder to your Path variable.

3. Git-clone this repository.

4. Edit ```h/Configuration.h``` with your group ID.

5. Change directory into your clone and the ```src/``` directory.

        $ cd RobolabSim/package/solution/

6. Compile it

    6.1. Linux:
    
        $ make

    6.2. Windows:
    
        Compile with Visual Studio:
			Create -> Create Project
		Compile with Cygwin:
			$ make

    6.3. MacOS X:
    
        $ make

7. Start the application:

    7.1. Linux & MacOS X:
        
        $ ./Release/RobolabSimClient
        
    7.2. Windows:
        
        $ ./Release/RobolabSimClient.exe
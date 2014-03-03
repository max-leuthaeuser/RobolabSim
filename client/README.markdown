## RobolabSim Client

Follow these steps to get started:

1. For Windows only:

    1.a. Download and install [Visual Studio 2013](http://www.microsoft.com/de-de/download/details.aspx?id=40787).    
    
    **Or**
    
    1.b. Download, install [Cygwin](http://cygwin.com/install.html) and add the `cygwin\bin` folder to your Path variable.

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

        5.2.b. Compile with Cygwin:
            $ make

    5.3. MacOS X:
        $ make

6. Start the application:

    6.1. Linux & MacOS X:
        $ ./Release/RobolabSimClient
        
    6.2. Windows:   
        $ ./Release/RobolabSimClient.exe
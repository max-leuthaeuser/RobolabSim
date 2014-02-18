## Development with Eclipse on Windows
----------------------
1. Download **Eclipse for C&C++** Developers & start it

2. Create a new C project ```File > New > C Project```

4. In the Wizard choose **Hello World ANSI C Project** and a Compiler which is adequate to your system (in my case ***MinGW gcc***). Additionaly give the Project a nice name =)

5. Build it (*small hammer icon*) & run it (*small green arrow button*)

6. If this works your compiler is OK, if not **STOP HERE** and try to get this to work!

7. Create a ```c/``` Folder. Additionaly reate 2 new subfolders in your ```c/``` folder named ```h/``` and ```src/```

8. Copy the 4 files ```Communication.h```, ```Configuration.h```, ```RobotProxy.h``` & ```Urlcode.h``` from the provided ```solution/``` folder in the created h folder.

9. Do the same with the ```*.c``` files and the ```src/``` folder.

10. Delete the auto-created ```.c``` file ```Your_project_name.c``` in your ```src/``` folder (it includes the "Hello World output").

11. **FOR THE NEXT STEPS YOU NEED PYTHON INSTALLED**

12. Right click on your ```Project > Properties > C/C++ General > Path and Symbols > Includes Tab > GNU C > Add...```

13. Add the python/include path from your filesystem (e.g. in my case ```C:\Python27\include```).

14. Right click on your Project > ```Properties > C/C++ Build > Settings > MinGW C Linker > Libaries > plus icon next to "Libaries (-l)"``` .

15. There type ```python27```

16. In the window below click on the plus icon next to "Libary search path (-L)" and add there the path to your python/libs folder (e.g. in my case ```C:\Python27\libs```).

17. It's possible now to build the application (*via the hammer icon* or ```CTRL+B```) and run it (*via the green arrow*).

Your can now follow the instructions in our README.

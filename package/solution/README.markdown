## RobolabSim Client

Follow these steps to get started:

- Install requirements:
	- For **Windows**:
		- Download and install [Cygwin](https://cygwin.com/install.html) (You need it anyway). 
		- When Cygwin asks about packages (Click on skip to install package):
			- Search make -> **Devel/make**
			- Search libcurl -> **Libs/libcurl-devel**
			- Search gcc-core -> **Devel/gcc-core**
		- After installation add the **`cygwin\bin`** folder to your Path variable.
		- **Restart** your PC or kill the explorer.exe and restart it.

	- For **Linux** and Apt-based systems:
		- Run **`sudo apt-get update`** 
		- Run **`sudo apt-get install build-essential`** (You need it anyway)
		- Run **`sudo apt-get install libcurl4-openssl-dev`**

	- For **OSX**:
		- Install **Xcode** from Apple Store (You need it anyway)

- Git-clone this repository.

- Change directory into your clone.

		$ cd RobolabSim/package/solution/

- Edit **`c/h/Configuration.h`** with your group ID.

- Compile it:
	- For **Windows**:
		- Run **`build.bat`**

	- For **Linux** and **OSX**:
		- Run **`build.sh`**

- Start the application:
	- For **Windows**:
		- Run **`c/builds/win/RobolabSimClient.exe`**

	- For **Linux** and Apt-based systems:
		- Run **`c/builds/linux/RobolabSimClient`**

	- For **OSX**:
		- Run **`c/builds/osx/RobolabSimClient`**
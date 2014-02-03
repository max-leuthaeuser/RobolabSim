#include "../h/Communication.h"

char* sendAndRecieve(const char* url) {
	Py_Initialize();

	PyObject *pArgs, *pValue, *pFunc;
	PyObject *pGlobal = PyDict_New();
	PyObject *pLocal;

	PyRun_SimpleString("import types,sys");

	//create the new module in python
	PyRun_SimpleString("mymod = types.ModuleType(\"mymod\")");

	//add it to the sys modules so that it can be imported by other modules
	PyRun_SimpleString("sys.modules[\"mymod\"] = mymod");

	//import sys so that path will be available in mymod so that other/newly created modules can be imported
	PyRun_SimpleString("exec 'import sys' in mymod.__dict__");

	//import it to the current python interpreter
	PyObject *pNewMod = PyImport_Import(PyString_FromString("mymod"));

	//Get the dictionary object from my module so I can pass this to PyRun_String
	pLocal = PyModule_GetDict(pNewMod);

	//import urllib2 to global namespace
	PyMapping_SetItemString(pGlobal, "urllib2",
			PyImport_ImportModule("urllib2"));

	//Define my function in the newly created module
	pValue =
			PyRun_String("def get(url):\n\treturn urllib2.urlopen(url).read()\n", Py_file_input, pGlobal, pLocal);
	Py_DECREF(pValue);

	//Get a pointer to the function I just defined
	pFunc = PyObject_GetAttrString(pNewMod, "get");
	if (pFunc == NULL) {
#ifdef DEBUG
		PyErr_Print();
#endif
		return NULL;
	}

	//Build a tuple to hold my arguments (just the number 4 in this case)
	pArgs = PyTuple_New(1);
	pValue = PyString_FromString(url);
	PyTuple_SetItem(pArgs, 0, pValue);

	//Call my function, passing it the number four
	pValue = PyObject_CallObject(pFunc, pArgs);
	if (pValue == NULL) {
#ifdef DEBUG
		PyErr_Print();
#endif
		return NULL;
	}

	Py_DECREF(pArgs);
	char* result = PyString_AsString(pValue);
	if (result == NULL) {
#ifdef DEBUG
		PyErr_Print();
#endif
		return NULL;
	}

	Py_DECREF(pValue);
	Py_XDECREF(pFunc);
	Py_DECREF(pNewMod);
	Py_Finalize();

	return result;
}

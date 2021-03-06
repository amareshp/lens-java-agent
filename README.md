# Reverse Engineering java libraries using runtime byte code manipulation
## Example of reverse engineering using javaassist

**There are three types of byte code manipulation libraries**
1. Low level - e.g. **asm**
2. High level - e.g. **javaassist**
3. Very high level - e.g. **AspectJ**

This utility uses javaassist library to create the instrumentation agent.

## Here is how this utility works
* lens-agent/out/artifacts/lens_agent_jar/lens-agent.jar file is our agent library.
* In order to use the agent use the option **_-javaagent:<agent_jar_path>_**
* The program take a JVM parameter to specify which packages to track. Specify **_-DINSTR_FILE=<path_to_instr_file>_**
* The program take a JVM parameter to specify location of the agent log file. Specify **_-DLOG_FILE=<path_to_log_file>_**
* Run the sample application **_lens-sample/src/com/ap/App.java_**
* Example VM parameters **_-javaagent:D:\\lens-agent\\lens-agent.jar -DINSTR_FILE=D:\\lens-agent\\instr_file.txt -DLOG_FILE=D:\\lens-agent\\lens-agent.log_**
* Now you should see the method calls printed out to the log file specified in VM parameters.

## Default behavior
* The VM Option **_-javaagent:<agent_jar_path>_** is mandatory.
* If you do not specify the VM Option **_-DINSTR_FILE=<path_to_instr_file>_**, it looks for a file called **instr_file.txt** in the user's home directory.
* If you do not specify the VM Option **_-DLOG_FILE=<path_to_log_file>_**, it generates the log file named **lens_log_file.log** in the user's home directory.

## Sample instr_file contains list of packages to track. 
**Sample contents of an instr_file**
com.ap
com.ebay.a

## Building from source - Modify the agent
You need to add **javaassist** library to the lens-agent project to be able to build it. You can make changes to the agent and transformer code. Export the project again to create a new agent jar file.  
When you run the main method from the lens-sample project use the new agent path.  
JVM options -  
**_-javaagent:<agent_jar_path>  -DINSTR_FILE=<path_to_instr_file>_**

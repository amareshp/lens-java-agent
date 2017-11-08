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
* The program take one more JVM parameter to specify which packages to track. Specify **_-DINSTR_FILE=<path_to_instr_file>_**
* Run the sample application **_lens-sample/src/com/ap/App.java_**
* Now you should see the method calls printed out to system console.

## Sample instr_file contains list of packages to track. 
**Sample contents of an instr_file**
com.ap
com.ebay.a

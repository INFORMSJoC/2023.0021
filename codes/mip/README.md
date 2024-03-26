## Replicating

Replicating the results of the mathematical model may take too much computatation power. Some instances cannot be solved optimally which increases the overall running time. For each instance, we shared both the processed results inside the mip folder for each model as well as the per-instance logs from CPLEX  which can be found inside the cplex_logs folder.

To replicate the results for a instance, you need to use the Main.java file and set the instance path and the number of shift variables as well as the log file path. Also, you may need to link the CPLEX libraries before compiling. We provided commands that work for us on Ubuntu.



```
 export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/path/to/cplex/bin/x86-64_linux 
 javac -cp /path/to/CPLEX/cplex/lib/cplex.jar *.java 
 java -cp /path/to/CPLEX/cplex/lib/cplex.jar:. Main 
 ```


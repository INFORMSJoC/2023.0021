## Replicating

Replicating the results of the mathematical model may take too much computatation power. Some instances cannot be solved optimally which increases the overall running time. For each instance, we shared both the processed results inside the mip folder for each model as well as the per-instance logs from CPLEX  which can be found inside the cplex_logs folder.

To replicate the results for a instance, you need to use the Main.java file and set the instance path and the number of shift variables as well as the log file path. Also, you may need to link the CPLEX libraries before compiling. We used IntelliJ IDEA for this and added the library using the GUI.
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;


import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


public class ExactMIP {


    public SolutionCost solve(DataMigrationInstanceCost instance, int timeLimit, boolean output,  String logFile) {
        int[] L = instance.getCapacities();
        int[] W = instance.getSizes();
        int DB =  W.length;
        int[] C = instance.getCosts();
        int APP = C.length;
        int SHIFT = L.length;
        short[][] D = instance.getInterrelations();

        try {
            IloCplex cplex = new IloCplex();
            cplex.setParam(IloCplex.Param.WorkMem, 1024 * 16);
            cplex.setParam(IloCplex.Param.MIP.Limits.TreeMemory, 24 * 1024);
            if (timeLimit != 0) {
                cplex.setParam(IloCplex.Param.TimeLimit, timeLimit);
                cplex.setParam(IloCplex.Param.ClockType, 2);
            }

            //variables
            IloNumVar[][]  x = new IloNumVar[DB][];
            for (int i = 0; i < DB; i++)
                x[i] =  cplex.boolVarArray(SHIFT);

            IloNumVar[][] h = new IloNumVar[APP][];
            for (int a = 0; a < APP; a++)
                h[a] = cplex.intVarArray(SHIFT, 0, DB);

            IloNumVar[][] g = new IloNumVar[APP][];
            for (int a = 0; a < APP; a++)
                g[a] = cplex.boolVarArray(SHIFT);

            IloNumVar[] a = cplex.intVarArray(APP, 0, DB);



            //Objective Function
            IloLinearNumExpr objective = cplex.linearNumExpr();
            for (int k = 0; k < APP; k++)
                objective.addTerm(a[k], C[k]);

//            cplex.addMinimize(objective);
            cplex.addMinimize(objective);
            //constraints


            //constraint 1
            for (int i = 0; i < DB; i++) {
                IloLinearNumExpr expr = cplex.linearNumExpr();
                for(int j = 0; j < SHIFT; j++)
                    expr.addTerm(1, x[i][j]);
                cplex.addEq(expr, 1);
            }

            //constraint2
            for (int j = 0; j < SHIFT; j++) {
                IloLinearNumExpr expr = cplex.linearNumExpr();
                for (int i = 0; i < DB; i++)
                    expr.addTerm(W[i], x[i][j]);
                cplex.addLe(expr, L[j]);
            }
            
            //constraint3
            for (int j = 0; j < SHIFT; j++)
                for (int k = 0; k < APP; k++) {
                    int M = 0;
                    IloLinearNumExpr expr = cplex.linearNumExpr();
                    IloLinearNumExpr expr2 = cplex.linearNumExpr();

                    for (int i = 0; i < DB; i++) {
                        M += D[i][k];
                        expr.addTerm(x[i][j], D[i][k]);
                    }


                    expr2.addTerm(M, g[k][j]);
                    cplex.addLe(expr, expr2);
                }
            

            //constraint 4
            for(int k = 0; k < APP; k++) {
                IloLinearNumExpr expr = cplex.linearNumExpr();
                for (int j = 0; j < SHIFT; j++)
                    expr.addTerm(1, g[k][j]);

                cplex.addEq(a[k], expr);
            }
            

            if (!output)
                cplex.setOut(null);
            else {
                if (logFile != null)
                    try {
                        FileOutputStream s = new FileOutputStream(logFile);
                        cplex.setOut(s);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

            }
            //solve
            long start = System.currentTimeMillis();
            cplex.solve();


            long end = System.currentTimeMillis();
            long runningTime = end - start;
            double objValue = cplex.getObjValue();

            SolutionCost solution = new SolutionCost(instance);
            for (int i = 0; i < DB; i++)
                for (int j = 0; j < SHIFT; j++)
                    if (cplex.getValue(x[i][j])  > 0.99) {
                        solution.assignDatabase(i,j);
                    }

            solution.setExecutionTime(runningTime);
            solution.setLowerBound(cplex.getBestObjValue());

            cplex.end();
            return solution;


        } catch (Exception ex) {
            System.out.println("Crashed!!!!!!!!!!");
            System.out.println(logFile);
            ex.printStackTrace();
        }
        return null;
    }

}

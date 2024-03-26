public class Main {
   public static void main(String[] args) {
        String instancePath = "../../instances/constant_shift_sizes/node_edge_weighted/dmp/10_20_2_5_2_0.1_0.dmp";
        boolean output = true;
        String logFilePath = "/tmp/test.txt";


        DataMigrationInstanceCost instance = DataMigrationInstanceGenerator.readMinCostInstance(instancePath);
        ExactMIP solver = new ExactMIP();

        SolutionCost solution = solver.solve(instance, 0, output,  logFilePath);

   }
}

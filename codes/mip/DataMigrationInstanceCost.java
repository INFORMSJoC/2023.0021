import java.util.ArrayList;
import java.util.HashSet;

public class DataMigrationInstanceCost extends DataMigrationInstance {

    private int[] costs;
    private short[][] interrelations;
    private int noApps;
    private ArrayList<HashSet<Integer>> usedAppsByDB = null;
    private int maxObjectiveValue = -1;
    private int[][] commonCosts = null;

    public DataMigrationInstanceCost(int noDatabases, int noShifts, int noApps) {
        super(noDatabases, noShifts);
        this.noApps = noApps;
    }

    public DataMigrationInstanceCost(int[] sizes, int[] capacities, int[] costs, short[][] interrelations) {
        super(sizes,capacities);
        this.costs = costs;
        this.interrelations = interrelations;
    }


    public int[] getCosts() {
        return costs;
    }

    public short[][] getInterrelations() {
        return interrelations;
    }


    public ArrayList<HashSet<Integer>> getUsedAppsByDB() {
        if (usedAppsByDB == null)
            usedAppsByDB = calculateUsedAppsByDB();

        return usedAppsByDB;
    }


    private ArrayList<HashSet<Integer>> calculateUsedAppsByDB() {
        ArrayList<HashSet<Integer>> apps = new ArrayList<>();
        for (int i = 0; i < noDatabases; i++) {
            HashSet<Integer> appByDB = new HashSet<>();
            for (int j = 0; j < interrelations[i].length;j++)
                if (interrelations[i][j] == 1)
                    appByDB.add(j);
            apps.add(appByDB);
        }

        return apps;

    }

    public int maxObjectiveFunctionValue() {
        if (maxObjectiveValue != -1)
            return maxObjectiveValue;
//        int[] costs = getCosts();
        short[] appUsage = new short[costs.length];
//        short[][] interrelations = instance.getInterrelations();
        for (int i = 0; i < costs.length; i++) {
            for (int j = 0; j < interrelations.length; j++)
                appUsage[i] += interrelations[j][i];
        }

        int totalCost = 0;
        for (int i = 0; i < appUsage.length;i++)
            totalCost += costs[i] * appUsage[i];
        maxObjectiveValue = totalCost;
        return totalCost;
    }

    public int commonCost(int db1, int db2) {
        ArrayList<HashSet<Integer>> usedAppsByDB = getUsedAppsByDB();
        HashSet<Integer> commonApps = new HashSet<>();
        commonApps.addAll(usedAppsByDB.get(db1));
        commonApps.addAll(usedAppsByDB.get(db2));

        int cost = 0;
        for (int i = 0; i < costs.length; i++)
            cost += costs[i];

        return cost;
    }

    public int[][] getCommonCosts() {
        if (commonCosts == null)
            calculateCommonsCosts();
        return commonCosts;
    }

    private void calculateCommonsCosts() {
        commonCosts = new int[noDatabases][noDatabases];
        ArrayList<HashSet<Integer>> usedAppsByDB = getUsedAppsByDB();
        for (int i = 0; i < noDatabases; i++)
            for (int j = i; j < noDatabases; j++) {
                int cost = 0;
                for (int app = 0; app < costs.length; app++) {
                    if (interrelations[i][app] == 1 && interrelations[j][app] == 1) {
                        cost += costs[app];
                        commonCosts[i][j] = cost;
                        commonCosts[j][i] = cost;
                    }
                }



            }

    }
}

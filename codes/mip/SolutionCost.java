import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class SolutionCost {
    private DataMigrationInstanceCost instance;
    private int objectiveFunctionValue;
    private double lowerBound;
    private long executionTime;
    private int[] currentCapacities = null;

    private int[][] noUsedApp;
    private ArrayList<HashSet<Integer>> assignments = null;
    private int[] database2Shift = null;

    public SolutionCost(int[] database2Shift, DataMigrationInstanceCost instance, long executionTime) {
        this.instance = instance;
        this.executionTime = executionTime;

        this.instance = instance;
        this.assignments = new ArrayList<>();
        this.objectiveFunctionValue = 0;
        for (int i = 0; i < instance.getCapacities().length; i++)
            this.assignments.add(new HashSet<>());


        this.database2Shift = new int[instance.getSizes().length];
        Arrays.fill(database2Shift, -1);
        this.currentCapacities = new int[instance.getCapacities().length];
        this.noUsedApp = new int[instance.getCapacities().length][instance.getCosts().length];
        for (int i = 0; i < currentCapacities.length; i++)
            currentCapacities[i] = instance.getCapacity(i);

        for (int db = 0; db < database2Shift.length; db++)
            assignDatabase(db, database2Shift[db]);
    }

    public SolutionCost(ArrayList<HashSet<Integer>> assignments, DataMigrationInstanceCost instance, long executionTime) {
        this.instance = instance;
        this.executionTime = executionTime;

        this.instance = instance;
        this.assignments = new ArrayList<>();
        this.objectiveFunctionValue = 0;
        for (int i = 0; i < instance.getCapacities().length; i++)
            this.assignments.add(new HashSet<>());


        this.database2Shift = new int[instance.getSizes().length];
        Arrays.fill(database2Shift, -1);
        this.currentCapacities = new int[instance.getCapacities().length];
        this.noUsedApp = new int[instance.getCapacities().length][instance.getCosts().length];
        for (int i = 0; i < currentCapacities.length; i++)
            currentCapacities[i] = instance.getCapacity(i);

        for (int shift = 0; shift < assignments.size(); shift++)
            for (int db : assignments.get(shift))
                assignDatabase(db, shift);


    }

    public SolutionCost(DataMigrationInstanceCost instance) {
        this.instance = instance;
        assignments = new ArrayList<>();
        for (int i = 0; i < instance.getCapacities().length; i++)
            assignments.add(new HashSet<>());


        database2Shift = new int[instance.getSizes().length];
        Arrays.fill(database2Shift, -1);
        currentCapacities = new int[instance.getCapacities().length];
        noUsedApp = new int[instance.getCapacities().length][instance.getCosts().length];
        for (int i = 0; i < currentCapacities.length; i++)
            currentCapacities[i] = instance.getCapacity(i);

        this.objectiveFunctionValue = 0;


    }

    public SolutionCost(SolutionCost other) {
        this.instance = other.instance;
        this.assignments = new ArrayList<>();
        this.executionTime = other.executionTime;
        for (int i = 0; i < instance.getCapacities().length; i++)
            this.assignments.add(new HashSet<>());

        this.database2Shift = new int[instance.getSizes().length];
        Arrays.fill(database2Shift, -1);
        this.currentCapacities = new int[instance.getCapacities().length];
        this.noUsedApp = new int[instance.getCapacities().length][instance.getCosts().length];
        for (int i = 0; i < currentCapacities.length; i++)
            currentCapacities[i] = instance.getCapacity(i);

        for (int shift = 0; shift < other.assignments.size(); shift++)
            for (int db : other.assignments.get(shift))
                assignDatabase(db, shift);

    }

    public SolutionCost(ArrayList<HashSet<Integer>> assignments, DataMigrationInstanceCost instance, long executionTime, int objVal) {
        this.instance = instance;
        this.executionTime = executionTime;

        this.instance = instance;
        this.assignments = new ArrayList<>();
        this.objectiveFunctionValue = 0;
        for (int i = 0; i < instance.getCapacities().length; i++)
            this.assignments.add(new HashSet<>());


        this.database2Shift = new int[instance.getSizes().length];
        Arrays.fill(database2Shift, -1);
        this.currentCapacities = new int[instance.getCapacities().length];
        this.noUsedApp = new int[instance.getCapacities().length][instance.getCosts().length];
        for (int i = 0; i < currentCapacities.length; i++)
            currentCapacities[i] = instance.getCapacity(i);

        for (int shift = 0; shift < assignments.size(); shift++)
            for (int db : assignments.get(shift))
                assignDatabase(db, shift);


        if (this.objectiveFunctionValue != objVal) {
            System.out.println("Error on objective function!..");
        }

    }

    private int calculateObjectiveFunctionValue() {
        int val = 0;
        int[] costs = instance.getCosts();
        for (int shift = 0; shift < noUsedApp.length; shift++)
            for (int app = 0; app < noUsedApp[shift].length; app++)
                if (noUsedApp[shift][app] > 0)
                    val += costs[app];

        return val;
    }

    public void updateObjectiveFunctionvalue() {
        this.objectiveFunctionValue = calculateObjectiveFunctionValue();
        currentCapacities = null;
        getCurrentCapacities();
    }

    public int getObjectiveFunctionValue() {
        return objectiveFunctionValue;
    }

    public DataMigrationInstanceCost getInstance() {
        return instance;
    }

    public int getCostOfShift(int shift) {
        int[] costs = instance.getCosts();
        int currentCostOfShift = 0;
        for (int app = 0; app < noUsedApp[shift].length; app++)
            if (noUsedApp[shift][app] > 0)
                currentCostOfShift += costs[app];

        return currentCostOfShift;
    }

    public int getCommonCostOfShift(int shift) {
        int[] costs = instance.getCosts();
        int currentCostOfShift = 0;
        for (int app = 0; app < noUsedApp[shift].length; app++)
            if (noUsedApp[shift][app] > 1)
                currentCostOfShift += costs[app];
//                currentCostOfShift += ((noUsedApp[shift][app] - 1) * costs[app]);


        return currentCostOfShift;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public void setLowerBound(double lowerBound) {this.lowerBound = lowerBound;}

    public int getCostOfShift(int shift, int in, int out) {
        int[] usedApps = Arrays.copyOf(noUsedApp[shift], noUsedApp[shift].length);
        short[][] interrelations = instance.getInterrelations();

        for (int app = 0; app < interrelations[in].length; app++) {
            if (interrelations[in][app] == 0)
                continue;

            usedApps[app]++;
        }

        for (int app = 0; app < interrelations[out].length; app++) {
            if (interrelations[out][app] == 0)
                continue;

            usedApps[app]--;
        }
        int[] costs = instance.getCosts();
        int currentCostOfShift = 0;
        for (int app = 0; app < usedApps.length; app++)
            if (usedApps[app] > 0)
                currentCostOfShift += costs[app];

        return currentCostOfShift;
    }

    public int changeInObjFunction(int i, int j) {
        if (i == j)
            return 0;

        int[] representation = database2Shift;
        int shiftCost1 = getCostOfShift(representation[i]);
        int shiftCost2 = getCostOfShift(representation[j]);
        int shiftCost3 = getCostOfShift(representation[i], j, i);
        int shiftCost4 = getCostOfShift(representation[j], i, j);
        return (shiftCost3+shiftCost4)-(shiftCost1+shiftCost2);
    }

    public int[] getCurrentCapacities() {
        if (currentCapacities == null) {
            int[] sizes = instance.getSizes();
            currentCapacities = Arrays.copyOf(instance.getCapacities(), instance.getCapacities().length);
            for(int shiftIndex = 0; shiftIndex < currentCapacities.length; shiftIndex++) {
                HashSet<Integer> shift = assignments.get(shiftIndex);
                for(int db : shift)
                    currentCapacities[shiftIndex] -= sizes[db];
            }
        }

        return currentCapacities;
    }

    public void assignDatabase(int db, int shift) {
        int[] currentCapacities = getCurrentCapacities();
        int[] sizes = instance.getSizes();
        int[] costs = instance.getCosts();
        assignments.get(shift).add(db);
        database2Shift[db] = shift;
        currentCapacities[shift] -= sizes[db];
        short[][] interrelations = instance.getInterrelations();

        for (int app = 0; app < interrelations[db].length; app++) {
            if (interrelations[db][app] == 0)
                continue;

            noUsedApp[shift][app]++;

            if (noUsedApp[shift][app] == 1)
                objectiveFunctionValue += costs[app];
        }
    }

    public void removeDatabase(int db, int shift) {
        int[] currentCapacities = getCurrentCapacities();
        int[] sizes = instance.getSizes();

        database2Shift[db] = -1;
        assignments.get(shift).remove(db);
        currentCapacities[shift] += sizes[db];

        short[][] interrelations = instance.getInterrelations();
        for (int app = 0; app < interrelations[db].length; app++) {
            if (interrelations[db][app] == 0)
                continue;

            noUsedApp[shift][app]--;
            if (noUsedApp[shift][app] == 0)
                objectiveFunctionValue--;
        }

    }

    public int[][] getNoUsedApp() {
        return noUsedApp;
    }

    public ArrayList<HashSet<Integer>> getAssignments() {
        return assignments;
    }

    public int[] getDatabase2ShiftRepresentation() {
        return database2Shift;
    }


    public int getSizeOfShift(int shift) {
        return instance.getCapacity(shift) - currentCapacities[shift];
    }

    public int capacityPenalty() {
        int[] capacities = instance.getCapacities();
        int[] currentCapacities = getCurrentCapacities();
        int penalty = 0;
        for (int shift = 0; shift < capacities.length; shift++)
            if (currentCapacities[shift] < 0)
                penalty += currentCapacities[shift];

        return -penalty;
    }


    public int calculateCommonCost(int db, int shift) {
        short[][] interrelations = instance.getInterrelations();
        int[] costs = instance.getCosts();
        int commonCost = 0;

        for (int app = 0; app < interrelations[db].length; app++) {
            if (interrelations[db][app] == 0)
                continue;

            if (noUsedApp[shift][app] > 0)
                commonCost += costs[app];
        }

        return commonCost;
    }


    public long getExecutionTime() {
        return executionTime;
    }

    @Override
    public String toString() {
        return "" + objectiveFunctionValue;
    }

    @Override
    public boolean equals(Object o) {
        if (!this.getClass().equals(o.getClass()))
            return false;

        SolutionCost other = (SolutionCost) o;

        for (int i = 0; i < this.database2Shift.length; i++)
            if (this.database2Shift[i] != other.database2Shift[i])
                return false;

        return true;


    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void writeSolution(String filename) {
        PrintWriter writer = null;
//        database2Shift
        try {
            writer = new PrintWriter(filename, "UTF-8");
            for (int i = 0; i < database2Shift.length; i++) {
//                writer.println(String.format("%d %d", i, database2Shift[i]));
                writer.println(database2Shift[i]);
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
    }
}

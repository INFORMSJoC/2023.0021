import java.util.*;

public class DataMigrationInstance {
    protected int[] sizes;
    protected int[] capacities;
    protected int noDatabases;
    protected int noShifts;
    protected ArrayList<HashSet<Integer>> precedenceList;
    protected ArrayList<HashSet<Integer>> reversePrecedenceList;
    private int maxShiftCapacity = -1;

    protected Database[] databases;
    protected Shift[] shifts;



    public DataMigrationInstance(Database[] databases, Shift[] shifts) {


        this.sizes = new int[databases.length];
        for (int i = 0; i < databases.length; i++)
            sizes[i] = databases[i].getSize();

        this.capacities = new int[shifts.length];
        for (int i = 0; i < shifts.length; i++)
            capacities[i] = shifts[i].getCapacity();

        this.databases = databases;
        this.shifts = shifts;



    }

    public DataMigrationInstance(Database[] databases, Shift[] shifts, ArrayList<HashSet<Integer>> precedenceList, ArrayList<HashSet<Integer>> reversePrecedenceList) {
        this.databases = databases;
        this.shifts = shifts;
        this.precedenceList = precedenceList;
        this.reversePrecedenceList = reversePrecedenceList;
    }

    public DataMigrationInstance(int[] sizes, int[] capacities, ArrayList<HashSet<Integer>> precedenceList, ArrayList<HashSet<Integer>> reversePrecedenceList) {
        databases = new Database[sizes.length];
        for (int i = 0; i < sizes.length; i++)
            databases[i] = new Database(i, sizes[i]);

        shifts = new Shift[capacities.length];
        for (int i = 0; i < capacities.length; i++)
            shifts[i] = new Shift(i, capacities[i]);

        this.sizes = sizes;
        this.capacities = capacities;

        this.precedenceList = precedenceList;
        this.reversePrecedenceList = reversePrecedenceList;

    }

    public DataMigrationInstance(int noDatabases, int noShifts) {
        this.noDatabases = noDatabases;
        this.noShifts = noShifts;

        this.sizes = new int[noDatabases];
        this.capacities = new int[noShifts];
    }



    public DataMigrationInstance(int[] sizes, int[] capacities) {
        this.sizes = sizes;
        this.capacities = capacities;

        this.noDatabases = sizes.length;
        this.noShifts = capacities.length;
    }

    public DataMigrationInstance(DataMigrationInstance instance) {
        this.sizes = instance.sizes;
        this.capacities = instance.capacities;
        this.noDatabases = instance.noDatabases;
        this.noShifts = instance.noShifts;

        this.precedenceList = new ArrayList<>();
        for (int i = 0; i < instance.precedenceList.size(); i++)
            precedenceList.add(new HashSet<>(instance.precedenceList.get(i)));


        this.reversePrecedenceList = new ArrayList<>();
        for (int i = 0; i < instance.reversePrecedenceList.size(); i++)
            reversePrecedenceList.add(new HashSet<>(instance.reversePrecedenceList.get(i)));

    }

    public int[] getSizes() {
        return sizes;
    }

    public int[] getCapacities() {
        return capacities;
    }

    public ArrayList<HashSet<Integer>> getPrecedenceList() {
        return precedenceList;
    }

    public int getDatabasesSize(int db) {
        return sizes[db];
    }

    public int getCapacity(int shift) {
        return capacities[shift];
    }

    public ArrayList<HashSet<Integer>> getReversePredenceList() {
        return reversePrecedenceList;
    }

    public int getMaxShiftCapcitiy() {
        if (maxShiftCapacity == -1) {
            int max = -1;
            for (int cap : capacities)
                if (cap > max)
                    max = cap;
            maxShiftCapacity = max;
        }
        return maxShiftCapacity;
    }

    public void setPrecedenceList(ArrayList<HashSet<Integer>> precedenceList) {
        this.precedenceList = precedenceList;
    }

    public void setReversePrecedenceList(ArrayList<HashSet<Integer>> reversePrecedenceList) {
        this.reversePrecedenceList = reversePrecedenceList;
    }


    public void calculateImpliedPrecedenceRelations() {
        boolean change = true;
        int c = -1;
        while (change) {
            change = false;
            c++;


            for (int i = 0; i < noDatabases; i++) {
                for (int db : reversePrecedenceList.get(i)) {
                    for (int db2 : reversePrecedenceList.get(db)) {
                        if (!precedenceList.get(db2).contains(i)) {
                            precedenceList.get(db2).add(i);
                            change = true;
                        }
//                    if (!reversePrecedenceList.get(i).contains(db2))
//                        reversePrecedenceList.get(i).add(db2);
                    }
                }
            }
            for (int i = noDatabases - 1; i >= 0; i--) {
                for (int db : precedenceList.get(i)) {
                    for (int db2 : precedenceList.get(db)) {
                        if (!reversePrecedenceList.get(db2).contains(i)) {
                            reversePrecedenceList.get(db2).add(i);
                            change = true;
                        }
                    }
                }
            }
        }
//        System.out.println(c);
    }

    public Database[] getDatabases() {
        return databases;
    }

    public Shift[] getShifts() {
        return shifts;
    }


    public int[] largestKShift(int k) {
        Pair[] sortedShifts = Pair.getSortedPair(capacities, true);

        int[] selectedShifts = new int[k];

        for (int i = 0; i < k; i++) {
            selectedShifts[i] = sortedShifts[i].getKey();
        }

        return selectedShifts;
    }

    public Database[] sortByDeadline() {
        Database[] clone = databases.clone();
        Arrays.sort(clone, new Comparator<Database>() {
            @Override
            public int compare(Database o1, Database o2) {

                int res = o1.getDeadline() - o2.getDeadline();
                if (res != 0)
                    return res;

                return o2.getReleaseTime() - o1.getReleaseTime();

            }
        });

        return clone;
    }

    public Database[] sortByRelaseTime() {
        Database[] clone = databases.clone();
        Arrays.sort(clone, new Comparator<Database>() {
            @Override
            public int compare(Database o1, Database o2) {
                int res =  o1.getReleaseTime() - o2.getReleaseTime();
                if (res != 0)
                    return  res;
                return o1.getDeadline() - o2.getDeadline();
            }
        });

        return clone;
    }

    public int getUsedLb() {
        int totalWeight = 0;
        int totalCapacity = 0;
        int minNeededBins = 0;


        Integer[] caps = new Integer[capacities.length];
        for (int i = 0; i  < capacities.length; i++)
            caps[i] = capacities[i] ;

        Arrays.sort(caps, Collections.reverseOrder());


        for (int i = 0; i < sizes.length; i++)
            totalWeight += sizes[i];

//            int totalWeight2 = totalWeight + leftOverSpace;

//            totalWeight += leftOverSpace;

        for (int i = 0; i < capacities.length; i++) {
            if (totalCapacity < totalWeight) {
                totalCapacity += caps[i];
                minNeededBins++;
            }

        }

        return minNeededBins;
    }

    public int getUsedLb2() {
        int totalWeight = 0;
        int totalCapacity = 0;
        int minNeededShifts = 0;


        Integer[] caps = new Integer[capacities.length];
        for (int i = 0; i  < capacities.length; i++)
            caps[i] = capacities[i] ;

        Arrays.sort(caps, Collections.reverseOrder());

        Integer[] weights = new Integer[sizes.length];
        for (int i = 0; i  < sizes.length; i++)
            weights[i] = sizes[i] ;

        Arrays.sort(weights, Collections.reverseOrder());

        int minWeightIdx = weights.length - 1;
        int minWeight = weights[minWeightIdx];

        int maxCapacity = caps[0];

        int idx = 0;
        int extraShifts = 0;

        boolean[] isShiftUsed = new boolean[capacities.length];

        while (weights[idx] + minWeight > maxCapacity) {
            if (weights[idx] + minWeight > maxCapacity) {
                for (int i = caps.length - 1; i >= 0; i--) {
                    if (!isShiftUsed[i] && caps[i] >= weights[idx]) {
                        isShiftUsed[i] = true;
                        break;
                    }
                }
            } else {
                for (int i = caps.length - 1; i >= 0; i--) {
                    if (!isShiftUsed[i] && caps[i] >= weights[idx] + minWeight) {
                        isShiftUsed[i] = true;
                        break;
                    }
                }
                minWeight = weights[--minWeightIdx];

            }

            maxCapacity = 0;

            for (int i = 0; i < capacities.length; i++) {
                if (!isShiftUsed[i] && caps[i] > maxCapacity) {
                    maxCapacity = caps[i];
                    break;
                }
            }
            extraShifts++;
            idx++;
        }

        for (int i = idx; i < weights.length; i++)
            totalWeight += weights[i];

        for (int i = 0; i < caps.length; i++) {
            if (!isShiftUsed[i] && totalCapacity < totalWeight) {
                totalCapacity += caps[i];
                minNeededShifts++;
            }

        }

        return minNeededShifts + extraShifts;
    }




    public double getBestEfficiency() {
        int totalWeight = 0;
        int totalCapacity = 0;
        int minNeededBins = 0;


        Integer[] caps = new Integer[capacities.length];
        for (int i = 0; i  < capacities.length; i++)
            caps[i] = capacities[i] ;

        Arrays.sort(caps, Collections.reverseOrder());


        for (int i = 0; i < sizes.length; i++)
            totalWeight += sizes[i];

        for (int i = 0; i < capacities.length; i++) {
            if (totalCapacity < totalWeight) {
                totalCapacity += caps[i];
                minNeededBins++;
            }

        }

        double efficieny = 0;
        for (int i = 0; i < minNeededBins - 1; i++)
            efficieny += ((double) caps[i]) / caps[0];

        efficieny += ((double)(totalWeight - totalCapacity + caps[minNeededBins - 1])) / caps[0];

        return efficieny / minNeededBins;

    }

}

import java.util.*;

public class Pair implements  Comparable{
    private int key;
    private int value;

    public Pair(int key, int value) {
        this.key = key;
        this.value = value;
    }

    public int getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Pair))
            return -1;
        Pair other = (Pair) o;
        return this.value - other.value;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof  Pair))
            return false;
        Pair other = (Pair) o;
//        return this.value == other.value && this.key == other.key;
        return  this.key == other.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    public static Pair[] getSortedPair(int[] array, boolean isReverse) {
        Pair[] arrayPair = new Pair[array.length];
        for (int i = 0; i < array.length; i++)
            arrayPair[i] = new Pair(i, array[i]);

        if (isReverse)
            Arrays.sort(arrayPair, Collections.reverseOrder());
        else
            Arrays.sort(arrayPair);
        return arrayPair;
    }

    public static Pair[] getSortedPair(ArrayList<Integer> key, int[] values, boolean isReverse) {
        Pair[] arrayPair = new Pair[key.size()];
        for (int i = 0; i < arrayPair.length; i++)
            arrayPair[i] = new Pair(key.get(i), values[key.get(i)]);

        if (isReverse)
            Arrays.sort(arrayPair, Collections.reverseOrder());
        else
            Arrays.sort(arrayPair);
        return arrayPair;
    }


    public static Pair[] getPairArray(int[] array) {
        Pair[] arrayPair = new Pair[array.length];
        for (int i = 0; i < array.length; i++)
            arrayPair[i] = new Pair(i, array[i]);
        return arrayPair;
    }

    public static Pair[] sortByCommonCost(int startIndex, Pair[] dbCostPairs, ArrayList<HashSet<Integer>> usedAppsByDB, int[] costs, HashSet<Integer> appSet) {
        Pair[] sorted = new Pair[dbCostPairs.length - startIndex];
        for (int i = 0; i < sorted.length; i++) {
            int db = dbCostPairs[i+startIndex].getKey();
            int commonCost = commonCost(usedAppsByDB.get(db), appSet, costs);
            sorted[i] = new Pair(db, commonCost);
        }

        Arrays.sort(sorted, Collections.reverseOrder());
        return sorted;
    }

    public static int commonCost(HashSet<Integer> apps, HashSet<Integer> appSet, int[] costs) {
        int commonCost = 0;
        for (int app : apps)
            if (appSet.contains(app))
                commonCost += costs[app];

        return commonCost;
    }

    public static int commonCost(SolutionCost solutionCost, int shift, int database) {
        ArrayList<HashSet<Integer>> assignments = solutionCost.getAssignments();
        ArrayList<HashSet<Integer>> usedAppsByDB = solutionCost.getInstance().getUsedAppsByDB();
        int[] costs = solutionCost.getInstance().getCosts();
        HashSet<Integer> appsInShift = new HashSet<>();
        for (int db : assignments.get(shift))
            appsInShift.addAll(usedAppsByDB.get(db));

        int commonCost = 0;
        for (int app : usedAppsByDB.get(database)) {
            if (appsInShift.contains(app))
                commonCost += costs[app];
        }

        return commonCost;
    }


    @Override
    public String toString() {
        return "key: " + key + " value: " + value;
    }
}

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DataMigrationInstanceGenerator {
    public static Random generator = new Random(0);

    public static DataMigrationInstanceCost generateCostInstance(int noDB, int noApp, int noShift, int dbSizeLower, int dbSizeUpper,
                                                                 int capacityLower, int capacityUpper, int costLower, int costUpper,
                                                                 double appDBprob) {
        int[] sizes = new int[noDB];
        if (dbSizeLower == dbSizeUpper) {
            for (int i = 0; i < noDB; i++)
                sizes[i] = dbSizeLower;
        } else {
            for (int i = 0; i < noDB; i++)
                sizes[i] = generator.nextInt(dbSizeUpper-dbSizeLower)+dbSizeLower;
        }

        int[] capacities = new int[noShift];

        if (capacityLower == capacityUpper) {
            for (int i = 0; i < noShift; i++)
                capacities[i] = capacityLower;
        } else {
            for (int i = 0; i < noShift; i++)
                capacities[i] = generator.nextInt(capacityUpper-capacityLower)+capacityLower;
        }

        int[] costs = new int[noApp];

        if (costLower == costUpper) {
            for (int i = 0; i < noApp; i++)
                costs[i] = costLower;
        } else {
            for (int i = 0; i < noApp; i++)
                costs[i] = generator.nextInt(costUpper-costLower)+costLower;
        }

        short[][] interrelations = new short[noDB][noApp];
        for (int j = 0; j < noApp; j++) {
            for (int i = 0; i < noDB; i++) {
                double random = generator.nextDouble();
                if (random < appDBprob)
                    interrelations[i][j] = 1;
            }
        }
        return new DataMigrationInstanceCost(sizes, capacities, costs, interrelations);
    }

    


    public static void writeMinCostInstance(DataMigrationInstanceCost instance, String fileName) {
        try {
            Path file = Paths.get(fileName);
            List<String> lines = new ArrayList<>();
            String numbers = "";
            int noDatabases = instance.getSizes().length;
            int noShifts = instance.getCapacities().length;
            int noApp = instance.getCosts().length;
            numbers = numbers + noDatabases + " " + noShifts + " " + noApp;
            int[] sizes = instance.getSizes();
            String strSizes = Arrays.toString(sizes).replace("[", "").replace("]","").replace(",", "");
            int[] capacities = instance.getCapacities();
            String strCapacities = Arrays.toString(capacities).replace("[", "").replace("]","").replace(",", "");
            int[] costs = instance.getCosts();
            String strCosts = Arrays.toString(costs).replace("[", "").replace("]", "").replace(",", "");

            lines.add(numbers);
            lines.add(strSizes);
            lines.add(strCapacities);
            lines.add(strCosts);

            short[][] interrelations = instance.getInterrelations();
            for (short[] arr : interrelations ) {
                String relation = Arrays.toString(arr).replace("[", "").replace("]", "").replace(",", "");
                lines.add(relation);
            }

            Files.write(file,lines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeMinCostInstancehMetis(DataMigrationInstanceCost instance, String fileName) {
        try {
            Path file = Paths.get(fileName);
            List<String> lines = new ArrayList<>();
            int noDatabases = instance.getSizes().length;
            int noApp = instance.getCosts().length;
            boolean isNodeWeighted = false;
            int[] sizes = instance.getSizes();
            for (int size : sizes)
                if (size != 1) {
                    isNodeWeighted = true;
                    break;
                }



            int[] costs = instance.getCosts();

            boolean isEdgeWeighted = false;
            for (int cost : costs)
                if (cost != 1) {
                    isEdgeWeighted = true;
                    break;
                }
            String firstLine = noApp + " " + noDatabases;
            if (isEdgeWeighted && isNodeWeighted)
                firstLine = firstLine + " " + "11";
            else if (isEdgeWeighted)
                firstLine = firstLine + " " + "1";
            else if (isNodeWeighted)
                firstLine = firstLine + " " + "10";

            lines.add(firstLine);

            short[][] interrelations = instance.getInterrelations();
            for (int app = 0; app < noApp; app++) {
                String line = "";
                for (int db = 0; db < noDatabases; db++) {
                    if (interrelations[db][app] != 0)
                        line = line + " " + (db + 1);
                }
                if (isEdgeWeighted)
                    line = costs[app] + line;
                line = line.trim();
                lines.add(line);
            }

            if (isNodeWeighted) {
                for (int db = 0 ; db < noDatabases; db++)
                    lines.add(""+ sizes[db]);
            }



            Files.write(file,lines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeMinCostInstancehPaToH(DataMigrationInstanceCost instance, String fileName) {
        try {
            Path file = Paths.get(fileName);
            List<String> lines = new ArrayList<>();
            int noDatabases = instance.getSizes().length;
            int noApp = instance.getCosts().length;
            short[][] interrelations = instance.getInterrelations();
            boolean isNodeWeighted = false;
            int[] sizes = instance.getSizes();
            for (int size : sizes)
                if (size != 1) {
                    isNodeWeighted = true;
                    break;
                }



            int[] costs = instance.getCosts();

            boolean isEdgeWeighted = false;
            for (int cost : costs)
                if (cost != 1) {
                    isEdgeWeighted = true;
                    break;
                }

            int totalPinSize = 0;
            for (int app = 0; app < noApp; app++) {
                for (int db = 0; db < noDatabases; db++)
                    if (interrelations[db][app] != 0)
                        totalPinSize += 1;
            }


            String firstLine = "0" + " " +  noDatabases + " " + noApp+  " " + totalPinSize;
            if (isEdgeWeighted && isNodeWeighted)
                firstLine = firstLine + " " + "3";
            else if (isEdgeWeighted)
                firstLine = firstLine + " " + "2";
            else if (isNodeWeighted)
                firstLine = firstLine + " " + "1";

            lines.add(firstLine);


            for (int app = 0; app < noApp; app++) {
                String line = "";
                for (int db = 0; db < noDatabases; db++) {
                    if (interrelations[db][app] != 0)
                        line = line + " " + db;
                }
                if (isEdgeWeighted)
                    line = costs[app] + line;
                line = line.trim();
                lines.add(line);
            }


            if (isNodeWeighted) {
                String line = "";
                for (int db = 0 ; db < noDatabases; db++)
                    line = line + " " + sizes[db];

                line = line.trim();
                lines.add(line);
            }



            Files.write(file,lines);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void writeMinShiftInstance(DataMigrationInstance instance, String fileName) {
//        PrintWriter fileOutputStream;
        try {

            Path file = Paths.get(fileName);
            List<String> lines = new ArrayList<>();
//            Writer writer = new BufferedWriter(new OutputStreamWriter(
//                    new FileOutputStream(fileName), "utf-8"));
//            fileOutputStream = new PrintWriter(fileName,"UTF-8");
            String numbers = "";
            int noDatabases = instance.noDatabases;
            int noShifts = instance.noShifts;
            numbers = numbers + noDatabases + " " + noShifts;
            int[] sizes = instance.getSizes();
            String strSizes = Arrays.toString(sizes).replace("[", "").replace("]","").replace(",", "");
            int[] capacities = instance.getCapacities();
            String strCapacities = Arrays.toString(capacities).replace("[", "").replace("]","").replace(",", "");

            lines.add(numbers);
            lines.add(strSizes);
            lines.add(strCapacities);
//            writer.write(numbers +"\n");
//            writer.write(strSizes+ "\n");
//            writer.write(strCapacities);
//            writer.close();
            Files.write(file,lines);
//            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
    public static DataMigrationInstanceCost readMinCostInstance(String fileName) {
        try {
            Path file = Paths.get(fileName);
            List<String> strings = Files.readAllLines(file);
            String numbers = strings.get(0);
            String[] numbersParsed = numbers.split(" ");
            int noDB = Integer.parseInt(numbersParsed[0]);
            int noShift = Integer.parseInt(numbersParsed[1]);
            int noApp = Integer.parseInt(numbersParsed[2]);
            String[] sizesStr = strings.get(1).split(" ");
            int[] sizes = new int[sizesStr.length];
            for (int i = 0; i < sizes.length; i++)
                sizes[i] = Integer.parseInt(sizesStr[i]);

            String[] capacitiesStr = strings.get(2).split(" ");
            int[] capacities = new int[capacitiesStr.length];
            for (int i = 0; i < capacities.length; i++)
                capacities[i] = Integer.parseInt(capacitiesStr[i]);

            String[] costsStr = strings.get(3).split(" ");
            int[] costs = new int[costsStr.length];
            for (int i = 0; i < costs.length; i++)
                costs[i] = Integer.parseInt(costsStr[i]);


            short[][] inter = new short[noDB][noApp];


            for (int j = 0; j < noApp; j++) {
                String[] split = strings.get(4 + j).split(" ");
                for (int i = 0; i < split.length; i++)
                    inter[i][j] = Short.parseShort(split[i]);
            }

            return new DataMigrationInstanceCost(sizes,capacities, costs,inter);
//            return null;
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static DataMigrationInstanceCost readMinCostInstancehMetis(String fileName, int k, double epsilon) {
        try {
            Path file = Paths.get(fileName);
            List<String> lines = Files.readAllLines(file);
            String numbers = lines.get(0);
            String[] numbersParsed = numbers.split(" ");
            int noApp = Integer.parseInt(numbersParsed[0]);
            int noDB = Integer.parseInt(numbersParsed[1]);
            short[][] inter = new short[noDB][noApp];

            for (int i = 1; i < lines.size(); i++) {
                for (String n : lines.get(i).split(" ")) {
                    int node = Integer.parseInt(n) - 1;
                    inter[node][i - 1] = 1;
                }
            }

            int[] sizes = new int[noDB];
            Arrays.fill(sizes,1);
            int[] costs = new int[noApp];
            Arrays.fill(costs, 1);


            int[] capacities = new int[k];
            for (int i = 0; i < capacities.length; i++)
                capacities[i] = (int) Math.ceil(noDB * (1+epsilon) / k);






            return new DataMigrationInstanceCost(sizes,capacities, costs,inter);
//            return null;
        }  catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    
}

package Classifir;

import static Classifir.Kmeans2.Silhouette;
import Preprocessing.OutPut;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Double.max;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.TreeMap;

public class Sequential_kmeans {

    List<Double> centroidID = new LinkedList<>();
    static double Silhouette_means = 0.0, Silhouette_seads = 0.0, MeanDistance = 0.0, meansCorrelation = 0.0, MeanKL = 0.0,
            Silhouette = 0.0, difDseads1 = 0.0, difDseads2 = 0.0, avaDifDseads = 0.0;
    static List<String> featuresLinkedList = new LinkedList<>();
    static List<Double[]> documentDoubleList = new ArrayList<>();
    Map<String, Double> documents = new LinkedHashMap<>();
    List<String> docs = new LinkedList<>();
    boolean stopClustring;
    static List<LinkedList<Double[]>> groups;
    List<Double[]> centroidsList;
    List<Double[]> oldcentroidsList;
    List<Double[]> seads;
    boolean ensampl = false;
    static int[] INDX;
    int iteratinos = 0;
    public String results = "accuracy	precision	recall	f_measure	iterations	Time";
    public String results_latex = "accuracy	precision	recall	f_measure	iterations	Time";
    double positivityk = 0.0, actualPositivityk = 0.0, balancek = 0.0;
    double negativityk = 0.0, actualNegativityk = 0.0;
    public List<String> docsEnsample = new LinkedList<>();
    List<Double[]> groupOne = new LinkedList<>();
    List<Double[]> groupTwo = new LinkedList<>();
    Map<Double, Double> groupOneDists = new LinkedHashMap<>();
    Map<Double, Double> groupTwoDists = new LinkedHashMap<>();
    String[] means = new String[2];

    public void readingMeans(String fileName) {
        String[] path = fileName.split("\\\\");
        try (BufferedReader inputstream = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\Means\\POW_R\\" + path[path.length - 1]))) {
            String line;
            while ((line = inputstream.readLine()) != null) {
                String[] values = line.split("\t");
                if (values[0].equalsIgnoreCase("groupOne")) {
                    means[0] = line;
                }
                if (values[0].equalsIgnoreCase("groupTwo")) {
                    means[1] = line;
                }
            }

        } catch (Exception e) {
            e.getMessage();
        }
        docs.add(means[0]);
        docs.add(means[1]);
    }

    public int reading_docSize() {
        int size = 0;
        try (BufferedReader read = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\IDFdocSize\\IDFdocSize.txt"))) {
            String line;
            while ((line = read.readLine()) != null) {
                size = Integer.valueOf(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return size;
    }

    public void updateMeans(List<LinkedList<Double[]>> docGroups, String filename, Map<String, Double> docs) throws IOException {
        String[] x = filename.split("\\\\");
        List<Double[]> gruop1 = new LinkedList<>();
        List<Double[]> gruop2 = new LinkedList<>();
        System.out.println("docGroups.get(0).get(0).length  " + docGroups.get(0).get(0).length);
        Double[] currentMean1 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] currentMean2 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] oldMean1 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] oldMean2 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] updatedMean1;
        updatedMean1 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] updatedMean2 = new Double[docGroups.get(0).get(0).length - 1];
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            gruop1.add(docGroups.get(0).get(i));
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            gruop2.add(docGroups.get(1).get(i));
        }
        currentMean1 = average((LinkedList<Double[]>) gruop1, docs);
        currentMean2 = average((LinkedList<Double[]>) gruop2, docs);

        String[] values1 = means[0].split("\t");
        String[] values2 = means[1].split("\t");

        int oldMeans1Index = 0, oldMeans2Index = 0;
        for (int i = 1; i < values1.length; i++) {
            oldMean1[oldMeans1Index] = Double.valueOf(values1[i]);
            oldMeans1Index++;
        }
        for (int i = 1; i < values2.length; i++) {
            oldMean2[oldMeans2Index] = Double.valueOf(values2[i]);
            oldMeans2Index++;
        }
        for (int i = 0; i < currentMean1.length; i++) {
            // updatedMean1[i] = oldMean1[i] + (reading_docSize() / (currentMean1[i] - oldMean1[i]));// 0- This does not work
            // updatedMean1[i] = oldMean1[i] + (currentMean1[i] - oldMean1[i]); // 1- This works
            updatedMean1[i] = currentMean1[i]; // 2- 2 = 1

        }
        for (int i = 0; i < currentMean2.length; i++) {
            //  updatedMean2[i] = oldMean2[i] + (reading_docSize() / (currentMean2[i] - oldMean2[i])); // This does not work
            //  updatedMean2[i] = oldMean2[i] + (currentMean2[i] - oldMean2[i]); //This works
            updatedMean2[i] = currentMean2[i]; //

        }
        File directory = new File("OUTPUT\\Means\\POW_R\\");
        directory.mkdirs();
        BufferedWriter newFileSWN = null;
        try {
            newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\Means\\POW_R\\" + x[x.length - 1]));
            // newFileSWN.write(G1pol + "\t");
            newFileSWN.write("groupOne" + "\t");

            for (int r = 0; r < updatedMean1.length; r++) {
                newFileSWN.write(updatedMean1[r] + "\t");
            }
            newFileSWN.newLine();

            // newFileSWN.write(G2pol + "\t");
            newFileSWN.write("groupTwo" + "\t");
            for (int r = 0; r < updatedMean2.length; r++) {
                newFileSWN.write(updatedMean2[r] + "\t");
            }
            newFileSWN.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file");
        } finally {
            if (newFileSWN != null) {
                newFileSWN.close();
            } else {
                System.out.println("gruopsMeans");
            }
        }
    }

    public void sequential_kmeansImplementation(String docPath, int k, int windowNum, boolean addCet, int iterNo, String distanceType) throws IOException {
        stopClustring = false;
        readingFeatusrs(docPath);
        readDocFileTolist(docPath);
        groups = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            groups.add(new LinkedList<>());
        }
        centroidsList = new LinkedList<>();
        oldcentroidsList = new LinkedList<>();
        seads = new LinkedList<>();
        boolean noFirstPoint = true;
        int iter = 1;
        Double docReference = 0.0;
        if (iter == 1) {
            if (windowNum == 1) {
                for (String FirstPionts : docs) {
                    int x = 0;
                    docReference++;
                    Double[] docRow = new Double[featuresLinkedList.size()];
                    Double[] cet = new Double[featuresLinkedList.size()];
                    cet[0] = docReference;
                    Double[] cetRow = new Double[featuresLinkedList.size() - 1];
                    String[] values = FirstPionts.split("\t");
                    if (values[0].equals("point-pos") || values[0].equals("point-neg")) {
                        noFirstPoint = false;
                        int cetIndex = 0;
                        for (int i = 1; i < values.length; i++) {
                            cetRow[cetIndex] = Double.parseDouble(values[i]);
                            cet[i] = Double.parseDouble(values[i]);
                            cetIndex++;
                        }
                        centroidID.add(docReference);
                        x++;
                        centroidsList.add(cetRow);
                        seads.add(cet);
                    }
                    docRow[0] = docReference;
                    for (int i = 1; i < values.length; i++) {
                        docRow[i] = Double.parseDouble(values[i]);
                    }
                    documentDoubleList.add(docRow);
                    documents.put(values[0], docReference);
                }
                if (noFirstPoint == true) {
                    throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers " + centroidsList.size());
                }
                if (centroidsList.size() != k) {
                    throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers  " + centroidsList.size());
                }
            } else {
                if (addCet == true) {
                    readingMeans(docPath);
                    Double[] cetRowNeg = new Double[featuresLinkedList.size() - 1];
                    Double[] cetRowPos = new Double[featuresLinkedList.size() - 1];

                    /////////////
                    for (String FirstPionts : docs) {

                        String[] values = FirstPionts.split("\t");

                        if (values[0].equals("point-neg")) {
                            int cetIndex = 0;
                            for (int i = 1; i < values.length; i++) {
                                cetRowNeg[cetIndex] = Double.parseDouble(values[i]);
                                cetIndex++;
                            }
                            seads.add(cetRowNeg);
                        } else if (values[0].equals("point-pos")) {
                            int cetIndex = 0;
                            for (int i = 1; i < values.length; i++) {
                                cetRowPos[cetIndex] = Double.parseDouble(values[i]);
                                cetIndex++;
                            }
                        }
                        seads.add(cetRowPos);
                    }

                    ////////////
                    for (String FirstPionts : docs) {
                        docReference++;
                        Double[] docRow = new Double[featuresLinkedList.size()];
                        Double[] cet = new Double[featuresLinkedList.size()];
                        cet[0] = docReference;
                        Double[] cetRow = new Double[featuresLinkedList.size() - 1];
                        String[] values = FirstPionts.split("\t");
                        if (values[0].equals("groupOne")) {
                            noFirstPoint = false;
                            int cetIndex = 0;
                            for (int i = 1; i < values.length; i++) {
                                 cetRow[cetIndex] = (Double.parseDouble(values[i]) + cetRowNeg[cetIndex]) / 2;
                                cetIndex++;
                            }
                            centroidID.add(docReference);
                            centroidsList.add(cetRow);

                        } else if (values[0].equals("groupTwo")) {
                            noFirstPoint = false;
                            int cetIndex = 0;
                            for (int i = 1; i < values.length; i++) {
                                cetRow[cetIndex] = (Double.parseDouble(values[i]) + cetRowPos[cetIndex]) / 2;
                                cetIndex++;
                            }
                            centroidID.add(docReference);
                            centroidsList.add(cetRow);

                        } else {
                            docRow[0] = docReference;
                            for (int i = 1; i < values.length; i++) {
                                docRow[i] = Double.parseDouble(values[i]);
                            }
                            documentDoubleList.add(docRow);
                            documents.put(values[0], docReference);
                        }
                    }
                    if (noFirstPoint == true) {
                        throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers  " + centroidsList.size());
                    }
                    if (centroidsList.size() != k) {
                        throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers  " + centroidsList.size());
                    }
                } else {
                    readingMeans(docPath);
                    for (String FirstPionts : docs) {
                        docReference++;
                        Double[] docRow = new Double[featuresLinkedList.size()];
                        Double[] cet = new Double[featuresLinkedList.size()];
                        cet[0] = docReference;
                        Double[] cetRow = new Double[featuresLinkedList.size() - 1];
                        String[] values = FirstPionts.split("\t");
                        if (values[0].equals("groupOne") || values[0].equals("groupTwo")) {
                            noFirstPoint = false;
                            int cetIndex = 0;
                            for (int i = 1; i < values.length; i++) {
                                cetRow[cetIndex] = Double.parseDouble(values[i]);
                                cet[i] = Double.parseDouble(values[i]);
                                cetIndex++;
                            }
                            centroidID.add(docReference);
                            centroidsList.add(cetRow);
                            seads.add(cet);
                        } else {
                            docRow[0] = docReference;
                            for (int i = 1; i < values.length; i++) {
                                docRow[i] = Double.parseDouble(values[i]);
                            }
                            documentDoubleList.add(docRow);
                            documents.put(values[0], docReference);
                        }
                    }
                    if (noFirstPoint == true) {
                        throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers  " + centroidsList.size());
                    }
                    if (centroidsList.size() != k) {
                        throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers  " + centroidsList.size());
                    }
                }
            }
        }
        do {
            double sim = 0.0;
            for (Double[] doc : documentDoubleList) {
                List<Double> rowDistance = new LinkedList<>();
                for (Double[] centeroid : centroidsList) {
                    if (distanceType == "cosin") {
                        sim = distance(doc, centeroid, "pm");
                        rowDistance.add(sim);
                    }
                    if (distanceType == "euclidean") {
                        sim = euclidean(doc, centeroid, "pm");
                        rowDistance.add(sim);
                    }
                    if (distanceType == "cityblock") {
                        sim = cityblock(doc, centeroid, "pm");
                        rowDistance.add(sim);
                    }

                }
                groups.get(rowDistance.indexOf(Collections.min(rowDistance))).add(doc);
                rowDistance.removeAll(rowDistance);
            }
            for (int i = 0; i < k; i++) {
                if (iter == 1) {
                    oldcentroidsList.add(centroidsList.get(i));
                } else {
                    oldcentroidsList.set(i, centroidsList.get(i));
                }
                if (!groups.get(i).isEmpty()) {
                    centroidsList.set(i, average(groups.get(i), documents));
                }
            }
            int counterIfEquals = 0;
            for (Double[] centroid : centroidsList) {
                for (Double[] oldcentroid : oldcentroidsList) {
                    if (Arrays.deepEquals(centroid, oldcentroid)) {
                        counterIfEquals++;
                    }
                }
            }
            if (counterIfEquals == centroidsList.size() || (iter == iterNo)) {
                stopClustring = true;
            }

            //if((windowNum>1) && (iter == 1)){stopClustring = true;}
            if (stopClustring == false) {
                for (int i = 0; i < groups.size(); i++) {
                    groups.get(i).removeAll(groups.get(i));
                }
            }
            if (stopClustring == true) {
                fanilOutbut(groups, documents, docPath, windowNum, iterNo);
                documentDoubleList.removeAll(documentDoubleList);
                featuresLinkedList.removeAll(featuresLinkedList);
                documents.remove(documents);
                docs.removeAll(docs);
                centroidsList.removeAll(centroidsList);
                oldcentroidsList.removeAll(oldcentroidsList);
                centroidID.removeAll(centroidID);
                seads.removeAll(seads);
            }
            iter++;
        } while (stopClustring == false);
        iteratinos = iter;
    }

    public void group(List<LinkedList<Double[]>> docGroups, String filename, Map<String, Double> documents, int winNumber,boolean groupOneIsNEG, boolean groupOneIsPOS, int iterNo) {
        String s[] = filename.split("\\\\");
        String fName = s[s.length - 1].substring(0, s[s.length - 1].length() - 4);
        File dir = new File("OUTPUT\\ProcessedDoc\\groups\\" + winNumber + "\\"+ iterNo+"\\" + fName + "\\");
        dir.mkdirs();

        for (int i = 0; i < docGroups.get(0).size(); i++) {
            groupOne.add(docGroups.get(0).get(i));
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            groupTwo.add(docGroups.get(1).get(i));
        }
        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\groups\\" + winNumber +"\\"+ iterNo+"\\" + fName + "\\g1.txt"));
                BufferedWriter newFileSWN2 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\groups\\" + winNumber +"\\"+ iterNo +"\\" + fName + "\\g2.txt"));) {
            for (int j = 0; j < groupOne.size(); j++) {
                for (Double v : groupOne.get(j)) {
                    newFileSWN.write(v.toString() + "\t");
                }
                newFileSWN.newLine();
            }
            for (int h = 0; h < groupTwo.size(); h++) {
                for (Double v : groupTwo.get(h)) {
                    newFileSWN2.write(v.toString() + "\t");
                }
                newFileSWN2.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error opening the file");
            e.getMessage();
        }

        try (BufferedWriter newFileSWN2 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\groups\\" + winNumber + "\\"+iterNo+"\\" + fName +"\\docs.txt"));) {
            for (String h : documents.keySet()) {
                newFileSWN2.write(h + "\t" + documents.get(h).toString());
                newFileSWN2.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error opening the file");
            e.getMessage();
        }
        String G1Polarity = null, G2Polarity = null;
        if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
//group 1 --> POS : group 2-->NEG
            G1Polarity = "POS";
            G2Polarity = "NEG";
        } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
//group 2 --> POS : group 1 --> NEG
            G1Polarity = "NEG";
            G2Polarity = "POS";
        } else {
            G1Polarity = "INA"; //inaccurate
            G2Polarity = "INA";
        }

        try (BufferedWriter newFileSWN2 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\groups\\" + winNumber + "\\"+iterNo+"\\" + fName +"\\inf.txt"));) {
            newFileSWN2.write(G1Polarity + "\t" + G2Polarity + "\t" + iterNo);
            newFileSWN2.newLine();
        } catch (IOException e) {
            System.out.println("Error opening the file");
            e.getMessage();
        }
    }

    public Double[] average(LinkedList<Double[]> list, Map<String, Double> docs) {
        Double[] mean = new Double[featuresLinkedList.size() - 1];
        Arrays.fill(mean, 0.0);
        int indx = 0;
        if (list.isEmpty()) {
            list.add(mean);
            System.out.println("THE METHOD AVERAGE, LIST IS EMPTY :: THEREFORW WE ADD THE ZERO MEAN TO IT ........ ");
        }
        Double[][] go = new Double[list.size()][list.get(0).length];
        double colSumation = 0.0;
        for (Double[] rowd : list) {
            if (rowd[0] != docs.get("point-pos") || rowd[0] != docs.get("point-neg")) {
                go[indx] = rowd;
                indx++;
            }
        }
        int meanIndex = 0;
        for (int i = 1; i < go[0].length; i++) {
            for (int g = 0; g < go.length; g++) {
                colSumation = colSumation + go[g][i];
            }
            mean[meanIndex] = round(colSumation / list.size());
            meanIndex++;
            colSumation = 0.0;
        }
        return mean;
    }

    public double groupCorrelation(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs) {
        double r = 0.0;
        Double[] mean1 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] mean2 = new Double[docGroups.get(0).get(0).length - 1];
        List<Double[]> group1 = new LinkedList<>();
        List<Double[]> group2 = new LinkedList<>();
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            group1.add(docGroups.get(0).get(i));
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            group2.add(docGroups.get(1).get(i));
        }
        mean1 = average((LinkedList<Double[]>) group1, docs);
        mean2 = average((LinkedList<Double[]>) group2, docs);
        r = correlation(mean1, mean2, "mm");
        return round(r);
    }

    public void gruopsMeans(List<LinkedList<Double[]>> docGroups, boolean groupOneIsNEG, boolean groupOneIsPOS, boolean accuract, String filename, Map<String, Double> docs) throws IOException {
        String[] x = filename.split("\\\\");
        List<Double[]> gruop1 = new LinkedList<>();
        List<Double[]> gruop2 = new LinkedList<>();
        Double[] mean1 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] mean2 = new Double[docGroups.get(0).get(0).length - 1];

        for (int i = 0; i < docGroups.get(0).size(); i++) {
            if (docGroups.get(0).get(i)[0] != centroidID.get(0) || docGroups.get(0).get(i)[0] != centroidID.get(1)) {
                gruop1.add(docGroups.get(0).get(i));
            }
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            if (docGroups.get(1).get(i)[0] != centroidID.get(0) || docGroups.get(1).get(i)[0] != centroidID.get(1)) {
                gruop2.add(docGroups.get(1).get(i));
            }
        }
        mean1 = average((LinkedList<Double[]>) gruop1, docs);
        mean2 = average((LinkedList<Double[]>) gruop2, docs);
        String G1pol = null, G2pol = null;

        if (accuract == true) {
            if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
                //group 1 --> POS : group 2-->NEG
                G1pol = "POS";
                G2pol = "NEG";
            } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
                //group 2 --> POS : group 1 --> NEG
                G1pol = "NEG";
                G2pol = "POS";
            }
        } else {
            G1pol = "NOT";
            G2pol = "NOT";
        }

        File directory = new File("OUTPUT\\Means\\POW_R\\");
        directory.mkdirs();
        BufferedWriter newFileSWN = null;
        try {
            newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\Means\\POW_R\\" + x[x.length - 1]));
            // newFileSWN.write(G1pol + "\t");
            newFileSWN.write("groupOne" + "\t");

            for (int r = 0; r < mean1.length; r++) {
                newFileSWN.write(mean1[r] + "\t");
            }
            newFileSWN.newLine();

            // newFileSWN.write(G2pol + "\t");
            newFileSWN.write("groupTwo" + "\t");
            for (int r = 0; r < mean2.length; r++) {
                newFileSWN.write(mean2[r] + "\t");
            }
            newFileSWN.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file");
        } finally {
            if (newFileSWN != null) {
                newFileSWN.close();
            } else {
                System.out.println("gruopsMeans");
            }
        }
    }

    private void fanilOutbut(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs, String filename, int windowNum, int iterNo) throws IOException {
        System.out.println("***** clustering ******* ");
        System.out.print(filename);
        System.out.println("");
        List<List<String>> confusingMatrix = new LinkedList<>();
        List<String> posGone = new ArrayList<>();
        List<String> posGtwo = new ArrayList<>();
        List<String> negGone = new ArrayList<>();
        List<String> negGtwo = new ArrayList<>();
        Integer[] xx = new Integer[docGroups.size()];
        HashMap< String, Integer> grouDoc = new HashMap<>();

        grouDoc.remove(grouDoc);
        posGone.removeAll(posGone);
        posGtwo.removeAll(posGtwo);
        negGone.removeAll(negGone);
        negGtwo.removeAll(negGtwo);

        int groupNo = 0;
        for (int x = 0; x > docGroups.size(); x++) {
            confusingMatrix.add(new ArrayList<String>());
        }
        Double[] posPoint = new Double[featuresLinkedList.size()];
        Double[] negPoint = new Double[featuresLinkedList.size()];

        for (int inx = 0; inx < docGroups.size(); inx++) {
            groupNo = inx;
            groupNo++;
            for (int in = 0; in < docGroups.get(inx).size(); in++) {
                for (Map.Entry entry : docs.entrySet()) {
                    if (entry.getValue() == docGroups.get(inx).get(in)[0]) {
                        grouDoc.put(entry.getKey().toString(), groupNo);

                        if (entry.getKey().toString().equalsIgnoreCase("point-pos")) {
                            posPoint = docGroups.get(inx).get(in);
                        }

                        if (entry.getKey().toString().equalsIgnoreCase("point-neg")) {
                            negPoint = docGroups.get(inx).get(in);
                        }
                        xx[inx] = groupNo;
                    }
                }
            }
        }

        boolean groupOneIsPOS = false, groupOneIsNEG = false, accuract = false;
        for (Map.Entry entry : grouDoc.entrySet()) {
            Integer x = 1;
            String[] values = entry.getKey().toString().split("-");
            if ((entry.getValue() == xx[0]) && "pos".equals(values[1])) {
                posGone.add(entry.getKey().toString());
                if (values[0].equalsIgnoreCase("point")) {
                    groupOneIsPOS = true;
                }
            } else if ((entry.getValue() == xx[1]) && "pos".equals(values[1])) {
                posGtwo.add(entry.getKey().toString());

            } else if ((entry.getValue() == xx[0]) && "neg".equals(values[1])) {
                negGone.add(entry.getKey().toString());
                if (values[0].equalsIgnoreCase("point")) {
                    groupOneIsNEG = true;
                }
            } else if ((entry.getValue() == xx[1]) && "neg".equals(values[1])) {
                negGtwo.add(entry.getKey().toString());
            }
        }
        if ((groupOneIsNEG == true) && (groupOneIsPOS == true)) {

        } else if ((groupOneIsNEG == false) && (groupOneIsPOS == false)) {

        } else if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
            accuract = true;

        } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
            accuract = true;
        }

        double posGoneSize = 0.0, negGtwoSize = 0.0, posGtwoSize = 0.0, negGoneSize = 0.0;
        if (!posGone.isEmpty()) {
            posGoneSize = posGone.size();
        }
        negGtwoSize = negGtwo.size();
        posGtwoSize = posGtwo.size();
        negGoneSize = negGone.size();

        double grouDocSize = posGoneSize + negGtwoSize + posGtwoSize + negGoneSize;
        double accuracy = 0.0;
        double precision = 0.0;
        double recall = 0.0;
        double f_measure = 0.0;
        double r1 = 0.0;
        double r2 = 0.0;
        double r = 0.0;
        confusingMatrix.add(posGone);
        confusingMatrix.add(negGtwo);
        confusingMatrix.add(posGtwo);
        confusingMatrix.add(negGone);

        if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
            //correlation

            r1 = correlation(posPoint, average(docGroups.get(0), docs), "pm");
            r2 = correlation(negPoint, average(docGroups.get(1), docs), "pm");
            r = (r1 + r2) / 2;
            //   correlation(,average(docGroups.get(0),docs),"pm");
            System.out.println("group 1 --> POS : group 2-->NEG ");
            accuracy = ((posGoneSize + negGtwoSize) / grouDocSize);
            precision = posGoneSize / (posGoneSize + negGoneSize);
            recall = posGoneSize / (posGoneSize + posGtwoSize);
            f_measure = (2 * precision * recall) / (precision + recall);
        } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
//correlation
            r1 = correlation(negPoint, average(docGroups.get(0), docs), "pm");
            r2 = correlation(posPoint, average(docGroups.get(1), docs), "pm");
            r = (r1 + r2) / 2;
            System.out.println("group 2 --> POS : group 1 --> NEG");
            accuracy = ((posGtwoSize + negGoneSize) / grouDocSize);
            precision = posGtwoSize / (posGtwoSize + negGtwoSize);
            recall = posGtwoSize / (posGtwoSize + posGoneSize);
            f_measure = (2 * precision * recall) / (precision + recall);
        }
        if (windowNum == 1) {
            gruopsMeans(docGroups, groupOneIsNEG, groupOneIsPOS, accuract, filename, docs);
        } else {
            updateMeans(docGroups, filename, docs);
        }

        System.out.println("Silhouette" + "\t"
                + "Silhouette_means" + "\t"
                + "Silhouette_seads" + "\t"
                + "MeanDistance" + "\t"
                + "meansCorrelation" + "\t"
                + "MeanKL" + "\t"
                + "difDseads1) " + "\t"
                + "difDseads2" + "\t"
                + "avaDifDseads" + "\t"
                + "actualPositivity" + "\t"
                + "actualNegativity" + "\t"
                + "balance" + "\t"
                + "accuracy" + "\t"
                + "precision" + "\t"
                + "recall" + "\t"
                + "f_measure" + "\t"
                + "iterations" + "\t"
                + "Time");

        if (Double.isNaN(accuracy)) {
            accuracy = 0.0;
        }
        if (Double.isNaN(precision)) {
            precision = 0.0;
        }
        if (Double.isNaN(recall)) {
            recall = 0.0;
        }
        if (Double.isNaN(f_measure)) {
            f_measure = 0.0;
        }
        if (Double.isNaN(Silhouette_means)) {
            Silhouette_means = 0.0;
        }
        Silhouette = Silhouette(docGroups);
        meansCorrelation = groupCorrelation(docGroups, docs);
        actualPositivityk = round((posGone.size() + posGtwo.size()) / grouDocSize);
        actualNegativityk = round((negGone.size() + negGtwo.size()) / grouDocSize);
        balancek = Math.abs(actualPositivityk - actualNegativityk);
        results
                = Double.toString(Silhouette) + "\t"
                + Double.toString(Silhouette_means) + "\t"
                + Double.toString(Silhouette_seads) + "\t"
                + Double.toString(MeanDistance) + "\t"
                + Double.toString(meansCorrelation) + "\t"
                + Double.toString(MeanKL) + "\t"
                + Double.toString(difDseads1) + "\t"
                + Double.toString(difDseads2) + "\t"
                + Double.toString(avaDifDseads) + "\t"
                + Double.toString(actualPositivityk) + "\t"
                + Double.toString(actualNegativityk) + "\t"
                + Double.toString(balancek) + "\t"
                + Double.toString(round(accuracy * 100, 2)) + "\t"
                + Double.toString(round(precision * 100, 2)) + "\t"
                + Double.toString(round(recall * 100, 2)) + "\t"
                + Double.toString(round(f_measure * 100, 2)) + "\t"
                + Integer.toString(iteratinos);

        results_latex
                = Double.toString(Silhouette) + " & "
                + Double.toString(Silhouette_means) + " & "
                + Double.toString(Silhouette_seads) + " & "
                + Double.toString(MeanDistance) + " & "
                + Double.toString(meansCorrelation) + " & "
                + Double.toString(MeanKL) + " & "
                + Double.toString(difDseads1) + "&"
                + Double.toString(difDseads2) + "&"
                + Double.toString(avaDifDseads) + "&"
                + Double.toString(actualPositivityk) + " & "
                + Double.toString(actualNegativityk) + " & "
                + Double.toString(balancek) + " & "
                + Double.toString(round(accuracy * 100, 2)) + " & "
                + Double.toString(round(precision * 100, 2)) + " & "
                + Double.toString(round(recall * 100, 2)) + " & "
                + Double.toString(round(f_measure * 100, 2)) + " & "
                + Integer.toString(iteratinos);

        Map<String, Integer> treeOfGroupIndex = new TreeMap<>();
        INDX = new int[grouDoc.size()];
        for (Map.Entry entry : grouDoc.entrySet()) {
            if (treeOfGroupIndex.size() != grouDoc.size()) {
                treeOfGroupIndex.put(entry.getKey().toString(), (Integer) entry.getValue());
            } else {
                treeOfGroupIndex.replace(entry.getKey().toString(), treeOfGroupIndex.get(entry.getKey().toString()), (Integer) entry.getValue());
            }
        }
        int indx = 0;
        if ((accuract == true)) {
            for (String entr : treeOfGroupIndex.keySet()) {
                INDX[indx] = treeOfGroupIndex.get(entr);
                indx++;
                if (docsEnsample.isEmpty() || docsEnsample.size() < treeOfGroupIndex.size()) {
                    docsEnsample.add(entr);
                }
            }
        } else {
            Arrays.fill(INDX, 0);
        }
        if (r >= -0.25 && r <= 0.25) {
        }
        group(docGroups, filename, docs, windowNum ,groupOneIsNEG, groupOneIsPOS, iterNo);
       // SilhouetteCoefficient(docGroups, docs, filename, groupOneIsNEG, groupOneIsPOS, "cityblock", iterNo); //euclidean cityblock cosin
        // MeanSilhouetteCoefficient(docGroups, docs, filename, groupOneIsNEG, groupOneIsPOS, "cityblock", iterNo);
        //  DistanceMean(docGroups, docs, filename, groupOneIsNEG, groupOneIsPOS, "cityblock", iterNo);

        System.out.println("---------------------------------------");
        System.out.println("Actual pos: " + posGoneSize + "\t" + posGtwoSize);
        System.out.println("Actual neg: " + negGoneSize + "\t" + negGtwoSize);
        System.out.println("****************************************");

        grouDoc.remove(grouDoc);
        posGone.removeAll(posGone);
        posGtwo.removeAll(posGtwo);
        negGone.removeAll(negGone);
        negGtwo.removeAll(negGtwo);
    }

    public double Silhouette(List<LinkedList<Double[]>> docGroups) {
        List<Double> Silhouette_a = new LinkedList<>();
        List<Double> Silhouette_b = new LinkedList<>();
        Silhouette = 0.0;
        double sim = 0.0, a = 0.0, b = 0.0, SumSim = 0.0;
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            groupOne.add(docGroups.get(0).get(i));
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            groupTwo.add(docGroups.get(1).get(i));
        }
        for (int g = 0; g < docGroups.size(); g++) {
            for (int i = 0; i < docGroups.get(g).size(); i++) {
                sim = 0.0;
                a = 0.0;
                SumSim = 0.0;
                for (int j = 0; j < groupOne.size(); j++) {
                    sim = distance(docGroups.get(g).get(i), groupOne.get(j), "pp");
                    SumSim = SumSim + sim;
                }
                a = round(SumSim / groupOne.size());
                sim = 0.0;
                b = 0.0;
                SumSim = 0.0;
                for (int h = 0; h < groupTwo.size(); h++) {
                    sim = distance(docGroups.get(g).get(i), groupTwo.get(h), "pp");
                    SumSim = SumSim + sim;
                }
                b = round(SumSim / groupTwo.size());
                if (g == 0) {
                    Silhouette_a.add(round((b - a) / max(b, a)));
                } else if (g == 1) {
                    Silhouette_b.add(round((a - b) / max(b, a)));
                }
            }
        }
        double sum_Silhouette_a = 0.0, Overall_Silhouette = 0.0;
        int count = 0;
        for (double val : Silhouette_a) {
            sum_Silhouette_a = sum_Silhouette_a + val;
            Overall_Silhouette = Overall_Silhouette + val;
            count++;
        }
        double ava_Silhouette_a = round(sum_Silhouette_a / Silhouette_a.size());
        double sum_Silhouette_b = 0.0;
        for (double val : Silhouette_b) {
            sum_Silhouette_b = sum_Silhouette_b + val;
            Overall_Silhouette = Overall_Silhouette + val;
            count++;
        }
        double ava_Silhouette_b = round(sum_Silhouette_b / Silhouette_b.size());
        Silhouette = round(Overall_Silhouette / count);
        groupOne.removeAll(groupOne);
        groupTwo.removeAll(groupTwo);
        return Silhouette;
    }

    public void MeanSilhouetteCoefficient(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs, String fileName, boolean groupOneIsNEG, boolean groupOneIsPOS, String distanceType, int iterNo) throws IOException {
        Double[] mean1 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] mean2 = new Double[docGroups.get(0).get(0).length - 1];

        List<Double[]> group1 = new LinkedList<>();
        List<Double[]> group2 = new LinkedList<>();
        String G1Polarity = null, G2Polarity = null;
        Map<Double, Double> silhouette_Gone = new LinkedHashMap<>();
        Map<String, Double> silhouette_G1 = new LinkedHashMap<>();

        Map<Double, Double> silhouette_Gtwo = new LinkedHashMap<>();
        Map<String, Double> silhouette_G2 = new LinkedHashMap<>();
        if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
//group 1 --> POS : group 2-->NEG
            G1Polarity = "POS";
            G2Polarity = "NEG";
        } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
//group 2 --> POS : group 1 --> NEG
            G1Polarity = "NEG";
            G2Polarity = "POS";
        } else {
            G1Polarity = "INA"; //inaccurate
            G2Polarity = "INA";
        }

        for (int i = 0; i < docGroups.get(0).size(); i++) {
            group1.add(docGroups.get(0).get(i));
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            group2.add(docGroups.get(1).get(i));
        }
        mean1 = average((LinkedList<Double[]>) group1, docs);
        mean2 = average((LinkedList<Double[]>) group2, docs);
        double r = 0;
        double a = 0.0;
        double b = 0.0;
        double max1 = 0.0;
        Double value = 0.0;
        r = correlation(mean1, mean2, "mm");
        for (Double[] x : group1) {

            if (r >= -0.25 && r <= 0.25) {
                if (distanceType == "cosin") {
                    a = distance(x, mean1, "pm");
                    b = distance(x, mean2, "pm");
                }
                if (distanceType == "euclidean") {
                    a = euclidean(x, mean1, "pm");
                    b = euclidean(x, mean2, "pm");
                }
                if (distanceType == "cityblock") {
                    a = cityblock(x, mean1, "pm");
                    b = cityblock(x, mean2, "pm");
                }

                max1 = max(a, b);
                if (max1 == 0.0) {
                    max1 = 1;
                }
                value = (b - a) / max1;
                silhouette_Gone.put(x[0], value);

            }

        }

        for (Double[] x : group2) {

            if (r >= -0.25 && r <= 0.25) {

                if (distanceType == "cosin") {
                    a = distance(x, mean2, "pm");
                    b = distance(x, mean1, "pm");
                }
                if (distanceType == "euclidean") {
                    a = euclidean(x, mean2, "pm");
                    b = euclidean(x, mean1, "pm");
                }
                if (distanceType == "cityblock") {
                    a = cityblock(x, mean2, "pm");
                    b = cityblock(x, mean1, "pm");
                }

                max1 = max(a, b);
                if (max1 == 0.0) {
                    max1 = 1;
                }
                value = (b - a) / max1;
                silhouette_Gtwo.put(x[0], value);

            }

        }

        for (String key : docs.keySet()) {
            for (Double keyS : silhouette_Gone.keySet()) {
                if (Objects.equals(docs.get(key), keyS)) {
                    silhouette_G1.put(key, silhouette_Gone.get(keyS));
                }
            }
        }
        for (String key : docs.keySet()) {
            for (Double keyS : silhouette_Gtwo.keySet()) {
                if (Objects.equals(docs.get(key), keyS)) {
                    silhouette_G2.put(key, silhouette_Gtwo.get(keyS));
                }
            }
        }
        silhouette_Gtwo.clear();
        silhouette_Gone.clear();
        writeSilhouette(1, silhouette_G1, silhouette_G2, fileName, G1Polarity, G2Polarity, "MeanSilhouetteCoefficient", "larg", iterNo); //for complic
        writeSilhouette(2, silhouette_G1, silhouette_G2, fileName, G1Polarity, G2Polarity, "MeanSilhouetteCoefficient", "larg", iterNo); //for complic
        silhouette_G1.clear();
        silhouette_G2.clear();
    }

    public void DistanceMean(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs, String fileName, boolean groupOneIsNEG, boolean groupOneIsPOS, String distanceType, int iterNo) throws IOException {
        Double[] mean1 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] mean2 = new Double[docGroups.get(0).get(0).length - 1];

        List<Double[]> group1 = new LinkedList<>();
        List<Double[]> group2 = new LinkedList<>();
        String G1Polarity = null, G2Polarity = null;
        Map<Double, Double> silhouette_Gone = new LinkedHashMap<>();
        Map<String, Double> silhouette_G1 = new LinkedHashMap<>();

        Map<Double, Double> silhouette_Gtwo = new LinkedHashMap<>();
        Map<String, Double> silhouette_G2 = new LinkedHashMap<>();
        if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
//group 1 --> POS : group 2-->NEG
            G1Polarity = "POS";
            G2Polarity = "NEG";
        } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
//group 2 --> POS : group 1 --> NEG
            G1Polarity = "NEG";
            G2Polarity = "POS";
        } else {
            G1Polarity = "INA"; //inaccurate
            G2Polarity = "INA";
        }

        for (int i = 0; i < docGroups.get(0).size(); i++) {
            group1.add(docGroups.get(0).get(i));
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            group2.add(docGroups.get(1).get(i));
        }
        mean1 = average((LinkedList<Double[]>) group1, docs);
        mean2 = average((LinkedList<Double[]>) group2, docs);
        double r = 0;
        double a = 0.0;
        double b = 0.0;
        double max1 = 0.0;
        Double value = 0.0;
        r = correlation(mean1, mean2, "mm");
        for (Double[] x : group1) {

            if (r >= -0.25 && r <= 0.25) {
                if (distanceType == "cosin") {
                    silhouette_Gone.put(x[0], distance(x, mean1, "pm"));
                }
                if (distanceType == "euclidean") {
                    silhouette_Gone.put(x[0], euclidean(x, mean1, "pm"));
                }
                if (distanceType == "cityblock") {
                    silhouette_Gone.put(x[0], cityblock(x, mean1, "pm"));
                }
            }

        }

        for (Double[] x : group2) {

            if (r >= -0.25 && r <= 0.25) {
                if (distanceType == "cosin") {
                    silhouette_Gtwo.put(x[0], distance(x, mean2, "pm"));
                }
                if (distanceType == "euclidean") {

                    silhouette_Gtwo.put(x[0], euclidean(x, mean2, "pm"));
                }
                if (distanceType == "cityblock") {
                    silhouette_Gtwo.put(x[0], cityblock(x, mean2, "pm"));

                }
            }

        }

        for (String key : docs.keySet()) {
            for (Double keyS : silhouette_Gone.keySet()) {
                if (Objects.equals(docs.get(key), keyS)) {
                    silhouette_G1.put(key, silhouette_Gone.get(keyS));
                }
            }
        }
        for (String key : docs.keySet()) {
            for (Double keyS : silhouette_Gtwo.keySet()) {
                if (Objects.equals(docs.get(key), keyS)) {
                    silhouette_G2.put(key, silhouette_Gtwo.get(keyS));
                }
            }
        }
        silhouette_Gtwo.clear();
        silhouette_Gone.clear();
        writeSilhouette(1, silhouette_G1, silhouette_G2, fileName, G1Polarity, G2Polarity, "DistanceMean", "small", iterNo);
        writeSilhouette(2, silhouette_G1, silhouette_G2, fileName, G1Polarity, G2Polarity, "DistanceMean", "small", iterNo);
        silhouette_G1.clear();
        silhouette_G2.clear();
    }

    public void SilhouetteCoefficient(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs, String fileName, boolean groupOneIsNEG, boolean groupOneIsPOS, String distanceType, int iterNo) throws IOException {
        List<Double[]> groupOne = new LinkedList<>();
        List<Double[]> groupTwo = new LinkedList<>();
        String G1Polarity = null, G2Polarity = null;
        Map<Double, Double> silhouette_Gone = new LinkedHashMap<Double, Double>();
        Map<String, Double> silhouette_G1 = new LinkedHashMap<>();

        Map<Double, Double> silhouette_Gtwo = new LinkedHashMap<>();
        Map<String, Double> silhouette_G2 = new LinkedHashMap<>();
        if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
//group 1 --> POS : group 2-->NEG
            G1Polarity = "POS";
            G2Polarity = "NEG";
        } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
//group 2 --> POS : group 1 --> NEG
            G1Polarity = "NEG";
            G2Polarity = "POS";
        } else {
            G1Polarity = "INA"; //inaccurate
            G2Polarity = "INA";
        }
        if (docGroups.get(0).isEmpty()) {
            System.out.println("Sequential_kmeans.sequential_kmeansImplementation.fanilOutbut.SilhouetteCoefficient.docGroups.get(0) isEmpty()");
        }
        if (docGroups.get(1).isEmpty()) {
            System.out.println("Sequential_kmeans.sequential_kmeansImplementation.fanilOutbut.SilhouetteCoefficient.docGroups.get(1) isEmpty() ");
        }

        if (G1Polarity.equalsIgnoreCase("INA") || G2Polarity.equalsIgnoreCase("INA")) {
            System.out.println("Sequential_kmeans.sequential_kmeansImplementation.fanilOutbut.SilhouetteCoefficient.classifier is inaccurate ... ");
        }

        double sim = 0.0, a = 0.0, b = 0.0, SumSim = 0.0;
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            groupOne.add(docGroups.get(0).get(i));
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            groupTwo.add(docGroups.get(1).get(i));
        }
        double r = groupCorrelation(docGroups, docs);
        if (r >= -0.50 && r <= 0.50) {

            for (int g = 0; g < docGroups.size(); g++) {
                for (int i = 0; i < docGroups.get(g).size(); i++) {
                    sim = 0.0;
                    a = 0.0;
                    SumSim = 0.0;
                    for (int j = 0; j < groupOne.size(); j++) {
                        if (distanceType == "cosin") {
                            sim = distance(docGroups.get(g).get(i), groupOne.get(j), "pp");
                        }
                        if (distanceType == "euclidean") {
                            sim = euclidean(docGroups.get(g).get(i), groupOne.get(j), "pp");
                        }
                        if (distanceType == "cityblock") {
                            sim = cityblock(docGroups.get(g).get(i), groupOne.get(j), "pp");
                        }
                        SumSim = SumSim + sim;
                    }
                    int documentNogroupOne = groupOne.size();
                    a = round(SumSim / documentNogroupOne);

                    if (groupOne.isEmpty()) {
                        a = 0.0;
                    }
                    sim = 0.0;
                    b = 0.0;
                    SumSim = 0.0;
                    for (int h = 0; h < groupTwo.size(); h++) {
                        if (distanceType == "cosin") {
                            sim = distance(docGroups.get(g).get(i), groupTwo.get(h), "pp");
                        }
                        if (distanceType == "euclidean") {
                            sim = euclidean(docGroups.get(g).get(i), groupTwo.get(h), "pp");
                        }
                        if (distanceType == "cityblock") {
                            sim = cityblock(docGroups.get(g).get(i), groupTwo.get(h), "pp");
                        }
                        SumSim = SumSim + sim;
                    }
                    int documentNogroupTwo = groupTwo.size();
                    b = round(SumSim / documentNogroupTwo);
                    if (groupTwo.isEmpty()) {
                        b = 0.0;
                    }
                    double max1 = max(b, a);
                    if (max1 == 0.0) {
                        max1 = 1;
                    }
                    if (g == 0) {
                        silhouette_Gone.put(docGroups.get(g).get(i)[0], round((b - a) / max1));
                    } else if (g == 1) {
                        silhouette_Gtwo.put(docGroups.get(g).get(i)[0], round((a - b) / max1));
                    }
                }
            }
        } else {
            System.out.println("Out of the correlation ");
        }

        for (String key : docs.keySet()) {
            for (Double keyS : silhouette_Gone.keySet()) {
                if (Objects.equals(docs.get(key), keyS)) {
                    silhouette_G1.put(key, silhouette_Gone.get(keyS));
                }
            }
        }
        for (String key : docs.keySet()) {
            for (Double keyS : silhouette_Gtwo.keySet()) {
                if (Objects.equals(docs.get(key), keyS)) {
                    silhouette_G2.put(key, silhouette_Gtwo.get(keyS));
                }
            }
        }
        if (silhouette_G2.isEmpty()) {
            System.out.println("Sequential_kmeans.sequential_kmeansImplementation.fanilOutbut.SilhouetteCoefficient.silhouette_G2 isEmpty() ");
        }
        if (silhouette_G1.isEmpty()) {
            System.out.println("Sequential_kmeans.sequential_kmeansImplementation.fanilOutbut.SilhouetteCoefficient.silhouette_G1 isEmpty() ");
        }
        silhouette_Gtwo.clear();
        silhouette_Gone.clear();
        writeSilhouette(1, silhouette_G1, silhouette_G2, fileName, G1Polarity, G2Polarity, "SilhouetteCoefficient", "larg", iterNo);
        writeSilhouette(2, silhouette_G1, silhouette_G2, fileName, G1Polarity, G2Polarity, "SilhouetteCoefficient", "larg", iterNo);
        silhouette_G1.clear();
        silhouette_G2.clear();
    }

    public void writeSilhouette(int run, Map<String, Double> silhouette_G1, Map<String, Double> silhouette_G2, String fileName, String G1Polarity, String G2Polarity, String dir, String small_larg, int iterNo) throws UnsupportedEncodingException, IOException {
        String[] f1 = fileName.split("\\\\");
        String G1max_name = "null";
        Double G1max_value = 0.0;
        String G2max_name = "null";
        Double G2max_value = 0.0;

        if (!silhouette_G1.isEmpty()) {
            for (Map.Entry<String, Double> entry : silhouette_G1.entrySet()) {
                if (entry.getKey().equals("point-pos") || entry.getKey().equals("point-neg")) {
                } else {
                    if (G1max_name.equals("null")) {
                        G1max_name = entry.getKey();
                        G1max_value = entry.getValue();
                    } else {
                        if (small_larg.equalsIgnoreCase("small")) {
                            if (G1max_value > entry.getValue()) {
                                G1max_name = entry.getKey();
                                G1max_value = entry.getValue();
                            }
                        } else if (small_larg.equalsIgnoreCase("larg")) {
                            if (G1max_value < entry.getValue()) {
                                G1max_name = entry.getKey();
                                G1max_value = entry.getValue();
                            }
                        }
                    }
                }
            }
        }

        if (!silhouette_G2.isEmpty()) {
            for (Map.Entry< String, Double> entry : silhouette_G2.entrySet()) {
                if (entry.getKey().equals("point-pos") || entry.getKey().equals("point-neg")) {
                } else {
                    if (G2max_name.equals("null")) {
                        G2max_name = entry.getKey();
                        G2max_value = entry.getValue();
                    } else {
                        if (small_larg.equalsIgnoreCase("small")) {
                            if (G2max_value > entry.getValue()) {
                                G2max_name = entry.getKey();
                                G2max_value = entry.getValue();
                            }
                        } else if (small_larg.equalsIgnoreCase("larg")) {
                            if (G2max_value < entry.getValue()) {
                                G2max_name = entry.getKey();
                                G2max_value = entry.getValue();
                            }
                        }
                    }
                }
            }
        }

        File dirPOW = new File("OUTPUT\\ProcessedDoc\\DateSeries_windowNumber\\");
        File[] filePOW = dirPOW.listFiles();
        int winNumber = filePOW.length;

        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dir + run + "\\Win" + winNumber + "\\kmeans\\G1\\" + G1Polarity + iterNo + f1[f1.length - 2] + f1[f1.length - 1]));
                BufferedWriter newFileSWN2 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dir + run + "\\Win" + winNumber + "\\kmeans\\G2\\" + G2Polarity + iterNo + f1[f1.length - 2] + f1[f1.length - 1]));
                BufferedWriter newFileSWN3 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dir + run + "\\Win" + winNumber + "\\kmeans\\Max\\G1\\" + G1Polarity + iterNo + f1[f1.length - 2] + f1[f1.length - 1]));
                BufferedWriter newFileSWN4 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dir + run + "\\Win" + winNumber + "\\kmeans\\Max\\G2\\" + G2Polarity + iterNo + f1[f1.length - 2] + f1[f1.length - 1]));) {
            if (!silhouette_G1.isEmpty()) {
                for (String H : silhouette_G1.keySet()) {
                    newFileSWN.write(H + "\t" + silhouette_G1.get(H));
                    newFileSWN.newLine();
                }
            } else {
                newFileSWN.newLine();
            }
            if (!silhouette_G2.isEmpty()) {
                for (String x : silhouette_G2.keySet()) {
                    newFileSWN2.write(x + "\t" + silhouette_G2.get(x));
                    newFileSWN2.newLine();
                }
            } else {
                newFileSWN2.newLine();
            }
            newFileSWN3.write(G1max_name + "\t" + G1max_value);
            newFileSWN3.newLine();

            newFileSWN4.write(G2max_name + "\t" + G2max_value);
            newFileSWN4.newLine();

        } catch (IOException e) {
            System.out.println("Error opening the file");
            e.getMessage();
        }
        silhouette_G1.remove(G1max_name); //wont be selected in the second run
        silhouette_G2.remove(G2max_name);
    }

    public double round(double value) {
        double db = 0.0;
        db = Math.round(value * 1000000) / 1000000.0;
        return db;
    }

    public double round(double value, int x) {
        double db = 0.0;
        db = Math.round(value * 100) / 100.0;
        return db;
    }

    private void readingFeatusrs(String docPath) throws IOException {
        BufferedReader featuresReader = null;
        try {
            featuresReader = new BufferedReader(new FileReader(docPath));
            String lineFeatures;
            do {
                lineFeatures = featuresReader.readLine();
                String[] values = lineFeatures.split("\t");
                if (values[0].matches("NULL#")) {
                    for (int x = 0; x < values.length; x++) {
                        featuresLinkedList.add(values[x]);
                    }
                }
            } while (featuresLinkedList.size() == 0);
            featuresReader.close();
        } catch (IOException ioex) {
            System.out.println("Failed to copy files : " + ioex.getMessage());
            ioex.printStackTrace();
        } finally {
            if (featuresReader != null) {
                featuresReader.close();
            } else {
                System.out.println("readingFeatusrs");
            }
        }
    }

    private void readDocFileTolist(String docPath) throws IOException {
        String lineDoc;
        BufferedReader docReader = null;
        try {
            docReader = new BufferedReader(new FileReader(docPath));
            while ((lineDoc = docReader.readLine()) != null) {
                if (!lineDoc.startsWith("#")) {
                    String[] values = lineDoc.split("\t");
                    if (values.length != featuresLinkedList.size()) {
                        System.out.println("Documents file:Incorrect tabulation format in file, line: ");
                        throw new IllegalArgumentException(
                                "Documents file:Incorrect tabulation format in file, line: ");
                    }
                    if (!values[0].matches("NULL#")) {
                        docs.add(lineDoc);
                    }
                }
            }
            docReader.close();
        } catch (IOException ioex) {
            System.out.println("Failed to files : " + ioex.getMessage());
            ioex.printStackTrace();
        } finally {
            if (docReader != null) {
                docReader.close();
            } else {
                System.out.println("readDocFileTolist");
            }
        }
    }

    private double distance(Double[] instanceValue, Double[] centroidValue, String compination) {
        double euclideanInstanceValue = 0.0;
        double euclideanCentroidValue = 0.0;
        double dotProduct = 0.0;
        int instanceIndexValue = 1, centroidIndexValue = 0;

        if (compination.equalsIgnoreCase("pm")) {
            instanceIndexValue = 1;
            centroidIndexValue = 0;
        } else if (compination.equalsIgnoreCase("pp")) {
            instanceIndexValue = 1;
            centroidIndexValue = 1;
        } else if (compination.equalsIgnoreCase("mm")) {
            instanceIndexValue = 0;
            centroidIndexValue = 0;
        }

        int IndexCent = centroidIndexValue;
        double cosineSimilarity = 0.0;
        for (int instanceIndex = instanceIndexValue; instanceIndex < instanceValue.length; instanceIndex++) {
            euclideanInstanceValue = euclideanInstanceValue + (instanceValue[instanceIndex] * instanceValue[instanceIndex]);
        }

        for (int centroidIndex = centroidIndexValue; centroidIndex < centroidValue.length; centroidIndex++) {
            euclideanCentroidValue = euclideanCentroidValue + (centroidValue[centroidIndex] * centroidValue[centroidIndex]);
        }

        euclideanInstanceValue = round(Math.sqrt(euclideanInstanceValue));
        euclideanCentroidValue = round(Math.sqrt(euclideanCentroidValue));

        for (int instanceIndex = instanceIndexValue; instanceIndex < instanceValue.length; instanceIndex++) {
            dotProduct = dotProduct + (instanceValue[instanceIndex] * centroidValue[IndexCent]);
            IndexCent++;
        }

        cosineSimilarity = round(dotProduct / (euclideanInstanceValue * euclideanCentroidValue));

        double distance = 1 - cosineSimilarity;

        if (Double.isNaN(distance)) {
            distance = 0.0;
        }

        return round(distance);
    }

    private double euclidean(Double[] instanceValue, Double[] centroidValue, String compination) {
        int instanceIndexValue = 1, centroidIndexValue = 0;

        if (compination.equalsIgnoreCase("pm")) {
            instanceIndexValue = 1;
            centroidIndexValue = 0;
        } else if (compination.equalsIgnoreCase("pp")) {
            instanceIndexValue = 1;
            centroidIndexValue = 1;
        } else if (compination.equalsIgnoreCase("mm")) {
            instanceIndexValue = 0;
            centroidIndexValue = 0;
        }

        double dist = 0.0;
        for (int instanceIndex = instanceIndexValue; instanceIndex < instanceValue.length; instanceIndex++) {
            double x = instanceValue[instanceIndex] - centroidValue[centroidIndexValue];
            centroidIndexValue++;
            dist = dist + (x * x);
        }

        return round(Math.sqrt(dist));
    }

    private double cityblock(Double[] instanceValue, Double[] centroidValue, String compination) {
        //Taxicab geometry, also known as City block distance or Manhattan distance
        double cityblock = 0.0;
        int instanceIndexValue = 1, centroidIndexValue = 0;

        if (compination.equalsIgnoreCase("pm")) {
            instanceIndexValue = 1;
            centroidIndexValue = 0;
        } else if (compination.equalsIgnoreCase("pp")) {
            instanceIndexValue = 1;
            centroidIndexValue = 1;
        } else if (compination.equalsIgnoreCase("mm")) {
            instanceIndexValue = 0;
            centroidIndexValue = 0;
        }

        int iSCen = centroidIndexValue;
        for (int iIns = instanceIndexValue; iIns < instanceValue.length; iIns++) {
            cityblock = cityblock + (instanceValue[iIns] - centroidValue[iSCen]);
            iSCen++;
        }
        return round(cityblock);
    }

    private double correlation(Double[] instanceValue, Double[] centroidValue, String compination) {

        double dotProduct = 0.0;
        int instanceIndexValue = 1, centroidIndexValue = 0;

        if (compination.equalsIgnoreCase("pm")) {
            instanceIndexValue = 1;
            centroidIndexValue = 0;
        } else if (compination.equalsIgnoreCase("pp")) {
            instanceIndexValue = 1;
            centroidIndexValue = 1;
        } else if (compination.equalsIgnoreCase("mm")) {
            instanceIndexValue = 0;
            centroidIndexValue = 0;
        }

        double correlation = 0.0, cov = 0.0;
        int iSCen = centroidIndexValue;
        double sumCe = 0.0, sumIn = 0.0, avgCe = 0.0, avgIn = 0.0, stdCe = 0.0, stdIn = 0.0, stdSumCe = 0.0, stdSumIn = 0.0;

        for (int j = centroidIndexValue; j < centroidValue.length; j++) {
            sumCe = sumCe + centroidValue[j];
        }
        avgCe = round(sumCe / centroidValue.length);

        for (int i = instanceIndexValue; i < instanceValue.length; i++) {
            sumIn = sumIn + instanceValue[i];
        }
        avgIn = round(sumIn / (instanceValue.length - 1));

        for (int j = centroidIndexValue; j < centroidValue.length; j++) {
            stdSumCe = stdSumCe + ((centroidValue[j] - avgCe) * (centroidValue[j] - avgCe));
        }

        stdCe = round(Math.sqrt(stdSumCe / centroidValue.length));
        // stdCe = round(Math.sqrt(stdSumCe));

        for (int i = instanceIndexValue; i < instanceValue.length; i++) {
            stdSumIn = stdSumIn + ((instanceValue[i] - avgIn) * (instanceValue[i] - avgIn));
        }

        stdIn = round(Math.sqrt(stdSumIn / (instanceValue.length - 1)));
        // stdIn = round(Math.sqrt(stdSumIn));

        int length = 0;
        for (int iIns = instanceIndexValue; iIns < instanceValue.length; iIns++) {
            cov = cov + ((instanceValue[iIns] - avgIn) * (centroidValue[iSCen] - avgCe));
            iSCen++;
            length++;
        }
        cov = round(cov / length);
        //  cov = round(cov );
        correlation = round(cov / (stdCe * stdIn));
        return round(correlation);
    }
}

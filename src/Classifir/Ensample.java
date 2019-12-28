/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import Preprocessing.OutPut;
import Preprocessing.Reading;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.io.comparator.NameFileComparator;

/**
 *
 * @author 17511035
 */
public class Ensample extends Kmeans2 {

    public static Map<String, String> consistency = new HashMap<>();
    public static Set<String> inConsistentDoc = new HashSet<>();
    public static Map<String, Integer> inConsistentDocStatistc = new HashMap<>();
    public static Map<Integer, Double> consistencyRates = new HashMap<>();
    public static Map<Integer, Integer> consistency_WinSizes = new HashMap<>();
    public static Map<Integer, Double> consistency_ConsisDocSize = new HashMap<>();

    List<String> resu_ensample_effect = new LinkedList<>();
    String re = null;
    //   StringBuilder docs = new StringBuilder();
    public static List<int[]> clusterIndex = new LinkedList<>();
    public static List<int[]> clusterResults = new LinkedList<>();
    public Map<String, Integer> ensampLearning = new TreeMap<>();
    public List<String> docs = new LinkedList<>();
    OutPut wrting = new OutPut();
    List<String> resu_latex = new LinkedList<>();
    List<String> resu = new LinkedList<>();
    List<String> resu_latex2 = new LinkedList<>();
    List<String> resu2 = new LinkedList<>();
    double positivity = 0.0, actualPositivity = 0.0;
    double negativity = 0.0, actualNegativity = 0.0, balance = 0.0;
    int wNumber = 0;

    public void ensample_Implementation(String whichPOS) throws IOException {
        classifierImplementation(whichPOS);
        xxx();
        accuracy();

        ensampLearning.clear();
        clusterIndex.clear();
        clusterResults.clear();

    }

    public void ensample_Implementation(String whichPOS, int winNumber) throws IOException {
        wNumber = winNumber;
        classifierImplementation(whichPOS);
        xxx();
        accuracy();
        typRes(winNumber);
        consistency();

        System.out.println("inConsistentDoc.size() " + inConsistentDoc.size());
        System.out.println("ConsistentDoc " + (consistency.size() - inConsistentDoc.size()));
        System.out.println("consistency.size()  " + consistency.size());
        double consisDocSize = consistency.size() - inConsistentDoc.size();
        double consis = consisDocSize / consistency.size();
        System.out.println(consis);
        System.out.println("Consistency : " + consis + " Window : " + winNumber);

        consistency_WinSizes.put(wNumber, consistency.size());
        consistency_ConsisDocSize.put(wNumber, consisDocSize);
        consistencyRates.put(wNumber, consis);

        System.out.println("Window_Number " + "\t" + "Consistency_Rates" + "\t" + "Consistent_Docs_Number" + "\t" + "Window_Size");
        for (Integer dd : consistencyRates.keySet()) {
            System.out.println(dd + "\t" + consistencyRates.get(dd) + "\t" + consistency_ConsisDocSize.get(dd) + "\t" + consistency_WinSizes.get(dd));
        }

        System.out.println("DocID" + "\t" + "Number_of_Changes");
        for (String dd : inConsistentDocStatistc.keySet()) {
            System.out.println(dd + "\t" + inConsistentDocStatistc.get(dd));
        }

        System.out.println("DocID" + "\t" + "Actaul_Label" + " Predected_Label " + "\t" + " Changed " + "\t" + "Window_Number");
        for (String dd : consistency.keySet()) {
            System.out.println(dd + "\t" + consistency.get(dd));
        }
        ensampLearning.clear();
        clusterIndex.clear();
        clusterResults.clear();
    }

    public void classifierImplementation(String whichPOS) throws IOException {

        int index = 0;
        /*
         File dirPOW;

         dirPOW = new File("OUTPUT\\" + whichPOS + "\\POW");
         File[] filePOW = dirPOW.listFiles();
         for (File f : filePOW) {
         index++;
         System.out.print(index + " : ");
         long KmeansTimesS = System.currentTimeMillis();
         kmeansImplementation(f.toString(), 2, 100, "cosin");
         clusterIndex.add(INDX);
         long KmeansTimesF = System.currentTimeMillis();
         long KmeansTotalTime = KmeansTimesF - KmeansTimesS;
         results = results + "\t" + (KmeansTotalTime / 1000);
         results_latex = f + " & " + results_latex + " & " + (KmeansTotalTime / 1000) + "\\";
         resu.add(results);
         resu_latex.add(results_latex);
         System.out.println(results);
         System.out.println("Time of clustring: " + (KmeansTotalTime / 1000) + " sec,  " + (KmeansTotalTime / 60000) + " mnt");
         System.out.println("");
         }
         */
        File dirPOWR;
        dirPOWR = new File("OUTPUT\\" + whichPOS + "\\POW_R");
        File[] filePOWR = dirPOWR.listFiles();
        Arrays.sort(filePOWR, NameFileComparator.NAME_COMPARATOR);
        for (File fr : filePOWR) {
            System.out.println(fr);
        }

        for (File fr : filePOWR) {
            index++;
            System.out.print(index + " : ");
            long KmeansTimesS = System.currentTimeMillis();
            long KmeansTimesS2 = System.currentTimeMillis();
            kmeansImplementation(fr.toString(), 2, 100, "cosin");
            clusterIndex.add(INDX);
            long KmeansTimesF = System.currentTimeMillis();
            long KmeansTotalTime = KmeansTimesF - KmeansTimesS;
            results = results + "\t" + (KmeansTotalTime / 1000);
            results_latex = fr + " & " + results_latex + " & " + (KmeansTotalTime / 1000) + "\\";
            System.out.println(results);
            System.out.println("ENDS after " + (KmeansTotalTime / 1000) + " sec,  " + (KmeansTotalTime / 60000) + " mnt");
            System.out.println("");
            resu_latex.add(results_latex);
            resu.add(results);

            kmeansImplementation(fr.toString(), 2, 1, "cosin");
            clusterIndex.add(INDX);

            long KmeansTimesF2 = System.currentTimeMillis();
            long KmeansTotalTime2 = KmeansTimesF2 - KmeansTimesS2;
            results = results + "\t" + (KmeansTotalTime2 / 1000);
            results_latex = fr + " & " + results_latex + " & " + (KmeansTotalTime2 / 1000) + "\\";
            System.out.println(results);
            System.out.println("ENDS after " + (KmeansTotalTime2 / 1000) + " sec,  " + (KmeansTotalTime2 / 60000) + " mnt");
            System.out.println("");
            resu_latex2.add(results_latex);
            resu2.add(results);

        }

        wrting.results("OUTPUT\\kmeans_results\\Latex\\Win" + wNumber, whichPOS, resu_latex, "100Iter");
        wrting.results("OUTPUT\\kmeans_results\\Excel\\Win" + wNumber, whichPOS, resu, "100Iter");

        /*
         File dirPOWA;
         dirPOWA = new File("OUTPUT\\a_AND_a\\POW_A");
         File[] filePOWA = dirPOWA.listFiles();
         for (File fa : filePOWA) {
         // clusterIndex.add(kmeansImplementation(fa.toString(), 2));
         }
         */
        System.out.println("::::: " + clusterIndex.get(0).length);
        for (int u = 0; u < clusterIndex.get(0).length; u++) {
            //  System.out.print(u + ": ");
            int[] val = new int[clusterIndex.size()];
            int idx = 0;
            for (int[] xx : clusterIndex) {
                val[idx] = xx[u];
                idx++;
                //   System.out.print(" " + val[idx]);
            }
            clusterResults.add(val);
        }

        for (int x = 0; x < clusterIndex.size(); x++) {
            for (int d = 0; d < clusterIndex.get(0).length; d++) {
                //   System.out.print(" " + clusterIndex.get(x)[d]);
            }
            //    System.out.println();
        }

        for (int x = 0; x < clusterResults.size(); x++) {
            for (int d = 0; d < clusterResults.get(0).length; d++) {
                // System.out.print(" " + clusterResults.get(x)[d]);
            }
            //System.out.println();
        }
    }

    public void ensample_Implementation_ensample_effect(String whichPOS) throws IOException {
        classifierImplementation(whichPOS);
        int index = clusterResults.get(0).length;
        while (index >= 0) {
            xxx_ensample_effect(index);
            accuracy();
            index--;
        }
        System.out.println("Imbalance \t Accuracy \t Precision \t Recall \t F_measure");
        int r = clusterResults.get(0).length;;
        for (String s : resu_ensample_effect) {

            System.out.println(r + "\t" + s);
            r--;
        }
        ensampLearning.clear();
        clusterResults.clear();
        clusterIndex.clear();
    }

    public void xxx_ensample_effect(int index) {
        System.out.println("The size of docsEnsample is : " + docsEnsample.size());
        System.out.println("The size of clusterResults is : " + clusterResults.size());
        for (String doc : docsEnsample) {
            docs.add(doc);
        }
        int dx = 0;
        System.out.println("The size of docs is : " + docs.size());
        System.out.println("size of clusterResults.get(0) " + clusterResults.get(0).length);
        for (int[] arrayL : clusterResults) {
            ensampLearning.put(docs.get(dx), majorityVoting_ensample_effect(arrayL, index));
            dx++;
        }
        System.out.println("The size of ensampLearning is : " + ensampLearning.size());
    }

    private int majorityVoting_ensample_effect(int[] learnersResults, int index) {
        int result = 0;
        int ones = 0;
        int tows = 0;
        for (int i = 0; i < index; i++) {

            if (learnersResults[i] == 1) {
                ones++;
            } else if (learnersResults[i] == 2) {
                tows++;
            }
        }
        if (ones < tows) {
            result = 2;
        } else if (ones >= tows) {
            result = 1;
        }
        //     System.out.print(result + "\t");
        return result;
    }

    public void xxx() {
        System.out.println("The size of docsEnsample is : " + docsEnsample.size());
        System.out.println("The size of clusterResults is : " + clusterResults.size());
        for (String doc : docsEnsample) {
            docs.add(doc);
        }
        int dx = 0;
        System.out.println("The size of docs is : " + docs.size());
        for (int[] arrayL : clusterResults) {
            ensampLearning.put(docs.get(dx), majorityVoting(arrayL));
            dx++;
        }
        System.out.println("The size of ensampLearning is : " + ensampLearning.size());
    }

    private int majorityVoting(int[] learnersResults) {
        int result = 0;
        int ones = 0;
        int tows = 0;
        for (int element : learnersResults) {
            if (element == 1) {
                ones++;
            } else if (element == 2) {
                tows++;
            }
        }
        if (ones < tows) {
            result = 2;
        } else if (ones >= tows) {
            result = 1;
        }
        //     System.out.print(result + "\t");
        return result;
    }

    public void consistency() {
        int winNo = 0;
        String gOne = "";
        String gTwo = "";

        if (ensampLearning.get("point-neg") == 1 && ensampLearning.get("point-pos") == 2) {
            gOne = "neg";
            gTwo = "pos";
        } else if (ensampLearning.get("point-neg") == 2 && ensampLearning.get("point-pos") == 1) {
            gOne = "pos";
            gTwo = "neg";
        }
        String predictedLabel = "";

        ensampLearning.remove("point-neg");
        ensampLearning.remove("point-pos");

        if (consistency.isEmpty()) {
            for (Map.Entry entry : ensampLearning.entrySet()) {
                if (entry.getValue().equals(1)) {
                    predictedLabel = gOne;
                } else if (entry.getValue().equals(2)) {
                    predictedLabel = gTwo;
                }
                String[] data = entry.getKey().toString().split("-"); //example doc1-neg-(1741).txt

                //data[2] is the uniqe ID of a doc.
                // data[1] actual label
                // data[0] is uniq ID in a given window
                // "0" refer to consistency in the prediction
                // "1" the number of window a doc is appeared
                consistency.put(data[2], data[1] + "\t" + predictedLabel + "\t" + "0" + "\t" + "1");
            }
        } else {
            for (Map.Entry entry : ensampLearning.entrySet()) {
                if (entry.getValue().equals(1)) {
                    predictedLabel = gOne;
                } else if (entry.getValue().equals(2)) {
                    predictedLabel = gTwo;
                }
                String[] newdata = entry.getKey().toString().split("-"); //example doc1-neg-(1741).txt
                if (consistency.containsKey(newdata[2])) {
                    String[] old = consistency.get(newdata[2]).toString().split("\t"); //example pos	pos	0	1 (Actual label, predicted label, changed or not, number of window the doc appears in)
                    if (old[1].equals(predictedLabel)) {
                    } else {
                        old[2] = "1";
                        inConsistentDoc.add(newdata[2]);
                        if (inConsistentDocStatistc.containsKey(newdata[2])) {
                            inConsistentDocStatistc.put(newdata[2], inConsistentDocStatistc.get(newdata[2]) + 1);
                        } else {
                            inConsistentDocStatistc.put(newdata[2], 1);
                        }
                    }
                    winNo = Integer.parseInt(old[3]) + 1;
                    consistency.put(newdata[2], newdata[1] + "\t" + predictedLabel + "\t" + old[2] + "\t" + winNo);
                } else {
                    consistency.put(newdata[2], newdata[1] + "\t" + predictedLabel + "\t" + "0" + "\t" + "1");
                }
            }
        }
    }

    public void accuracy() throws IOException {
        System.out.println("--- Ensample ---");
        List<List<String>> confusingMatrix = new LinkedList<>();
        List<String> posGone = new ArrayList<String>();
        List<String> posGtwo = new ArrayList<String>();
        List<String> negGone = new ArrayList<String>();
        List<String> negGtwo = new ArrayList<String>();
        List<String> wrongDocs = new ArrayList<String>();

        Integer[] xx = new Integer[ensampLearning.size()];
        xx[0] = 1;
        xx[1] = 2;
        boolean groupOneIsPOS = false, groupOneIsNEG = false;

        for (Map.Entry entry : ensampLearning.entrySet()) {

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
///////////////

///////////////
        if ((groupOneIsNEG == true) && (groupOneIsPOS == true)) {
            System.out.println("group one has TWO seeds unaccurate ");
        } else if ((groupOneIsNEG == false) && (groupOneIsPOS == false)) {
            System.out.println("group one has NO seeds unaccurate ");

        } else if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
            System.out.println("group 1 --> POS : group 2-->NEG : based one seeds");

        } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
            System.out.println("group 2 --> POS : group 1 --> NEG : based one seeds");

        }
        double grouDocSize = ensampLearning.size();
        double posGoneSize = posGone.size();
        double negGtwoSize = negGtwo.size();
        double posGtwoSize = posGtwo.size();
        double negGoneSize = negGone.size();
        double accuracy = 0.0;
        double precision = 0.0;
        double recall = 0.0;
        double f_measure = 0.0;

        confusingMatrix.add(posGone);
        confusingMatrix.add(negGtwo);
        confusingMatrix.add(posGtwo);
        confusingMatrix.add(negGone);

        //  if ((posGoneSize + negGtwoSize) >= (posGtwoSize + negGoneSize)) {
        if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
            //group 1 is positive
            // 2--> neg
            System.out.println("group 1 --> POS : group 2-->NEG ");
            accuracy = ((posGoneSize + negGtwoSize) / grouDocSize);
            precision = posGoneSize / (posGoneSize + negGoneSize);
            recall = posGoneSize / (posGoneSize + posGtwoSize);
            f_measure = (2 * precision * recall) / (precision + recall);
            positivity = (posGoneSize + negGoneSize) / grouDocSize;
            negativity = (negGtwoSize + posGtwoSize) / grouDocSize;

            //wrong classified docs are in negGone and in posGtwo.
            System.out.println(posGtwoSize + ".." + negGoneSize + " :::: " + posGoneSize + "+" + negGtwoSize + "/" + grouDocSize + "=" + accuracy);

//            wrongDoc(negGone, "_NEGATIVE");
            //           wrongDoc(posGtwo, "_POSITIVE");
//            wrongDocs.addAll(negGone);
            //           wrongDocs.addAll(posGtwo);
            //           wrongDoc(wrongDocs, "_POSITIVE_NEGATIVE");
            //} else {
        } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
            //  1--> neg
            // group 2 is positive
            System.out.println((posGoneSize + negGtwoSize) + " > " + (posGtwoSize + negGoneSize));
            System.out.println("group 2 --> POS : group 1 --> NEG");
            accuracy = ((posGtwoSize + negGoneSize) / grouDocSize);
            System.out.println(posGtwoSize + "+" + negGoneSize + "/" + grouDocSize + "=" + accuracy);
            precision = posGtwoSize / (posGtwoSize + negGtwoSize);
            recall = posGtwoSize / (posGtwoSize + posGoneSize);
            f_measure = (2 * precision * recall) / (precision + recall);
            positivity = (posGtwoSize + negGtwoSize) / grouDocSize;
            negativity = (negGoneSize + posGoneSize) / grouDocSize;
            //wrong classified docs are in posGone and in negGtwo.

            //          wrongDoc(negGtwo, "_NEGATIVE");
            //         wrongDoc(posGone, "_POSITIVE");
            //         wrongDocs.addAll(negGtwo);
            //         wrongDocs.addAll(posGone);
            //          wrongDoc(wrongDocs, "_POSITIVE_NEGATIVE");
        }
        positivity = round(positivity);
        negativity = round(negativity);
        actualPositivity = round((posGone.size() + posGtwo.size()) / grouDocSize);
        actualNegativity = round((negGone.size() + negGtwo.size()) / grouDocSize);
        balance = Math.abs(actualPositivity - actualNegativity);
        re = positivity + "," + negativity + "," + actualPositivity + "," + actualNegativity + "," + balance + "," + round(accuracy * 100, 2) + "," + round(precision * 100, 2) + "," + round(recall * 100, 2) + "," + round(f_measure * 100, 2);
        System.out.println("positivity= " + positivity + "\t" + "negativity= " + negativity + "\t" + "actualPositivity= " + actualPositivity + "\t" + "actualNegativity= " + actualNegativity + "\t" + "balance= " + balance);
        //  ensampLearning.remove(ensampLearning);
        System.out.println("accuracy= " + round(accuracy * 100, 2) + "\t"
                + "precision= " + round(precision * 100, 2) + "\t"
                + "recall= " + round(recall * 100, 2) + "\t"
                + "f_measure= " + round(f_measure * 100, 2));
        System.out.println("Actual pos: " + posGone.size() + "\t" + posGtwo.size());
        System.out.println("Actual neg: " + negGone.size() + "\t" + negGtwo.size());
        resu_ensample_effect.add(balance + "\t" + round(accuracy * 100, 2) + "\t" + round(precision * 100, 2) + "\t" + round(recall * 100, 2) + "\t" + round(f_measure * 100, 2));
        posGone.removeAll(posGone);
        posGtwo.removeAll(posGtwo);
        negGone.removeAll(negGone);
        negGtwo.removeAll(negGtwo);
    }

    public void typRes(int winNumber) throws IOException {
        File directory = new File("OUTPUT\\WindowOutbut");
        directory.mkdirs();
        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\WindowOutbut\\Window_" + winNumber + ".txt"))) {
            newFileSWN.write(re);
            newFileSWN.newLine();
            newFileSWN.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file");
        }
    }

    public void accuracy3() throws IOException {
        System.out.println("--- Ensample ---");
        List<List<String>> confusingMatrix = new LinkedList<>();
        List<String> Gone = new ArrayList<String>();
        List<String> Gtwo = new ArrayList<String>();
        List<String> Gthree = new ArrayList<String>();

        List<String> posGone = new ArrayList<String>();
        List<String> negGone = new ArrayList<String>();
        List<String> neuGone = new ArrayList<String>();

        List<String> posGtwo = new ArrayList<String>();
        List<String> negGtwo = new ArrayList<String>();
        List<String> neuGtwo = new ArrayList<String>();

        List<String> posGthree = new ArrayList<String>();
        List<String> negGthree = new ArrayList<String>();
        List<String> neuGthree = new ArrayList<String>();

        Integer[] xx = new Integer[3];
        xx[0] = 1;
        xx[1] = 2;
        xx[2] = 3;

        for (Map.Entry entry : ensampLearning.entrySet()) {
            String[] values = entry.getKey().toString().split("-");
            if ((entry.getValue() == xx[0]) && "pos".equals(values[1])) {
                posGone.add(entry.getKey().toString());
                Gone.add(entry.getKey().toString());

            } else if ((entry.getValue() == xx[1]) && "pos".equals(values[1])) {
                posGtwo.add(entry.getKey().toString());
                Gtwo.add(entry.getKey().toString());

            } else if ((entry.getValue() == xx[2]) && "pos".equals(values[1])) {
                posGthree.add(entry.getKey().toString());
                Gthree.add(entry.getKey().toString());
            } else if ((entry.getValue() == xx[0]) && "neg".equals(values[1])) {
                negGone.add(entry.getKey().toString());
                Gone.add(entry.getKey().toString());

            } else if ((entry.getValue() == xx[1]) && "neg".equals(values[1])) {
                negGtwo.add(entry.getKey().toString());
                Gtwo.add(entry.getKey().toString());
            } else if ((entry.getValue() == xx[2]) && "neg".equals(values[1])) {
                negGthree.add(entry.getKey().toString());
                Gthree.add(entry.getKey().toString());
            } else if ((entry.getValue() == xx[0]) && "neu".equals(values[1])) {
                neuGone.add(entry.getKey().toString());
                Gone.add(entry.getKey().toString());

            } else if ((entry.getValue() == xx[1]) && "neu".equals(values[1])) {
                neuGtwo.add(entry.getKey().toString());
                Gtwo.add(entry.getKey().toString());
            } else if ((entry.getValue() == xx[2]) && "neu".equals(values[1])) {
                neuGthree.add(entry.getKey().toString());
                Gthree.add(entry.getKey().toString());
            }

        }
        int pointCountGone = 0, pointCountGtwo = 0, pointCountGthree = 0;
        boolean accuract = false;

        /*
         1 means pos , 2 means neg , 3 means neu
         1 2 3
         1 3 2
         2 1 3
         2 3 1
         3 2 1
         3 1 2
         */
        String Group1 = "", Group2 = "", Group3 = "";
        int[] groupsIdentity = {0, 0, 0};
        for (String la : Gone) {
            String[] values = la.split("-");
            if (values[0].equalsIgnoreCase("point")) {
                pointCountGone++;
            }
            if (pointCountGone == 1) {
                if (values[0].equalsIgnoreCase("point") && values[1].equalsIgnoreCase("pos")) {
                    groupsIdentity[0] = 1;
                    Group1 = "POS";
                } else if (values[0].equalsIgnoreCase("point") && values[1].equalsIgnoreCase("neg")) {
                    groupsIdentity[0] = 2;
                    Group1 = "NEG";
                } else if (values[0].equalsIgnoreCase("point") && values[1].equalsIgnoreCase("neu")) {
                    groupsIdentity[0] = 3;
                    Group1 = "NEU";
                }
            }
        }
        for (String la : Gtwo) {
            String[] values = la.split("-");
            if (values[0].equalsIgnoreCase("point")) {
                pointCountGtwo++;
            }
            if (pointCountGtwo == 1) {
                if (values[0].equalsIgnoreCase("point") && values[1].equalsIgnoreCase("pos")) {
                    groupsIdentity[1] = 1;
                    Group2 = "POS";
                } else if (values[0].equalsIgnoreCase("point") && values[1].equalsIgnoreCase("neg")) {
                    groupsIdentity[1] = 2;
                    Group2 = "NEG";
                } else if (values[0].equalsIgnoreCase("point") && values[1].equalsIgnoreCase("neu")) {
                    groupsIdentity[1] = 3;
                    Group2 = "NEU";
                }
            }
        }
        for (String la : Gthree) {
            String[] values = la.split("-");
            if (values[0].equalsIgnoreCase("point")) {
                pointCountGthree++;
            }
            if (pointCountGthree == 1) {
                if (values[0].equalsIgnoreCase("point") && values[1].equalsIgnoreCase("pos")) {
                    groupsIdentity[2] = 1;
                    Group3 = "POS";
                } else if (values[0].equalsIgnoreCase("point") && values[1].equalsIgnoreCase("neg")) {
                    groupsIdentity[2] = 2;
                    Group3 = "NEG";
                } else if (values[0].equalsIgnoreCase("point") && values[1].equalsIgnoreCase("neu")) {
                    groupsIdentity[2] = 3;
                    Group3 = "NEU";
                }
            }
        }

        System.out.println(groupsIdentity[0] + "   " + groupsIdentity[1] + "    " + groupsIdentity[2]);
        if (pointCountGone == 1 && pointCountGtwo == 1 && pointCountGthree == 1) {
            accuract = true;
            System.out.println("This classifier is ACCURATE ");
        } else {
            System.out.println("the learner is not accurate ");
        }

        confusingMatrix.add(posGone);
        confusingMatrix.add(negGone);
        confusingMatrix.add(neuGone);

        confusingMatrix.add(posGtwo);
        confusingMatrix.add(negGtwo);
        confusingMatrix.add(neuGtwo);

        confusingMatrix.add(posGthree);
        confusingMatrix.add(negGthree);
        confusingMatrix.add(neuGthree);

        double grouDocSize = Gone.size() + Gtwo.size() + Gthree.size();

        double accuracy = 0.0;
        double precision = 0.0;
        double recall = 0.0;
        double f_measure = 0.0;

        double posGoneSize = posGone.size();
        double negGoneSize = negGone.size();
        double neuGoneSize = neuGone.size();

        double posGtwoSize = posGtwo.size();
        double negGtwoSize = negGtwo.size();
        double neuGtwoSize = neuGtwo.size();

        double posGthreeSize = posGthree.size();
        double negGthreeSize = negGthree.size();
        double neuGthreeSize = neuGthree.size();
        double GoneSize = Gone.size();
        double GtwoSize = Gtwo.size();
        double GthreeSize = Gthree.size();

        if (accuract == true) {
            if (groupsIdentity[0] == 1 && groupsIdentity[1] == 2 && groupsIdentity[2] == 3) {
                accuracy = (posGoneSize + negGtwoSize + neuGthreeSize) / grouDocSize;
                precision = posGoneSize / GoneSize;
                recall = posGoneSize / (posGoneSize + posGtwoSize + posGthreeSize);
            } else if (groupsIdentity[0] == 1 && groupsIdentity[1] == 3 && groupsIdentity[2] == 2) {
                accuracy = (posGoneSize + neuGtwoSize + negGthreeSize) / grouDocSize;
                precision = posGoneSize / GoneSize;
                recall = posGoneSize / (posGoneSize + posGtwoSize + posGthreeSize);
            } else if (groupsIdentity[0] == 2 && groupsIdentity[1] == 1 && groupsIdentity[2] == 3) {
                accuracy = (negGoneSize + posGtwoSize + neuGthreeSize) / grouDocSize;
                precision = posGtwoSize / GtwoSize;
                recall = posGtwoSize / (posGoneSize + posGtwoSize + posGthreeSize);
            } else if (groupsIdentity[0] == 2 && groupsIdentity[1] == 3 && groupsIdentity[2] == 1) {
                accuracy = (negGoneSize + neuGtwoSize + posGthreeSize) / grouDocSize;
                precision = posGthreeSize / GthreeSize;
                recall = posGthreeSize / (posGoneSize + posGtwoSize + posGthreeSize);
            } else if (groupsIdentity[0] == 3 && groupsIdentity[1] == 2 && groupsIdentity[2] == 1) {
                accuracy = (neuGoneSize + negGtwoSize + posGthreeSize) / grouDocSize;
                precision = posGthreeSize / GthreeSize;
                recall = posGthreeSize / (posGoneSize + posGtwoSize + posGthreeSize);
            } else if (groupsIdentity[0] == 3 && groupsIdentity[1] == 1 && groupsIdentity[2] == 2) {
                accuracy = (neuGthreeSize + posGtwoSize + negGthreeSize) / grouDocSize;
                precision = posGtwo.size() / GtwoSize;
                recall = posGtwoSize / (posGoneSize + posGtwoSize + posGthreeSize);
            }
            f_measure = (2 * precision * recall) / (precision + recall);
        }

        System.out.println(round(accuracy * 100) + "\t" + round(precision * 100) + "\t" + round(recall * 100) + "\t" + round(f_measure * 100));
        System.out.println("\t" + Group1 + " \t" + Group2 + "\t" + Group3);
        System.out.println("Actual pos: " + posGone.size() + "\t" + posGtwo.size() + "\t" + posGthree.size());
        System.out.println("Actual pos: " + negGone.size() + "\t" + negGtwo.size() + "\t" + negGthree.size());
        System.out.println("Actual pos: " + neuGone.size() + "\t" + neuGtwo.size() + "\t" + neuGthree.size());

        //  ensampLearning.remove(ensampLearning);
        Gone.removeAll(Gone);
        Gtwo.removeAll(Gtwo);
        Gthree.removeAll(Gthree);
        posGone.removeAll(posGone);
        negGone.removeAll(negGone);
        neuGone.removeAll(neuGone);
        posGtwo.removeAll(posGtwo);
        negGtwo.removeAll(negGtwo);
        neuGtwo.removeAll(neuGtwo);
        posGthree.removeAll(posGthree);
        negGthree.removeAll(negGthree);
        neuGthree.removeAll(neuGthree);
    }

    private void deleteOldWrongDoc(String path) {
        File dir;
        dir = new File(path);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    private void wrongDoc(List<String> misclasdDocName, String pos_neg) throws IOException {
        deleteOldWrongDoc("OUTPUT\\ProcessedDoc\\WrongClassifiedDoc\\Files\\" + pos_neg + "\\");
        Reading reader;
        reader = new Reading();
        reader.readWritWoungDocs(misclasdDocName, "a_AND_a", pos_neg);
    }

    private int majorityVoting3(int[] learnersResults) {
        int result = 0;
        int ones = 0;
        int tows = 0;
        int threes = 0;
        for (int element : learnersResults) {
            if (element == 1) {
                ones++;
            } else if (element == 2) {
                tows++;
            } else if (element == 3) {
                threes++;
            }
        }
        if (ones >= tows && ones >= threes) {
            result = 1;
        } else if (tows > ones && tows > threes) {
            result = 2;
        } else if (threes > ones && threes > tows) {
            result = 3;
        }
        //     System.out.print(result + "\t");
        return result;
    }

}

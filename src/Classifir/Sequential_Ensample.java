/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import static Classifir.Ensample.clusterIndex;
import static Classifir.Ensample.clusterResults;
import static Classifir.Kmeans2.INDX;
import Preprocessing.OutPut;
import Preprocessing.Reading;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author 17511035
 */
public class Sequential_Ensample extends Sequential_kmeans {

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

    public void Sequential_ensample_Implementation(String whichPOS, int winNumber) throws IOException {

        wNumber = winNumber;
        sequential_classifierImplementation(whichPOS, winNumber);
        xxx();
        accuracy();
        typRes(winNumber);
        ensampLearning.clear();
        clusterIndex.clear();
        clusterResults.clear();
    }

    public void sequential_classifierImplementation(String whichPOS, int winNumber) throws IOException {
        int index = 0;
        File dirPOWR;
        dirPOWR = new File("OUTPUT\\" + whichPOS + "\\POW_R\\" + winNumber);
        File[] filePOWR = dirPOWR.listFiles();
        for (File fr : filePOWR) {
            index++;
            System.out.print(index + " : ");
            long KmeansTimesS = System.currentTimeMillis();
            long KmeansTimesS2 = System.currentTimeMillis();
            sequential_kmeansImplementation(fr.toString(), 2, winNumber, true, 100, "cosin");
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
            
            sequential_kmeansImplementation(fr.toString(), 2, winNumber, true, 1, "cosin");
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

        wrting.results("OUTPUT\\kmeans_results\\Latex\\Win" + wNumber, whichPOS, resu_latex,"100Iter");
        wrting.results("OUTPUT\\kmeans_results\\Excel\\Win" + wNumber, whichPOS, resu,"100Iter");
        wrting.results("OUTPUT\\kmeans_results\\Latex\\Win" + wNumber, whichPOS, resu_latex2,"1Iter");
        wrting.results("OUTPUT\\kmeans_results\\Excel\\Win" + wNumber, whichPOS, resu2,"1Iter");
        System.out.println("::::: " + clusterIndex.get(0).length);
        for (int u = 0; u < clusterIndex.get(0).length; u++) {
            int[] val = new int[clusterIndex.size()];
            int idx = 0;
            for (int[] xx : clusterIndex) {
                val[idx] = xx[u];
                idx++;
            }
            clusterResults.add(val);
        }

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

    public void accuracy() throws IOException {
        System.out.println("--- Ensample ---");
        List<List<String>> confusingMatrix = new LinkedList<>();
        List<String> posGone = new ArrayList<>();
        List<String> posGtwo = new ArrayList<>();
        List<String> negGone = new ArrayList<>();
        List<String> negGtwo = new ArrayList<>();
        List<String> wrongDocs = new ArrayList<>();

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
        return result;
    }

}

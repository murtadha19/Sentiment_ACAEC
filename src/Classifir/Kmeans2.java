/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

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
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Kmeans2 extends OutPut {

    List<Double> dist = new ArrayList<>();

    List<Double> centroidID = new LinkedList<>();
    static double Silhouette_means = 0.0, Silhouette_seads = 0.0, MeanCosin = 0.0, MeanEuclidean = 0.0, MeanCityblock = 0.0, meansCorrelation = 0.0, MeanKL = 0.0,
            Silhouette = 0.0, difDseads1 = 0.0, difDseads2 = 0.0, avaDifDseads = 0.0, MeanChi = 0.0;
    static List<String> featuresLinkedList = new LinkedList<>();
    static List<Double[]> documentDoubleList = new ArrayList<>();
    Map<String, Double> documents = new LinkedHashMap<String, Double>();
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
    Map<Double, Double> groupOneDists = new LinkedHashMap<Double, Double>();
    Map<Double, Double> groupTwoDists = new LinkedHashMap<Double, Double>();
    Map<String, Integer> wrongDocssOne = new LinkedHashMap<String, Integer>();
    Map<String, Integer> wrongDocssTwo = new LinkedHashMap<String, Integer>();

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

    public void selection(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs, String fileName, boolean groupOneIsNEG, boolean groupOneIsPOS) throws IOException {
        List<Double[]> groupOne = new LinkedList<>();
        List<Double[]> groupTwo = new LinkedList<>();
        String G1Polarity = null, G2Polarity = null;
        Map<Double, Double> silhouette_Gone = new LinkedHashMap<Double, Double>();
        Map<String, Double> silhouette_G1 = new LinkedHashMap<>();

        Map<Double, Double> silhouette_Gtwo = new LinkedHashMap<Double, Double>();
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
                int documentNogroupOne = groupOne.size();
                a = round(SumSim / documentNogroupOne);

                if (groupOne.isEmpty()) {
                    a = 0.0;
                }
                sim = 0.0;
                b = 0.0;
                SumSim = 0.0;
                for (int h = 0; h < groupTwo.size(); h++) {
                    sim = distance(docGroups.get(g).get(i), groupTwo.get(h), "pp");
                    SumSim = SumSim + sim;
                }
                int documentNogroupTwo = groupTwo.size();
                b = round(SumSim / documentNogroupTwo);
                if (groupTwo.isEmpty()) {
                    b = 0.0;
                }
                double max = max(b, a);
                if (max == 0.0) {
                    max = 1;
                }
                if (g == 0) {
                    silhouette_Gone.put(docGroups.get(g).get(i)[0], round((b - a) / max));
                } else if (g == 1) {
                    silhouette_Gtwo.put(docGroups.get(g).get(i)[0], round((a - b) / max));
                }
            }
        }

        for (String key : docs.keySet()) {
            for (Double keyS : silhouette_Gone.keySet()) {
                if (docs.get(key) == keyS) {
                    silhouette_G1.put(key, silhouette_Gone.get(keyS));
                }
            }
        }
        for (String key : docs.keySet()) {
            for (Double keyS : silhouette_Gtwo.keySet()) {
                if (docs.get(key) == keyS) {
                    silhouette_G2.put(key, silhouette_Gtwo.get(keyS));
                }
            }
        }
        silhouette_Gtwo.clear();
        silhouette_Gone.clear();
        writeSilhouette(silhouette_G1, silhouette_G2, fileName, G1Polarity, G2Polarity);
        silhouette_G1.clear();
        silhouette_G2.clear();
    }

    public void writeSilhouette(Map<String, Double> silhouette_G1, Map<String, Double> silhouette_G2, String fileName, String G1Polarity, String G2Polarity) throws UnsupportedEncodingException, IOException {
        String[] f1 = fileName.split("\\\\");

        String G1max_name = "null";
        Double G1max_value = 0.0;
        for (Entry<String, Double> entry : silhouette_G1.entrySet()) {
            if (entry.getKey().equals("point-pos") || entry.getKey().equals("point-neg")) {
            } else {
                if (G1max_name.equals("null")) {
                    G1max_name = entry.getKey();
                    G1max_value = entry.getValue();
                } else if (G1max_value < entry.getValue()) {
                    G1max_name = entry.getKey();
                    G1max_value = entry.getValue();
                }
            }
        }
        String G2max_name = "null";
        Double G2max_value = 0.0;
        for (Entry< String, Double> entry : silhouette_G2.entrySet()) {
            if (entry.getKey().equals("point-pos") || entry.getKey().equals("point-neg")) {
            } else {
                if (G2max_name.equals("null")) {
                    G2max_name = entry.getKey();
                    G2max_value = entry.getValue();
                } else if (G2max_value < entry.getValue()) {
                    G2max_name = entry.getKey();
                    G2max_value = entry.getValue();
                }
            }
        }
        File dirPOW = new File("OUTPUT\\ProcessedDoc\\DateSeries_windowNumber\\");
        File[] filePOW = dirPOW.listFiles();
        int winNumber = filePOW.length;

        BufferedWriter newFileSWN = null;
        BufferedWriter newFileSWN2 = null;
        BufferedWriter newFileSWN3 = null;
        BufferedWriter newFileSWN4 = null;
        try {
            newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\DateSeriesSilhouette\\Win" + winNumber + "\\kmeans\\G1\\" + G1Polarity + f1[f1.length - 2] + f1[f1.length - 1]));
            newFileSWN2 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\DateSeriesSilhouette\\Win" + winNumber + "\\kmeans\\G2\\" + G2Polarity + f1[f1.length - 2] + f1[f1.length - 1]));
            newFileSWN3 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\DateSeriesSilhouette\\Win" + winNumber + "\\kmeans\\Max\\G1\\" + G1Polarity + f1[f1.length - 2] + f1[f1.length - 1]));
            newFileSWN4 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\DateSeriesSilhouette\\Win" + winNumber + "\\kmeans\\Max\\G2\\" + G2Polarity + f1[f1.length - 2] + f1[f1.length - 1]));
            for (String H : silhouette_G1.keySet()) {
                newFileSWN.write(H + "\t" + silhouette_G1.get(H));
                newFileSWN.newLine();
            }
            for (String x : silhouette_G2.keySet()) {
                newFileSWN2.write(x + "\t" + silhouette_G2.get(x));
                newFileSWN2.newLine();
            }
            if (silhouette_G1.isEmpty()) {
                System.out.println("THE silhouette_G1 IS EMPTY ");
            } else {
                newFileSWN3.write(G1max_name + "\t" + G1max_value);
                newFileSWN3.newLine();
            }
            if (silhouette_G2.isEmpty()) {
                System.out.println("THE silhouette_G2 IS EMPTY ");
            } else {
                newFileSWN4.write(G2max_name + "\t" + G2max_value);
                newFileSWN4.newLine();
            }

            newFileSWN.close();
            newFileSWN2.close();
            newFileSWN3.close();
            newFileSWN4.close();
        } catch (IOException e) {
            System.out.println("Error opening the file");
            e.getMessage();
        } finally {
            if (newFileSWN != null) {
                newFileSWN.close();
            } else {
                System.out.println("writeSilhouette1");
            }
            if (newFileSWN2 != null) {
                newFileSWN2.close();
            } else {
                System.out.println("writeSilhouette2");
            }
            if (newFileSWN3 != null) {
                newFileSWN3.close();
            } else {
                System.out.println("writeSilhouette3");
            }
            if (newFileSWN4 != null) {
                newFileSWN4.close();
            } else {
                System.out.println("writeSilhouette4");
            }
        }
    }

    public double means(List<LinkedList<Double[]>> docGroups, String distanceType) {
        double distance = 0.0;
        List<Double[]> means = new LinkedList<>();
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            if (docGroups.get(0).get(i)[0] != centroidID.get(0) || docGroups.get(0).get(i)[0] != centroidID.get(1)) {
                groupOne.add(docGroups.get(0).get(i));
            }
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            if (docGroups.get(1).get(i)[0] != centroidID.get(0) || docGroups.get(1).get(i)[0] != centroidID.get(1)) {
                groupTwo.add(docGroups.get(1).get(i));
            }
        }
        means.add(average((LinkedList<Double[]>) groupOne));
        means.add(average((LinkedList<Double[]>) groupTwo));

        for (int i = 0; i < means.get(0).length; i++) {
            means.get(0)[i] = round(means.get(0)[i] + 0.01);
            means.get(1)[i] = round(means.get(1)[i] + 0.01);
        }

        if (distanceType == "cosin") {
            distance = distance(means.get(0), means.get(1), "mm");
        }
        if (distanceType == "euclidean") {
            distance = euclidean(means.get(0), means.get(1), "mm");
        }
        if (distanceType == "cityblock") {
            distance = cityblock(means.get(0), means.get(1), "mm");
        }
        means.removeAll(means);
        groupOne.removeAll(groupOne);
        groupTwo.removeAll(groupTwo);
        distance = round(distance);

        if (distanceType == "cosin") {
            MeanCosin = round(distance);
        }
        if (distanceType == "euclidean") {
            MeanEuclidean = round(distance);
        }
        if (distanceType == "cityblock") {
            MeanCityblock = round(distance);
        }

        return distance;
    }

    public double meansCorrelation(List<LinkedList<Double[]>> docGroups) {
        double corre = 0.0;
        List<Double[]> means = new LinkedList<>();
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            if (docGroups.get(0).get(i)[0] != centroidID.get(0) || docGroups.get(0).get(i)[0] != centroidID.get(0)) {
                groupOne.add(docGroups.get(0).get(i));
            }
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            if (docGroups.get(1).get(i)[0] != centroidID.get(1) || docGroups.get(1).get(i)[0] != centroidID.get(1)) {
                groupTwo.add(docGroups.get(1).get(i));
            }
        }
        means.add(average((LinkedList<Double[]>) groupOne));
        means.add(average((LinkedList<Double[]>) groupTwo));
        double sumM1 = 0.0;
        double sumM2 = 0.0;

        for (int i = 0; i < means.get(0).length; i++) {
            sumM1 = sumM1 + (means.get(0)[i]);
            sumM2 = sumM2 + (means.get(1)[i]);
        }
        for (int i = 0; i < means.get(0).length; i++) {
            means.get(0)[i] = round(means.get(0)[i] / sumM1);
            means.get(1)[i] = round(means.get(1)[i] / sumM2);
        }
        corre = correlation(means.get(0), means.get(1), "mm");
        means.removeAll(means);
        groupOne.removeAll(groupOne);
        groupTwo.removeAll(groupTwo);
        meansCorrelation = corre;
        return corre;
    }

    public double means_KL(List<LinkedList<Double[]>> docGroups) {
        //Kullback–Leibler divergence
        double KL = 0.0;
        double sumM1 = 0.0;
        double sumM2 = 0.0;
        List<Double[]> means = new LinkedList<>();
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            if (docGroups.get(0).get(i)[0] != centroidID.get(0) || docGroups.get(0).get(i)[0] != centroidID.get(0)) {
                groupOne.add(docGroups.get(0).get(i));
            }
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            if (docGroups.get(1).get(i)[0] != centroidID.get(1) || docGroups.get(1).get(i)[0] != centroidID.get(1)) {
                groupTwo.add(docGroups.get(1).get(i));
            }
        }
        means.add(average((LinkedList<Double[]>) groupOne));
        means.add(average((LinkedList<Double[]>) groupTwo));

        for (int i = 0; i < means.get(0).length; i++) {
            sumM1 = sumM1 + (means.get(0)[i]);
            sumM2 = sumM2 + (means.get(1)[i]);
        }
        for (int i = 0; i < means.get(0).length; i++) {
            means.get(0)[i] = round(means.get(0)[i] / sumM1);
            means.get(1)[i] = round(means.get(1)[i] / sumM2);
        }
        for (int i = 0; i < means.get(0).length; i++) {
            KL = KL + (round(means.get(0)[i] * round(log(round(means.get(0)[i] / means.get(1)[i])))));
        }
        means.removeAll(means);
        groupOne.removeAll(groupOne);
        groupTwo.removeAll(groupTwo);
        KL = round(KL);
        MeanKL = KL;
        return KL;
    }

    public double chi(List<LinkedList<Double[]>> docGroups) {
        double chi = 0.0;
        double sumM1 = 0.0;
        double sumM2 = 0.0;
        List<Double[]> means = new LinkedList<>();
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            if (docGroups.get(0).get(i)[0] != centroidID.get(0) || docGroups.get(0).get(i)[0] != centroidID.get(0)) {
                groupOne.add(docGroups.get(0).get(i));
            }
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            if (docGroups.get(1).get(i)[0] != centroidID.get(1) || docGroups.get(1).get(i)[0] != centroidID.get(1)) {
                groupTwo.add(docGroups.get(1).get(i));
            }
        }
        means.add(average((LinkedList<Double[]>) groupOne));
        means.add(average((LinkedList<Double[]>) groupTwo));

        for (int i = 0; i < means.get(0).length; i++) {
            sumM1 = sumM1 + (means.get(0)[i]);
            sumM2 = sumM2 + (means.get(1)[i]);
        }
        for (int i = 0; i < means.get(0).length; i++) {
            means.get(0)[i] = round(means.get(0)[i] / sumM1);
            means.get(1)[i] = round(means.get(1)[i] / sumM2);
        }
        for (int i = 0; i < means.get(0).length; i++) {
            chi = chi + round(((means.get(0)[i] - means.get(1)[i]) * (means.get(0)[i] - means.get(1)[i])) / means.get(1)[i]);
        }
        means.removeAll(means);
        groupOne.removeAll(groupOne);
        groupTwo.removeAll(groupTwo);
        chi = round(chi);
        MeanChi = chi;
        return chi;
    }

    public void Silhouette_means(List<LinkedList<Double[]>> docGroups, String docPath) throws IOException {
        List<Double[]> means = new LinkedList<>();
        List<Double> Ss_groupOne = new LinkedList<Double>();
        List<Double> Ss_groupTwo = new LinkedList<Double>();
        double S_groupTwo = 0.0, S_groupOne = 0.0, a = 0.0, b = 0.0;
        Silhouette_means = 0.0;
        int count = 0;
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            if (docGroups.get(0).get(i)[0] != centroidID.get(0) || docGroups.get(0).get(i)[0] != centroidID.get(0)) {
                groupOne.add(docGroups.get(0).get(i));
            }
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            if (docGroups.get(1).get(i)[0] != centroidID.get(1) || docGroups.get(1).get(i)[0] != centroidID.get(1)) {
                groupTwo.add(docGroups.get(1).get(i));
            }
        }
        means.add(average((LinkedList<Double[]>) groupOne));
        means.add(average((LinkedList<Double[]>) groupTwo));

        for (int j = 0; j < groupOne.size(); j++) {
            a = distance(groupOne.get(j), means.get(0), "pm");
            b = distance(groupOne.get(j), means.get(1), "pm");

            // a = 1 - euclidean(groupOne.get(j), means.get(0), "pm");
            // b = 1 - euclidean(groupOne.get(j), means.get(1), "pm");
            Ss_groupOne.add(round(((b - a) / max(b, a))));
        }

        for (int i = 0; i < groupTwo.size(); i++) {
            b = distance(groupTwo.get(i), means.get(0), "pm");
            a = distance(groupTwo.get(i), means.get(1), "pm");

            // b = 1 - euclidean(groupTwo.get(i), means.get(0), "pm");
            //  a = 1 - euclidean(groupTwo.get(i), means.get(1), "pm");
            Ss_groupTwo.add(round(((b - a) / max(b, a))));
        }

        for (int x = 0; x < Ss_groupOne.size(); x++) {
            S_groupOne = S_groupOne + Ss_groupOne.get(x);
            Silhouette_means = Silhouette_means + Ss_groupOne.get(x);
            count++;
        }
        S_groupOne = round(S_groupOne / Ss_groupOne.size());

        for (int z = 0; z < Ss_groupTwo.size(); z++) {
            S_groupTwo = S_groupTwo + Ss_groupTwo.get(z);
            Silhouette_means = Silhouette_means + Ss_groupTwo.get(z);
            count++;
        }
        S_groupTwo = round(S_groupTwo / Ss_groupTwo.size());
        Silhouette_Values_means(Ss_groupTwo, Ss_groupOne, docPath);

        means.removeAll(means);
        Ss_groupOne.removeAll(Ss_groupOne);
        Ss_groupTwo.removeAll(Ss_groupTwo);
        groupOne.removeAll(groupOne);
        groupTwo.removeAll(groupTwo);
        Silhouette_means = round(Silhouette_means / count);
    }

    public double seedDistance(List<LinkedList<Double[]>> docGroups) {
        return distance(seads.get(0), seads.get(1), "mm");
    }

    public double seedTOmeans(List<LinkedList<Double[]>> docGroups) {

        List<Double[]> means = new LinkedList<>();
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            if (docGroups.get(0).get(i)[0] != centroidID.get(0) || docGroups.get(0).get(i)[0] != centroidID.get(0)) {
                groupOne.add(docGroups.get(0).get(i));
            }
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            if (docGroups.get(1).get(i)[0] != centroidID.get(1) || docGroups.get(1).get(i)[0] != centroidID.get(1)) {
                groupTwo.add(docGroups.get(1).get(i));
            }
        }
        means.add(average((LinkedList<Double[]>) groupOne));
        means.add(average((LinkedList<Double[]>) groupTwo));

        difDseads1 = distance(seads.get(0), means.get(0), "mm") - distance(seads.get(0), means.get(1), "mm");
        difDseads2 = distance(seads.get(1), means.get(0), "mm") - distance(seads.get(1), means.get(1), "mm");
        avaDifDseads = (difDseads1 + difDseads2) / 2;

        means.removeAll(means);
        groupOne.removeAll(groupOne);
        groupTwo.removeAll(groupTwo);
        return avaDifDseads;
    }

    public void Silhouette_seeds(List<LinkedList<Double[]>> docGroups, String docPath) throws IOException {
        List<Double> Ss_groupOne = new LinkedList<Double>();
        List<Double> Ss_groupTwo = new LinkedList<Double>();
        double S_groupTwo = 0.0, S_groupOne = 0.0, a = 0.0, b = 0.0;
        List<Double[]> centroidOne = new LinkedList<>();
        List<Double[]> centroidTwo = new LinkedList<>();

        int count = 0;
        Silhouette_seads = 0.0;
        for (int i = 0; i < docGroups.get(0).size(); i++) {
            if (docGroups.get(0).get(i)[0] != centroidID.get(0) || docGroups.get(0).get(i)[0] != centroidID.get(1)) {
                groupOne.add(docGroups.get(0).get(i));
            }
            if (docGroups.get(0).get(i)[0] == centroidID.get(0)) {
                centroidOne.add(seads.get(0));
            }
            if (docGroups.get(0).get(i)[0] == centroidID.get(1)) {
                centroidOne.add(seads.get(1));
            }
        }
        for (int i = 0; i < docGroups.get(1).size(); i++) {
            if (docGroups.get(1).get(i)[0] != centroidID.get(0) || docGroups.get(1).get(i)[0] != centroidID.get(1)) {
                groupTwo.add(docGroups.get(1).get(i));
            }

            if (docGroups.get(1).get(i)[0] == centroidID.get(0)) {
                centroidTwo.add(seads.get(0));
            }
            if (docGroups.get(1).get(i)[0] == centroidID.get(1)) {
                centroidTwo.add(seads.get(1));
            }
        }
        if (centroidTwo.isEmpty()) {
            System.out.println("The group is empty ");
        }
       System.out.println("centroidTwo.get(0) " + centroidTwo.get(0).length);
        int size = centroidTwo.size() + centroidOne.size();
        if (size == 2) {
            if (groupOne.isEmpty()) {
            } else {
                for (int j = 0; j < groupOne.size(); j++) {
                    a = distance(groupOne.get(j), centroidOne.get(0), "pp");
                    b = distance(groupOne.get(j), centroidTwo.get(0), "pp");
                    Ss_groupOne.add(round(((b - a) / max(b, a))));
                }
                for (int x = 0; x < Ss_groupOne.size(); x++) {
                    Silhouette_seads = Silhouette_seads + Ss_groupOne.get(x);
                    count++;
                }
            }
            if (groupTwo.isEmpty()) {
            } else {
                for (int i = 0; i < groupTwo.size(); i++) {
                    a = distance(groupTwo.get(i), centroidTwo.get(0), "pp");
                    b = distance(groupTwo.get(i), centroidOne.get(0), "pp");
                    Ss_groupTwo.add(round(((b - a) / max(b, a))));
                }

                for (int z = 0; z < Ss_groupTwo.size(); z++) {
                    Silhouette_seads = Silhouette_seads + Ss_groupTwo.get(z);
                    count++;
                }
            }
            Silhouette_Values_means(Ss_groupTwo, Ss_groupOne, docPath);

            centroidOne.removeAll(centroidOne);
            centroidTwo.removeAll(centroidTwo);
            Ss_groupOne.removeAll(Ss_groupOne);
            Ss_groupTwo.removeAll(Ss_groupTwo);
            groupOne.removeAll(groupOne);
            groupTwo.removeAll(groupTwo);
            Silhouette_seads = round(Silhouette_seads / count);
        } else {
            System.out.println("This group not clustering is not accurate");
        }
    }

    public void docTracking(int iter) {
        System.out.println("Iteration : " + iter);
        List<String> groupOneName = new LinkedList<>();
        List<String> groupTwoName = new LinkedList<>();

        List<Double> groupOne = new LinkedList<>();
        List<Double> groupTwo = new LinkedList<>();
        for (int i = 0; i < groups.get(0).size(); i++) {
            groupOne.add(groups.get(0).get(i)[0]);
        }

        for (int i = 0; i < groups.get(1).size(); i++) {
            groupTwo.add(groups.get(1).get(i)[0]);
        }
        for (Double x : groupOne) {
            for (String k : documents.keySet()) {
                if (documents.get(k) == x) {
                    groupOneName.add(k);

                }
            }
        }
        for (Double x : groupTwo) {
            for (String k : documents.keySet()) {
                if (documents.get(k) == x) {
                    groupTwoName.add(k);
                }
            }
        }
        for (String ss : groupOneName) {
            if (wrongDocssOne.containsKey(ss)) {
                wrongDocssOne.put(ss, wrongDocssOne.get(ss) + 1);
            } else {
                wrongDocssOne.put(ss, 1);
            }
        }
        for (String ss : groupTwoName) {
            if (wrongDocssTwo.containsKey(ss)) {
                wrongDocssTwo.put(ss, wrongDocssTwo.get(ss) + 1);
            } else {
                wrongDocssTwo.put(ss, 1);
            }
        }

        System.out.println("DOCS IN GRUOP 1:");
        for (String s : groupOneName) {
            System.out.print(s + "\t");
        }
        System.out.println();
        System.out.println("DOCS IN GRUOP 2:");
        for (String s : groupTwoName) {
            System.out.print(s + "\t");
        }
        System.out.println();
    }

    public void wrongDoc(int iter) {
        System.out.println(" Number of assigning each documents to a  particular group. Number of iteration is " + iter);
        System.out.println("DOCS IN GRUOP 1:");
        for (String s : wrongDocssOne.keySet()) {
            System.out.println(s + "\t" + wrongDocssOne.get(s));
        }
        System.out.println();
        System.out.println("DOCS IN GRUOP 2:");
        for (String s : wrongDocssTwo.keySet()) {
            System.out.println(s + "\t" + wrongDocssTwo.get(s));
        }
        System.out.println();
        wrongDocssOne.clear();
        wrongDocssTwo.clear();

    }

    public void kmeansImplementation(String docPath, int k, int iterNo, String distanceType) throws IOException {
        double avaregeDis1 = 0.0, avaregeDis2 = 0.0, docSizeG1 = 0.0, docSizeG2 = 0.0;

        stopClustring = false;
        String kmeasType = null;
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
        double numberOfPositiveFeatures = 0.0;
        double numberOfNagativeFeature = 0.0;
        Double docReference = 0.0;
        if (iter == 1) {
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
                        if (cet[i] != 0.0 && values[0].equals("point-pos")) {
                            numberOfPositiveFeatures++;
                        }
                        if (cet[i] != 0.0 && values[0].equals("point-neg")) {
                            numberOfNagativeFeature++;
                        }
                    }
                    centroidID.add(docReference);
                    x++;
                    centroidsList.add(cetRow);
                    seads.add(cet);
                } //if you would like to inclued the two points and then jugde the groups.

             //   else {
                    docRow[0] = docReference;
                    for (int i = 1; i < values.length; i++) {
                        docRow[i] = Double.parseDouble(values[i]);
                    }
                    documentDoubleList.add(docRow);
                    documents.put(values[0], docReference);
            //    }

            }
            if (noFirstPoint == true) {
                throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers  " + centroidsList.size());
            }
            if (centroidsList.size() != k) {
                throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers " + centroidsList.size());
            }
        }
        double rp = 0.0, rn = 0.0;
        rp = numberOfPositiveFeatures / (numberOfPositiveFeatures + numberOfNagativeFeature);
        rn = numberOfNagativeFeature / (numberOfPositiveFeatures + numberOfNagativeFeature);
        System.out.println("positive : " + numberOfPositiveFeatures + " The ratio is " + rp);
        System.out.println("nagative: " + numberOfNagativeFeature + " The ratio is " + rn);
        System.out.println(" minus : " + (numberOfPositiveFeatures - numberOfNagativeFeature) + " minus ratio : " + (rp - rn));
        do {
            double sim = 0.0;
            //  System.out.println("Iteration : " + iter);
            int o = 0;
            for (Double[] doc : documentDoubleList) {

                String[] v = docs.get(o).split("\t");
                o++; //paper3
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

                avaregeDis1 = avaregeDis1 + rowDistance.get(0);
                avaregeDis2 = avaregeDis2 + rowDistance.get(1);

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
                    centroidsList.set(i, average(groups.get(i)));
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
            if (stopClustring == false) {
                for (int i = 0; i < groups.size(); i++) {
                    groups.get(i).removeAll(groups.get(i));
                }
            }

            if (stopClustring == true) {
                //  Silhouette(groups);
                //  Silhouette_means(groups, docPath);
                //   means(groups, "cosin");
                //  means(groups, "euclidean");
                //  means(groups, "cityblock");
                //    means_KL(groups);
                //    chi(groups);
                //     meansCorrelation(groups);
                // seedTOmeans(groups);
                //  Silhouette_seeds(groups, docPath);
                docSizeG1 = groups.get(0).size();
                docSizeG2 = groups.get(1).size();
                fanilOutbut(groups, documents, docPath);
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
        // System.out.println("The iterations no is : " + iter);
        iteratinos = iter;
        System.out.println("avaregeDis1 : : " + avaregeDis1 / docSizeG1);
        System.out.println("avaregeDis2 : : " + avaregeDis2 / docSizeG2);
        Collections.sort(dist);
        for (double d : dist) {
            //   System.out.println(d);
        }
    }

    public Double[] average(LinkedList<Double[]> list) {
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
            go[indx] = rowd;
            indx++;
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

    public void gruopsMeans(List<LinkedList<Double[]>> docGroups, boolean groupOneIsNEG, boolean groupOneIsPOS, boolean accuract, String filename) throws IOException {
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
        mean1 = average((LinkedList<Double[]>) gruop1);
        mean2 = average((LinkedList<Double[]>) gruop2);
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
            newFileSWN.write(G1pol + "\t");
            for (int r = 0; r < mean1.length; r++) {
                newFileSWN.write(mean1[r] + "\t");
            }
            newFileSWN.newLine();
            newFileSWN.write(G2pol + "\t");

            for (int r = 0; r < mean2.length; r++) {
                newFileSWN.write(mean2[r] + "\t");
            }
            newFileSWN.flush();
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

    private void fanilOutbut(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs, String filename) throws IOException {
        System.out.println("***** clustering ******* ");
        System.out.print(filename);
        System.out.println("");
        List<List<String>> confusingMatrix = new LinkedList<>();
        List<String> posGone = new ArrayList<String>();
        List<String> posGtwo = new ArrayList<String>();
        List<String> negGone = new ArrayList<String>();
        List<String> negGtwo = new ArrayList<String>();
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
        for (int inx = 0; inx < docGroups.size(); inx++) {
            groupNo = inx;
            groupNo++;
            for (int in = 0; in < docGroups.get(inx).size(); in++) {
                for (Map.Entry entry : docs.entrySet()) {
                    if (entry.getValue() == docGroups.get(inx).get(in)[0]) {
                        grouDoc.put(entry.getKey().toString(), groupNo);
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
            //   System.out.println("group one has TWO seeds inaccurate  ");
        } else if ((groupOneIsNEG == false) && (groupOneIsPOS == false)) {
            //  System.out.println("group one has NO seeds inaccurate  ");

        } else if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
            accuract = true;
            //  System.out.println("group 1 --> POS : group 2-->NEG : based one seeds");

        } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
            accuract = true;
            //   System.out.println("group 2 --> POS : group 1 --> NEG : based one seeds");
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
        confusingMatrix.add(posGone);
        confusingMatrix.add(negGtwo);
        confusingMatrix.add(posGtwo);
        confusingMatrix.add(negGone);
        if ((posGoneSize + negGtwoSize) >= (posGtwoSize + negGoneSize)) {
            //      if ((groupOneIsNEG == false) && (groupOneIsPOS == true)) {
            //group 1 is positive
            // 2--> neg
            System.out.println("group 1 --> POS : group 2-->NEG ");

            accuracy = ((posGoneSize + negGtwoSize) / grouDocSize);
            precision = posGoneSize / (posGoneSize + negGoneSize);
            recall = posGoneSize / (posGoneSize + posGtwoSize);
            f_measure = (2 * precision * recall) / (precision + recall);
        } else {
            //   } else if ((groupOneIsNEG == true) && (groupOneIsPOS == false)) {
            //  1--> neg
            // group 2 is positive
            //    System.out.println((posGoneSize + negGtwoSize) + " > " + (posGtwoSize + negGoneSize));
            System.out.println("group 2 --> POS : group 1 --> NEG");
            accuracy = ((posGtwoSize + negGoneSize) / grouDocSize);
            precision = posGtwoSize / (posGtwoSize + negGtwoSize);
            recall = posGtwoSize / (posGtwoSize + posGoneSize);
            f_measure = (2 * precision * recall) / (precision + recall);
            //wrong classified docs are in posGone and in negGtwo.
            //  nuturalClass(posGone, negGtwo, documents);
        }
        gruopsMeans(docGroups, groupOneIsNEG, groupOneIsPOS, accuract, filename);
        System.out.println("Silhouette" + "\t"
                + "Silhouette_means" + "\t"
                + "Silhouette_seads" + "\t"
                + "MeanCosin" + "\t"
                + "MeanEuclidean" + "\t"
                + "MeanCityblock" + "\t"
                + "meansCorrelation" + "\t"
                + "MeanKL" + "\t"
                + "MeanChi" + "\t"
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

        actualPositivityk = round((posGone.size() + posGtwo.size()) / grouDocSize);
        actualNegativityk = round((negGone.size() + negGtwo.size()) / grouDocSize);
        balancek = Math.abs(actualPositivityk - actualNegativityk);
        results
                = Double.toString(Silhouette) + "\t"
                + Double.toString(Silhouette_means) + "\t"
                + Double.toString(Silhouette_seads) + "\t"
                + Double.toString(MeanCosin) + "\t"
                + Double.toString(MeanEuclidean) + "\t"
                + Double.toString(MeanCityblock) + "\t"
                + Double.toString(meansCorrelation) + "\t"
                + Double.toString(MeanKL) + "\t"
                + Double.toString(MeanChi) + "\t"
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
        System.out.println(
                Double.toString(round(accuracy * 100, 2)) + "\t"
                + Double.toString(round(precision * 100, 2)) + "\t"
                + Double.toString(round(recall * 100, 2)) + "\t"
                + Double.toString(round(f_measure * 100, 2)) + "\t"
                + Integer.toString(iteratinos));
        results_latex
                = Double.toString(Silhouette) + " & "
                + Double.toString(Silhouette_means) + " & "
                + Double.toString(Silhouette_seads) + " & "
                + Double.toString(MeanCosin) + " & "
                + Double.toString(MeanEuclidean) + " & "
                + Double.toString(MeanCityblock) + " & "
                + Double.toString(meansCorrelation) + " & "
                + Double.toString(MeanKL) + " & "
                + Double.toString(MeanChi) + " & "
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
        System.out.println(" accuract ;;;; " + accuract);
        if (accuract == true) {

            for (String entr : treeOfGroupIndex.keySet()) {
                //  val.add(treeOfGroupIndex.get(entr));
                INDX[indx] = treeOfGroupIndex.get(entr);
                indx++;
                if (docsEnsample.isEmpty() || docsEnsample.size() < treeOfGroupIndex.size()) {
                    docsEnsample.add(entr);
                }
                //System.out.print(" "+treeOfGroupIndex.get(entr));
            }
        } else {
            Arrays.fill(INDX, 0);
        }
        System.out.println("docsEnsample   ;;;; " + docsEnsample.size());
        if (Silhouette_means > 0.2) {
            //   Arrays.fill(INDX, 0);
        }
//        typeResult(treeOfGroupIndex, filename);
        // selection(docGroups, docs, filename, groupOneIsNEG, groupOneIsPOS);
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

    public double round(double value) {
        double db = 0.0;
        db = Math.round(value * 100000) / 100000.0;
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
            System.out.println("Failed to files : " + ioex.getMessage());
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
        int IndexCent = centroidIndexValue;
        for (int instanceIndex = instanceIndexValue; instanceIndex < instanceValue.length; instanceIndex++) {
            dist = dist + (instanceValue[instanceIndex] - centroidValue[IndexCent]) * (instanceValue[instanceIndex] - centroidValue[IndexCent]);
            IndexCent++;
        }

        return round(Math.sqrt(dist));
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
        avgIn = round(sumIn / instanceValue.length - 1);

        for (int j = centroidIndexValue; j < centroidValue.length; j++) {
            stdSumCe = stdSumCe + ((centroidValue[j] - avgCe) * (centroidValue[j] - avgCe));
        }
        stdCe = round(Math.sqrt(stdSumCe / centroidValue.length));

        for (int i = instanceIndexValue; i < instanceValue.length; i++) {
            stdSumIn = stdSumIn + ((instanceValue[i] - avgIn) * (instanceValue[i] - avgIn));
        }
        stdIn = round(Math.sqrt(stdSumIn / instanceValue.length - 1));
        int length = 0;
        for (int iIns = instanceIndexValue; iIns < instanceValue.length; iIns++) {
            cov = cov + ((instanceValue[iIns] - avgIn) * (centroidValue[iSCen] - avgCe));
            iSCen++;
            length++;
        }

        cov = round(cov / length);
        correlation = round(cov / (stdCe * stdIn));
        return round(correlation);
    }

    //////////////////////////
    private double cosineSimilarity(Double[] instanceValue, Double[] centroidValue, int division) {
        ArrayList<Double> Distances = new ArrayList<>();
        double euclideanInstanceValue = 0.0;
        double euclideanCentroidValue = 0.0;
        double dotProduct = 0.0;
        double cosineSimilarity = 0.0;
        for (int iIns = 1; iIns < instanceValue.length; iIns++) {
            euclideanInstanceValue = euclideanInstanceValue + (instanceValue[iIns] * instanceValue[iIns]);
        }
        euclideanInstanceValue = Math.sqrt(euclideanInstanceValue);
        for (Double[] sudCent : centroidPartioning(centroidValue, division)) {
            int iSCen = 0;
            for (int iCen = 0; iCen < sudCent.length; iCen++) {
                euclideanCentroidValue = euclideanCentroidValue + (sudCent[iCen] * sudCent[iCen]);
            }
            euclideanCentroidValue = Math.sqrt(euclideanCentroidValue);
            for (int iSIns = 1; iSIns < instanceValue.length; iSIns++) {
                dotProduct = dotProduct + (instanceValue[iSIns] * sudCent[iSCen]);
                iSCen++;
            }

            cosineSimilarity = dotProduct / (euclideanInstanceValue * euclideanCentroidValue);
            Distances.add(cosineSimilarity);
        }

        return round(Collections.min(Distances));
    }

    private List<Double[]> centroidPartioning(Double[] centroidValue, int division) {
        List<Double[]> centroidPortion = new ArrayList<>(division);
        int cinSize = 0;
        if (centroidValue.length % division == 0) {
            cinSize = centroidValue.length / division;
        } else {
            cinSize = (centroidValue.length - 1) / division;
        }
        int incrimantil = 0;
        Double[] subCentroid = new Double[centroidValue.length];
        Arrays.fill(subCentroid, 0.0);
        for (int x = 0; x < division; x++) {
            for (int indx = 0; indx < cinSize; indx++) {
                subCentroid[indx] = centroidValue[incrimantil];
                incrimantil++;
            }
            centroidPortion.add(subCentroid);
        }
        return centroidPortion;
    }





}

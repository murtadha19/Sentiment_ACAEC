/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import static Classifir.Sequential_kmeans.featuresLinkedList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.Double.max;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author 17511035
 */
public class Selection {

    List<String> docG1List = new LinkedList<>();
    List<String> docG2List = new LinkedList<>();
    List<LinkedList<Double[]>> docGroups = new LinkedList<>();
    int winNumber = 0;
    String filename = "", G1Polarity = "", G2Polarity = "";
    int iterNo = 0;
    Map<String, Double> documents = new LinkedHashMap<>();

    public void doSelection() throws IOException {
        winNo();
        select_learmers();
        selection_esenmble();
    }

    public void selection_esenmble() throws IOException {

        for (int x = 0; x <= winNumber; x++) {
            System.out.println("WINDOW NUMBER : " + x);
            reviewSelection(1, x, "SilhouetteCoefficient");
            printSelectedReviews();
            reviewSelection(2, x, "SilhouetteCoefficient");
            printSelectedReviews();

            reviewSelection(1, x, "MeanSilhouetteCoefficient");
            printSelectedReviews();
            reviewSelection(2, x, "MeanSilhouetteCoefficient");
            printSelectedReviews();

            reviewSelection(1, x, "DistanceMean");
            printSelectedReviews();
            reviewSelection(2, x, "DistanceMean");
            printSelectedReviews();
        }

    }

    public void select_learmers() throws IOException {
        for (int i = 1; i <= winNumber; i++) {
            readin(i, "cosin"); //euclidean cityblock cosin
            documents.clear();
            docGroups.removeAll(docGroups);
        }
    }

    public void winNo() {
        File dirPOW = new File("OUTPUT\\ProcessedDoc\\DateSeries_windowNumber\\");
        File[] filePOW = dirPOW.listFiles();
        winNumber = filePOW.length;
    }
//euclidean cityblock cosin

    public void readin(int winNumber, String dis) throws FileNotFoundException, IOException {
        LinkedList<Double[]> group1 = new LinkedList<>();
        LinkedList<Double[]> group2 = new LinkedList<>();
        File groups = new File("OUTPUT\\ProcessedDoc\\groups\\" + winNumber + "\\");
        File[] files = groups.listFiles();
        File groupDoc = new File("OUTPUT\\ProcessedDoc\\groupDoc\\" + winNumber + "\\");
        File[] filesDoc = groupDoc.listFiles();
        File dirgroupInf = new File("OUTPUT\\ProcessedDoc\\groupInf\\" + winNumber + "\\");
        File[] filesInf = dirgroupInf.listFiles();
       
        for (int z = 0; z < files.length; z++) {
            System.out.println(files[z]);
            try (BufferedReader inputstreamG1 = new BufferedReader(new FileReader(files[z] + "\\g1.txt"));
                    BufferedReader inputstreamG2 = new BufferedReader(new FileReader(files[z] + "\\g2.txt"))) {
                String doc;
                while ((doc = inputstreamG1.readLine()) != null) {
                    if (doc.length() > 0) {
                        String value[] = doc.split("\t");
                        Double val[] = new Double[value.length];
                        for (int i = 0; i < value.length; i++) {
                            double x = Double.parseDouble(value[i]);
                            val[i] = x;
                        }
                        group1.add(val);
                    }
                }
                while ((doc = inputstreamG2.readLine()) != null) {
                    if (doc.length() > 0) {
                        String value[] = doc.split("\t");
                        Double val[] = new Double[value.length];
                        for (int i = 0; i < value.length; i++) {
                            double x = Double.parseDouble(value[i]);
                            val[i] = x;
                        }
                        group2.add(val);
                    }
                }
                docGroups.add(group1);
                docGroups.add(group2);
            } catch (IOException e) {
                e.getMessage();
            }

            filename = files[z].toString();
            try (BufferedReader inputstreamG2 = new BufferedReader(new FileReader(filesDoc[z] + "\\docs.txt"))) {
                String doc;
                while ((doc = inputstreamG2.readLine()) != null) {
                    if (doc.length() > 0) {
                        String value[] = doc.split("\t");
                        documents.put(value[0], Double.parseDouble(value[1]));
                    }
                }
            }
            try (BufferedReader inputstreamG2 = new BufferedReader(new FileReader(filesInf[z] + "\\inf.txt"))) {
                String doc;
                while ((doc = inputstreamG2.readLine()) != null) {
                    if (doc.length() > 0) {
                        String value[] = doc.split("\t");
                        G1Polarity = value[0];
                        G2Polarity = value[1];
                        iterNo = Integer.parseInt(value[2]);
                    }
                }
            }

            SilhouetteCoefficient(docGroups, documents, filename, G1Polarity, G2Polarity, dis, iterNo); //
            MeanSilhouetteCoefficient(docGroups, documents, filename, G1Polarity, G2Polarity, dis, iterNo);
            DistanceMean(docGroups, documents, filename, G1Polarity, G2Polarity, dis, iterNo);
        }
    }

    public void MeanSilhouetteCoefficient(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs, String fileName, String G1Polarity, String G2Polarity, String distanceType, int iterNo) throws IOException {
        Double[] mean1 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] mean2 = new Double[docGroups.get(0).get(0).length - 1];

        List<Double[]> group1 = new LinkedList<>();
        List<Double[]> group2 = new LinkedList<>();

        Map<Double, Double> silhouette_Gone = new LinkedHashMap<>();
        Map<String, Double> silhouette_G1 = new LinkedHashMap<>();

        Map<Double, Double> silhouette_Gtwo = new LinkedHashMap<>();
        Map<String, Double> silhouette_G2 = new LinkedHashMap<>();

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

    public void DistanceMean(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs, String fileName, String G1Polarity, String G2Polarity, String distanceType, int iterNo) throws IOException {
        Double[] mean1 = new Double[docGroups.get(0).get(0).length - 1];
        Double[] mean2 = new Double[docGroups.get(0).get(0).length - 1];

        List<Double[]> group1 = new LinkedList<>();
        List<Double[]> group2 = new LinkedList<>();
        Map<Double, Double> silhouette_Gone = new LinkedHashMap<>();
        Map<String, Double> silhouette_G1 = new LinkedHashMap<>();

        Map<Double, Double> silhouette_Gtwo = new LinkedHashMap<>();
        Map<String, Double> silhouette_G2 = new LinkedHashMap<>();

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

    public void SilhouetteCoefficient(List<LinkedList<Double[]>> docGroups, Map<String, Double> docs, String fileName, String G1Polarity, String G2Polarity, String distanceType, int iterNo) throws IOException {
        List<Double[]> groupOne = new LinkedList<>();
        List<Double[]> groupTwo = new LinkedList<>();
        Map<Double, Double> silhouette_Gone = new LinkedHashMap<Double, Double>();
        Map<String, Double> silhouette_G1 = new LinkedHashMap<>();

        Map<Double, Double> silhouette_Gtwo = new LinkedHashMap<>();
        Map<String, Double> silhouette_G2 = new LinkedHashMap<>();

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

        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dir + run + "\\Win" + winNumber + "\\kmeans\\G1\\" + G1Polarity + iterNo + f1[f1.length - 1] + ".txt"));
                BufferedWriter newFileSWN2 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dir + run + "\\Win" + winNumber + "\\kmeans\\G2\\" + G2Polarity + iterNo + f1[f1.length - 1] + ".txt"));
                BufferedWriter newFileSWN3 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dir + run + "\\Win" + winNumber + "\\kmeans\\Max\\G1\\" + G1Polarity + iterNo + f1[f1.length - 1] + ".txt"));
                BufferedWriter newFileSWN4 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dir + run + "\\Win" + winNumber + "\\kmeans\\Max\\G2\\" + G2Polarity + iterNo + f1[f1.length - 1] + "txt"));) {
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

    public Double[] average(LinkedList<Double[]> list, Map<String, Double> docs) {
        Double[] mean = new Double[14494];
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

    public void reviewSelection(int run, int windowNumber, String dirs) throws FileNotFoundException, IOException {
        Map<String, Integer> reviewG1 = new HashMap<>();
        Map<String, Integer> reviewG2 = new HashMap<>();
        docG1List.removeAll(docG1List);
        docG2List.removeAll(docG1List);

        File dir = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dirs + run + "\\Win" + windowNumber + "\\kmeans\\Max\\G1");
        File[] files = dir.listFiles();

        for (File f : files) {
            try (BufferedReader inputstreamG1 = new BufferedReader(new FileReader(f))) {
                String value[] = inputstreamG1.readLine().split("\t");
                if (value[0].equalsIgnoreCase("null")) {
                } else {
                    if (reviewG1.isEmpty()) {
                        reviewG1.put(value[0], 1);
                    } else if (reviewG1.containsKey(value[0])) {
                        reviewG1.put(value[0], (reviewG1.get(value[0]) + 1));
                    }
                }
            } catch (IOException e) {
                e.getMessage();
            }
        }

        File dir2 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dirs + run + "\\Win" + windowNumber + "\\kmeans\\Max\\G2");
        File[] files2 = dir2.listFiles();
        for (File f : files2) {
            try (BufferedReader inputstreamG2 = new BufferedReader(new FileReader(f))) {
                String value[] = inputstreamG2.readLine().split("\t");
                if (value[0].equalsIgnoreCase("null")) {
                } else {
                    if (reviewG2.isEmpty()) {
                        reviewG2.put(value[0], 1);
                    } else if (reviewG2.containsKey(value[0])) {
                        reviewG2.put(value[0], (reviewG2.get(value[0]) + 1));
                    }
                }
            } catch (IOException e) {
                e.getMessage();
            }
        }
        Map.Entry<String, Integer> maxG1 = null;
        for (Map.Entry< String, Integer> entry : reviewG1.entrySet()) {
            if (maxG1 == null || maxG1.getValue() < entry.getValue()) {
                maxG1 = entry;
            }
        }
        Map.Entry<String, Integer> maxG2 = null;
        for (Map.Entry< String, Integer> entry : reviewG2.entrySet()) {
            if (maxG2 == null || maxG2.getValue() < entry.getValue()) {
                maxG2 = entry;
            }
        }

        StringBuilder docG1 = new StringBuilder("");
        StringBuilder docG2 = new StringBuilder("");

        File dir1;
        dir1 = new File("OUTPUT\\ProcessedDoc\\DateSeries");
        File[] fil = dir1.listFiles();
        for (File f : fil) {
            String N = f.getName().substring(0, f.getName().length() - 4);
            int winNumber = Integer.parseInt(N);
            if (winNumber == windowNumber) {
                try (BufferedReader inputstream = new BufferedReader(new FileReader(f))) {

                    String doc;
                    while ((doc = inputstream.readLine()) != null) {
                        String docname[] = doc.split("\t");
                        if (docname[0].equals(maxG1.getKey())) {
                            docG1List.add(docname[0]);
                            System.out.println(docname[0] + "...." + maxG1.getKey() + "   ==  " + doc);
                            docG1.append(doc);
                        }
                        if (docname[0].equals(maxG2.getKey())) {
                            docG2List.add(docname[0]);
                            System.out.println(docname[0] + "...." + maxG2.getKey() + "  ==== " + doc);
                            docG2.append(doc);
                        }
                    }
                } catch (IOException e) {
                    e.getMessage();
                }
            }

        }
        System.out.println("Window " + windowNumber);
        System.out.println(dirs);
        try (BufferedWriter g1Writer = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dirs + run + "\\Win" + windowNumber + "\\Ensemble\\MaxG1\\docG1.txt"));
                BufferedWriter g2Writer = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\ReviewSelection\\" + dirs + run + "\\Win" + windowNumber + "\\Ensemble\\MaxG2\\docG2.txt"))) {
            g1Writer.write(docG1.toString());
            g1Writer.newLine();
            g1Writer.write(maxG1.getKey() + "\t" + maxG1.getValue());
            g2Writer.write(docG2.toString());
            g2Writer.newLine();
            g2Writer.write(maxG2.getKey() + "\t" + maxG2.getValue());
        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file ");
            e.getMessage();
        }
        docG1.setLength(0);
        docG2.setLength(0);
        reviewG1.clear();
        reviewG2.clear();
    }

    public void printSelectedReviews() {
        for (String x : docG1List) {
            System.out.println(x);
        }
    }

}

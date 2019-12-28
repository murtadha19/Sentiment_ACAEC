/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author 17511035
 */
public class KmeansRandom {

    static List<String> featuresLinkedList = new LinkedList<>();
    static List<Double[]> documentDoubleList = new ArrayList<>();
    Map<String, Double> documents = new LinkedHashMap<String, Double>();
    List<String> docs = new LinkedList<>();
    boolean stopClustring;
    List<ArrayList<Double[]>> groups;
    List<Double[]> centroidsList;
    List<Double[]> oldcentroidsList;

    public void kmeansImplementation(String docPath, String featuresPath, int k) throws IOException {

        stopClustring = false;
        readingFeatusrs(featuresPath);
        readDocFileTolist(docPath);
        groups = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            groups.add(new ArrayList<>());
        }
        centroidsList = new LinkedList<>();
        oldcentroidsList = new LinkedList<>();
        int iter = 1;
        List<Integer> firstPoint = new ArrayList<Integer>();
        Double docReference = 0.0;
        if (iter == 1) {
            Double[] cetRow = new Double[featuresLinkedList.size() - 1];
            Random rndINDX = new Random();
            int indx = 0;
            for (int j = 0; j < k; j++) {
                indx = rndINDX.nextInt(docs.size() - 3);
                while (firstPoint.contains(indx)) {
                    indx = rndINDX.nextInt(docs.size() - 3);
                }
                firstPoint.add(indx);
                String[] valuesFirst = docs.get(indx).split("\t");
                int cetIndex = 0;
                System.out.println(valuesFirst[0]);
                for (int i = 1; i < valuesFirst.length; i++) {
                    cetRow[cetIndex] = Double.parseDouble(valuesFirst[i]);
                    cetIndex++;
                }
                centroidsList.add(cetRow);
            }
            for (String FirstPionts : docs) {
                Double[] docRow = new Double[featuresLinkedList.size()];
                String[] values = FirstPionts.split("\t");
                if (values[0].equals("point-pos") || values[0].equals("point-neg")) {
                } else {
                    docReference++;
                    docRow[0] = docReference;
                    for (int i = 1; i < values.length; i++) {
                        docRow[i] = Double.parseDouble(values[i]);
                    }

                    documentDoubleList.add(docRow);
                    documents.put(values[0], docReference);
                }
            }
            System.out.println("first c1  " + Arrays.toString(centroidsList.get(0)));
            System.out.println("first c2  " + Arrays.toString(centroidsList.get(1)));
            if (centroidsList.size() != k) {
                throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers !. first points no is " + centroidsList.size());
            }

        }

        do {
            for (Double[] doc : documentDoubleList) {
                // System.out.println(Arrays.toString(doc));
                ArrayList<Double> rowDistance = new ArrayList<>();
                for (Double[] centeroid : centroidsList) {
                    rowDistance.add(cosineSimilarity(doc, centeroid));
                }
// System.out.println(rowDistance);

                groups.get(rowDistance.indexOf(Collections.min(rowDistance))).add(doc);
                rowDistance.removeAll(rowDistance);
            }
            outbut(groups, documents);
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
            System.out.println(stopClustring);
            if (counterIfEquals == centroidsList.size() || (iter == 100)) {
                stopClustring = true;
                System.out.println(stopClustring);
            }

            if (stopClustring == false) {
                for (int i = 0; i < groups.size(); i++) {
                    groups.get(i).removeAll(groups.get(i));
                }
            }
            if (stopClustring == true) {
                outbut(groups, documents);
                documentDoubleList.removeAll(documentDoubleList);
                featuresLinkedList.removeAll(featuresLinkedList);
                documents.remove(documents);
                docs.removeAll(docs);
                centroidsList.removeAll(centroidsList);
                oldcentroidsList.removeAll(oldcentroidsList);
            }
            iter++;
            System.out.println("***** clustering ******* ");
        } while (stopClustring
                == false);

    }

    private void outbut(List<ArrayList<Double[]>> docGroups, Map<String, Double> docs) {
        ArrayList<Double> refList = new ArrayList<>();
        for (int inx = 0; inx < docGroups.size(); inx++) {
            int groupNo = inx;
            System.out.println("Griup no : " + ++groupNo);
            for (int in = 0; in < docGroups.get(inx).size(); in++) {
                // System.out.print(docGroups.get(inx).get(in)[0] + "  ");

                for (Map.Entry entry : docs.entrySet()) {
                    if (entry.getValue() == docGroups.get(inx).get(in)[0]) {
                        System.out.print(entry.getKey() + " | ");

                    }
                }
            }

            System.out.println();

            refList.removeAll(refList);
        }
    }

    public static Double[] average(ArrayList<Double[]> list) {
        Double[] mean = new Double[featuresLinkedList.size() - 1];
        int indx = 0;
        Double[][] go = new Double[list.size()][featuresLinkedList.size() - 1];

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

            mean[meanIndex] = colSumation / list.size();
            meanIndex++;
            colSumation = 0.0;
        }
        // System.out.println("mean: " + Arrays.toString(mean));
        return mean;
    }

    private void readingFeatusrs(String featuresPath) throws IOException {
        BufferedReader featuresReader = null;
        try {
            featuresReader = new BufferedReader(new FileReader(featuresPath));
            String lineFeatures;
            while ((lineFeatures = featuresReader.readLine()) != null) {
                if (!lineFeatures.startsWith("#")) {
                    featuresLinkedList.add(lineFeatures);

                }
            }
            featuresReader.close();
        } catch (Exception e) {
        } finally {
            if (featuresReader != null) {
                featuresReader.close();
            }else {
                System.out.println("readDocFileTolist");
            }
        }
    }

    private void readDocFileTolist(String docPath) throws IOException {
        BufferedReader docReader = null;

        String lineDoc;

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
                    docs.add(lineDoc);
                }
            }
            docReader.close();
        } catch (Exception e) {
        } finally {
            if (docReader != null) {
                docReader.close();
            }
        }
    }

    private double cosineSimilarity(Double[] instanceValue, Double[] centroidValue) {
        //   System.out.println(Arrays.toString(instanceValue));
        double euclideanInstanceValue = 0.0;
        double euclideanCentroidValue = 0.0;
        double dotProduct = 0.0;
        int iSCen = 0;
        double cosineSimilarity = 0.0;
        for (int iIns = 1; iIns < instanceValue.length; iIns++) {
            euclideanInstanceValue = euclideanInstanceValue + (instanceValue[iIns] * instanceValue[iIns]);

        }
        for (int iCen = 0; iCen < centroidValue.length; iCen++) {
            euclideanCentroidValue = euclideanCentroidValue + (centroidValue[iCen] * centroidValue[iCen]);
        }

        euclideanInstanceValue = Math.sqrt(euclideanInstanceValue);
        euclideanCentroidValue = Math.sqrt(euclideanCentroidValue);

        for (int iSIns = 1; iSIns < instanceValue.length; iSIns++) {
            dotProduct = dotProduct + (instanceValue[iSIns] * centroidValue[iSCen]);
            iSCen++;
        }
        cosineSimilarity = dotProduct / (euclideanInstanceValue * euclideanCentroidValue);
        return cosineSimilarity;
    }

    private double euclideanDistanceCalculator(Double[] instanceValue, Double[] centroidValue) {

        double dist = 0.0;
        int iCen = 0;
        for (int iIns = 1; iIns < instanceValue.length; iIns++) {
            dist = dist + (instanceValue[iIns] - centroidValue[iCen]) * (instanceValue[iIns] - centroidValue[iCen]);
            iCen++;
        }
        double r = 0.0;

        return Math.sqrt(dist);
    }

}

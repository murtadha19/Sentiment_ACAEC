/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Kmeans {

    Map<String, Double> documents = new LinkedHashMap<String, Double>();
    List<String> featuresLinkedList = new LinkedList<>();
    List<String> docs = new LinkedList<>();
    List<String> centroidsList = new LinkedList<>();
    double[] dis = new double[2];
    boolean stopClustring = false;
    boolean mappingTheDoc = true;

    public void kmeansImplementation(String docPath, String featuresPath, int clusterNumber) throws IOException {
        int iteration = 0;
        readingFeatusrs(featuresPath);
        readDocFileTolist(docPath);
        while (!stopClustring == true) {
            clusring(centroidsPoints(clusterNumber), mappingTheDoc(clusterNumber));
            iteration++;
        }
        System.out.println("The number of K-means iterations is : " + iteration);
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
                System.out.println("readingFeatusrs, KMEANS");
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
                    docs.add(lineDoc);
                }
            }
            docReader.close();
        } catch (Exception e) {
        } finally {
            if (docReader != null) {
                docReader.close();
            }else {
                System.out.println("readDocFileTolist, KMEANS");
            }
        }
    }

    private Double[][] mappingTheDoc(int clusterNumber) {
        Double[][] wordValues = new Double[docs.size() - clusterNumber][featuresLinkedList.size()];
        Double docReference = 0.0;
        int row = 0, col = 0;
        for (int k = 0; k < docs.size(); k++) {
            String line;
            line = docs.get(k);
            String[] termValues = line.split("\t");
            if ((!termValues[0].contains("pointPos")) && (!termValues[0].contains("pointNeg"))) {
                docReference++;
                wordValues[row][0] = docReference;
                col = 1;
                for (int i = 1; i < termValues.length; i++) {
                    wordValues[row][col] = Double.parseDouble(termValues[i]);
                    col++;
                }
                row++;

                if (documents.size() != wordValues.length) {
                    documents.put(termValues[0], docReference);
                }
            }
        }
        for (String Label : documents.keySet()) {
            // System.out.println(Label + " = " + documents.get(Label));
        }
        return wordValues;
    }

    private Double[][] centroidsPoints(int clusterNumber) {
        Double[][] centroids = new Double[clusterNumber][featuresLinkedList.size() - 1];
        boolean noFirstPoint = true;
        if (centroidsList.isEmpty() == true) {
            for (String FirstPionts : docs) {
                String[] values = FirstPionts.split("\t");
                if (values[0].contains("pointPos") || values[0].contains("pointNeg")) {
                    centroidsList.add(FirstPionts);
                    noFirstPoint = false;
                }
            }
            if (noFirstPoint == true) {
                System.out.println("No first pionts are specified ??? ");
                throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers !. first points no is " + centroidsList.size());
            }
            if (centroidsList.size() != clusterNumber) {
                throw new IllegalArgumentException("the points number for first ituration  is not the same as the clusters numbers !. first points no is " + centroidsList.size());
            }

            int row = 0, col = 0;
            for (String line : centroidsList) {
                String[] termValues = line.split("\t");
                col = 0;
                //The index started from one that means we will not take the first
                // element which is the doc name. so we will pass the actual values only.
                for (int i = 1; i < termValues.length; i++) {
                    centroids[row][col] = Double.parseDouble(termValues[i]);
                    col++;
                }
                row++;
                //System.out.println();
            }

            System.out.println("first tow points:::::");
            for (int r = 0; r < centroids.length; r++) {
                for (int f = 0; f < centroids[0].length; f++) {

                    System.out.print(centroids[r][f] + "\t");
                }
                System.out.println();
            }
            System.out.println("===");
        } else {
            int row = 0;
            for (String line : centroidsList) {
                String[] termValues = line.split("\t");
                System.out.println(line);
                //The index started from one that means we will not take the first
                // element which is the doc name. so we will pass the actual values only.
                for (int i = 0; i < termValues.length; i++) {
                    centroids[row][i] = Double.parseDouble(termValues[i]);
                }
                row++;
            }
        }
        centroidsList.clear();
        return centroids;
    }

    private void clusring(Double[][] points, Double[][] documentsArray) {
        double[] distance = new double[points.length];
        Set<Double> cluster1 = new LinkedHashSet<>();
        Set<Double> cluster2 = new LinkedHashSet<>();
        cluster2.clear();
        cluster1.clear();
        Double[] rowDoc = new Double[featuresLinkedList.size()];
        Double[] rowPoint = new Double[featuresLinkedList.size() - 1];//because the feature.size() is with NULL
        for (int r = 0; r < documentsArray.length; r++) {
            // System.out.print("instances value ");
            for (int c = 0; c < documentsArray[r].length; c++) {
                rowDoc[c] = documentsArray[r][c];
//System.out.print(rowDoc[c] + " "); 
            }
            //    System.out.println();
            int k = 0;
            for (int pr = 0; pr < points.length; pr++) {
                //    System.out.print("point value ");
                for (int pc = 0; pc < points[pr].length; pc++) {
                    rowPoint[pc] = points[pr][pc];
                    //System.out.print(rowPoint[pc] + " ");
                }
                //    System.out.println();
                double test = euclideanDistanceCalculator(rowDoc, rowPoint);
                distance[k] = test;
                k++;
                System.out.println("test distancess  " + test + " ");
                //  System.out.print(euclideanDistanceCalculator(rowDoc, rowPoint)+" ");
//cosineSimilarity(rowDoc, rowPoint);
            }
            System.out.println();
            if (distance[0] <= distance[1]) {
                cluster1.add(rowDoc[0]);
            } else {
                cluster2.add(rowDoc[0]);
            }
        }
        System.out.println("Cluster ONE " + cluster1);
        System.out.println("Cluster TWO " + cluster2);
        Double[][] newPoints = new Double[2][documentsArray[0].length - 1];
        Double colSumation = 0.0;
        int newPointsIndex = 0;
        for (int i = 1; i < documentsArray[0].length; i++) {
            for (int g = 0; g < documentsArray.length; g++) {
                if (cluster1.contains(documentsArray[g][0])) {
                    colSumation = colSumation + documentsArray[g][i];
                }
            }
            newPoints[0][newPointsIndex] = colSumation / cluster1.size();
            colSumation = 0.0;
            newPointsIndex++;
        }

        newPointsIndex = 0;
        for (int i = 1; i < documentsArray[0].length; i++) {
            for (int g = 0; g < documentsArray.length; g++) {
                if (cluster2.contains(documentsArray[g][0])) {
                    colSumation = colSumation + documentsArray[g][i];
                }
            }
            newPoints[1][newPointsIndex] = colSumation / cluster2.size();
            colSumation = 0.0;
            newPointsIndex++;
        }
        /*
         System.out.println();
         System.out.println("The NEW  pionts :::::");
         for (int r = 0; r < newPoints.length; r++) {
         for (int f = 0; f < newPoints[0].length; f++) {
         System.out.print(newPoints[r][f] + "\t");
         }
         }
         System.out.println(" :::::: " + Arrays.deepEquals(points, newPoints));*/
        if (Arrays.deepEquals(points, newPoints)) {

            stopClustring = true;
        } else {
            centroidsList.clear();
            StringBuilder line = new StringBuilder();
            for (int f = 0; f < newPoints.length; f++) {
                for (int x = 0; x < newPoints[f].length; x++) {
                    line.append(newPoints[f][x] + "\t");
                }
                centroidsList.add(line.toString());
                line.delete(0, line.length());
            }
        }
    }

    private double euclideanDistanceCalculator(Double[] instanceValue, Double[] centroidValue) {
        /*System.out.println("DIST"); 
         System.out.println(centroidValue.length);
         for (int r = 0; r < centroidValue.length; r++) {
         System.out.print(centroidValue[r] + "\t");
         }
         System.out.println("DIST");*/
        double dist = 0.0;
        int iCen = 0;
        for (int iIns = 1; iIns < instanceValue.length; iIns++) {
            dist = dist + (instanceValue[iIns] - centroidValue[iCen]) * (instanceValue[iIns] - centroidValue[iCen]);
            iCen++;
        }
        double r = 0.0;
        /* for (int t = 0; t < centroidValue.length; t++) {
         r = r + (centroidValue[t] * centroidValue[t]);
         }*/
        return Math.sqrt(dist);
    }

    private double cosineSimilarity(Double[] instanceValue, Double[] centroidValue) {
        //System.out.print(instanceValue[4]);
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

//System.out.println(euclideanCentroidValue+"----"+euclideanInstanceValue);
        for (int iSIns = 1; iSIns < instanceValue.length; iSIns++) {
            dotProduct = dotProduct + (instanceValue[iSIns] * centroidValue[iSCen]);
            iSCen++;
        }
        cosineSimilarity = dotProduct / (euclideanInstanceValue * euclideanCentroidValue);
        //   System.out.print(cosineSimilarity+" ");
        return cosineSimilarity;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Preprocessing;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.regex.*;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author 17511035
 */
//extends OutPut
public class Processing {

    MetadataFile metadataFile = new MetadataFile();
    public ArrayList<String> uniquewordslist = new ArrayList<>(300000);
    ArrayList<String> wordslist = new ArrayList<>(300000);
    public Set<String> SetOfFeatures = new LinkedHashSet<>();
    public ArrayList<String> docsAsadjectivesAndAdverbsString = new ArrayList<>();
    public Map<String, String> hashDocsAsadjAndAdvString = new LinkedHashMap<>();
    public static Set<String> SetOfLabeledFeatures = new LinkedHashSet<>();
    public static Map<String, String> hashLabeledDocsAsadjAndAdvString = new LinkedHashMap<>();
    int pos_adj = 0, pos_adv = 0, neg_adj = 0, neg_adv = 0, neu_adj = 0, neu_adv = 0;
    public static Map<String, Integer> docsLength1 = new LinkedHashMap<>();
    public static List< Integer> docsLength_all_windows1 = new ArrayList<>();// This is for BM25

    public void hashDocsAsJJandRBstringAndSetOfAllJJandRB(String modelPath, String whichPOS) throws IOException {
        System.out.print("hashDocsAsJJandRBstringAndSetOfAllJJandRB() is runing .. ");
        SetOfFeatures.removeAll(SetOfFeatures);
        SetOfLabeledFeatures.removeAll(SetOfLabeledFeatures);
        hashDocsAsadjAndAdvString.clear();
        hashLabeledDocsAsadjAndAdvString.clear();
        docsLength1.clear();
        docsLength_all_windows1.removeAll(docsLength_all_windows1);
        // whichPOS="n_AND_v" OR whichPOS="a_and_a"
        MaxentTagger tagger = new MaxentTagger(modelPath);
        for (String Label : docs.keySet()) {
            StringBuilder docJJandRB = new StringBuilder();
            StringBuilder LabeleddocJJandRB = new StringBuilder();
            Pattern word = Pattern.compile("[\\w]+");
            Matcher matchedword = word.matcher(docs.get(Label).toLowerCase());
            ArrayList<String> length = new ArrayList<>();
            while (matchedword.find()) {
                length.add(matchedword.group());
                List<HasWord> sent = Sentence.toWordList(matchedword.group());
                List<TaggedWord> taggedSent = tagger.tagSentence(sent);
                for (TaggedWord tw : taggedSent) {
// if (tw.word().contains("but")) {
                    //       System.out.println(tw);
                    //          }
                    if (whichPOS.equals("a_AND_a")) {
                        int wordNoInEachDoc = 0; //this is for TF
                        if (tw.tag().startsWith("JJ")) {
                            docJJandRB.append(tw.word() + "\t");
                            LabeleddocJJandRB.append(tw.word() + "#a" + "\t");
                            wordNoInEachDoc++;//this is for TF
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#a");
                        }
                        if (tw.tag().startsWith("RB")) {
                            docJJandRB.append(tw.word() + "\t");
                            wordNoInEachDoc++;//this is for TF
                            LabeleddocJJandRB.append(tw.word() + "#r" + "\t");
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#r");
                        }
                    } else if (whichPOS.equals("n_AND_v")) {
                        int wordNoInEachDoc = 0; //this is for TF
                        if (tw.tag().startsWith("NN")) {
                            docJJandRB.append(tw.word() + "\t");
                            LabeleddocJJandRB.append(tw.word() + "#n" + "\t");
                            wordNoInEachDoc++;//this is for TF
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#n");
                        }
                        if (tw.tag().startsWith("VB")) {
                            docJJandRB.append(tw.word() + "\t");
                            wordNoInEachDoc++;//this is for TF
                            LabeleddocJJandRB.append(tw.word() + "#v" + "\t");
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#v");
                        }
                    }
                    if (whichPOS.equals("a_a_AND_v")) {
                        int wordNoInEachDoc = 0; //this is for TF
                        if (tw.tag().startsWith("JJ")) {

                            docJJandRB.append(tw.word() + "\t");
                            LabeleddocJJandRB.append(tw.word() + "#a" + "\t");
                            wordNoInEachDoc++;//this is for TF
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#a");
                        }
                        if (tw.tag().startsWith("RB")) {
                            docJJandRB.append(tw.word() + "\t");
                            wordNoInEachDoc++;//this is for TF
                            LabeleddocJJandRB.append(tw.word() + "#r" + "\t");
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#r");
                        }
                        if (tw.tag().startsWith("VB")) {
                            docJJandRB.append(tw.word() + "\t");
                            wordNoInEachDoc++;//this is for TF
                            LabeleddocJJandRB.append(tw.word() + "#v" + "\t");
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#v");
                        }
                    }
                    if (whichPOS.equals("a_a__v_AND_n")) {
                        int wordNoInEachDoc = 0; //this is for TF
                        if (tw.tag().startsWith("JJ")) {

                            docJJandRB.append(tw.word() + "\t");
                            LabeleddocJJandRB.append(tw.word() + "#a" + "\t");
                            wordNoInEachDoc++;//this is for TF
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#a");
                        }
                        if (tw.tag().startsWith("RB")) {
                            docJJandRB.append(tw.word() + "\t");
                            wordNoInEachDoc++;//this is for TF
                            LabeleddocJJandRB.append(tw.word() + "#r" + "\t");
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#r");
                        }
                        if (tw.tag().startsWith("VB")) {
                            docJJandRB.append(tw.word() + "\t");
                            wordNoInEachDoc++;//this is for TF
                            LabeleddocJJandRB.append(tw.word() + "#v" + "\t");
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#v");
                        }
                        if (tw.tag().startsWith("NN")) {
                            docJJandRB.append(tw.word() + "\t");
                            wordNoInEachDoc++;//this is for TF
                            LabeleddocJJandRB.append(tw.word() + "#n" + "\t");
                            SetOfFeatures.add(tw.word());
                            SetOfLabeledFeatures.add(tw.word() + "#n");
                        }
                    }

                }
            }
            docsLength1.put(Label, length.size());
            docsLength_all_windows1.add(length.size());
            hashDocsAsadjAndAdvString.put(Label, docJJandRB.toString());
            hashLabeledDocsAsadjAndAdvString.put(Label, LabeleddocJJandRB.toString());
            docJJandRB.delete(0, docJJandRB.length());
            LabeleddocJJandRB.delete(0, LabeleddocJJandRB.length());
        }

         // n_AND_v ||| a_and_a
        resultWri("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\", "Each_Documents_as_Adj_and_Adv.txt", hashDocsAsadjAndAdvString);
        outputAsTextFiles("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\", "All_Adjectives_And_Adverbs_words.txt", SetOfFeatures);
        resultWri("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\", "Labeled_Each_Documents_as_Adj_and_Adv.txt", hashLabeledDocsAsadjAndAdvString);
        outputAsTextFiles("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\", "Labeled_Features.txt", SetOfLabeledFeatures);
        System.out.println(" .. finished ");
    }

    public double avgDL_BM25_1() throws IOException {
        double sumDl = 0.0;
        for (int l : docsLength_all_windows1) {
            sumDl = sumDl + l;
        }
        System.out.println("docsLength_all_windows : " + docsLength_all_windows1.size());
        return round(sumDl / docsLength_all_windows1.size());
    }


    static Set<String> assignedTermSet = new HashSet<String>();
    static Set<String> positiveDocFeaturesSet = new HashSet<String>();
    static Set<String> nagitiveDocFeaturesSet = new HashSet<String>();
    static Set<String> neutralFeatur = new HashSet<String>();

    public void longDoc(String swnFileName, String featuresFile, String properties, String whichPOS) throws IOException {

        assignedTermSet.removeAll(assignedTermSet);
        positiveDocFeaturesSet.removeAll(positiveDocFeaturesSet);
        nagitiveDocFeaturesSet.removeAll(nagitiveDocFeaturesSet);
        neutralFeatur.removeAll(neutralFeatur);

        // whichPOS="n_AND_v" OR whichPOS="a_and_a"
        //   BufferedReader readerMPQA = null; // 2paper
        String lineFeatures = null, lineSWNscore = null, lineMPQA = null;
        StringBuilder negDoc = new StringBuilder();
        StringBuilder posDoc = new StringBuilder();
        StringBuilder neuDoc = new StringBuilder();

        StringBuilder negDocImp = new StringBuilder();
        StringBuilder posDocImp = new StringBuilder();
        String postiveDocImp = null;
        String nagativeDocImp = null;

        String postiveDoc = null;
        String nagativeDoc = null;
        String neutralDoc = null;

        List<String> swnLineSet = new ArrayList<>();
        List<String> polarMPQA = new ArrayList<>(); // 2paper
        Set<String> featuresSet = new HashSet<String>();
        List<String> mostPos = new LinkedList<>();
        List<String> mostNeg = new LinkedList<>();
        // n_AND_v ||| a_and_a
        BufferedReader readerFeatures = null;
        BufferedReader readerSWN = null;
        try {
            readerFeatures = new BufferedReader(new FileReader("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\" + featuresFile));
            readerSWN = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + whichPOS + "\\" + swnFileName));
            //"TwoDim\\Noun_zero_Pos-Neg.txt"
//            readerMPQA = new BufferedReader(new FileReader("OUTPUT\\MPQA\\newWordsStrongSubj.txt")); // 2paper
            while ((lineFeatures = readerFeatures.readLine()) != null) {
                if (!lineFeatures.trim().startsWith("#")) {
                    featuresSet.add(lineFeatures);
                }
            }
            // start2paper
            // while ((lineMPQA = readerMPQA.readLine()) != null) {
            //  polarMPQA.add(lineMPQA);
            // }
            // start2paper

            while ((lineSWNscore = readerSWN.readLine()) != null) {
                if (!lineSWNscore.startsWith("#")) {
                    swnLineSet.add(lineSWNscore);
                }
            }
            readerFeatures.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (readerFeatures != null) {
                readerFeatures.close();
            } else {
                System.out.println("longDoc, readerFeatures is not opened");
            }
            if (readerSWN != null) {
                readerSWN.close();
            } else {
                System.out.println("longDoc, readerSWN is not opened");
            }
        }
        TFIDF tfidf = new TFIDF();
        tfidf.functionOfIDF(featuresSet, hashLabeledDocsAsadjAndAdvString);
        System.out.println(tfidf.adjectivesAndAdverbsWithIDF.size());
        //8888888888
        List<Double> v = new ArrayList<>();
        for (String s : tfidf.adjectivesAndAdverbsWithIDF.keySet()) {
            //    System.out.println(tfidf.adjectivesAndAdverbsWithIDF.get(s));
            //  v.add(tfidf.adjectivesAndAdverbsWithIDF.get(s));
        }
        //8888888888
        boolean assigned = false;
        int i = 0;
        int u = 0;
        double positivty = 0.0, negativity = 0.0; // 3paper

        for (String term : featuresSet) {
            assigned = false;

            for (String swnString : swnLineSet) {
                String[] values = swnString.split("\t");
                if (values.length != 2) {
                    throw new IllegalArgumentException(
                            "Documents file:Incorrect tabulation format in file.");
                }

                if (values[0].equalsIgnoreCase(term)) {
                    if (Double.parseDouble(values[1]) > 0.0) {
                        positiveDocFeaturesSet.add(term);
                        positivty = positivty + Double.parseDouble(values[1]);// 3paper
                        if (tfidf.adjectivesAndAdverbsWithIDF.get(term) > 1) {
                            mostPos.add(term);
                        }
                        assignedTermSet.add(term);
                        posDoc.append(term + "\t");
                        assigned = true;
                    } else {
                        nagitiveDocFeaturesSet.add(term);
                        negativity = negativity + Double.parseDouble(values[1]);// 3paper
                        assignedTermSet.add(term);
                        negDoc.append(term + "\t");
                        if (tfidf.adjectivesAndAdverbsWithIDF.get(term) > 1) {
                            mostNeg.add(term);
                        }
                        assigned = true;
                    }
                }
            }

            /*
             // start2paper
             for (String stringMPQA : polarMPQA) {
             String[] values = stringMPQA.split("\t");
             if (values.length != 2) {
             throw new IllegalArgumentException(
             "Documents file:Incorrect tabulation format in file.");
             }
             if (values[0].equalsIgnoreCase(term)) {
             if (values[1].equalsIgnoreCase("positive")) {
             positiveDocFeaturesSet.add(term);
             assignedTermSet.add(term);
             posDoc.append(term + "\t");
             assigned = true;
             } else if(values[1].equalsIgnoreCase("negative")) {
             nagitiveDocFeaturesSet.add(term);
             assignedTermSet.add(term);
             negDoc.append(term + "\t");
             assigned = true;
             }
             }
             }
             //end2paper
             */
            if (assigned == false) {
                //  if (tfidf.adjectivesAndAdverbsWithIDF.get(term) > 2) {
                //    neuDoc.append(term + "\t");

                //  }
                neutralFeatur.add(term);
            }
        }

        postiveDoc = posDoc.toString();
        nagativeDoc = negDoc.toString();
        //  neutralDoc = neuDoc.toString();
        /// sameLength_InitialCentroid(whichPOS);
        docsLength1.put("point-pos", positiveDocFeaturesSet.size());
        docsLength1.put("point-neg", nagitiveDocFeaturesSet.size());
        docsLength_all_windows1.add(positiveDocFeaturesSet.size());
        docsLength_all_windows1.add(nagitiveDocFeaturesSet.size());

        Processing.hashLabeledDocsAsadjAndAdvString.put("point-pos", postiveDoc);
        Processing.hashLabeledDocsAsadjAndAdvString.put("point-neg", nagativeDoc);
        //    Processing.hashLabeledDocsAsadjAndAdvString.put("point-neu", neutralDoc);
        if (properties.equals("assigned")) {
            //  System.out.println("assigned");
            polarityOFnotAssignedfeatur(mostPos, mostNeg, neutralFeatur, featuresSet);
        }
        if (properties.equals("remove")) {
            removeUnsignedFeatures(neutralFeatur, assignedTermSet, whichPOS);
        }
        // n_AND_v ||| a_and_a\

        File dir;
        int winNumber = 0;
        dir = new File("OUTPUT\\ProcessedDoc\\DateSeries_windowNumber");
        File[] files = dir.listFiles();
        winNumber = files.length;

        outputAsTextFiles("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\LongDoc\\", "PastiveFeature.txt", positiveDocFeaturesSet);
        outputAsTextFiles("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\LongDoc\\", "NagativeFeatures.txt", nagitiveDocFeaturesSet);

        outputAsTextFiles("OUTPUT\\ProcessedDoc\\FeatuersDateSeries\\Win" + winNumber + "\\", "PastiveFeature.txt", positiveDocFeaturesSet);
        outputAsTextFiles("OUTPUT\\ProcessedDoc\\FeatuersDateSeries\\Win" + winNumber + "\\", "NagativeFeatures.txt", nagitiveDocFeaturesSet);

        // outputAsTextFiles("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\LongDoc\\", "NuturalFeaturs.txt", neutralFeatur);
        //System.out.println(assignedTermSet.size());
        double noPos = 0.0, noNeg = 0.0, rp = 0.0, rn = 0.0;// 3paper
        noPos = positiveDocFeaturesSet.size();// 3paper
        noNeg = nagitiveDocFeaturesSet.size();// 3paper

        System.out.println("positivty score from SWN : " + positivty);// 3paper
        System.out.println("Negativty score from SWN : " + negativity);// 3paper
        System.out.println("MINUS score from SWN : " + (positivty - negativity));// 3paper
        System.out.println("positivty score from SWN AVERAGE: " + positivty / noPos);// 3paper
        System.out.println("Negativty score from SWN AVERAGE: " + negativity / noNeg);// 3paper
        System.out.println("MINUS score from SWN AVERAGE: " + ((positivty / noPos) - (negativity / noNeg)));// 3paper

        System.out.println("imbalace : " + ((positivty / noPos) - (negativity / noNeg)));// 3paper

        rp = noPos / (noPos + noNeg);// 3paper
        rn = noNeg / (noPos + noNeg);// 3paper
        System.out.println("noPos : " + noPos + " The ratio is " + rp);// 3paper
        System.out.println("noNeg : " + noNeg + " The ratio is " + rn);// 3paper
        System.out.println(" MINUS no : " + (noPos - noNeg));
        System.out.println(" MINUS ratio : " + (rp - rn));

        System.out.println(positivty + "\t" + positivty / noPos + "\t" + negativity + "\t" + negativity / noNeg + "\t" + (positivty - negativity) + "\t" + ((positivty / noPos) - (negativity / noNeg)) + "\t" + noPos + "\t" + noNeg + "\t" + (noPos - noNeg) + "\t" + rp + "\t" + rn + "\t" + (rp - rn));
        //System.out.println("The featuer set size INCLUDING NUll: " + featuresSet.size());
        //System.out.println("The number of the features which not been assigned INCLUDING NUll:" + (featuresSet.size() - (positiveDocFeaturesSet.size() + nagitiveDocFeaturesSet.size())));
        //System.out.println(neutralFeatur.size());
        //System.out.println(featuresSet);
        //Boolean varaible is for unsigened featuears to be removied ...
    }

    private void polarityOFnotAssignedfeatur(List<String> mostPositive, List<String> mostNegative, Set<String> notassigned, Set<String> allFeatures) {
        StringBuilder negDoc = new StringBuilder();
        StringBuilder posDoc = new StringBuilder();
        List<double[]> unWordsFreq = new LinkedList<>();
        Map<String, Double> unWordsFreqency = new HashMap<>();
        List<double[]> posSeads = new LinkedList<>();
        List<double[]> negSeads = new LinkedList<>();
        List<String> newPos = new ArrayList<>();
        List<String> newNeg = new ArrayList<>();
        TFIDF tfidf = new TFIDF();
        tfidf.functionOfIDF(allFeatures, hashLabeledDocsAsadjAndAdvString);
        int sizeOffeatuers = 0;
        if (mostNegative.size() <= mostPositive.size()) {
            sizeOffeatuers = mostNegative.size();
        } else {
            sizeOffeatuers = mostPositive.size();
        }
        double referance = 1;
        //not asignned featuers Matrix
        for (String s : notassigned) {
            // System.out.print(s+": ");
            int docIndx = 1;
            double[] vectorUnsigned = new double[hashLabeledDocsAsadjAndAdvString.size() + 1];
            Arrays.fill(vectorUnsigned, 0);
            vectorUnsigned[0] = referance;
            for (Map.Entry entry : hashLabeledDocsAsadjAndAdvString.entrySet()) {
                int numberOfTerms = StringUtils.countMatches(entry.getValue().toString(), "\t");//For TF
                int numberOfSpecificTermInDoc = StringUtils.countMatches(entry.getValue().toString(), s);
                if (numberOfSpecificTermInDoc != 0) {
                    vectorUnsigned[docIndx] = tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms) * tfidf.adjectivesAndAdverbsWithIDF.get(s);
                    //    vectorUnsigned[docIndx] = 1;
                }
                //  System.out.print( vectorUnsigned[docIndx]);
                docIndx++;
            }
            unWordsFreq.add(vectorUnsigned);
            unWordsFreqency.put(s, referance);
            referance++;
            //  System.out.println();
        }
        // most positv featur matrix
        for (int indx = 0; indx < mostPositive.size(); indx++) {
            {
                //   System.out.print(mostPositive.get(indx)+" : ");
                int docIndx = 0;
                double[] vectorPos = new double[hashLabeledDocsAsadjAndAdvString.size()];
                Arrays.fill(vectorPos, 0);
                for (Map.Entry entry : hashLabeledDocsAsadjAndAdvString.entrySet()) {
                    int numberOfTerms = StringUtils.countMatches(entry.getValue().toString(), "\t");//For TF
                    int numberOfSpecificTermInDoc = StringUtils.countMatches(entry.getValue().toString(), mostPositive.get(indx));
                    if (numberOfSpecificTermInDoc != 0) {
                        vectorPos[docIndx] = tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms) * tfidf.adjectivesAndAdverbsWithIDF.get(mostPositive.get(indx));
                        //   vectorPos[docIndx] = 1;
                    }
                    //      System.out.print( vectorPos[docIndx]);
                    docIndx++;
                }
                //  System.out.println();
                posSeads.add(vectorPos);

            }
        }
        //most nagative feature matrix
        for (int indx = 0; indx < mostNegative.size(); indx++) {
            //  System.out.print(mostNegative.get(indx)+" : ");
            int docIndx = 0;
            double[] vectorNeg = new double[hashLabeledDocsAsadjAndAdvString.size()];
            Arrays.fill(vectorNeg, 0);
            for (Map.Entry entry : hashLabeledDocsAsadjAndAdvString.entrySet()) {
                int numberOfTerms = StringUtils.countMatches(entry.getValue().toString(), "\t");//For TF
                int numberOfSpecificTermInDoc = StringUtils.countMatches(entry.getValue().toString(), mostNegative.get(indx));
                if (numberOfSpecificTermInDoc != 0) {
                    vectorNeg[docIndx] = tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms) * tfidf.adjectivesAndAdverbsWithIDF.get(mostNegative.get(indx));
                    //   vectorNeg[docIndx] = 1;
                }
                //   System.out.print( vectorNeg[docIndx]);
                docIndx++;
            }
            // System.out.println();
            negSeads.add(vectorNeg);
        }
        double sumPosSimilarty = 0;
        double sumNegSimilarty = 0;
        List<Double> posTerm = new ArrayList<>();
        List<Double> negTerm = new ArrayList<>();
        int[] clusterGroup = new int[sizeOffeatuers];
        for (double[] wordVector : unWordsFreq) {
            for (int x = 0; x < sizeOffeatuers; x++) {
                int idx = 0;
                sumPosSimilarty = 1 - euclideanDistanceCalculator(posSeads.get(x), wordVector);
                sumNegSimilarty = 1 - euclideanDistanceCalculator(negSeads.get(x), wordVector);

                if (sumPosSimilarty <= sumNegSimilarty) {
                    clusterGroup[idx] = 1;//1 means POS
                } else {
                    clusterGroup[idx] = 0;//0 means Neg
                }
                idx++;
            }
            int zeros = 0, ones = 0;
            for (int i = 0; i < clusterGroup.length; i++) {
                if (clusterGroup[i] == 1) {
                    ones++;
                } else if (clusterGroup[i] == 0) {
                    zeros++;
                }
            }
            if (ones >= zeros) {
                posTerm.add(wordVector[0]);
            } else {
                negTerm.add(wordVector[0]);
            }
        }
        for (String keys : unWordsFreqency.keySet()) {
            for (Double refrence : posTerm) {
                if (unWordsFreqency.get(keys).equals(refrence)) {
                    posDoc.append(keys + "\t");
                    newPos.add(keys);
                }
            }

        }
        for (String keys : unWordsFreqency.keySet()) {
            for (Double refrence : negTerm) {
                if (unWordsFreqency.get(keys).equals(refrence)) {
                    negDoc.append(keys + "\t");
                    newNeg.add(keys);
                }
            }
        }
        //  System.out.println("new neg ; " + newNeg.size());
        // System.out.println("new pos ; " + newPos.size());
        hashLabeledDocsAsadjAndAdvString.replace("point-pos", hashLabeledDocsAsadjAndAdvString.get("point-pos"), posDoc.append(hashLabeledDocsAsadjAndAdvString.get("point-pos")).toString());
        hashLabeledDocsAsadjAndAdvString.replace("point-neg", hashLabeledDocsAsadjAndAdvString.get("point-neg"), negDoc.append(hashLabeledDocsAsadjAndAdvString.get("point-neg")).toString());
    }

    private double cosineSimilarity(double[] instanceValue, double[] centroidValue) {
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

        euclideanInstanceValue = round(Math.sqrt(euclideanInstanceValue));
        euclideanCentroidValue = round(Math.sqrt(euclideanCentroidValue));

        for (int iSIns = 1; iSIns < instanceValue.length; iSIns++) {
            dotProduct = dotProduct + (instanceValue[iSIns] * centroidValue[iSCen]);
            iSCen++;
        }
        cosineSimilarity = round(dotProduct / (euclideanInstanceValue * euclideanCentroidValue));
        return cosineSimilarity;
    }

    private double euclideanDistanceCalculator(double[] instanceValue, double[] centroidValue) {

        double dist = 0.0;
        int iCen = 0;
        for (int iIns = 1; iIns < instanceValue.length; iIns++) {
            dist = dist + (instanceValue[iIns] - centroidValue[iCen]) * (instanceValue[iIns] - centroidValue[iCen]);
            iCen++;
        }
        double r = 0.0;

        return round(Math.sqrt(dist));
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

    private void removeUnsignedFeatures(Set<String> notAssignedfeatur, Set<String> assignedTermSet, String whichPOS) throws IOException {
        // whichPOS="n_AND_v" OR whichPOS="a_and_a"

        StringBuilder doc = new StringBuilder();

        SetOfFeatures.removeAll(SetOfFeatures);
        for (String feature : assignedTermSet) {
            String[] val = feature.split("#");
            SetOfFeatures.add(val[0]);
        }
        SetOfLabeledFeatures.removeAll(SetOfLabeledFeatures);
        for (String feature : assignedTermSet) {
            SetOfLabeledFeatures.add(feature);
        }
        ////
        for (String key : hashDocsAsadjAndAdvString.keySet()) {
            String[] values = hashDocsAsadjAndAdvString.get(key).split("\t");
            for (String val : values) {
                boolean here = true;
                for (String fetuerValue : notAssignedfeatur) {
                    String[] mereFeature = fetuerValue.split("#");
                    if (val.equalsIgnoreCase(mereFeature[0])) {
                        here = false;
                    }
                }
                if (here == true) {
                    doc.append(val + "\t");
                }
            }
            hashDocsAsadjAndAdvString.replace(key, doc.toString());
            doc.delete(0, doc.length());
        }
        /////
        for (String key : hashLabeledDocsAsadjAndAdvString.keySet()) {
            String[] values = hashLabeledDocsAsadjAndAdvString.get(key).split("\t");
            for (String val : values) {
                boolean here = true;
                for (String fetuerValue : notAssignedfeatur) {
                    if (val.equalsIgnoreCase(fetuerValue)) {
                        here = false;
                    }
                }
                if (here == true) {
                    doc.append(val + "\t");
                }
            }
            hashLabeledDocsAsadjAndAdvString.replace(key, doc.toString());
            doc.delete(0, doc.length());
        }
// n_AND_v ||| a_and_a
        SetOfFeatures.add("NonZero");
        SetOfLabeledFeatures.add("NonZero#a");
        resultWri("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\RemovedFeatuers\\", "Each_Documents_as_Adj_and_Adv.txt", hashDocsAsadjAndAdvString);
        outputAsTextFiles("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\RemovedFeatuers\\", "All_Adjectives_And_Adverbs_words.txt", SetOfFeatures);
        resultWri("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\RemovedFeatuers\\", "Labeled_Each_Documents_as_Adj_and_Adv.txt", hashLabeledDocsAsadjAndAdvString);
        outputAsTextFiles("OUTPUT\\ProcessedDoc\\" + whichPOS + "\\RemovedFeatuers\\", "Labeled_Features.txt", SetOfLabeledFeatures);
    }

    public void sampleFeatuers(String pathPOW, Set<String> SetOfFeatures, int portionNo, String whichPOS) throws IOException {
        List<Set<String>> portionOfFeatuers = new ArrayList<>(portionNo);

        List<String> listOfFeatures = new LinkedList<>();
        for (String term : SetOfFeatures) {
            listOfFeatures.add(term);
        }
        //  System.out.println(listOfFeatures.size());
        int sampleSize = 0;

        if (listOfFeatures.size() % portionNo == 0) {
            sampleSize = listOfFeatures.size() / portionNo;
        } else {
            sampleSize = (listOfFeatures.size() - 1) / portionNo;
        }

        int incrimantil = 0;
        for (int x = 0; x < portionNo; x++) {
            Set<String> subFeatures = new HashSet<>();

            for (int indx = 0; indx < sampleSize; indx++) {
                subFeatures.add(listOfFeatures.get(incrimantil));
                incrimantil++;
            }
            portionOfFeatuers.add(subFeatures);
        }
        int portion = 1;
        for (Set subTermSet : portionOfFeatuers) {
            bagOfWord(pathPOW, subTermSet, hashLabeledDocsAsadjAndAdvString, portion, whichPOS);
            portion++;
        }
    }

    public void bagOfWord(String path, Set<String> listOfFeatures, Map<String, String> docs, int portion, String whichPOS) throws IOException {
        scoreAnalyis(listOfFeatures);
        double avgDL = avgDL_BM25_1();
        // double k = 1.2;
        double b = 0.75;
        double k = 2.0;
        Map<String, Double[]> frequency_TFIDF = new LinkedHashMap<>();
        Map<String, Double[]> frequency = new LinkedHashMap<>();
            Map<String, Double[]> frequency_Term_Normalization = new LinkedHashMap<>();
        Map<String, Double[]> presence = new LinkedHashMap<>();
        Map<String, Double[]> presence_TFIDF = new LinkedHashMap<>();
             Map<String, Double[]> presence_Term_Normalization = new LinkedHashMap<>();
            Map<String, Double[]> presence_WFIDF = new LinkedHashMap<>();
            Map<String, Double[]> frequency_WFIDF = new LinkedHashMap<>();
             Map<String, Double[]> presence_AW = new LinkedHashMap<>();
            Map<String, Double[]> frequency_AW = new LinkedHashMap<>();

          Map<String, Double[]> frequency_IDF = new LinkedHashMap<>();//new
            Map<String, Double[]> presence_IDF = new LinkedHashMap<>();//new
            Map<String, Double[]> frequency_2BM25 = new LinkedHashMap<>();
            Map<String, Double[]> frequency_BM25 = new LinkedHashMap<>();
            Map<String, Double[]> presence_2BM25 = new LinkedHashMap<>();
            Map<String, Double[]> presence_BM25 = new LinkedHashMap<>();

                TFIDF tfidf = new TFIDF();
        tfidf.functionOfIDF(listOfFeatures, docs);

        for (String keys : docs.keySet()) {
            double p = 0, g = 0, n = 0;

            Double[] valueFrequency = new Double[listOfFeatures.size()];
            Double[] valueFrequencyTFIDF = new Double[listOfFeatures.size()];
                    Double[] valueFrequencyNormalization = new Double[listOfFeatures.size()];
            Double[] valuePresence = new Double[listOfFeatures.size()];
            Double[] valuePresenceTFIDF = new Double[listOfFeatures.size()];
                    Double[] valuePresenceNormalization = new Double[listOfFeatures.size()];
                     Double[] valuePresenceWFIDF = new Double[listOfFeatures.size()];
                     Double[] valueFrequencyWFIDF = new Double[listOfFeatures.size()];
                     Double[] valuePresenceAW = new Double[listOfFeatures.size()];
                    Double[] valueFrequencyAW = new Double[listOfFeatures.size()];

                  Double[] valueFrequencyIDF = new Double[listOfFeatures.size()];//new
                   Double[] valuePresenceIDF = new Double[listOfFeatures.size()];//new
                 Double[] valueFrequency2BM25 = new Double[listOfFeatures.size()];
                 Double[] valueFrequencyBM25 = new Double[listOfFeatures.size()];
                 Double[] valuePresence2BM25 = new Double[listOfFeatures.size()];
                   Double[] valuePresenceBM25 = new Double[listOfFeatures.size()];
            Arrays.fill(valueFrequency, 0.0);
            Arrays.fill(valueFrequencyTFIDF, 0.0);
                  Arrays.fill(valueFrequencyNormalization, 0.0);
            Arrays.fill(valuePresence, 0.0);
            Arrays.fill(valuePresenceTFIDF, 0.0);
                  Arrays.fill(valuePresenceNormalization, 0.0);
                  Arrays.fill(valuePresenceWFIDF, 0.0);
                  Arrays.fill(valueFrequencyWFIDF, 0.0);
                  Arrays.fill(valuePresenceAW, 0.0);
                 Arrays.fill(valueFrequencyAW, 0.0);
                   Arrays.fill(valueFrequencyIDF, 0.0);//new
                   Arrays.fill(valuePresenceIDF, 0.0);//new



            valueFrequency[valueFrequency.length - 1] = 0.0001;
            valueFrequencyTFIDF[valueFrequencyTFIDF.length - 1] = 0.0001;
                 valueFrequencyNormalization[valueFrequencyNormalization.length - 1] = 0.0001;
            valuePresence[valuePresence.length - 1] = 0.0001;
            valuePresenceTFIDF[valuePresenceTFIDF.length - 1] = 0.0001;
                 valuePresenceNormalization[valuePresenceNormalization.length - 1] = 0.0001;
                 valuePresenceWFIDF[valuePresenceWFIDF.length - 1] = 0.0001;
                valueFrequencyWFIDF[valueFrequencyWFIDF.length - 1] = 0.0001;
                valuePresenceAW[valuePresenceAW.length - 1] = 0.0001;
                  valueFrequencyAW[valueFrequencyAW.length - 1] = 0.0001;
                    valuePresenceIDF[valuePresenceIDF.length - 1] = 0.0001;//new
                 valueFrequencyIDF[valueFrequencyIDF.length - 1] = 0.0001;//new

            String[] DocumentWord = docs.get(keys).split("\t");
            //FOr TF the wight spaces count is equal to the terms counts.
            int numberOfTerms = StringUtils.countMatches(docs.get(keys), "\t");//For TF
            // int numberOfTerms = docsLength1.get(keys);//For TF length of hole docs
            for (String word : DocumentWord) {
                int i = 0;
                int numberOfSpecificTermInDoc = 0;
                double numberOfSpecificTermInDoc2 = 0.0;
                for (String term : listOfFeatures) {
                    numberOfSpecificTermInDoc = StringUtils.countMatches(docs.get(keys), term);//For TF
                    //     numberOfSpecificTermInDoc2 = numberOfSpecificTermInDoc * 1.0;
                    numberOfSpecificTermInDoc2 = round(Math.log10(numberOfSpecificTermInDoc + 1));
                    //    numberOfSpecificTermInDoc2 = 1 + round(Math.log(numberOfSpecificTermInDoc));
                    if (valueFrequencyTFIDF[i] == 0 && term.equalsIgnoreCase(word) == true) {

                        valueFrequency[i] = numberOfSpecificTermInDoc2;
                        valueFrequencyTFIDF[i] = (numberOfSpecificTermInDoc2 * (tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms) * tfidf.adjectivesAndAdverbsWithIDF.get(term)));
                                 valueFrequencyNormalization[i] = (numberOfSpecificTermInDoc2 * tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms));
                        valuePresence[i] = 1.0;
                        valuePresenceTFIDF[i] = (tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms) * tfidf.adjectivesAndAdverbsWithIDF.get(term));

                                valuePresenceNormalization[i] = (tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms));
                                 valuePresenceWFIDF[i] = ((tfidf.termLogarithmFrequency(numberOfSpecificTermInDoc) + 1) * tfidf.adjectivesAndAdverbsWithIDF.get(term));
                                valueFrequencyWFIDF[i] = (numberOfSpecificTermInDoc2 * ((tfidf.termLogarithmFrequency(numberOfSpecificTermInDoc) + 1) * tfidf.adjectivesAndAdverbsWithIDF.get(term)));
                               valuePresenceAW[i] = ((valuePresenceTFIDF[i] + valuePresenceWFIDF[i]) / 2);
                                valueFrequencyAW[i] = (numberOfSpecificTermInDoc2 * ((valuePresenceTFIDF[i] + valuePresenceWFIDF[i]) / 2));
                              valuePresenceIDF[i] = (tfidf.adjectivesAndAdverbsWithIDF.get(term));//new
                                valueFrequencyIDF[i] = (numberOfSpecificTermInDoc2 * tfidf.adjectivesAndAdverbsWithIDF.get(term));//new
                              valuePresenceBM25[i] = ((tfidf.adjectivesAndAdverbsWithIDF.get(term) * ((k + 1) * numberOfSpecificTermInDoc)) / (k * ((1 - b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc));
                             valueFrequencyBM25[i] = (numberOfSpecificTermInDoc2 * ((tfidf.adjectivesAndAdverbsWithIDF.get(term) * ((k + 1) * numberOfSpecificTermInDoc)) / (k * ((1 - b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc)));

                        i++;
                    } else {
                        i++;
                    }
                }
            }

            presence.put(keys, valuePresence);
            frequency.put(keys, valueFrequency);
            frequency_TFIDF.put(keys, valueFrequencyTFIDF);
                frequency_Term_Normalization.put(keys, valueFrequencyNormalization);
            presence_TFIDF.put(keys, valuePresenceTFIDF);
                presence_Term_Normalization.put(keys, valuePresenceNormalization);
                 presence_WFIDF.put(keys, valuePresenceWFIDF);
                  frequency_WFIDF.put(keys, valueFrequencyWFIDF);
                  presence_AW.put(keys, valuePresenceAW);
                   frequency_AW.put(keys, valueFrequencyAW);
                  frequency_IDF.put(keys, valueFrequencyIDF);//new
                    presence_IDF.put(keys, valuePresenceIDF);//new
                 presence_BM25.put(keys, valuePresenceBM25);
                   frequency_BM25.put(keys, valueFrequencyBM25);

        }

        resultWriter(path, path + portion + "e-Presence.txt", presence, listOfFeatures);
        swnScoreForAllPOWfiles(path, "Presence", listOfFeatures, portion, presence, whichPOS, false);

        resultWriter(path, path + portion + "e-Frequency.txt", frequency, listOfFeatures);
        swnScoreForAllPOWfiles(path, "Frequency", listOfFeatures, portion, frequency, whichPOS, false);

        resultWriter(path, path + portion + "e-Frequency_TFIDF.txt", frequency_TFIDF, listOfFeatures);
        swnScoreForAllPOWfiles(path, "Frequency_TFIDF", listOfFeatures, portion, frequency_TFIDF, whichPOS, false);
         resultWriter(path, path + portion + "e-Frequency_Term_Normalization.txt", frequency_Term_Normalization, listOfFeatures);

          swnScoreForAllPOWfiles(path, "Frequency_Term_Normalization", listOfFeatures, portion, frequency_Term_Normalization, whichPOS, false);
        resultWriter(path, path + portion + "e-Presence_TFIDF.txt", presence_TFIDF, listOfFeatures);
        swnScoreForAllPOWfiles(path, "Presence_TFIDF", listOfFeatures, portion, presence_TFIDF, whichPOS, false);

         resultWriter(path, path + portion + "e-Presence_Term_Normalization.txt", presence_Term_Normalization, listOfFeatures);
         swnScoreForAllPOWfiles(path, "Presence_Term_Normalization", listOfFeatures, portion, presence_Term_Normalization, whichPOS, false);
          resultWriter(path, path + portion + "e-Presence_WFIDF.txt", presence_WFIDF, listOfFeatures);
         swnScoreForAllPOWfiles(path, "Presence_WFIDF", listOfFeatures, portion, presence_WFIDF, whichPOS, false);
          resultWriter(path, path + portion + "e-Frequency_WFIDF.txt", frequency_WFIDF, listOfFeatures);
          swnScoreForAllPOWfiles(path, "Frequency_WFIDF", listOfFeatures, portion, frequency_WFIDF, whichPOS, false);
           resultWriter(path, path + portion + "e-Presence_AW.txt", presence_AW, listOfFeatures);
          swnScoreForAllPOWfiles(path, "Presence_AW", listOfFeatures, portion, presence_AW, whichPOS, false);
            resultWriter(path, path + portion + "e-Frequency_AW.txt", frequency_AW, listOfFeatures);
           swnScoreForAllPOWfiles(path, "Frequency_AW", listOfFeatures, portion, frequency_AW, whichPOS, false);
         resultWriter(path, path + portion + "e-Frequency_IDF.txt", frequency_IDF, listOfFeatures);//new
            swnScoreForAllPOWfiles(path, "Frequency_IDF", listOfFeatures, portion, frequency_IDF, whichPOS, false);
              resultWriter(path, path + portion + "e-Presence_IDF.txt", presence_IDF, listOfFeatures);//new
            swnScoreForAllPOWfiles(path, "Presence_IDF", listOfFeatures, portion, presence_IDF, whichPOS, false);
          resultWriter(path, path + portion + "e-Presence_BM25.txt", presence_BM25, listOfFeatures);
          swnScoreForAllPOWfiles(path, "Presence_BM25", listOfFeatures, portion, presence_BM25, whichPOS, false);
          resultWriter(path, path + portion + "e-Frequency_BM25.txt", frequency_BM25, listOfFeatures);
          swnScoreForAllPOWfiles(path, "Frequency_BM25", listOfFeatures, portion, frequency_BM25, whichPOS, false);
    }
    Set<String> scoreSet = new LinkedHashSet<>();
    Set<String> termScoreSWN = new LinkedHashSet<>();
    Set<String> termScoreFeatuers = new LinkedHashSet<>();
    Map<Integer, Integer> denseAva = new HashMap<>();

    public void fileFroMatlab1(Map<String, Double[]> p, Set<String> listOfFeatures, String fileName, int winNumber) throws IOException {
        //NonZero 0.0001 feature.
        List<Double[]> p1 = new LinkedList<>();

        for (String key : p.keySet()) {
            Double[] vals = new Double[p.get(key).length + 1];
            String[] data = key.split("-");
            if (data[1].equalsIgnoreCase("pos")) {
                vals[0] = 1.0;
            } else if (data[1].equalsIgnoreCase("neg")) {
                vals[0] = 2.0;
            }
            int h = 1;
            for (int i = 0; i < p.get(key).length; i++) {
                vals[h] = p.get(key)[i];
                h++;
            }
            p1.add(vals);
        }
        // writeing
        String path = "OUTPUT\\Matrixes_Matlab\\";
        resultWriter2(path, path + winNumber + fileName, p1, listOfFeatures);

        //resultWriter2(path, path + "PresenceTFIDF.txt", p1, listOfFeatures);
    }

    public void fileFroWeka(Map<String, Double[]> p, Set<String> listOfFeatures, String fileName, int winNumber) throws IOException {
        //NonZero 0.0001 feature.
        List<Double[]> p1 = new LinkedList<>();

        for (String key : p.keySet()) {
            Double[] vals = new Double[p.get(key).length + 1];
            String[] data = key.split("-");
            if (data[1].equalsIgnoreCase("pos")) {
                vals[0] = 1.0;
            } else if (data[1].equalsIgnoreCase("neg")) {
                vals[0] = 2.0;
            }
            int h = 1;
            for (int i = 0; i < p.get(key).length; i++) {
                vals[h] = p.get(key)[i];
                h++;
            }
            p1.add(vals);
        }
        // writeing
        String path = "OUTPUT\\Matrixes_WEKA\\";
        resultWriter2Weka(path, path + winNumber + fileName, p1, listOfFeatures);

        //resultWriter2(path, path + "PresenceTFIDF.txt", p1, listOfFeatures);
    }


    public void scoreAnalyis(Set<String> features) throws IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\a_AND_a\\TwoDim\\denseFeature\\Book1.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().startsWith("#")) {
                    String data[] = line.split("\t");
                    scoreSet.add(data[1]);
                    termScoreSWN.add(line);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        for (String term : features) {
            for (String termSWN : termScoreSWN) {
                String value[] = termSWN.split("\t");
                if (value[0].equalsIgnoreCase(term)) {
                    termScoreFeatuers.add(term + "\t" + value[1]);
                }
            }
        }
        for (int i = 0; i < scoreSet.size(); i++) {
            denseAva.put(i, 0);
        }

    }

    public int denseMatrex(String term, int dimenNo) {
        //   assignedTermSet
        // positiveDocFeaturesSet
        //  nagitiveDocFeaturesSet
        //   neutralFeatur
        int polarity = 0; //positive= 1, negative = 2, neutural=3
        if (dimenNo == 3) {
            if (positiveDocFeaturesSet.contains(term)) {
                polarity = 1;
            } else if (nagitiveDocFeaturesSet.contains(term)) {
                polarity = 2;
            } else if (neutralFeatur.contains(term)) {
                polarity = 3;
            }
        }
        if (dimenNo == 6) {
            if (positiveDocFeaturesSet.contains(term)) {
                String[] values = term.split("#");
                if (values[1].equalsIgnoreCase("a")) {
                    polarity = 1;
                    pos_adj++;
                } else if (values[1].equalsIgnoreCase("r")) {
                    polarity = 2;
                    pos_adv++;
                }

            } else if (nagitiveDocFeaturesSet.contains(term)) {
                String[] values = term.split("#");
                if (values[1].equalsIgnoreCase("a")) {
                    polarity = 3;
                    neg_adj++;
                } else if (values[1].equalsIgnoreCase("r")) {
                    polarity = 4;
                    neg_adv++;
                }

            } else if (neutralFeatur.contains(term)) {
                String[] values = term.split("#");
                if (values[1].equalsIgnoreCase("a")) {
                    polarity = 5;
                    neu_adj++;
                } else if (values[1].equalsIgnoreCase("r")) {
                    polarity = 6;
                    neu_adv++;
                }

            }
        }
        return polarity;

    }

    public Map<String, Double[]> normlize(Map<String, Double[]> matrix) {
        List<Double> minMax = new ArrayList<>();
        for (int i = 0; i < matrix.get("point-pos").length; i++) {
            for (String key : matrix.keySet()) {
                minMax.add(matrix.get(key)[i]);
            }
            for (String key : matrix.keySet()) {
                double nom = 0.0, den = 0.0;
                den = Collections.max(minMax) - Collections.min(minMax);
                nom = matrix.get(key)[i] - Collections.min(minMax);
                if (den == 0) {
                    matrix.get(key)[i] = 0.0;
                } else {
                    matrix.get(key)[i] = nom / den;
                }
            }
            minMax.removeAll(minMax);
        }
        return matrix;
    }

    public Map<String, Double[]> normlize2(Map<String, Double[]> matrix) {
        List<Double> minMax = new ArrayList<>();
        for (int i = 0; i < matrix.get("point-pos").length; i++) {
            double den = 0.0;
            for (String key : matrix.keySet()) {
                den = den + (matrix.get(key)[i]);
            }
            for (String key : matrix.keySet()) {
                if (den == 0) {
                    matrix.get(key)[i] = 0.0;
                } else {
                    matrix.get(key)[i] = matrix.get(key)[i] / den;
                }
            }
            minMax.removeAll(minMax);
        }
        return matrix;
    }

    public void swnScoreForAllPOWfiles(String path, String matrixName, Set<String> featuers, int portion, Map<String, Double[]> matrix, String whichPOS, boolean creatDenseOnce) throws IOException {
        // whichPOS="n_AND_v" OR whichPOS="a_and_a"
        StringBuilder allTheSWNterms = new StringBuilder();
        String lineSWNscore;
        List<String> featuresList = new LinkedList<String>();
        BufferedReader readerSWNscore = null;
        try {
            readerSWNscore = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + whichPOS + "\\TwoDim\\Noun_zero_Pos-Neg.txt"));
            for (String term : featuers) {
                featuresList.add(term);
            }
            // n_AND_v || a_and_a || "a_a__v_AND_n" || a_a_AND_v
            List<String> swnScores = new LinkedList<>();
            while ((lineSWNscore = readerSWNscore.readLine()) != null) {
                if (!lineSWNscore.trim().startsWith("#")) {
                    swnScores.add(lineSWNscore);
                    allTheSWNterms.append(lineSWNscore + "\t");
                }
            }
            Map<String, Double> termSWNScore = new LinkedHashMap<>();
            Double[] value = new Double[featuers.size()];
            Map<String, Double[]> SWNScore = new LinkedHashMap<>();
            for (String term : featuers) {
                for (String swnScore : swnScores) {
                    String[] data = swnScore.split("\t");
                    if (term.equalsIgnoreCase(data[0])) {
                        termSWNScore.put(term, Double.parseDouble(data[1]));

                    }
                }
            }
            Map<String, Double[]> denseSumSWN_Matrix = new LinkedHashMap<>();
            Map<String, Double[]> denseAvgSWN_Matrix = new LinkedHashMap<>();

            for (String key : matrix.keySet()) {

                Double[] denseSumSWN = new Double[6];
                Double[] denseAvgSWN = new Double[6];

                Arrays.fill(denseSumSWN, 0.0);
                int j = 0;
                for (String term : featuers) {
                    if (matrix.get(key)[j] != 0.0) {
                        for (String termKey : termSWNScore.keySet()) {
                            if (term.equalsIgnoreCase(termKey)) {
                                //   System.out.println(listOfPOWArray.get(0).get(key)[j] + " :  : "+ termSWNScore.get(termKey));
                                matrix.get(key)[j] = matrix.get(key)[j] * termSWNScore.get(termKey);

                                if (creatDenseOnce == true) {
                                    if (denseMatrex(term, 6) == 1) {

                                        denseSumSWN[0] += denseSumSWN[0];

                                    } else if (denseMatrex(term, 6) == 2) {

                                        denseSumSWN[1] += denseSumSWN[1];

                                    } else if (denseMatrex(term, 6) == 3) {

                                        denseSumSWN[2] += denseSumSWN[2];

                                    } else if (denseMatrex(term, 6) == 4) {

                                        denseSumSWN[3] += denseSumSWN[3];

                                    } else if (denseMatrex(term, 6) == 5) {

                                        denseSumSWN[4] += denseSumSWN[4];

                                    } else if (denseMatrex(term, 6) == 6) {

                                        denseSumSWN[5] += denseSumSWN[5];

                                    }
                                }

                            }
                        }
                    }
                    j++;
                }
                if (creatDenseOnce == true) {
                    denseAvgSWN[0] = denseSumSWN[0] / positiveDocFeaturesSet.size();
                    denseAvgSWN[1] = denseSumSWN[1] / nagitiveDocFeaturesSet.size();
                    denseAvgSWN[2] = denseSumSWN[2] / neutralFeatur.size();

                    denseSumSWN_Matrix.put(key, denseSumSWN);
                    denseAvgSWN_Matrix.put(key, denseAvgSWN);
                }
            }
            resultWriter(path, path + portion + "e-SWN_" + matrixName + ".txt", matrix, featuers);

            if (creatDenseOnce == true) {
                Set<String> summationFeaturesSWN = new LinkedHashSet<>();
                summationFeaturesSWN.add("PositiveSumSWN");
                summationFeaturesSWN.add("NagativeSumSWN");
                summationFeaturesSWN.add("NeutralSumSWN");

                Set<String> averageFeaturesSWN = new LinkedHashSet<>();
                averageFeaturesSWN.add("PositiveAverageSWN");
                averageFeaturesSWN.add("NagativeAverageSWN");
                averageFeaturesSWN.add("NeutralAverageSWN");

                resultWriter(path + "DENSE\\", path + "DENSE\\" + portion + "dense-e-SumSWN.txt", denseSumSWN_Matrix, summationFeaturesSWN);
                resultWriter(path + "DENSE\\", path + "DENSE\\" + portion + "dense-e-AvgSWN.txt", denseAvgSWN_Matrix, averageFeaturesSWN);
            }
            /*
             for (String key : listOfPOWArray.get(0).keySet()) {
             int j = 0;
             for (String term : featuers) {
             if (listOfPOWArray.get(0).get(key)[j] == 1.0) {
             for (String termKey : termSWNScore.keySet()) {
             if (term.equalsIgnoreCase(termKey)) {

             //   System.out.println(listOfPOWArray.get(0).get(key)[j] + " :  : "+ termSWNScore.get(termKey));
             listOfPOWArray.get(0).get(key)[j] = termSWNScore.get(termKey);
             }
             }
             }

             j++;
             }

             }
             for (int i = 1; i < listOfPOWArray.size(); i++) {
             for (String keys : listOfPOWArray.get(i).keySet()) {
             int j = 0;
             for (String term : featuers) {
             listOfPOWArray.get(i).get(keys)[j] = listOfPOWArray.get(i).get(keys)[j] * listOfPOWArray.get(0).get(keys)[j];
             j++;
             }

             }
             }
             */
            readerSWNscore.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (readerSWNscore != null) {
                readerSWNscore.close();
            }
        }
    }

    public void addingSWNscoreToGivenDoc(String docFileName, String swnFileName, String termLabeledFile) throws IOException {
        //docFileName is the Text of all the Document where each one as a Double Values. Kyes"\t"0.0"\t"0.0"\t"..
        //swnFileName is the text file of each term with avarage of the sentiWordnet score. good#a 0.0
        //termLabeledFile has all SET of all the Features. good#a
        StringBuilder allTheSWNterms = new StringBuilder();

        BufferedWriter writer2 = null;
        BufferedReader readerDoc = null;
        BufferedReader readerSWNscore = null;
        BufferedReader readerFeatures = null;
        try {
            writer2 = new BufferedWriter(new FileWriter("OUTPUT\\Adding_SentiWordNet_Score.txt"));
            readerDoc = new BufferedReader(new FileReader("OUTPUT\\POW\\" + docFileName));
            readerSWNscore = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + swnFileName));
            readerFeatures = new BufferedReader(new FileReader("OUTPUT\\ProcessedDoc\\" + termLabeledFile));

            int j = 1;

            String lineDoc, lineFeatures, lineSWNscore;
            List<String> tempFeatures = new LinkedList<>();
            //the first element is NULL, Not counte.
            while ((lineFeatures = readerFeatures.readLine()) != null) {
                if (!lineFeatures.trim().startsWith("#")) {
                    tempFeatures.add(lineFeatures);
                }
            }
            writer2.write("# Adding SentiWordNet Scores to " + docFileName + " by using " + swnFileName + " AND " + termLabeledFile);
            writer2.newLine();
            writer2.write("#" + tempFeatures);
            writer2.newLine();
            String[] LabeledFeaturesList = new String[tempFeatures.size()];
            int i = 0;
            for (String word : tempFeatures) {
                LabeledFeaturesList[i] = word;
                i++;
            }
            List<String> swnScores = new LinkedList<>();
            while ((lineSWNscore = readerSWNscore.readLine()) != null) {
                if (!lineSWNscore.trim().startsWith("#")) {
                    swnScores.add(lineSWNscore);
                    allTheSWNterms.append(lineSWNscore + "\t");
                }
            }

            while ((lineDoc = readerDoc.readLine()) != null) {
                if (!lineDoc.trim().startsWith("#")) {
                    String[] dataDoc = lineDoc.split("\t");
                    if (dataDoc.length != LabeledFeaturesList.length) {
                        System.out.println("Documents file:Incorrect tabulation format in file, line: ");
                        throw new IllegalArgumentException(
                                "Documents file:Incorrect tabulation format in file, line: ");
                    }
// this array will have the values which will be marage with SWN scores then will be written in a text file
//the first element is zero and will not consider.
                    double[] dataLineOfDoc = new double[dataDoc.length];
                    dataLineOfDoc[0] = 0.0;
                    for (int h = 1; h < dataDoc.length; h++) {
                        dataLineOfDoc[h] = Double.parseDouble(dataDoc[h]);
                    }

                    writer2.write(dataDoc[0] + "\t");
                    for (j = 1; j < LabeledFeaturesList.length; j++) {

                        double docValue = 0.0;
                        boolean TermFoundInSWN;
                        if (TermFoundInSWN = StringUtils.
                                containsIgnoreCase(allTheSWNterms, LabeledFeaturesList[j])) //this condition is for getting the original value of the term if it is not found in the SentiWordNet
                        {
                            for (String swnScore : swnScores) {
                                String[] data = swnScore.split("\t");
                                if (LabeledFeaturesList[j].equalsIgnoreCase(data[0])) {
//NOTE here if the condition is false the value will not be changes because we did not find a score for the word.
                                    docValue = Double.parseDouble(dataDoc[j]) * Double.parseDouble(data[3]);
                                    dataLineOfDoc[j] = docValue;
                                }

                            }
                        } else {
                            //writer.write(j+"="+dataDoc[j] + "\t");
                        }
                    }
                    for (int t = 1; t < dataLineOfDoc.length; t++) {
                        writer2.write(dataLineOfDoc[t] + "\t");
                    }
                    writer2.newLine();
                }
            }
            writer2.close();
            readerDoc.close();
            readerSWNscore.close();
            readerFeatures.close();
        } catch (IOException e) {
        } finally {
            if (writer2 != null) {
                writer2.close();
            }
            if (readerDoc != null) {
                readerDoc.close();
            }
            if (readerSWNscore != null) {
                readerSWNscore.close();
            }
            if (readerFeatures != null) {
                readerFeatures.close();
            }
        }
    }

    public Map<String, Double[]> hashWordPresenceHash(Set<String> listOfFeatures, Map<String, String> docs) {
        Map<String, Double[]> docsWordPresenceHash = new LinkedHashMap<>();
        int inc = 0;
        for (String label : docs.keySet()) {

            inc++;
            String keys = "doc" + inc + "," + label + " : ";
            Double[] value = new Double[listOfFeatures.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = 0.0;
            }

            //System.out.println();
            Pattern word = Pattern.compile("[\\w]+");
            Matcher matchedword = word.matcher(docs.get(label));
            while (matchedword.find()) {
                int i = 0;
                // System.out.println(matchedword.group());
                //System.out.print(" "+matchedword.group());
                for (String words : listOfFeatures) {
                    if (words.equalsIgnoreCase(matchedword.group()) == true) {
                        value[i] = 1.0;
                        i++;
                        // System.out.println(matchedword.group() +":: " + words);
                    } else {

                        i++;
                    }

                }
            }
            docsWordPresenceHash.put(keys, value);
        }

        return docsWordPresenceHash;
    }

    /* Here the Method accept a hasmap where the keyes are the Labels which are also the name of the files, this hashMap
     ganerated by this Method in the Reading Class readingDocsToMap("path").

     */
    class TFIDF {

        LinkedHashMap<String, Double> adjectivesAndAdverbsWithIDF = new LinkedHashMap<>();

        //  LinkedHashMap<String, Double> idf = new LinkedHashMap<>();
        public Map<String, Double> functionOfIDF(Set<String> setOfFeatures, Map<String, String> docs) {
            double i = 0;
            for (String s : setOfFeatures) {
                for (String k : docs.keySet()) {
                    if (StringUtils.contains(docs.get(k), s) == true) {
                        i++;
                    }
                }
                adjectivesAndAdverbsWithIDF.put(s, (Math.log10(docs.size() / i)));
                //  idf.put(s, round(Math.log10(docs.size() / i)));
                i = 0;
            }
            return adjectivesAndAdverbsWithIDF;
            // return idf;
        }

        public double termNormalization(int tremFrequency, int docLength) {
            double normalization = (((double) tremFrequency / (double) docLength));
            return normalization;
        }

        public double termLogarithmFrequency(double tf) {
            double wieght = 0.0;
            if (tf > 0) {
                wieght = (1 + Math.log10(tf));
            }
            return wieght;
        }
    }

    class Sequential_TFIDF {

        LinkedHashMap<String, Double> idf = new LinkedHashMap<>();
        LinkedHashMap<String, Integer> numDocsHaveIt_IDF = new LinkedHashMap<>();
        int docsSize = 0;

        public void sequential_functionOfIDF(int winNumber) {
            System.out.print("bagOfWordFixDimintions().sequential_functionOfIDF() is running .. ");
            idf.clear();
            if (winNumber == 1) {
                for (String s : SWNFeatures) {
                    int i = 0;
                    for (String k : Docs_A_and_a.keySet()) {
                        if (StringUtils.containsIgnoreCase(Docs_A_and_a.get(k), s) == true) {
                            i++;
                        }
                    }
                    numDocsHaveIt_IDF.put(s, i);
                    idf.put(s, round(Math.log10(Docs_A_and_a.size() / i))); // every word will at least be found in one document, because we used the seeds the long feature. Therefore, we dont need add one in the formula
                    // This can be research more.
                }
                docsSize = Docs_A_and_a.size();
                save_old_numDocsHaveIt();
                save_new_docSize();
            } else {
                reading_old_numDocsHaveIt();
                docsSize = Docs_A_and_a.size() + reading_old_docSize();
                save_new_docSize();
                for (String s : SWNFeatures) {
                    for (String k : Docs_A_and_a.keySet()) {
                        if (StringUtils.containsIgnoreCase(Docs_A_and_a.get(k), s) == true) {
                            if (numDocsHaveIt_IDF.containsKey(s)) {
                                numDocsHaveIt_IDF.put(s, numDocsHaveIt_IDF.get(s) + 1);
                            } else {
                                numDocsHaveIt_IDF.put(s, 1);
                            }
                        }
                    }
                    idf.put(s, round(Math.log10(docsSize / numDocsHaveIt_IDF.get(s))));
                }
                save_old_numDocsHaveIt();
                save_new_docSize();
            }
            System.out.println(". fininshed.");
        }

        public void reading_old_numDocsHaveIt() {
            numDocsHaveIt_IDF.clear();
            try (BufferedReader read = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\IDF\\idf.txt"))) {
                String line;
                while ((line = read.readLine()) != null) {
                    String[] value = line.split("\t");
                    numDocsHaveIt_IDF.put(value[0], Integer.valueOf(value[1]));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public int reading_old_docSize() {
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

        public void save_new_docSize() {
            File directory = new File("F:\\Dropbox\\Coding\\OUTPUT\\IDFdocSize");
            directory.mkdirs();
            try (BufferedWriter write = new BufferedWriter(new BufferedWriter(new FileWriter("F:\\Dropbox\\Coding\\OUTPUT\\IDFdocSize\\IDFdocSize.txt")))) {
                write.write(String.valueOf(docsSize));
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public void save_old_numDocsHaveIt() {
            File directory = new File("F:\\Dropbox\\Coding\\OUTPUT\\IDF");
            directory.mkdirs();
            try (BufferedWriter write = new BufferedWriter(new BufferedWriter(new FileWriter("F:\\Dropbox\\Coding\\OUTPUT\\IDF\\idf.txt")))) {
                for (String feature : numDocsHaveIt_IDF.keySet()) {
                    write.write(feature + "\t" + numDocsHaveIt_IDF.get(feature));
                    write.newLine();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        public double termNormalization(int tremFrequency, int docLength) {
            double normalization = round(((double) tremFrequency / (double) docLength));
            return normalization;
        }

        public double termLogarithmFrequency(double tf) {
            double wieght = 0.0;
            if (tf > 0) {
                wieght = round(1 + Math.log10(tf));
            }
            return wieght;
        }
    }

    public static Map<String, String> Docs_A_and_a = new LinkedHashMap<>();
    public Set<String> features = new LinkedHashSet<>();
    public Set<String> SWNFeatures = new LinkedHashSet<>();
    public Set<String> SWNFeaturesNEG = new LinkedHashSet<>();
    public Set<String> SWNFeaturesPOS = new LinkedHashSet<>();
    public Map<String, Double> termSWNScore = new LinkedHashMap<>();
    public Map<String, Integer> SWNFeatures_position = new LinkedHashMap<>();
    public static Map<String, String> docs = new LinkedHashMap<>();
    public static Map<String, Integer> docsLength = new LinkedHashMap<>();
    public static List< Integer> docsLength_all_windows = new ArrayList<>();// This is for BM25

    public void readingDocsToMap() throws IOException, Exception {
//BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("OUTPUT\\ProcessedDoc\\all_Docs.txt"))))
        //
        docs.clear();
        File f = new File("OUTPUT\\ProcessedDoc\\all_Docs.txt");
        if (f.exists() && f.isFile()) {
            System.out.println("GOOD");
        } else {
            System.out.println("There is no file ");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader("OUTPUT\\ProcessedDoc\\all_Docs.txt"))) {

            String doc;
            while ((doc = reader.readLine()) != null) {
                String[] data = doc.split("\t");
                docs.put(data[0], data[1]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File with TXT Extention might be there, chick for"
                    + "another posible  problem");
        }
        if (docs.isEmpty()) {
            System.exit(0);
            System.out.println("readingDocsToMap(). Docs SIZA is ::: " + docs.size());
        }
    }

    public void a_and_a_docs_SWN(String whichPOS, int winNumber) {
        System.out.print("a_and_a_docs_SWN() is runing .. ");
        String lineNEG = null, linePOS = null, line = null;
        StringBuilder NegDoc = new StringBuilder();
        StringBuilder PosDoc = new StringBuilder();
        termSWNScore.clear();
        Docs_A_and_a.clear();
        docsLength.clear();
        SWNFeatures.removeAll(SWNFeatures);
        SWNFeaturesNEG.removeAll(SWNFeaturesNEG);
        SWNFeaturesPOS.removeAll(SWNFeaturesPOS);
        features.removeAll(features);
        SWNFeatures_position.clear();

        try (BufferedReader readerSWNscore = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\a_AND_a\\a_and_a_docs_SWN()\\Noun_zero_Pos-Neg.txt"))) {
            while ((line = readerSWNscore.readLine()) != null) {
                String[] data = line.split("\t");
                SWNFeatures.add(data[0]);
                termSWNScore.put(data[0], Double.valueOf(data[1]));
            }

        } catch (IOException e) {
        }

        int i = 0;
        for (String term : SWNFeatures) {
            SWNFeatures_position.put(term, i);
            i++;
        }
        for (String term : termSWNScore.keySet()) {
            if (termSWNScore.get(term) < 0) {
                SWNFeaturesNEG.add(term);
                NegDoc.append(term + "\t");
            } else {
                SWNFeaturesPOS.add(term);
                PosDoc.append(term + "\t");
            }
        }
        for (String key : docs.keySet()) {
            StringBuilder docJJandRB = new StringBuilder();
            Pattern word = Pattern.compile("[\\w]+");
            Matcher matchedword = word.matcher(docs.get(key).toLowerCase());
            ArrayList<String> length = new ArrayList<>();
            while (matchedword.find()) {
                length.add(matchedword.group());
                if (SWNFeatures.contains(matchedword.group().toLowerCase())) {
                    features.add(matchedword.group().toLowerCase());
                    docJJandRB.append(matchedword.group().toLowerCase() + "\t");
                }
            }
            docsLength.put(key, length.size());
            docsLength_all_windows.add(length.size());
            Docs_A_and_a.put(key, docJJandRB.toString());
        }

        Docs_A_and_a.put("point-neg", NegDoc.toString());
        Docs_A_and_a.put("point-pos", PosDoc.toString());
        docsLength.put("point-neg", SWNFeaturesNEG.size());
        docsLength.put("point-pos", SWNFeaturesPOS.size());
        System.out.println(". fininshed.");

    }

    public double avgDL_BM25() throws IOException {
        double sumDl = 0.0;
        for (int l : docsLength_all_windows) {
            sumDl = sumDl + l;
        }
        System.out.println("docsLength_all_windows : " + docsLength_all_windows.size());
        return round(sumDl / docsLength_all_windows.size());
    }

    public void bagOfWordFixDimintions(String path, int portion, String whichPOS, int winNumber) throws IOException {
        System.out.println("bagOfWordFixDimintions() is runing .. ");
        double avgDL = avgDL_BM25();
        double k = 1.2, b = 0.75;
        double k2 = 2.0;
        Map<String, Double[]> frequency_TFIDF = new LinkedHashMap<>();
        Map<String, Double[]> frequency = new LinkedHashMap<>();
        Map<String, Double[]> frequency_Term_Normalization = new LinkedHashMap<>();
        Map<String, Double[]> presence = new LinkedHashMap<>();
        Map<String, Double[]> presence_TFIDF = new LinkedHashMap<>();
        Map<String, Double[]> presence_Term_Normalization = new LinkedHashMap<>();
        Map<String, Double[]> presence_WFIDF = new LinkedHashMap<>();
        Map<String, Double[]> frequency_WFIDF = new LinkedHashMap<>();
        Map<String, Double[]> presence_AW = new LinkedHashMap<>();
        Map<String, Double[]> frequency_AW = new LinkedHashMap<>();
        Map<String, Double[]> frequency_IDF = new LinkedHashMap<>();
        Map<String, Double[]> presence_IDF = new LinkedHashMap<>();
        // second paper
        Map<String, Double[]> frequency_2BM25 = new LinkedHashMap<>();
        Map<String, Double[]> frequency_BM25 = new LinkedHashMap<>();
        Map<String, Double[]> presence_2BM25 = new LinkedHashMap<>();
        Map<String, Double[]> presence_BM25 = new LinkedHashMap<>();
        // second paper
        Sequential_TFIDF tfidf = new Sequential_TFIDF();
        tfidf.sequential_functionOfIDF(winNumber);
        // TFIDF tfidf = new TFIDF();
        // tfidf.functionOfIDF(SWNFeatures, Docs_A_and_a);
        for (String keys : Docs_A_and_a.keySet()) {
            double p = 0, g = 0, n = 0;
            Double[] valueFrequency = new Double[SWNFeatures.size()];
            Double[] valueFrequencyTFIDF = new Double[SWNFeatures.size()];
            Double[] valueFrequencyNormalization = new Double[SWNFeatures.size()];
            Double[] valuePresence = new Double[SWNFeatures.size()];
            Double[] valuePresenceTFIDF = new Double[SWNFeatures.size()];
            Double[] valuePresenceNormalization = new Double[SWNFeatures.size()];
            Double[] valuePresenceWFIDF = new Double[SWNFeatures.size()];
            Double[] valueFrequencyWFIDF = new Double[SWNFeatures.size()];
            Double[] valuePresenceAW = new Double[SWNFeatures.size()];
            Double[] valueFrequencyAW = new Double[SWNFeatures.size()];
            Double[] valueFrequencyIDF = new Double[SWNFeatures.size()];
            Double[] valuePresenceIDF = new Double[SWNFeatures.size()];
            // second paper
            Double[] valueFrequency2BM25 = new Double[SWNFeatures.size()];
            Double[] valueFrequencyBM25 = new Double[SWNFeatures.size()];
            Double[] valuePresence2BM25 = new Double[SWNFeatures.size()];
            Double[] valuePresenceBM25 = new Double[SWNFeatures.size()];
            //second paper
            Arrays.fill(valueFrequency, 0.0);
            Arrays.fill(valueFrequencyTFIDF, 0.0);
            Arrays.fill(valueFrequencyNormalization, 0.0);
            Arrays.fill(valuePresence, 0.0);
            Arrays.fill(valuePresenceTFIDF, 0.0);
            Arrays.fill(valuePresenceNormalization, 0.0);
            Arrays.fill(valuePresenceWFIDF, 0.0);
            Arrays.fill(valueFrequencyWFIDF, 0.0);
            Arrays.fill(valuePresenceAW, 0.0);
            Arrays.fill(valueFrequencyAW, 0.0);
            Arrays.fill(valueFrequencyIDF, 0.0);
            Arrays.fill(valuePresenceIDF, 0.0);
            // second paper
            Arrays.fill(valueFrequency2BM25, 0.0);
            Arrays.fill(valueFrequencyBM25, 0.0);
            Arrays.fill(valuePresence2BM25, 0.0);
            Arrays.fill(valuePresenceBM25, 0.0);
            // second paper
            //FOr TF the wight spaces count is equal to the terms counts.
            // int numberOfTerms = StringUtils.countMatches(Docs_A_and_a.get(keys), "\t");//For TF length of adjectivs and avderbs only
            int numberOfTerms = docsLength.get(keys);//For TF length of hole docs
            if (keys.equalsIgnoreCase("point-neg") || keys.equalsIgnoreCase("point-pos")) {
                String[] DocumentWord = Docs_A_and_a.get(keys).split("\t");
                for (String term : DocumentWord) {
                    int numberOfSpecificTermInDoc = StringUtils.countMatches(Docs_A_and_a.get(keys), term);//For TF
                    //  numberOfSpecificTermInDoc2 = numberOfSpecificTermInDoc * 1.0;
                    double numberOfSpecificTermInDoc2 = Math.log(numberOfSpecificTermInDoc + 1);
                    //double numberOfSpecificTermInDoc2 = round(Math.sqrt(numberOfSpecificTermInDoc));
                    valueFrequency[SWNFeatures_position.get(term)] = numberOfSpecificTermInDoc2;
                    valueFrequencyTFIDF[SWNFeatures_position.get(term)] = (numberOfSpecificTermInDoc2 * (tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms) * tfidf.idf.get(term)));
                    valueFrequencyNormalization[SWNFeatures_position.get(term)] = (numberOfSpecificTermInDoc2 * tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms));
                    valuePresence[SWNFeatures_position.get(term)] = 1.0;
                    valuePresenceTFIDF[SWNFeatures_position.get(term)] = (tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms) * tfidf.idf.get(term));
                    valuePresenceNormalization[SWNFeatures_position.get(term)] = (tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms));
                    valuePresenceWFIDF[SWNFeatures_position.get(term)] = ((tfidf.termLogarithmFrequency(numberOfSpecificTermInDoc) + 1) * tfidf.idf.get(term));
                    valueFrequencyWFIDF[SWNFeatures_position.get(term)] = (numberOfSpecificTermInDoc2 * ((tfidf.termLogarithmFrequency(numberOfSpecificTermInDoc) + 1) * tfidf.idf.get(term)));
                    valuePresenceAW[SWNFeatures_position.get(term)] = ((valuePresenceTFIDF[SWNFeatures_position.get(term)] + valuePresenceWFIDF[SWNFeatures_position.get(term)]) / 2);
                    valueFrequencyAW[SWNFeatures_position.get(term)] = (numberOfSpecificTermInDoc2 * ((valuePresenceTFIDF[SWNFeatures_position.get(term)] + valuePresenceWFIDF[SWNFeatures_position.get(term)]) / 2));
                    valuePresenceIDF[SWNFeatures_position.get(term)] = (tfidf.idf.get(term));
                    valueFrequencyIDF[SWNFeatures_position.get(term)] = (numberOfSpecificTermInDoc2 * tfidf.idf.get(term));
                    valuePresenceBM25[SWNFeatures_position.get(term)] = ((tfidf.idf.get(term) * ((k + 1) * numberOfSpecificTermInDoc)) / (k * ((1 - b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc));
                    valueFrequencyBM25[SWNFeatures_position.get(term)] = (numberOfSpecificTermInDoc2 * ((tfidf.idf.get(term) * ((k + 1) * numberOfSpecificTermInDoc)) / (k * ((1 - b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc)));
                       valuePresence2BM25[SWNFeatures_position.get(term)] = round(((tfidf.idf.get(term) * ((k2 + 1) * numberOfSpecificTermInDoc)) / (k2 * ((1 - b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc)) * (((k2 + 1) * numberOfSpecificTermInDoc) / k2 + numberOfSpecificTermInDoc));
                       valueFrequency2BM25[SWNFeatures_position.get(term)] = round(numberOfSpecificTermInDoc2 * ((tfidf.idf.get(term) * ((k2 + 1) * numberOfSpecificTermInDoc)) / (k2 * ((1 - b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc)) * (((k2 + 1) * numberOfSpecificTermInDoc) / k2 + numberOfSpecificTermInDoc));

                }
            } else {
                String[] DocumentWord = Docs_A_and_a.get(keys).split("\t");
                for (String word : DocumentWord) {
                    boolean stop = false;
                    int numberOfSpecificTermInDoc = 0;
                    double numberOfSpecificTermInDoc2 = 0.0;
                    for (String term : features) {
                        numberOfSpecificTermInDoc = StringUtils.countMatches(Docs_A_and_a.get(keys), term);//For TF
                        //  numberOfSpecificTermInDoc2 = numberOfSpecificTermInDoc * 1.0;
                        numberOfSpecificTermInDoc2 = round(Math.log10(numberOfSpecificTermInDoc + 1));
                        //numberOfSpecificTermInDoc2 = round(Math.sqrt(numberOfSpecificTermInDoc));
                        if (term.equalsIgnoreCase(word) == true) {
                            stop = true;
                            valueFrequency[SWNFeatures_position.get(term)] = numberOfSpecificTermInDoc2;
                            valueFrequencyTFIDF[SWNFeatures_position.get(term)] = round(numberOfSpecificTermInDoc2 * (tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms) * tfidf.idf.get(term)));
                            valueFrequencyNormalization[SWNFeatures_position.get(term)] = round(numberOfSpecificTermInDoc2 * tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms));
                            valuePresence[SWNFeatures_position.get(term)] = 1.0;
                            valuePresenceTFIDF[SWNFeatures_position.get(term)] = round(tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms) * tfidf.idf.get(term));
                            valuePresenceNormalization[SWNFeatures_position.get(term)] = round(tfidf.termNormalization(numberOfSpecificTermInDoc, numberOfTerms));
                            valuePresenceWFIDF[SWNFeatures_position.get(term)] = round((tfidf.termLogarithmFrequency(numberOfSpecificTermInDoc) + 1) * tfidf.idf.get(term));
                            valueFrequencyWFIDF[SWNFeatures_position.get(term)] = round(numberOfSpecificTermInDoc2 * ((tfidf.termLogarithmFrequency(numberOfSpecificTermInDoc) + 1) * tfidf.idf.get(term)));
                            valuePresenceAW[SWNFeatures_position.get(term)] = round((valuePresenceTFIDF[SWNFeatures_position.get(term)] + valuePresenceWFIDF[SWNFeatures_position.get(term)]) / 2);
                            valueFrequencyAW[SWNFeatures_position.get(term)] = round(numberOfSpecificTermInDoc2 * ((valuePresenceTFIDF[SWNFeatures_position.get(term)] + valuePresenceWFIDF[SWNFeatures_position.get(term)]) / 2));
                            valuePresenceIDF[SWNFeatures_position.get(term)] = round(tfidf.idf.get(term));
                            valueFrequencyIDF[SWNFeatures_position.get(term)] = round(numberOfSpecificTermInDoc2 * tfidf.idf.get(term));
                            valuePresenceBM25[SWNFeatures_position.get(term)] = round(tfidf.idf.get(term) * (((k + 1) * numberOfSpecificTermInDoc2)) / (k * ((1 - b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc2));
                            valueFrequencyBM25[SWNFeatures_position.get(term)] = round(numberOfSpecificTermInDoc2 * ((tfidf.idf.get(term) * ((k + 1) * numberOfSpecificTermInDoc2)) / (k * ((1 - b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc2)));
                               valuePresence2BM25[SWNFeatures_position.get(term)] = round(((tfidf.idf.get(term) * ((k2 + 1) * numberOfSpecificTermInDoc)) / (k2 * ((1 - b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc)) * (((k2 + 1) * numberOfSpecificTermInDoc) / k2 + numberOfSpecificTermInDoc));
                               valueFrequency2BM25[SWNFeatures_position.get(term)] = round(numberOfSpecificTermInDoc2 * ((tfidf.idf.get(term) * ((k2 + 1) * numberOfSpecificTermInDoc)) / (k2 * ((1- b) + b * (numberOfTerms / avgDL)) + numberOfSpecificTermInDoc)) * (((k2 + 1) * numberOfSpecificTermInDoc) / k2 + numberOfSpecificTermInDoc));

                            /*

                             valueFrequencyStf[SWNFeatures_position.get(term)] =round(Math.sqrt(numberOfSpecificTermInDoc ));
                             valueFrequencyStfL [SWNFeatures_position.get(term)]=
                             valueFrequencyBM25[SWNFeatures_position.get(term)] =
                             valuePresenceStfL [SWNFeatures_position.get(term)]=
                             valuePresenceBM25 [SWNFeatures_position.get(term)]=

                             */
                        }
                        if (stop == true) {
                            break;
                        }
                    }
                }
            }
            presence.put(keys, valuePresence);
            frequency.put(keys, valueFrequency);
            frequency_TFIDF.put(keys, valueFrequencyTFIDF);
            frequency_Term_Normalization.put(keys, valueFrequencyNormalization);
            presence_TFIDF.put(keys, valuePresenceTFIDF);
            presence_Term_Normalization.put(keys, valuePresenceNormalization);
            presence_WFIDF.put(keys, valuePresenceWFIDF);
            frequency_WFIDF.put(keys, valueFrequencyWFIDF);
            presence_AW.put(keys, valuePresenceAW);
            frequency_AW.put(keys, valueFrequencyAW);
            frequency_IDF.put(keys, valueFrequencyIDF);
            presence_IDF.put(keys, valuePresenceIDF);
            presence_BM25.put(keys, valuePresenceBM25);
            frequency_BM25.put(keys, valueFrequencyBM25);
              presence_2BM25.put(keys, valuePresenceBM25);
              frequency_2BM25.put(keys, valueFrequencyBM25);

        }
        System.out.println(" fors are finished");
        resultWriter(path, path + portion + "e-presence.txt", presence, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "presence", portion, presence, whichPOS);

        resultWriter(path, path + portion + "e-Frequency.txt", frequency, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "frequency", portion, frequency, whichPOS);

        resultWriter(path, path + portion + "e-Frequency_TFIDF.txt", frequency_TFIDF, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "frequency_TFIDF", portion, frequency_TFIDF, whichPOS);

        resultWriter(path, path + portion + "e-Frequency_Term_Normalization.txt", frequency_Term_Normalization, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "frequency_Term_Normalization", portion, frequency_Term_Normalization, whichPOS);

        resultWriter(path, path + portion + "e-Presence_TFIDF.txt", presence_TFIDF, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "presence_TFIDF", portion, presence_TFIDF, whichPOS);

        resultWriter(path, path + portion + "e-Presence_Term_Normalization.txt", presence_Term_Normalization, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "presence_Term_Normalization", portion, presence_Term_Normalization, whichPOS);

        resultWriter(path, path + portion + "e-Presence_WFIDF.txt", presence_WFIDF, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "presence_WFIDF", portion, presence_WFIDF, whichPOS);

        resultWriter(path, path + portion + "e-Frequency_WFIDF.txt", frequency_WFIDF, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "frequency_WFIDF", portion, frequency_WFIDF, whichPOS);

        resultWriter(path, path + portion + "e-presence_AW.txt", presence_AW, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "presence_AW", portion, presence_AW, whichPOS);

        resultWriter(path, path + portion + "e-frequency_AW.txt", frequency_AW, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "frequency_AW", portion, frequency_AW, whichPOS);

        resultWriter(path, path + portion + "e-frequency_IDF.txt", frequency_IDF, SWNFeatures);//new
        sequential_swnScoreForAllPOWfiles(path, winNumber, "frequency_IDF", portion, frequency_IDF, whichPOS);

        resultWriter(path, path + portion + "e-presence_IDF.txt", presence_IDF, SWNFeatures);//new
        sequential_swnScoreForAllPOWfiles(path, winNumber, "presence_IDF", portion, presence_IDF, whichPOS);

        resultWriter(path, path + portion + "e-presence_BM25.txt", presence_BM25, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "presence_BM25", portion, presence_BM25, whichPOS);
        resultWriter(path, path + portion + "e-frequency_BM25.txt", frequency_BM25, SWNFeatures);
        sequential_swnScoreForAllPOWfiles(path, winNumber, "frequency_BM25", portion, frequency_BM25, whichPOS);
           resultWriter(path, path + portion + "e-presence_2BM25.txt", presence_2BM25, SWNFeatures);
           sequential_swnScoreForAllPOWfiles(path, winNumber, "presence_2BM25", portion, presence_2BM25, whichPOS);
           resultWriter(path, path + portion + "e-frequency_2BM25.txt", frequency_2BM25, SWNFeatures);
            sequential_swnScoreForAllPOWfiles(path, winNumber, "frequency_2BM25", portion, frequency_2BM25, whichPOS);
        fileFroMatlab(frequency_TFIDF, presence_TFIDF, winNumber);
        System.out.println(".. fininshed ");
    }

    public void fileFroMatlab(Map<String, Double[]> f, Map<String, Double[]> p, int winNumber) throws IOException {
        //NonZero 0.0001 feature.
        List<Double[]> f1 = new LinkedList<>();
        List<Double[]> p1 = new LinkedList<>();
        for (String key : f.keySet()) {
            Double[] vals = new Double[f.get(key).length + 2];
            String[] data = key.split("-");
            if (data[1].equalsIgnoreCase("pos")) {
                vals[0] = 1.0;
            } else if (data[1].equalsIgnoreCase("neg")) {
                vals[0] = 2.0;
            }
            int h = 1;
            for (int i = 0; i < f.get(key).length; i++) {
                vals[h] = f.get(key)[i];
                h++;
            }
            vals[vals.length - 1] = 0.0001;
            f1.add(vals);
        }
        for (String key : p.keySet()) {
            Double[] vals = new Double[p.get(key).length + 2];
            String[] data = key.split("-");
            if (data[1].equalsIgnoreCase("pos")) {
                vals[0] = 1.0;
            } else if (data[1].equalsIgnoreCase("neg")) {
                vals[0] = 2.0;
            }
            int h = 1;
            for (int i = 0; i < p.get(key).length; i++) {
                vals[h] = p.get(key)[i];
                h++;
            }
            vals[vals.length - 1] = 0.0001;
            p1.add(vals);
        }
        // writeing
        String path = "OUTPUT\\Matrixes_Matlab\\";
        resultWriter2(path, path + "FrequencyTFIDF" + winNumber + ".txt", f1, SWNFeatures);
        resultWriter2(path, path + "PresenceTFIDF" + winNumber + ".txt", p1, SWNFeatures);
    }

    public void resultWriter2(String path, String fileName, List<Double[]> docValu, Set<String> labeledFeatureSet) throws IOException {
        File directory = new File(path);
        directory.mkdirs();
        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter(fileName))) {
            //  newFileSWN.write("class" + "\t"); //Matlab
            newFileSWN.write("class" + ","); // Weka
            for (String feature : labeledFeatureSet) {
                //  newFileSWN.write(feature + "\t");//Matlab
                newFileSWN.write(feature + ",");// Weka
            }
            // newFileSWN.write("NonZero");
            newFileSWN.newLine();
            for (Double[] x : docValu) {
                for (int i = 0; i < x.length; i++) {
                    //newFileSWN.write(Double.toString(x[i]) + "\t");//Matlab
                    newFileSWN.write(Double.toString(x[i]) + ",");// Weka
                }
                newFileSWN.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error opening the file" + fileName);
        }
    }

    public void resultWriter2Weka(String path, String fileName, List<Double[]> docValu, Set<String> labeledFeatureSet) throws IOException {
        File directory = new File(path);
        directory.mkdirs();
        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter(fileName))) {
            newFileSWN.write("@relation " + fileName + ".arff" + "\n"); // Weka
            newFileSWN.write("@attribute class {2.0,1.0}" + "\n"); // Weka @attribute class {2.0,1.0}
            for (String feature : labeledFeatureSet) {
                newFileSWN.write("@attribute " + feature + " numeric" + "\n");// Weka
            }
            newFileSWN.write("@data" + "\n");
            newFileSWN.newLine();
            for (Double[] x : docValu) {
                for (int i = 0; i < x.length; i++) {
                    newFileSWN.write(Double.toString(x[i]) + ",");// Weka
                }
                newFileSWN.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error opening the file" + fileName);
        }
    }

    public void sequential_swnScoreForAllPOWfiles(String path, int winNumber, String matrixName, int portion, Map<String, Double[]> matrix, String whichPOS) throws IOException {
        for (String key : matrix.keySet()) {
            int j = 0;
            for (String Key : termSWNScore.keySet()) {
                if (matrix.get(key)[j] != 0.0) {
                    matrix.get(key)[j] = matrix.get(key)[j] * termSWNScore.get(Key);
                }
                j++;
            }
        }
        resultWriter(path, path + portion + "e-SWN_" + matrixName + ".txt", matrix, SWNFeatures);
    }

    public void outputAsTextFiles(String path, String fileName, Set<String> listOfFeatures) throws IOException {

        File del = new File(path + fileName);
        try {
            if (del.exists()) {
                del.delete();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        File directory = new File(path);
        directory.mkdirs();
        try (BufferedWriter outputStream1 = new BufferedWriter(new FileWriter(path + fileName))) {
            outputStream1.write("NULL#");
            outputStream1.newLine();
            for (String words : listOfFeatures) {
                outputStream1.write(words);
                outputStream1.newLine();
            }
            System.out.println(" 'All Adjectives And Adverbs words.txt' in the corpus is GANERATED.");
        } catch (IOException e) {
            System.out.println("Error opening the file " + fileName);
        }
    }

    public void resultWriter(String path, String fileName, Map<String, Double[]> docValu, Set<String> labeledFeatureSet) throws IOException {
        File directory = new File(path);
        directory.mkdirs();
        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter(fileName))) {
            newFileSWN.write("#" + fileName);
            newFileSWN.newLine();
            newFileSWN.write("NULL#" + "\t");
            for (String feature : labeledFeatureSet) {
                newFileSWN.write(feature + "\t");
            }
            newFileSWN.newLine();
            for (String key : docValu.keySet()) {
                newFileSWN.write(key + "\t");
                Double[] value = docValu.get(key);
                for (Double v : value) {
                    newFileSWN.write(Double.toString(v) + "\t");
                }
                newFileSWN.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error opening the file" + fileName);
        }
    }

    public void resultWri(String path, String fileName, Map<String, String> docWords) throws IOException {
        File del = new File(path + fileName);
        try {
            if (del.exists()) {
                del.delete();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        File directory = new File(path);
        directory.mkdirs();
        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter(path + fileName));) {
            for (String key : docWords.keySet()) {
                if (docWords.get(key).isEmpty()) {
                    System.out.println("The document : " + key + "is empty.");
                } else {
                    newFileSWN.write(key + "\t" + docWords.get(key).trim());
                    newFileSWN.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    {

    }
}

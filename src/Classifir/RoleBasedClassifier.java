/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import Preprocessing.Reading;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author morteza
 */
public class RoleBasedClassifier {

    public static Map<String, String> docs2 = new LinkedHashMap<String, String>();
    public static Map<String, String> docMap = new LinkedHashMap<String, String>();
    public static Map<String, String> labeledDocMap = new LinkedHashMap<String, String>();
    Set<String> SetOfFeatures = new LinkedHashSet<>();
    public static String POSModel = "F:\\Dropbox\\Coding\\stanford-postagger-2015-01-30\\models\\english-left3words-distsim.tagger";
    MaxentTagger tagger = new MaxentTagger(POSModel);
    public static Map<String, Double> docsWeight = new LinkedHashMap<String, Double>();
    Map<String, Double> termSWNScore = new LinkedHashMap<String, Double>();
    Set<String> tag = new HashSet<>();
    TFIDF tfidf = new TFIDF();

    public void implementation() throws IOException {
        Reading reader = new Reading();
        reader.readingDocs("F:\\Dropbox\\DS\\baby", true, docs2);
        for (String keys : docs2.keySet()) {
            tagging(keys, docs2.get(keys));
        }
        tfidf.functionOfIDF(SetOfFeatures, labeledDocMap);
        getScore();
        for (Map.Entry entry : labeledDocMap.entrySet()) {
            ranking(entry.getKey().toString(), entry.getValue().toString(), 3);
        }
        for (Map.Entry entry : labeledDocMap.entrySet()) {
            inetialWieght(entry.getKey().toString(), entry.getValue().toString());
        }
        accuracy();
        /// applying the rules
        // FIRST:
        /*
               
         lastSentence(labeledDocMap, 1);
         for (Map.Entry entry : labeledDocMap.entrySet()) {
         inetialWieght(entry.getKey().toString(), entry.getValue().toString());
         }
         accuracy();
         */
        /// SECOND:
        // negaton(labeledDocMap);
        for (Map.Entry entry : labeledDocMap.entrySet()) {
            //   inetialWieght(entry.getKey().toString(), entry.getValue().toString());
        }
        //accuracy();
    }

    class TFIDF {

        LinkedHashMap<String, Double> adjectivesAndAdverbsWithIDF = new LinkedHashMap<>();

        public Map<String, Double> functionOfIDF(Set<String> setOfFeatures, Map<String, String> docs) {
            double i = 0;
            for (String s : setOfFeatures) {
                for (String k : docs.keySet()) {
                    if (StringUtils.contains(docs.get(k), s) == true) {
                        i++;
                    }
                }
                adjectivesAndAdverbsWithIDF.put(s, Math.log10(docs.size() / i));
                i = 0;
            }
            return adjectivesAndAdverbsWithIDF;
        }

        public double termNormalization(int tremFrequency, int docLength) {
            double normalization = ((double) tremFrequency / (double) docLength);
            return normalization;
        }

        public double termLogarithmFrequency(int tf) {
            double wieght = 0.0;
            if (tf > 0) {
                wieght = 1 + Math.log10(tf);
            }
            return wieght;
        }
    }

    private void sbd(Map<String, String> doc) {
        StringBuilder labeledDoc = new StringBuilder();
        labeledDoc.append("");
        Pattern word = Pattern.compile("[\\w]+|.|\\S|EOS");
        for (String key : doc.keySet()) {
            System.out.println(doc.get(key));
            Matcher matchedword = word.matcher(doc.get(key));
            while (matchedword.find()) {
                if (matchedword.group().equals("EOS")) {
                    System.out.println(labeledDoc.toString());
                    labeledDoc.delete(0, labeledDoc.length());
                } else {
                    labeledDoc.append(matchedword.group());

                }

            }
        }
    }

    private void tagging(String label, String docs) {
        StringBuilder labeledDoc = new StringBuilder();
        Pattern word = Pattern.compile("[\\w]+|.|\\S+");
        Matcher matchedword = word.matcher(docs);
        while (matchedword.find()) {
            List<HasWord> sent = Sentence.toWordList(matchedword.group());
            List<TaggedWord> taggedSent = tagger.tagSentence(sent);
            for (TaggedWord tw : taggedSent) {
                if (tw.tag().startsWith("JJ")) {
                    tag.add(tw.tag());
                    labeledDoc.append(tw.word() + "#a");
                    SetOfFeatures.add(tw.word() + "#a");
                } else if (tw.tag().startsWith("RB")) {
                    labeledDoc.append(tw.word() + "#r");
                    SetOfFeatures.add(tw.word() + "#r");
                    tag.add(tw.tag());
                } else {
                    labeledDoc.append(tw.word());

                }
            }
        }
        labeledDocMap.put(label, labeledDoc.toString());
        labeledDoc.delete(0, labeledDoc.length());

    }

    private void getScore() throws IOException {
        BufferedReader readerSWNscore = null;
        StringBuilder allTheSWNterms = new StringBuilder();
        String lineSWNscore;
        List<String> featuresList = new LinkedList<String>();
        try {
            for (String term : SetOfFeatures) {
                featuresList.add(term);
            }
            readerSWNscore = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\a_AND_a\\TwoDim\\Noun_zero_Pos-Neg.txt"));
            List<String> swnScores = new LinkedList<>();
            while ((lineSWNscore = readerSWNscore.readLine()) != null) {
                if (!lineSWNscore.trim().startsWith("#")) {
                    swnScores.add(lineSWNscore);
                    allTheSWNterms.append(lineSWNscore + "\t");
                }
            }
            for (String term : featuresList) {
                for (String swnScore : swnScores) {
                    String[] data = swnScore.split("\t");
                    if (term.equalsIgnoreCase(data[0])) {
                        termSWNScore.put(term, Double.parseDouble(data[1]));
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (readerSWNscore != null) {
                readerSWNscore.close();
            }else {
                System.out.println("getScore");
            }
        }
    }
//        Pattern word = Pattern.compile("((\\w)+#(a|r))+");

    private String roundmy(double value) {
        StringBuilder fl = new StringBuilder();
        if (value == 0) {
            fl.append("0.000000000");
        } else {
            fl.append("00000000000");
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(7, RoundingMode.HALF_UP);
            fl.replace(0, bd.toString().length(), bd.toString());
        }
        return fl.toString();
    }

    public double round2(double value) {
        long factor = (long) Math.pow(10, 5);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public double round(double value) {
        double db = 0.0;
        db = Math.round(value * 100) / 100.0;
        return db;
    }
    /*
     public double round(double value) {
     BigDecimal bd = new BigDecimal(value);
     bd = bd.setScale(2, RoundingMode.HALF_UP);
     return bd.doubleValue();
     }
     */

    private int present(double value) {
        int val = 0;
        if (value < 0) {
            val = -1;

        } else {

            val = 1;
        }
        return val;
    }

    private void ranking(String label, String docs, int type) {
        // 0 means 0 or -1 or +1 values only. 1 means score from SWN. 2 means adding wieght 
        StringBuilder labeledDoc = new StringBuilder();
        // System.out.println(label + " : " + docs);
        Pattern word = Pattern.compile("((\\w)+#(a|r))+|[\\w]+|.|\\S+");
        Matcher matchedword = word.matcher(docs);
        int noOfAllTerm = 0;
        int numberOfSpecificTermInDoc = 0;
        while (matchedword.find()) {
            if (matchedword.group().contains("#a")) {
                noOfAllTerm++;
            } else if (matchedword.group().contains("#r")) {
                noOfAllTerm++;
            }

            if (type == 0) {
                if (matchedword.group().contains("#a")) {
                    numberOfSpecificTermInDoc = StringUtils.countMatches(docs, matchedword.group());//For TF
                    double tfidfx = tfidf.termNormalization(numberOfSpecificTermInDoc, noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    double widf = tfidf.termLogarithmFrequency(noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    if (termSWNScore.get(matchedword.group()) == null) {
                        //   labeledDoc.append(matchedword.group() + "#" + "0.000000000");
                        labeledDoc.append(matchedword.group() + "#" + roundmy(widf));
                    } else {
                        labeledDoc.append(matchedword.group() + "#" + roundmy(present(termSWNScore.get(matchedword.group())) * widf));
                    }
                } else if (matchedword.group().contains("#r")) {
                    numberOfSpecificTermInDoc = StringUtils.countMatches(docs, matchedword.group());//For TF
                    double tfidfx = tfidf.termNormalization(numberOfSpecificTermInDoc, noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    double widf = tfidf.termLogarithmFrequency(noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    if (termSWNScore.get(matchedword.group()) == null) {
                        //  labeledDoc.append(matchedword.group() + "#" + "0.000000000");
                        labeledDoc.append(matchedword.group() + "#" + roundmy(widf));
                    } else {
                        labeledDoc.append(matchedword.group() + "#" + roundmy(present(termSWNScore.get(matchedword.group())) * widf));
                    }
                } else {
                    labeledDoc.append(matchedword.group());
                }
            } else if (type == 1) {
                if (matchedword.group().contains("#a")) {
                    numberOfSpecificTermInDoc = StringUtils.countMatches(docs, matchedword.group());//For TF
                    double tfidfx = tfidf.termNormalization(numberOfSpecificTermInDoc, noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    double widf = tfidf.termLogarithmFrequency(noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    if (termSWNScore.get(matchedword.group()) == null) {
                        labeledDoc.append(matchedword.group() + "#" + "0.000000000");
                    } else {
                        labeledDoc.append(matchedword.group() + "#" + roundmy(termSWNScore.get(matchedword.group()) * widf));
                    }
                } else if (matchedword.group().contains("#r")) {
                    numberOfSpecificTermInDoc = StringUtils.countMatches(docs, matchedword.group());//For TF
                    double tfidfx = tfidf.termNormalization(numberOfSpecificTermInDoc, noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    double widf = tfidf.termLogarithmFrequency(noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());

                    if (termSWNScore.get(matchedword.group()) == null) {
                        labeledDoc.append(matchedword.group() + "#" + "0.000000000");
                    } else {
                        labeledDoc.append(matchedword.group() + "#" + roundmy(termSWNScore.get(matchedword.group()) * widf));
                    }
                } else {
                    labeledDoc.append(matchedword.group());
                }
            } else if (type == 2) {
                if (matchedword.group().contains("#a")) {
                    numberOfSpecificTermInDoc = StringUtils.countMatches(docs, matchedword.group());//For TF
                    double tfidfx = tfidf.termNormalization(numberOfSpecificTermInDoc, noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    double widf = tfidf.termLogarithmFrequency(noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    if (tfidfx == 0.0) {
                        labeledDoc.append(matchedword.group() + "#" + "0.000000000");
                    } else {
                        labeledDoc.append(matchedword.group() + "#" + roundmy(tfidfx));
                    }
                } else if (matchedword.group().contains("#r")) {
                    numberOfSpecificTermInDoc = StringUtils.countMatches(docs, matchedword.group());//For TF
                    double tfidfx = tfidf.termNormalization(numberOfSpecificTermInDoc, noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    double widf = tfidf.termLogarithmFrequency(noOfAllTerm) * tfidf.adjectivesAndAdverbsWithIDF.get(matchedword.group());
                    if (tfidfx == 0.0) {
                        labeledDoc.append(matchedword.group() + "#" + "0.000000000");
                    } else {
                        labeledDoc.append(matchedword.group() + "#" + roundmy(tfidfx));
                    }
                } else {
                    labeledDoc.append(matchedword.group());
                }
            } else if (type == 3) {
                if (matchedword.group().contains("#a")) {
                    if (termSWNScore.get(matchedword.group()) == null) {
                        labeledDoc.append(matchedword.group() + "#" + "0.000000000");
                    } else {
                        labeledDoc.append(matchedword.group() + "#" + roundmy(termSWNScore.get(matchedword.group())));
                    }
                } else if (matchedword.group().contains("#r")) {

                    if (termSWNScore.get(matchedword.group()) == null) {
                        labeledDoc.append(matchedword.group() + "#" + "0.000000000");
                    } else {
                        labeledDoc.append(matchedword.group() + "#" + roundmy(termSWNScore.get(matchedword.group())));
                    }
                } else {
                    labeledDoc.append(matchedword.group());
                }
            }
        }
        // System.out.println(label + " : " + labeledDoc);
        labeledDocMap.put(label, labeledDoc.toString());
        labeledDoc.delete(0, labeledDoc.length());
    }

    private void inetialWieght(String label, String docs) {
        StringBuilder labeledDoc = new StringBuilder();
        Pattern word = Pattern.compile("((\\w)+#(a|r)#-?(\\d)+\\.(\\d)+)+");
        Matcher matchedword = word.matcher(docs);
        double weight = 0.0;
        while (matchedword.find()) {
            String[] value = matchedword.group().split("#");
            weight = weight + (Double.parseDouble(value[2]) / 100);
        }
        //   System.out.println(label + " : " + weight);
        docsWeight.put(label, weight);
        // labeledDoc.delete(0, labeledDoc.length());
    }

    public void accuracy() {
        List<String> tp = new ArrayList<String>();
        List<String> fp = new ArrayList<String>();
        List<String> fn = new ArrayList<String>();
        List<String> tn = new ArrayList<String>();

        for (Map.Entry entry : docsWeight.entrySet()) {
            String[] keyVal = entry.getKey().toString().split("-");
            double value = Double.parseDouble(entry.getValue().toString());
            if ((keyVal[1].equals("pos")) && value >= 0.0) {
                tp.add(entry.getKey().toString());
            } else if ((keyVal[1].equals("neg")) && (value >= 0.0)) {
                fp.add(entry.getKey().toString());
            } else if ((keyVal[1].equals("pos")) && (value < 0.0)) {
                fn.add(entry.getKey().toString());
            } else if ((keyVal[1].equals("neg")) && (value < 0.0)) {
                tn.add(entry.getKey().toString());
            }
        }
        double tpSize = tp.size();
        double fpSize = fp.size();
        double fnSize = fn.size();
        double tnSize = tn.size();
        double accuracy = 0.0;
        double precision = 0.0;
        double recall = 0.0;
        double f_measure = 0.0;
        accuracy = (tpSize + tnSize) / (tpSize + fpSize + fnSize + tnSize);
        precision = tpSize / (tpSize + fpSize);
        recall = tpSize / (tpSize + fnSize);
        f_measure = (2 * precision * recall) / (precision + recall);
        System.out.println("accuracy: " + round(accuracy * 100) + "%" + ", precision: " + round(precision * 100) + ", recall: " + round(recall * 100) + ", f_measure: " + round(f_measure * 100));
        tp.removeAll(tp);
        fp.removeAll(fp);
        fn.removeAll(fn);
        tn.removeAll(tn);
    }

    private void lastSentence(Map<String, String> docs, int sentLocation) {
        StringBuffer newSent = new StringBuffer();
        Pattern word = Pattern.compile("((\\w)+#(a|r)#-?(\\d)+\\.(\\d)+)+");
        for (String key : docs.keySet()) {
            docTOlist(docs.get(key));
            if (sentDocTemprary.size() == 1) {
                sentLocation = 1;
            }
            newSent.append(sentDocTemprary.get(sentDocTemprary.size() - sentLocation));
            Matcher matchedword = word.matcher(newSent.toString());
            double val = 0.0;
            //     System.out.println(newSent);
            while (matchedword.find()) {
                newSent.delete(matchedword.start(), matchedword.end());
                String[] value = matchedword.group().split("#");
                val = (Double.parseDouble(value[2]) * 3.5);
                String s = value[0] + "#" + value[1] + "#" + roundmy(val);
                newSent.insert(matchedword.start(), s);
            }
            //   System.out.println(newSent);
            sentDocTemprary.set(sentDocTemprary.size() - sentLocation, newSent.toString());
            newSent.delete(0, newSent.length() - 1);
            listTOdoc(key, sentDocTemprary);
            sentDocTemprary.removeAll(sentDocTemprary);
        }
    }
    List<String> negation = new ArrayList<>();

    private boolean negList(String s) {
        boolean itisNegation = false;
        for (String negWord : negation) {
            if (s.equalsIgnoreCase(negWord)) {
                itisNegation = true;
            }
        }
        return itisNegation;
    }
    List<String> sentDocTemprary = new LinkedList<>();

    private void docTOlist(String doc) {
        StringBuilder labeledDoc = new StringBuilder();
        labeledDoc.append("");
        Pattern word = Pattern.compile("[\\w]+|.|\\S|EOS");
        Matcher matchedword = word.matcher(doc);
        while (matchedword.find()) {
            if (matchedword.group().equals("EOS")) {
                sentDocTemprary.add(labeledDoc.toString());
                labeledDoc.delete(0, labeledDoc.length());
            } else {
                labeledDoc.append(matchedword.group());
            }
        }
    }

    private void listTOdoc(String label, List<String> doc) {
        StringBuilder document = new StringBuilder();
        for (String sent : doc) {
            document.append(sent + "EOS ");
        }
        labeledDocMap.put(label, document.toString());
        document.delete(0, document.length());
    }

    private StringBuilder snetanceSiplter(String negWord, String sent, int wordAround) {
        StringBuilder s = new StringBuilder();
        String[] siplitedSent = sent.split(" ");
        int negPostion = 0;

        for (int i = 0; i < siplitedSent.length; i++) {
            if (siplitedSent[i].equals(negWord + ",")) {
                negPostion = i;
                //System.out.println(siplitedSent[i]+" equl "+negWord+" poas:: "+ i);
            }
        }
        // System.out.println("sentence leanth: "+siplitedSent.length );    
        double val = 0.0;
        int t = 0, p = 0;
        if (negPostion + wordAround < siplitedSent.length) {
            for (int j = negPostion; j < negPostion + wordAround; j++) {
                if (siplitedSent[j].matches("((\\w)+#(a)#-?(\\d)+\\.(\\d)+,)")) {
                    StringBuilder term = new StringBuilder();
                    term.append(siplitedSent[j], 0, siplitedSent[j].length() - 2);
                    siplitedSent[j] = term.toString();
                    String[] value = siplitedSent[j].split("#");
                    val = (Double.parseDouble(value[2]) * -1);
                    String adjestedTerm = value[0] + "#" + value[1] + "#" + roundmy(val);
                    siplitedSent[j] = adjestedTerm;
                }
            }
        } else if (negPostion + wordAround >= siplitedSent.length) {
            for (int j = negPostion; j < siplitedSent.length; j++) {
                if (siplitedSent[j].matches("((\\w)+#(a)#-?(\\d)+\\.(\\d)+,)")) {
                    StringBuilder term = new StringBuilder();
                    term.append(siplitedSent[j], 0, siplitedSent[j].length() - 2);
                    siplitedSent[j] = term.toString();
                    String[] value = siplitedSent[j].split("#");
                    val = (Double.parseDouble(value[2]) * -1);
                    String adjestedTerm = value[0] + "#" + value[1] + "#" + roundmy(val);
                    siplitedSent[j] = adjestedTerm;
                }
            }
        }

        //System.out.print("siplited sentence : ");
        for (String x : siplitedSent) {
            s.append(x + " ");
        }
        // System.out.println("adjetied sentences : "+s);
        return s;
    }

    private void negaton(Map<String, String> docs) {
        StringBuilder sentPortion = new StringBuilder();
        Set<String> sentenceCounter = new HashSet<>();
        Set<String> docCounter = new HashSet<>();
        int inOneSent = 0;
        int negTerms = 0;
        negation.add("no");
        negation.add("not");
        negation.add("never");
        negation.add("nothing");
        negation.add("nor");
        negation.add("neither");
        negation.add("n't");
        Pattern word = Pattern.compile("(((n't)+)|(\\w)+#(a|r)#-?(\\d)+\\.(\\d)+)+|(no-[\\w]+)+|[\\w]+|.|\\S+");
        // Pattern word = Pattern.compile("((n't)+)");
        // Pattern word = Pattern.compile("(not#)+|((no)[,\\S])+");
        for (String key : docs.keySet()) {

            docTOlist(docs.get(key));

            for (int i = 0; i < sentDocTemprary.size(); i++) {
                Matcher matchedword = word.matcher(sentDocTemprary.get(i));
                boolean neg = false;
                inOneSent = 0;
                while (matchedword.find()) {
                    if (negList(matchedword.group()) == true) {
                        if (!sentDocTemprary.get(i).endsWith("?")) {
                            sentenceCounter.add(sentDocTemprary.get(i));
                            docCounter.add(key);
                            negTerms++;
                            sentDocTemprary.set(i, snetanceSiplter(matchedword.group().toString(), sentDocTemprary.get(i).toString(), 10).toString());
                        }
                    }
                }
            }
            listTOdoc(key, sentDocTemprary);
            sentDocTemprary.removeAll(sentDocTemprary);
        }
        System.out.println("The no of doc which include negation is : " + docCounter.size());
        System.out.println("The no of sentences with negation is : " + sentenceCounter.size());
        System.out.println("The number of the negation term is : " + negTerms);
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Preprocessing;

/**
 *
 * @author 17511035
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

public class SentiWordNet extends OutPut {
    /*
     a_AND_a
     n_AND_v
     a_a_AND_v
     a_a__v_AND_n
     */

    String pofType = "a_AND_a";

    public void dict() throws IOException {
        Set<String> dictionary = new TreeSet<>();
        BufferedReader dic = null;
        try {
            dic = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\SpellCheckers\\dict\\english1.txt"));
            String line;
            while ((line = dic.readLine()) != null) {
                dictionary.add(line);
            }
            dic.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dic != null) {
                dic.close();
            }
        }

        System.out.println(dictionary.size());
        BufferedReader dic2 = null;
        try {
            dic2 = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\SpellCheckers\\dict\\english2.txt"));
            String line;
            while ((line = dic2.readLine()) != null) {
                dictionary.add(line);
            }
            dic2.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dic2 != null) {
                dic2.close();
            }
        }

        int i = 0;
        System.out.println(dictionary.size());
        for (String s : dictionary) {
            System.out.print(s);
            System.out.println("\n");
        }
    }

    public void sentiWordNet() throws IOException {

        //  try {
        String pathToSWN = ("SentiWordNet_3.0.0_20130122.txt");
        String wordTypeMarker;
        BufferedReader csv = null;
        PrintWriter newFileSWN = null;
        ArrayList<String> temp = new ArrayList<>();
        Map<String, String> tempTreeHash = new TreeMap<>();
        File directory = new File("OUTPUT\\SentiWordNet\\" + pofType + "\\");

        directory.mkdirs();
        try {
            newFileSWN = new PrintWriter(new FileOutputStream("OUTPUT\\SentiWordNet\\" + pofType + "\\terms_SentiWordNet.txt"));

        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file SentiWordNet.txt.");
        }
        newFileSWN.println("#these are the extracted adjectives and adverbs.");
        newFileSWN.println("#Word  Positivity    Negativity Pos-Neg ObjScore");
        try {
            csv = new BufferedReader(new FileReader(pathToSWN));
            int lineNumber = 0;
            String line;
            while ((line = csv.readLine()) != null) {
                lineNumber++;
                // If it's a comment, skip this line.
                if (!line.trim().startsWith("#")) {
                    // We use tab separation
                    String[] data = line.split("\t");

                    if (data.length != 6) {
                        throw new IllegalArgumentException(
                                "SentiWordNet file:Incorrect tabulation format in file, line: "
                                + lineNumber);
                    }
                    //a=Adjective n=Noun r=Adverb v=Verb and we select the a and r only 
                    //if ("a".equals(data[0]) || "r".equals(data[0])) {
                    //if ("n".equals(data[0]) || "v".equals(data[0])) {
                    //  if("a".equals(data[0]) || "r".equals(data[0]) || "v".equals(data[0])){
                    if ((pofType.equals("a_AND_a")) && ("a".equals(data[0]) || "r".equals(data[0]))) {
                        wordTypeMarker = data[0];

                        // Calculate synset score as score = PosS - NegS
                        Double synsetScore = Double.parseDouble(data[2])
                                - Double.parseDouble(data[3]);
                        Double ObjScore = 1 - (Double.parseDouble(data[2]) + Double.parseDouble(data[3]));

                        // Get all Synset terms
                        String[] synTermsSplit = data[4].split(" ");

                        // Go through all terms of current synset.
                        for (String synTermSplit : synTermsSplit) {
                            // Get synterm and synterm rank
                            String[] synTermAndRank = synTermSplit.split("#");
                            String synTerm = synTermAndRank[0] + "#"
                                    + wordTypeMarker;

                            int synTermRank = Integer.parseInt(synTermAndRank[1]);

                            System.out.println(synTerm + "\t" + data[2] + "\t" + data[3] + "\t" + synsetScore + "\t" + ObjScore);

                            newFileSWN.println(synTerm + "\t" + data[2] + "\t" + data[3] + "\t" + synsetScore + "\t" + ObjScore);

// newFileSWN.println(synTerm + "\t" + data[2] + "\t" + data[3] + "\t" + synsetScore);
                        }
                    } else if ((pofType.equals("a_a__v_AND_n")) && ("a".equals(data[0]) || "r".equals(data[0]) || "v".equals(data[0]) || "n".equals(data[0]))) {
                        wordTypeMarker = data[0];

                        // Calculate synset score as score = PosS - NegS
                        Double synsetScore = Double.parseDouble(data[2])
                                - Double.parseDouble(data[3]);
                        Double ObjScore = 1 - (Double.parseDouble(data[2]) + Double.parseDouble(data[3]));

                        // Get all Synset terms
                        String[] synTermsSplit = data[4].split(" ");

                        // Go through all terms of current synset.
                        for (String synTermSplit : synTermsSplit) {
                            // Get synterm and synterm rank
                            String[] synTermAndRank = synTermSplit.split("#");
                            String synTerm = synTermAndRank[0] + "#"
                                    + wordTypeMarker;

                            int synTermRank = Integer.parseInt(synTermAndRank[1]);

                            System.out.println(synTerm + "\t" + data[2] + "\t" + data[3] + "\t" + synsetScore + "\t" + ObjScore);

                            newFileSWN.println(synTerm + "\t" + data[2] + "\t" + data[3] + "\t" + synsetScore + "\t" + ObjScore);

// newFileSWN.println(synTerm + "\t" + data[2] + "\t" + data[3] + "\t" + synsetScore);
                        }

                    }
                }
            }
            newFileSWN.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (csv != null) {
                csv.close();
            }
            if (newFileSWN != null) {
                newFileSWN.close();
            }
        }

    }

    public void scoreAnalyis() throws IOException {
        Set<String> scoreNeg = new HashSet<>();
        Set<String> scorePos = new HashSet<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + pofType + "\\TwoDim\\Noun_zero_Pos-Neg.txt"));
            String line;
            //  Writer writerNounZero5 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\FiveDim\\1Noun_zero_Pos-Neg.txt");

            while ((line = reader.readLine()) != null) {
                if (!line.trim().startsWith("#")) {
                    String data[] = line.split("\t");
                    if (Double.parseDouble(data[1]) > 0.0) {
                        scorePos.add(data[1]);
                    } else {
                        scoreNeg.add(data[1]);
                    }
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

    }

    public void swnArrangeAndAverage() throws IOException {
        BufferedReader reader = null;
        Set<String> temp = new LinkedHashSet<>();
        PrintWriter newFileSWN = null;
        PrintWriter newFileSWN2 = null;

        File directory = new File("OUTPUT\\SentiWordNet\\" + pofType + "\\");

        directory.mkdirs();
        try {
            newFileSWN = new PrintWriter(new FileOutputStream("OUTPUT\\SentiWordNet\\" + pofType + "\\Arrange_SentiWordNet.txt"));
            newFileSWN2 = new PrintWriter(new FileOutputStream("OUTPUT\\SentiWordNet\\" + pofType + "\\Avarge_of_Pos_Neg_Pos-Neg_ObjScore.txt"));

        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file SentiWordNet.txt. or Error opening the file Term with its avarge of Pos-Neg");
        }
        newFileSWN.println("#these are the alphabetically arranged words(adjectives and adverbs).");
        newFileSWN.println("#Word  Positivity    Negativity Pos-Neg ObjScore.");
        newFileSWN2.println("#Unique word with its average of Pos-Neg.");
        try {
            reader = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + pofType + "\\terms_SentiWordNet.txt"));

            int lineNumber = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().startsWith("#")) {
                    String[] data = line.split("\t");
                    if (data.length != 5) {
                        throw new IllegalArgumentException(
                                "SentiWordNet file:Incorrect tabulation format in file, line: "
                                + lineNumber);
                    }
                    temp.add(data[0]);
                }
            }
            newFileSWN.println("#The number of all terms is : " + lineNumber);
            newFileSWN2.println("#The number of unique terms is : " + temp.size());
            for (String theWord : temp) {
                reader = null;
                int wordsFrquency = 0;
                double PsoNegSumation = 0.0;
                double NegSumation = 0.0;
                double PosSumation = 0.0;
                double ObjScoreSumation = 0.0;
                reader = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + pofType + "\\terms_SentiWordNet.txt"));

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (!line.trim().startsWith("#")) {
                        String[] data = line.split("\t");
                        if (theWord.equals(data[0])) {
                            wordsFrquency++;
                            newFileSWN.println(data[0] + "\t" + data[1] + "\t" + data[2] + "\t" + data[3] + "\t" + data[4]);
                            PsoNegSumation = PsoNegSumation + 1 / Double.parseDouble(data[3]);
                            NegSumation = NegSumation + 1 / Double.parseDouble(data[2]);
                            PosSumation = PosSumation + 1 / Double.parseDouble(data[1]);
                            ObjScoreSumation = ObjScoreSumation + 1 / Double.parseDouble(data[4]);
                        }

                    }
                }
                newFileSWN2.println(theWord + "\t" + wordsFrquency / PosSumation + "\t" + wordsFrquency / NegSumation + "\t" + wordsFrquency / PsoNegSumation + "\t" + wordsFrquency / ObjScoreSumation);
            }

            newFileSWN.close();
            newFileSWN2.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (newFileSWN != null) {
                newFileSWN.close();
            }
            if (newFileSWN2 != null) {
                newFileSWN2.close();
            }
        }

    }

    public void alphabeticalDivision(String dividedFileName, String dirctoryOfNewFiles) throws IOException {

        File directory = new File("OUTPUT\\SentiWordNet\\" + pofType + "\\" + dirctoryOfNewFiles);

        String line;

        directory.mkdirs();
        BufferedReader reader = null;
        char[] alphabet = {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', 'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm'};
        try {
            reader = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + pofType + "\\" + dirctoryOfNewFiles));

            while ((line = reader.readLine()) != null) {
                if (!line.trim().startsWith("#")) {

                }

            }

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public void scorePartitioning() throws IOException {
        BufferedReader reader = null;
        String line;
        int lineNumber = 0;

        File directory = new File("OUTPUT\\SentiWordNet\\" + pofType + "\\TwoDim");
        File directory2 = new File("OUTPUT\\SentiWordNet\\" + pofType + "\\FiveDim");

        directory.mkdirs();
        directory2.mkdirs();

        Writer writerAll2 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\TwoDim\\All_Pos-Neg.txt");
        Writer writerNounZero2 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\TwoDim\\Noun_zero_Pos-Neg.txt");
        Writer writerZero2 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\TwoDim\\Zero_Pos-Neg.txt");
        Writer writerPositive2 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\TwoDim\\POS_Pos-Neg.txt");
        Writer writerNagatrive2 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\TwoDim\\NEG_Pos-Neg.txt");
        Writer writerNagatrive2Obj = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\TwoDim\\NEG_Pos-Neg_Obj.txt");
        Writer writerPositive2Obj = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\TwoDim\\POS_Pos-Neg_Obj.txt");
        writerNounZero2.writeln("#Noun zero avarge value of Pos-Neg of Unique Term");
        writerZero2.writeln("# Zero avarge value of Pos-Neg of Unique Term");

        Writer writerNounZero5 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\FiveDim\\Noun_zero_Pos-Neg.txt");
        Writer writerZero5 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\FiveDim\\Zero_Pos-Neg.txt");
        Writer writerPositive5 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\FiveDim\\POS_Pos-Neg.txt");
        Writer writerNagatrive5 = new Writer("OUTPUT\\SentiWordNet\\" + pofType + "\\FiveDim\\NEG_Pos-Neg.txt");

        try {
            int termCountNounZeros = 0, tremCount0s = 0, posCount = 0, negCount = 0, allCount = 0;

            reader = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + pofType + "\\Avarge_of_Pos_Neg_Pos-Neg_ObjScore.txt"));

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (!line.trim().startsWith("#")) {
                    String[] data = line.split("\t");
                    if (data.length != 5) {
                        throw new IllegalArgumentException(
                                "SentiWordNet file:Incorrect tabulation format in file, line: "
                                + lineNumber);

                    }
                    writerAll2.writeln(data[0] + "\t" + data[3]);

                    if (Double.parseDouble(data[3]) > 0.0) {
                        writerPositive2.writeln(data[0] + "\t" + data[3]);
                        writerPositive2Obj.writeln(data[0] + "\t" + data[3] + "\t" + data[4]);
                        writerPositive5.writeln(data[0] + "\t" + data[1] + "\t" + data[2] + "\t" + data[3] + "\t" + data[4]);
                        posCount++;
                    }
                    if (Double.parseDouble(data[3]) < 0.0) {
                        writerNagatrive2.writeln(data[0] + "\t" + data[3]);
                        writerNagatrive2Obj.writeln(data[0] + "\t" + data[3] + "\t" + data[4]);
                        writerNagatrive5.writeln(data[0] + "\t" + data[1] + "\t" + data[2] + "\t" + data[3] + "\t" + data[4]);
                        negCount++;
                    }
                    if (Double.parseDouble(data[3]) != 0.0) {
                        writerNounZero2.writeln(data[0] + "\t" + data[3]);
                        writerNounZero5.writeln(data[0] + "\t" + data[1] + "\t" + data[2] + "\t" + data[3] + "\t" + data[4]);
                        termCountNounZeros++;
                    } else {
                        writerZero2.writeln(data[0] + "\t" + data[3]);
                        writerZero5.writeln(data[0] + "\t" + data[1] + "\t" + data[2] + "\t" + data[3] + "\t" + data[4]);
                        tremCount0s++;
                    }
                    allCount++;
                }

            }
            writerAll2.writeln("#the term number is : " + allCount);
            writerNounZero2.writeln("#the term number is : " + termCountNounZeros);
            writerZero2.writeln("#the term number is : " + tremCount0s);
            writerNagatrive2.writeln("#the term number is : " + negCount);
            writerPositive2.writeln("#the term number is : " + posCount);
            writerPositive2Obj.writeln("#the term number is : " + posCount);
            writerNagatrive2Obj.writeln("#the term number is : " + posCount);
            writerAll2.close();
            writerNagatrive2.close();
            writerPositive2.close();
            writerNounZero2.close();
            writerZero2.close();
            writerPositive2Obj.close();
            writerNagatrive2Obj.close();

            writerNounZero5.writeln("#the term number is : " + termCountNounZeros);
            writerZero5.writeln("#the term number is : " + tremCount0s);
            writerNagatrive5.writeln("#the term number is : " + negCount);
            writerPositive5.writeln("#the term number is : " + posCount);

            writerNagatrive5.close();
            writerPositive5.close();
            writerNounZero5.close();
            writerZero5.close();

        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    public void seadGaneraterFromSWN() throws IOException {
        BufferedReader termReader = null;
        BufferedReader reader = null;
        Writer writerPos = new Writer("F:\\Dropbox\\Coding\\OUTPUT\\SentiWordNet\\" + pofType + "\\SeadPos.txt");
        Writer writerNeg = new Writer("F:\\Dropbox\\Coding\\OUTPUT\\SentiWordNet\\" + pofType + "\\SeadNeg.txt");

        int counterPos = 0, counterNeg = 0;

        StringBuilder docJJandRB = new StringBuilder();

        try {

            termReader = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + pofType + "\\Avarge_of_Pos_Neg_Pos-Neg_ObjScore.txt"));
            reader = new BufferedReader(new FileReader("OUTPUT\\SentiWordNet\\" + pofType + "\\Arrange_SentiWordNet.txt"));

            String line, allTermLine;
            while ((allTermLine = reader.readLine()) != null) {
                if (!allTermLine.trim().startsWith("#")) {
                    String[] dataAllTerm = allTermLine.split("\t");
                    // allTheTerm.add(dataAllTerm[0]);
                    docJJandRB = docJJandRB.append(dataAllTerm[0]);
                    docJJandRB = docJJandRB.append(" ");
                }
            }

            while ((line = termReader.readLine()) != null) {
                int termPoplarty = 0;
                if (!line.trim().startsWith("#")) {
                    String[] data = line.split("\t");
                    termPoplarty = StringUtils.countMatches(docJJandRB, data[0]);
                    if (termPoplarty > 3) {
                        if (Double.parseDouble(data[1]) > 0.6 && Double.parseDouble(data[2]) < 0.3) {
                            String[] dataLine = data[0].split("#");
                            writerPos.write(dataLine[0] + " ");
                            counterPos++;
                        }
                        if (Double.parseDouble(data[2]) > 0.6 && Double.parseDouble(data[1]) < 0.3) {
                            String dataLine[] = data[0].split("#");
                            writerNeg.write(dataLine[0] + " ");
                            counterNeg++;
                        }
                    }
                }
            }
            //   writerPos.writeln("# The Number of Positive words (>0.85):"+ counterPos);
            //  writerNeg.writeln("# The number of Negative Words (>0.85) : " + counterNeg);
            writerPos.close();
            writerNeg.close();
        } catch (Exception e) {
            e.getLocalizedMessage();
        } finally {
            if (termReader != null) {
                termReader.close();
                reader.close();
            }
        }
    }

}

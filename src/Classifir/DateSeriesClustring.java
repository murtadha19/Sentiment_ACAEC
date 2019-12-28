/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import static Classifir.SpellChecker.docs_Processed;
import Preprocessing.OutPut;
import Preprocessing.Processing;
import Preprocessing.Reading;
import com.cybozu.labs.langdetect.LangDetectException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author 17511035
 */
//extends OutPut
public class DateSeriesClustring {
    private String DS;

    public DateSeriesClustring(String DS) {
        this.DS = DS;
    }
    public Map<String, String> labelAnddocs = new LinkedHashMap<>();
    ChartRe chart = new ChartRe();

    public Map<LocalDate, LocalDate> Windows = new LinkedHashMap<>();
    Map<Integer, String> docTreeMap = new TreeMap<>();
    Set<String> documents = new HashSet<>();
    List<String> docG1List = new LinkedList<>();
    List<String> docG2List = new LinkedList<>();
    //RayWhite
    //String dataSet = "neg_AND_pos_AND_obj_as_pos";
    String dataSet = "RayWhite";
    public int WinNumer = 0, docNumber = 0, docNumberPos = 0, docNumberNeg = 0;
    public double avaregeDocInWin = 0.0;

    public void clear() throws IOException {
        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\ReviewSelection").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\ReviewSelection"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\WindowOutbut").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\WindowOutbut"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\Means\\POW_R").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\Means\\POW_R"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\kmeans_results\\Latex").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\kmeans_results\\Latex"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\kmeans_results\\Excel").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\kmeans_results\\Excel"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\IDFdocSize").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\IDFdocSize"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\IDF").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\IDF"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\a_AND_a\\POW_R").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\a_AND_a\\POW_R"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeries").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeries"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeries_windowNumber").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeries_windowNumber"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeriesSepratedFiles").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeriesSepratedFiles"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\FeatuersDateSeries").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\FeatuersDateSeries"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\Matrixes_Matlab").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\Matrixes_Matlab"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\Matrixes_WEKA").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\Matrixes_WEKA"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ClassifiedReviews").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ClassifiedReviews"));
        }
    }

    public void clearS() throws IOException {
        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\ReviewSelection").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\ReviewSelection"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\WindowOutbut").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\WindowOutbut"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\Means\\POW_R").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\Means\\POW_R"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\kmeans_results\\Latex").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\kmeans_results\\Latex"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\kmeans_results\\Excel").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\kmeans_results\\Excel"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\IDFdocSize").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\IDFdocSize"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\IDF").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\IDF"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\a_AND_a\\POW_R").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\a_AND_a\\POW_R"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeries").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeries"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeries_windowNumber").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeries_windowNumber"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeriesSepratedFiles").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\DateSeriesSepratedFiles"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\FeatuersDateSeries").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\FeatuersDateSeries"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\Matrixes_Matlab").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\Matrixes_Matlab"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\Matrixes_WEKA").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\Matrixes_WEKA"));
        }

        if (new File("F:\\Dropbox\\Coding\\OUTPUT\\ClassifiedReviews").exists()) {
            FileUtils.forceDelete(new File("F:\\Dropbox\\Coding\\OUTPUT\\ClassifiedReviews"));
        }
    }

    public static void readWinOut(int x) throws FileNotFoundException {
        for (int i = 1; i <= x; i++) {
            Scanner inputstream = null;
            File filePo = new File("F:\\Dropbox\\Coding\\OUTPUT\\WindowOutbut\\Window_" + i + ".txt");
            inputstream = new Scanner(filePo);
            while (inputstream.hasNextLine()) {
                System.out.println(inputstream.nextLine());
            }
            inputstream.close();
        }
    }

    public void sequential_implementation() throws IOException, LangDetectException, Exception {
        clearS();
        docDate(DS);
        File dirWrite = new File("OUTPUT\\ProcessedDoc");
        dirWrite.mkdirs();
        File dir;
        dir = new File("OUTPUT\\ProcessedDoc\\DateSeries\\");
        File[] files = dir.listFiles();
        System.out.println("The Number of windows : " + files.length);
        for (File g : files) {
            String[] path = g.getName().toString().split("\\\\");
            String stringName = path[0];
            String name = stringName.substring(0, stringName.length() - 4);
            Integer winNum = Integer.valueOf(name);
            StringBuilder line = new StringBuilder("");
            try (BufferedReader inputstream = new BufferedReader(new FileReader(g))) {
                String doc;
                while ((doc = inputstream.readLine()) != null) {
                    line.append(doc);
                    line.append("\n");
                }
            } catch (IOException e) {
                e.getMessage();
            }
            docTreeMap.put(winNum, line.toString());
        }

        for (int x = 1; x <= files.length; x++) {

            try (BufferedWriter write = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\all_Docs.txt"))) {
                write.write(docTreeMap.get(x));
            } catch (IOException e) {
                e.getMessage();
            }

            System.out.println("WINDOW NUMBER : " + x);
            windowNumber(x);
            sequential_clustringEachWindow(x);

            //   reviewSelection(1, x, "SilhouetteCoefficient");
            //    printSelectedReviews();
            //  reviewSelection(2, x, "SilhouetteCoefficient");
            //    printSelectedReviews();
            //  reviewSelection(1, x, "MeanSilhouetteCoefficient");
            //   printSelectedReviews();
            //   reviewSelection(2, x, "MeanSilhouetteCoefficient");
            //  printSelectedReviews();
            //  reviewSelection(1, x, "DistanceMean");
            //   printSelectedReviews();
            //  reviewSelection(2, x, "DistanceMean");
            //   printSelectedReviews();
            dele("OUTPUT\\ProcessedDoc\\all_Docs.txt");
        }
        avaregeDocInWin = docNumber / WinNumer;
        System.out.println("avaregeDocInWin : " + avaregeDocInWin + "  docNumber : " + docNumber + "  WinNumer : " + WinNumer + "  docNumberPos : " + docNumberPos + "  docNumberNeg : " + docNumberNeg);

        readWinOut(WinNumer);
        chart.cha(WinNumer);
    }

    public void implementation() throws IOException, LangDetectException, Exception {
        clear();
        docDate();
        File dirWrite = new File("OUTPUT\\ProcessedDoc");
        dirWrite.mkdirs();
        File dir;
        dir = new File("OUTPUT\\ProcessedDoc\\DateSeries\\");
        File[] files = dir.listFiles();
        System.out.println("The Number of windows : " + files.length);

        for (File g : files) {
            String[] path = g.getName().toString().split("\\\\");
            String stringName = path[0];
            String name = stringName.substring(0, stringName.length() - 4);
            Integer winNum = Integer.valueOf(name);
            StringBuilder line = new StringBuilder("");
            try (BufferedReader inputstream = new BufferedReader(new FileReader(g));) {
                String dc;
                while ((dc = inputstream.readLine()) != null) {
                    line.append(dc);
                    line.append("\n");
                }
            } catch (IOException e) {
                e.getMessage();
            }
            docTreeMap.put(winNum, line.toString());
        }

        for (int x = 1; x <= files.length; x++) {

            try (BufferedWriter write = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\all_Docs.txt"))) {
                write.write(docTreeMap.get(x));
            } catch (IOException e) {
                e.getMessage();
            }

            System.out.println("WINDOW NUMBER : " + x);
            windowNumber(x);
            clustringEachWindow(x);

            dele("OUTPUT\\ProcessedDoc\\all_Docs.txt");
        }
        avaregeDocInWin = docNumber / WinNumer;
        System.out.println("avaregeDocInWin : " + avaregeDocInWin + "  docNumber : " + docNumber + "  WinNumer : " + WinNumer + "  docNumberPos : " + docNumberPos + "  docNumberNeg : " + docNumberNeg);

        readWinOut(WinNumer);
    }

    public void dele(String path) {
        File all_doc = new File(path);
        try {
            if (all_doc.exists()) {
                all_doc.delete();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void windowNumber(int winNumber) {
        File dir = new File("OUTPUT\\ProcessedDoc\\DateSeries_windowNumber\\window" + winNumber + "\\");
        dir.mkdirs();
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

    public void sequential_clustringEachWindow(int winNumber) throws IOException, LangDetectException, Exception {
        String s = "a_AND_a";
        DetectCheckLang DCL = new DetectCheckLang();
        SpellChecker sc = new SpellChecker();
        IntensifiersDiminishers id = new IntensifiersDiminishers();
        Negation neg = new Negation();
        Conjunction c = new Conjunction();
        Processing processor = new Processing();
        Sequential_Ensample kmeansCrrowd = new Sequential_Ensample();

        DCL.implemant_DetectCheckLang();

        sc.spellCheck_Implementation("SpellCheckers\\dict\\english1.0");
        //sc.spellCheck_Implementation("SpellCheckers\\dict\\english1.0");
        sc.spellCheck_Implementation("SpellCheckers\\dict\\english2.0");

        id.IntensifiersDiminishers_Implementation();

        neg.negation_Implementation(s);

        c.conjunction_Implementation();

        processor.readingDocsToMap();

        processor.a_and_a_docs_SWN(s, winNumber);

        processor.bagOfWordFixDimintions("OUTPUT\\" + s + "\\POW_R\\" + winNumber + "\\", 0, s, winNumber);

        kmeansCrrowd.Sequential_ensample_Implementation(s, winNumber);
    }

    public void clustringEachWindow(int winNumber) throws IOException, LangDetectException, Exception {
        String s = "a_AND_a";
        //String s = "a_a__v_AND_n";
        DetectCheckLang DCL = new DetectCheckLang();
        SpellChecker sc = new SpellChecker();
        IntensifiersDiminishers id = new IntensifiersDiminishers();
        Negation neg = new Negation();
        Conjunction c = new Conjunction();
        Processing processor = new Processing();
        Ensample kmeansCrrowd = new Ensample();

        DCL.implemant_DetectCheckLang();

        //=   sc.spellCheck_Implementation("SpellCheckers\\dict\\english1.0");
        //= sc.spellCheck_Implementation("SpellCheckers\\dict\\english1.0");
        //=   sc.spellCheck_Implementation("SpellCheckers\\dict\\english2.0");
        //=   id.IntensifiersDiminishers_Implementation();
        //=     neg.negation_Implementation(s);
        //=    c.conjunction_Implementation();
        processor.readingDocsToMap();

        String POSModel = "F:\\Dropbox\\Coding\\stanford-postagger-2015-01-30\\models\\english-left3words-distsim.tagger";
        processor.hashDocsAsJJandRBstringAndSetOfAllJJandRB(POSModel, s);

        //=  processor.longDoc("TwoDim\\Noun_zero_Pos-Neg.txt", "Labeled_Features.txt", "", s);
        //=  processor.bagOfWord("OUTPUT\\" + s + "\\POW\\", processor.SetOfLabeledFeatures, processor.hashLabeledDocsAsadjAndAdvString, 0, s);
        processor.longDoc("TwoDim\\Noun_zero_Pos-Neg.txt", "Labeled_Features.txt", "remove", s);
        processor.bagOfWord("OUTPUT\\" + s + "\\POW_R\\", processor.SetOfLabeledFeatures, processor.hashLabeledDocsAsadjAndAdvString, 0, s);
        kmeansCrrowd.ensample_Implementation(s, winNumber);

    }

    public void docDate(String DS) throws IOException {
        LocalDate DateFirst = LocalDate.parse("2016-02-01"); // example 2012-03-01 to 2017-07-01 / //2012-03-01 2016-02-01  TO  2018-10-01
        LocalDate DateLast = LocalDate.parse("2018-10-01");

        LocalDate DateWin = DateFirst.plusMonths(2);
        int i = 0;
        while (!DateWin.isAfter(DateLast)) {
            i++;
            System.out.println("FROM " + DateFirst + " TO " + DateWin);
            readingDocsToTextFileDate(DS, DateFirst, DateWin, i);
            makedic(1, i);
            makedic(2, i);
            //  DateFirst = DateFirst.plusWeeks(2);
            DateFirst = DateFirst.plusMonths(2);
            DateWin = DateFirst.plusMonths(2);
        }
        System.out.println("The number of window : " + i);
        WinNumer = i;
    }

    public void makedic(int run, int winNumber) {

        File dir1 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\SilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "Ensemble\\MaxG1\\");
        File dir2 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\SilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "Ensemble\\MaxG2\\");

        File dir3 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\SilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "kmeans\\G1\\");
        File dir4 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\SilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "kmeans\\G2\\");

        File dir5 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\SilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "kmeans\\Max\\G1\\");
        File dir6 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\SilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "kmeans\\Max\\G2\\");

        File dir7 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\MeanSilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "Ensemble\\MaxG1\\");
        File dir8 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\MeanSilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "Ensemble\\MaxG2\\");

        File dir9 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\MeanSilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "kmeans\\G1\\");
        File dir10 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\MeanSilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "kmeans\\G2\\");

        File dir11 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\MeanSilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "kmeans\\Max\\G1\\");
        File dir12 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\MeanSilhouetteCoefficient" + run + "\\Win" + winNumber + "\\" + "kmeans\\Max\\G2\\");

        File dir13 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\DistanceMean" + run + "\\Win" + winNumber + "\\" + "Ensemble\\MaxG1\\");
        File dir14 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\DistanceMean" + run + "\\Win" + winNumber + "\\" + "Ensemble\\MaxG2\\");

        File dir15 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\DistanceMean" + run + "\\Win" + winNumber + "\\" + "kmeans\\G1\\");
        File dir16 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\DistanceMean" + run + "\\Win" + winNumber + "\\" + "kmeans\\G2\\");

        File dir17 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\DistanceMean" + run + "\\Win" + winNumber + "\\" + "kmeans\\Max\\G1\\");
        File dir18 = new File("OUTPUT\\ProcessedDoc\\ReviewSelection\\DistanceMean" + run + "\\Win" + winNumber + "\\" + "kmeans\\Max\\G2\\");

        dir1.mkdirs();
        dir2.mkdirs();
        dir3.mkdirs();
        dir4.mkdirs();
        dir5.mkdirs();
        dir6.mkdirs();
        dir7.mkdirs();
        dir8.mkdirs();
        dir9.mkdirs();
        dir10.mkdirs();
        dir11.mkdirs();
        dir12.mkdirs();
        dir13.mkdirs();
        dir14.mkdirs();
        dir15.mkdirs();
        dir16.mkdirs();
        dir17.mkdirs();
        dir18.mkdirs();

    }
    static int docCount = 0;

    public void write_Dos(String fileName, String doc) throws IOException {
        //this is to wirte only docs within a given data (being processed)
        File directory = new File("OUTPUT\\ClassifiedReviews\\");
        directory.mkdirs();
        BufferedWriter newFileSWN = null;
        try {
            newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\ClassifiedReviews\\" + fileName + ".txt"));
            newFileSWN.write(doc);
            newFileSWN.close();
        } catch (IOException e) {
            System.out.println("Error opening the file" + fileName);
        } finally {
            if (newFileSWN != null) {
                newFileSWN.close();
            }
        }

    }

    public void readingDocsToTextFileDate(String filesPath, LocalDate localDateFirst, LocalDate localDateWin, int winNumber) throws FileNotFoundException, IOException {
        StringBuilder line = new StringBuilder("");
        sequential_wirteForWeks_mkdirs(winNumber);
        int inc = 0;
        File dir;
        dir = new File(filesPath);
        File[] files = dir.listFiles();
        for (File f : files) {
            String fileName = f.getName();
            try (BufferedReader inputstream = new BufferedReader(new FileReader(f))) {
                String doc;
                while ((doc = inputstream.readLine()) != null) {
                    line.append(doc);
                }

                LocalDate docDate = LocalDate.parse(line.substring(0, 10));
                if (docDate.isAfter(localDateFirst) && docDate.isBefore(localDateWin)) {
                    docCount++;
                    String[] data = fileName.split(" ");
                    inc++;
                    sequential_wirteForWeks_write(winNumber, data[0], line.substring(11, line.length() - 1).toString());
                    labelAnddocs.put("doc" + inc + "-" + data[0] + "-" + data[1], line.substring(11, line.length() - 1).toString());
                    write_Dos(fileName, line.substring(11, line.length() - 1).toString());
                    // documents.add(line.substring(11, line.length() - 1));
                    if (data[0].equalsIgnoreCase("pos")) {
                        docNumberPos++;

                    } else if (data[0].equalsIgnoreCase("neg")) {
                        docNumberNeg++;

                    }
                }
                line.append("");
                line.delete(0, line.length());
            } catch (IOException e) {
                System.out.println("File with TXT Extention might be there, chick for"
                        + "another posible  problem");
                e.getMessage();
            }
        }
        docNumber = inc;
        File del = new File("OUTPUT\\ProcessedDoc\\DateSeries\\" + winNumber + ".txt");
        try {
            if (del.exists()) {
                del.delete();
            }
        } catch (Exception e) {
            e.getMessage();
        }
        File directory = new File("OUTPUT\\ProcessedDoc\\DateSeries\\");
        directory.mkdirs();
        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\DateSeries\\" + winNumber + ".txt"))) {

            for (String key : labelAnddocs.keySet()) {
                if (labelAnddocs.get(key).isEmpty()) {
                    System.out.println("The document : " + key + "is empty.");
                } else {
                    newFileSWN.write(key + "\t" + labelAnddocs.get(key).trim());
                    newFileSWN.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
        System.out.println("win : " + winNumber + "Rev no : " + labelAnddocs.size());
        labelAnddocs.clear();
    }

    public void sequential_wirteForWeks_mkdirs(int winNo) {
        File directory = new File("OUTPUT\\ProcessedDoc\\DateSeriesSepratedFiles\\" + winNo);
        directory.mkdirs();
    }

    public void sequential_wirteForWeks_write(int winNo, String filename, String doc) throws FileNotFoundException, IOException {
        try (BufferedWriter newFileSWN2 = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\DateSeriesSepratedFiles\\" + winNo + "\\" + filename + docCount + ".txt"))) {
            newFileSWN2.write(doc);
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

}

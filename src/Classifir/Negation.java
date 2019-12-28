/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import Preprocessing.OutPut;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 17511035
 */
public class Negation {

    List<String> antonym = new LinkedList<>();
    Map<String, LinkedList<String>> docs = new LinkedHashMap<String, LinkedList<String>> ();
    Map<String, String> docs_Processed = new LinkedHashMap<String, String>();
   // IOClass res = new IOClass();

    public void negation_Implementation(String whichPOS) throws IOException {
        System.out.print("negation_Implementation(String whichPOS), Negation is runing ..");
        read_Antonym_dictionary(whichPOS);
        readDocs();
        negation_Main();
       // res.resultWriter2("OUTPUT\\ProcessedDoc\\", "all_Docs.txt", docs_Processed);
        result();
        antonym.removeAll(antonym);
        docs.clear();
        docs_Processed.clear();
        System.out.println(" it finished");
    }
 public void result() throws IOException {

        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\all_Docs.txt"))) {

            for (String key : docs_Processed.keySet()) {
                if (docs_Processed.get(key).isEmpty()) {
                    System.out.println("The document : " + key + "is empty.");
                } else {
                    newFileSWN.write(key + "\t" + docs_Processed.get(key).trim());
                    newFileSWN.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        }

 }

    public void read_Antonym_dictionary(String whichPOS) throws IOException {
        BufferedReader allLine = null;
        try {
            allLine = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\SentiWordNet\\" + whichPOS + "\\TwoDim\\antonym_dictionary.txt"));
            String line;
            while ((line = allLine.readLine()) != null) {
                antonym.add(line);
            }
            allLine.close();
        } catch (FileNotFoundException e) {
            System.out.println("File with TXT Extention might be there, chick for"
                    + "another posible  problem");
        } finally {
            if (allLine != null) {
                allLine.close();
            }else {
                System.out.println("read_Antonym_dictionary");
            }
        }
    }

    public void readDocs() throws IOException {
                File f = new File("OUTPUT\\ProcessedDoc\\all_Docs.txt");
        if (f.exists() && f.isFile()) {
        } else {
            System.out.println("Negation. There is no file ");
            System.exit(0);
        }
        BufferedReader allDocs = null;
        try {
            allDocs = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\all_Docs.txt"));
            String doc;
            while ((doc = allDocs.readLine()) != null) {
                LinkedList<String> sentenceList = new LinkedList<>();
                if (!doc.startsWith("#")) {
                    String[] data = doc.split("\t");
                    Reader reader = new StringReader(data[1]);
                    DocumentPreprocessor dp = new DocumentPreprocessor(reader);
                    for (List<HasWord> element : dp) {
                        StringBuilder sentence = new StringBuilder();
                        List<HasWord> hasWordList = element;
                        for (HasWord token : hasWordList) {
                            sentence.append(token).append(" ");
                        }
                        sentenceList.add(sentence.toString());
                    }
                    docs.put(data[0], new LinkedList<>());
                    docs.replace(data[0], sentenceList);
                }
            }
            allDocs.close();
        } catch (FileNotFoundException e) {
            System.out.println("File with TXT Extention might be there, chick for"
                    + "another posible  problem");
        } finally {
            if (allDocs != null) {
                allDocs.close();
            }else {
                System.out.println("readDocs");
            }
        }
    }

    private void negation_Main() {
        for (String key : docs.keySet()) {
            StringBuilder sents_Proce = new StringBuilder();
            for (String sent : docs.get(key)) {
                if (negationExist(sent) == true) {
                    sents_Proce.append(negationProcessing(sent, negator_Position, comma_Position));
                } else {
                    sents_Proce.append(sent);
                }
            }
            docs_Processed.put(key, sents_Proce.toString());
        }
    }
    List<Integer> negator_Position = new ArrayList<>();
    List<Integer> comma_Position = new ArrayList<>();

    private String negationProcessing(String sent, List negator_Position, List comma_Position) {
        StringBuilder newSent = new StringBuilder();
        String token[] = sent.split("\\s");
        Map<Integer, String> word = new HashMap<>();
        for (int i = 0; i < negator_Position.size(); i++) {
            int limt = (int) negator_Position.get(i) + 5;
            if (limt <= token.length) {
                limt = limt;
            } else {
                limt = token.length;
            }
            int j = (int) negator_Position.get(i);
            while (j < limt) {
                for (String ant : antonym) {
                    String[] data = ant.split("\t");
                    if (data[0].equalsIgnoreCase(token[j])) {
                        word.put(j, data[2]);
                    }
                    if (data[2].equalsIgnoreCase(token[j])) {
                        word.put(j, data[2]);
                    }
                }
                j++;
            }
        }
        for (int i : word.keySet()) {
            token[i] = word.get(i);
        }
        for (String s : token) {
            newSent.append(s + " ");
        }
        return newSent.toString();
    }

    private boolean negationExist(String sent) {
        negator_Position.removeAll(negator_Position);
        boolean exist = false;
        List<String> negation = new ArrayList<>();
                negation.add("no");
        negation.add("n't");
        negation.add("not!");
        negation.add("amn’t");

        negation.add("aint");
 negation.add("arent");
 negation.add("cannot");
 negation.add("cant");
 negation.add("couldnt");
 negation.add("darent");
 negation.add("didnt");
 negation.add("doesnt");
 negation.add("ain't");
 negation.add("aren't");
 negation.add("can't");
 negation.add("couldn't");
 negation.add("daren't");
 negation.add("didn't");
 negation.add("doesn't");
 negation.add("dont");
 negation.add("hadnt");
 negation.add("hasnt");
 negation.add("havent");
 negation.add("isnt");
 negation.add("mightnt");
 negation.add("mustnt");
 negation.add("neither");
 negation.add("don't");
 negation.add("hadn't");
 negation.add("hasn't");
 negation.add("haven't");
 negation.add("isn't");
 negation.add("mightn't");
 negation.add("mustn't");
 negation.add("neednt");
 negation.add("needn't");
 negation.add("never");
 negation.add("none");
 negation.add("nope");
 negation.add("nor");
 negation.add("not");
 negation.add("nothing");
 negation.add("nowhere");
 negation.add("oughtnt");
 negation.add("shant");
 negation.add("shouldnt");
 negation.add("uhuh");
 negation.add("wasnt");
 negation.add("werent");
 negation.add("oughtn't");
 negation.add("shan't");
 negation.add("shouldn't");
 negation.add("uh-uh");
 negation.add("wasn't");
 negation.add("weren't");
 negation.add("without");
 negation.add("wont");
 negation.add("wouldnt");
 negation.add("won't");
 negation.add("wouldn't");
 negation.add("rarely");
 negation.add("seldom");
 negation.add("despite");
   /*
        negation.add("no");
        negation.add("not");
        negation.add("n't");
        negation.add("not!");
        negation.add("never");
        negation.add("nothing");
        negation.add("nor");
        negation.add("neither");
        negation.add("can't");
        negation.add("cannot");
        negation.add("wont");
        negation.add("won't");
        negation.add("dont");
        negation.add("don't");
        negation.add("didn’t");
        negation.add("didnt");
        negation.add("couldnt");
        negation.add("couldn’t");
        negation.add("doesn't");
        negation.add("haven't");
        negation.add("hadn't");
        negation.add("shouldn't");
        negation.add("wouldn't");
        negation.add("isn't");
        negation.add("isnt");
        negation.add("amn’t");
        negation.add("aren't");
        negation.add("wasn’t");
        negation.add("weren’t");
        negation.add("ain't");
        negation.add("aint");
*/
        String token[] = sent.split(" ");
        int token_Count = 0;
        for (String tok : token) {
            if (negation.contains(tok.toLowerCase())) {
                exist = true;
                negator_Position.add(token_Count);
            }
            token_Count++;
        }
        return exist;
    }
}

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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 17511035
 */
public class Conjunction {

    Map<String, LinkedList<String>> docs = new LinkedHashMap<>();
    Map<String, String> docs_Processed1 = new LinkedHashMap<>();
    // IOClass res = new IOClass();

    public void conjunction_Implementation() throws IOException {
        System.out.print("conjunction_Implementation(), Conjunction is runing ..");
        readDocs();
        conj_Main();
        result();

        docs.clear();
        docs_Processed1.clear();
        System.out.println(" it finished.");
    }

    public void result() throws IOException {
        System.out.println("result() IS RUNNING NOW ");

        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\all_Docs.txt"))) {
            System.out.println("docs_Processed1 first " + docs_Processed1.size());
            for (String key : docs_Processed1.keySet()) {
                if (docs_Processed1.get(key).isEmpty()) {
                    System.out.println("The document : " + key + "is empty.");
                } else {
                    newFileSWN.write(key + "\t" + docs_Processed1.get(key).trim());
                    newFileSWN.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        }

    }

    public void readDocs() throws IOException {
        File f = new File("OUTPUT\\ProcessedDoc\\all_Docs.txt");
        if (f.exists() && f.isFile()) {
        } else {
            System.out.println("Conjunction. There is no file ");
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
        } catch (IOException e) {
            System.out.println("File with TXT Extention might be there, chick for"
                    + "another posible  problem");
            e.getMessage();
        } finally {
            if (allDocs != null) {
                allDocs.close();
            } else {
                System.out.println("allDocs,readDocs,conjunction");
            }
        }
    }
    int conj_Position = 0;

    private void conj_Main() {
        for (String key : docs.keySet()) {
            StringBuilder sents_Proce = new StringBuilder();
            for (String sent : docs.get(key)) {
                conj_Position = 0;
                if (conjunctorExist(sent) == true) {
                    sents_Proce.append(conjProcessing(sent, conj_Position));
                } else {
                    sents_Proce.append(sent);
                }
                conjss_Position.removeAll(conjss_Position);
            }
            docs_Processed1.put(key, sents_Proce.toString());
        }
    }

    private String commma(String sent, int conj_Position) {
        StringBuilder newSent = new StringBuilder();
        List<Integer> comma_Pos = new LinkedList<>();
        int procePostion = 0;
        String token[] = sent.split(" ");
        for (int i = 0; i < token.length; i++) {
            if ((token[i].equals(","))) {
                comma_Pos.add(i);
            }
        }
        if (comma_Pos.size() == 0) {
            procePostion = conj_Position;
        } else if (comma_Pos.get(0) == conj_Position + 1) {
            if (comma_Pos.size() > 1) {
                procePostion = comma_Pos.get(1);
            } else {
                procePostion = comma_Pos.get(0);
            }
        } else {
            procePostion = comma_Pos.get(0);
        }

        int j = procePostion;
        for (j = j + 1; j < token.length; j++) {
            newSent.append(token[j] + " ");
        }
        return newSent.toString();
    }

    private String conjProcessing(String sent, int conj_Position) {
        StringBuilder newSent = new StringBuilder();
        String token[] = sent.split(" ");
        if ((conj_Position != 0)) {
            int j = conj_Position;
            for (j = j + 1; j < token.length; j++) {
                newSent.append(token[j] + " ");
            }
        } else if (conj_Position == 0) {
            newSent.append(commma(sent, conj_Position));
        }
        return newSent.toString();
    }
    List<Integer> conjss_Position = new LinkedList<>();

    private boolean conjunctorExist(String sent) {
        boolean exist = false;
        List<String> conjunctors = new ArrayList<>();
        conjunctors.add("but");
        conjunctors.add("however");
        conjunctors.add("yet");
        conjunctors.add("nevertheless");
        conjunctors.add("nonetheless");
        conjunctors.add("whereas");
        conjunctors.add("despite");
        conjunctors.add("while");
        conjunctors.add("although");
        conjunctors.add("notwithstanding");

        String token[] = sent.split(" ");
        int token_Count = 0;
        for (String tok : token) {
            if (conjunctors.contains(tok.toLowerCase())) {
                exist = true;
                conjss_Position.add(token_Count);
                conj_Position = token_Count;
            }
            token_Count++;
        }
        return exist;
    }
}


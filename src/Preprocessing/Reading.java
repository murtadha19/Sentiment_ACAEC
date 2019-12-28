
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Preprocessing;

/////////
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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
///////////

/**
 *
 * @author 17511035
 */
//extends OutPut
public class Reading {

    public Map<String, String> labelAnddocsMap = new LinkedHashMap<>();
    //public static Map<String, String> docs2 = new LinkedHashMap();
    Map<String, String> WrongDocs = new LinkedHashMap<>();

    public void readingDocs(String filesPath, boolean asSentences, Map<String, String> docs2) {
        System.out.println("Reading ");
        StringBuilder line = new StringBuilder("");
        int inc = 0;
        File dir;
        dir = new File(filesPath);
        File[] files = dir.listFiles();
        for (File f : files) {
            String fileName = f.getName();
            if (f.isFile() && f.getName().endsWith(".txt")) {
                DocumentPreprocessor dp = new DocumentPreprocessor(f.toString());
                for (List<HasWord> sentence : dp) {
                    line.append(sentence.toString(), 1, sentence.toString().length() - 1);
                    line.append("EOS ");
                }
                String[] data = fileName.split(" ");
                inc++;
                docs2.put("doc" + inc + "-" + data[0], line.toString());
                line.delete(0, line.length());
            }

        }
    }

    public void readingDocsToTextFile(String filesPath) throws IOException {
        StringBuilder line = new StringBuilder("");
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
                String[] data = fileName.split(" ");
                inc++;
                labelAnddocsMap.put("doc" + inc + "-" + data[0], line.toString());
                line.append("");
                line.delete(0, line.length());

            } catch (FileNotFoundException e) {
                System.out.println("File with TXT Extention might be there, chick for"
                        + "another posible  problem");
            } 
        }
        result("OUTPUT\\ProcessedDoc\\", "all_Docs.txt", labelAnddocsMap);
        result("OUTPUT\\ProcessedDoc\\all_Docs\\", "all_Docs.txt", labelAnddocsMap);
        labelAnddocsMap.clear();
    }
///

    public void result(String path, String fileName, Map<String, String> docWords) throws IOException {
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
///

    public void readWritWoungDocs(List<String> docsID, String workOn, String pos_neg) throws IOException {

        List<String> row_pro_wrongDoc = new LinkedList<>();
        BufferedReader allDocs = null;
        BufferedReader rep_Docs = null;
        try {
            allDocs = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\all_Docs.txt"));
            rep_Docs = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\" + workOn + "\\Each_Documents_as_Adj_and_Adv.txt"));
            String doc;
            String rep_Doc;
            while (((doc = allDocs.readLine()) != null) && ((rep_Doc = rep_Docs.readLine()) != null)) {
                if (!doc.startsWith("#")) {
                    String[] data = doc.split("\t");
                    if (docsID.contains(data[0])) {
                        WrongDocs.put(data[0], data[1]);
                        if (!rep_Doc.startsWith("#")) {
                            row_pro_wrongDoc.add(doc);
                            row_pro_wrongDoc.add(rep_Doc);
                        }
                    }

                }
            }

        } catch (IOException e) {
            System.out.println("File with TXT Extention might be there, chick for"
                    + "another posible  problem");
        } finally {
            if (allDocs != null) {
                allDocs.close();
            }
            if (rep_Docs != null) {
                rep_Docs.close();
            }
        }

        System.out.println(" ,,,, " + WrongDocs.size());
        if (WrongDocs.isEmpty()) {
        } else {
            //  resultWriter2("OUTPUT\\ProcessedDoc\\WrongClassifiedDoc\\Files\\" + pos_neg + "\\", WrongDocs);
            result("OUTPUT\\ProcessedDoc\\WrongClassifiedDoc\\", "wrong_doc" + pos_neg + ".txt", WrongDocs);
            //   resultWriter2("OUTPUT\\ProcessedDoc\\WrongClassifiedDoc\\", "Raw_Proc_wrong_doc" + pos_neg + ".txt", row_pro_wrongDoc);
            row_pro_wrongDoc.removeAll(row_pro_wrongDoc);
        }
        WrongDocs.clear();
    }

    public Map<String, String> readingDocsToMap(Map<String, String> docs) throws IOException {
        docs.clear();
        try ( BufferedReader allDocs = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\all_Docs.txt"));){
            String doc;
            while ((doc = allDocs.readLine()) != null) {
                if (!doc.startsWith("#")) {
                    String[] data = doc.split("\t");
                    if (data.length == 2) {
                        docs.put(data[0], data[1]);
                    } else {
                        System.out.println("In C = Reading, M=readingDocsToMap. The document structure is not right !!!");
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("File with TXT Extention might be there, chick for"
                    + "another posible  problem");
        } 
        return docs;
    }

}

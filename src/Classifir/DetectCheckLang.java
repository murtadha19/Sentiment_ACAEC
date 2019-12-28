/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import Preprocessing.OutPut;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author 17511035
 */
public class DetectCheckLang {

    public static Map<String, String> docs_Processed = new LinkedHashMap<String, String>();
    public Map<String, String> docc = new LinkedHashMap<String, String>();

    //IOClass res = new IOClass();
    public void implemant_DetectCheckLang() throws IOException, LangDetectException {
        System.out.print("implemant_DetectCheckLang(), DetectCheckLang is runing ..");
        read_detectLang();
        dublictedValue();
        dots_Proce();
        addingWhiteSpace_Proce();
     //   res.resultWriter2("OUTPUT\\ProcessedDoc\\", "all_Docs.txt", docs_Processed);
        result();
        docs_Processed.clear();
        docc.clear();
        DetectorFactory.clear();
        System.out.println(" it finished.");
    }

    public void result() throws IOException {

        try (BufferedWriter newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\ProcessedDoc\\all_Docs.txt"))) {

            for (String key : docs_Processed.keySet()) {
                if (docs_Processed.get(key).isEmpty()) {
                    System.out.println("The document : " + key + "is empty.");
                } else {
                    newFileSWN.write(key + "\t" + docs_Processed.get(key).trim());
                   // System.out.println(docs_Processed.get(key).trim());
                    newFileSWN.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    public void init(String profileDirectory) throws LangDetectException {
        DetectorFactory.loadProfile(profileDirectory);
    }

    public void read_detectLang() throws IOException, LangDetectException {
          File f = new File("OUTPUT\\ProcessedDoc\\all_Docs.txt");
        if (f.exists() && f.isFile()) {
        } else {
            System.out.println("DetectCheckLang. There is no file ");
            System.exit(0);
        }
        init("F:\\Dropbox\\Coding\\DetectLang\\profiles");
        BufferedReader allDocs = null;
        try {
            allDocs = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\all_Docs.txt"));
            String doc;
            while ((doc = allDocs.readLine()) != null) {
               // System.out.println(doc);
                Detector detector = DetectorFactory.create();
                if (!doc.startsWith("#")) {
                    String[] data = doc.split("\t");
                    detector.append(data[1]);
                    if (detector.detect().equals("en")) {
                        docc.put(data[0], data[1]);
                        docs_Processed.put(data[0], data[1]);
                    } else {
                        System.out.println("NNNNNOOOOOOOOOTTTTT" + detector.detect());
                    }
                }
            }
            allDocs.close();
        } catch (FileNotFoundException e) {
            System.out.println("File with TXT Extention might be there, chick for"
                    + "another posible  problem");
        } finally {
            if (allDocs != null) {
                allDocs.close();
            } else {
                System.out.println("read_detectLang");
            }
        }
    }

    private void dublictedValue() {
        List<String> valList = new ArrayList<>();
        docs_Processed.clear();
        int x = 0;
        for (String key : docc.keySet()) {
            if (!valList.isEmpty()) {
                if (!valList.contains(docc.get(key))) {
                    docs_Processed.put(key, docc.get(key));
                } else {
                    System.out.println("DUPLICATED " + key + " " + docc.get(key));
                    x++;
                }
            } else if (valList.isEmpty()) {
                docs_Processed.put(key, docc.get(key));
            }
            valList.add(docc.get(key));
        }

    }

    public void dots_Proce() {
        for (String doc : docs_Processed.keySet()) {
            docs_Processed.replace(doc, dots(docs_Processed.get(doc)));
        }
    }

    public void addingWhiteSpace_Proce() {
        for (String doc : docs_Processed.keySet()) {
            docs_Processed.replace(doc, addingWhiteSpace(docs_Processed.get(doc)));
            docs_Processed.replace(doc, addingWhiteSpaceBefore(docs_Processed.get(doc)));
        }
    }

    public String dots(String docs) {
        StringBuilder newDoc = new StringBuilder();
        int nextPos = 0;
        for (int i = 0; i < docs.length(); i++) {
            if (docs.charAt(i) == '.') {
                if (i != docs.length() - 1) {
                    nextPos = i + 1;
                    if (Character.isLetter(docs.charAt(nextPos))) {
                        if ((i <= docs.length() - 3)) {
                            if ((Character.isUpperCase(docs.charAt(nextPos))) && (docs.charAt(nextPos + 1) == '.')) {
                                newDoc.append(docs.charAt(i));
                            } else {
                                newDoc.append(docs.charAt(i)).append(" ");
                            }
                        } else {
                            newDoc.append(docs.charAt(i)).append(" ");
                        }
                    } else {
                        newDoc.append(docs.charAt(i));
                    }
                } else {
                    newDoc.append(docs.charAt(i));
                }
            } else {
                newDoc.append(docs.charAt(i));
            }
        }
        return newDoc.toString();
    }

    public String addingWhiteSpace(String docs) {
        StringBuilder newDoc = new StringBuilder();
        int nextPos = 0;
        for (int i = 0; i < docs.length(); i++) {
            if ((docs.charAt(i) == ';') || (docs.charAt(i) == '?') || (docs.charAt(i) == '(') || (docs.charAt(i) == ')') || (docs.charAt(i) == ',') || (docs.charAt(i) == ':') || (docs.charAt(i) == '$')) {
                if (i != docs.length() - 1) {
                    nextPos = i + 1;
                    if (Character.isLetter(docs.charAt(nextPos))) {
                        newDoc.append(docs.charAt(i)).append(" ");
                    } else {
                        newDoc.append(docs.charAt(i));
                    }
                } else {
                    newDoc.append(docs.charAt(i));
                }
            } else {
                newDoc.append(docs.charAt(i));
            }
        }
        return newDoc.toString();
    }

    public String addingWhiteSpaceBefore(String docs) {
        StringBuilder newDoc = new StringBuilder();
        int prePos = 0;
        for (int i = 0; i < docs.length(); i++) {
            if (i != 0) {
                prePos = i - 1;
                if ((docs.charAt(i) == ';') || (docs.charAt(i) == '?') || (docs.charAt(i) == '(') || (docs.charAt(i) == ')') || (docs.charAt(i) == ',') || (docs.charAt(i) == ':' || (docs.charAt(i) == '$'))) {
                    if ((Character.isLetter(docs.charAt(prePos)))) {
                        newDoc.append(" ").append(docs.charAt(i));
                    } else {
                        newDoc.append(docs.charAt(i));
                    }
                } else {
                    newDoc.append(docs.charAt(i));
                }
            } else {
                newDoc.append(docs.charAt(i));
            }
        }
        return newDoc.toString();
    }
}


/*
 public ArrayList<Language> detectLangs(String text) throws LangDetectException {
 Detector detector = DetectorFactory.create();
 detector.append(text);
 return detector.getProbabilities();
 }
 */

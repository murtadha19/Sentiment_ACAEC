
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import static Classifir.DetectCheckLang.docs_Processed;
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
public class IntensifiersDiminishers {

    List<String> synonym = new LinkedList<>();
    Map<String, LinkedList<String>> docs = new LinkedHashMap<String, LinkedList<String>>();
    Map<String, String> docs_Processed = new LinkedHashMap<String, String>();
    Map<Integer, String> amplify = new HashMap<Integer, String>(); //?????????????????????
   // IOClass res = new IOClass();

    List<Integer> strong_pos = new ArrayList<>();
    List<Integer> id_pos = new ArrayList<>();
    int word_number = 0;

    public void IntensifiersDiminishers_Implementation() throws IOException {
        System.out.print("IntensifiersDiminishers_Implementation(), IntensifiersDiminishers is runing ..");
        readDocs();
        dictionary();
        IntensifiersDiminishers_Main();
      //  res.resultWriter2("OUTPUT\\ProcessedDoc\\", "all_Docs.txt", docs_Processed);
        result();
        docs.clear();
        docs_Processed.clear();
        synonym.removeAll(synonym);
        amplify.clear();
        System.out.println(" it finished.");
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


    public void dictionary() throws IOException {
        BufferedReader allLine = null;
        try {
            allLine = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\SentiWordNet\\a_AND_a\\TwoDim\\synonym_dictionary.txt"));
            String line;
            while ((line = allLine.readLine()) != null) {
                synonym.add(line);
            }
            allLine.close();
        } catch (FileNotFoundException e) {
            System.out.println("File with TXT Extention might be there, chick for"
                    + "another posible  problem");
        } finally {
            if (allLine != null) {
                allLine.close();
            } else {
                System.out.println("dictionary");
            }
        }
    }

    public void readDocs() throws IOException {
        File f = new File("OUTPUT\\ProcessedDoc\\all_Docs.txt");
        if (f.exists() && f.isFile()) {
        } else {
            System.out.println("IntensifiersDiminishers. There is no file ");
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
                        word_number = word_number + hasWordList.size();
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
            } else {
                System.out.println("readDocs, IntensifiersDiminishers");
            }
        }
    }

    private void IntensifiersDiminishers_Main() {
        for (String key : docs.keySet()) {
           // System.out.println(docs.get(key));
            LinkedList<String> sentances = new LinkedList<>();
            for (String sent : docs.get(key)) {

                StringBuilder sents_Proce = new StringBuilder();
                intensifiersDiminishers_Exist(sent);
                if (!id_pos.isEmpty() || !strong_pos.isEmpty()) {
                    String token[] = sent.split("\\s");
                    intensifiersDiminishers_Processing(sent);
                    for (int i = 0; i < token.length; i++) {
                        if (amplify.containsKey(i)) {
                            token[i] = amplify.get(i);
                            sents_Proce.append(token[i]).append(" ");
                        } else {
                            sents_Proce.append(token[i]).append(" ");
                        }
                    }
                } else {
                    sents_Proce.append(sent);
                }

                sentances.add(sents_Proce.toString());
            }
            StringBuilder docs_procee = new StringBuilder();

            for (String sent : sentances) {
                docs_procee.append(sent).append(" ");
            }
          //  System.out.println(docs_procee.toString());
            docs_Processed.put(key, docs_procee.toString());
        }
    }


    private void intensifiersDiminishers_Processing(String sent) {

        String token[] = sent.split("\\s");
        for (int id_postion : id_pos) {
            int next = id_postion + 1;
            if ((token[next].equals(",")) && next != token.length - 1) {
                next = next + 1;
            }
            boolean ch = false;
            for (String ant : synonym) {
                String[] data = ant.split("\t");
                if (data[0].equalsIgnoreCase(token[next])) {
                    amplify.put(id_postion, data[2]);
                    ch = true;
                }
                if (data[2].equalsIgnoreCase(token[next])) {
                    amplify.put(id_postion, data[0]);
                    ch = true;
                }
            }
            if (ch == false) {
                amplify.put(id_postion, token[id_postion]);
            }
        }

        for (int strong_postion : strong_pos) {
            boolean ch = false;
            for (String ant : synonym) {
                String[] data = ant.split("\t");
                if (data[0].equalsIgnoreCase(token[strong_postion])) {
                    amplify.put(strong_postion, data[2]);
                    ch = true;
                }
                if (data[2].equalsIgnoreCase(token[strong_postion])) {
                    amplify.put(strong_postion, data[0]);
                    ch = true;
                }
            }
            if (ch == false) {
                amplify.put(strong_postion, token[strong_postion]);
            }
        }

    }

    private void intensifiersDiminishers_Exist(String sent) {

        strong_pos.removeAll(strong_pos);
        id_pos.removeAll(id_pos);
        List<String> IntensifiersDiminishers = new ArrayList<>();
        /*
        IntensifiersDiminishers.add("very");
        IntensifiersDiminishers.add("too");
        IntensifiersDiminishers.add("So");
        IntensifiersDiminishers.add("really");
        IntensifiersDiminishers.add("extremely");
        IntensifiersDiminishers.add("amazingly");
        IntensifiersDiminishers.add("exceptionally");
        IntensifiersDiminishers.add("incredibly");
        IntensifiersDiminishers.add("remarkably");
        IntensifiersDiminishers.add("particularly");
        IntensifiersDiminishers.add("unusually");
        IntensifiersDiminishers.add("absolutely");
        IntensifiersDiminishers.add("completely");
        IntensifiersDiminishers.add("totally");
        IntensifiersDiminishers.add("utterly");
        IntensifiersDiminishers.add("absolute");
        IntensifiersDiminishers.add("total");
        IntensifiersDiminishers.add("utter");
        IntensifiersDiminishers.add("complete");
        IntensifiersDiminishers.add("perfectly");
        IntensifiersDiminishers.add("real");
*/
                IntensifiersDiminishers.add("too");
        IntensifiersDiminishers.add("absolute");
        IntensifiersDiminishers.add("complete");
        IntensifiersDiminishers.add("perfectly");
        IntensifiersDiminishers.add("real");
        IntensifiersDiminishers.add("absolutely");
 IntensifiersDiminishers.add("amazingly");
 IntensifiersDiminishers.add("awfully");
      IntensifiersDiminishers.add("completely");
 IntensifiersDiminishers.add("considerable");
 IntensifiersDiminishers.add("considerably");
     IntensifiersDiminishers.add("decidedly");
 IntensifiersDiminishers.add("deeply");
 IntensifiersDiminishers.add("effing");
 IntensifiersDiminishers.add("enormous");
 IntensifiersDiminishers.add("enormously");
     IntensifiersDiminishers.add("entirely");
 IntensifiersDiminishers.add("especially");
 IntensifiersDiminishers.add("exceptional");
 IntensifiersDiminishers.add("exceptionally");
      IntensifiersDiminishers.add("extreme");
 IntensifiersDiminishers.add("extremely");
     IntensifiersDiminishers.add("fabulously");
 IntensifiersDiminishers.add("flipping");
 IntensifiersDiminishers.add("flippin");
 IntensifiersDiminishers.add("frackin");
 IntensifiersDiminishers.add("fracking");
     IntensifiersDiminishers.add("fricking");
 IntensifiersDiminishers.add("frickin");
 IntensifiersDiminishers.add("frigging");
 IntensifiersDiminishers.add("friggin");
 IntensifiersDiminishers.add("fully");
      IntensifiersDiminishers.add("fuckin");
 IntensifiersDiminishers.add("fucking");
 IntensifiersDiminishers.add("fuggin");
 IntensifiersDiminishers.add("fugging");
     IntensifiersDiminishers.add("greatly");
 IntensifiersDiminishers.add("hella");
 IntensifiersDiminishers.add("highly");
 IntensifiersDiminishers.add("hugely");
      IntensifiersDiminishers.add("incredible");
 IntensifiersDiminishers.add("incredibly");
 IntensifiersDiminishers.add("intensely");
      IntensifiersDiminishers.add("major");
 IntensifiersDiminishers.add("majorly");
 IntensifiersDiminishers.add("more");
 IntensifiersDiminishers.add("most");
 IntensifiersDiminishers.add("particularly");
     IntensifiersDiminishers.add("purely");
 IntensifiersDiminishers.add("quite");
 IntensifiersDiminishers.add("really");
 IntensifiersDiminishers.add("remarkably");
     IntensifiersDiminishers.add("so");
 IntensifiersDiminishers.add("substantially");
     IntensifiersDiminishers.add("thoroughly");
 IntensifiersDiminishers.add("total");
 IntensifiersDiminishers.add("totally");
 IntensifiersDiminishers.add("tremendous");
 IntensifiersDiminishers.add("tremendously");
     IntensifiersDiminishers.add("uber");
 IntensifiersDiminishers.add("unbelievably");
 IntensifiersDiminishers.add("unusually");
 IntensifiersDiminishers.add("utter");
 IntensifiersDiminishers.add("utterly");
     IntensifiersDiminishers.add("very");

        //----
        //
        List<String> strongTerm = new ArrayList<>();
        strongTerm.add("enormous");
        strongTerm.add("huge");
        strongTerm.add("tiny");
        strongTerm.add("brilliant");
        strongTerm.add("awful");
        strongTerm.add("terrible");
        strongTerm.add("disgusting");
        strongTerm.add("dreadful");
        strongTerm.add("certain");
        strongTerm.add("excellent");
        strongTerm.add("perfect");
        strongTerm.add("ideal");
        strongTerm.add("wonderful");
        strongTerm.add("splendid");
        strongTerm.add("delicious");
        //

        String token[] = sent.split("\\s");
        int token_Count = 0;
        for (String tok : token) {
            if (IntensifiersDiminishers.contains(tok.toLowerCase()) && token_Count != token.length - 1) {
                id_pos.add(token_Count);
            }
            if (strongTerm.contains(tok) && token_Count != token.length - 1) {
                //   strong_pos.add(token_Count);
            }
            token_Count++;
        }
    }

}

/*
 ===Intensifiers can come before adjectives
 very
 really
 extremely
 amazingly
 exceptionally
 incredibly
 remarkably
 particularly
 unusually
 absolutely
 completely
 totally
 utterly
 quite
 ===Those are adjectives work as intensifiers before words
 absolute
 total  - complete
 utter  - perfect
 real
 ====Intensifiers can come after adjectives
 enough

 ====strong adjectives adjectives: can act like very :
 enormous
 huge
 tiny
 brilliant
 awful
 terrible
 disgusting
 dreadful
 certain
 excellent
 perfect
 ideal
 wonderful
 splendid
 delicious
 ====== Diminishers
 airly - rather - quite
 a bit - just a bit - a little - a little bit - just a little bit - rather - slightly
 less
 ====Intensifiers with adverbs
 much - far - a lot - quite a lot - a great deal - a good deal - a good bit - a fair bit
 easily - much - far - by far


 ====Mitigators with adverbs
 a bit - just a bit - a little - a little bit - just a little bit - slightly

 ====Adverbials of probability (certainity)
 certainly - definitely - maybe - possibly
 clearly - obviously - perhaps - probably


 maybe and perhaps usually come at the beginning of the clause:


 */
///often literally from their eye

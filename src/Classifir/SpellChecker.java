/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import Preprocessing.OutPut;
import com.cybozu.labs.langdetect.LangDetectException;
import com.swabunga.spell.engine.SpellDictionary;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellCheckEvent;
import com.swabunga.spell.event.SpellCheckListener;
import com.swabunga.spell.event.StringWordTokenizer;
import com.swabunga.spell.event.TeXWordFinder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author 17511035
 */
public class SpellChecker implements SpellCheckListener {

    public static Map<String, String> docs_Processed = new LinkedHashMap<>();
    private List<String> misspelledWords;
    // private static String dictFile = "SpellCheckers\\dict\\english1.0";
    private static String phonetFile = "SpellCheckers\\dict\\phonet.en";
    private com.swabunga.spell.event.SpellChecker spellChecker = null;
    // IOClass res = new IOClass();

    public void spellCheck_Implementation(String dic) throws IOException, LangDetectException {
        System.out.print("spellCheck_Implementation(String dic), SpellChecker is runing ..");
        SpellDictionary dictionary = new SpellDictionaryHashMap(new File(dic), new File(phonetFile));
        spellChecker = new com.swabunga.spell.event.SpellChecker(dictionary);
        misspelledWords = new ArrayList<>();
        spellChecker.addSpellCheckListener(this);
        read_proce_docs();
        //  res.resultWriter2("OUTPUT\\ProcessedDoc\\", "all_Docs.txt", docs_Processed);
        result();
        docs_Processed.clear();
        misspelledWords.removeAll(misspelledWords);
        spellChecker.reset();
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

    public List<String> getMisspelledWords(String text) {
        StringWordTokenizer texTok = new StringWordTokenizer(text, new TeXWordFinder());
        spellChecker.checkSpelling(texTok);
        return misspelledWords;
    }
    private static SpellDictionaryHashMap dictionaryHashMap;

    static {
        File dict;
        dict = new File("dic/dic.txt");
        try {
            dictionaryHashMap = new SpellDictionaryHashMap(dict);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    public List<String> getSuggestions(String misspelledWord) {
        @SuppressWarnings("unchecked")
        List<Word> su99esti0ns = spellChecker.getSuggestions(misspelledWord, 0);
        List<String> suggestions = new ArrayList<>();
        for (Word suggestion : su99esti0ns) {
            suggestions.add(suggestion.getWord());
        }
        return suggestions;
    }

    public String getCorrectedDoc(String doc) {
        StringBuilder builder = new StringBuilder();
        Map<String, String> bestSug = new LinkedHashMap<>();
        List<String> misSpelledWords = getMisspelledWords(doc);
        for (String misSpelledWord : misSpelledWords) {
            List<String> suggestions = getSuggestions(misSpelledWord);
            if (suggestions.isEmpty()) {
                continue;
            }
            bestSug.put(misSpelledWord, suggestions.get(0));
            suggestions.removeAll(suggestions);
        }
        String doc_word[] = doc.split("\\s");
        for (String word : doc_word) {
            if (word.endsWith(".")) {
                StringBuilder w = new StringBuilder();
                for (int i = 0; i < word.length() - 1; i++) {
                    w.append(word.charAt(i));
                }
                if (bestSug.containsKey(w)) {
                    builder.append(bestSug.get(w)).append('.').append(' ');
                } else {
                    builder.append(w).append('.').append(' ');
                }
            } else {
                if (bestSug.containsKey(word)) {

                    builder.append(bestSug.get(word)).append(' ');
                } else {
                    builder.append(word).append(' ');

                }
            }

        }
        for (String s : bestSug.keySet()) {
        }
        bestSug.clear();
        misSpelledWords.removeAll(misSpelledWords);
        misspelledWords.removeAll(misSpelledWords);
        return builder.toString();
    }

    public void read_proce_docs() throws IOException, LangDetectException {
        File f = new File("OUTPUT\\ProcessedDoc\\all_Docs.txt");
        if (f.exists() && f.isFile()) {
            System.out.println("GOOD");
        } else {
            System.out.println("SpellChecker. There is no file ");
            System.exit(0);
        }
        BufferedReader allDocs = null;
        try {
            allDocs = new BufferedReader(new FileReader("F:\\Dropbox\\Coding\\OUTPUT\\ProcessedDoc\\all_Docs.txt"));
            String doc;
            while ((doc = allDocs.readLine()) != null) {
                if (!doc.startsWith("#")) {
                    String[] data = doc.split("\t");
                    docs_Processed.put(data[0], getCorrectedDoc(data[1]));
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
                System.out.println("read_proce_docs");
            }
        }
    }

    @Override
    public void spellingError(SpellCheckEvent event) {
        event.ignoreWord(true);
        misspelledWords.add(event.getInvalidWord());
    }

    public void duplicated_Consecutive_Letter() {

    }
}

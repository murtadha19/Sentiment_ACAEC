/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Preprocessing;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author 17511035
 */
public class OutPut {

    public void outputAsTextFiles(String path, String fileName, List<String> listOfFeatures) throws IOException {
        File directory = new File(path);
        directory.mkdirs();
        BufferedWriter outputStream1 = null;

        try {
            outputStream1 = new BufferedWriter(new FileWriter(path + fileName));
            for (String words : listOfFeatures) {
                outputStream1.write(words);
                outputStream1.newLine();
            }
            outputStream1.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file MetadataFile.txt.");
        } finally {
            if (outputStream1 != null) {
                outputStream1.close();
            }
        }
        System.out.println(" 'All Adjectives And Adverbs words.txt' in the corpus is GANERATED.");
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
        PrintWriter outputStream1 = null;
        try {
            outputStream1 = new PrintWriter(new FileOutputStream(path + fileName));
            outputStream1.println("NULL#");
            for (String words : listOfFeatures) {
                outputStream1.println(words);
            }
            outputStream1.close();
            System.out.println(" 'All Adjectives And Adverbs words.txt' in the corpus is GANERATED.");
        } catch (IOException e) {
            System.out.println("Error opening the file " + fileName);
            e.printStackTrace();
        } finally {
            if (outputStream1 != null) {
                outputStream1.close();
            }
        }
    }

    public class Writer {

        PrintWriter file;
        // String content;

        Writer(String path) {
            this.file = null;
            try {
                file = new PrintWriter(new FileOutputStream(path));
            } catch (FileNotFoundException e) {
                System.out.println("Error opening the file " + path);
            }
        }

        public void write(String content) {
            this.file.print(content);
        }

        public void writeln(String content) {
            this.file.println(content);
        }

        public void writeln() {
            this.file.println();
        }

        public void close() {
            this.file.close();
        }

      
    }

    public void resultWriter(String path, String fileName, Map<String, Double[]> docValu, Set<String> labeledFeatureSet) throws IOException {
        File directory = new File(path);
        directory.mkdirs();
        BufferedWriter newFileSWN = null;
        try {
            newFileSWN = new BufferedWriter(new FileWriter(fileName));
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
            newFileSWN.close();
        } catch (IOException e) {
            System.out.println("Error opening the file" + fileName);
        } finally {
            if (newFileSWN != null) {
                newFileSWN.close();
            }
        }
    }

   
    public void resultWriter2(String path, Map<String, String> docWords) throws IOException {
        File directory = new File(path);
        directory.mkdirs();
        BufferedWriter newFileSWN = null;
        for (String key : docWords.keySet()) {
            try {
                newFileSWN = new BufferedWriter(new FileWriter(path + key + ".txt"));
                newFileSWN.write(docWords.get(key));
                newFileSWN.newLine();
                newFileSWN.close();
            } catch (IOException e) {
                System.out.println("Error opening the file" + key + ".txt");
            } finally {
                if (newFileSWN != null) {
                    newFileSWN.close();
                }
            }

        }
    }

    public void results(String path, String fileName, List<String> wrongDoc, String classiferName) throws IOException {
        File directory = new File(path);
        directory.mkdirs();
        BufferedWriter newFileSWN = null;
        try {
            newFileSWN = new BufferedWriter(new FileWriter(path + fileName+"_"+classiferName + ".txt"));
            for (String doc : wrongDoc) {
                newFileSWN.write(doc);
                newFileSWN.newLine();
            }
            newFileSWN.close();
        } catch (IOException e) {
            System.out.println("Error opening the file" + fileName);
        } finally {
            if (newFileSWN != null) {
                newFileSWN.close();
            }
        }

    }

    public void Silhouette_Values_means(List<Double> Ss_groupTwo, List<Double> Ss_groupOne, String fileName) throws IOException {
        String[] x = fileName.split("\\\\");
        File directory = new File("OUTPUT\\Silhouette_Values_means");
        directory.mkdirs();
        BufferedWriter newFileSWN = null;
        try {
            newFileSWN = new BufferedWriter(new FileWriter("OUTPUT\\Silhouette_Values_means\\" + x[x.length - 2] + "_" + x[x.length - 1]));
            newFileSWN.write("#Ss_groupOne ==================");
            newFileSWN.newLine();
            for (double doc : Ss_groupOne) {
                newFileSWN.write(Double.toString(doc));
                newFileSWN.newLine();
            }
            newFileSWN.write("#Ss_groupTwo ==================");
            for (double doc : Ss_groupTwo) {
                newFileSWN.write(Double.toString(doc));
                newFileSWN.newLine();
            }
            newFileSWN.close();
        } catch (IOException e) {
            System.out.println("Error opening the file" + fileName);
        } finally {
            if (newFileSWN != null) {
                newFileSWN.close();
            }
        }

    }

    public void resultWriter2(String path, String fileName, List<String> wrongDoc) throws IOException {
        File directory = new File(path);
        directory.mkdirs();
        BufferedWriter newFileSWN = null;
        try {
            newFileSWN = new BufferedWriter(new FileWriter(path + fileName));
            int x = 0;
            for (String doc : wrongDoc) {
                newFileSWN.write(doc);
                newFileSWN.newLine();
                newFileSWN.newLine();
                x++;
                if (x == 2) {
                    newFileSWN.newLine();
                    newFileSWN.write("===========================");
                    newFileSWN.newLine();
                    x = 0;
                }
            }
            newFileSWN.write("# " + fileName + " no " + wrongDoc.size() / 2);
            newFileSWN.newLine();
            newFileSWN.close();
        } catch (IOException e) {
            System.out.println("Error opening the file" + fileName);
        } finally {
            if (newFileSWN != null) {
                newFileSWN.close();
            }
        }
    }

    public void typeResult(Map<String, Integer> gruopANDdoc, String fileName) throws IOException {
        String[] val = fileName.split("-");
        File dir = new File("OUTPUT\\Ensample");
        dir.mkdirs();
        BufferedWriter outputStream = null;
        try {
            outputStream = new BufferedWriter(new FileWriter("OUTPUT\\Ensample\\" + "r-" + val[val.length - 1]));
            for (Map.Entry entry : gruopANDdoc.entrySet()) {
                outputStream.write(entry.getKey() + "\t" + entry.getValue());
                outputStream.newLine();
            }
            outputStream.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }

    public void dataset() throws IOException {
        File dir = new File("F:\\Dropbox\\DS\\MyDataSet\\Airlines\\Terrible");
        dir.mkdirs();
        BufferedWriter write = null;
        try {
            write = new BufferedWriter(new FileWriter("F:\\Dropbox\\DS\\MyDataSet\\Airlines\\Terrible\\neg().txt"));
            write.write("FU** YOu");
            write.newLine();
            write.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } finally {
            if (write != null) {
                write.close();
            }
        }

    }

    public void outputAsTextFiles(Map<String, Integer> wordsFrequencyHash) throws IOException {
        File directory = new File("OUTPUT\\Some_output_As_Text_Files");
        directory.mkdirs();
        BufferedWriter outputStream1 = null;
        try {
            outputStream1 = new BufferedWriter(new FileWriter("OUTPUT\\Some_output_As_Text_Files\\All_The_Words_and_their_frequency.txt"));
            outputStream1.write(" All The Words and their frequency");
            outputStream1.newLine();
            for (String words : wordsFrequencyHash.keySet()) {
                int wordsFrequency = wordsFrequencyHash.get(words);
                outputStream1.write(words + " : " + wordsFrequency);
                outputStream1.newLine();

            }
            outputStream1.close();
        } catch (IOException e) {
            System.out.println("Error opening the file MetadataFile.txt.");
        } finally {
            if (outputStream1 != null) {
                outputStream1.close();
            }
        }
        System.out.println(" 'All The Words and their frequency.txt' is GANERATED.");
    }

}

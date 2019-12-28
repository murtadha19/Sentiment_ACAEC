/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Preprocessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author 17511035
 */
public class AugmentedLexicons {

    public static List SWNList = new LinkedList<>();
    public static List MPQAList = new LinkedList<>();
    public static List matched = new LinkedList<>();
    public static List newWords_ToSWN = new LinkedList<>();
    public static List newWords_ToMPQA = new LinkedList<>();

    public void imli() throws IOException {
        reading("Lexicons\\All_Pos-Neg.txt", "Lexicons\\MPQA.txt");
        matcher();
        extraWordsToSWN();
        writer("Lexicons", newWords_ToMPQA);

    }

    private void reading(String SWNPath, String MPQAPath) throws IOException {
        BufferedReader SWNReader = null;
        BufferedReader MPQAReader = null;
        try {
            SWNReader = new BufferedReader(new FileReader(SWNPath));
            MPQAReader = new BufferedReader(new FileReader(MPQAPath));
            String SWNLine;
            while ((SWNLine = SWNReader.readLine()) != null) {
                SWNList.add(SWNLine);
            }
            String MPQALine;
            while ((MPQALine = MPQAReader.readLine()) != null) {
                MPQAList.add(MPQALine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (SWNReader != null) {
                SWNReader.close();
            }else {
                System.out.println("reading");
            }
            if (MPQAReader != null) {
                MPQAReader.close();
            }else {
                System.out.println("reading");
            }
        }

    }

    private void matcher() {
        for (int i = 0; i < SWNList.size(); i++) {
            String[] SWNdata = SWNList.get(i).toString().split("\t");
            for (int j = 0; j < MPQAList.size(); j++) {
                String[] MPQAdata = MPQAList.get(j).toString().split("\t");
                if (SWNdata[0].equalsIgnoreCase(MPQAdata[0])) {
                    matched.add(MPQAList.get(j));
                }
            }
        }
        System.out.println("The number of the matched word : " + matched.size());
    }

    private void extraWordsToSWN() {
        for (int r = 0; r < MPQAList.size(); r++) {
            newWords_ToSWN.add(MPQAList.get(r));
        }
        System.out.println("New list size : " + newWords_ToSWN.size());
        for (int i = 0; i < matched.size(); i++) {
            String[] matchedData = matched.get(i).toString().split("\t");
            for (int x = 0; x < newWords_ToSWN.size(); x++) {
                String[] newData = newWords_ToSWN.get(x).toString().split("\t");
                if (matchedData[0].equalsIgnoreCase(newData[0])) {
                    newWords_ToSWN.remove(x);
                }
            }
        }
        System.out.println("The number of the new eliments to be added: " + newWords_ToSWN.size());
    }

    private void writer(String path, List newWords) throws IOException {
        File directory = new File(path);
        directory.mkdirs();
        BufferedWriter newFileSWN = null;
        try {
            newFileSWN = new BufferedWriter(new FileWriter(path + "\\newWords.txt"));
            for (int x = 0; x < newWords.size(); x++) {
                newFileSWN.write(newWords.get(x).toString());
                newFileSWN.newLine();
            }
            
            newFileSWN.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file ");
        } finally {
            if (newFileSWN != null) {
                newFileSWN.close();
            }else {
                System.out.println("writer");
            }
        }

    }
}

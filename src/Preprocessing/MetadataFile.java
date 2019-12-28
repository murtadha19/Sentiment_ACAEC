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

/**
 *
 * @author morteza
 */
class MetadataFile {

    public void metadataFile(int eachfileendixmethod, int linenumbermethod) throws IOException {
        File directory = new File("OUTPUT\\MetadataFile");
        directory.mkdirs();
        BufferedWriter outputStream = null;
        try {
            outputStream = new BufferedWriter(new FileWriter("OUTPUT\\MetadataFile\\MetadataFile.txt"));
            outputStream.write("The Number of the Text Documents IS " + eachfileendixmethod);
            outputStream.newLine();
            outputStream.write("The Number of the lines IS " + linenumbermethod);
            outputStream.newLine();
            outputStream.close();
        } catch (IOException e) {
            System.out.println("Error opening the file MetadataFile.txt.");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        System.out.println("MetadataFile.txt have been ganerated.");
        System.out.println("The Number of the Text Files IS " + eachfileendixmethod);
        System.out.println("The Number of the lines IS " + linenumbermethod);
    }

    public void metadataFile(String comment, int counterToMetadata) throws IOException {
        File directory = new File("OUTPUT\\MetadataFile");
        directory.mkdirs();
        BufferedWriter outputStream = null;
        try {
            outputStream = new BufferedWriter(new FileWriter("OUTPUT\\MetadataFile\\MetadataFile.txt"));
            outputStream.write(comment + counterToMetadata);
            outputStream.newLine();
            outputStream.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error opening the file MetadataFile.txt.");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

        System.out.println(comment + counterToMetadata);
    }

}

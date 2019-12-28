/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Preprocessing;

/**
 *
 * @author 17511035
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ReadingDataset {

    ArrayList<String> reviews = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();

    public void read() throws IOException {
        BufferedReader txt = null;
        try {
            txt = new BufferedReader(new FileReader("F:\\Dropbox\\DS\\magazines\\positive.txt"));
            String line;
            while ((line = txt.readLine()) != null) {
                data.add(line);
            }
            int reviewCount = 0;
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).equals("<review_text>")) {
                    int j = i + 1;
                    String rev = "";
                    while (!data.get(j).equals("</review_text>")) {
                        if (rev.equals("")) {
                            rev = data.get(j);
                        } else {
                            rev = rev + "\n" + data.get(j);
                        }
                        j++;

                    }
                    i = j;
                    reviewCount++;
                    //     System.out.println(reviewCount + " : " + rev);
                    write(reviewCount, rev);
                }
            }
            txt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (txt != null) {
                txt.close();
            }
        }
    }

    public void read2() throws IOException {
        BufferedReader txt = null;
        try {
            txt = new BufferedReader(new FileReader("F:\\Dropbox\\DS\\scale_dataset_v1.0\\scaledata\\Steve+Rhodes\\New\\neu.txt"));
            String line;
            while ((line = txt.readLine()) != null) {
                data.add(line);
            }
            int reviewCount = 1267;
            for (int i = 0; i < data.size(); i++) {
                reviewCount++;
                write(reviewCount, data.get(i));
            }
            txt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (txt != null) {
                txt.close();
            }
        }
    }

    private void write(int reviewCount, String review) throws IOException {
        BufferedWriter printReview = null;
        try {
            printReview = new BufferedWriter(new FileWriter("F:\\Dropbox\\DS\\scale_dataset\\neu\\neu (" + reviewCount + ").txt"));
            printReview.write(review);
            printReview.close();
        } catch (IOException e) {
            System.out.println("Error opening the file.");
        } finally {
            if (printReview != null) {
                printReview.close();
            }
        }
    }
}

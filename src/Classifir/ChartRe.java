/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.Styler.LegendPosition;
import org.knowm.xchart.style.markers.SeriesMarkers;

public class ChartRe {

    public void cha(int winNo) throws ParseException {
        double[] xData = new double[winNo];

        for (int j = 0; j < winNo; j++) {
            xData[j] = j;
        }
        double[] yDataAP = new double[winNo];
        double[] yDataPP = new double[winNo];
        File dir;
        dir = new File("F:\\Dropbox\\Coding\\OUTPUT\\WindowOutbut");
        File[] files = dir.listFiles();
        int i = 0;
        for (File f : files) {

            String fileName = f.getName();
            try (BufferedReader inputstream = new BufferedReader(new FileReader(f))) {
                String doc;
                while ((doc = inputstream.readLine()) != null) {
                    String[] line = doc.split(",");
                    System.out.println(line[0]);
                    yDataPP[i] = DecimalFormat.getNumberInstance().parse(line[0]).doubleValue();
                    yDataAP[i] = DecimalFormat.getNumberInstance().parse(line[2]).doubleValue();
                    System.out.println(yDataPP[i]);
                }
            } catch (IOException e) {
                System.out.println("File with TXT Extention might be there, chick for");
                e.getMessage();
            }
            i++;
        }

        org.knowm.xchart.XYChart chart = QuickChart.getChart("Sample Chart", "Windows", "Accuracy", "v", xData, yDataPP);
        XYSeries series = chart.addSeries("Actual Positivity", xData, yDataAP);
        // Show it
        new SwingWrapper(chart).displayChart();

    }

}

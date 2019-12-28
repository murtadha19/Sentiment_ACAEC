/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.net.*;
import java.io.*;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RvScraper {

    //metricon(rate=4,5,1,2), clarendon(rate=4,5,1,2), simonds(rate=4,5,1,2),porter-davis-homes(rate=4,5,1,2), 
    //hotondo-homes (rate=1,2), boutique-homes (rate=1,2), 8-homes (rate=1,2), Burbank (rate=1,2), 
    //homebuyers-center (rate=1,2), beechwood-homes (rate=1,2),dixon-homes (rate=1,2),watersun-homes (rate=1,2), 
    //plantation-homes (rate=1,2), kurmond-homes (rate=1,2. 
    ///******Airlines
    // tiger-airways , jetstar, qantas, virgin-blue
    ////////
    //  second paper: toys-paradise, holden-astra-hatch-2004-present, sealy-posturepedic-luxury-collection, ray-white
    String pr = "";
    int pagesNo = 0; // start with number of pages +1
    int fileNo = 0; //start with 1, and from new number
    int rating = 0;
    Set<String> docs = new LinkedHashSet<>();
    List<String> docsDate = new LinkedList<>();

    //" + "#reviews""
    public void productreview() {
        String urll = "p/" + pr + ".html?rating=" + rating + "#reviews";
        scrapeDocDate(urll);
        scrapeTopic(urll);
        if (pagesNo != 0) {
            for (int j = 2; j < pagesNo; j++) {
                urll = "p/" + pr + "/" + j + ".html?rating=" + rating + "#reviews";
                scrapeDocDate(urll);
              scrapeTopic(urll);
            }
        }
        writ();
    }

    public void scrapeTopic(String url) {
        String html = getUrl("http://www.productreview.com.au/" + url);
        Document doc = Jsoup.parse(html);
        Elements link = doc.getElementsByAttributeValue("class", "review-overall");

        for (Element lin : link) {
            String s = null;
            s = lin.getElementsByAttributeValue("class", "review-overall-start").text();
            s = lin.getElementsByAttributeValue("class", "review-overall-start").text();
            s = s + lin.getElementsByAttributeValue("class", "review-overall-end").text();
            if (s.isEmpty()) {
                s = lin.getElementsByAttributeValue("itemprop", "description").text();
            }
            docs.add(s);
        }
    }

    public void writ() {
        int i = 0;
        System.out.println("The size of the docs is : " + docsDate.size());

        for (String text : docs) {
            System.out.println(i);
             dataset(docsDate.get(i) + " " + text);
            i++;
            fileNo++;
        }

    }

    public void scrapeNewSite() {
        String html = getUrl("https://www.amazon.com/Kindle-Paperwhite-High-Resolution-Display-Built/product-reviews/B00QJDU3KY/ref=cm_cr_arp_d_paging_btm_2?ie=UTF8&reviewerType=avp_only_reviews&showViewpoints=1&sortBy=helpful&pageNumber=52");
        Document doc = Jsoup.parse(html);
        System.out.println(doc);
    }

    public void scrapeDocDate(String url) {

  
        String html = getUrl("http://www.productreview.com.au/" + url);
        Document doc = Jsoup.parse(html);
        Elements date = doc.getElementsByAttributeValueMatching("content", "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$");
        for (Element g : date) {
            String z = g.toString().substring(40, 50);
            LocalDate Date = LocalDate.parse(z); 
            
                docsDate.add(z);
        }
    }

    public void scrapeTopicTest(String url) {
        String html = getUrl("http://www.productreview.com.au/" + url);
        Document doc = Jsoup.parse(html);
        //  System.out.println(doc);
        Elements date = doc.getElementsByAttributeValueMatching("content", "^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$");
        //           <meta itemprop="datePublished" content="2017-02-04"> reviewed on Feb 04, 2017 </p>"^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$"
        for (Element g : date) {
            String z = g.toString().substring(40, 50);
            System.out.println(z);
            // String g= d.("content","2017-02-04").text();         
            //    System.out.println(g.getElementsByAttributeValueMatching("content","^\\d{4}\\-\\d{1,2}\\-\\d{1,2}$"));
        }
        // <span itemprop="description">      
        // System.out.println(doc);
    }

    public static String getUrl(String url) {
        URL urlObj = null;
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            System.out.println("The url was malformed!");
            return "";
        }
        URLConnection urlCon = null;
        BufferedReader in = null;
        String outputText = "";
        try {
            urlCon = urlObj.openConnection();
            in = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                outputText += line;
            }
            in.close();
        } catch (IOException e) {
            System.out.println(
                    "There was an error connecting to the URL");
            return "";
        }
        return outputText;
    }

    public void dataset(String review) {
        File dir = new File("F:\\Dropbox\\DS\\MyDataSet\\   \\Negative");
        dir.mkdirs();
        PrintWriter write = null;
        try {
            write = new PrintWriter(new FileOutputStream("F:\\Dropbox\\DS\\MyDataSet\\   \\Negative\\neg (" + fileNo + ").txt"));
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        write.println(review);
        write.close();
    }
}

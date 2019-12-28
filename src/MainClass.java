/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author 17511035
 */
import Classifir.Conjunction;
import Classifir.DateSeriesClustring;
import Classifir.DetectCheckLang;
import Classifir.Ensample;
import Classifir.IncrementalClassifier;
import Classifir.IntensifiersDiminishers;
import Classifir.Kmeans2;
import Classifir.KmeansRandom;
import Classifir.Negation;
import Classifir.RoleBasedClassifier;
import Classifir.Selection;
import Classifir.SpellChecker;
import Preprocessing.AugmentedLexicons;
import Preprocessing.Processing;
import Preprocessing.Reading;
import Preprocessing.ReadingDataset;
import Preprocessing.SentiWordNet;
import com.cybozu.labs.langdetect.LangDetectException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import scraper.RvScraper;

public class MainClass {
    public int x=0;

    public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException, LangDetectException, Exception {


        DateSeriesClustring DSC = new DateSeriesClustring("F:\\Dropbox\\DS\\Wizz\\textANDdate");
         // DSC.implementation();
           DSC.sequential_implementation();

    }

}

package edu.dartmouth.cs.myorganizer.ML;


import android.text.style.TtsSpan;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;


public class TextProcessing {
    private static final String SIMPLE_SPACE_OR_PUNCTUATION = " |\\,|\\.|\\!|\\?|\n";
    private static final String DEBUG = "TextProcessing";

    //Just split the text into words by space. More complex preprocessing (steming, lemmatization, rare word etc)
    //should be handled in a preprocessing stage like this
    public static String [] preprocess(String text){

        ArrayList<String> tokens = new ArrayList<>(Arrays.asList(text.split(SIMPLE_SPACE_OR_PUNCTUATION)));
        String toks[] = new String[tokens.size()];
        int j;
        for(j =0;j<tokens.size();j++){
            toks[j] = tokens.get(j);
        }
        String[] toksStemmed = new String[tokens.size()];
        for (int i =0; i < tokens.size(); i++){
            englishStemmer stemmer = new englishStemmer();
             stemmer.setCurrent(toks[i]);
            if (stemmer.stem()){
                toksStemmed[i] = stemmer.getCurrent();

            }


        }

        return toksStemmed;
    }
}

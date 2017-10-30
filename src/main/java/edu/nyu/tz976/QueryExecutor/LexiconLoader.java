package edu.nyu.tz976.QueryExecutor;

import edu.nyu.tz976.LexiconWordTuple;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

public class LexiconLoader {
    public HashMap<String, LexiconWordTuple> lexiconMap = new HashMap<>();

    public void loadLexicon() {

        Path lexiconPath = Paths.get("./output/lexicon.txt");
        File lexiconFile = lexiconPath.toFile();

        try {
            BufferedReader lexiconInStream = new BufferedReader(new FileReader(lexiconFile));
            String line;
            while ((line = lexiconInStream.readLine()) != null) {
                String[] lexiconSplit = line.split(",");
                if (lexiconSplit.length == 4) {
                    LexiconWordTuple tuple = new LexiconWordTuple(lexiconSplit[1], lexiconSplit[2], lexiconSplit[3]);
                    lexiconMap.put(lexiconSplit[0], tuple);
                }
            }
            lexiconInStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}


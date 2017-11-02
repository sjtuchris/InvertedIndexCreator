package edu.nyu.tz976.InvertedIndexGenerator;

import edu.nyu.tz976.DocIdCounter;
import edu.nyu.tz976.LexiconWordTuple;
import edu.nyu.tz976.WordCountMap;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputUtils {

    public static void writeIntermediatePostings(List<WordCountMap> wordCountMapList, String fileName) {

        Path file = Paths.get("./output/Postings_" + fileName + ".txt");
        File outputFile = file.toFile();

        try {
            outputFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(outputFile, true);

            for (WordCountMap tmpMap:wordCountMapList) {
                for (Map.Entry<String, Integer> entry:tmpMap.map.entrySet()) {
                    String word = entry.getKey();
                    Integer count = entry.getValue();
                    String stringData = word + "\t" + String.valueOf(tmpMap.docId) + "\t" + count + "\n";
                    byte[] byteData = stringData.getBytes();

                    try {
                        outputStream.write(byteData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeLexiconMap(HashMap<String, LexiconWordTuple> lexiconMap) {
        Path file = Paths.get("./output/lexicon.txt");
        File outpuFile = file.toFile();

        try {
            outpuFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(outpuFile, true);

            // Write total doc numbers at the first line of lexicon
            try {
                byte[] totalDoc = (Integer.toString(DocIdCounter.DOC_ID)+"\n").getBytes();
                outputStream.write(totalDoc);
            } catch (IOException e) {
                e.printStackTrace();
            }

            for (Map.Entry<String, LexiconWordTuple> entry:lexiconMap.entrySet()) {
                String word = entry.getKey();
                LexiconWordTuple lexiconTuple = entry.getValue();
                String stringData = word + "," + lexiconTuple.startByte + "," + lexiconTuple.endByte + "," + lexiconTuple.numOfDoc + "\n";
                byte[] byteData = stringData.getBytes();

                try {
                    outputStream.write(byteData);
                } catch (IOException e){
                    e.printStackTrace();
                }
            }

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writePageUrlTable(List<List<String>> pageUrlTable) {
        Path file = Paths.get("./output/pageUrlTable.txt");

        int counter = 0;

        for(int idx = 0; idx < pageUrlTable.size(); idx++) {
            String list = StringUtils.join(pageUrlTable.get(idx), " ");

            if (list.length() == 0) {
                continue;
            }

            String stringData = list + "\n";
            byte[] byteData = stringData.getBytes();
            try {
                Files.write(file, byteData, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                counter++;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        System.out.println(counter);
    }
}

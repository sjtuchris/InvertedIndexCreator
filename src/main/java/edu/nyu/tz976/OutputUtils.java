package edu.nyu.tz976;

import com.google.common.collect.Maps;

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
import java.util.stream.IntStream;

public class OutputUtils {
    public void writeIntermediatePostings(List<HashMap<String, Integer>> wordCountMapList) {

        Path file = Paths.get("./output/tempPostings.txt");
        File outputFile = file.toFile();

        try {
            outputFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(outputFile, true);

            IntStream.range(0, wordCountMapList.size())
                    .forEach(idx -> {
                        HashMap<String, Integer> tmpMap = wordCountMapList.get(idx);
                        tmpMap.forEach((word, count) -> {
                            String stringData = word + " " + String.valueOf(idx) + " " + count + "\n";
//                        System.out.println(stringData);
                            byte[] byteData = stringData.getBytes();

                            try {
                                outputStream.write(byteData);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    });

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeLexiconMap(HashMap<String, LexiconWordTuple> lexiconMap) {
        Path file = Paths.get("./output/lexicon.txt");
        File outpuFile = file.toFile();

        try {
            outpuFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(outpuFile, true);

            lexiconMap.forEach((word, lexiconTuple) -> {
                String stringData = word + "," + lexiconTuple.startByte + "," + lexiconTuple.endByte + "," + lexiconTuple.numOfDoc + "\n";
                byte[] byteData = stringData.getBytes();

            try {
//                Files.write(file, byteData, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                outputStream.write(byteData);
            } catch (IOException e){
                //
            }
            });

            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePageUrlTable(List<List<String>> pageUrlTable) {
        Path file = Paths.get("./output/pageUrlTable.txt");

        int counter = 0;

        for(int idx = 0; idx < pageUrlTable.size(); idx++) {
            String list = String.join(" ", pageUrlTable.get(idx));
            if (list.length() == 0) {
                continue;
            }

            // Filter those pages that are not ascii format
            if (list.charAt(0) != '0') {
                String stringData = String.valueOf(idx) + " " + list + "\n";
                byte[] byteData = stringData.getBytes();
                try {
                    Files.write(file, byteData, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                    counter++;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println(counter);
    }
}

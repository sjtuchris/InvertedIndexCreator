package edu.nyu.tz976;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.*;
import java.util.HashMap;
import java.util.List;

public class WetReader {
    private HashMap<Integer, Integer> wordCountMap = Maps.newHashMap();
    private HashMap<String, Integer> wordIdMap = Maps.newHashMap();
    private List<String> wordList = Lists.newArrayList();
    public List<List<String>> fileContentList = Lists.newArrayList();
    public List<List<String>> fileHeaderList = Lists.newArrayList();

    public void loadWetData(String fileName) {

        BufferedReader reader = null;
        int docCounter = -1;

        try {
            File file = new File(fileName);
            reader = new BufferedReader(new FileReader(file));
            String contentType = "Header";
            String pageCheck;
            String line;

            while ((line = reader.readLine()) != null) {
                pageCheck = newPageCheck(line, contentType);

                if (pageCheck.equals("Header")) {
//                    printHashMap(docCounter);
                    contentType = "Header";
//                    System.out.println(contentType);
                    docCounter++;
                } else if (pageCheck.equals("Content")) {
                    contentType = "Content";
//                    System.out.println(contentType);

                }

                if (contentType.equals("Content")) {
                    if (fileContentList.size() < docCounter + 1) {
                        List<String> pageLineList = Lists.newArrayList();
                        fileContentList.add(pageLineList);
                    }
//                    System.out.println(docCounter);
                    fileContentList.get(fileContentList.size() - 1).add(line);
                } else {
                    if (fileHeaderList.size() < docCounter + 1) {
                        List<String> pageHeaderList = Lists.newArrayList();
                        fileHeaderList.add(pageHeaderList);
                    }
                    fileHeaderList.get(fileHeaderList.size() - 1).add(line);
                }
//                System.out.println(line);
            }
//            System.out.println(docCounter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert reader != null;
                reader.close();
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private String newPageCheck(String line, String previousContentType) {
        if (line.substring(0, Math.min(line.length(), 8)).equals("WARC/1.0")) {
            return "Header";
        } else if (line.length() == 0 && previousContentType.equals("Header")) {
            return "Content";
        }
        return "Pass";
    }

    private void printHashMap(int docId) {
        for(Object objname:wordCountMap.keySet()) {
            System.out.println("DocId: " + String.valueOf(docId));
            System.out.println(objname);
            System.out.println(wordCountMap.get(objname));
        }
        wordCountMap = Maps.newHashMap();
    }
}

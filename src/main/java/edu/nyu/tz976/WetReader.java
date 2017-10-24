package edu.nyu.tz976;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class WetReader {
    public List<List<String>> fileContentList = Lists.newArrayList();
    public List<List<String>> fileHeaderList = Lists.newArrayList();
    public List<HashMap<String, Integer>> wordCountMapList = Lists.newArrayList();
    public List<List<String>> pageUrlList = Lists.newArrayList();

    public void loadWetData(Path path) {
        List<String> contentList = Lists.newArrayList();
        List<String> headerList = Lists.newArrayList();

        BufferedReader reader = null;

        try {
            File file = path.toFile();
            reader = new BufferedReader(new FileReader(file));
            String contentType = "Header";
            String pageCheck;
            String line;

            while ((line = reader.readLine()) != null) {
                pageCheck = newPageCheck(line, contentType);

                if (pageCheck.equals("Header")) {
                    // Before go to the next doc, process the current doc first
                    // New doc if previous line is content
                    if(contentType.equals("Content")) {

                        processDocData(contentList, headerList);
                        PageUrlTable.appendPageUrlList(pageUrlList);

                        contentList = Lists.newArrayList();
                        headerList = Lists.newArrayList();
                        pageUrlList = Lists.newArrayList();
                    }
                    contentType = "Header";

                } else if (pageCheck.equals("Content")) {
                    contentType = "Content";
                }

                if (contentType.equals("Content")) {
                    contentList.add(line);
                } else {
                    headerList.add(line);
                }
            }
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

    private void processDocData (List<String> contentLines, List<String> headerLines) {

        // Count term frequency in one doc
        HashMap<String, Integer> wordCountMap = getWordCountMap(contentLines);
        wordCountMapList.add(wordCountMap);

        // Map url and termNum for each doc
        List<String> pageUrlTermNumTuple = pageUrlMapper(headerLines, wordCountMap.size());
        pageUrlList.add(pageUrlTermNumTuple);
    }

    private List<String> pageUrlMapper(List<String> headerLines, int wordCountMapSize) {
        List<String> urlTermNumTuple = Lists.newArrayList();
        for (String headerLine:headerLines) {
            String[] lineParts = headerLine.split(" ");
            if (lineParts[0].equals("WARC-Target-URI:")) {
                String url = lineParts[1];
                String termNum = String.valueOf(wordCountMapSize);
                urlTermNumTuple.add(termNum);
                urlTermNumTuple.add(url);
            }
        }
        return urlTermNumTuple;
    }

    private HashMap<String, Integer> getWordCountMap(List<String> docLines) {
        HashMap<String, Integer> wordCountMap = Maps.newHashMap();
        for (String line:docLines) {
            if (isAscii(line)) {
                String regex = "([^a-zA-Z0-9']+)'*\\1*";
                String[] words = line.split(regex);
                for (String word : words) {
                    if (word.equals(" ") || word.equals("") || word.length() == 0) {
                        continue;
                    }
                    String tmp = word.replaceAll("'", "");
                    Integer count = wordCountMap.get(tmp);
                    if (count == null) {
                        wordCountMap.put(tmp, 1);
                    } else {
                        wordCountMap.put(tmp, count + 1);
                    }
                }
            }
        }
       return wordCountMap;
    }

    private boolean isAscii(String line) {
        return CharMatcher.ascii().matchesAllOf(line);
    }

}

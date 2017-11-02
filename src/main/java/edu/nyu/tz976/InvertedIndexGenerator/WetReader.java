package edu.nyu.tz976.InvertedIndexGenerator;

import edu.nyu.tz976.DocIdCounter;
import edu.nyu.tz976.PageUrlTable;
import edu.nyu.tz976.WordCountMap;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WetReader {
//    public List<HashMap<String, Integer>> wordCountMapList = Lists.newArrayList();
    public List<WordCountMap> wordCountMapList = new ArrayList<>();
    public List<List<String>> pageUrlList = new ArrayList<>();

    public void loadWetData(Path path) {
        List<String> contentList = new ArrayList<>();
        List<String> headerList = new ArrayList<>();

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
                        int docId = DocIdCounter.getDocId();
                        if (processDocData(contentList, headerList, docId)) {
                            PageUrlTable.appendPageUrlList(pageUrlList);
                        }

                        contentList = new ArrayList<>();
                        headerList = new ArrayList<>();
                        pageUrlList = new ArrayList<>();
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

    private Boolean processDocData (List<String> contentLines, List<String> headerLines, int docId) {

        // Count term frequency in one doc
        WordCountMap wordCountMap = new WordCountMap(docId, getWordCountMap(contentLines));

        // Filter those pages that are not ascii format
        if (wordCountMap.map.size() != 0) {
            wordCountMapList.add(wordCountMap);

            // Map url and termNum for each doc
            List<String> pageUrlTermNumTuple = pageUrlMapper(headerLines, wordCountMap.map.size(), docId);
            pageUrlList.add(pageUrlTermNumTuple);
            return true;
        }
        return false;
    }

    private List<String> pageUrlMapper(List<String> headerLines, int wordCountMapSize, int docId) {
        List<String> urlTermNumTuple = new ArrayList<>();
        for (String headerLine:headerLines) {
            String[] lineParts = headerLine.split(" ");
            if (lineParts[0].equals("WARC-Target-URI:")) {
                String url = lineParts[1];
                urlTermNumTuple.add(String.valueOf(docId));
                urlTermNumTuple.add(url);
            } else if (lineParts[0].equals("Content-Length:")) {
                String termNum = lineParts[1];
                urlTermNumTuple.add(termNum);
            }
        }
        return urlTermNumTuple;
    }

    private HashMap<String, Integer> getWordCountMap(List<String> docLines) {
        HashMap<String, Integer> wordCountMap = new HashMap<>();
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

//    private boolean isAscii(String line) {
//        return CharMatcher.ascii().matchesAllOf(line);
//    }
    private boolean isAscii(String line) {
        return line.matches("\\A\\p{ASCII}*\\z");
    }

}

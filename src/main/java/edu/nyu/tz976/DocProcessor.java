package edu.nyu.tz976;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.List;
import java.util.stream.IntStream;

public class DocProcessor {
    public List<HashMap<String, Integer>> wordCountMapList = Lists.newArrayList();

    private HashMap<String, Integer> wordCountMap = Maps.newHashMap();

    public void processDocData (List<List<String>> contentList, List<List<String>> headerList) {
        List<List<String>> pageUrlList = Lists.newArrayList();

        IntStream.range(0, contentList.size())
                .forEach(idx -> {
                    List<String> headerLines = headerList.get(idx);
                    List<String> docLines = contentList.get(idx);

                    // Count term frequency in one doc
                    freqCount(docLines);
                    wordCountMapList.add(wordCountMap);

                    // Map url and termNum for each doc
                    List<String> pageUrlTermNumTuple = pageUrlMapper(headerLines);
                    pageUrlList.add(pageUrlTermNumTuple);
                    wordCountMap = Maps.newHashMap();
                });
        PageUrlTable.appendPageUrlList(pageUrlList);
    }

    private List<String> pageUrlMapper(List<String> headerLines) {
        List<String> urlTermNumTuple = Lists.newArrayList();
        headerLines.forEach(headerLine -> {
            String[] lineParts = headerLine.split(" ");
            if (lineParts[0].equals("WARC-Target-URI:")) {
                String url = lineParts[1];
                String termNum = String.valueOf(wordCountMap.size());
                urlTermNumTuple.add(termNum);
                urlTermNumTuple.add(url);
            }
        });
        return urlTermNumTuple;
    }

    private void freqCount(List<String> docLines) {
        docLines.forEach(line -> {
            if (isAscii(line)) {
                wordCount(line);
            }
        });
    }

    private boolean isAscii(String line) {
        return CharMatcher.ascii().matchesAllOf(line);
    }

    private void wordCount(String line) {
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

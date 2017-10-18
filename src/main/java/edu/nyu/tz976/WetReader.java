package edu.nyu.tz976;

import com.google.common.collect.Lists;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class WetReader {
    public List<List<String>> fileContentList = Lists.newArrayList();
    public List<List<String>> fileHeaderList = Lists.newArrayList();

    public void loadWetData(Path path) {

        BufferedReader reader = null;
        int docCounter = -1;

        try {
            File file = path.toFile();
            reader = new BufferedReader(new FileReader(file));
            String contentType = "Header";
            String pageCheck;
            String line;

            while ((line = reader.readLine()) != null) {
                pageCheck = newPageCheck(line, contentType);

                if (pageCheck.equals("Header")) {
                    contentType = "Header";
                    docCounter++;
                } else if (pageCheck.equals("Content")) {
                    contentType = "Content";
                }

                if (contentType.equals("Content")) {
                    if (fileContentList.size() < docCounter + 1) {
                        List<String> pageLineList = Lists.newArrayList();
                        fileContentList.add(pageLineList);
                    }
                    fileContentList.get(fileContentList.size() - 1).add(line);
                } else {
                    if (fileHeaderList.size() < docCounter + 1) {
                        List<String> pageHeaderList = Lists.newArrayList();
                        fileHeaderList.add(pageHeaderList);
                    }
                    fileHeaderList.get(fileHeaderList.size() - 1).add(line);
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
}

package edu.nyu.webSearchEngine.QueryExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageUrlTableLoader {
    public HashMap<String, List<String>> pageUrlTable;
    public int avgLength;

    // Map: (word, (url, num))
    public void loadPageUrlTable() {
        pageUrlTable = new HashMap<>();
        Path pageUrlTablePath = Paths.get("./output/pageUrlTable.txt");
        File pageUrlTableFile = pageUrlTablePath.toFile();

        try {
            BufferedReader pageUrlTableInStream = new BufferedReader(new FileReader(pageUrlTableFile));
            String line;
            int totalLength = 0;
            int pageNum = 0;
            avgLength = 0;
            while ((line = pageUrlTableInStream.readLine()) != null) {
                String[] split = line.split(" ");
                if (split.length == 3) {
                    List<String> list = new ArrayList<>();
                    list.add(split[1]);
                    list.add(split[2]);
                    pageNum++;
                    totalLength += Integer.valueOf(split[2]);
                    pageUrlTable.put(split[0], list);
                }
            }
            avgLength = totalLength/pageNum;
            pageUrlTableInStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

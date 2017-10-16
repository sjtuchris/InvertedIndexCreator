package edu.nyu.tz976;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class InvertedIndexOrchestrator {
    private final static Logger LOGGER = Logger.getLogger(InvertedIndexOrchestrator.class.getName());

    public void test() {
        cleanHistoryFiles();

        WetReader wetReader = new WetReader();
        DocProcessor docProcessor = new DocProcessor();
        OutputUtils outputUtils = new OutputUtils();
        IndexGenerator indexGenerator = new IndexGenerator();

        String fileName = "CC-MAIN-20170919112242-20170919132242-00000.warc.wet";
//        String fileName = "test.warc.wet";

        // Load wet data file and store contents in a list
        wetReader.loadWetData(fileName);
        List<List<String>> headerList = wetReader.fileHeaderList;
        List<List<String>> contentList = wetReader.fileContentList;
        LOGGER.info("Wet data loaded!");

        // Iterate the content list, do the word-doc-freq mapping and page-url-termNum mapping
        docProcessor.processDocData(contentList, headerList);
        List<HashMap<String, Integer>> wordCountMapList = docProcessor.wordCountMapList;
        List<List<String>> pageUrlTable = docProcessor.pageUrlTable;

        // Write intermediatePosting and pageUrlTable to disk
        LOGGER.info("Starting to generate tempPostings");
        outputUtils.writeIntermediatePostings(wordCountMapList);
        LOGGER.info("Postings generated!");
        outputUtils.writePageUrlTable(pageUrlTable);
        LOGGER.info("PageUrlTable generated!");

        // Sort temp postings and export to disk
        unixSort();
        LOGGER.info("Unix sort complete!");

        // Generate inverted index list according to the sorted temp posting file
        indexGenerator.processTempPostings();
        LOGGER.info("Inverted index list complete!");
    }

    private void cleanHistoryFiles() {
        Path tempPostingsPath = Paths.get("./output/tempPostings.txt");
        Path sortedPostingsPath = Paths.get("./output/sortedPostings.txt");
        Path lexiconPath = Paths.get("./output/lexicon.txt");
        Path indexListPath = Paths.get("./output/invertedIndex.txt");
        Path pageUrlTablePath = Paths.get("./output/pageUrlTable.txt");

        try {
            Files.deleteIfExists(tempPostingsPath);
            Files.deleteIfExists(sortedPostingsPath);
            Files.deleteIfExists(lexiconPath);
            Files.deleteIfExists(indexListPath);
            Files.deleteIfExists(pageUrlTablePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unixSort() {
        Runtime rt = Runtime.getRuntime();
        try {
            Process pr = rt.exec(new String[] {"/bin/sh", "./unixSortPostings.sh"});
            pr.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


}

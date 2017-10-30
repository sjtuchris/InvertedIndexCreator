package edu.nyu.tz976;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class InvertedIndexOrchestrator {

    private static final Logger LOGGER = LogManager.getLogger(InvertedIndexOrchestrator.class);
    CountDownLatch countDownLatch;
    public void createInvertedIndexList() {
        cleanHistoryFiles();

        IndexGenerator indexGenerator = new IndexGenerator();

        List<Path> pathList = wetFileList("./input");

        try {
            ExecutorService executor = Executors.newFixedThreadPool(8);
            countDownLatch = new CountDownLatch(pathList.size());

            for (Path path:pathList) {
                Runnable runnable = new MyRunnable<>(path);
                executor.execute(runnable);

            }

            executor.shutdown();

            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // Write intermediatePosting and pageUrlTable to disk

        OutputUtils.writePageUrlTable(PageUrlTable.getPageUrlTable());
        LOGGER.info("PageUrlTable generated!");

        // Sort temp postings and export to disk
        LOGGER.info("Starting unix sort");
        mergeSort();
        LOGGER.info("Unix sort complete!");

        // Generate lexicon and inverted index list according to the sorted temp posting file
        LOGGER.info("Starting generating inverted index list and lexicon");
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

    private void mergeSort() {
        Runtime rt = Runtime.getRuntime();

        try {
            Process pr = rt.exec(new String[] {"/bin/sh", "./src/main/shell/unixSortPostings.sh"});
            pr.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }



//        try {
//            String command =
//                    "cd ./output;" +
//                    "for f in Postings_*.txt ;" +
//                    "do sort -o $f < $f ;" +
//                    "done;" +
//                    "sort -n --merge Postings_*.txt -o sortedPostings.txt;" +
//                    "rm *_*.txt";
//            Process process = new ProcessBuilder("/bin/bash", "-c", command).start();
//            process.waitFor();
//
//        } catch (IOException | InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private List<Path> wetFileList(String directory) {
        List<Path> pathList = new ArrayList<>();
        File dir = new File(directory);
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith((".wet"))) {
                pathList.add(Paths.get(file.getPath()));
            }
        }
        return pathList;
    }

    private class MyRunnable<T> implements Runnable{
        private Path path;

        public MyRunnable(Path t) {
            this.path = t;
        }

        public void run() {
            WetReader wetReader = new WetReader();
            //                    DocProcessor docProcessor = new DocProcessor();

            String fileName = path.getFileName().toString();
            LOGGER.info("Starting to load " + fileName);
            wetReader.loadWetData(path);
            LOGGER.info("Wet data " + fileName + " loaded!");

            // Iterate the content list, do the word-doc-freq mapping and page-url-termNum mapping
            //                    LOGGER.info("Processing " + fileName);
            //                    docProcessor.processDocData(wetReader.fileContentList, wetReader.fileHeaderList);
            //                    LOGGER.info("Process " + fileName + " complete!");

            // Write out temp postings
            LOGGER.info("Generating tempPostings for " + fileName);
            OutputUtils.writeIntermediatePostings(wetReader.wordCountMapList, fileName);

            LOGGER.info("Postings generated!");
            countDownLatch.countDown();
        }
    }
}

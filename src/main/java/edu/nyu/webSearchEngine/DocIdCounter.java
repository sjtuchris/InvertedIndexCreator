package edu.nyu.webSearchEngine;

public class DocIdCounter {
    // Make it atomic
    public static volatile int DOC_ID = 0;

    public static synchronized int getDocId() {
        DOC_ID += 1;
        return DOC_ID -1;
    }
}

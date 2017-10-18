package edu.nyu.tz976;

public class DocIdCounter {
    public static volatile int DOC_ID = 0;

    public static synchronized int getDocId() {
        DOC_ID += 1;
        return DOC_ID -1;
    }
}

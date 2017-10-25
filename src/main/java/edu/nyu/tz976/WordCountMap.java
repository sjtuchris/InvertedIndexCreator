package edu.nyu.tz976;

import java.util.HashMap;

public class WordCountMap {
    public int docId;
    public HashMap<String, Integer> map;

    public WordCountMap(int docId, HashMap<String, Integer> map) {
        this.docId = docId;
        this.map = map;
    }

}

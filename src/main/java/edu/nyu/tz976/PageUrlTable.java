package edu.nyu.tz976;

import java.util.ArrayList;
import java.util.List;

public class PageUrlTable {
    public static volatile List<List<String>> PAGE_URL_TABLE = new ArrayList<>();

    public static synchronized void appendPageUrlTuple(List<String> pageUrlTuple) {
        PAGE_URL_TABLE.add(pageUrlTuple);
    }

    public static synchronized void appendPageUrlList(List<List<String>> pageUrlTuple) {
        PAGE_URL_TABLE.addAll(pageUrlTuple);
    }

    public static List<List<String>> getPageUrlTable() {
        return PAGE_URL_TABLE;
    }
}

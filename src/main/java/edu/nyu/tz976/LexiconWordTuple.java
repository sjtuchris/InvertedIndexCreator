package edu.nyu.tz976;

public class LexiconWordTuple {
    public String startByte;
    public String endByte;
    public String numOfDoc;

    public LexiconWordTuple(String start, String end, String num) {
        startByte = start;
        endByte = end;
        numOfDoc = num;
    }
}

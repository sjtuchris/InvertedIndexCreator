package edu.nyu.tz976;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Scanner;

public class InvertedListSeeker {
    public void seekWord() {
        Scanner reader = new Scanner(System.in);
        System.out.println("Input the start byte: ");
        long start = reader.nextLong();
        System.out.println("Input the end byte: ");
        long end = reader.nextLong();
        reader.close();
        System.out.println("");

        String fileName = "./output/invertedIndex.txt";
        readFromRandomAccessFile(fileName, start, end);
    }

    public void readFromRandomAccessFile(String file, long start, long end) {
        byte[] record = new byte[(int)(end-start+1)];
        try {
            RandomAccessFile fileStore = new RandomAccessFile(file, "rw");
            // moves file pointer to position specified
             fileStore.seek(start);
            // reading String from RandomAccessFile
             fileStore.readFully(record, 0, (int) (end - start));
            showDecode(record);
             fileStore.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showDecode(byte[] line) {
        List<Integer> list = VarByteCompressor.decode(line);
        for (Integer i:list) {
            System.out.print(i);
            System.out.print(" ");
        }
    }
}

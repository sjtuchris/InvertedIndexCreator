package edu.nyu.webSearchEngine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.log;

public class Compressor {
    public static byte[] intToByteArray(int value) {
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(value).array();
    }

    public static int byteArrayToInt(byte[] b) {
        return ByteBuffer.wrap(b).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static byte[] varByteEncode(List<Integer> numbers) {
        // Compress the list first
//        List<Integer> numbers = Compressor.compressIntegerList(inList);

        ByteBuffer buf = ByteBuffer.allocate(numbers.size() * (Integer.SIZE / Byte.SIZE));
        for (Integer number : numbers) {
            buf.put(encodeNumber(number));
        }
        buf.flip();
        byte[] rv = new byte[buf.limit()];
        buf.get(rv);
        return rv;
    }

    public static List<Integer> varByteDecode(byte[] byteStream) {
        List<Integer> numbers = new ArrayList<Integer>();
        int n = 0;
        for (byte b : byteStream) {
            if ((b & 0xff) < 128) {
                n = 128 * n + b;
            } else {
                int num = (128 * n + ((b - 128) & 0xff));
                numbers.add(num);
                n = 0;
            }
        }
        return numbers;
//        return Compressor.decompressIntegerList(numbers);
    }

    private static byte[] encodeNumber(int n) {
        if (n == 0) {
            return new byte[]{0};
        }
        int i = (int) (log(n) / log(128)) + 1;
        byte[] rv = new byte[i];
        int j = i - 1;
        do {
            rv[j--] = (byte) (n % 128);
            n /= 128;
        } while (j >= 0);
        rv[i - 1] += 128;
        return rv;
    }

    // E.g. {1,3,5,8,12} -> {1,2,2,3,4}
    public static List<Integer> compressIntegerList(List<Integer> inList) {
        List<Integer> outList = new ArrayList<>();

        for (int i = 0; i < inList.size(); i++) {
            if (i == 0) {
                outList.add(inList.get(i));
            } else {
                outList.add(inList.get(i) - inList.get(i-1));
            }
        }
        return outList;
    }

    public static List<Integer> decompressIntegerList(List<Integer> inList) {
        List<Integer> outList = new ArrayList<>();

        for (int i = 0; i < inList.size(); i++) {
            if (i == 0) {
                outList.add(inList.get(i));
            } else {
                outList.add(inList.get(i) + outList.get(i-1));
            }
        }
        return outList;
    }
}

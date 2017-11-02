package edu.nyu.tz976;

import com.mongodb.client.MongoCollection;
import edu.nyu.tz976.InvertedIndexGenerator.IndexGenerator;
import edu.nyu.tz976.MongoDBUtils.MongoDBUtil;
import edu.nyu.tz976.QueryExecutor.BMValueComparatorReverse;
import edu.nyu.tz976.QueryExecutor.DocIdWithBmValue;
import edu.nyu.tz976.QueryExecutor.LexiconLoader;
import org.apache.commons.lang3.ArrayUtils;
import org.bson.Document;

import java.util.*;

public class Test {
    public void testMongo() {
        MongoCollection<Document> collection = MongoDBUtil.getMongoCollection();
        Document doc = MongoDBUtil.getRecord(collection, 471);
        String line = doc.values().toString();

        System.out.println(line.substring(20, 40));
    }

    public void test() {
        List<Integer> a = new ArrayList<>();
        a.add(1);
        a.add(2);
        List<Integer> b = new ArrayList<>();
        b.add(3);
        b.add(4);

        byte[] c = {};
        c = ArrayUtils.addAll(Compressor.varByteEncode(a), Compressor.varByteEncode(b));

        List<Integer> d = Compressor.varByteDecode(c);
        for (int i = 0; i<d.size(); i++) {
            System.out.println(d.get(i));
        }
    }

    public void testIndex() {
        IndexGenerator indexGenerator = new IndexGenerator();
        indexGenerator.processTempPostings();
    }

    public void testLexiconLoader() {
        LexiconLoader loader = new LexiconLoader();
        loader.loadLexicon();
        System.out.println(loader.totalDocNum);
    }

    public void testSortedSet() {
        SortedSet<DocIdWithBmValue> set = new TreeSet<>(new BMValueComparatorReverse());
        int[] i = {1,2,3};
        DocIdWithBmValue a1 = new DocIdWithBmValue(1,9,i);
        DocIdWithBmValue a2 = new DocIdWithBmValue(1,6,i);
        DocIdWithBmValue a3 = new DocIdWithBmValue(1,2,i);

        set.add(a1);
        set.add(a2);
        set.add(a3);
        System.out.println(set.last());


    }
    public void testCompress(){
        List<Integer> l1 = new ArrayList<>(Arrays.asList(99,3,6,8,9));
        List<Integer> l2 = Compressor.compressIntegerList(l1);
        for(int i:l2){
            System.out.print(i);
        }
        System.out.println();
        for(int i:Compressor.decompressIntegerList(l2)) {
            System.out.print(i);
        }
    }

//    int setBits (int value)
//    {
//        value |= (value >> 1);
//        value |= (value >> 2);
//        value |= (value >> 4);
//        value |= (value >> 8);
//        value |= (value >> 16);
//        return value;
//    }
//
//    private static byte[] encodeNumber(int n) {
//        if (n == 0) {
//            return new byte[]{0};
//        }
//        int i = (int) (log(n) / log(128)) + 1;
//        byte[] rv = new byte[i];
//        int j = i - 1;
//        do {
//            rv[j--] = (byte) (n % 128);
//            n /= 128;
//        } while (j >= 0);
//        rv[i - 1] += 128;
//        return rv;
//    }
//
//    public static byte[] varByteEncode(List<Integer> numbers) {
//        ByteBuffer buf = ByteBuffer.allocate(numbers.size() * (Integer.SIZE / Byte.SIZE));
//        for (Integer number : numbers) {
//            buf.put(encodeNumber(number));
//        }
//        buf.flip();
//        byte[] rv = new byte[buf.limit()];
//        buf.get(rv);
//        return rv;
//    }
//
//    public static List<Integer> varByteDecode(byte[] byteStream) {
//        List<Integer> numbers = new ArrayList<Integer>();
//        int n = 0;
//        for (byte b : byteStream) {
//            if ((b & 0xff) < 128) {
//                n = 128 * n + b;
//            } else {
//                int num = (128 * n + ((b - 128) & 0xff));
//                numbers.add(num);
//                n = 0;
//            }
//        }
//        return numbers;
//    }
}

package edu.nyu.tz976;

import edu.nyu.tz976.QueryExecutor.QueryProcessOrchestrator;

public class App
{
    public static void main( String[] args )
    {
        System.out.println( "Generator starts!" );
//        InvertedIndexOrchestrator invertedIndexOrchestrator = new InvertedIndexOrchestrator();
//        invertedIndexOrchestrator.createInvertedIndexList();
//        InvertedListSeeker seeker = new InvertedListSeeker();
//        seeker.metaTest();
        QueryProcessOrchestrator queryProcessOrchestrator = new QueryProcessOrchestrator();
        queryProcessOrchestrator.executeQuery();
//        Test createInvertedIndexList = new Test();
//        createInvertedIndexList.testCompress();
    }
}

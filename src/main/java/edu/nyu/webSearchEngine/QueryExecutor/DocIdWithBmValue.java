package edu.nyu.webSearchEngine.QueryExecutor;

import java.util.Comparator;

public class DocIdWithBmValue {
    int docId;
    double bmValue;
    int[] freq;

    public DocIdWithBmValue(int docId, double bmValue, int[] freq) {
        this.docId = docId;
        this.bmValue = bmValue;
        this.freq = freq;
    }

    public class BMValueComparator implements Comparator<DocIdWithBmValue>
    {
        @Override
        public int compare(DocIdWithBmValue x, DocIdWithBmValue y)
        {
            if (x.bmValue < y.bmValue)
            {
                return -1;
            }
            if (x.bmValue > y.bmValue)
            {
                return 1;
            }
            return 0;
        }
    }
}

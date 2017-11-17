package edu.nyu.webSearchEngine.QueryExecutor;

import java.util.Comparator;

public class BMValueComparatorReverse implements Comparator<DocIdWithBmValue>
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

package edu.nyu.tz976.QueryExecutor;

import java.util.Comparator;

public class BMValueComparator implements Comparator<DocIdWithBmValue>
{
    @Override
    public int compare(DocIdWithBmValue x, DocIdWithBmValue y)
    {
        if (x.bmValue > y.bmValue)
        {
            return -1;
        }
        if (x.bmValue < y.bmValue)
        {
            return 1;
        }
        return 0;
    }
}

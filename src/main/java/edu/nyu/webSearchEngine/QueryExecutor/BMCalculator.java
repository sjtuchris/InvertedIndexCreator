package edu.nyu.webSearchEngine.QueryExecutor;

import java.util.List;

public class BMCalculator {
    public static double getBMValue(int N, List<InvertedIndexMeta> metaList, int[] fd, int d, int davg) {
        double k1 = 1.2;
        double b = 0.75;
        double bmValue = 0;
        double K = k1*((1-b)+b*Math.abs(d)/Math.abs(davg));

        for (int i=0; i<metaList.size(); i++) {
            InvertedIndexMeta meta = metaList.get(i);
            int ft = Integer.valueOf(meta.numOfDocContainTerm);
            int fdt = fd[i];
            bmValue += Math.log((N-ft+0.5)/(ft+0.5)) * (k1+1)*fdt/(K+fdt);
        }
        return bmValue;
    }
}

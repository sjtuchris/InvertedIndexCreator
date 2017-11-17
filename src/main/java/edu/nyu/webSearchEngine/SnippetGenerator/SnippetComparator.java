package edu.nyu.webSearchEngine.SnippetGenerator;


import java.util.Comparator;

public class SnippetComparator implements Comparator<SnippetCandidate>
{
    @Override
    public int compare(SnippetCandidate x, SnippetCandidate y)
    {
        if (x.score > y.score)
        {
            return -1;
        }
        if (x.score < y.score)
        {
            return 1;
        }
        return 0;
    }
}

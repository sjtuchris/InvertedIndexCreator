package edu.nyu.webSearchEngine.SnippetGenerator;

import java.util.*;

public class SnippetUtils {
    public static List<String> generateSnippets(String content, List<String> words){
        int snippetLength = 200;
        List<String> snippets = new ArrayList<>();

        // Too short, just return the whole content
        if (content.length() < snippetLength) {
            snippets.add(content);
            return snippets;
        }

        PriorityQueue<SnippetCandidate> snippetQueue = new PriorityQueue<>(11, new SnippetComparator());

        // Sliding window across the whole content. Snippet contains the most keywords would be on the top of the queue
        int pointer = 0;
        while (pointer < content.length()-snippetLength) {
            String snippet = content.substring(pointer, pointer+snippetLength);
            int score = calculateScore(snippet, words);
            snippetQueue.add(new SnippetCandidate("..."+snippet+"...", score));

            pointer += snippetLength;
        }

        // Last snippet, corner case
        if (pointer < content.length()-1) {
            String snippet = content.substring(pointer, content.length()-1);
            int score = calculateScore(snippet, words);
            snippetQueue.add(new SnippetCandidate("..."+snippet+"...", score));
        }

        // Pick up the top 3 snippets
        int i = 0;
        while (i<3 && !snippetQueue.isEmpty()) {
            snippets.add(snippetQueue.poll().content);
            i++;
        }

        return snippets;
    }

    private static int calculateScore(String snippet, List<String> words) {
        int score = 0;
        for (String word:words) {
            if (snippet.contains(word)) {
                if (score > 0) {
                    score += 2;
                } else {
                    score++;
                }
            }
        }
        return score;
    }

}

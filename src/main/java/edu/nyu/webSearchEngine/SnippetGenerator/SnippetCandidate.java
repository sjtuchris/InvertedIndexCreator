package edu.nyu.webSearchEngine.SnippetGenerator;

public class SnippetCandidate {
    String content;
    int score;

    public SnippetCandidate(String content, int score) {
        this.content = content;
        this.score = score;
    }
}

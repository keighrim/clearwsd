package io.github.clearwsd.corpus;

import io.github.clearwsd.parser.NlpParser;
import io.github.clearwsd.type.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AnchoredTextCorpusReader extends TextCorpusReader {

    public AnchoredTextCorpusReader(NlpParser parser) {
        super(parser);
    }

    @Override
    public List<DepTree> readInstances(InputStream inputStream) {
        List<DepTree> results = new ArrayList<>();
        int chunkStart = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // +1 for linebreaker
                int chunkEnd = chunkStart + line.length() + 1;
                if (line.trim().length() > 0) {
                    int sentenceStart = -1;
                    int sentenceEnd = 0;
                    for (String sentence : parser.segment(line)) {
                        sentence = sentence.trim();
                        sentenceStart = line.indexOf(sentence, sentenceEnd);
                        sentenceEnd = sentenceStart + sentence.length();

                        List<String> tokenized = parser.tokenize(sentence);

                        DepTree tree = parser.parse(tokenized);
                        int tokenStart = -1;
                        int tokenEnd = 0;
                        List<DepNode> nodes = new ArrayList<>();
                        for (DepNode token : tree.tokens()) {
                            String tokenText = token.feature(FeatureType.Text);
                            tokenStart = sentence.indexOf(tokenText, tokenEnd);
                            tokenEnd = tokenStart + tokenText.length();
                            AnchoredDepNode anchoredToken = new AnchoredDepNode(token);
                            anchoredToken.setStart(tokenStart + sentenceStart + chunkStart);
                            anchoredToken.setEnd(tokenEnd + sentenceStart + chunkStart);

                            nodes.add(anchoredToken);

                        }
                        AnchoredDepTree anchoredTree = new AnchoredDepTree(
                                tree.index(),
                                nodes,
                                tree.root(),
                                sentenceStart + chunkStart,
                                sentenceEnd + chunkStart);
                        anchoredTree.addFeature(FeatureType.Text, sentence);
                        results.add(anchoredTree);
                    }
                }
                chunkStart = chunkEnd;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return results;
    }
}

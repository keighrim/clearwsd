/*
 * Copyright 2017 James Gung
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.clearwsd.corpus.semlink;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.clearwsd.corpus.CoNllDepTreeReader;
import io.github.clearwsd.corpus.CorpusReader;
import io.github.clearwsd.type.*;
import io.github.clearwsd.utils.SenseInventory;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static io.github.clearwsd.corpus.CoNllDepTreeReader.treeToString;
import static io.github.clearwsd.type.FeatureType.Gold;
import static io.github.clearwsd.type.FeatureType.Lemma;
import static io.github.clearwsd.type.FeatureType.Metadata;
import static io.github.clearwsd.type.FeatureType.Predicate;
import static io.github.clearwsd.type.FeatureType.Sense;
import static io.github.clearwsd.type.FeatureType.Text;

/**
 * VerbNet (parsed) corpus reader.
 *
 * @author jamesgung
 */
public class VerbNetReader implements CorpusReader<NlpFocus<DepNode, DepTree>> {

    private VerbNetCoNllDepReader depReader = new VerbNetCoNllDepReader();

    @Override
    public List<NlpFocus<DepNode, DepTree>> readInstances(InputStream inputStream, Set<String> filter) {
        List<NlpFocus<DepNode, DepTree>> results = new ArrayList<>();
        int index = 0;
        for (DepTree tree : depReader.readInstances(inputStream)) {
            for (DepNode focus : tree.tokens().stream()
                .filter(t -> t.feature(Gold) != null)
                .collect(Collectors.toList())) {
                if (!filter.isEmpty() && !filter.contains(focus.feature(Lemma).toString())) {
                    continue;
                }

                NlpFocus<DepNode, DepTree> instance = new DefaultNlpFocus<>(index++, focus, tree);

                String label = focus.feature(Gold);
                if (label.equals(SenseInventory.DEFAULT_SENSE)) {
                    continue;
                }
                instance.addFeature(Gold, label);
                results.add(instance);
            }
        }
        return results;
    }

    @Override
    public List<NlpFocus<DepNode, DepTree>> readInstances(InputStream inputStream) {
        return readInstances(inputStream, Collections.emptySet());
    }

    @Override
    public void writeInstances(List<NlpFocus<DepNode, DepTree>> instances, OutputStream outputStream) {
        try (PrintWriter writer = new PrintWriter(outputStream)) {
            if (instances.size() == 0) {
                return;
            }
            DepTree currentTree = instances.get(0).sequence();
            for (NlpFocus<DepNode, DepTree> instance : instances) {
                if (instance.sequence() != currentTree) {
                    writer.println(treeToString(currentTree, Sense.name()));
                    writer.println();
                    currentTree = instance.sequence();
                }
                String metadata = instance.feature(Metadata);
                if (metadata == null) {
                    writer.println("# " + VerbNetInstanceParser.toString(new VerbNetInstance()
                        .path(Integer.toString(instance.index()))
                        .label(Optional.<String>ofNullable(instance.focus().feature(Gold))
                            .orElse(instance.focus().feature(Sense)))
                        .sentence(instance.sequence().index())
                        .sentenceStart(instance.sequence() instanceof AnchoredDepTree ? ((AnchoredDepTree) instance.sequence()).getStart() : -1)
                        .sentenceEnd(instance.sequence() instanceof AnchoredDepTree ? ((AnchoredDepTree) instance.sequence()).getEnd() : -1)
                        .token(instance.focus().index())
                        .tokenStart(instance.focus() instanceof AnchoredDepNode ? ((AnchoredDepNode) instance.focus()).getStart() : -1)
                        .tokenEnd(instance.focus() instanceof AnchoredDepNode ? ((AnchoredDepNode) instance.focus()).getEnd() : -1)
                        .lemma(instance.focus().feature(Predicate))
                        .originalText(Optional.<String>ofNullable(instance.sequence().feature(Text)).orElse(
                            currentTree.tokens().stream().map(t -> (String) t.feature(Text))
                                .collect(Collectors.joining(" "))))));
                } else {
                    writer.println("# " + metadata);
                }
                writer.flush();
            }
            writer.println(treeToString(currentTree, Sense.name()));
        }
    }

    public static class VerbNetCoNllDepReader extends CoNllDepTreeReader {

        @Override
        protected void processHeader(List<String> header, DepTree result) {
            for (String headerLine : header) {
                headerLine = headerLine.replaceAll("^#\\s*", "");
                VerbNetInstance instance = new VerbNetInstanceParser().parse(headerLine);
                DepNode focus = result.get(instance.token);
                focus.addFeature(Gold, instance.label);
                focus.addFeature(Sense, instance.label);
                focus.addFeature(Predicate, instance.lemma);
                result.addFeature(Text, instance.originalText);
            }
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true)
    public static class VerbNetInstance {

        private String path;
        private int sentence;
        private int sentenceStart = -1;
        private int sentenceEnd = -1;
        private int token;
        private int tokenStart = -1;
        private int tokenEnd = -1;
        private String lemma;
        private String label;
        private String originalText;

        public boolean isAnchored() {
            return tokenStart > 0 && tokenEnd > 0 && sentenceStart > 0 && sentenceEnd > 0;
        }
    }

    public static class VerbNetInstanceParser {

        public VerbNetInstance parse(String input) {
            String[] fields = input.split("\t");
            String[] subFields = fields[0].split(" ");
            return new VerbNetInstance()
                .path(subFields[0])
                .sentence(Integer.parseInt(subFields[1]))
                .token(Integer.parseInt(subFields[2]))
                .lemma(subFields[3])
                .label(subFields[4].replaceAll("-\\S+", ""))
                .originalText(fields[1]);
        }

        public static String toString(VerbNetInstance instance) {
            String sentenceString = instance.isAnchored() ? String.format("%d[%d,%d]", instance.sentence, instance.sentenceStart, instance.sentenceEnd) : Integer.toString(instance.sentence);
            String tokenString = instance.isAnchored() ? String.format("%d[%d,%d]", instance.token, instance.tokenStart, instance.tokenEnd) : Integer.toString(instance.token);
            return String.format("%s %s %s %s %s\t%s", instance.path, sentenceString, tokenString,
                instance.lemma, instance.label, instance.originalText);
        }
    }

}

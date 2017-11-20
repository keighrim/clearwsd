package edu.colorodo.clear.wsd.feature.annotator;

import org.junit.Test;

import java.io.IOException;
import java.util.Collections;

import edu.colorodo.clear.wsd.feature.TestInstanceBuilder;
import edu.colorodo.clear.wsd.feature.extractor.LookupFeatureExtractor;
import edu.colorodo.clear.wsd.feature.resource.MultimapResource;
import edu.colorodo.clear.wsd.type.DepNode;
import edu.colorodo.clear.wsd.type.DependencyTree;
import edu.colorodo.clear.wsd.type.FeatureType;
import edu.colorodo.clear.wsd.type.FocusInstance;

import static junit.framework.TestCase.assertEquals;

/**
 * @author jamesgung
 */
public class ListAnnotatorTest {

    private FocusInstance<DepNode, DependencyTree> getTestInstance() {
        return new TestInstanceBuilder("the fox jumped over the fence", 2)
                .addHead(0, 1, "det")
                .addHead(1, 2, "nsubj")
                .addHead(3, 5, "prep")
                .addHead(4, 5, "det")
                .addHead(5, 2, "nmod")
                .root(2)
                .build();
    }

    @Test
    public void testAnnotate() throws IOException {
        MultimapResource<String> testResource = new MultimapResource<>("testResource");
        //noinspection ConstantConditions
        testResource.initialize(getClass().getClassLoader().getResource("test_resource.tsv").openStream());
        ListAnnotator<DepNode, FocusInstance<DepNode, DependencyTree>> annotator = new ListAnnotator<>(
                new LookupFeatureExtractor<DepNode>(FeatureType.Text.name()),
                "testResource", testResource);
        FocusInstance<DepNode, DependencyTree> annotated = annotator.annotate(getTestInstance());
        assertEquals(Collections.singletonList("noun"), annotated.get(1).feature("testResource"));
        assertEquals(Collections.singletonList("verb"), annotated.get(2).feature("testResource"));
        assertEquals(Collections.singletonList("noun"), annotated.get(5).feature("testResource"));
    }

}
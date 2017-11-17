package edu.colorodo.clear.wsd.feature.function;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import edu.colorodo.clear.wsd.feature.StringFeature;
import edu.colorodo.clear.wsd.feature.context.NlpContext;
import edu.colorodo.clear.wsd.feature.context.NlpContextFactory;
import edu.colorodo.clear.wsd.feature.extractor.FeatureExtractor;
import edu.colorodo.clear.wsd.feature.util.FeatureUtils;
import edu.colorodo.clear.wsd.type.NlpInstance;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * String feature function.
 *
 * @author jamesgung
 */
@NoArgsConstructor
@AllArgsConstructor
public class StringFeatureFunction<InputT extends NlpInstance, OutputT extends NlpInstance>
        implements FeatureFunction<InputT> {

    @JsonProperty
    private NlpContextFactory<InputT, OutputT> contextFactory;
    @JsonProperty
    private List<FeatureExtractor<OutputT, String>> featureExtractors;

    @Override
    public List<StringFeature> apply(InputT instance) {
        List<StringFeature> features = new ArrayList<>();
        for (NlpContext<OutputT> context : contextFactory.apply(instance)) {
            for (FeatureExtractor<OutputT, String> featureExtractor : featureExtractors) {
                List<String> results = new ArrayList<>();
                for (OutputT token : context.tokens()) {
                    results.add(featureExtractor.extract(token));
                }
                String id = FeatureUtils.computeId(context.identifier(), featureExtractor.id());
                features.add(new StringFeature(id, String.join(FeatureUtils.CONTEXT_DELIM, results)));
            }
        }
        return features;
    }

}

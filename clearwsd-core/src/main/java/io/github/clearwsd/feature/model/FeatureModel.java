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

package io.github.clearwsd.feature.model;

import java.io.Serializable;

/**
 * Serializable feature model.
 *
 * @author jamesgung
 */
public interface FeatureModel extends Serializable {

    /**
     * Feature indices.
     */
    Vocabulary features();

    /**
     * Update features vocabulary.
     *
     * @param features new features
     */
    FeatureModel features(Vocabulary features);

    /**
     * Label indices.
     */
    Vocabulary labels();

    /**
     * Update label vocabulary.
     *
     * @param labels new labels
     */
    FeatureModel labels(Vocabulary labels);

    /**
     * Get the label for a specified index.
     *
     * @param index label index
     * @return label
     */
    String label(int index);

    /**
     * Return the index for a given label
     *
     * @param label input label
     * @return label index
     */
    Integer labelIndex(String label);

    /**
     * Return the feature for a given index.
     *
     * @param index feature index
     * @return feature
     */
    String feature(int index);

    /**
     * Return the index for a given feature.
     *
     * @param feature feature string
     * @return feature index
     */
    Integer featureIndex(String feature);

}

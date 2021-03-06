/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 * 
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.ml.composition.boosting;

import org.apache.ignite.ml.composition.boosting.loss.SquaredError;
import org.apache.ignite.ml.dataset.DatasetBuilder;
import org.apache.ignite.ml.environment.LearningEnvironmentBuilder;
import org.apache.ignite.ml.preprocessing.Preprocessor;

/**
 * Trainer for regressor using Gradient Boosting. This algorithm uses gradient of Mean squared error loss metric [MSE]
 * in each step of learning.
 */
public abstract class GDBRegressionTrainer extends GDBTrainer {
    /**
     * Constructs instance of GDBRegressionTrainer.
     *
     * @param gradStepSize Grad step size.
     * @param cntOfIterations Count of learning iterations.
     */
    public GDBRegressionTrainer(double gradStepSize, Integer cntOfIterations) {
        super(gradStepSize, cntOfIterations, new SquaredError());
    }

    /** {@inheritDoc} */
    @Override protected <V, K> boolean learnLabels(DatasetBuilder<K, V> builder,
                                                   Preprocessor<K, V> preprocessor) {

        return true;
    }

    /** {@inheritDoc} */
    @Override protected double externalLabelToInternal(double x) {
        return x;
    }

    /** {@inheritDoc} */
    @Override protected double internalLabelToExternal(double x) {
        return x;
    }

    /** {@inheritDoc} */
    @Override public GDBRegressionTrainer withEnvironmentBuilder(LearningEnvironmentBuilder envBuilder) {
        return (GDBRegressionTrainer)super.withEnvironmentBuilder(envBuilder);
    }
}

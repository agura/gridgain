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

package org.apache.ignite.ml.tree.impurity;

import java.io.Serializable;

/**
 * Base interface for impurity measures that can be used in distributed decision tree algorithm.
 *
 * @param <T> Type of this impurity measure.
 */
public interface ImpurityMeasure<T extends ImpurityMeasure<T>> extends Comparable<T>, Serializable {
    /**
     * Calculates impurity measure as a single double value.
     *
     * @return Impurity measure value.
     */
    public double impurity();

    /**
     * Adds the given impurity to this.
     *
     * @param measure Another impurity.
     * @return Sum of this and the given impurity.
     */
    public T add(T measure);

    /**
     * Subtracts the given impurity for this.
     *
     * @param measure Another impurity.
     * @return Difference of this and the given impurity.
     */
    public T subtract(T measure);

    /** {@inheritDoc} */
    @Override public default int compareTo(T o) {
        return Double.compare(impurity(), o.impurity());
    }
}

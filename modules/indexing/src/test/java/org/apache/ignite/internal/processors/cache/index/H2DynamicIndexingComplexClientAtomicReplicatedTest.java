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

package org.apache.ignite.internal.processors.cache.index;

import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;

/**
 * Test to check work of DML+DDL operations of atomic replicated cache with queries initiated from client node.
 */
public class H2DynamicIndexingComplexClientAtomicReplicatedTest extends H2DynamicIndexingComplexAbstractTest {
    /**
     * Constructor.
     */
    public H2DynamicIndexingComplexClientAtomicReplicatedTest() {
        super(CacheMode.REPLICATED, CacheAtomicityMode.ATOMIC, 1, CLIENT_IDX);
    }
}

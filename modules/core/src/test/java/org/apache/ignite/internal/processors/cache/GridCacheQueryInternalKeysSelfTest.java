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

package org.apache.ignite.internal.processors.cache;

import java.util.Collection;
import java.util.UUID;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.IgniteKernal;
import org.apache.ignite.internal.processors.cache.distributed.near.GridNearCacheAdapter;
import org.apache.ignite.internal.processors.datastructures.GridCacheQueueHeaderKey;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.internal.util.typedef.P1;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.apache.ignite.cache.CacheMode.PARTITIONED;
import static org.apache.ignite.cache.CacheRebalanceMode.SYNC;

/**
 * Cache query internal keys self test.
 */
public class GridCacheQueryInternalKeysSelfTest extends GridCacheAbstractSelfTest {
    /** Grid count. */
    private static final int GRID_CNT = 2;

    /** Entry count. */
    private static final int ENTRY_CNT = 10;

    /** {@inheritDoc} */
    @Override protected int gridCount() {
        return GRID_CNT;
    }

    /** {@inheritDoc} */
    @Override protected CacheMode cacheMode() {
        return PARTITIONED;
    }

    /** {@inheritDoc} */
    @Override protected CacheConfiguration cacheConfiguration(String igniteInstanceName) throws Exception {
        CacheConfiguration cc = super.cacheConfiguration(igniteInstanceName);

        cc.setRebalanceMode(SYNC);

        return cc;
    }

    /**
     * @throws Exception If failed.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testInternalKeysPreloading() throws Exception {
        try {
            IgniteCache<Object, Object> cache = grid(0).cache(DEFAULT_CACHE_NAME);

            for (int i = 0; i < ENTRY_CNT; i++)
                cache.put(new GridCacheQueueHeaderKey("queue" + i), 1);

            startGrid(GRID_CNT); // Start additional node.

            for (int i = 0; i < ENTRY_CNT; i++) {
                GridCacheQueueHeaderKey internalKey = new GridCacheQueueHeaderKey("queue" + i);

                Collection<ClusterNode> nodes = grid(0).affinity(DEFAULT_CACHE_NAME).mapKeyToPrimaryAndBackups(internalKey);

                for (ClusterNode n : nodes) {
                    Ignite g = findGridForNodeId(n.id());

                    assertNotNull(g);

                    assertTrue("Affinity node doesn't contain internal key [key=" + internalKey + ", node=" + n + ']',
                        ((GridNearCacheAdapter)((IgniteKernal)g).internalCache(DEFAULT_CACHE_NAME)).dht().containsKey(internalKey));
                }
            }
        }
        finally {
            stopGrid(GRID_CNT);
        }
    }

    /**
     * Finds the {@link org.apache.ignite.Ignite}, which has a local node
     * with given ID.
     *
     * @param nodeId ID for grid's local node.
     * @return A grid instance or {@code null}, if the grid
     * is not found.
     */
    @Nullable private Ignite findGridForNodeId(final UUID nodeId) {
        return F.find(G.allGrids(), null, new P1<Ignite>() {
            @Override public boolean apply(Ignite e) {
                return nodeId.equals(e.cluster().localNode().id());
            }
        });
    }
}

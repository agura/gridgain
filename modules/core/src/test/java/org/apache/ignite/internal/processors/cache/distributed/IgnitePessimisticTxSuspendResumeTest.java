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

package org.apache.ignite.internal.processors.cache.distributed;

import java.util.concurrent.Callable;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteTransactions;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.testframework.GridTestUtils;
import org.apache.ignite.testframework.MvccFeatureChecker;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.apache.ignite.transactions.Transaction;
import org.apache.ignite.transactions.TransactionConcurrency;
import org.apache.ignite.transactions.TransactionIsolation;
import org.junit.Test;

import static org.apache.ignite.cache.CacheMode.PARTITIONED;

/**
 *
 */
public class IgnitePessimisticTxSuspendResumeTest extends GridCommonAbstractTest {
    /**
     * Creates new cache configuration.
     *
     * @return CacheConfiguration New cache configuration.
     */
    protected CacheConfiguration<Integer, String> getCacheConfiguration() {
        CacheConfiguration<Integer, String> cacheCfg = defaultCacheConfiguration();

        cacheCfg.setCacheMode(PARTITIONED);

        return cacheCfg;
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);

        cfg.setClientMode(false);
        cfg.setCacheConfiguration(getCacheConfiguration());

        return cfg;
    }

    /**
     * Test for suspension on pessimistic transaction.
     *
     * @throws Exception If failed.
     */
    @Test
    public void testSuspendPessimisticTx() throws Exception {
        try (Ignite g = startGrid()) {
            IgniteCache<Integer, String> cache = jcache();

            IgniteTransactions txs = g.transactions();

            for (TransactionIsolation isolation : TransactionIsolation.values()) {
                if (MvccFeatureChecker.forcedMvcc() &&
                    !MvccFeatureChecker.isSupported(TransactionConcurrency.PESSIMISTIC, isolation))
                    continue;

                final Transaction tx = txs.txStart(TransactionConcurrency.PESSIMISTIC, isolation);

                cache.put(1, "1");

                GridTestUtils.assertThrowsWithCause(new Callable<Object>() {
                    @Override public Object call() throws Exception {
                        tx.suspend();

                        return null;
                    }
                }, UnsupportedOperationException.class);

                tx.close();

                assertNull(cache.get(1));
            }
        }
    }
}

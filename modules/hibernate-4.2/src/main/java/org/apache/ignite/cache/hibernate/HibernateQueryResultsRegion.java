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

package org.apache.ignite.cache.hibernate;

import org.apache.ignite.Ignite;
import org.hibernate.Query;
import org.hibernate.cache.spi.QueryResultsRegion;

/**
 * Implementation of {@link QueryResultsRegion}. This region is used to store query results.
 * <p>
 * Query results caching can be enabled in the Hibernate configuration file:
 * <pre name="code" class="xml">
 * &lt;hibernate-configuration&gt;
 *     &lt;!-- Enable L2 cache. --&gt;
 *     &lt;property name="cache.use_second_level_cache"&gt;true&lt;/property&gt;
 *
 *     &lt;!-- Enable query cache. --&gt;
 *     &lt;property name="cache.use_second_level_cache"&gt;true&lt;/property&gt;

 *     &lt;!-- Use Ignite as L2 cache provider. --&gt;
 *     &lt;property name="cache.region.factory_class"&gt;org.apache.ignite.cache.hibernate.HibernateRegionFactory&lt;/property&gt;
 *
 *     &lt;!-- Specify entity. --&gt;
 *     &lt;mapping class="com.example.Entity"/&gt;
 *
 *     &lt;!-- Enable L2 cache with nonstrict-read-write access strategy for entity. --&gt;
 *     &lt;class-cache class="com.example.Entity" usage="nonstrict-read-write"/&gt;
 * &lt;/hibernate-configuration&gt;
 * </pre>
 * By default queries are not cached even after enabling query caching, to enable results caching for a particular
 * query, call {@link Query#setCacheable(boolean)}:
 * <pre name="code" class="java">
 *     Session ses = getSession();
 *
 *     Query qry = ses.createQuery("...");
 *
 *     qry.setCacheable(true); // Enable L2 cache for query.
 * </pre>
 * Note: the query cache does not cache the state of the actual entities in the cache, it caches only identifier
 * values. For this reason, the query cache should always be used in conjunction with
 * the second-level cache for those entities expected to be cached as part of a query result cache
 */
class HibernateQueryResultsRegion extends HibernateGeneralDataRegion implements QueryResultsRegion {
    /**
     * @param factory Region factory.
     * @param name Region name.
     * @param ignite Grid.
     * @param cache Region cache.
     */
    HibernateQueryResultsRegion(HibernateRegionFactory factory, String name,
        Ignite ignite, HibernateCacheProxy cache) {
        super(factory, name, ignite, cache);
    }
}
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

package org.apache.ignite.cache.spring;

import java.io.Serializable;
import java.util.concurrent.Callable;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteLock;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

/**
 * Spring cache implementation.
 */
class SpringCache implements Cache {
    /** */
    private static final Object NULL = new NullValue();

    /** */
    private final IgniteCache<Object, Object> cache;

    /** */
    private final SpringCacheManager mgr;

    /**
     * @param cache Cache.
     * @param mgr Manager
     */
    SpringCache(IgniteCache<Object, Object> cache, SpringCacheManager mgr) {
        assert cache != null;

        this.cache = cache;
        this.mgr = mgr;
    }

    /** {@inheritDoc} */
    @Override public String getName() {
        return cache.getName();
    }

    /** {@inheritDoc} */
    @Override public Object getNativeCache() {
        return cache;
    }

    /** {@inheritDoc} */
    @Override public ValueWrapper get(Object key) {
        Object val = cache.get(key);

        return val != null ? fromValue(val) : null;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override public <T> T get(Object key, Class<T> type) {
        Object val = cache.get(key);

        if (NULL.equals(val))
            val = null;

        if (val != null && type != null && !type.isInstance(val))
            throw new IllegalStateException("Cached value is not of required type [cacheName=" + cache.getName() +
                ", key=" + key + ", val=" + val + ", requiredType=" + type + ']');

        return (T)val;
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override public <T> T get(final Object key, final Callable<T> valLdr) {
        Object val = cache.get(key);

        if (val == null) {
            IgniteLock lock = mgr.getSyncLock(cache.getName(), key);

            lock.lock();

            try {
                val = cache.get(key);

                if (val == null) {
                    try {
                        T retVal = valLdr.call();

                        val = wrapNull(retVal);

                        cache.put(key, val);
                    }
                    catch (Exception e) {
                        throw new ValueRetrievalException(key, valLdr, e);
                    }
                }
            }
            finally {
                lock.unlock();
            }
        }

        return (T)unwrapNull(val);
    }

    /** {@inheritDoc} */
    @Override public void put(Object key, Object val) {
        if (val == null)
            cache.withSkipStore().put(key, NULL);
        else
            cache.put(key, val);
    }

    /** {@inheritDoc} */
    @Override public ValueWrapper putIfAbsent(Object key, Object val) {
        Object old;

        if (val == null)
            old = cache.withSkipStore().getAndPutIfAbsent(key, NULL);
        else
            old = cache.getAndPutIfAbsent(key, val);

        return old != null ? fromValue(old) : null;
    }

    /** {@inheritDoc} */
    @Override public void evict(Object key) {
        cache.remove(key);
    }

    /** {@inheritDoc} */
    @Override public void clear() {
        cache.removeAll();
    }

    /**
     * @param val Cache value.
     * @return Wrapped value.
     */
    private static ValueWrapper fromValue(Object val) {
        assert val != null;

        return new SimpleValueWrapper(unwrapNull(val));
    }

    private static Object unwrapNull(Object val) {
        return NULL.equals(val) ? null : val;
    }

    private <T> Object wrapNull(T val) {
        return val == null ? NULL : val;
    }

    /** */
    private static class NullValue implements Serializable {
        /** {@inheritDoc} */
        @Override public boolean equals(Object o) {
            return this == o || (o != null && getClass() == o.getClass());
        }
    }
}

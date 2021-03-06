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

namespace Apache.Ignite.Core.Cache.Store
{
    using System;
    using System.Collections.Generic;
    using System.Diagnostics.CodeAnalysis;
    using System.Threading.Tasks;

    /// <summary>
    /// Cache storage adapter with parallel loading in LoadAll method. 
    /// </summary>
    /// <remarks>
    /// LoadCache calls GetInputData() and iterates over it in parallel.
    /// GetInputData().GetEnumerator() result will be disposed if it implements IDisposable.
    /// Any additional post-LoadCache steps can be performed by overriding LoadCache method.
    /// </remarks>
    /// <typeparam name="TK">Key type.</typeparam>
    /// <typeparam name="TV">Value type.</typeparam>
    /// <typeparam name="TData">Custom data entry type.</typeparam>
    public abstract class CacheParallelLoadStoreAdapter<TK, TV, TData> : ICacheStore<TK, TV>
    {
        /// <summary>
        /// Constructor.
        /// </summary>
        protected CacheParallelLoadStoreAdapter()
        {
            MaxDegreeOfParallelism = Environment.ProcessorCount;
        }

        /// <summary>
        /// Loads all values from underlying persistent storage. Note that keys are
        /// not passed, so it is up to implementation to figure out what to load.
        /// This method is called whenever <see cref="ICache{K,V}.LocalLoadCache" />
        /// method is invoked which is usually to preload the cache from persistent storage.
        /// <para />
        /// This method is optional, and cache implementation
        /// does not depend on this method to do anything.
        /// <para />
        /// For every loaded value method provided action should be called.
        /// The action will then make sure that the loaded value is stored in cache.
        /// </summary>
        /// <param name="act">Action for loaded values.</param>
        /// <param name="args">Optional arguemnts passed to <see cref="ICache{K,V}.LocalLoadCache" /> method.</param>
        /// <exception cref="CacheStoreException" />
        public virtual void LoadCache(Action<TK, TV> act, params object[] args)
        {
            if (MaxDegreeOfParallelism == 0 || MaxDegreeOfParallelism < -1)
                throw new ArgumentOutOfRangeException("MaxDegreeOfParallelism must be either positive or -1: " +
                                                      MaxDegreeOfParallelism);

            var options = new ParallelOptions {MaxDegreeOfParallelism = MaxDegreeOfParallelism};

            Parallel.ForEach(GetInputData(), options, item =>
            {
                var cacheEntry = Parse(item, args);

                if (cacheEntry != null)
                    act(cacheEntry.Value.Key, cacheEntry.Value.Value);
            });
        }

        /// <summary>
        /// Gets the input data sequence to be used in LoadCache.
        /// </summary>
        [SuppressMessage("Microsoft.Design", "CA1024:UsePropertiesWhereAppropriate", Justification = "Semantics.")]
        protected abstract IEnumerable<TData> GetInputData();

        /// <summary>
        /// This method should transform raw data records from GetInputData
        /// into valid key-value pairs to be stored into cache.        
        /// </summary>
        protected abstract KeyValuePair<TK, TV>? Parse(TData inputRecord, params object[] args);

        /// <summary>
        /// Gets or sets the maximum degree of parallelism to use in LoadCache. 
        /// Must be either positive or -1 for unlimited amount of threads.
        /// <para />
        /// Defaults to <see cref="Environment.ProcessorCount"/>.
        /// </summary>
        public int MaxDegreeOfParallelism { get; set; }

        /// <summary>
        /// Loads an object. Application developers should implement this method to customize the loading
        /// of a value for a cache entry.
        /// This method is called by a cache when a requested entry is not in the cache.
        /// If the object can't be loaded <c>null</c> should be returned.
        /// </summary>
        /// <param name="key">The key identifying the object being loaded.</param>
        /// <returns>
        /// The value for the entry that is to be stored in the cache
        /// or <c>null</c> if the object can't be loaded
        /// </returns>
        [ExcludeFromCodeCoverage]
        public virtual TV Load(TK key)
        {
            return default(TV);
        }

        /// <summary>
        /// Loads multiple objects. Application developers should implement this method to customize
        /// the loading of cache entries. This method is called when the requested object is not in the cache.
        /// If an object can't be loaded, it is not returned in the resulting map.
        /// </summary>
        /// <param name="keys">Keys identifying the values to be loaded.</param>
        /// <returns>
        /// A map of key, values to be stored in the cache.
        /// </returns>
        [ExcludeFromCodeCoverage]
        public virtual IEnumerable<KeyValuePair<TK, TV>> LoadAll(IEnumerable<TK> keys)
        {
            return null;
        }

        /// <summary>
        /// Write the specified value under the specified key to the external resource.
        /// <para />
        /// This method is intended to support both key/value creation and value update.
        /// </summary>
        /// <param name="key">Key to write.</param>
        /// <param name="val">Value to write.</param>
        [ExcludeFromCodeCoverage]
        public virtual void Write(TK key, TV val)
        {
            // No-op.
        }

        /// <summary>
        /// Write the specified entries to the external resource.
        /// This method is intended to support both insert and update.
        /// <para />
        /// The order that individual writes occur is undefined.
        /// <para />
        /// If this operation fails (by throwing an exception) after a partial success,
        /// the writer must remove any successfully written entries from the entries collection
        /// so that the caching implementation knows what succeeded and can mutate the cache.
        /// </summary>
        /// <param name="entries">a mutable collection to write. Upon invocation,  it contains the entries
        /// to write for write-through. Upon return the collection must only contain entries
        /// that were not successfully written. (see partial success above).</param>
        [ExcludeFromCodeCoverage]
        public virtual void WriteAll(IEnumerable<KeyValuePair<TK, TV>> entries)
        {
            // No-op.
        }

        /// <summary>
        /// Delete the cache entry from the external resource.
        /// <para />
        /// Expiry of a cache entry is not a delete hence will not cause this method to be invoked.
        /// <para />
        /// This method is invoked even if no mapping for the key exists.
        /// </summary>
        /// <param name="key">The key that is used for the delete operation.</param>
        [ExcludeFromCodeCoverage]
        public virtual void Delete(TK key)
        {
            // No-op.
        }

        /// <summary>
        /// Remove data and keys from the external resource for the given collection of keys, if present.
        /// <para />
        /// The order that individual deletes occur is undefined.
        /// <para />
        /// If this operation fails (by throwing an exception) after a partial success,
        /// the writer must remove any successfully written entries from the entries collection
        /// so that the caching implementation knows what succeeded and can mutate the cache.
        /// <para />
        /// Expiry of a cache entry is not a delete hence will not cause this method to be invoked.
        /// <para />
        /// This method may include keys even if there is no mapping for that key,
        /// in which case the data represented by that key should be removed from the underlying resource.
        /// </summary>
        /// <param name="keys">a mutable collection of keys for entries to delete. Upon invocation,
        /// it contains the keys to delete for write-through. Upon return the collection must only contain
        /// the keys that were not successfully deleted.</param>
        [ExcludeFromCodeCoverage]
        public virtual void DeleteAll(IEnumerable<TK> keys)
        {
            // No-op.
        }

        /// <summary>
        /// Tells store to commit or rollback a transaction depending on the value of the
        /// <c>commit</c> parameter.
        /// </summary>
        /// <param name="commit"><c>True</c> if transaction should commit, <c>false</c> for rollback.</param>
        [ExcludeFromCodeCoverage]
        public virtual void SessionEnd(bool commit)
        {
            // No-op.
        }
    }
}

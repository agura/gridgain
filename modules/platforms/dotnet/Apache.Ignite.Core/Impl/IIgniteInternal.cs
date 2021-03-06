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

namespace Apache.Ignite.Core.Impl
{
    using System;
    using Apache.Ignite.Core.Binary;
    using Apache.Ignite.Core.Datastream;
    using Apache.Ignite.Core.Impl.Binary;
    using Apache.Ignite.Core.Impl.Cluster;
    using Apache.Ignite.Core.Impl.Handle;
    using Apache.Ignite.Core.Impl.Plugin;

    /// <summary>
    /// Internal Ignite interface.
    /// </summary>
    internal interface IIgniteInternal
    {
        /// <summary>
        /// Gets the binary processor.
        /// </summary>
        IBinaryProcessor BinaryProcessor { get; }

        /// <summary>
        /// Configuration.
        /// </summary>
        IgniteConfiguration Configuration { get; }

        /// <summary>
        /// Handle registry.
        /// </summary>
        HandleRegistry HandleRegistry { get; }

        /// <summary>
        /// Gets the node from cache.
        /// </summary>
        /// <param name="id">Node id.</param>
        /// <returns>Cached node.</returns>
        ClusterNodeImpl GetNode(Guid? id);

        /// <summary>
        /// Gets the marshaller.
        /// </summary>
        Marshaller Marshaller { get; }

        /// <summary>
        /// Gets the plugin processor.
        /// </summary>
        PluginProcessor PluginProcessor { get; }

        /// <summary>
        /// Gets the data streamer.
        /// </summary>
        IDataStreamer<TK, TV> GetDataStreamer<TK, TV>(string cacheName, bool keepBinary);

        /// <summary>
        /// Gets the public Ignite API.
        /// </summary>
        IIgnite GetIgnite();

        /// <summary>
        /// Gets the binary API.
        /// </summary>
        IBinary GetBinary();
    }
}
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

namespace Apache.Ignite.Core.Services
{
    using System;
    using System.Diagnostics;
    using System.Diagnostics.CodeAnalysis;
    using Apache.Ignite.Core.Binary;
    using Apache.Ignite.Core.Cluster;

    /// <summary>
    /// Service configuration.
    /// </summary>
    [Serializable]
    public class ServiceConfiguration
    {
        /// <summary>
        /// Gets or sets the service name.
        /// </summary>
        public string Name { get; set; }

        /// <summary>
        /// Gets or sets the service instance.
        /// </summary>
        public IService Service { get; set; }

        /// <summary>
        /// Gets or sets the total number of deployed service instances in the cluster, 0 for unlimited.
        /// </summary>
        public int TotalCount { get; set; }

        /// <summary>
        /// Gets or sets maximum number of deployed service instances on each node, 0 for unlimited.
        /// </summary>
        public int MaxPerNodeCount { get; set; }

        /// <summary>
        /// Gets or sets cache name used for key-to-node affinity calculation.
        /// </summary>
        public string CacheName { get; set; }

        /// <summary>
        /// Gets or sets affinity key used for key-to-node affinity calculation.
        /// </summary>
        public object AffinityKey { get; set; }

        /// <summary>
        /// Gets or sets node filter used to filter nodes on which the service will be deployed.
        /// </summary>
        public IClusterNodeFilter NodeFilter { get; set; }

        /// <summary>
        /// Serializes the Service configuration using IBinaryRawWriter
        /// </summary>
        /// <param name="w">IBinaryRawWriter</param>
        internal void Write(IBinaryRawWriter w)
        {
            Debug.Assert(w != null);

            w.WriteString(Name);
            w.WriteObject(Service);
            w.WriteInt(TotalCount);
            w.WriteInt(MaxPerNodeCount);
            w.WriteString(CacheName);
            w.WriteObject(AffinityKey);

            if (NodeFilter != null)
                w.WriteObject(NodeFilter);
            else
                w.WriteObject<object>(null);
        }

        /// <summary>
        /// Default constructor
        /// </summary>
        public ServiceConfiguration()
        {
            // No-op.
        }

        /// <summary>
        /// Deserialization constructor. Used to collect FailedConfigurations during ServiceDeploymentException 
        /// </summary>
        [SuppressMessage("Microsoft.Design", "CA1031:DoNotCatchGeneralExceptionTypes")]
        internal ServiceConfiguration(IBinaryRawReader r)
        {
            Debug.Assert(r != null);

            Name = r.ReadString();

            try
            {
                Service = r.ReadObject<IService>();
            }
            catch (Exception)
            {
                // Ignore exceptions in user deserealization code.
            }

            TotalCount = r.ReadInt();
            MaxPerNodeCount = r.ReadInt();
            CacheName = r.ReadString();
            AffinityKey = r.ReadObject<object>();

            try
            {
                NodeFilter = r.ReadObject<IClusterNodeFilter>();
            }
            catch (Exception)
            {
                // Ignore exceptions in user deserealization code.
            }
        }
    }
}
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

package org.apache.ignite.visor.commands.config

import org.apache.ignite.configuration.IgniteConfiguration
import org.apache.ignite.events.EventType._
import org.apache.ignite.visor.{VisorRuntimeBaseSpec, visor}

import org.apache.ignite.visor.commands.config.VisorConfigurationCommand._

/**
 * Unit test for 'config' command.
 */
class VisorConfigurationCommandSpec extends VisorRuntimeBaseSpec(1) {
    /**
     * Creates grid configuration for provided grid host.
     *
     * @param name Ignite instance name.
     * @return Grid configuration.
     */
    override def config(name: String): IgniteConfiguration = {
        val cfg = new IgniteConfiguration

        cfg.setIgniteInstanceName(name)
        cfg.setIncludeEventTypes(EVTS_ALL: _*)

        cfg
    }

    describe("A 'config' visor command") {
        it("should print configuration for first node") {
            visor.config("-id8=@n0")
        }
    }
}

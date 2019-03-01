/*
 *                   GridGain Community Edition Licensing
 *                   Copyright 2019 GridGain Systems, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License") modified with Commons Clause
 * Restriction; you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * Commons Clause Restriction
 *
 * The Software is provided to you by the Licensor under the License, as defined below, subject to
 * the following condition.
 *
 * Without limiting other conditions in the License, the grant of rights under the License will not
 * include, and the License does not grant to you, the right to Sell the Software.
 * For purposes of the foregoing, “Sell” means practicing any or all of the rights granted to you
 * under the License to provide to third parties, for a fee or other consideration (including without
 * limitation fees for hosting or consulting/ support services related to the Software), a product or
 * service whose value derives, entirely or substantially, from the functionality of the Software.
 * Any license notice or attribution required by the License must also include this Commons Clause
 * License Condition notice.
 *
 * For purposes of the clause above, the “Licensor” is Copyright 2019 GridGain Systems, Inc.,
 * the “License” is the Apache License, Version 2.0, and the Software is the GridGain Community
 * Edition software provided with this notice.
 */

import saver from 'file-saver';

export default class ConfigurationDownload {
    static $inject = [
        'IgniteMessages',
        'IgniteActivitiesData',
        'IgniteConfigurationResource',
        'IgniteSummaryZipper',
        'IgniteVersion',
        '$q',
        '$rootScope',
        'PageConfigure'
    ];

    constructor(messages, activitiesData, configuration, summaryZipper, Version, $q, $rootScope, PageConfigure) {
        Object.assign(this, {messages, activitiesData, configuration, summaryZipper, Version, $q, $rootScope, PageConfigure});

        this.saver = saver;
    }

    /**
     * @param {{_id: string, name: string}} cluster
     * @returns {Promise}
     */
    downloadClusterConfiguration(cluster) {
        this.activitiesData.post({action: '/configuration/download'});

        return this.PageConfigure.getClusterConfiguration({clusterID: cluster._id, isDemo: !!this.$rootScope.IgniteDemoMode})
            .then((data) => this.configuration.populate(data))
            .then(({clusters}) => {
                return clusters.find(({_id}) => _id === cluster._id)
                    || this.$q.reject({message: `Cluster ${cluster.name} not found`});
            })
            .then((cluster) => {
                return this.summaryZipper({
                    cluster,
                    data: {},
                    IgniteDemoMode: this.$rootScope.IgniteDemoMode,
                    targetVer: this.Version.currentSbj.getValue()
                });
            })
            .then((data) => this.saver.saveAs(data, this.nameFile(cluster)))
            .catch((e) => (
                this.messages.showError(`Failed to generate project files. ${e.message}`)
            ));
    }

    nameFile(cluster) {
        return `${this.escapeFileName(cluster.name)}-project.zip`;
    }

    escapeFileName(name) {
        return name.replace(/[\\\/*\"\[\],\.:;|=<>?]/g, '-').replace(/ /g, '_');
    }
}
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

package org.apache.ignite.internal.pagemem.wal.record;

import org.apache.ignite.internal.util.typedef.internal.S;

/**
 * Wal snapshot record.
 */
public class SnapshotRecord extends WALRecord {
    /** Snapshot id. */
    private long snapshotId;

    /** Full snapshot or incremental. */
    private boolean full;

    /**
     *
     */
    public SnapshotRecord(long snapshotId, boolean full) {
        this.snapshotId = snapshotId;
        this.full = full;
    }

    /**
     *
     */
    public long getSnapshotId() {
        return snapshotId;
    }

    /**
     *
     */
    public boolean isFull() {
        return full;
    }

    /**
     *
     */
    @Override public RecordType type() {
        return RecordType.SNAPSHOT;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(SnapshotRecord.class, this, "super", super.toString());
    }
}

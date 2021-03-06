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

import org.apache.ignite.internal.util.typedef.internal.S;
import org.jetbrains.annotations.Nullable;

/**
 * Local WAL state change result.
 */
public class WalStateResult {
    /** Original message. */
    private final WalStateProposeMessage msg;

    /** Whether mode was changed. */
    private final boolean changed;

    /** Error message (if any). */
    private final String errMsg;

    /**
     * Constructor.
     *
     * @param msg Original message.
     * @param changed Whether mode was changed.
     */
    public WalStateResult(WalStateProposeMessage msg, boolean changed) {
        this(msg, changed, null);
    }

    /**
     * Constructor.
     *
     * @param msg Original message.
     * @param errMsg Error message (if any).
     */
    public WalStateResult(WalStateProposeMessage msg, String errMsg) {
        this(msg, false, errMsg);
    }

    /**
     * Constructor.
     *
     * @param msg Original message.
     * @param changed Whether mode was changed.
     * @param errMsg Error message (if any).
     */
    private WalStateResult(WalStateProposeMessage msg, boolean changed, String errMsg) {
        this.msg = msg;
        this.changed = changed;
        this.errMsg = errMsg;
    }

    /**
     * @return Original message.
     */
    public WalStateProposeMessage message() {
        return msg;
    }

    /**
     * @return Whether mode was changed.
     */
    public boolean changed() {
        return changed;
    }

    /**
     * @return Error message (if any).
     */
    @Nullable public String errorMessage() {
        return errMsg;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(WalStateResult.class, this);
    }
}

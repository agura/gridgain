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

package org.apache.ignite.internal.processors.query.h2.dml;

import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.processors.query.EnlistOperation;
import org.apache.ignite.internal.processors.query.UpdateSourceIterator;

import java.util.Iterator;
import java.util.List;

/**
 * DML update results iterator.
 */
public class DmlUpdateResultsIterator implements UpdateSourceIterator<Object> {
    /** */
    private static final long serialVersionUID = 0L;

    /** */
    private final EnlistOperation op;

    /** */
    private final UpdatePlan plan;

    /** */
    private Iterator<List<?>> it;

    /**
     * Constructor.
     *
     * @param op Operation.
     * @param plan Plan.
     * @param rows Rows.
     */
    public DmlUpdateResultsIterator(EnlistOperation op, UpdatePlan plan, Iterable<List<?>> rows) {
        this.op = op;
        this.plan = plan;

        it = rows.iterator();
    }

    /** {@inheritDoc} */
    @Override public EnlistOperation operation() {
        return op;
    }

    /** {@inheritDoc} */
    @Override public boolean hasNextX() {
        return it.hasNext();
    }

    /** {@inheritDoc} */
    @Override public Object nextX() throws IgniteCheckedException {
        return plan.processRowForTx(it.next());
    }
}

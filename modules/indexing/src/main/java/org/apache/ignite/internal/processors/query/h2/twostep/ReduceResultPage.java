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

package org.apache.ignite.internal.processors.query.h2.twostep;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.UUID;
import javax.cache.CacheException;
import org.apache.ignite.IgniteCheckedException;
import org.apache.ignite.internal.GridKernalContext;
import org.apache.ignite.internal.processors.query.h2.twostep.messages.GridQueryNextPageResponse;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.plugin.extensions.communication.Message;
import org.h2.value.Value;

import static org.apache.ignite.internal.processors.query.h2.twostep.msg.GridH2ValueMessageFactory.fillArray;

/**
 * Page result.
 */
public class ReduceResultPage {
    /** */
    private final UUID src;

    /** */
    private final GridQueryNextPageResponse res;

    /** */
    private final int rowsInPage;

    /** */
    private Iterator<Value[]> rows;

    /** */
    private boolean last;

    /**
     * @param ctx Kernal context.
     * @param src Source.
     * @param res Response.
     */
    @SuppressWarnings("unchecked")
    public ReduceResultPage(final GridKernalContext ctx, UUID src, GridQueryNextPageResponse res) {
        assert src != null;

        this.src = src;
        this.res = res;

        // res == null means that it is a terminating dummy page for the given source node ID.
        if (res != null) {
            Collection<?> plainRows = res.plainRows();

            if (plainRows != null) {
                assert plainRows instanceof RandomAccess : "instance of " + plainRows.getClass();

                rowsInPage = plainRows.size();

                if (rowsInPage == 0 || ((List<Value[]>)plainRows).get(0).length == res.columns())
                    rows = (Iterator<Value[]>)plainRows.iterator();
                else {
                    // If it's a result of SELECT FOR UPDATE (we can tell by difference in number
                    // of columns checked above), we need to strip off stuff we don't need.
                    rows = F.iterator(plainRows, new IgniteClosure<Object, Value[]>() {
                        @Override public Value[] apply(Object o) {
                            Value[] row = (Value[])o;

                            assert row.length >= res.columns();

                            return Arrays.copyOfRange(row, 0, res.columns());
                        }
                    }, true);
                }
            }
            else {
                final int cols = res.columns();

                rowsInPage = res.values().size() / cols;

                final Iterator<Message> valsIter = res.values().iterator();

                rows = new Iterator<Value[]>() {
                    /** */
                    int rowIdx;

                    @Override public boolean hasNext() {
                        return rowIdx < rowsInPage;
                    }

                    @Override public Value[] next() {
                        if (!hasNext())
                            throw new NoSuchElementException();

                        rowIdx++;

                        try {
                            return fillArray(valsIter, new Value[cols], ctx);
                        }
                        catch (IgniteCheckedException e) {
                            throw new CacheException(e);
                        }
                    }

                    @Override public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }
        else {
            rowsInPage = 0;

            rows = Collections.emptyIterator();
        }
    }

    /**
     * @return {@code true} If this is a dummy fail page.
     */
    public boolean isFail() {
        return false;
    }

    /**
     * @return {@code true} If this is either a real last page for a source or
     *      a dummy terminating page with no rows.
     */
    public boolean isLast() {
        return last;
    }

    /**
     * @return {@code true} If it is a dummy last page.
     */
    public boolean isDummyLast() {
        return last && res == null;
    }

    /**
     * @param last Last page for a source.
     * @return {@code this}.
     */
    public ReduceResultPage setLast(boolean last) {
        this.last = last;

        return this;
    }

    /**
     * @return Number on rows in this page.
     */
    public int rowsInPage() {
        return rowsInPage;
    }

    /**
     * @return Rows.
     */
    public Iterator<Value[]> rows() {
        Iterator<Value[]> r = rows;

        assert r != null;

        rows = null;

        return r;
    }

    /**
     * @return Result source node ID.
     */
    public UUID source() {
        return src;
    }

    /**
     * @return Segment ID.
     */
    public int segmentId() {
        return res.segmentId();
    }

    /**
     * @return Response.
     */
    public GridQueryNextPageResponse response() {
        return res;
    }

    /**
     * Request next page.
     */
    public void fetchNextPage() {
        throw new CacheException("Failed to fetch data from node: " + src);
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(ReduceResultPage.class, this);
    }
}
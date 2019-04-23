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

package org.apache.ignite.internal.processors.hadoop;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;

import org.apache.ignite.hadoop.HadoopInputSplit;
import org.apache.ignite.internal.util.tostring.GridToStringExclude;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;

/**
 * The wrapper for native hadoop input splits.
 *
 * Warning!! This class must not depend on any Hadoop classes directly or indirectly.
 */
public class HadoopSplitWrapper extends HadoopInputSplit {
    /** */
    private static final long serialVersionUID = 0L;

    /** Native hadoop input split. */
    @GridToStringExclude
    private byte[] bytes;

    /** */
    private String clsName;

    /** Internal ID */
    private int id;

    /**
     * Creates new split wrapper.
     */
    public HadoopSplitWrapper() {
        // No-op.
    }

    /**
     * Creates new split wrapper.
     *
     * @param id Split ID.
     * @param clsName Class name.
     * @param bytes Serialized class.
     * @param hosts Hosts where split is located.
     */
    public HadoopSplitWrapper(int id, String clsName, byte[] bytes, String[] hosts) {
        assert hosts != null;
        assert clsName != null;
        assert bytes != null;

        this.hosts = hosts;
        this.id = id;

        this.clsName = clsName;
        this.bytes = bytes;
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(id);

        out.writeUTF(clsName);
        U.writeByteArray(out, bytes);
    }

    /**
     * @return Class name.
     */
    public String className() {
        return clsName;
    }

    /**
     * @return Class bytes.
     */
    public byte[] bytes() {
        return bytes;
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = in.readInt();

        clsName = in.readUTF();
        bytes = U.readByteArray(in);
    }

    /** {@inheritDoc} */
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        HadoopSplitWrapper that = (HadoopSplitWrapper)o;

        return id == that.id;
    }

    /** {@inheritDoc} */
    @Override public int hashCode() {
        return id;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(HadoopSplitWrapper.class, this, "hosts", Arrays.toString(hosts));
    }
}
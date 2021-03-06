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

package org.apache.ignite.spi.discovery.tcp.ipfinder.s3.client;

import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;

/**
 * Class to test {@link DummyObjectListing}.
 */
public class DummyObjectListingTest extends GridCommonAbstractTest {
    /**
     * Test cases for various object listing functions for S3 bucket.
     */
    @Test
    public void testDummyObjectListing() {
        Set<String> fakeKeyPrefixSet = new HashSet<>();

        fakeKeyPrefixSet.add("/test/path/val");
        fakeKeyPrefixSet.add("/test/val/test/path");
        fakeKeyPrefixSet.add("/test/test/path/val");

        ObjectListing listing = DummyObjectListing.of("bucket", fakeKeyPrefixSet);

        List<S3ObjectSummary> summaries = listing.getObjectSummaries();

        assertFalse("'testBucket' contains keys", summaries.isEmpty());
        assertTrue("'testBucket' contains more keys to fetch", listing.isTruncated());
        assertTrue(fakeKeyPrefixSet.contains(summaries.get(0).getKey()));

        summaries = listing.getObjectSummaries();

        assertFalse("'testBucket' contains keys", summaries.isEmpty());
        assertTrue("'testBucket' contains more keys to fetch", listing.isTruncated());
        assertTrue(fakeKeyPrefixSet.contains(summaries.get(0).getKey()));

        summaries = listing.getObjectSummaries();

        assertFalse("'testBucket' contains keys", summaries.isEmpty());
        assertFalse("'testBucket' does not contain anymore keys", listing.isTruncated());
        assertTrue(fakeKeyPrefixSet.contains(summaries.get(0).getKey()));

        listing = DummyObjectListing.of("bucket", new HashSet<>());

        summaries = listing.getObjectSummaries();

        assertTrue("'testBucket' does not contains keys", summaries.isEmpty());
        assertFalse("'testBucket' does not contain anymore keys", listing.isTruncated());
    }
}

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

#include <boost/test/unit_test.hpp>

#include <ignite/ignition.h>

#include <ignite/thin/ignite_client_configuration.h>
#include <ignite/thin/ignite_client.h>

#include <test_utils.h>

using namespace ignite::thin;
using namespace boost::unit_test;

class AuthTestSuiteFixture
{
public:
    AuthTestSuiteFixture()
    {
        ignite_test::ClearLfs();

        serverNode = ignite_test::StartCrossPlatformServerNode("auth.xml", "ServerNode");
    }

    ~AuthTestSuiteFixture()
    {
        ignite::Ignition::StopAll(false);
    }

private:
    /** Server node. */
    ignite::Ignite serverNode;
};

BOOST_FIXTURE_TEST_SUITE(AuthTestSuite, AuthTestSuiteFixture)

BOOST_AUTO_TEST_CASE(AuthSuccess)
{
    IgniteClientConfiguration cfg;

    cfg.SetEndPoints("127.0.0.1:11110");
    cfg.SetUser("ignite");
    cfg.SetPassword("ignite");

    IgniteClient::Start(cfg);
}

BOOST_AUTO_TEST_CASE(AuthReject)
{
    IgniteClientConfiguration cfg;

    cfg.SetEndPoints("127.0.0.1:11110");

    BOOST_CHECK_THROW(IgniteClient::Start(cfg), ignite::IgniteError);
}

BOOST_AUTO_TEST_SUITE_END()

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

package org.apache.ignite.internal.sql.command;

import org.apache.ignite.internal.sql.SqlLexer;
import org.apache.ignite.internal.sql.SqlParserUtils;
import org.apache.ignite.internal.util.typedef.internal.S;

import static org.apache.ignite.internal.sql.SqlKeyword.PASSWORD;
import static org.apache.ignite.internal.sql.SqlKeyword.WITH;
import static org.apache.ignite.internal.sql.SqlParserUtils.parseString;
import static org.apache.ignite.internal.sql.SqlParserUtils.skipIfMatchesKeyword;

/**
 * CREATE USER command.
 */
public class SqlCreateUserCommand implements SqlCommand {
    /** User name. */
    private String userName;

    /** User's password. */
    private String passwd;

    /** {@inheritDoc} */
    @Override public String schemaName() {
        return null;
    }

    /** {@inheritDoc} */
    @Override public void schemaName(String schemaName) {
        // No-op.
    }

    /**
     * @return User name.
     */
    public String userName() {
        return userName;
    }

    /**
     * @return User's password.
     */
    public String password() {
        return passwd;
    }

    /** {@inheritDoc} */
    @Override public SqlCommand parse(SqlLexer lex) {
        userName = SqlParserUtils.parseUsername(lex);

        skipIfMatchesKeyword(lex, WITH);
        skipIfMatchesKeyword(lex, PASSWORD);

        passwd = parseString(lex);

        return this;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(SqlCreateUserCommand.class, this);
    }
}

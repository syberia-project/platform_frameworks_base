/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.database.sqlite;

import android.annotation.Nullable;
import android.database.sqlite.SQLiteConstants.AuthorizerAction;
import android.database.sqlite.SQLiteConstants.AuthorizerResult;

/**
 * Authorizer which is consulted during compilation of a SQL statement.
 * <p>
 * During compilation, this callback will be invoked to determine if each action
 * requested by the SQL statement is allowed.
 * <p>
 * This can be useful to dynamically block interaction with private, internal,
 * or otherwise sensitive columns or tables inside a database, such as when
 * compiling an untrusted SQL statement.
 */
public interface SQLiteAuthorizer {
    /**
     * Test if the given action should be allowed.
     *
     * @param action The action requested by the SQL statement currently being
     *            compiled.
     * @param arg3 Optional argument relevant to the given action.
     * @param arg4 Optional argument relevant to the given action.
     * @param arg5 Optional argument relevant to the given action.
     * @param arg6 Optional argument relevant to the given action.
     * @return {@link SQLiteConstants#SQLITE_OK} to allow the action,
     *         {@link SQLiteConstants#SQLITE_IGNORE} to disallow the specific
     *         action but allow the SQL statement to continue to be compiled, or
     *         {@link SQLiteConstants#SQLITE_DENY} to cause the entire SQL
     *         statement to be rejected with an error.
     * @see <a href="https://www.sqlite.org/c3ref/c_alter_table.html">Upstream
     *      SQLite documentation</a> that describes possible actions and their
     *      arguments.
     */
    public @AuthorizerResult int onAuthorize(@AuthorizerAction int action,
            @Nullable String arg3, @Nullable String arg4,
            @Nullable String arg5, @Nullable String arg6);
}

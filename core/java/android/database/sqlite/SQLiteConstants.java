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

import android.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Useful constants defined by
 * <a href="https://www.sqlite.org/c3ref/constlist.html">upstream SQLite</a>.
 */
public class SQLiteConstants {
    private SQLiteConstants() {
    }

    /** @hide */
    @IntDef(prefix = { "SQLITE_" }, value = {
            SQLITE_OK,
            SQLITE_DENY,
            SQLITE_IGNORE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AuthorizerResult {}

    /** @hide */
    @IntDef(prefix = { "SQLITE_" }, value = {
            SQLITE_CREATE_INDEX,
            SQLITE_CREATE_TABLE,
            SQLITE_CREATE_TEMP_INDEX,
            SQLITE_CREATE_TEMP_TABLE,
            SQLITE_CREATE_TEMP_TRIGGER,
            SQLITE_CREATE_TEMP_VIEW,
            SQLITE_CREATE_TRIGGER,
            SQLITE_CREATE_VIEW,
            SQLITE_DELETE,
            SQLITE_DROP_INDEX,
            SQLITE_DROP_TABLE,
            SQLITE_DROP_TEMP_INDEX,
            SQLITE_DROP_TEMP_TABLE,
            SQLITE_DROP_TEMP_TRIGGER,
            SQLITE_DROP_TEMP_VIEW,
            SQLITE_DROP_TRIGGER,
            SQLITE_DROP_VIEW,
            SQLITE_INSERT,
            SQLITE_PRAGMA,
            SQLITE_READ,
            SQLITE_SELECT,
            SQLITE_TRANSACTION,
            SQLITE_UPDATE,
            SQLITE_ATTACH,
            SQLITE_DETACH,
            SQLITE_ALTER_TABLE,
            SQLITE_REINDEX,
            SQLITE_ANALYZE,
            SQLITE_CREATE_VTABLE,
            SQLITE_DROP_VTABLE,
            SQLITE_FUNCTION,
            SQLITE_SAVEPOINT,
            SQLITE_COPY,
            SQLITE_RECURSIVE,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AuthorizerAction {}

    /** Successful result */
    public static final int SQLITE_OK = 0;

    /** Abort the SQL statement with an error */
    public static final int SQLITE_DENY = 1;
    /** Don't allow access, but don't generate an error */
    public static final int SQLITE_IGNORE = 2;

    /** Authorizer action for {@code CREATE INDEX} */
    public static final int SQLITE_CREATE_INDEX          = 1;
    /** Authorizer action for {@code CREATE TABLE} */
    public static final int SQLITE_CREATE_TABLE          = 2;
    /** Authorizer action for {@code CREATE TEMP INDEX} */
    public static final int SQLITE_CREATE_TEMP_INDEX     = 3;
    /** Authorizer action for {@code CREATE TEMP TABLE} */
    public static final int SQLITE_CREATE_TEMP_TABLE     = 4;
    /** Authorizer action for {@code CREATE TEMP TRIGGER} */
    public static final int SQLITE_CREATE_TEMP_TRIGGER   = 5;
    /** Authorizer action for {@code CREATE TEMP VIEW} */
    public static final int SQLITE_CREATE_TEMP_VIEW      = 6;
    /** Authorizer action for {@code CREATE TRIGGER} */
    public static final int SQLITE_CREATE_TRIGGER        = 7;
    /** Authorizer action for {@code CREATE VIEW} */
    public static final int SQLITE_CREATE_VIEW           = 8;
    /** Authorizer action for {@code DELETE} */
    public static final int SQLITE_DELETE                = 9;
    /** Authorizer action for {@code DROP INDEX} */
    public static final int SQLITE_DROP_INDEX           = 10;
    /** Authorizer action for {@code DROP TABLE} */
    public static final int SQLITE_DROP_TABLE           = 11;
    /** Authorizer action for {@code DROP TEMP INDEX} */
    public static final int SQLITE_DROP_TEMP_INDEX      = 12;
    /** Authorizer action for {@code DROP TEMP TABLE} */
    public static final int SQLITE_DROP_TEMP_TABLE      = 13;
    /** Authorizer action for {@code DROP TEMP TRIGGER} */
    public static final int SQLITE_DROP_TEMP_TRIGGER    = 14;
    /** Authorizer action for {@code DROP TEMP VIEW} */
    public static final int SQLITE_DROP_TEMP_VIEW       = 15;
    /** Authorizer action for {@code DROP TRIGGER} */
    public static final int SQLITE_DROP_TRIGGER         = 16;
    /** Authorizer action for {@code DROP VIEW} */
    public static final int SQLITE_DROP_VIEW            = 17;
    /** Authorizer action for {@code INSERT} */
    public static final int SQLITE_INSERT               = 18;
    /** Authorizer action for {@code PRAGMA} */
    public static final int SQLITE_PRAGMA               = 19;
    /** Authorizer action for read access on a specific table and column */
    public static final int SQLITE_READ                 = 20;
    /** Authorizer action for {@code SELECT} */
    public static final int SQLITE_SELECT               = 21;
    /** Authorizer action for transaction operations */
    public static final int SQLITE_TRANSACTION          = 22;
    /** Authorizer action for {@code UPDATE} */
    public static final int SQLITE_UPDATE               = 23;
    /** Authorizer action for {@code ATTACH} */
    public static final int SQLITE_ATTACH               = 24;
    /** Authorizer action for {@code DETACH} */
    public static final int SQLITE_DETACH               = 25;
    /** Authorizer action for {@code ALTER TABLE} */
    public static final int SQLITE_ALTER_TABLE          = 26;
    /** Authorizer action for {@code REINDEX} */
    public static final int SQLITE_REINDEX              = 27;
    /** Authorizer action for {@code ANALYZE} */
    public static final int SQLITE_ANALYZE              = 28;
    /** Authorizer action for {@code CREATE VIRTUAL TABLE} */
    public static final int SQLITE_CREATE_VTABLE        = 29;
    /** Authorizer action for {@code DROP VIRTUAL TABLE} */
    public static final int SQLITE_DROP_VTABLE          = 30;
    /** Authorizer action for invocation of a function */
    public static final int SQLITE_FUNCTION             = 31;
    /** Authorizer action for savepoint operations */
    public static final int SQLITE_SAVEPOINT            = 32;
    /** Authorizer action for copy operations */
    public static final int SQLITE_COPY                  = 0;
    /** Authorizer action for recursive operations */
    public static final int SQLITE_RECURSIVE            = 33;
}

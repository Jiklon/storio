package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.query.InsertQuery;
import com.pushtorefresh.storio.db.query.UpdateQuery;

import java.util.Collections;

public abstract class DefaultPutResolver<T> implements PutResolver<T> {

    @NonNull protected abstract String getTable();

    @Override
    @NonNull
    public PutResult performPut(@NonNull StorIODb storIODb, @NonNull ContentValues contentValues) {
        final Long id = contentValues.getAsLong(BaseColumns._ID);
        final String table = getTable();

        if (id == null) {
            return insert(storIODb, contentValues);
        } else {
            final int numberOfUpdatedRows = storIODb.internal().update(
                    new UpdateQuery.Builder()
                            .table(table)
                            .where(BaseColumns._ID + "=?")
                            .whereArgs(String.valueOf(id))
                            .build(),
                    contentValues
            );
            if (numberOfUpdatedRows > 0) {
                return PutResult.newUpdateResult(numberOfUpdatedRows, Collections.singleton(table));
            } else {
                return insert(storIODb, contentValues);
            }
        }
    }

    @NonNull
    private PutResult insert(@NonNull StorIODb storIODb, @NonNull ContentValues contentValues) {
        final String table = getTable();

        final long insertedId = storIODb.internal().insert(
                new InsertQuery.Builder()
                        .table(table)
                        .nullColumnHack(null)
                        .build(),
                contentValues
        );
        return PutResult.newInsertResult(insertedId, Collections.singleton(table));
    }
}

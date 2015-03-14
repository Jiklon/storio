package com.pushtorefresh.storio.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.query.DeleteQuery;

import java.util.Collections;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteByQuery extends PreparedDelete<DeleteResult> {

    @NonNull private final DeleteQuery deleteQuery;

    protected PreparedDeleteByQuery(@NonNull StorIODb storIODb, @NonNull DeleteQuery deleteQuery) {
        super(storIODb);
        this.deleteQuery = deleteQuery;
    }

    @NonNull @Override public DeleteResult executeAsBlocking() {
        final StorIODb.Internal internal = storIODb.internal();

        final int countOfDeletedRows = internal.delete(deleteQuery);
        internal.notifyAboutChanges(new Changes(deleteQuery.table));

        return DeleteResult.newDeleteResult(countOfDeletedRows, Collections.singleton(deleteQuery.table));
    }

    @NonNull @Override public Observable<DeleteResult> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteResult>() {
            @Override public void call(Subscriber<? super DeleteResult> subscriber) {
                final DeleteResult deleteByQueryResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteByQueryResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder {

        @NonNull private final StorIODb storIODb;
        @NonNull private final DeleteQuery deleteQuery;

        public Builder(@NonNull StorIODb storIODb, @NonNull DeleteQuery deleteQuery) {
            this.storIODb = storIODb;
            this.deleteQuery = deleteQuery;
        }

        @NonNull public PreparedDeleteByQuery prepare() {
            return new PreparedDeleteByQuery(storIODb, deleteQuery);
        }
    }
}
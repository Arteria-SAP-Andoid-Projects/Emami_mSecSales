package com.arteriatech.emami.store;

import android.annotation.SuppressLint;
import android.content.AsyncTaskLoader;
import android.content.Context;

import com.arteriatech.mutils.common.AsyncResult;
import com.arteriatech.mutils.common.OfflineError;

import java.util.List;

@SuppressLint("NewApi")
public class OfflineErrorListDataAsyncLoader extends AsyncTaskLoader<AsyncResult<List<OfflineError>>> {
    Context ctx;

    public OfflineErrorListDataAsyncLoader(Context context) {
        super(context);
        ctx = context;
    }

    @Override
    public AsyncResult<List<OfflineError>> loadInBackground() {
        try {

            return new AsyncResult<List<OfflineError>>(OfflineManager.getErrorArchive());
        } catch (Exception e) {
            return new AsyncResult<List<OfflineError>>(e);
        }

    }

}

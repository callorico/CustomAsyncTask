package com.fattybeagle;

import android.app.Activity;
import android.os.AsyncTask;

public abstract class CustomAsyncTask<TParams, TProgress, TResult> extends AsyncTask<TParams, TProgress, TResult> {
	protected CustomApplication mApp;
	protected Activity mActivity;
	
	public CustomAsyncTask(Activity activity) {
		mActivity = activity;
		mApp = (CustomApplication) mActivity.getApplication();
	}
	
	public void setActivity(Activity activity) {
		mActivity = activity;
		if (mActivity == null) {
			onActivityDetached();
		}
		else {
			onActivityAttached();
		}
	}
	
	protected void onActivityAttached() {}
	
	protected void onActivityDetached() {}
	
	@Override
	protected void onPreExecute() {
		mApp.addTask(mActivity, this);
	}
	
	@Override
	protected void onPostExecute(TResult result) {
		mApp.removeTask(this);
	}
	
	@Override
	protected void onCancelled() {
		mApp.removeTask(this);
	}
}

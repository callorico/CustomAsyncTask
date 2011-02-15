package com.fattybeagle;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends Activity {
	
	private static class DoBackgroundTask extends CustomAsyncTask<Void, Integer, Void> {
		private static final String TAG = "DoBackgroundTask";
		
		private ProgressDialog mProgress;
		private int mCurrProgress;

		public DoBackgroundTask(TestActivity activity) {
			super(activity);
		}		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgressDialog();
		}
		
		@Override
		protected void onActivityDetached() {
			if (mProgress != null) {
				mProgress.dismiss();
				mProgress = null;
			}
		}
		
		@Override
		protected void onActivityAttached() {
			showProgressDialog();
		}
		
		private void showProgressDialog() {
			mProgress = new ProgressDialog(mActivity);
			mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgress.setMessage("Doing stuff...");
			mProgress.setCancelable(true);
			mProgress.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
				}
			});
			
			mProgress.show();
			mProgress.setProgress(mCurrProgress);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				for (int i = 0; i < 100; i+=10) {
					Thread.sleep(1000);
					this.publishProgress(i);
				}
				
			} 
			catch (InterruptedException e) {
			}
			
			return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... progress) {
			mCurrProgress = progress[0];
			if (mActivity != null) {
				mProgress.setProgress(mCurrProgress);	
			}
			else {
				Log.d(TAG, "Progress updated while no Activity was attached.");
			}
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (mActivity != null) {
				mProgress.dismiss();
				Toast.makeText(mActivity, "AsyncTask finished", Toast.LENGTH_LONG).show();
			}
			else {
				Log.d(TAG, "AsyncTask finished while no Activity was attached.");
			}
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button b = (Button) findViewById(R.id.launchTaskButton);
        b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new DoBackgroundTask(TestActivity.this).execute();
			}
        });
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	((CustomApplication) getApplication()).detach(this);
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	
    	((CustomApplication) getApplication()).attach(this);
    }
}
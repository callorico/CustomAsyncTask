package com.fattybeagle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.Application;

public class CustomApplication extends Application {
	/**
	 * Maps between an activity class name and the list of currently running
	 * AsyncTasks that were spawned while it was active.
	 */
	private Map<String, List<CustomAsyncTask<?,?,?>>> mActivityTaskMap;
	
	public CustomApplication() {
		mActivityTaskMap = new HashMap<String, List<CustomAsyncTask<?,?,?>>>();
	}
	
	public void removeTask(CustomAsyncTask<?,?,?> task) {
		for (Entry<String, List<CustomAsyncTask<?,?,?>>> entry : mActivityTaskMap.entrySet()) {
			List<CustomAsyncTask<?,?,?>> tasks = entry.getValue();
			for (int i = 0; i < tasks.size(); i++) {
				if (tasks.get(i) == task) {
					tasks.remove(i);
					break;
				}
			}
			
			if (tasks.size() == 0) {
				mActivityTaskMap.remove(entry.getKey());
				return;
			}
		}
	}
	
	public void addTask(Activity activity, CustomAsyncTask<?,?,?> task) {
		String key = activity.getClass().getCanonicalName();
		List<CustomAsyncTask<?,?,?>> tasks = mActivityTaskMap.get(key);
		if (tasks == null) {
			tasks = new ArrayList<CustomAsyncTask<?,?,?>>();
			mActivityTaskMap.put(key, tasks);
		}
		
		tasks.add(task);
	}
	
	public void detach(Activity activity) {
		List<CustomAsyncTask<?,?,?>> tasks = mActivityTaskMap.get(activity.getClass().getCanonicalName());
		if (tasks != null) {
			for (CustomAsyncTask<?,?,?> task : tasks) {
				task.setActivity(null);
			}
		}
	}
	
	public void attach(Activity activity) {
		List<CustomAsyncTask<?,?,?>> tasks = mActivityTaskMap.get(activity.getClass().getCanonicalName());
		if (tasks != null) {
			for (CustomAsyncTask<?,?,?> task : tasks) {
				task.setActivity(activity);
			}
		}
	}
}

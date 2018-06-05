package com.application.onlineTestSeries.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

public class UtilsApp {
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
	public static final String webUrl = "http://lawuniverse.co.in/demo_api/";
	public static final String webUrlHome = "http://lawuniverse.co.in/";

	private static String uniqueID = null;
	private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";

	public synchronized static String id(Context context) {
		if (uniqueID == null) {
			SharedPreferences sharedPrefs = context.getSharedPreferences(
					PREF_UNIQUE_ID, Context.MODE_PRIVATE);
			uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);

			if (uniqueID == null) {
				uniqueID = UUID.randomUUID().toString();
				SharedPreferences.Editor editor = sharedPrefs.edit();
				editor.putString(PREF_UNIQUE_ID, uniqueID);
				editor.apply();
			}
		}
		return uniqueID;
	}

}
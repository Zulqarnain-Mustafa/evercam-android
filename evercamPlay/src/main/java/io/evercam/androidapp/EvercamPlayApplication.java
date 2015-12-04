package io.evercam.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.HashMap;

import io.evercam.androidapp.feedback.IntercomApi;
import io.evercam.androidapp.utils.DataCollector;
import io.evercam.androidapp.utils.PropertyReader;
import io.intercom.android.sdk.Intercom;

public class EvercamPlayApplication extends MultiDexApplication
{
    private static final String PROPERTY_ID = "UA-52483995-1";

    private static final String TAG = "EvercamPlayApplication";

    public enum TrackerName
    {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    public EvercamPlayApplication()
    {
        super();
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        PropertyReader propertyReader = new PropertyReader(this);

        IntercomApi.ANDROID_API_KEY = propertyReader.getPropertyStr(PropertyReader.KEY_INTERCOM_ANDROID_KEY);
        IntercomApi.APP_ID = propertyReader.getPropertyStr(PropertyReader.KEY_INTERCOM_APP_ID);
        IntercomApi.WEB_API_KEY = propertyReader.getPropertyStr(PropertyReader.KEY_INTERCOM_KEY);
        Intercom.initialize(this, IntercomApi.ANDROID_API_KEY, IntercomApi.APP_ID);

//            // Redirect URL, just for temporary testing
//            API.URL = "http://proxy.evr.cm:9292/v1/";
    }

    synchronized Tracker getTracker(TrackerName trackerId)
    {
        if(!mTrackers.containsKey(trackerId))
        {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml
                    .app_tracker) : analytics.newTracker(PROPERTY_ID);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }

    private static Tracker getAppTracker(Activity activity)
    {
        Tracker tracker = ((EvercamPlayApplication) activity.getApplication()).getTracker(TrackerName
            .APP_TRACKER);
        tracker.enableAdvertisingIdCollection(true);
        return tracker;
    }

    /**
     * Send screen view to Google Analytics from activity with screen name.
     *
     * @param activity
     * @param screenName The screen name that shows in Google dashboard.
     */
    public static void sendScreenAnalytics(Activity activity, String screenName)
    {
        Tracker tracker = getAppTracker(activity);
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.AppViewBuilder().build());
    }

    public static void sendEventAnalytics(Activity activity, int cateory, int action, int label)
    {
        Tracker tracker = getAppTracker(activity);
        tracker.send(new HitBuilders.EventBuilder().setCategory(activity.getString(cateory))
                .setAction(activity.getString(action)).setLabel(activity.getString(label)).build());
    }

    public static void sendCaughtException(Activity activity, Exception e)
    {
        Tracker tracker = getAppTracker(activity);
        tracker.send(new HitBuilders.ExceptionBuilder().setDescription(e.getStackTrace()[0]
                .toString().replace("io.evercam.androidapp", e.toString())).setFatal(true).build());
    }

    public static void sendCaughtException(Activity activity, String message)
    {
        Tracker tracker = getAppTracker(activity);
        tracker.send(new HitBuilders.ExceptionBuilder().setDescription(message).setFatal(true)
                .build());
    }

    public static void sendCaughtExceptionNotImportant(Activity activity, Exception e)
    {
        Tracker tracker = getAppTracker(activity);
        tracker.send(new HitBuilders.ExceptionBuilder().setDescription(e.getStackTrace()[0]
                .toString().replace("io.evercam.androidapp", e.toString())).setFatal(false).build
                ());
    }
}
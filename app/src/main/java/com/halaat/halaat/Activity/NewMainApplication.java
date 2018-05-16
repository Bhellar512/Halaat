package com.halaat.halaat.Activity;

import android.app.Application;
import android.util.Log;

import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

/**
 * Created by Hp on 5/5/2018.
 */

public class NewMainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("wOTiZj2EJAO6yjOrw3M8tKpcg", "eVs7rMksRILZuKew0VDwf10phny2uqAaoSi5BlycQAL7G3GncD"))
                .debug(true)
                .build();
        Twitter.initialize(config);


    }
}

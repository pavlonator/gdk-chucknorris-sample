package com.chucknorris;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;

/**
 * @author Pavlo Cherkashyn
 */
public class JokeService extends Service {
    private static final String LIVE_CARD_ID = "chucknorris";

    private TextToSpeech tts;

    private final IBinder mBinder = new JokeBinder();


    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int i) {

            }
        });

        new ReceiveJokeTask(tts)
                .execute("http://api.icndb.com/jokes/random");

        return Service.START_STICKY;
    }

    public class JokeBinder extends Binder {
        JokeService getService() {
            return JokeService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }




}

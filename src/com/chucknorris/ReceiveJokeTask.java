package com.chucknorris;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Pavlo Cherkashyn
 */
public class ReceiveJokeTask extends AsyncTask<String, Void, String> {
    private Exception exception;

    private TextToSpeech tts;
    public ReceiveJokeTask(TextToSpeech tts) {
        this.tts = tts;
    }

    protected String doInBackground(String... urls) {
        // http get client
        HttpClient client = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet();

        try {
            // construct a URI object
            getRequest.setURI(new URI(urls[0]));
        } catch (URISyntaxException e) {
            Log.e("URISyntaxException", e.toString());
        }

        // buffer reader to read the response
        BufferedReader in = null;
        // the service response
        HttpResponse response = null;
        try {
            // execute the request
            response = client.execute(getRequest);
        } catch (ClientProtocolException e) {
            Log.e("ClientProtocolException", e.toString());
        } catch (IOException e) {
            Log.e("IO exception", e.toString());
        }

        try {
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IllegalStateException e) {
            Log.e("IllegalStateException", e.toString());
        } catch (IOException e) {
            Log.e("IO exception", e.toString());
        }
        StringBuffer buff = new StringBuffer("");
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                buff.append(line);
            }
        } catch (IOException e) {
            Log.e("IO exception", e.toString());
            return e.getMessage();
        }

        try {
            in.close();
        } catch (IOException e) {
            Log.e("IO exception", e.toString());
        }

        String joke = "";
        try {
            JSONObject jObject = new JSONObject(buff.toString());
            joke = jObject.getJSONObject("value").getString("joke");
            joke = joke.replaceAll("&quot;", "");
            joke = joke.replaceAll("&lt;", "");
            joke = joke.replaceAll("&gt;", "");
            joke = joke.replaceAll("'", "");

        } catch (JSONException e) {
            Log.e("JSON exception", e.toString());
        }

        return joke;
    }

    protected void onPostExecute(String joke) {
        if (exception != null) {
            return;
        }
        readOutLoud(joke);
    }

    private void readOutLoud(String joke) {
        tts.speak(joke, TextToSpeech.QUEUE_FLUSH, null);
    }
}
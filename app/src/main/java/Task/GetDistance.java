package Task;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by arbaz on 10/27/2017.
 */

public class GetDistance extends AsyncTask<String, Void, String> {

    TextView distance;


    public GetDistance(TextView distance) {
        this.distance = distance;
    }


    @Override
    protected String doInBackground(String... strings) {

        String response = "";
        try {
            URL url = new URL(strings[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                response += line;
            }
            br.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return response;
    }


    @Override
    protected void onPostExecute(String s) {

        try {
            JSONObject matrix = new JSONObject(s);
            String distance = ((JSONObject) matrix.getJSONArray("routes").get(0)).getJSONArray("legs")
                    .getJSONObject(0).getJSONObject("distance").getString("text");
            this.distance.setText(distance);
        } catch (Exception e) {
            Log.d("herehere", e.getMessage());
        }

    }

}

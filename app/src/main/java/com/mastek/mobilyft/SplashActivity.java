package com.mastek.mobilyft;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    Button btnRegister;
    int userID;
    String baseServiceURL;

    // flag for Internet connection status
    Boolean isInternetPresent = false;

    // Connection detector class
    ConnectionDetector cd;

    String regStatus;
    String regLocStatus;
    String regVehStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        // get Internet status
        isInternetPresent = cd.isConnectingToInternet();

        // check for Internet status
        if (!isInternetPresent) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.app_name)
                    .setMessage("No internet connection.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .show();
        }
        else
        {
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            baseServiceURL = globalVariable.getBaseServiceURL();

            pref = getSharedPreferences("MobiLyft", MODE_PRIVATE);
            editor = pref.edit();

            //userID = Integer.parseInt(pref.getString("userID", "0"));
            userID = pref.getInt("userID", 0);

            btnRegister = (Button) findViewById(R.id.btnRegister);

            regStatus=pref.getString("register", "false");
            regLocStatus=pref.getString("regLoc", "false");
            regVehStatus=pref.getString("regVeh", "false");
            Log.d("getSharedPreferences",regStatus);
            if(regStatus.equals("true")) {
                //Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                //startActivity(homeActivity);
                //Intent avatarActivity = new Intent(getApplicationContext(), AvatarActivity.class);
                //startActivity(avatarActivity);
                new ExecuteTaskGetUserDetails().execute();
            }
            else
            {
                //Intent regActivity = new Intent(getApplicationContext(), RegistrationActivity.class);
                //startActivity(regActivity);
                btnRegister.setVisibility(View.VISIBLE);
            }
        }
    }

    public void Register(View view)
    {
        Intent regActivity = new Intent(getApplicationContext(), RegistrationActivity.class);
        regActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        regActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        regActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(regActivity);
    }

    class ExecuteTaskGetUserDetails extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //String res=GetData();
            ////String res = GetData();
            String result = PostRequest();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jObj = new JSONObject(result);

                String strResult = jObj.getString("Result");
                Intent activity;
                if (strResult.equals("S")) {
                    if(regLocStatus.equals("false")){
                        activity = new Intent(getApplicationContext(), CurrentLocationActivity.class);
                    }
                    else if(regVehStatus.equals("false")){
                        activity = new Intent(getApplicationContext(), VehicleRegistrationActivity.class);
                    }
                    else{
                        activity = new Intent(getApplicationContext(), HomeActivity.class);
                    }
                    /*Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(homeActivity);*/
                    activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(activity);
                }else
                {
                    btnRegister.setVisibility(View.VISIBLE);
                }

            } catch (Exception e) {
                Log.e("onPostExecute ", "" + e.getMessage());
            }

        }

        private String readResponse(HttpResponse res) {
            InputStream is = null;
            String return_text = "";
            try {
                is = res.getEntity().getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
                String line = "";
                StringBuffer sb = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }
                return_text = sb.toString();
            } catch (Exception e) {

            }
            return return_text;

        }

        private String PostRequest() {
            String result = "";

            String serviceURL = baseServiceURL + "/User/ValidateUser";
            Log.d("servicename",serviceURL);

            HttpPost request = new HttpPost(serviceURL);

            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            try {
                // Build JSON string
                JSONStringer jsonStringer = new JSONStringer();
                jsonStringer.object();
                jsonStringer.key("UserID");
                jsonStringer.value("" + userID + "");
                jsonStringer.endObject();

                StringEntity entity = new StringEntity(jsonStringer.toString());

                //Toast.makeText(this, vehicle.toString() + "\n", Toast.LENGTH_LONG).show() ;
                Log.d("jsonStringer", jsonStringer.toString());

                request.setEntity(entity);

                // Send request to WCF service
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
                Log.d("WebInvoke", "Saving : " + response.getStatusLine().getStatusCode());
                Log.d("WebInvoke", "Saving : " + response.toString());
                //Toast.makeText(this, response.getStatusLine().getStatusCode() + "\n", Toast.LENGTH_LONG).show() ;

                result = readResponse(response);
                Log.d("WebInvoke result", result);

            }catch (Exception e) {
                e.printStackTrace();
                result = "";
            }
            return  result;
            // Toast.makeText(this, not + " OK ! " + "\n", Toast.LENGTH_LONG).show() ;

        }
    }
}



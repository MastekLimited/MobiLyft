package com.mastek.mobilyft;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class VehicleRegistrationActivity extends AppCompatActivity {

    Button btnRegVehicle;
    EditText etVehRegNo;
    EditText etVehDescription;

    Spinner spVehicleType;

    int userID;
    int companyID;

    String username;
    String useremail;

    String vehicleType;
    String vehicle_RegNo;
    String vehicle_desc;
    String regVehStatus;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private ProgressDialog progressDialog;

    String baseServiceURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_registration);


        spVehicleType = (Spinner) findViewById(R.id.spVehicleType);
        etVehRegNo = (EditText) findViewById(R.id.etVehRegNo);
        etVehDescription = (EditText) findViewById(R.id.etVehDescription);

        pref = getSharedPreferences("MobiLyft", MODE_PRIVATE);
        editor = pref.edit();

        /*userID = Integer.parseInt(pref.getString("userID", "nil"));
        companyID = Integer.parseInt(pref.getString("companyID", "nil"));*/
        userID = pref.getInt("userID", 0);
        companyID = pref.getInt("companyID", 0);
        username = pref.getString("username", "nil");
        useremail = pref.getString("useremail", "nil");

        regVehStatus=pref.getString("regVeh", "false");

        ArrayAdapter<CharSequence> arrayAdapterVehicleType = ArrayAdapter.createFromResource(
                this, R.array.vehicle_type_array, android.R.layout.simple_spinner_item);
        arrayAdapterVehicleType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spVehicleType.setAdapter(arrayAdapterVehicleType);


        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();

        Log.d("23333333333333", "333333333333333333333333");
        //Back button on Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        String getStatus=pref.getString("register", "false");
        if(getStatus.equals("true")){
            Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);

            homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            startActivity(homeActivity);
        }
        else {
            Intent clActivity = new Intent(getApplicationContext(), CurrentLocationActivity.class);
            startActivity(clActivity);
        }
    }

    public void RegisterVehicle(View view) {

        String vehRegNumPattern = "^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$";
        try {
            if (etVehRegNo.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Reg. no. is blank", Toast.LENGTH_LONG).show();
                etVehRegNo.setError("Reg. no. is required");
            }
            else if (!etVehRegNo.getText().toString().matches(vehRegNumPattern)) {
                Toast.makeText(getApplicationContext(), "Invalid vehicle reg. no.", Toast.LENGTH_SHORT).show();
                etVehRegNo.setError("Enter proper vehicle reg no.\n Eg: MH 00 A 0000");
            }
            else if (etVehDescription.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Description is blank", Toast.LENGTH_LONG).show();
                etVehDescription.setError("Description is required");
            }
            else {
                new ExecuteTaskRegisterVehicle().execute();
            }
        } catch (Exception e) {
            Log.e("Click E VerifyOTP ", e.getMessage());
        }
    }

    public void SkipVehicleReg(View view)
    {
        //editor.putString("register","true");
        editor.putString("regVeh","NA");
        editor.commit();
        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);

        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        startActivity(homeActivity);
    }

    class ExecuteTaskRegisterVehicle extends AsyncTask<String, Integer, String> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(VehicleRegistrationActivity.this);
            progressDialog.setTitle("Completing registration..");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            Log.d("rrrrrrrrrrrr", "rrrrrrrrrrrrrrr");
            String result = PostRequest();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jObj = new JSONObject(result);

                String strResult = jObj.getString("Result");
                String strResultMessage = jObj.getString("ResultMessage");

                if (strResult.equals("S")) {
                    Toast.makeText(getApplicationContext(), strResultMessage,
                            Toast.LENGTH_LONG).show();
                    Log.d("onPostExecute ", "vehicle registration successful");

                    editor.putString("RegVehicleType", vehicleType.trim());
                    editor.putString("RegVehicleList", vehicleType.trim() + " - " + vehicle_RegNo.trim());
                    //String getStatus=pref.getString("regVeh", "false");
                    Log.d("getStatus", regVehStatus);
                    editor.putString("regVeh","true");
                    editor.commit();
                    //editor.putString("register","true");
                    //editor.putString("regVeh","true");
                    //editor.commit();
                    /*if(getStatus.equals("NA")){
                        Intent offerRideActivity = new Intent(getApplicationContext(), OfferRideActivity.class);
                        startActivity(offerRideActivity);
                    }
                    else {
                        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);

                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                        startActivity(homeActivity);
                    }*/
                    Intent activity;
                    if(regVehStatus.equals("NA")){
                        activity = new Intent(getApplicationContext(), OfferRideActivity.class);
                    }
                    else {
                        activity = new Intent(getApplicationContext(), HomeActivity.class);
                    }

                    activity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    startActivity(activity);

                } else {
                    Toast.makeText(getApplicationContext(), strResultMessage,
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.e("onPostExecute ", "" + e.getMessage());
            }
            finally {
                progressDialog.cancel();
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
            vehicleType = spVehicleType.getSelectedItem().toString();
            Log.d("11111111111", "11111111111111");
            vehicle_RegNo = etVehRegNo.getText().toString().trim();
            Log.d("222222222222", "22222222222222");
            vehicle_desc = etVehDescription.getText().toString().trim();

            String serviceURL = baseServiceURL + "/User/VehicleReg";
            Log.d("servicename",serviceURL);

            HttpPost request = new HttpPost(serviceURL);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");


            // EditText plateEdit = null;
            // Editable plate ;
            // plate =  plateEdit.getText();

            try {
                // Build JSON string
                JSONStringer jsonStringer = new JSONStringer();
                jsonStringer.object();
                jsonStringer.key("UserID");
                jsonStringer.value("" + userID + "");
                jsonStringer.key("VehicleTypeName");
                jsonStringer.value("" + vehicleType + "");
                jsonStringer.key("VehicleRegNo");
                jsonStringer.value("" + vehicle_RegNo + "");
                jsonStringer.key("VehicleDesc");
                jsonStringer.value("" + vehicle_desc + "");
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

package com.mastek.mobilyft;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CurrentLocationActivity extends AppCompatActivity {


    ArrayList<String> branchesList;
    ArrayList<String> branchLocationList;
    ArrayList<String> locationList;
    int userID;
    int companyID;

    String username;
    String useremail;

    Spinner spCompanyBranch;
    Spinner spOfficeLocation;
    Spinner spHomeLocation;

    String currentBranchLocation;
    String officeLocation;
    String homeLocation;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    String baseServiceURL;
    //String callingParent;
    String branchLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);

        spCompanyBranch = (Spinner) this.findViewById(R.id.spCompanyBranch);
        spOfficeLocation = (Spinner) this.findViewById(R.id.spOfficeLocation);
        spHomeLocation = (Spinner) this.findViewById(R.id.spHomeLocation);
        branchLocation = "";
        pref = getSharedPreferences("MobiLyft", MODE_PRIVATE);
        editor = pref.edit();

        /*userID = Integer.parseInt(pref.getString("userID", "nil"));
        companyID = Integer.parseInt(pref.getString("companyID", "nil"));*/
        userID = pref.getInt("userID", 0);
        companyID = pref.getInt("companyID", 0);
        username = pref.getString("username", "nil");
        useremail = pref.getString("useremail", "nil");

        currentBranchLocation = pref.getString("branchLocation", "nil");
        officeLocation = pref.getString("officeLocation", "nil");
        homeLocation = pref.getString("homeLocation", "nil");

        Log.d("currentBranchLocation",currentBranchLocation);
        Log.d("officeLocation",officeLocation);
        Log.d("homeLocation",homeLocation);

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();



        //Back button on Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        spCompanyBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                currentBranchLocation = spCompanyBranch.getSelectedItem().toString();
                branchLocation = branchLocationList.get(position);
                Log.d("branchLocation", "spCompanyBranchspCompanyBranchspCompanyBranchspCompanyBranchspCompanyBranch");
                Log.d("branchLocation", branchLocation);
                new ExecuteTaskLoadLocation().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        new ExecuteTaskLoadBranches().execute();
    }


    @Override
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        //this.startActivity(new Intent(CurrentLocationActivity.this,OTPActivity.class));
        String getStatus=pref.getString("register", "false");
        Log.d("getStatus", getStatus);
        if(getStatus.equals("true")){
            Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);

            homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            startActivity(homeActivity);
        }
        else {
            Intent otpActivity = new Intent(getApplicationContext(), OTPActivity.class);
            startActivity(otpActivity);
        }

        return;
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

    public void SubmitBranchLocation(View view)
    {
        new ExecuteTaskSaveBranchLocation().execute();
    }


    class ExecuteTaskLoadBranches extends AsyncTask<Void, Void, Void> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(CurrentLocationActivity.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //String res=GetData();
            Log.d("ccccccccc", "cccccccccccccccccccccccc");
            //String result = GetData();
            String result = PostRequest();
            try {

                JSONArray jsonArray = new JSONArray(result);
                Log.d("jsonArray", jsonArray.toString());
                Log.d("jsonArray length", String.valueOf(jsonArray.length()));
                branchesList = new ArrayList<String>();
                branchLocationList = new ArrayList<String>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d("Aaaa",String.valueOf(i));
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Log.d("jsonObject",jsonObject.toString());
                    Log.d("bbbbbbbbb",String.valueOf(i));
                    Log.d("Spinner Branch id", jsonObject.getString("BranchID"));
                    Log.d("Spinner Branch_Name", jsonObject.getString("BranchName"));
                    //branchesList.add(Integer.parseInt(jsonObject.getString("BranchID")), jsonObject.getString("BranchName"));
                    branchesList.add(jsonObject.getString("BranchName"));
                    branchLocationList.add(jsonObject.getString("BranchLocationName"));
                }
                //editor.putString("branchesList", branchesList.toString());
                //editor.putString("branchLocationList", branchLocationList.toString());
            } catch (Exception e) {
                Log.e("doInBackground ", "" + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            //spCompanyBranch
            //        .setAdapter(new ArrayAdapter<String>(CurrentLocationActivity.this, android.R.layout.simple_spinner_dropdown_item, branchesList));

            ArrayAdapter<String> arrayAdapterBranches = new ArrayAdapter<String>(CurrentLocationActivity.this, android.R.layout.simple_spinner_item, branchesList);
            arrayAdapterBranches.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCompanyBranch
                    .setAdapter(arrayAdapterBranches);
            if (!currentBranchLocation.equals(null)) {
                int itemPosition = arrayAdapterBranches.getPosition(currentBranchLocation);
                spCompanyBranch.setSelection(itemPosition);
            }

            progressDialog.cancel();
        }

        public String readResponse(HttpResponse res) {
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

            String serviceURL = baseServiceURL + "/Company/Branches";
            Log.d("servicename",serviceURL);
            //servicename = servicename + "/" + etName.getText() + "/" + etCompemail.getText() + "/" + spGender.getSelectedItem().toString() + "/" + etMobileNo.getText();
            //servicename = servicename + "/" + name + "/" + compEmail + "/" + gender + "/" + mobileNo;
            HttpPost request = new HttpPost(serviceURL);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            try {
                // Build JSON string
                JSONStringer jsonStringer = new JSONStringer();
                jsonStringer.object();
                jsonStringer.key("CompanyID");
                jsonStringer.value("" + companyID + "");
                jsonStringer.endObject();

                StringEntity entity = new StringEntity(jsonStringer.toString());

                Log.d("jsonStringer", jsonStringer.toString());

                request.setEntity(entity);

                // Send request to WCF service
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(request);
                Log.d("WebInvoke", "Saving : " + response.getStatusLine().getStatusCode());
                Log.d("WebInvoke", "Saving : " + response.toString());

                result = readResponse(response);
                Log.d("WebInvoke result", result);

            }catch (Exception e) {
                e.printStackTrace();
                result = "";
            }
            return  result;

        }
    }

    class ExecuteTaskLoadLocation extends AsyncTask<Void, Void, Void> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(CurrentLocationActivity.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //String res=GetData();
            Log.d("ccccccccc", "cccccccccccccccccccccccc");
            //String result = GetData();
            String result = PostRequest();
            try {

                JSONArray jsonArray = new JSONArray(result);
                Log.d("jsonArray", jsonArray.toString());
                Log.d("jsonArray length", String.valueOf(jsonArray.length()));
                locationList = new ArrayList<String>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d("Aaaa",String.valueOf(i));
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Log.d("jsonObject",jsonObject.toString());
                    Log.d("bbbbbbbbb",String.valueOf(i));
                    Log.d("Spinner Branch id", jsonObject.getString("LocationID"));
                    Log.d("Spinner Branch_Name", jsonObject.getString("LocationName"));
                    //branchesList.add(Integer.parseInt(jsonObject.getString("BranchID")), jsonObject.getString("BranchName"));
                    locationList.add(jsonObject.getString("LocationName"));
                }
                editor.putString("locationList", result);
            } catch (Exception e) {
                Log.e("doInBackground ", "" + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            Log.d("LoadLc","LLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            Log.d("LoadLc",locationList.toString());
           // spOfficeLocation
            //        .setAdapter(new ArrayAdapter<String>(CurrentLocationActivity.this, android.R.layout.simple_spinner_item, locationList));

           // spHomeLocation
           //         .setAdapter(new ArrayAdapter<String>(CurrentLocationActivity.this, android.R.layout.simple_spinner_item, locationList));


            ArrayAdapter<String> arrayAdapterLoc = new ArrayAdapter<String>(CurrentLocationActivity.this, android.R.layout.simple_spinner_item, locationList);

            arrayAdapterLoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spOfficeLocation
                    .setAdapter(arrayAdapterLoc);
            /*if (!officeLocation.equals(null)) {
                int itemPosition = arrayAdapterLoc.getPosition(officeLocation);
                spOfficeLocation.setSelection(itemPosition);
            }
            else {
                Log.d("branchLocation","branchLocationbranchLocationbranchLocationbranchLocationbranchLocation");
                Log.d("branchLocation",branchLocation);
                int itemPos = arrayAdapterLoc.getPosition(branchLocation);
                spOfficeLocation.setSelection(itemPos);
            }*/

            if(!branchLocation.equals("")){
                Log.d("branchLocation","branchLocationbranchLocationbranchLocationbranchLocationbranchLocation");
                Log.d("branchLocation",branchLocation);
                int itemPos = arrayAdapterLoc.getPosition(branchLocation);
                spOfficeLocation.setSelection(itemPos);
            }
            else {
                if (!officeLocation.equals(null)) {
                    int itemPosition = arrayAdapterLoc.getPosition(officeLocation);
                    spOfficeLocation.setSelection(itemPosition);
                }
            }

            spHomeLocation
                    .setAdapter(arrayAdapterLoc);
            if (!homeLocation.equals(null)) {
                int itemPosition = arrayAdapterLoc.getPosition(homeLocation);
                spHomeLocation.setSelection(itemPosition);
            }
            progressDialog.cancel();

            //spOfficeLocation.performClick();
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

            String serviceURL = baseServiceURL + "/Ride/Locations";
            Log.d("servicename",serviceURL);
            HttpPost request = new HttpPost(serviceURL);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

         try {
                // Build JSON string
                JSONStringer jsonStringer = new JSONStringer();
                jsonStringer.object();
                jsonStringer.key("BranchName");
                jsonStringer.value("" + currentBranchLocation + "");
                jsonStringer.key("CompanyID");
                jsonStringer.value("" + companyID + "");
                //jsonStringer.key("Branch");
                //jsonStringer.value("" + currentBranchLocation + "");
                jsonStringer.endObject();

                StringEntity entity = new StringEntity(jsonStringer.toString());

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
        }
    }


    class ExecuteTaskSaveBranchLocation extends AsyncTask<String, Integer, String> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(CurrentLocationActivity.this);
            progressDialog.setTitle("Saving locations....");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            String result = PostRequest();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jObj = new JSONObject(result);

                String strResult=jObj.getString("Result");
                String strResultMessage=jObj.getString("ResultMessage");

                if(strResult.equals("S"))
                {
                    String[] values  = strResultMessage.split(",");
                    Log.d("BranchID", values[0].toString());
                    /*editor.putString("branchID", values[0]);
                    editor.putString("officeLocationId", values[1]);
                    editor.putString("homeLocationId", values[2]);*/
                    editor.putInt("branchID", Integer.parseInt(values[0]));
                    /*editor.putInt("officeLocationId", Integer.parseInt(values[1]));
                    editor.putInt("homeLocationId", Integer.parseInt(values[2]));*/
                    editor.putString("branchLocation", currentBranchLocation);
                    editor.putString("officeLocation", officeLocation);
                    editor.putString("homeLocation", homeLocation);
                    editor.putString("regLoc", "true");
                    //editor.putString("register","true");
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Success",
                            Toast.LENGTH_LONG).show();

                    /*String getStatus=pref.getString("register", "false");
                    Log.d("getSharedPreferences",getStatus);
                    if(getStatus.equals("true")) {
                        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(homeActivity);
                    }
                    else {
                        Intent vehRegActivity = new Intent(getApplicationContext(), VehicleRegistrationActivity.class);
                        startActivity(vehRegActivity);
                    }*/

                    String getStatus=pref.getString("regVeh", "false");
                    Log.d("getSharedPreferences",getStatus);
                    if(getStatus.equals("false")) {
                        Intent vehRegActivity = new Intent(getApplicationContext(), VehicleRegistrationActivity.class);
                        startActivity(vehRegActivity);
                    }
                    else {
                        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(homeActivity);
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Enter correct location",
                            Toast.LENGTH_LONG).show();
                }


            }catch (Exception e)
            {
                Log.e("onPostExecute ", ""+e.getMessage());
            }
            finally {
                progressDialog.cancel();
            }


        }

       /* public String GetData() {
            String result = "";
            Log.d("currentBranchLocation", "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
            //otp = etOTP.getText().toString().trim();
            currentBranchLocation = spCompanyBranch.getSelectedItem().toString();
            Log.d("currentBranchLocation", currentBranchLocation);

            String servicename = "http://172.16.217.176/RDServices/RDServices.svc/User/Branch";
            try {
                //servicename = servicename + "/" + etName.getText() + "/" + etCompemail.getText() + "/" + spGender.getSelectedItem().toString() + "/" + etMobileNo.getText();
                servicename = servicename + "/" + userID + "/" + currentBranchLocation;

                Log.d("servicename", servicename);

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpPost = new HttpGet(servicename);

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();
                result = readResponse(httpResponse);

                Log.d("PostData", "" + result);
            } catch (Exception exception) {

                Log.d("exception", "" + exception.getMessage());

            }
            return result;


        }
*/
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

            Log.d("currentBranchLocation", "yyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
            //otp = etOTP.getText().toString().trim();
            currentBranchLocation = spCompanyBranch.getSelectedItem().toString();
            officeLocation = spOfficeLocation.getSelectedItem().toString();
            homeLocation = spHomeLocation.getSelectedItem().toString();
            Log.d("currentBranchLocation", currentBranchLocation);
            Log.d("officeLocation", officeLocation);
            Log.d("homeLocation", homeLocation);


            String serviceURL = baseServiceURL + "/User/Branch";
            Log.d("servicename",serviceURL);
            //servicename = servicename + "/" + etName.getText() + "/" + etCompemail.getText() + "/" + spGender.getSelectedItem().toString() + "/" + etMobileNo.getText();
            //servicename = servicename + "/" + name + "/" + compEmail + "/" + gender + "/" + mobileNo;
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
                jsonStringer.key("Branch");
                jsonStringer.value("" + currentBranchLocation + "");
                jsonStringer.key("OfficeLocation");
                jsonStringer.value("" + officeLocation + "");
                jsonStringer.key("HomeLocation");
                jsonStringer.value("" + homeLocation + "");
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

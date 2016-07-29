package com.mastek.mobilyft;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AvailRideActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner spLocFrom, spLocTo;
    List<String> spinnerList1, spinnerList2, spinnerList3;
    ImageButton imgBtnDate;
    ImageButton imgBtnTime;
    EditText etdate;
    EditText ettime;

    private Calendar calendar;

    private int year, month, day;
    private int hour;
    private int minute;

    int userID;
    int companyID;
    int currentbranchID;
    String username;
    String useremail;
    String officeLocation;
    String homeLocation;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ArrayList<String> locationList;
    JSONArray jsonArray;
    //A ProgressDialog object
    private ProgressDialog progressDialog;
    String baseServiceURL;

    String from_Location;
    String to_Location;
    String ride_date;
    String ride_time;
    String strLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avail_ride);

        spLocFrom = (Spinner) findViewById(R.id.spLocFrom);
        spLocTo = (Spinner) findViewById(R.id.spLocTo);
        imgBtnDate = (ImageButton) findViewById(R.id.imgBtnDate);
        imgBtnTime = (ImageButton) findViewById(R.id.imgBtnTime);
        etdate = (EditText) findViewById(R.id.etDate);
        ettime = (EditText) findViewById(R.id.etTime);

        Log.d("AAAAA", "Offerrrrrrrrrrrr");


        pref = getSharedPreferences("MobiLyft", MODE_PRIVATE);
        editor = pref.edit();

        /*userID = Integer.parseInt(pref.getString("userID", "0"));
        companyID = Integer.parseInt(pref.getString("companyID", "0"));
        currentbranchID = Integer.parseInt(pref.getString("branchID", "0"));*/
        userID = pref.getInt("userID", 0);
        companyID = pref.getInt("companyID", 0);
        currentbranchID = pref.getInt("branchID", 0);
        username = pref.getString("username", "nil");
        useremail = pref.getString("useremail", "nil");
        strLocationList = pref.getString("locationList", "");
        /*officeLocation = pref.getString("officeLocation", "nil");
        homeLocation = pref.getString("homeLocation", "nil");*/

        officeLocation = pref.getString("avail_from_Location",pref.getString("officeLocation", "nil"));
        homeLocation = pref.getString("avail_to_Location",pref.getString("homeLocation", "nil"));

        imgBtnDate.setOnClickListener(this);
        imgBtnTime.setOnClickListener(this);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);


        etdate.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
        ettime.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();

        //Back button on Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //new ExecuteTaskLoadLocation().execute();
        LoadLocation();

    }


    @Override
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        //this.startActivity(new Intent(AvailRideActivity.this,HomeActivity.class));
        Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);

        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        this.startActivity(homeActivity);

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

    private void LoadLocation()
    {
        try {
            jsonArray = new JSONArray(strLocationList);
            //jsonArray = new JSONArray(result);
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

            ArrayAdapter<String> arrayAdapterLoc = new ArrayAdapter<String>(AvailRideActivity.this, android.R.layout.simple_spinner_item, locationList);

            arrayAdapterLoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spLocFrom
                    .setAdapter(arrayAdapterLoc);
            if (!officeLocation.equals(null)) {
                int itemPosition = arrayAdapterLoc.getPosition(officeLocation);
                spLocFrom.setSelection(itemPosition);
            }


            spLocTo
                    .setAdapter(arrayAdapterLoc);
            if (!homeLocation.equals(null)) {
                int itemPosition = arrayAdapterLoc.getPosition(homeLocation);
                spLocTo.setSelection(itemPosition);
            }
        } catch (Exception e) {
            Log.e("doInBackground ", "" + e.getMessage());
            e.printStackTrace();
        }
    }

    /*class ExecuteTaskLoadLocation extends AsyncTask<Void, Void, Void> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(AvailRideActivity.this);
            progressDialog.setTitle("Loading..");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            //String res=GetData();
            Log.d("ccccccccc", "cccccccccccccccccccccccc");
            //String result = GetData();
            //String result = PostRequest();
            try {
                jsonArray = new JSONArray(strLocationList);
                //jsonArray = new JSONArray(result);
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
            } catch (Exception e) {
                Log.e("doInBackground ", "" + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

       @Override
       protected void onPostExecute(Void args) {

           Log.d("LoadLc","LLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
           Log.d("LoadLc", locationList.toString());
           ArrayAdapter<String> arrayAdapterLoc = new ArrayAdapter<String>(AvailRideActivity.this, android.R.layout.simple_spinner_item, locationList);

           arrayAdapterLoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

           //spLocFrom
           //        .setAdapter(new ArrayAdapter<String>(AvailRideActivity.this, android.R.layout.simple_spinner_item, locationList));

           //spLocTo
           //        .setAdapter(new ArrayAdapter<String>(AvailRideActivity.this, android.R.layout.simple_spinner_item, locationList));

           spLocFrom
                   .setAdapter(arrayAdapterLoc);
           if (!officeLocation.equals(null)) {
               int itemPosition = arrayAdapterLoc.getPosition(officeLocation);
               spLocFrom.setSelection(itemPosition);
           }


           spLocTo
                   .setAdapter(arrayAdapterLoc);
           if (!homeLocation.equals(null)) {
               int itemPosition = arrayAdapterLoc.getPosition(homeLocation);
               spLocTo.setSelection(itemPosition);
           }
           progressDialog.cancel();
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
                jsonStringer.key("BranchID");
                jsonStringer.value("" + currentbranchID + "");
                //jsonStringer.key("Branch");
                //jsonStringer.value("" + currentBranchLocation + "");
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
    }*/

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 999:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, month, day);

            case 888:
                return new TimePickerDialog(this,timePickerListener, hour, minute,false);

        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    // set current time into textview
                    ettime.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)));



                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;
            etdate.setText(new StringBuilder().append(day).append("/")
                    .append(month + 1).append("/").append(year));

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgBtnDate:
                showDialog(999);
                break;

            case R.id.imgBtnTime:
                showDialog(888);
                break;
        }
    }

    public void SearchAvailableRides(View view)
    {
        new ExecuteTaskSearchAvailableRides().execute();;
    }

    class ExecuteTaskSearchAvailableRides extends AsyncTask<Void, Void, Void>
    {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(AvailRideActivity.this);
            progressDialog.setTitle("Searching availables rides..");
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

                jsonArray = new JSONArray(result);
                Log.d("jsonArray", jsonArray.toString());
                Log.d("jsonArray length", String.valueOf(jsonArray.length()));

                /*locationList = new ArrayList<String>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    Log.d("Aaaa",String.valueOf(i));
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    Log.d("jsonObject",jsonObject.toString());
                    Log.d("bbbbbbbbb",String.valueOf(i));
                    Log.d("Spinner Branch id", jsonObject.getString("LocationID"));
                    Log.d("Spinner Branch_Name", jsonObject.getString("LocationName"));
                    //branchesList.add(Integer.parseInt(jsonObject.getString("BranchID")), jsonObject.getString("BranchName"));
                    locationList.add(jsonObject.getString("LocationName"));
                }*/
            } catch (Exception e) {
                Log.e("doInBackground ", "" + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

           /* Log.d("LoadLc","LLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
            Log.d("LoadLc",locationList.toString());
            spLocFrom
                    .setAdapter(new ArrayAdapter<String>(AvailRideActivity.this, android.R.layout.simple_spinner_dropdown_item, locationList));

            spLocTo
                    .setAdapter(new ArrayAdapter<String>(AvailRideActivity.this, android.R.layout.simple_spinner_dropdown_item, locationList));
        */
            Log.d("availRide",jsonArray.toString());
            if (jsonArray == null || Integer.valueOf(jsonArray.length()) == 0)
            {
                //Toast.makeText(getApplicationContext(), "No Rides available",Toast.LENGTH_LONG).show();
                ShowAlert("No Rides available from " + from_Location + " to " + to_Location + ". Would you like to create a request for it?");
            }
            else
            {
                editor.putString("avail_from_Location", from_Location);
                editor.putString("avail_to_Location", to_Location);
                editor.commit();
                Intent darActivity = new Intent(getApplicationContext(), DisplayAvailableRidesActivity.class);
                //intent.putExtra("availableRidesArray", jsonArray.toString());
                //Bundle b = new Bundle();
                Log.d("Post", "PPPPPPPPPPPPPPPPPPPPPOOOOOOOOOOOOOOOOOOOOOOO");
                Log.d("jsonArrayPost", jsonArray.toString());
                darActivity.putExtra("availableRidesList", jsonArray.toString());
                //Log.d("availableRidesList", b.getString("availableRidesList"));
                Log.d("Post", "PPPPPPPPPPPPPPPPPPPPPOOOOOOOOOOOOOOOOOOOOOOO");
                startActivity(darActivity);
            }
            progressDialog.cancel();
        }

        private void ShowAlert(String msg)
        {
            new AlertDialog.Builder(AvailRideActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ExecuteTaskRequestRide().execute();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
        }


       /* private void requestRide()
        {
            try {
                String result = PostRequest(1);
                JSONObject jObj = new JSONObject(result);

                String strResult = jObj.getString("Result");
                String strResultMessage = jObj.getString("ResultMessage");
                //progressDialog.cancel();
                Toast.makeText(getApplicationContext(), strResultMessage,
                        Toast.LENGTH_LONG).show();
                ShowAlert(strResultMessage);

            }
            catch (Exception ex)
            {
                Log.e("doInBackground ", "" + ex.getMessage());
                ex.printStackTrace();
            }
        }*/

        public String readResponse(HttpResponse res) {
            InputStream is=null;
            String return_text="";
            try {
                is=res.getEntity().getContent();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
                String line="";
                StringBuffer sb=new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                {
                    sb.append(line);
                }
                return_text=sb.toString();
            } catch (Exception e)
            {

            }
            return return_text;

        }


        private String PostRequest() {
            String result = "";

            from_Location = spLocFrom.getSelectedItem().toString();
            Log.d("33333333333333","3333333333333333333");
            to_Location = spLocTo.getSelectedItem().toString();
            Log.d("AAAAAAAAAAAAA","AAAAAAAAAAAAAAAAA");
            ride_date = etdate.getText().toString().trim();
            Log.d("bbbbbbbbbbbbbbbbbb","bbbbbbbbbb");
            ride_time = ettime.getText().toString().trim();

            String serviceURL = baseServiceURL;

            serviceURL = serviceURL + "/Ride/SearchRide";

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
                jsonStringer.key("RideUserID");
                jsonStringer.value("" + userID + "");
                jsonStringer.key("RideUserBranchID");
                jsonStringer.value("" + currentbranchID + "");
                jsonStringer.key("FromLocationName");
                jsonStringer.value("" + from_Location + "");
                jsonStringer.key("ToLocationName");
                jsonStringer.value("" + to_Location + "");
                jsonStringer.key("RideDate");
                jsonStringer.value("" + ride_date + "");
                jsonStringer.key("RideTime");
                jsonStringer.value("" + ride_time + "");
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

    class ExecuteTaskRequestRide extends AsyncTask<String, Integer, String>
    {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(AvailRideActivity.this);
            progressDialog.setTitle("Requesting ride..");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            //String res=GetData();
            Log.d("ccccccccc", "cccccccccccccccccccccccc");
            //String result = GetData();
            String result = PostRequest();

            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jObj = new JSONObject(result);

                String strResult = jObj.getString("Result");
                String strResultMessage = jObj.getString("ResultMessage");
                Log.d("strResult", strResult);
                Log.d("strResultMessage", strResultMessage);
                //progressDialog.cancel();
                Toast.makeText(getApplicationContext(), strResultMessage,
                        Toast.LENGTH_LONG).show();
                //ShowAlert(strResultMessage);
                ShowAlert(strResult,strResultMessage);


            }
            catch (Exception ex)
            {
                Log.e("doInBackground ", "" + ex.getMessage());
                ex.printStackTrace();
            }
            progressDialog.cancel();
        }

        private void ShowAlert(String result, String msg)
        {
            final String lresult = result;
            new AlertDialog.Builder(AvailRideActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(lresult.trim().equals("S")) {
                                Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                                homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(homeActivity);
                            }
                        }

                    })
                    .show();
        }


        public String readResponse(HttpResponse res) {
            InputStream is=null;
            String return_text="";
            try {
                is=res.getEntity().getContent();
                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
                String line="";
                StringBuffer sb=new StringBuffer();
                while ((line=bufferedReader.readLine())!=null)
                {
                    sb.append(line);
                }
                return_text=sb.toString();
            } catch (Exception e)
            {

            }
            return return_text;

        }


        private String PostRequest() {
            String result = "";

            from_Location = spLocFrom.getSelectedItem().toString();
            Log.d("33333333333333","3333333333333333333");
            to_Location = spLocTo.getSelectedItem().toString();
            Log.d("AAAAAAAAAAAAA","AAAAAAAAAAAAAAAAA");
            ride_date = etdate.getText().toString().trim();
            Log.d("bbbbbbbbbbbbbbbbbb","bbbbbbbbbb");
            ride_time = ettime.getText().toString().trim();

            String serviceURL = baseServiceURL;

            serviceURL = serviceURL + "/Ride/RequestRide";

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
                jsonStringer.key("RideUserID");
                jsonStringer.value("" + userID + "");
                jsonStringer.key("RideUserBranchID");
                jsonStringer.value("" + currentbranchID + "");
                jsonStringer.key("FromLocationName");
                jsonStringer.value("" + from_Location + "");
                jsonStringer.key("ToLocationName");
                jsonStringer.value("" + to_Location + "");
                jsonStringer.key("RideDate");
                jsonStringer.value("" + ride_date + "");
                jsonStringer.key("RideTime");
                jsonStringer.value("" + ride_time + "");
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

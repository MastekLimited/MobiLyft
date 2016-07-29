package com.mastek.mobilyft;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.app.ToolbarActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int userID;
    int companyID;
    int currentbranchID;

    String username;
    String useremail;

    TextView tvUserName;
    TextView tvEmailID;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private ProgressDialog progressDialog;


    String baseServiceURL;

    String vehicleType;
    String vehicle_RegNo;
    String from_Location;
    String to_Location;
    String ride_date;
    String ride_time;
    Integer totalAvailableSeats;
    Integer chargePerPerson;
    JSONArray jsonArray;

    Integer userSpecificOutput;

    private Boolean exit = false;

    Button btnOffeRideToHome;
    Button btnOffeRideToOffice;

    String regVehiclesList;
    String selectedVehicle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        tvUserName = (TextView)this.findViewById(R.id.tvUserName);
        tvEmailID = (TextView)this.findViewById(R.id.tvEmailID);
        //btnOffeRideToHome = (Button)this.findViewById(R.id.btnOffeRideToHome);
        btnOffeRideToHome = (Button) this.findViewById(R.id.btnOffeRideToHome);
        //btnOffeRideToOffice = (Button)this.findViewById(R.id.btnOffeRideToOffice);
        btnOffeRideToOffice = (Button)this.findViewById(R.id.btnOffeRideToOffice);

        // GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        //userID = globalVariable.getUserID();
        //companyID = globalVariable.getCompanyID();
        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setTitle("Home");

        pref = getSharedPreferences("MobiLyft", MODE_PRIVATE);
        editor = pref.edit();

        /*userID = Integer.parseInt(pref.getString("userID", "nil"));
        companyID = Integer.parseInt(pref.getString("companyID", "nil"));
        currentbranchID = Integer.parseInt(pref.getString("branchID", "nil"));*/
        userID = pref.getInt("userID", 0);
        companyID = pref.getInt("companyID", 0);
        currentbranchID = pref.getInt("branchID", 0);
        username = pref.getString("username", "nil");
        useremail = pref.getString("useremail", "nil");

        tvUserName.setText(username);
        tvEmailID.setText(useremail);

        regVehiclesList =  pref.getString("RegVehicleList", "");
        selectedVehicle = pref.getString("selectedVehicle", "");
        Log.d("regVehiclesList", "regVehiclesList");
        Log.d("regVehiclesList",regVehiclesList);

        if(regVehiclesList.equals("")) {
            if (pref.getString("offer_vehicle_RegNo", "") == "") {
                btnOffeRideToHome.setVisibility(View.GONE);
                btnOffeRideToOffice.setVisibility(View.GONE);
            } else {
                btnOffeRideToHome.setVisibility(View.VISIBLE);
                btnOffeRideToOffice.setVisibility(View.VISIBLE);
            }
        }else {
            btnOffeRideToHome.setVisibility(View.VISIBLE);
            btnOffeRideToOffice.setVisibility(View.VISIBLE);
        }

        userSpecificOutput = 0; //Search All Available Rides

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();
        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


       /* if (!isMyServiceRunning()){
            Intent serviceIntent = new Intent("com.mastek.mobilyft.NotificationService");
            startService(serviceIntent);
        }*/
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NotificationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    @Override
    public void onBackPressed() {
        Log.d("Backpressed","backpressed");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            if (exit) {
                finish(); // finish activity
                System.exit(0);
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_myOfferedRides) {
            userSpecificOutput = 1; //Search All Active Rides offered by current user
            new ExecuteTaskSearchAvailableRides().execute();

        } else if (id == R.id.nav_myAvailedRides) {
            userSpecificOutput = 2; //Search All Active Rides availed by current user
            new ExecuteTaskSearchAvailableRides().execute();
        }
        else if (id == R.id.nav_myRideMembersList) {
            userSpecificOutput = 3; //Search All Active Rides offered by current user to see members list
            new ExecuteTaskSearchAvailableRides().execute();
        }
        else if (id == R.id.nav_requestedRides) {
            userSpecificOutput = 3; //Search All Active Rides offered by current user to see members list
            new ExecuteTaskSearchRequestedRides().execute();
        }
        else if (id == R.id.nav_changeLocation) {
            userSpecificOutput = 0;
            Intent clActivity = new Intent(getApplicationContext(), CurrentLocationActivity.class);
            //Bundle bundle = new Bundle();
            //bundle.putString("callingParent", "menu");
            startActivity(clActivity);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void btnOfferRide_Click(View view)
    {
        if(regVehiclesList.equals("")) {
            Intent vrActivity = new Intent(getApplicationContext(), VehicleRegistrationActivity.class);
            startActivity(vrActivity);
        }
        else {
            Intent offerRideActivity = new Intent(getApplicationContext(), OfferRideActivity.class);
            startActivity(offerRideActivity);
        }
    }

    public void btnAvailRide_Click(View view)
    {
        Intent availRideActivity = new Intent(getApplicationContext(), AvailRideActivity.class);
        startActivity(availRideActivity);
    }

    public void OffeRideToHome(View view)
    {
        userSpecificOutput = 0;
        from_Location = pref.getString("officeLocation", "nil");
        to_Location = pref.getString("homeLocation", "nil");
        new ExecuteTaskSaveOfferRideData().execute();
    }


    public void OffeRideToOffice(View view)
    {
        userSpecificOutput = 0;
        from_Location = pref.getString("homeLocation", "nil");
        to_Location = pref.getString("officeLocation", "nil");
        new ExecuteTaskSaveOfferRideData().execute();
    }



    public void AvailRideToHome(View view)
    {
        userSpecificOutput = 0;
        from_Location = pref.getString("officeLocation", "nil");
        to_Location = pref.getString("homeLocation", "nil");
        new ExecuteTaskSearchAvailableRides().execute();
    }


    public void AvailRideToOffice(View view)
    {
        userSpecificOutput = 0;
        from_Location = pref.getString("homeLocation", "nil");
        to_Location = pref.getString("officeLocation", "nil");
        new ExecuteTaskSearchAvailableRides().execute();
    }


    class ExecuteTaskSaveOfferRideData extends AsyncTask<String, Integer, String> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(HomeActivity.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //String res=GetData();
            Log.d("ccccccccc", "cccccccccccccccccccccccc");
            //String res = GetData();
            //return res;
            String result = PostRequest();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //progess_msz.setVisibility(View.GONE);
            //Toast.makeText(getApplicationContext(), result, 3000).show();
            Log.d("OTP", "ppppppppppppppp");
            //etName.setText(result);
            try {
                JSONObject jObj = new JSONObject(result);

                String strResult = jObj.getString("Result");
                String strResultMessage = jObj.getString("ResultMessage");
                //progressDialog.cancel();
                if (strResult.equals("S")) {
                    Toast.makeText(getApplicationContext(), strResultMessage,
                            Toast.LENGTH_LONG).show();
                    ShowAlert("N",strResultMessage);
                    //Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                    //startActivity(homeActivity);
                } else {
                    Toast.makeText(getApplicationContext(), strResultMessage,
                            Toast.LENGTH_LONG).show();
                    ShowAlert("N",strResultMessage);
                }


            } catch (Exception e) {
                Log.e("onPostExecute ", "" + e.getMessage());
            }
            finally {
                progressDialog.cancel();
            }


        }

        /*private void ShowAlert(String msg)
        {
            new AlertDialog.Builder(HomeActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }

                    })
                    .show();
        }*/

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

            //vehicleType = pref.getString("offer_vehicleType", "nil");
            //vehicle_RegNo = pref.getString("offer_vehicle_RegNo", "nil");

            if(regVehiclesList.equals("")) {
                vehicleType = pref.getString("offer_vehicleType", "");
                Log.d("11111111111", "11111111111111");
                vehicle_RegNo = pref.getString("offer_vehicle_RegNo", "");
                Log.d("222222222222", "22222222222222");
            }
            else if(selectedVehicle.equals(""))
            {
                String[] arrayRegVehiclesList = regVehiclesList.split(",");
                selectedVehicle = arrayRegVehiclesList[0];
            }

            //totalAvailableSeats = pref.getString("offer_totalAvailableSeats", "nil");
            totalAvailableSeats = pref.getInt("offer_totalAvailableSeats", 1);
            //chargePerPerson = pref.getString("offer_chargePerPerson", "nil");
            chargePerPerson = pref.getInt("offer_chargePerPerson", 0);

            String serviceURL = baseServiceURL + "/Ride/OfferRide";
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
                jsonStringer.key("VehicleTypeName");
                jsonStringer.value("" + vehicleType + "");
                jsonStringer.key("VehicleRegNo");
                jsonStringer.value("" + vehicle_RegNo + "");
                jsonStringer.key("FromLocationName");
                jsonStringer.value("" + from_Location + "");
                jsonStringer.key("ToLocationName");
                jsonStringer.value("" + to_Location + "");
                //jsonStringer.key("RideDate");
                //jsonStringer.value("" + ride_date + "");
                //jsonStringer.key("RideTime");
                //jsonStringer.value("" + ride_time + "");
                jsonStringer.key("TotalAvailableSeats");
                jsonStringer.value("" + totalAvailableSeats + "");
                jsonStringer.key("ChargePerPerson");
                jsonStringer.value("" + chargePerPerson + "");
                jsonStringer.key("SelectedVehicle");
                jsonStringer.value("" + selectedVehicle + "");
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

    private void ShowAlert(String newEvent, String msg)
    {
        final String lnewEvent = newEvent;
        new AlertDialog.Builder(HomeActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(lnewEvent.trim().equals("Y")){
                            new ExecuteTaskRequestRide().execute();
                        }
                    }

                })
                .show();
    }

    class ExecuteTaskSearchAvailableRides extends AsyncTask<Void, Void, Void>
    {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(HomeActivity.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            String result = PostRequest();
            try {

                jsonArray = new JSONArray(result);
                Log.d("jsonArray", jsonArray.toString());
                Log.d("jsonArray length", String.valueOf(jsonArray.length()));

            } catch (Exception e) {
                Log.e("doInBackground ", "" + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            Log.d("availRide",jsonArray.toString());
            if (jsonArray == null || Integer.valueOf(jsonArray.length()) == 0)
            {
                String msg = "";
                String newEvent = "N";
                if(userSpecificOutput == 1 || userSpecificOutput == 3)
                {
                    msg = "No Rides Offered";
                    newEvent = "N";
                }
                else if(userSpecificOutput == 2)
                {
                    msg = "No Rides Availed";
                    newEvent = "N";
                }
                else {
                    //Toast.makeText(getApplicationContext(), "No Rides available",Toast.LENGTH_LONG).show();
                    //ShowAlert("No Rides available from " + from_Location + " to " + to_Location);
                    msg = "No Rides available from " + from_Location + " to " + to_Location + ". Would you like to create a request for it?";
                    newEvent = "Y";
                }
                ShowAlert(newEvent,msg);
            }
            else
            {
                Intent darActivity = new Intent(getApplicationContext(), DisplayAvailableRidesActivity.class);
                //intent.putExtra("availableRidesArray", jsonArray.toString());
                //Bundle b = new Bundle();
                Log.d("Post", "PPPPPPPPPPPPPPPPPPPPPOOOOOOOOOOOOOOOOOOOOOOO");
                Log.d("jsonArrayPost", jsonArray.toString());
                //darActivity.putExtra("availableRidesList", jsonArray.toString());
                Log.d("modificationAllowed",String.valueOf(userSpecificOutput));
                //darActivity.putExtra("modificationAllowed", userSpecificOutput);
                //Log.d("availableRidesList", b.getString("availableRidesList"));
                Log.d("Post", "PPPPPPPPPPPPPPPPPPPPPOOOOOOOOOOOOOOOOOOOOOOO");
                Bundle bundle = new Bundle();
                bundle.putString("availableRidesList", jsonArray.toString());
                bundle.putInt("modificationAllowed", userSpecificOutput);
                darActivity.putExtras(bundle);
                startActivity(darActivity);
            }
            progressDialog.cancel();
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

            String serviceURL = baseServiceURL + "/Ride/SearchRide";
            Log.d("servicename",serviceURL);

            HttpPost request = new HttpPost(serviceURL);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

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
                jsonStringer.key("UserSpecificOutput");
                jsonStringer.value("" + userSpecificOutput + "");
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

    class ExecuteTaskSearchRequestedRides extends AsyncTask<Void, Void, Void>
    {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(HomeActivity.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {
            String result = PostRequest();
            try {

                jsonArray = new JSONArray(result);
                Log.d("jsonArray", jsonArray.toString());
                Log.d("jsonArray length", String.valueOf(jsonArray.length()));

            } catch (Exception e) {
                Log.e("doInBackground ", "" + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            Log.d("availRide",jsonArray.toString());
            if (jsonArray == null || Integer.valueOf(jsonArray.length()) == 0)
            {
                String msg = "No rides requested";
                String newEvent = "N";
                ShowAlert(newEvent,msg);
            }
            else
            {
                Intent rrActivity = new Intent(getApplicationContext(), RequestedRidesActivity.class);
                Log.d("Post", "PPPPPPPPPPPPPPPPPPPPPOOOOOOOOOOOOOOOOOOOOOOO");
                Log.d("jsonArrayPost", jsonArray.toString());
                Log.d("Post", "PPPPPPPPPPPPPPPPPPPPPOOOOOOOOOOOOOOOOOOOOOOO");
                Bundle bundle = new Bundle();
                bundle.putString("reqRidesList", jsonArray.toString());
                rrActivity.putExtras(bundle);
                startActivity(rrActivity);
                //startActivity(rrActivity);
            }
            progressDialog.cancel();
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

            from_Location = pref.getString("officeLocation", "nil");
            to_Location = pref.getString("homeLocation", "nil");

            String serviceURL = baseServiceURL + "/Ride/RequestRideList";
            Log.d("servicename",serviceURL);

            HttpPost request = new HttpPost(serviceURL);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

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
            progressDialog  = new ProgressDialog(HomeActivity.this);
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
                ShowAlert("N",strResultMessage);


            }
            catch (Exception ex)
            {
                Log.e("doInBackground ", "" + ex.getMessage());
                ex.printStackTrace();
            }
            progressDialog.cancel();
        }

        /*private void ShowAlert(String msg)
        {
            new AlertDialog.Builder(HomeActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }

                    })
                    .show();
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

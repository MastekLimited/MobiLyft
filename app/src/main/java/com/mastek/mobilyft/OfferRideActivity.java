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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class OfferRideActivity extends AppCompatActivity implements View.OnClickListener {
    //Spinner spVehicleType, spLocFrom, spLocTo, spTotalAvailableSeats;
    Spinner spVehicleType;
    Spinner spLocFrom;
    Spinner spLocTo;
    Spinner spTotalAvailableSeats;
    Spinner spRegVehicleList;
    //List<String> spinnerList1, spinnerList2, spinnerList3;
    ImageButton imgBtnDate;
    ImageButton imgBtnTime;
    //ImageButton imgBtnMinus;
    //ImageButton imgBtnPlus;
    EditText etdate;
    EditText ettime;
    EditText etVehRegNo;
    //EditText etAvailableSeats;
    EditText etChargePerPerson;

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
    private ProgressDialog progressDialog;

    String baseServiceURL;

    String selectedVehicle;
    String vehicleType;
    String vehicle_RegNo;
    String from_Location;
    String to_Location;
    String ride_date;
    String ride_time;
    Integer totalAvailableSeats;
    Integer chargePerPerson;

    LinearLayout llVehicleType;
    LinearLayout llVehRegNo;
    LinearLayout llRegVehLists;

    String regVehiclesList;
    String regVehiclesType;
    String strLocationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_ride);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //android.support.v7.app.ActionBar ab = getSupportActionBar();
        //ab.setTitle("Offer a Ride");

        spVehicleType = (Spinner) findViewById(R.id.spVehicleType);
        spRegVehicleList = (Spinner) findViewById(R.id.spRegVehicles);
        spLocFrom = (Spinner) findViewById(R.id.spLocFrom);
        spLocTo = (Spinner) findViewById(R.id.spLocTo);
        imgBtnDate = (ImageButton) findViewById(R.id.imgBtnDate);
        imgBtnTime = (ImageButton) findViewById(R.id.imgBtnTime);
        //imgBtnMinus = (ImageButton) findViewById(R.id.imgBtnMinus);
        //imgBtnPlus = (ImageButton) findViewById(R.id.imgBtnPlus);
        etdate = (EditText) findViewById(R.id.etDate);
        ettime = (EditText) findViewById(R.id.etTime);
        etVehRegNo = (EditText) findViewById(R.id.etVehRegNo);
        //etAvailableSeats = (EditText) findViewById(R.id.etAvailableSeats);
        spTotalAvailableSeats = (Spinner) findViewById(R.id.spTotalAvailableSeats);
        etChargePerPerson = (EditText) findViewById(R.id.etChargePerPerson);

        llVehicleType = (LinearLayout) findViewById(R.id.llVehicleType);
        llVehRegNo = (LinearLayout) findViewById(R.id.llVehRegNo);
        llRegVehLists = (LinearLayout) findViewById(R.id.llRegVehLists);

        Log.d("AAAAA","Offerrrrrrrrrrrr");


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
        //officeLocation = pref.getString("officeLocation", "nil");
        //homeLocation = pref.getString("homeLocation", "nil");

        officeLocation = pref.getString("offer_from_Location",pref.getString("officeLocation", "nil"));
        homeLocation = pref.getString("offer_to_Location",pref.getString("homeLocation", "nil"));

        vehicleType = pref.getString("offer_vehicleType", "Car");
        regVehiclesType = pref.getString("RegVehicleType", "Car");
        selectedVehicle = pref.getString("selectedVehicle", "");

        vehicle_RegNo = pref.getString("offer_vehicle_RegNo", "");

        regVehiclesList =  pref.getString("RegVehicleList", "");
        strLocationList = pref.getString("locationList", "");

        Log.d("regVehiclesList","regVehiclesList");
        Log.d("regVehiclesList",regVehiclesList);
        //JSONArray jsonArray = null;
        String[] arrayRegVehiclesList = null;

        if(regVehiclesList.equals("")) {
            llVehicleType.setVisibility(View.VISIBLE);
            llVehRegNo.setVisibility(View.VISIBLE);
            llRegVehLists.setVisibility(View.GONE);
            ArrayAdapter<CharSequence> arrayAdapterVehicleType = ArrayAdapter.createFromResource(
                    this, R.array.vehicle_type_array, android.R.layout.simple_spinner_item);
            arrayAdapterVehicleType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spVehicleType.setAdapter(arrayAdapterVehicleType);
            if (!vehicleType.equals(null)) {
                int itemPosition = arrayAdapterVehicleType.getPosition(vehicleType);
                spVehicleType.setSelection(itemPosition);
            }

            etVehRegNo.setText(vehicle_RegNo);
        }
        else
        {
            llVehicleType.setVisibility(View.GONE);
            llVehRegNo.setVisibility(View.GONE);
            llRegVehLists.setVisibility(View.VISIBLE);
            /*try {
                jsonArray = new JSONArray(regVehiclesList);
                arrayRegVehiclesList = new String[jsonArray.length()];

                for (int i = 0; i < jsonArray.length(); i++) {
                    arrayRegVehiclesList[i] = jsonArray.getString(i);
                }*/
                arrayRegVehiclesList = regVehiclesList.split(",");

               /* System.out.println(Arrays.toString(arrayRegVehiclesList));
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            ArrayAdapter<String> arrayAdapterRegVehicles = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayRegVehiclesList);
            arrayAdapterRegVehicles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRegVehicleList.setAdapter(arrayAdapterRegVehicles);
            if (!selectedVehicle.equals(null)) {
                int itemPosition = arrayAdapterRegVehicles.getPosition(selectedVehicle);
                spRegVehicleList.setSelection(itemPosition);
            }
            else {
                spRegVehicleList.setSelection(0);
            }
        }




        //vehicleType = pref.getString("offer_vehicleType", "Car");
        //int itemPosition = ((ArrayAdapter)spVehicleType.getAdapter()).getPosition(vehicleType);
        //spVehicleType.setSelection(itemPosition);

        totalAvailableSeats = pref.getInt("offer_totalAvailableSeats", 1);
        Log.d("111111111111111111","1111111111111111111");
        Log.d("totalAvailableSeats",totalAvailableSeats.toString());
        //etAvailableSeats.setText(totalAvailableSeats);
        Log.d("regVehiclesType",regVehiclesType);
        Log.d("val",getResources().getStringArray(R.array.vehicle_type_array)[0].toString());
        Log.d("Result",String.valueOf(regVehiclesType.trim() == getResources().getStringArray(R.array.vehicle_type_array)[0].toString()));
        //Integer[] seats = new Integer[]{1, 2, 3, 4, 5};
        Integer[] seats;
        if(regVehiclesType.trim().equals(getResources().getStringArray(R.array.vehicle_type_array)[1].toString())) {
            //seats = new Integer[]{1, 2, 3, 4, 5};
            seats = new Integer[]{1};
            Log.d("BikeBike","BikeBikeBikeBikeBikeBike");
        }
        else
        {
            //seats = new Integer[]{1};
            seats = new Integer[]{1, 2, 3, 4, 5};
            Log.d("CarCar","CarCarCarCarCar");
        }
        Log.d("seats",seats.toString());
        ArrayAdapter<Integer> arrayAdapterAvailableSeats = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, seats);
        arrayAdapterAvailableSeats.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTotalAvailableSeats.setAdapter(arrayAdapterAvailableSeats);
        Log.d("00000000000000", "0000000000000000000000");
        if(regVehiclesType == "Car") {
            if (!totalAvailableSeats.equals(null)) {
                int itemPosition = arrayAdapterAvailableSeats.getPosition(totalAvailableSeats);
                spTotalAvailableSeats.setSelection(itemPosition);
            }
        }
        else {
            spTotalAvailableSeats.setSelection(0);
        }

        //chargePerPerson = pref.getString("offer_chargePerPerson", "0");
        chargePerPerson = pref.getInt("offer_chargePerPerson", 0);
        Log.d("2222222222222","22222222222222");
        Log.d("offer_chargePerPerson", chargePerPerson.toString());
        etChargePerPerson.setText(chargePerPerson.toString());
        Log.d("444444444444444", "444444444444444444444444");
        imgBtnDate.setOnClickListener(this);
        imgBtnTime.setOnClickListener(this);


        //imgBtnMinus.setOnClickListener(this);
        //imgBtnPlus.setOnClickListener(this);

        Log.d("5555555555555555555555", "55555555555555555555555555555");
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        Log.d("66666666666666666666666", "666666666666666666666666666");

        etdate.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
        ettime.setText(new SimpleDateFormat("HH:mm").format(calendar.getTime()));

        /*ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter
                .createFromResource(this, R.array.vehicle_type_array,
                        android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        //staticAdapter
         //       .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spVehicleType.setAdapter(staticAdapter);*/

        Log.d("777777777777777777", "77777777777777777777777");
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();

        Log.d("23333333333333","333333333333333333333333");
        //Back button on Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        Log.d("44444444444444444444444", "4444444444444444444444444444444");
        //new ExecuteTaskLoadLocation().execute();
        LoadLocation();
    }


    @Override
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        //this.startActivity(new Intent(OfferRideActivity.this,HomeActivity.class));
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
            JSONArray jsonArray = new JSONArray(strLocationList);
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

            ArrayAdapter<String> arrayAdapterLoc = new ArrayAdapter<String>(OfferRideActivity.this, android.R.layout.simple_spinner_item, locationList);

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
            progressDialog  = new ProgressDialog(OfferRideActivity.this);
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
            //String result = PostRequest();
            try {

                JSONArray jsonArray = new JSONArray(strLocationList);
                //JSONArray jsonArray = new JSONArray(result);
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
            //ArrayAdapter<String> arrayAdapterLoc = new ArrayAdapter<String>(OfferRideActivity.this, android.R.layout.simple_spinner_item, locationList);
            ArrayAdapter<String> arrayAdapterLoc = new ArrayAdapter<String>(OfferRideActivity.this, android.R.layout.simple_spinner_item, locationList);
            arrayAdapterLoc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //spLocFrom
            //        .setAdapter(new ArrayAdapter<String>(OfferRideActivity.this, android.R.layout.simple_spinner_item, locationList));

            //spLocTo
            //        .setAdapter(new ArrayAdapter<String>(OfferRideActivity.this, android.R.layout.simple_spinner_item, locationList));

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
                return new TimePickerDialog(this,
                        timePickerListener, hour, minute,false);

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

            /*case R.id.imgBtnMinus:
                Integer currValue = Integer.valueOf(etChargePerPerson.getText().toString());
                etChargePerPerson.setText(currValue == 0? 0 : currValue - 10);
                break;

            case R.id.imgBtnPlus:
                etChargePerPerson.setText(Integer.valueOf(etChargePerPerson.getText().toString()) + 10);
                break;*/
        }
    }

    public void SaveOfferRideData(View view)
    {
        Log.d("AAAAAAAAAAAAAAAAAAAA","BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB");
        Log.d("regVehiclesList",regVehiclesList);
        Boolean error = false;
        String dateRegExp = "^(((0[1-9]|[12]\\d|3[01])\\/(0[13578]|1[02])\\/((19|[2-9]\\d)\\d{2}))|((0[1-9]|[12]\\d|30)\\/(0[13456789]|1[012])\\/((19|[2-9]\\d)\\d{2}))|((0[1-9]|1\\d|2[0-8])\\/02\\/((19|[2-9]\\d)\\d{2}))|(29\\/02\\/((1[6-9]|[2-9]\\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))))$";
        String timeRegExp = "^([0-9]|0[0-9]|1[0-9]|2[0-3]):[0-5][0-9]$";
        if(regVehiclesList.equals("")) {
            String vehRegNumPattern = "^[A-Z]{2}[ -][0-9]{1,2}(?: [A-Z])?(?: [A-Z]*)? [0-9]{4}$";
            if (etVehRegNo.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Reg. No. is blank", Toast.LENGTH_LONG).show();
                etVehRegNo.setError("Reg. No. is blank");
                Log.d("Error", "Error");
                Log.d("VVVVVV","VVVVVVVVVVVVVVVV");
                error = true;
            }
            else if (!etVehRegNo.getText().toString().matches(vehRegNumPattern)) {
                Toast.makeText(getApplicationContext(), "Invalid vehicle reg. no.", Toast.LENGTH_SHORT).show();
                etVehRegNo.setError("Invalid vehicle reg. no.");
                error = true;
            }
        }
        if(etChargePerPerson.getText().toString().trim().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Charge is blank",Toast.LENGTH_LONG).show();
            etChargePerPerson.setError("Charge is blank");
            Log.d("BBBBBBBBBBBBBBB", "BBBBBBBBBBBBBB");
            error = true;
        }
        else if(etdate.getText().toString().trim().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Date is blank",Toast.LENGTH_LONG).show();
            etdate.setError("Date is blank");
            Log.d("DDDDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDDDD");
            error = true;
        }
        else if(!etdate.getText().toString().matches(dateRegExp)){
            Toast.makeText(getApplicationContext(),"Enter correct date",Toast.LENGTH_LONG).show();
            etdate.setError("Enter correct date");
            Log.d("DDDDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDDDD");
            error = true;
        }
        else if(ettime.getText().toString().trim().equals(""))
        {
            Toast.makeText(getApplicationContext(),"Time is blank",Toast.LENGTH_LONG).show();
            ettime.setError("Time is blank");
            Log.d("EEEEEEEEEEEEEEEEEEE", "EEEEEEEEEEEEEEE");
            error = true;
        }
        else if(!ettime.getText().toString().matches(timeRegExp)){
            Toast.makeText(getApplicationContext(),"Enter correct time",Toast.LENGTH_LONG).show();
            ettime.setError("Enter correct time");
            Log.d("DDDDDDDDDDDDDDDDD", "DDDDDDDDDDDDDDDDD");
            error = true;
        }
        //else {
        if(error == false)
        {
            Log.d("JJJJJJJJJJJJJJJJJJ","JJJJJJJJJJJJJJJJJJJJJJJJ");
            new ExecuteTaskSaveOfferRideData().execute();
        }
    }

    class ExecuteTaskSaveOfferRideData extends AsyncTask<String, Integer, String> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(OfferRideActivity.this);
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

                    //Log.d("onPostExecute ", "OTP verification successful");
                    //Intent clActivity = new Intent(getApplicationContext(), CurrentLocationActivity.class);
                    //clActivity.putExtra("userID", userID);
                    //clActivity.putExtra("companyID", companyID);
                    //startActivity(clActivity);

                    editor.putString("offer_vehicleType", vehicleType);
                    editor.putString("offer_vehicle_RegNo", vehicle_RegNo);
                    editor.putString("offer_from_Location", from_Location);
                    editor.putString("offer_to_Location", to_Location);
                    //editor.putString("offer_totalAvailableSeats", totalAvailableSeats);
                    editor.putInt("offer_totalAvailableSeats", totalAvailableSeats);
                    //editor.putString("offer_chargePerPerson", chargePerPerson);
                    editor.putInt("offer_chargePerPerson", chargePerPerson);
                    if(regVehiclesList.equals(""))
                    {
                        editor.putString("RegVehicleList", vehicleType.trim() + " - " + vehicle_RegNo.trim());
                    }
                    editor.putString("selectedVehicle",selectedVehicle);
                    editor.commit();

                    Log.d("offer_vehicleType", vehicleType);
                    Log.d("offer_vehicle_RegNo", vehicle_RegNo);
                    Log.d("offer_from_Location", from_Location);
                    Log.d("offer_to_Location", to_Location);
                    Log.d("offer_TAS", totalAvailableSeats.toString());
                    Log.d("offer_chargePerPerson", chargePerPerson.toString());

                    Toast.makeText(getApplicationContext(), strResultMessage,
                            Toast.LENGTH_LONG).show();
                    ShowAlert(strResultMessage);
                    Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(homeActivity);
                } else {
                    Toast.makeText(getApplicationContext(), strResultMessage,
                            Toast.LENGTH_LONG).show();
                    ShowAlert(strResultMessage);
                }


            } catch (Exception e) {
                Log.e("onPostExecute ", "" + e.getMessage());
            }
            finally {
                progressDialog.cancel();
            }


        }

        private void ShowAlert(String msg)
        {
            new AlertDialog.Builder(OfferRideActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.app_name)
                    .setMessage(msg)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }

                    })
                    .show();
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
            try
            {
                if(regVehiclesList.equals("")) {
                    vehicleType = spVehicleType.getSelectedItem().toString();
                    Log.d("11111111111", "11111111111111");
                    vehicle_RegNo = etVehRegNo.getText().toString().trim();
                    Log.d("222222222222", "22222222222222");
                }
                else {
                    selectedVehicle = spRegVehicleList.getSelectedItem().toString();
                    Log.d("22222222222", "222222222222");
                }
                from_Location = spLocFrom.getSelectedItem().toString();
                Log.d("33333333333333", "3333333333333333333");
                to_Location = spLocTo.getSelectedItem().toString();
                Log.d("AAAAAAAAAAAAA", "AAAAAAAAAAAAAAAAA");
                ride_date = etdate.getText().toString().trim();
                Log.d("bbbbbbbbbbbbbbbbbb", "bbbbbbbbbb");
                ride_time = ettime.getText().toString().trim();
                Log.d("dddddddddd", "dddddddddddddd");
                //totalAvailableSeats = etAvailableSeats.getText().toString().trim();
                totalAvailableSeats = Integer.parseInt(spTotalAvailableSeats.getSelectedItem().toString());
                Log.d("eeeeeeeeee", "eeeeeeeeeeeee");
                chargePerPerson = Integer.parseInt(etChargePerPerson.getText().toString().trim());
                Log.d("fffffffffff", "ffffffffffffff");

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

            //try {
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
                jsonStringer.key("RideDate");
                jsonStringer.value("" + ride_date + "");
                jsonStringer.key("RideTime");
                jsonStringer.value("" + ride_time + "");
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
            }
            catch (Exception e) {
                e.printStackTrace();
                result = "";
            }
            return  result;
            // Toast.makeText(this, not + " OK ! " + "\n", Toast.LENGTH_LONG).show() ;

        }
    }
}



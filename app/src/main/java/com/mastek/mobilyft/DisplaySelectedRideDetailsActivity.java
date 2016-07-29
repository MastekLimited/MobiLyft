package com.mastek.mobilyft;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
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
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class DisplaySelectedRideDetailsActivity extends AppCompatActivity {

    Bundle extraValues;
    JSONArray jsonArray;

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    int userID;
    int companyID;
    int rideID;

    String username;
    String useremail;

    //Button btnBookRide;
    ImageView imgViewRideUserPhoto;
    TextView tvRideUserName;
    TextView tvFromLocationName;
    TextView tvToLocationName;
    TextView tvVehicleDesc;
    TextView tvRideDateTime;
    TextView tvAvailableSeats;
    TextView tvChargePerPerson;
    TextView tvRideUserMobile;
    TextView tvRideUserGender;
    TextView tvRideUserEmail;
    TextView tvCancellationRemarks;

    EditText etCancellationRemarks;

    ImageView ivVehicleType;

    String baseServiceURL;


    Button btnBookRide;
    Button btnCancelRide;

    int modificationAllowed;

    private ProgressDialog progressDialog;
    String regVehiclesType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_selected_ride_details);

        //btnBookRide = (Button) this.findViewById(R.id.btnBookRide);
        imgViewRideUserPhoto = (ImageView) this.findViewById(R.id.imgViewRideUserPhoto);
        tvRideUserName = (TextView) this.findViewById(R.id.tvRideUserName);
        tvFromLocationName = (TextView) this.findViewById(R.id.tvFromLocationName);
        tvToLocationName = (TextView) this.findViewById(R.id.tvToLocationName);
        tvVehicleDesc = (TextView) this.findViewById(R.id.tvVehicleDesc);
        tvRideDateTime = (TextView) this.findViewById(R.id.tvRideDateTime);
        tvAvailableSeats = (TextView) this.findViewById(R.id.tvAvailableSeats);
        tvChargePerPerson = (TextView) this.findViewById(R.id.tvChargePerPerson);
        tvRideUserMobile = (TextView) this.findViewById(R.id.tvRideUserMobile);
        tvRideUserGender = (TextView) this.findViewById(R.id.tvRideUserGender);
        tvRideUserEmail = (TextView) this.findViewById(R.id.tvRideUserEmail);
        tvCancellationRemarks = (TextView) this.findViewById(R.id.tvCancellationRemarks);

        etCancellationRemarks = (EditText) this.findViewById(R.id.etCancellationRemarks);

        ivVehicleType = (ImageView) this.findViewById(R.id.ivVehicleType);

        btnBookRide = (Button) this.findViewById(R.id.btnBookRide);
        btnCancelRide = (Button) this.findViewById(R.id.btnCancelRide);

        pref = getSharedPreferences("MobiLyft", MODE_PRIVATE);
        editor = pref.edit();

        /*userID = Integer.parseInt(pref.getString("userID", "nil"));
        companyID = Integer.parseInt(pref.getString("companyID", "nil"));*/
        userID = pref.getInt("userID", 0);
        companyID = pref.getInt("companyID", 0);
        username = pref.getString("username", "nil");
        useremail = pref.getString("useremail", "nil");

        regVehiclesType = pref.getString("RegVehicleType", "Car");

        if(regVehiclesType.trim().equals(getResources().getStringArray(R.array.vehicle_type_array)[1].toString())) {
            ivVehicleType.setImageResource(R.drawable.motorcycle);
        }
        else {
            ivVehicleType.setImageResource(R.drawable.car);
        }

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();

        //Back button on Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        /*extraValues = getIntent().getExtras();
        Log.d("extraValues", "extraValues");
        Log.d("extraValues", extraValues.toString());

        Log.d("modificationAllowed",extraValues.getString("ModificationAllowed"));
        modificationAllowed = Integer.parseInt(extraValues.getString("ModificationAllowed"));*/
        String Array = "";
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()) {
            Array = extrasBundle.getString("selectedRideDetails");
            modificationAllowed = extrasBundle.getInt("modificationAllowed",0);
        }
        Log.d("availableRidesList",Array.toString());
        Log.d("modificationAllowed", String.valueOf(modificationAllowed));

        if(modificationAllowed == 1 || modificationAllowed == 2)
        {
            btnBookRide.setVisibility(View.GONE);
            btnCancelRide.setVisibility(View.VISIBLE);
            etCancellationRemarks.setVisibility(View.VISIBLE);
            tvCancellationRemarks.setVisibility(View.VISIBLE);
        }
        else
        {
            btnBookRide.setVisibility(View.VISIBLE);
            btnCancelRide.setVisibility(View.GONE);
            etCancellationRemarks.setVisibility(View.GONE);
            tvCancellationRemarks.setVisibility(View.GONE);
        }

        //String Array= extraValues.getString("selectedRideDetails");
        //List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        try {
            //jsonArray = new JSONArray(Array);

            //JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONObject jsonObject = new JSONObject(Array);
/*
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("tvRideUserName", "RideUserName : " + jsonObject.getString("RideUserName"));
            hm.put("tvFromLocationName","FromLocationName : " + jsonObject.getString("FromLocationName"));
            hm.put("tvToLocationName","ToLocationName : " + jsonObject.getString("ToLocationName"));
            hm.put("tvVehicleRegNo","VehicleRegNo : " + jsonObject.getString("VehicleRegNo"));
            hm.put("tvRideDateTime","RideDateTime : " + jsonObject.getString("RideDateTime"));
            hm.put("tvAvailableSeats","AvailableSeats : " + jsonObject.getString("AvailableSeats"));
            hm.put("tvChargePerPerson","ChargePerPerson : " + jsonObject.getString("ChargePerPerson"));
            hm.put("tvRideUserMobile","RideUserMobile : " + jsonObject.getString("RideUserMobile"));
            rideID = Integer.parseInt(jsonObject.getString("RideId"));
            hm.put("ivRideUserPhoto", Integer.toString(R.drawable.profilepic));
            aList.add(hm);
*/
            /*tvRideUserName.setText("RideUserName : " + jsonObject.getString("RideUserName"));
            tvFromLocationName.setText("FromLocationName : " + jsonObject.getString("FromLocationName"));
            tvToLocationName.setText("ToLocationName : " + jsonObject.getString("ToLocationName"));
            tvVehicleRegNo.setText("VehicleRegNo : " + jsonObject.getString("VehicleRegNo"));
            tvRideDateTime.setText("RideDateTime : " + jsonObject.getString("RideDateTime"));
            tvAvailableSeats.setText("AvailableSeats : " + jsonObject.getString("AvailableSeats"));
            tvChargePerPerson.setText("ChargePerPerson : " + jsonObject.getString("ChargePerPerson"));
            tvRideUserMobile.setText("RideUserMobile : " + jsonObject.getString("RideUserMobile"));
            imgViewRideUserPhoto.findViewById(Integer.parseInt(jsonObject.getString("RideId")));
            rideID = Integer.parseInt(jsonObject.getString("RideId"));*/
            tvRideUserName.setText(jsonObject.getString("RideUserName"));
            tvRideUserMobile.setText(jsonObject.getString("RideUserMobile"));
            tvRideUserEmail.setText(jsonObject.getString("RideUserEmail"));
            tvRideUserGender.setText(jsonObject.getString("RideUserGender"));
            tvFromLocationName.setText(jsonObject.getString("FromLocationName"));
            tvToLocationName.setText(jsonObject.getString("ToLocationName"));
            //tvVehicleRegNo.setText(jsonObject.getString("VehicleRegNo"));
            tvVehicleDesc.setText(jsonObject.getString("VehicleDesc"));
            tvRideDateTime.setText(jsonObject.getString("RideDateTime"));
            tvAvailableSeats.setText(jsonObject.getString("CurrentAvailableSeats"));
            //tvChargePerPerson.setText(jsonObject.getString("ChargePerPerson"));
            tvChargePerPerson.setText(getResources().getString(R.string.Rs) + " " + jsonObject.getString("ChargePerPerson") + "/- per person");
            //imgViewRideUserPhoto.findViewById(Integer.parseInt(jsonObject.getString("RideId")));
            //imgViewRideUserPhoto.setImageResource(Integer.parseInt(jsonObject.getString("RideId")));
            rideID = Integer.parseInt(jsonObject.getString("RideId"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

/*
        // Keys used in Hashmap
        String[] from = { "ivRideUserPhoto","tvRideUserName","tvFromLocationName","tvToLocationName","tvVehicleRegNo"
                ,"tvRideDateTime","tvAvailableSeats","tvChargePerPerson","tvRideUserMobile" };

        // Ids of views in listview_layout
        int[] to = { R.id.ivRideUserPhoto,R.id.tvRideUserName,R.id.tvFromLocationName,R.id.tvToLocationName,R.id.tvVehicleRegNo
                ,R.id.tvRideDateTime,R.id.tvAvailableSeats,R.id.tvChargePerPerson,R.id.tvRideUserMobile};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout, from, to);

        // Getting a reference to listview of main.xml layout file
        ListView listView = ( ListView ) findViewById(R.id.lvDisplayAvailableRides);

        // Setting the adapter to the listView
        listView.setAdapter(adapter);
        Log.d("listView length", String.valueOf(listView.getCount()));
        Log.d("listView", listView.toString());
        Log.d("listView", listView.getAdapter().getItem(0).toString());
        */
    }

    /*@Override
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        this.startActivity(new Intent(DisplaySelectedRideDetailsActivity.this,DisplayAvailableRidesActivity.class));

        return;
    }*/

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

    public void BookRide(View view)
    {
        new ExecuteTaskBookRide().execute();;
    }

    public void CancelRide(View view)
    {
        new ExecuteTaskBookRide().execute();;
    }


    class ExecuteTaskBookRide extends AsyncTask<String, Integer, String>
    {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(DisplaySelectedRideDetailsActivity.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            //try {
                //String res=GetData();
                Log.d("makePostRequest", "cccccccccccccccccccccccc");
                //String res = GetData();
                //res = sendPost();
                result = PostRequest();
                Log.d("makePostRequest", result.toString());
            //}
            //catch(Exception exception)  {

                //Log.d("exception", "" + exception.getMessage());
                //res = exception.getMessage();

            //}
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

                if (strResult.equals("S")) {
                    Toast.makeText(getApplicationContext(), strResultMessage,
                            Toast.LENGTH_LONG).show();
                    Log.d("onPostExecute ", strResultMessage);
                    Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(homeActivity);
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
/*
        public String GetData() {
            String result ="";
            try
            {
                Log.d("dddddddddd","dddddddddddddd");

                String servicename="http://172.16.217.176/RDServices/RDServices.svc/Ride/BookRide";

                //servicename = servicename + "/" + etName.getText() + "/" + etCompemail.getText() + "/" + spGender.getSelectedItem().toString() + "/" + etMobileNo.getText();
                servicename = servicename + "/" + userID;
                servicename = servicename + "/" + rideID;

                Log.d("servicename",servicename);

                HttpClient httpClient=new DefaultHttpClient();
                HttpGet httpPost=new HttpGet(servicename);

                HttpResponse httpResponse=  httpClient.execute(httpPost);

                HttpEntity httpEntity=httpResponse.getEntity();
                result = readResponse(httpResponse);

                Log.d("PostData",""+ result);
            }
            catch(Exception exception)  {

                Log.d("exception", "" + exception.getMessage());

            }
            return result;


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

        /*// HTTP POST request
        private String sendPost() throws Exception {

            Log.d("sendPost","Postttttttttttttttttttttt");
            String url = "http://172.16.217.176/RDServices/RDServices.svc/Ride/BookSelectedRide";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //add reuqest header
            con.setRequestMethod("POST");
            //con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

            String urlParameters = "UserID=" + userID + "&RideID=" + rideID;

            Log.d("urlParameters",urlParameters);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            Log.d("Sending 'POST' ","Sending 'POST' request to URL : " + url);
            Log.d("Post parameters", "Post parameters : " + urlParameters);
            Log.d("Response Code", "Response Code : " + responseCode);
            Log.d("Response Code", "Response Code : " + con.getResponseMessage());
            Log.d("Response Code", "Response Code : " + con.getErrorStream());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            //System.out.println(response.toString());
            Log.d("response","aaaaaaaaaaaaaaaaaaaaaa");
            Log.d("response",response.toString());
            return response.toString();
        }*/

        public String PostRequest() {
            String result = "";
            String remarks = etCancellationRemarks.getText().toString().trim();
// String plate = new String("test");
            // POST request to <service>
            String serviceURL = baseServiceURL + "/Ride/BookSelectedRide";
            //HttpPost request = new HttpPost("http://172.16.217.176/RDServices/RDServices.svc/Ride/BookSelectedRide");
            HttpPost request = new HttpPost(serviceURL);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            //String not = new String(" ");

            // EditText plateEdit = null;
            // Editable plate ;
            // plate =  plateEdit.getText();

            try {
                // Build JSON string
                JSONStringer jsonStringer = new JSONStringer();
                jsonStringer.object();
                jsonStringer.key("UserId");
                jsonStringer.value("" + userID + "");
                jsonStringer.key("RideID");
                jsonStringer.value("" + rideID + "");
                jsonStringer.key("Action");
                jsonStringer.value("" + modificationAllowed + "");
                jsonStringer.key("Remarks");
                jsonStringer.value("" + remarks + "");
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

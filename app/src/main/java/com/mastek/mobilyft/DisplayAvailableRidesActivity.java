package com.mastek.mobilyft;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpResponse;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DisplayAvailableRidesActivity extends AppCompatActivity {

    Bundle bundle;
    JSONArray jsonArray;
    Integer modificationAllowed;
    // Array of strings storing country names
    //String[] countries;// = new String[] {   };

    // Array of integers points to images stored in /res/drawable-ldpi/
    //int[] flags;// = new int[]{   };

    // Array of strings to store currencies
    //String[] currency;// = new String[]{    };
    Integer rideId;
    Integer userID;

    Boolean newVisitor = false;

    //private ProgressDialog progressDialog;
    String baseServiceURL;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_available_rides);

        //Back button on Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Log.d("DisplayAvailable", "DisplayAvailableRidesActivity");
        //progressDialog.cancel();
        //Bundle b = new Bundle();
        /*extraValues = getIntent().getExtras();
        Log.d("extraValues","extraValues");
        Log.d("extraValues", extraValues.toString());

        modificationAllowed = extraValues.getString("ModificationAllowed");
        Log.d("modificationAllowed456", modificationAllowed);
        String Array= extraValues.getString("availableRidesList");*/

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();

        pref = getSharedPreferences("MobiLyft", MODE_PRIVATE);
        editor = pref.edit();

        //userID = Integer.parseInt(pref.getString("userID", "0"));
        userID = pref.getInt("userID", 0);

        String Array = "";
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()) {
            Array = extrasBundle.getString("availableRidesList");
            modificationAllowed = extrasBundle.getInt("modificationAllowed",0);
        }
        Log.d("availableRidesList",Array.toString());
        Log.d("modificationAllowed",String.valueOf(modificationAllowed));



        if(modificationAllowed == 1 || modificationAllowed == 3)
        {
            setTitle("My Offered Rides");

        }
        else if(modificationAllowed == 2)
        {
            setTitle("My Availed Rides");
        }
        else
        {
            setTitle("Available Rides");
        }

        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        try {
            jsonArray = new JSONArray(Array);
            System.out.println(jsonArray.toString(2));
            //countries = new String[jsonArray.length()];
            //currency = new String[jsonArray.length()];
            //flags = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d("Aaaa", String.valueOf(i));
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                //branchesList.add(Integer.parseInt(jsonObject.getString("BranchID")), jsonObject.getString("BranchName"));
                //locationList.add(jsonObject.getString("LocationName"));
                //countries[i] = jsonObject.getString("RideUserEmail");
                //currency[i] = jsonObject.getString("AvailableSeats");
                //flags[i] = R.drawable.profilepic;

                HashMap<String, String> hm = new HashMap<String,String>();
                /*hm.put("tvRideUserName", jsonObject.getString("RideUserName"));
                hm.put("tvFromLocationName","From: " + jsonObject.getString("FromLocationName"));
                hm.put("tvToLocationName","To: " + jsonObject.getString("ToLocationName"));
                hm.put("tvVehicleRegNo","Vehicle Reg. No: " + jsonObject.getString("VehicleRegNo"));
                hm.put("tvRideDateTime","Ride Date Time: " + jsonObject.getString("RideDateTime"));
                hm.put("tvAvailableSeats","Available Seats: " + jsonObject.getString("CurrentAvailableSeats"));
                hm.put("tvChargePerPerson","Charge Per Person: " + jsonObject.getString("ChargePerPerson"));
                hm.put("tvRideUserMobile","Mobile No: " + jsonObject.getString("RideUserMobile"));
                hm.put("ivRideUserPhoto", Integer.toString(R.drawable.profilepic) );*/
                hm.put("tvRideUserName", jsonObject.getString("RideUserName"));
                hm.put("tvFromLocationName",jsonObject.getString("FromLocationName"));
                hm.put("tvToLocationName",jsonObject.getString("ToLocationName"));
                hm.put("tvRideUserGender",jsonObject.getString("RideUserGender"));
                hm.put("tvRideDateTime",jsonObject.getString("RideDateTime"));
                hm.put("tvAvailableSeats",jsonObject.getString("CurrentAvailableSeats") + " Available Seats");
                //hm.put("ivRideUserPhoto", Integer.toString(R.drawable.profilepic) );
                aList.add(hm);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Each row in the list stores country name, currency and flag
        /*List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<10;i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("txt", "Country : " + countries[i]);
            hm.put("cur","Currency : " + currency[i]);
            hm.put("flag", Integer.toString(flags[i]) );
            aList.add(hm);
        }*/

        // Keys used in Hashmap
        /*String[] from = { "ivRideUserPhoto","tvRideUserName","tvFromLocationName","tvToLocationName","tvVehicleRegNo"
                                ,"tvRideDateTime","tvAvailableSeats","tvChargePerPerson","tvRideUserMobile" };

        // Ids of views in listview_layout
        int[] to = { R.id.ivRideUserPhoto,R.id.tvRideUserName,R.id.tvFromLocationName,R.id.tvToLocationName,R.id.tvVehicleRegNo
                        ,R.id.tvRideDateTime,R.id.tvAvailableSeats,R.id.tvChargePerPerson,R.id.tvRideUserMobile};
*/

        String[] from = { "ivRideUserPhoto","tvRideUserName","tvFromLocationName","tvToLocationName","tvRideUserGender","tvRideDateTime","tvAvailableSeats" };

        // Ids of views in listview_layout
        int[] to = { R.id.ivRideUserPhoto,R.id.tvRideUserName,R.id.tvFromLocationName,R.id.tvToLocationName,R.id.tvRideUserGender,R.id.tvRideDateTime,R.id.tvAvailableSeats};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        final SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), aList, R.layout.listview_layout_rides, from, to);

        // Getting a reference to listview of main.xml layout file
        ListView listView = ( ListView ) findViewById(R.id.lvDisplayAvailableRides);

        Log.d("adapter",adapter.toString());
        Log.d("adapter length",String.valueOf(adapter.getCount()));
        Log.d("adapter content",adapter.getItem(0).toString());
        // Setting the adapter to the listView
        listView.setAdapter(adapter);
        Log.d("listView length", String.valueOf(listView.getCount()));
        Log.d("listView", listView.toString());
        Log.d("listView", listView.getAdapter().getItem(0).toString());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                try {
                    JSONObject jsonObject = jsonArray.getJSONObject(position);
                    rideId = jsonObject.getInt("RideId");

                    Log.d("listView record", "listViewlistViewlistViewlistViewlistView");
                    Log.d("parent: ",parent.toString());
                    Log.d("view: ",view.toString());
                    Log.d("position: ",String.valueOf(position));
                    Log.d("id: ",String.valueOf(id));


                    Log.d("Selected record", "SSSSSSSSSSSSSSSSSSSSSS");
                    Log.d("Selected record", jsonObject.toString());
                    /*dsrdActivity.putExtra("selectedRideDetails", jsonObject.toString());

                    Log.d("modificationAllowed123", modificationAllowed);
                    dsrdActivity.putExtra("ModificationAllowed", modificationAllowed);*/
                    //Bundle bundle = new Bundle();
                    bundle = new Bundle();
                    bundle.putString("selectedRideDetails", jsonObject.toString());
                    bundle.putInt("modificationAllowed", modificationAllowed);
                    if(modificationAllowed == 3){
                        //Intent mlActivity = new Intent(getApplicationContext(), MembersListActivity.class);
                        //mlActivity.putExtras(bundle);
                        //startActivity(mlActivity);
                        new ExecuteTaskGetMembersList().execute();
                    }
                    else {
                        Intent dsrdActivity = new Intent(getApplicationContext(), DisplaySelectedRideDetailsActivity.class);
                        dsrdActivity.putExtras(bundle);
                        startActivity(dsrdActivity);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        if(pref.getString("newUser","true").equals("true")){
            if(modificationAllowed == 3){
                ShowAlert("Click on a ride to display members list");
            }
            else {
                ShowAlert("Click on a ride to display ride details");
            }
            editor.putString("newUser","false");
            editor.commit();
        }

    }

    private void ShowAlert(String msg)
    {
        new AlertDialog.Builder(DisplayAvailableRidesActivity.this)
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


    @Override
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        //this.startActivity(new Intent(DisplayAvailableRidesActivity.this,modificationAllowed == 0? AvailRideActivity.class : HomeActivity.class));
        /*if(modificationAllowed == 0)
        {
            Intent availActivity = new Intent(getApplicationContext(), AvailRideActivity.class);

            this.startActivity(availActivity);
        }
        else
        {*/
            Intent homeActivity = new Intent(getApplicationContext(), HomeActivity.class);

            homeActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            homeActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            homeActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            this.startActivity(homeActivity);
       // }
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

    class ExecuteTaskGetMembersList extends AsyncTask<Void, Void, Void>
    {
        /*//Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(DisplayAvailableRidesActivity.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }*/
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
                Toast.makeText(getApplicationContext(), "No Members have availed this ride",
                        Toast.LENGTH_LONG).show();
                //progressDialog.cancel();
            }
            else
            {
                bundle.putString("MembersList", jsonArray.toString());
                Intent mlActivity = new Intent(getApplicationContext(), MembersListActivity.class);
                mlActivity.putExtras(bundle);
                startActivity(mlActivity);
            }
            //progressDialog.cancel();
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

            String serviceURL = baseServiceURL + "/Ride/GetMembersList";
            Log.d("servicename",serviceURL);

            HttpPost request = new HttpPost(serviceURL);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");

            try {
                // Build JSON string
                JSONStringer jsonStringer = new JSONStringer();
                jsonStringer.object();
                jsonStringer.key("RideID");
                jsonStringer.value("" + rideId + "");
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

    /*public void BookRide(View view)
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
            progressDialog  = new ProgressDialog(DisplayAvailableRidesActivity.this);
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

        public String PostRequest() {
            String result = "";
            //String remarks = etCancellationRemarks.getText().toString().trim();
            String remarks = "";
// String plate = new String("test");
            // POST request to <service>
            String serviceURL = baseServiceURL + "/Ride/BookSelectedRide";
            //HttpPost request = new HttpPost("http://172.16.217.176/RDServices/RDServices.svc/Ride/BookSelectedRide");
            HttpPost request = new HttpPost(serviceURL);
            request.setHeader("Accept", "application/json");
            request.setHeader("Content-type", "application/json");


            try {
                // Build JSON string
                JSONStringer jsonStringer = new JSONStringer();
                jsonStringer.object();
                jsonStringer.key("UserId");
                jsonStringer.value("" + userID + "");
                jsonStringer.key("RideID");
                jsonStringer.value("" + rideId + "");
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
    }*/
}

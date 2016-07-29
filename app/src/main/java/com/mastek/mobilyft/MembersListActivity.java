package com.mastek.mobilyft;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
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

public class MembersListActivity extends SwipeListViewActivity {

    JSONArray jsonArray;
    Integer modificationAllowed;
    Integer rideID;
    Integer userID;
    Integer bookRideID;

    //ProgressDialog progressDialog;
    String baseServiceURL;

    ListView lvMembersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_list);

        //Back button on Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Log.d("DisplayAvailable", "DisplayAvailableRidesActivity");

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();

        lvMembersList = (ListView)this.findViewById(R.id.lvMembersList);

        String selectedRideDetails = "";
        String membersList = "";
        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()) {
            selectedRideDetails = extrasBundle.getString("selectedRideDetails");
            modificationAllowed = extrasBundle.getInt("modificationAllowed", 0);
            membersList = extrasBundle.getString("MembersList");
        }
        Log.d("selectedRideDetails",selectedRideDetails.toString());
        Log.d("modificationAllowed",String.valueOf(modificationAllowed));
        Log.d("MembersList",membersList);

        List<HashMap<String,String>> listSelectedRideDetails = new ArrayList<HashMap<String,String>>();
        List<HashMap<String,String>> listSelectedRideMembers = new ArrayList<HashMap<String,String>>();

        try {
            jsonArray = new JSONArray(membersList);

            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d("Aaaa", String.valueOf(i));
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                rideID = jsonObject.getInt("RideId");
                userID = jsonObject.getInt("RideUserID");
                bookRideID = jsonObject.getInt("BookRideId");
                /*HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("tvMemberName", "Name: " + jsonObject.getString("RideUserName"));
                hm.put("tvMemberEmail","Email ID: " + jsonObject.getString("RideUserEmail"));
                hm.put("tvMemberMobile","Mobile No: " + jsonObject.getString("RideUserMobile"));
                hm.put("tvGender","Gender: " + jsonObject.getString("RideUserGender"));
                hm.put("ivMemberPhoto", Integer.toString(R.drawable.profilepic) );
                listSelectedRideMembers.add(hm);*/
                HashMap<String, String> hm = new HashMap<String,String>();
                hm.put("tvMemberName", jsonObject.getString("RideUserName"));
                hm.put("tvMemberEmail",jsonObject.getString("RideUserEmail"));
                hm.put("tvMemberMobile", jsonObject.getString("RideUserMobile"));
                hm.put("tvGender",jsonObject.getString("RideUserGender"));
                //hm.put("ivMemberPhoto", Integer.toString(R.drawable.profilepic) );
                listSelectedRideMembers.add(hm);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Keys used in Hashmap
        String[] from = { "ivMemberPhoto","tvMemberName","tvMemberEmail","tvMemberMobile","tvGender"};

        // Ids of views in listview_layout
        int[] to = { R.id.ivMemberPhoto,R.id.tvMemberName,R.id.tvMemberEmail,R.id.tvMemberMobile,R.id.tvGender};

        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        //SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), listSelectedRideDetails, R.layout.listview_layout_members, from, to);
        SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), listSelectedRideMembers, R.layout.listview_layout_members, from, to);

        // Getting a reference to listview of main.xml layout file
        //ListView listView = ( ListView ) findViewById(R.id.lvMembersList);

        Log.d("adapter",adapter.toString());
        Log.d("adapter length",String.valueOf(adapter.getCount()));
        Log.d("adapter content",adapter.getItem(0).toString());
        // Setting the adapter to the listView
        lvMembersList.setAdapter(adapter);
        Log.d("listView length", String.valueOf(lvMembersList.getCount()));
        Log.d("listView", lvMembersList.toString());
        Log.d("listView", lvMembersList.getAdapter().getItem(0).toString());

      /*  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                    if (swipeListener.swipeDetected()) {
                        if (swipeListener.getAction() == Action.LR) {
// perform any task

                            Toast.makeText(getApplicationContext(), "Action.LR",
                                    Toast.LENGTH_LONG).show();
                        }
                        if (swipeListener.getAction() == Action.RL) {
// perform any task
                            Toast.makeText(getApplicationContext(), "Action.RL",
                                    Toast.LENGTH_LONG).show();
                        }

                    }
            }
        });*/
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
    public ListView getListView() {
        return lvMembersList;
    }

    @Override
    public void getSwipeItem(boolean isRight, int position) {
        Log.d("Swipe","Swipe direction");
        Log.d("Swipe","Swipe to " + (isRight ? "right" : "left") + " direction");
        Toast.makeText(this,
                "Swipe to " + (isRight ? "right" : "left") + " direction",
                Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want to reject?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        //System.exit(0);
                        new ExecuteTaskRejectMember().execute();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onItemClickListener(ListAdapter adapter, int position) {
        Log.d("Click","Click");
        Log.d("Swipe", "Single tap on item position " + position);
        //Toast.makeText(this, "Single tap on item position " + position,Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Swipe a member to reject",Toast.LENGTH_SHORT).show();
    }



    class ExecuteTaskRejectMember extends AsyncTask<String, Integer, String>
    {

       /* //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(MembersListActivity.this);
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }*/

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
           /* finally {
                progressDialog.cancel();
            }*/

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
                jsonStringer.value("" + rideID + "");
                jsonStringer.key("Action");
                jsonStringer.value("" + modificationAllowed + "");
                jsonStringer.key("BookRideId");
                jsonStringer.value("" + bookRideID + "");
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

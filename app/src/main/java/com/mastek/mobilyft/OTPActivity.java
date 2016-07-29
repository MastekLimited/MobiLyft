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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

public class OTPActivity  extends AppCompatActivity  {

    Button btnSubmitOTP;
    EditText etOTP;
    TextView tvOTPEmailID;

    int userID;
    int companyID;

    String username;
    String useremail;

    String otp;


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;
    String baseServiceURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        btnSubmitOTP = (Button) this.findViewById(R.id.btnSubmitOTP);
        etOTP = (EditText) this.findViewById(R.id.etOTP);
        tvOTPEmailID = (TextView) this.findViewById(R.id.tvOTPEmailID);
        //extraValues = getIntent().getExtras();
        //if (extraValues != null) {
          //  userID = extraValues.getString("userID");
            //companyID = extraValues.getString("companyID");
        //}
        //final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
       // userID = globalVariable.getUserID();
        //companyID = globalVariable.getCompanyID();

        pref = getSharedPreferences("MobiLyft", MODE_PRIVATE);
        editor = pref.edit();

        /*userID = Integer.parseInt(pref.getString("userID", "nil"));
        companyID = Integer.parseInt(pref.getString("companyID", "nil"));*/
        userID = pref.getInt("userID", 0);
        companyID = pref.getInt("companyID", 0);
        username = pref.getString("username", "nil");
        useremail = pref.getString("useremail", "nil");
        tvOTPEmailID.append(useremail);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();
        //Back button on Action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        //this.startActivity(new Intent(OTPActivity.this,RegistrationActivity.class));
        Intent regActivity = new Intent(getApplicationContext(), RegistrationActivity.class);

        regActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        regActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        regActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        this.startActivity(regActivity);

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

    public void VerifyOTP(View view)
    {
        try {
            Log.d("VerifyOTP", "66666666666666666");
            if(etOTP.getText().toString().trim().equals(""))
            {
                Toast.makeText(getApplicationContext(),"OTP is blank",Toast.LENGTH_LONG).show();
                etOTP.setError("OTP is required");
            }
            else {
                new ExecuteTaskOTP().execute();
            }
            Log.d("VerifyOTP", "777777777777777777");

        } catch (Exception e) {
            Log.e("Click E VerifyOTP ", e.getMessage());
        }
    }


    class ExecuteTaskOTP extends AsyncTask<String, Integer, String> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(OTPActivity.this);
            progressDialog.setTitle("Verifying OTP..");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
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

                if (strResult.equals("S")) {
                    Toast.makeText(getApplicationContext(), "OTP verification successful",
                            Toast.LENGTH_LONG).show();
                    Log.d("onPostExecute ", "OTP verification successful");
                    editor.putString("register", "true");
                    editor.putString("regLoc", "false");
                    editor.putString("regVeh", "false");
                    editor.commit();
                    Intent clActivity = new Intent(getApplicationContext(), CurrentLocationActivity.class);
                    //clActivity.putExtra("userID", userID);
                    //clActivity.putExtra("companyID", companyID);
                    //Bundle bundle = new Bundle();
                    //bundle.putString("callingParent", "otp");
                    startActivity(clActivity);
                } else {
                    Toast.makeText(getApplicationContext(), "Enter correct OTP",
                            Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                Log.e("onPostExecute ", "" + e.getMessage());
            }
            finally {
                progressDialog.cancel();
            }

        }


      /*  public String GetData() {
            String result = "";
            Log.d("OTP", "xxxxxxxxxxxxxxxxxxxxx");
            otp = etOTP.getText().toString().trim();

            String servicename = "http://172.16.217.176/RDServices/RDServices.svc/User/VerifyOTP";
            try {
                //servicename = servicename + "/" + etName.getText() + "/" + etCompemail.getText() + "/" + spGender.getSelectedItem().toString() + "/" + etMobileNo.getText();
                servicename = servicename + "/" + userID + "/" + otp;

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
            otp = etOTP.getText().toString().trim();

            String serviceURL = baseServiceURL + "/User/VerifyOTP";
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
                jsonStringer.key("OTP");
                jsonStringer.value("" + otp + "");
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


    public void ResendOTP(View view)
    {
        new ExecuteTaskResendOTP().execute();
    }

    class ExecuteTaskResendOTP extends AsyncTask<String, Integer, String> {

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(OTPActivity.this);
            progressDialog.setTitle("Sending OTP..");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(false);
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

                    Toast.makeText(getApplicationContext(), strResultMessage,
                            Toast.LENGTH_LONG).show();

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
            otp = etOTP.getText().toString().trim();

            String serviceURL = baseServiceURL + "/User/ResendOTP";
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

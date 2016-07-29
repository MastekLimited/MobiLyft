package com.mastek.mobilyft;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import android.provider.Settings.Secure;

//public class RegistrationActivity  extends AppCompatActivity implements View.OnClickListener {
public class RegistrationActivity  extends AppCompatActivity {

    Button btnSubmit;
    EditText etName;
    EditText etCompemail;
    EditText etMobileNo;
    //Spinner spGender;
    RadioGroup rgGender;
    RadioButton rbSelectedGender;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ProgressDialog progressDialog;

    String baseServiceURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Log.e("URL", "llllllllllllllllllllll");
        btnSubmit = (Button) this.findViewById(R.id.submit);
        etName = (EditText) this.findViewById(R.id.name);
        etCompemail = (EditText) this.findViewById(R.id.compemail);
        etMobileNo = (EditText) this.findViewById(R.id.mobileno);
        //spGender = (Spinner) this.findViewById(R.id.gender);
        rgGender = (RadioGroup) this.findViewById(R.id.rgGender);

        pref = getSharedPreferences("MobiLyft", MODE_PRIVATE);
        editor = pref.edit();

        editor.putString("register","false");
        editor.commit();

        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
        baseServiceURL = globalVariable.getBaseServiceURL();

        //btnSubmit.setOnClickListener(this);
    }


    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

   /* @Override
    public void onClick(View arg0) {
        try {

            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            Log.d("etName",etName.getText().toString());
            Log.d("etCompemail",etCompemail.getText().toString());
            Log.d("etMobileNo",etMobileNo.getText().toString());

            if(etName.getText().toString().trim().equals(""))
            {
                Toast.makeText(getApplicationContext(),"Name is blank",Toast.LENGTH_LONG).show();
                etName.setError("Name is required");
            }
            else if(etCompemail.getText().toString().trim().equals(""))
            {
                Toast.makeText(getApplicationContext(),"Email ID is blank",Toast.LENGTH_LONG).show();
                etCompemail.setError("Email ID is required");
            }
            else if(!etCompemail.getText().toString().matches(emailPattern))
            {
                Toast.makeText(getApplicationContext(),"Invalid Email ID",Toast.LENGTH_SHORT).show();
                etCompemail.setError("Invalid Email ID");
            }
            else if(etMobileNo.getText().toString().trim().equals(""))
            {
                Toast.makeText(getApplicationContext(),"Mobile No. is blank",Toast.LENGTH_LONG).show();
                etMobileNo.setError("Mobile No. is required");
            }
            else {
                new ExecuteTaskRegisterUser().execute();
            }

        } catch (Exception e) {
            Log.e("Click Exception ", e.getMessage());
        }
    }*/

    public void RegisterUser(View view)
    {
        try {
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            Log.d("etName", etName.getText().toString());
            Log.d("etCompemail", etCompemail.getText().toString());
            Log.d("etMobileNo", etMobileNo.getText().toString());

            if (etName.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Name is blank", Toast.LENGTH_LONG).show();
                etName.setError("Name is required");
            } else if (etCompemail.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Email ID is blank", Toast.LENGTH_LONG).show();
                etCompemail.setError("Email ID is required");
            } else if (!etCompemail.getText().toString().matches(emailPattern)) {
                Toast.makeText(getApplicationContext(), "Invalid Email ID", Toast.LENGTH_SHORT).show();
                etCompemail.setError("Invalid Email ID");
            } else if (etMobileNo.getText().toString().trim().equals("")) {
                Toast.makeText(getApplicationContext(), "Mobile No. is blank", Toast.LENGTH_LONG).show();
                etMobileNo.setError("Mobile No. is required");
            } else {
                new ExecuteTaskRegisterUser().execute();
            }
        }
     catch (Exception e) {
            Log.e("Click Exception ", e.getMessage());
        }
    }

    class ExecuteTaskRegisterUser extends AsyncTask<String, Integer, String> {


        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog  = new ProgressDialog(RegistrationActivity.this);
            progressDialog.setTitle("Registration in progress..");
            progressDialog.setMessage("Please wait");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            //String res=GetData();
            ////String res = GetData();
            String result = PostRequest();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //progess_msz.setVisibility(View.GONE);
            //Toast.makeText(getApplicationContext(), result, 3000).show();
            Log.e("URL", "ppppppppppppppp");
            //etName.setText(result);
            try {

                //final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
                JSONObject jObj = new JSONObject(result);

                String strResult = jObj.getString("Result");
                String strResultMessage = jObj.getString("ResultMessage");
                Log.d("strResult", strResult);
                Log.d("strResultMessage", strResultMessage);

                if (strResult.equals("S")) {
                    Log.d("strResultMessage123", strResultMessage.substring(strResultMessage.indexOf(',') + 1, strResultMessage.length() - 1));
                    String[] values = strResultMessage.split(",");

                    //String userID = strResultMessage.substring(0,strResultMessage.indexOf(','));
                    //String companyID = strResultMessage.substring(strResultMessage.indexOf(',')+1);

                    //values=strResultMessage.split(",");

                    Log.d("userID", values[0]);
                    Log.d("companyID", values[1]);
                    Log.d("username", values[2]);
                    Log.d("useremail", values[3]);

                    /*editor.putString("userID", values[0]);
                    editor.putString("companyID", values[1]);*/
                    editor.putInt("userID", Integer.parseInt(values[0]));
                    editor.putInt("companyID", Integer.parseInt(values[1]));
                    editor.putString("username", values[2]);
                    editor.putString("useremail", values[3]);

                    editor.commit();


                    Intent otpActivity = new Intent(getApplicationContext(), OTPActivity.class);
                    //otpActivity.putExtra("userID", userID);
                    //otpActivity.putExtra("companyID", companyID);
                    //globalVariable.setUserID(Integer.parseInt(userID));
                    //globalVariable.setCompanyID(Integer.parseInt(companyID));

                    startActivity(otpActivity);
                }else
                {
                    Toast.makeText(getApplicationContext(), "Enter Correct Details",
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
            try {
                String name = URLEncoder.encode(etName.getText().toString().trim()).replace("+", "%20");
                String compEmail = URLEncoder.encode(etCompemail.getText().toString().trim()).replace("+", "%20");
                String mobileNo = etMobileNo.getText().toString().trim();
                String gender = spGender.getSelectedItem().toString().substring(0, 1);
                //String servicename = "http://172.16.217.176/RDServices/RDServices.svc/User/RegUserDetails";
                String servicename = baseServiceURL + "/User/RegUserDetails";

                //servicename = servicename + "/" + etName.getText() + "/" + etCompemail.getText() + "/" + spGender.getSelectedItem().toString() + "/" + etMobileNo.getText();
                servicename = servicename + "/" + name + "/" + compEmail + "/" + gender + "/" + mobileNo;

                Log.d("name", etName.getText() + " - " + name);
                Log.d("compEmail", etCompemail.getText() + " - " + compEmail);
                Log.d("mobileNo", etMobileNo.getText() + " - " + mobileNo);
                Log.d("gender", spGender.getSelectedItem().toString() + " - " + gender);
                Log.d("servicename", servicename);
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpPost = new HttpGet(servicename);
//            Log.e("PostData","22");
//            List<NameValuePair> list=new ArrayList<NameValuePair>();
//            list.add(new BasicNameValuePair("OrderID", "10248"));
//            Log.e("PostData", "33");
                // httpPost.setEntity(new UrlEncodedFormEntity(list));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                Log.d("PostData", "44");
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
// String plate = new String("test");
            // POST request to <service>
            /*String name = URLEncoder.encode(etName.getText().toString().trim()).replace("+", "%20");
            String compEmail = URLEncoder.encode(etCompemail.getText().toString().trim()).replace("+", "%20");
            String mobileNo = etMobileNo.getText().toString().trim();
            String gender = spGender.getSelectedItem().toString().substring(0, 1);*/
            String name = etName.getText().toString().trim();
            String compEmail = etCompemail.getText().toString().trim();
            String mobileNo = etMobileNo.getText().toString().trim();
            //String gender = spGender.getSelectedItem().toString().substring(0, 1);
            // get selected radio button from radioGroup
            int selectedId = rgGender.getCheckedRadioButtonId();

            // find the radiobutton by returned id
            rbSelectedGender = (RadioButton) findViewById(selectedId);
            String gender = rbSelectedGender.getText().toString();

            /*TelephonyManager mTelephonyMgr;
            mTelephonyMgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

            String deviceID = mTelephonyMgr.getDeviceId();*/
            //String deviceID = "";
            String deviceID = Secure.getString(getContentResolver(),Secure.ANDROID_ID);
            //String servicename = "http://172.16.217.176/RDServices/RDServices.svc/User/RegUserDetails";
            String serviceURL = baseServiceURL + "/User/UserReg";
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
                jsonStringer.key("UserName");
                jsonStringer.value("" + name + "");
                jsonStringer.key("EmailId");
                jsonStringer.value("" + compEmail + "");
                jsonStringer.key("Gender");
                jsonStringer.value("" + gender + "");
                jsonStringer.key("MobileNumber");
                jsonStringer.value("" + mobileNo + "");
                jsonStringer.key("DeviceID");
                jsonStringer.value("" + deviceID + "");
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

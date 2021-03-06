package com.example.aviwe.pelebox;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aviwe.pelebox.checkInternetConnection.ConnectionDetector;
import com.example.aviwe.pelebox.pojos.Confirmation;
import com.example.aviwe.pelebox.pojos.MediPackClient;
import com.example.aviwe.pelebox.pojos.UserClient;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyServices extends android.app.job.JobService
{
    MJobExecuter mJobExecuter;
    static String tokenVal = null,convertedUserid;
    static int userid;
    int number=1, mainuserid;
    private String newTime;
    ConnectionDetector connectionDetector;
    final DataBaseHelpe myHelper = new DataBaseHelpe(this);
    public static String apiLink = "https://www.pelebox.com/MedipackService/Medipack/";
    public static String device_id = "1" ;
    public  String stringValue = "\"Unauthorized access!\"";
    public  String stringVal = "\"Cannot find table 0.\"";
    public String strValu = "\"ExecuteReader requires an open and available Connection. The connection's current state is open.\"";

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onStartJob(final android.app.job.JobParameters jobParameters) {
        jobFinished(jobParameters,false);

        // check internet connection
        mJobExecuter = new MJobExecuter()
        {
            @Override
            protected void onPostExecute(String s) {

                connectionDetector = new ConnectionDetector(MyServices.this);
                final String MY_PREFS_NAME = "MyPrefsFile";
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                tokenVal =MainActivity.newtoken;
                userid = MainActivity.userloginid;
                convertedUserid = String.valueOf(userid);

                refreshRecyclerview();
                if (MainActivity.isLogedIn == true)
                {
                    //Toast.makeText(MyServices.this, "is syncing", Toast.LENGTH_SHORT).show();
                    if (tokenVal != null) {
                        if (connectionDetector.isNetworkAvailable()) {
                            getcloudData();
                            updateCloudData();
                        }
                    }
                }
                else{
                   // Toast.makeText(MyServices.this, "Is no syncing", Toast.LENGTH_SHORT).show();
                }

                jobFinished(jobParameters,false);
            }
        };

        mJobExecuter.execute();
        return true;
    }

    @Override
    public boolean onStopJob(android.app.job.JobParameters jobParameters) {
        mJobExecuter.cancel(true);

        return false;
    }

    public  void getcloudData()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiLink +"GetCloudData/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        try
                        {
                            if(response.equalsIgnoreCase(stringValue) || response.equalsIgnoreCase(stringVal) || response.equalsIgnoreCase(strValu))
                            {
                                Toast.makeText(MyServices.this, " Failed To Sync " , Toast.LENGTH_SHORT).show();
                                ArrayList<UserClient> users = myHelper.getAllUsers();

                                for (UserClient userClients : users)
                                {
                                    if (userClients.getUserclientId() == userid )
                                    {
                                        Toast.makeText(MyServices.this, " There is a user " , Toast.LENGTH_SHORT).show();
                                        myHelper.DeleteUser(userid);
                                        Intent dialogIntent = new Intent(MyServices.this, MainActivity.class);
                                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(dialogIntent);
                                    }
                                }
                            }
                            else
                                {

                                //Toast.makeText(MyServices.this, "Time out  " + MainActivity.newTimeout  , Toast.LENGTH_SHORT).show();
                                Toast.makeText(MyServices.this, "getting cloud data" , Toast.LENGTH_SHORT).show();

                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray medipackArray = jsonObject.getJSONArray("MediPackList");

                                ArrayList<Confirmation> confirmationList = new ArrayList<>();

                                Confirmation conf;
                                for (int i = 0; i < medipackArray.length(); i++) {
                                    JSONObject medipackObject = medipackArray.getJSONObject(i);

                                    //MediPackClient pojo object
                                    MediPackClient medi = new MediPackClient();

                                    medi.setMediPackId(medipackObject.getInt("MediPackId"));
                                    medi.setPatientFisrtName(medipackObject.getString("PatientFirstName"));
                                    medi.setPatientLastName(medipackObject.getString("PatientLastName"));
                                    medi.setPatientCellphone(medipackObject.getString("PatientCellphone"));
                                    medi.setMediPackBarcode(medipackObject.getString("MediPackBarcode"));
                                    medi.setPatientRSA(medipackObject.getString("PatientRSA"));
                                    medi.setManifestNumber(medipackObject.getString("ManifestNumber"));
                                    medi.setDeviceId(medipackObject.getInt("DeviceId"));
                                    // medi.setInUserId(medipackObject.getInt("InUserId"));
                                    medi.setMediPackDueDateTime(medipackObject.getString("MediPackDueDateTime"));
                                    medi.setMediPackStatusId(medipackObject.getInt("MediPackStatusId"));
                                    medi.setDirtyFlag(medipackObject.getInt("DirtyFlag"));

                                    int myid = medi.getMediPackId();

                                    switch (medi.getDirtyFlag()) {
                                        case 1:
                                            MediPackClient mediPackClient = myHelper.getMedipackDetails(myid);

                                            if (mediPackClient != null) {
                                                myHelper.UpdateMedipackData(medi);
                                            } else {
                                                String names = medi.getPatientLastName();
                                                names.toLowerCase();
                                                myHelper.addDataFromCloud(medi);
                                            }
                                            conf = new Confirmation(myid, 0);
                                            confirmationList.add(conf);
                                            break;
                                        case 2:
                                            myHelper.UpdateMedipackData(medi);
                                            conf = new Confirmation(myid, 0);
                                            confirmationList.add(conf);
                                            break;
                                        case 3:
                                            conf = new Confirmation(myid, medi.getDirtyFlag());
                                            confirmationList.add(conf);
                                            myHelper.DeleteMediPackData(medi.getMediPackId());
                                            break;
                                        default : break;
                                    }
                                }

                                updateTokenTime();
                                Gson gson = new Gson();
                                String jsonCartList = gson.toJson(confirmationList);
                                sendConfirmationToCloud(jsonCartList);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Token", tokenVal);
                params.put("DeviceId", device_id);
                params.put("UserId", convertedUserid);
                return params;
            }
        };

        MySingleton.getmInstance(MyServices.this).addToRequestQue(stringRequest);
    }

    public void sendConfirmationToCloud( final String jsonCartList)
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiLink +"SendConfirmationToCloud/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Token", tokenVal);
                params.put("DeviceId", device_id);
                params.put("UserId", convertedUserid);
                params.put("confList ", jsonCartList);

                return params;
            }
        };

        MySingleton.getmInstance(MyServices.this).addToRequestQue(stringRequest);
    }

    public  void updateCloudData()
    {
        final ArrayList<MediPackClient> updateList  = GetPendingMedipackData();
        Gson gson = new Gson();
        final String penidngMediPackList  = gson.toJson(updateList);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiLink +"UpdateCloudData/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        if(response.equalsIgnoreCase(stringValue) || response.equalsIgnoreCase(stringVal))
                        {
                            Toast.makeText(MyServices.this, " Failed To Sync " , Toast.LENGTH_SHORT).show();
                            ArrayList<UserClient> users = myHelper.getAllUsers();

                            for (UserClient userClients : users)
                            {
                                if (userClients.getUserclientId() == userid )
                                {
                                    Toast.makeText(MyServices.this, " There is a user " , Toast.LENGTH_SHORT).show();
                                    myHelper.DeleteUser(userid);
                                    Intent dialogIntent = new Intent(MyServices.this, MainActivity.class);
                                    dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(dialogIntent);
                                }
                            }
                        }
                        else if (response.equals("true"))
                        {
                            //update local database
                            for(int i=0; i< updateList.size();i++)
                            {
                                myHelper.UpdateDirtyFlag(updateList.get(i).getMediPackId());
                            }
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("Token", tokenVal);
                params.put("DeviceId", device_id);
                params.put("UserId", convertedUserid);
                params.put("MedipackList",penidngMediPackList);

                return params;
            }
        };

        MySingleton.getmInstance(MyServices.this).addToRequestQue(stringRequest);
    }

    public ArrayList<MediPackClient> GetPendingMedipackData()
    {
        ArrayList<MediPackClient> updateList=new ArrayList<MediPackClient>();
        updateList= myHelper.getAllMediPackCollectedForUpDate();

        return updateList;
    }

    public void refreshRecyclerview()
    {
        ArrayList<MediPackClient> mediPackClients=myHelper.getAllMediPack();
    }

    //Updating the token time out when ever sync
    public void updateTokenTime()
    {
        mainuserid = MainActivity.userloginid;
        String userDatabaseTokenTime = "";

        ArrayList<UserClient> users = myHelper.getAllUsers();

        for (UserClient userClients : users)
        {
            if (userClients.getUserclientId() == mainuserid )
            {
                userDatabaseTokenTime = userClients.getTimeout();
            }
        }

        //Getting the current date time and add 60 minutes to it
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = null;
        try
        {
            d = df.parse(userDatabaseTokenTime);
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(d);
        c.add(Calendar.MINUTE, 30);
        newTime = df.format(c.getTime());

       // Toast.makeText(MyServices.this, " NEW UPDATED SYNC TIME " + " " + newTime , Toast.LENGTH_SHORT).show();
        myHelper.updateTokenTimeout(mainuserid,newTime);
    }

}

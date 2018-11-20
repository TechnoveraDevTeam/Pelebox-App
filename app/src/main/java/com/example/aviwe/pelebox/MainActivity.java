package com.example.aviwe.pelebox;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.aviwe.pelebox.checkInternetConnection.ConnectionDetector;
import com.example.aviwe.pelebox.forgotPassword.RequestPasswordActivity;
import com.example.aviwe.pelebox.pojos.UserClient;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements TextWatcher,CompoundButton.OnCheckedChangeListener
{
   // declaration
    private EditText password,user_email;
    private Button btnLogin;
    private TextView requestPassword;
    private boolean found = false;
    String formattedTime ="";
    public static Boolean isLogedIn = false;

    //for the toast
    RelativeLayout holder, holder2;
    TextView customText, customText2;


    private ProgressDialog dialog;
    static public CheckBox ckRemember;
    DataBaseHelpe myHelper;

    //Object of the pojo
    UserClient userClient;

    JSONObject jsonObject;
    String userPassword,userEmail;
    boolean valid = true;

    public static String newtoken = null;
    public static String newTimeout = null;
    public static String user_name,user_surname,uEmail,uPassword,loginType,databaseNewTime;
    public static int userloginid;
    //Shared Preferences for remember me function
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private static final String PREF_NAME = "prefs";
    private static final String KEY_REMEMBER = "remember";
    private static final String KEY_USERNAME = "username";

    //Checking the network availabilty
    ConnectionDetector connectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        connectionDetector=new ConnectionDetector(this);
        myHelper= new DataBaseHelpe(this);

        //Finding the variables
        ckRemember = findViewById(R.id.ckRememberM);
        requestPassword = findViewById(R.id.txtRequestPassword);
        password = findViewById(R.id.edPssword);
        user_email = findViewById(R.id.edEmail);
        btnLogin=findViewById(R.id.btnSearch);

        password.setOnEditorActionListener(editorActionListener);

        // for the toast
        holder = (RelativeLayout) getLayoutInflater().inflate(R.layout.custom_toast, (RelativeLayout)findViewById(R.id.customToast));
        holder2 = (RelativeLayout) getLayoutInflater().inflate(R.layout.custom_toast2, (RelativeLayout)findViewById(R.id.customToast2));

        customText = holder.findViewById(R.id.customToas_text);
        customText2 = holder2.findViewById(R.id.customToas_text2);

        //Allow single line to the fields
        password.setSingleLine(true);
        user_email.setSingleLine(true);

        //Hidding the password
        password.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_PASSWORD);


        //Progress Dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Signing in");
        dialog.setMessage("Please wait...");

        //Login button functions
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view)
            {
                LoginMethod();
            }
        });

        requestPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,RequestPasswordActivity.class);
                startActivity(intent);
            }
        });

        //Function for remember checkbox
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(sharedPreferences.getBoolean(KEY_REMEMBER, false))
        {
            ckRemember.setChecked(true);
        }
        else
        {
            ckRemember.setChecked(false);
        }

        user_email.setText(sharedPreferences.getString(KEY_USERNAME,""));
        user_email.addTextChangedListener(this);
        password.addTextChangedListener(this);
        ckRemember.setOnCheckedChangeListener(this);
    }

    //Method for closing the keyboard
    private void closeKeyboard()
    {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //for toast
    public void customToast(String message)
    {
        customText.setText(message);
        customText.setTextSize(25);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM,50,50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(holder);
        toast.show();
    }

    public void customToast2(String message)
    {
        customText2.setText(message);
        customText2.setTextSize(25);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM,80,80);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(holder);
        toast.show();
    }

    public void LoginFromCloud()
    {
        final String email= user_email.getText().toString();
        final String jpassword=password.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,  MyServices.apiLink + "Login/",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            String stringValue = "\"Unauthorized access!\"";

                            //Login unsuccessfull
                            if (response.equalsIgnoreCase(stringValue))
                            {

                                FancyToast.makeText(getApplication(), "Entered email or password is not correct", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show();
                            }
                            else {
                                //Login successfully
                                jsonObject = new JSONObject(response);

                                //Getting the current date time and add 60 minutes to it
//                                String f = "";
//                                Calendar c = Calendar.getInstance();
//                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                                f = df.format(c.getTime());
//                                Date d = null;
//                                try
//                                {
//                                    d = df.parse(f);
//                                }
//                                catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                c.setTime(d);
//                                c.add(Calendar.MINUTE, 30);
//                                String newTime = df.format(c.getTime());


                               // Inserting user information to the local database
                                userClient = new UserClient(
                                        Integer.parseInt(jsonObject.getString("UserId")),
                                        jsonObject.getString("UserFirstName"),
                                        jsonObject.getString("UserLastName"),
                                        jsonObject.getString("UserPassword"),
                                        Integer.parseInt(jsonObject.getString("UserRoleId")),
                                        jsonObject.getString("UserEmail"),
                                        jsonObject.getString("Token"),
                                        jsonObject.getString("Timeout"));

                                String clousTime = userClient.getTimeout();
                                String subTokenTimeout = clousTime.substring(0,10) + " " + clousTime.substring(11,19);

                                Date date = new Date();

                                DateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                try
                                {
                                     date =  parser.parse(subTokenTimeout);
                                }
                                catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }

                                DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                userClient.setTimeout(formatter.format(date));
                                //Toast.makeText(MainActivity.this, "CLOUD TIME " + formatter.format(date), Toast.LENGTH_LONG).show();

                                ArrayList<UserClient> users = myHelper.getAllUsers();

                                for (UserClient userClients : users)
                                {
                                    if (userClients.getUserclientId() == userClient.getUserclientId())
                                    {
                                        found = true;
                                        userClient.setUserPassword(userPassword);
                                        myHelper.UpdateUser(userClient);
                                    }
                                }

                                if(found == false)
                                {
                                    userClient.setUserPassword(userPassword);
                                    myHelper.addUserFromCloud(userClient);
                                }

                                user_name = userClient.getUserFirstName();
                                user_surname = userClient.getUserLastName();
                                uEmail = userClient.getUserEmail();
                                uPassword = userClient.getUserPassword();
                                userloginid = userClient.getUserclientId();
                                newtoken = jsonObject.getString("Token");
                                loginType="cloud";
                                isLogedIn = true;

                                Intent intent = new Intent(MainActivity.this, MediPackClientActivity.class);
                                intent.putExtra("username", jsonObject.getString("UserEmail"));
                                intent.putExtra("token", jsonObject.getString("Token"));
                                intent.putExtra("userId", jsonObject.getInt("UserId"));
                                intent.putExtra("  loginType", " cloud");
                                startActivity(intent);
                            }
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Log.d("Response Error  ", String.valueOf(error));
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("email",email );
                params.put("Password",jpassword );
                params.put("DeviceId", "1");
                params.put("Reme", "1");

                return params;
            }
        };

        MySingleton.getmInstance(MainActivity.this).addToRequestQue(stringRequest);
    }


    //Validation method for user inputed data/ Edit text
    public boolean validateInput()
    {
        String passwordHolder, emailHolder;

        passwordHolder = password.getText().toString();
        emailHolder = user_email.getText().toString();

        String email_pattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Matcher matcher = Pattern.compile(email_pattern).matcher(emailHolder);

        if (!emailHolder.isEmpty() )
        {
            if (matcher.matches())
            {
                valid = true;
            }
            else {
                valid = false;
                user_email.setError("Invalid email");
                dialog.dismiss();
            }

            //Checking if the entered email does not contain space
            for (int i = 0; i < emailHolder.length(); i++)
            {
                if (Character.isWhitespace(emailHolder.charAt(i))) {
                    user_email.setError("Email must not contain spaces");
                    valid = false;
                }
            }
        }
        else
        {
            user_email.setError(" Please Enter email");
        }

        if (passwordHolder.isEmpty()) {
            valid = false;
            password.setError(" Please enter password");
            dialog.dismiss();
        }

        return valid;
    }


    //Method to save key value for password and password if the remember me check box is checked
    private void managePrefs(){
        if(ckRemember.isChecked()){
            editor.putString(KEY_USERNAME, user_email.getText().toString().trim());

            editor.putBoolean(KEY_REMEMBER, true);
            editor.apply();
        }else{
            editor.putBoolean(KEY_REMEMBER, false);
            editor.remove(KEY_USERNAME);
            editor.apply();
        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        managePrefs();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        managePrefs();
    }

    private EditText.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent)
        {
            switch(actionId)
            {
                case EditorInfo.IME_ACTION_SEARCH:
                    LoginMethod();
            }
            return false;
        }
    };

    public void LoginMethod()
    {
        dialog.show();

        //Enter user email and password
        userPassword= password.getText().toString();
        userEmail= user_email.getText().toString();

        //Validate user input
        if(validateInput() ==true)
        {
            //Calling the database helper passing the email and password
            UserClient userClientVal = myHelper.verifyUser(userEmail, userPassword);

            //Condition : Verify username and password in the local database
            if(userClientVal != null)
            {
                //Get current date time
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String d1 = df.format(c.getTime());
                Date date = null;
                Date currentDate = null;

                int userid = userClientVal.getUserclientId();

                //change a string to a datetime format
                String timeoutDb = userClientVal.getTimeout();

                try
                {
                    date = df.parse(timeoutDb);
                    currentDate = df.parse(d1);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

                //Condition : Verify token expired date time wih the current date time
                if ( currentDate.getTime() < date.getTime())
                {
                    Toast.makeText(MainActivity.this, " Local", Toast.LENGTH_SHORT).show();
                    newtoken = userClientVal.getToken();
                    user_name =userClientVal.getUserFirstName();
                    user_surname =userClientVal.getUserLastName();
                    uEmail=userClientVal.getUserEmail();
                    newTimeout = userClientVal.getTimeout();
                    loginType="local";
                    userloginid = userClientVal.getUserclientId();

                    isLogedIn = true;

                    Intent intent = new Intent(MainActivity.this, MediPackClientActivity.class);
                    startActivity(intent);

                    //dialog.dismiss();
                }
                else
                {
                    if (connectionDetector.isNetworkAvailable())
                    {
                        myHelper.DeleteUser(userid);
                        LoginFromCloud();

//                                FancyToast.makeText(getApplicationContext(),"Hello World !",FancyToast.LENGTH_LONG,FancyToast.WARNING,true).show();
                    }
                    else
                    {
                        FancyToast.makeText(getApplicationContext(),"No network Connection!",FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();
                    }
                }
            }
            //Calling the cloud API passing the username and password
            else
            {
                if (connectionDetector.isNetworkAvailable())
                {
                    LoginFromCloud();
                    dialog.dismiss();
                }
                else
                {
                    FancyToast.makeText(getApplicationContext(),"No Network Connection!",FancyToast.LENGTH_LONG,FancyToast.WARNING,false).show();

                }
            }
        }
        closeKeyboard();
    }
}
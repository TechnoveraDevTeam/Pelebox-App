package com.example.aviwe.pelebox.forgotPassword;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aviwe.pelebox.DataBaseHelpe;
import com.example.aviwe.pelebox.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestPasswordActivity extends AppCompatActivity {

    EditText edittxt_email;
    Button btnRequestpassword;
    String emailTo,emailFrom,emailSubject,emailMessage,emailPattern;
    SendMail sendMail;
    private ProgressDialog dialog;
    DataBaseHelpe helper;

    //for the toast
    RelativeLayout holder;
    TextView customText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_retrieve);

        //progress dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Requesting Password");
        dialog.setMessage(" Sending the email..Please wait...");

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                dialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 3000);

        edittxt_email = (EditText)findViewById(R.id.txt_RequestPassword);
        btnRequestpassword = (Button)findViewById(R.id.btn_requestPassword);

        // for the toast
        holder = (RelativeLayout) getLayoutInflater().inflate(R.layout.custom_toast, (RelativeLayout)findViewById(R.id.customToast));
        customText = (TextView) holder.findViewById(R.id.customToas_text);

        helper = new DataBaseHelpe(getApplicationContext());

//        emailFrom = "tharollo.moraba@technovera.co.za";
//        //emailTo = edittxt_email.getText().toString().trim();
//        emailSubject = "Requested Password";
//        emailMessage = "Dear User.\n\n" +
//                "Can you please help me with my login details" +"\n\n Here is your requested password: ";

        btnRequestpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateEmail();

                // String requestedPassword = helper.getRequestedPassword(emailFrom);
                closeKeyboard();
            }
        });

    }
    public void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //for toast
    public void customToast(String message)
    {


        //customText.setText("Unauthorized access!");
        customText.setText(message);
        customText.setTextSize(25);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM,50,50);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(holder);
        toast.show();
    }

    public  void validateEmail()
    {
        emailFrom = "tharollo.moraba@technovera.co.za";
        //emailTo = edittxt_email.getText().toString().trim();
        emailSubject = "Requested Password";
        emailMessage = "Dear User.\n\n" +
                "We have received your request for password." +"\n\n Here is your requested password: ";

        emailPattern =  "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        emailTo = edittxt_email.getText().toString();

        Matcher matcher = Pattern.compile(emailPattern).matcher(emailTo);

        if(emailTo.length() > 0)
        {
            if (matcher.matches())
            {
                String requestedPassword = helper.getRequestedPassword(emailTo);

                if(requestedPassword != null ) {

                dialog.show();
//                sendMail  = new SendMail(getApplicationContext(), emailTo, emailSubject , emailMessage+ emailFrom);
                    sendMail = new SendMail(getApplicationContext(), emailTo, emailSubject, emailMessage + requestedPassword);
                    sendMail.execute();
                    edittxt_email.setText(" ");
                dialog.dismiss();

                }
                else{

                    customToast(" No Record Found");
                }
            }
            else
            {
                edittxt_email.setError("The email is invalid");
            }
        }
        else
        {
            edittxt_email.setError("Please enter the email");
        }

    }

}

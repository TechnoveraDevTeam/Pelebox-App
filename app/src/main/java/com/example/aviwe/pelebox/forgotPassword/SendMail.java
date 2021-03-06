package com.example.aviwe.pelebox.forgotPassword;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Gravity;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail extends AsyncTask<Void, Void, Void> {

    //Declaring Variables
    private Context context;
    private Session session;
    private String email,subject,message;
    private ProgressDialog progressDialog;

    public SendMail(Context context, String email, String subject, String message){
        //Initializing variables
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Toast.makeText(context, "Message sent", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... voids) {

        //Creating properties
        Properties props = new Properties();

        //Configuring properties for gmail
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        //Creating a new Session
        session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator(){
                    //Authenticating the password.
                    protected PasswordAuthentication getPasswordAuthentication(){
                        return  new PasswordAuthentication(Config.email, Config.password);
                    }

                });

        try {

            //Creating MimeMessage object
            MimeMessage mm = new MimeMessage(session);

            //Setting sender address
             mm.setFrom(new InternetAddress(Config.email));

            // Adding receiver
            mm.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

            //Adding Subject
            mm.setSubject(subject);

            //add message
            mm.setText(message);

            //Sending email
            Transport.send(mm);

        } catch (MessagingException e){
            e.printStackTrace();
        }

        return null;
    }
}

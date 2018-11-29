package com.example.aviwe.pelebox;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aviwe.pelebox.Scanout.ScanOoutActivity;
import com.example.aviwe.pelebox.Scanout.ScanOutParcel;
import com.example.aviwe.pelebox.pojos.MediPackClient;
import com.example.aviwe.pelebox.utils.ConstantMethods;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScanoutByAssistantActivity extends AppCompatActivity 
{
    EditText PatientFisrtName,PatientLastName,PatientRSA,PatientCellphone,ScannedInDateTime,MediPackDueDateTime,MediPackBarcode,MediPackStatus;
    private MediPackClient med;
    SimpleDateFormat df ;
    Calendar c;
    Button BtnCollect;
    public String intentBarcode;

    //for the toast
    RelativeLayout holder;
    TextView customText;

    private DataBaseHelpe myHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanout_by_assistant);

        med = new MediPackClient();
        myHelper = new DataBaseHelpe(this);

        PatientFisrtName = findViewById(R.id.name);
        PatientLastName = findViewById(R.id.surnname);
        PatientRSA = findViewById(R.id.idNumber);
        PatientCellphone = findViewById(R.id.cellphone);
        MediPackDueDateTime = findViewById(R.id.duedate);
        MediPackBarcode = findViewById(R.id.nhi);
        MediPackStatus = findViewById(R.id.status);
        ScannedInDateTime = findViewById(R.id.capaturedDate);

        // for the toast
        holder = (RelativeLayout) getLayoutInflater().inflate(R.layout.custom_toast, (RelativeLayout)findViewById(R.id.customToast));
        customText = (TextView) holder.findViewById(R.id.customToas_text);

         //Getting the current date
         c = Calendar.getInstance();
         df = new SimpleDateFormat("yyyy-MM-dd");

         BtnCollect = findViewById(R.id.collect);
        final Intent intent = getIntent();
        
        //Getting the pojo object medi pack
        med = (MediPackClient) intent.getSerializableExtra("object_of_medi");

        //Setting data 
        PatientFisrtName.setText(med.getPatientFisrtName());
        PatientLastName.setText(med.getPatientLastName());
        PatientRSA.setText(med.getPatientRSA());
        PatientCellphone.setText(med.getPatientCellphone());
        ScannedInDateTime.setText(med.getScannedInDateTime());
        MediPackDueDateTime.setText(med.getMediPackDueDateTime());
        MediPackBarcode.setText(med.getMediPackBarcode());

        intentBarcode = med.getMediPackBarcode();

        getMediPackIdStatus();

        //Collect button functionality
        BtnCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(med != null ) {
                    Intent intent1 = new Intent(ScanoutByAssistantActivity.this, ScanOutParcel.class);
                    intent1.putExtra("medipakid","4");
                    intent1.putExtra("intentBarcode",intentBarcode);
                    startActivity(intent1);
                }
                else {
                    customToast("No Record found to proceed");
                }
            }
        });

    }

    public void customToast(String message)
    {
        //customText.setText("Unauthorized access!");
        customText.setText(message);
        customText.setTextSize(25);

        final Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER | Gravity.BOTTOM,0,5);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(holder);
        toast.show();
    }
    //Assign status based on the medi pack id
    public void getMediPackIdStatus()
    {
        //Getting the medipack id from the database
        int status = med.getMediPackStatusId();

        if (status == 0) {
            MediPackStatus.setText("Uploaded");
        } else if (status == 1) {
            MediPackStatus.setText("Booking  for scanning ");
        } else if (status == 2) {
            MediPackStatus.setText("Scanned In");
        } else if (status == 3) {
            MediPackStatus.setText("Scanned Out Collected ");
            BtnCollect.setVisibility(View.INVISIBLE);
        } else if (status == 4) {
            MediPackStatus.setText("Collected by Patient With Assistance From Admin");
            BtnCollect.setVisibility(View.INVISIBLE);
        }
        else if(status == 5)
        {
            MediPackStatus.setText("Medication Returned Due to Non Collections");
        }
    }

    //Clearing the fields after collecting
    public void clearingInputFields()
    {
        PatientFisrtName.setText(" ");
        PatientLastName.setText(" ");
        PatientRSA.setText(" ");
        PatientCellphone.setText(" ");
        ScannedInDateTime.setText(" ");
        MediPackBarcode.setText(" ");
        MediPackDueDateTime.setText(" ");
        MediPackStatus.setText("");
    }

}




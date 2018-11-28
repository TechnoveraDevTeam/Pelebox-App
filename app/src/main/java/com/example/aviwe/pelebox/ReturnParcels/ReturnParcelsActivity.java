package com.example.aviwe.pelebox.ReturnParcels;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.aviwe.pelebox.DataBaseHelpe;
import com.example.aviwe.pelebox.R;
import com.example.aviwe.pelebox.Scanin.ScanInParcelActivity;
import com.example.aviwe.pelebox.pojos.MediPackClient;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
public class ReturnParcelsActivity extends AppCompatActivity {
    private DataBaseHelpe helper;
    private EditText edBarcode;
    private MediPackClient med;
    private ReturnPacelsAdapter returnAdapter;
    private RecyclerView mRecyclerView;
    private int status;
    private Button btnAcceptAll, btnCountList, btnCountRtn;
    public Button btnManualScan, btnSearchManual;
    private Timer timer;
    String barcode, changedBarcode;
    ArrayList<MediPackClient> medList;
    Context context;
    boolean isavailable;
    int countReturn;
    int countinitialList;
    Boolean isManual, valid = true;
    //for the toast
    RelativeLayout holder;
    TextView customText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_parcels);
        med = new MediPackClient();
        helper = new DataBaseHelpe(this);
        medList = new ArrayList<>();
        countReturn = 0;
        countinitialList = 0;
        context = getBaseContext();
        isManual = false;
        edBarcode = findViewById(R.id.edtBarcode);
        mRecyclerView = findViewById(R.id.scanInCycle);
        btnAcceptAll = findViewById(R.id.bntAcceptAll);
        btnCountRtn = findViewById(R.id.btnCountRtn);
        btnCountList = findViewById(R.id.btnCountList);
        btnManualScan = findViewById(R.id.btnManualScan);
        btnSearchManual = findViewById(R.id.btnSearchManual);
        medList = helper.getSevenDaysNonCollectedParcels();
        for(MediPackClient mediPackClient : medList )
        {
            countinitialList++;
        }
        btnCountList.setText(String.valueOf(countinitialList));
        btnCountRtn.setText(String.valueOf(countReturn));
        returnAdapter = new ReturnPacelsAdapter(medList, ReturnParcelsActivity.this);
        returnAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(returnAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        holder = (RelativeLayout) getLayoutInflater().inflate(R.layout.custom_toast, (RelativeLayout)findViewById(R.id.customToast));
        customText = holder.findViewById(R.id.customToas_text);
        final TextWatcher scannerWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (timer != null)
                {
                    timer.cancel();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ReturnParcelsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                        try{
                            Thread.sleep(100);
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        ReturnParcelsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                myBarcode();
                                edBarcode.setText("");
                            }
                        });
                    }
                }, 400);
            }
        };
        edBarcode.addTextChangedListener(scannerWatcher);
        btnManualScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isManual == false) {
                    isManual = true;
                    edBarcode.removeTextChangedListener(scannerWatcher);
                    customToast("Auto Scan DeActivated!");
                    edBarcode.setHint("Enter Barcode");
                    btnSearchManual.setVisibility(View.VISIBLE);
                    btnManualScan.setText("Auto Scan");
                    //isManual = true;
                }
                else
                {
                    isManual= false;
                    btnManualScan.setText("Scan Manual");
                    edBarcode.setHint("Scan Barcode");
                    btnSearchManual.setVisibility(View.INVISIBLE);
                    customToast("Auto Scan Activated");
                    edBarcode.addTextChangedListener(scannerWatcher);
                }
            }
        });
        //Search Manualy
        btnSearchManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(isBarcodeValid() == true)
                {
                    myBarcode();
                    if(barcode.length() > 14 || barcode.length() == 13 || barcode.length() < 12)
                    {
                        customToast("Incorrect Barcode, Please try again");
                    }
                    edBarcode.setText("");
                    closeKeyboard();
                }
            }
        });
        btnAcceptAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean foundTick = false;
                for(MediPackClient mediReturn : medList)
                {
                    if (mediReturn.getMediPackStatusId() == 5 )
                    {
                        helper.UpdateParcelForReturn(mediReturn.getMediPackId());
                        foundTick = true;
                        Log.d("Tharollo", "onClick: "+mediReturn.getMediPackId());
//                        customToast("is in: "+ mediReturn.getMediPackStatusId());
                    }
                }
                if (foundTick == false)
                {
                    customToast("There is  no record found!");
                }else
                {
                    customToast("Parcels Are Successfully Returned");
                    medList = helper.getSevenDaysNonCollectedParcels();
                    countReturn = 0;
                    countinitialList = 0;
                    for(MediPackClient mediPackClient : medList )
                    {
                        countinitialList++;
                    }
                    btnCountList.setText(String.valueOf(countinitialList));
                    btnCountRtn.setText(String.valueOf(countReturn));
                    getAdapter(medList);
                }
            }
        });
    }
    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    //Validation method for user inputed data/ Edit text
    public boolean isBarcodeValid()
    {
        barcode = edBarcode.getText().toString();
        if (!barcode.isEmpty()) {
            valid = true;
        }
        else {
            valid = false;
            edBarcode.setError("Please enter barcode");
        }
        return valid;
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
    public void getAdapter(ArrayList arrayList) {
//        for(MediPackClient mediPackClient : medList )
//        {
//            countinitialList++;
//        }
//        btnCountList.setText(String.valueOf(countinitialList));
//        adapter = new ReportAdapter(arrayList);
        returnAdapter = new ReturnPacelsAdapter(arrayList, ReturnParcelsActivity.this);
        returnAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(returnAdapter);
    }
    public void myBarcode()
    {
        barcode = edBarcode.getText().toString();
        isavailable = false;
        //This is a NHI that starts with the * when scanned
        if(barcode.length() == 14)
        {
            //Checking if the barcode starts with NHI
            changedBarcode = barcode.substring(1, 4);
            if(changedBarcode.equalsIgnoreCase("NHI"))
            {
                String nhi=barcode.substring(1,13);
                //Searching if the barcode does exist on the database
                med = helper.getBarcodeParcel(nhi);
                scanInBarcodeFunctinality(nhi);
            }
            else
            {
                closeKeyboard();
                customToast(" No such barcode  found, Please try");
                //Toast.makeText(ScanInParcelActivity.this, " No such barcode  found, Please try", Toast.LENGTH_LONG).show();
            }
        }
        //This is a NHI that does not start with the * when scanned
        else if(barcode.length() == 12)
        {
            changedBarcode = barcode.substring(0, 3);
            if(changedBarcode.equalsIgnoreCase("NHI"))
            {
                med = helper.getBarcodeParcel(barcode);
                scanInBarcodeFunctinality(barcode);
            }
            else
            {
                closeKeyboard();
                customToast(" No such barcode  found, Please try");
                //Toast.makeText(ScanInParcelActivity.this, " No such barcode  found, Please try", Toast.LENGTH_LONG).show();
            }
        }
        return;
    }
    public void scanInBarcodeFunctinality(String scannedNHI)
    {
        if (med != null)
        {
            if(med.getMediPackStatusId() == 2)
            {
                int index = 0;
                for (MediPackClient m : medList)
                {
                    if (m.getMediPackBarcode().equalsIgnoreCase(scannedNHI)) {
                        isavailable = true;
                        break;
                    }
                    index++;
                }
                if (isavailable == false)
                {
//                    medList.add(med);
                }
                else if (isavailable == true)
                {
                    closeKeyboard();
                    medList.get(index).setMediPackStatusId(5);
                    countReturn = countReturn + 1;
                    btnCountRtn.setText(String.valueOf(countReturn));
                    getAdapter(medList);
                    //customToast(" Parcel barcode has already been scanned in");
                    //Toast.makeText(ScanInParcelActivity.this, " Parcel barcode has already been scanned in", Toast.LENGTH_LONG).show();
                }
            }
            else
            {
                customToast(" Parcel has already being scanned");
                //Toast.makeText(ScanInParcelActivity.this, " Parcel has already being scanned", Toast.LENGTH_LONG).show();
            }
        }
        else if(changedBarcode.equalsIgnoreCase("NHI"))
        {
            for(MediPackClient m :medList ){
                if( m.getMediPackBarcode().equalsIgnoreCase(scannedNHI)) {
                    isavailable = true;
                    break;
                }
            }
            if(isavailable == false)
            {
                med = new MediPackClient("", "", "", scannedNHI, "", "", 0);
                medList.add(med);
            }
            else
            {
                closeKeyboard();
                customToast("barcode has already been scanned in for unknowm barcode");
                //Toast.makeText(ScanInParcelActivity.this, "barcode has already been scanned in for unknowm barcode", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            closeKeyboard();
            customToast(" No such barcode was found, Please try");
            //Toast.makeText(ScanInParcelActivity.this, " No such barcode was found, Please try", Toast.LENGTH_LONG).show();
        }
        returnAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.scan_in, menu);
        return true;
    }
}
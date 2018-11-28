package com.example.aviwe.pelebox.report;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import com.example.aviwe.pelebox.MediPackClientsAdapter;
import com.example.aviwe.pelebox.PatientInformationActivity;
import com.example.aviwe.pelebox.R;
import com.example.aviwe.pelebox.DataBaseHelpe;
import com.example.aviwe.pelebox.pojos.MediPackClient;
import com.example.aviwe.pelebox.utils.RecyclerItemClickListener;
import java.util.ArrayList;
import java.util.Calendar;

public class CollectedParcelActivity extends AppCompatActivity implements MediPackClientsAdapter.MediPackClientsAdapterListener {

    private RadioGroup ageRadioGroup, genderRadioGroup;
    private RadioButton radioButton;
    private DataBaseHelpe helper;
    private Button btnSearchData, btnCount, btnGender;
    private SearchView searchView;
    private RecyclerView mRecyclerView;
    ArrayList<MediPackClient> mediPackList, listID;
    ReportAdapter adapter;
    MediPackClient med;
    int count, countall, year, convert, addYear2, patientAge, counter,radioid,convertYear;
    String mediPackPatientYear,patientId, firstNumber,addYear1,checkingid;

    // filtering by gender and age
    String genderString = "AG";
    String ageString = "AA";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collected_parcel);

        adapter = new ReportAdapter(mediPackList);
        helper = new DataBaseHelpe(this);
        med = new MediPackClient();
        listID = new ArrayList<>();

        mRecyclerView = findViewById(R.id.parcel_ready_for_collection);
//        btnSearchData = findViewById(R.id.btnSearchData);
        btnCount = findViewById(R.id.btnCount);
        btnGender = findViewById(R.id.btnSearchGender);
        ageRadioGroup = findViewById(R.id.rgAge);
        genderRadioGroup = findViewById(R.id.rgGender);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        myrecyclerview();

        //Getting the total number on the list before searhing
        for (MediPackClient user : mediPackList) {
            countall++;
        }
        btnCount.setText(String.valueOf(countall));
        getAdapter(mediPackList);

        //Sort by age functionlity
//        btnSearchData.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                if (radioButton.getText().equals("0 to 17"))
//                {
//                    listID.clear();
//                    count = 0;
//
//                    for (MediPackClient med : mediPackList)
//                    {
//                        patientId = med.getPatientRSA();
//                        checkingid = patientId.substring(0, 2);
//                        firstNumber = patientId.substring(0, 1);
//                        convert = Integer.parseInt(firstNumber);
//
//                        if (convert == 0 || convert == 1)
//                        {
//                            addYear1 = "20";
//
//                            //Getting the year
//                            mediPackPatientYear = addYear1 + checkingid;
//
//                            //Convert string year to int
//                            convertYear = Integer.parseInt(mediPackPatientYear);
//
//                            //Calculating the age
//                            patientAge = getCurrentYear() - convertYear;
//
//                            if (patientAge >= 0 || patientAge <= 17) {
//                                count++;
//                                listID.add(med);
//                            }
//                        }
//                    }
//                    getAdapter(listID);
//                    btnCount.setText(String.valueOf(count));
//                }
//                else if (radioButton.getText().equals("18 to 35"))
//                {
//                    listID.clear();
//                    counter = 0;
//                    for (MediPackClient med : mediPackList)
//                    {
//                        patientId = med.getPatientRSA();
//
//                        checkingid = patientId.substring(0, 2);
//
//                        firstNumber = patientId.substring(0, 1);
//
//                        convert = Integer.parseInt(firstNumber);
//
//                        if (convert == 0 || convert == 1)
//                        {
//                            addYear1 = "20";
//                            mediPackPatientYear = addYear1 + checkingid;
//                            convertYear = Integer.parseInt(mediPackPatientYear);
//                            patientAge = getCurrentYear() - convertYear;
//
//                            if (patientAge >= 18 && patientAge <= 35) {
//                                counter++;
//                                listID.add(med);
//                            }
//                        }
//                        else if (convert >= 2)
//                        {
//                            addYear2 = 19;
//                            mediPackPatientYear = addYear2 + checkingid;
//                            convertYear = Integer.parseInt(mediPackPatientYear);
//                            int patientAge = getCurrentYear() - convertYear;
//
//                            if (patientAge >= 18 && patientAge <= 35)
//                            {
//                                counter++;
//                                listID.add(med);
//                            }
//                        }
//                        count = counter;
//                    }
//                    getAdapter(listID);
//                    btnCount.setText(String.valueOf(count));
//                }
//                else if (radioButton.getText().equals("36 to 65"))
//                {
//                    listID.clear();
//                    counter = 0;
//
//                    for (MediPackClient med : mediPackList)
//                    {
//                        patientId = med.getPatientRSA();
//
//                        checkingid = patientId.substring(0, 2);
//
//                        firstNumber = patientId.substring(0, 1);
//
//                        convert = Integer.parseInt(firstNumber);
//
//                        if (convert >= 2)
//                        {
//                            addYear1 = "19";
//                            mediPackPatientYear = addYear1 + checkingid;
//                            convertYear = Integer.parseInt(mediPackPatientYear);
//                            patientAge = getCurrentYear() - convertYear;
//
//                            if (patientAge >= 36 && patientAge <= 65) {
//                                counter++;
//                                listID.add(med);
//                            }
//                        }
//                        count = counter;
//                    }
//                    getAdapter(listID);
//                    btnCount.setText(String.valueOf(count));
//                }
//                else if (radioButton.getText().equals("Above 65"))
//                {
//                    listID.clear();
//                    counter = 0;
//                    for (MediPackClient med : mediPackList)
//                    {
//                        patientId = med.getPatientRSA();
//
//                        checkingid = patientId.substring(0, 2);
//
//                        firstNumber = patientId.substring(0, 1);
//
//                        convert = Integer.parseInt(firstNumber);
//
//                        if (convert >= 2 )
//                        {
//                            addYear1 = "19";
//                            mediPackPatientYear = addYear1 + checkingid;
//                            convertYear = Integer.parseInt(mediPackPatientYear);
//                            patientAge = getCurrentYear() - convertYear;
//                            if (patientAge >= 66 )
//                            {
//                                counter++;
//                                listID.add(med);
//                            }
//                        }
//                        count = counter;
//                    }
//                    getAdapter(listID);
//                    btnCount.setText(String.valueOf(count));
//                }
//                else if (radioButton.getText().equals("All")) {
//                    mediPackList.clear();
//                    counter = 0;
//                    mediPackList = helper.getAllMediPackCollected();
//                    for (MediPackClient user : mediPackList) {
//                        counter++;
//                    }
//                    btnCount.setText(String.valueOf(counter));
//                    getAdapter(mediPackList);
//                }
//            }
//        });


        //Sorting by Gender functionality
        btnGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                filtering();
//                if (radioButton.getText().equals("Female"))
//                {
//                    listID.clear();
//                    counter = 0;
//
//                    for (MediPackClient med : mediPackList)
//                    {
//                        patientId = med.getPatientRSA();
//
//                        checkingid = patientId.substring(6, 7);
//
//                        convert = Integer.parseInt(checkingid);
//                        if (convert >= 0 && convert <= 4) {
//                            counter++;
//                            listID.add(med);
//                        }
//                    }
//                    getAdapter(listID);
//                    btnCount.setText(String.valueOf(counter));
//
//                }
//                else if (radioButton.getText().equals("Male"))
//                {
//                    listID.clear();
//                    counter = 0;
//                    for (MediPackClient med : mediPackList)
//                    {
//                        patientId = med.getPatientRSA();
//                        checkingid = patientId.substring(6, 7);
//                        convert = Integer.parseInt(checkingid);
//
//                        if (convert >= 5 && convert <= 9) {
//                            counter++;
//                            listID.add(med);
//                        }
//                    }
//                    getAdapter(listID);
//                    btnCount.setText(String.valueOf(counter));
//                }
//                else if (radioButton.getText().equals("All"))
//                {
//                    mediPackList.clear();
//                    counter = 0;
//                    mediPackList = helper.getAllMediPackCollected();
//                    for (MediPackClient user : mediPackList) {
//                        counter++;
//                    }
//
//                    btnCount.setText(String.valueOf(counter));
//                    getAdapter(mediPackList);
//                }
            }
        });


        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position)
            {
                MediPackClient medi = mediPackList.get(position);
                Intent intent = new Intent(CollectedParcelActivity.this, PatientInformationActivity.class);
                intent.putExtra("object_of_medi", medi);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) { }
        }) {
        });

    }

    public void myrecyclerview()
    {
        mediPackList = new ArrayList<>();
        mediPackList = helper.getAllMediPackCollected();
        Button medPackTotal = findViewById(R.id.btnCount);
        int count = 0;

        for(MediPackClient user : mediPackList)
        {
            count++;
        }

        medPackTotal.setText(String.valueOf(count));
        adapter = new ReportAdapter(mediPackList);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    public void checkButton(View v) {
        radioid = ageRadioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioid);

        if (radioButton.getText().equals("0 to 17")) {
            ageString = "17";
        } else if (radioButton.getText().equals("18 to 35")) {
            ageString = "35";
        } else if (radioButton.getText().equals("36 to 65")) {
            ageString = "65";
        } else if (radioButton.getText().equals("Above 65")) {
            ageString = "A65";
        } else if (radioButton.getText().equals("All")) {
            ageString = "AA";
        }
    }

    public int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        return year;
    }

    public void getAdapter(ArrayList arrayList) {
        adapter = new ReportAdapter(arrayList);
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
    }

    //Checking the checked gender radio button
    public void checkedGender(View view) {
        radioid = genderRadioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioid);
        if (radioButton.getText().equals("Male")) {
            genderString = "M";
        } else if (radioButton.getText().equals("Female")) {
            genderString = "F";
        } else if (radioButton.getText().equals("All")) {
            genderString = "AG";
        }
    }


    @Override
    public void onBackPressed() {
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                adapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public void onContactSelected(MediPackClient mediPackClient) {

    }

    public void filtering() {

        String selectedRadioButton = null;
        if (ageString == "17" && genderString == "M") {

            selectedRadioButton = "7M";
            getSelectedFilter(selectedRadioButton);


        } else if (ageString == "35" && genderString == "M") {


            selectedRadioButton = "3M";
            getSelectedFilter(selectedRadioButton);



        } else if (ageString == "65" && genderString == "M") {

            selectedRadioButton = "6M";
            getSelectedFilter(selectedRadioButton);


        } else if (ageString == "A65" && genderString == "M") {


            selectedRadioButton = "A6M";
            getSelectedFilter(selectedRadioButton);



        } else if (ageString == "AA" && genderString == "M") {


            selectedRadioButton = "AM";
            getSelectedFilter(selectedRadioButton);


        } else if (ageString == "17" && genderString == "F") {


            selectedRadioButton = "7F";
            getSelectedFilter(selectedRadioButton);

        } else if (ageString == "35" && genderString == "F") {


            selectedRadioButton = "3F";
            getSelectedFilter(selectedRadioButton);

        } else if (ageString == "65" && genderString == "F") {


            selectedRadioButton = "6F";
            getSelectedFilter(selectedRadioButton);

        } else if (ageString == "A65" && genderString == "F") {


            selectedRadioButton = "A6F";
            getSelectedFilter(selectedRadioButton);

        } else if (ageString == "AA" && genderString == "F") {


            selectedRadioButton = "AF";
            getSelectedFilter(selectedRadioButton);

        } else if (ageString == "17" && genderString == "AG") {


            selectedRadioButton = "7A";
            getSelectedFilter(selectedRadioButton);

        } else if (ageString == "35" && genderString == "AG") {


            selectedRadioButton = "3A";
            getSelectedFilter(selectedRadioButton);


        } else if (ageString == "65" && genderString == "AG") {


            selectedRadioButton = "6A";
            getSelectedFilter(selectedRadioButton);

        } else if (ageString == "A65" && genderString == "AG") {


            selectedRadioButton = "A6A";
            getSelectedFilter(selectedRadioButton);

        } else if (ageString == "AA" && genderString == "AG") {


            selectedRadioButton = "AA";
            getSelectedFilter(selectedRadioButton);

        }

    }

    public void getSelectedFilter(String radioButtonSelected) {

        listID.clear();
        counter = 0;

        for (MediPackClient med : mediPackList) {

            patientId = med.getPatientRSA();
            checkingid = patientId.substring(0, 2);
            firstNumber = patientId.substring(0, 1);
            convert = Integer.parseInt(firstNumber);

            String checkingGender = patientId.substring(6, 7);
            int genderNumber = Integer.parseInt(checkingGender);

//                if (genderNumber >= 0 && genderNumber <= 4)
//                {
//                    counter++;
//                    listID.add(med);
//                }

            if ((convert == 0 || convert == 1) && (genderNumber >= 5 && genderNumber <= 9) && (radioButtonSelected == "7M")) {
                addYear1 = "20";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if ((patientAge >= 0 || patientAge <= 17 )  ) {


                    counter++;
                    listID.add(med);
                }
            }
            else if ((convert == 0 || convert == 1) && (genderNumber >= 5 && genderNumber <= 9) && (radioButtonSelected == "3M")) {
                addYear1 = "20";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if (patientAge >= 18 && patientAge <= 35 ) {

                    counter++;
                    listID.add(med);
                }
            }
            else if ((convert >= 2) && (genderNumber >= 5 && genderNumber <= 9 ) && (radioButtonSelected == "3M")) {
                addYear2 = 19;
                mediPackPatientYear = addYear2 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                int patientAge = getCurrentYear() - convertYear;

                if ( (patientAge >= 18 && patientAge <= 35 ) ) {
                    counter++;
                    listID.add(med);
                }
            }
            else if ((convert >= 2) && (genderNumber >= 5 && genderNumber <= 9) && (radioButtonSelected == "6M") ) {
                addYear1 = "19";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if ( (patientAge >= 36 && patientAge <= 65 )  ) {
                    counter++;
                    listID.add(med);
                }
            }
            else if ( (convert >= 2) && (genderNumber >= 5 && genderNumber <= 9)  && (radioButtonSelected == "A6M")) {
                addYear1 = "19";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if ( patientAge >= 66  ) {
                    counter++;
                    listID.add(med);
                }
            }
            else if ( (genderNumber >= 5 && genderNumber <= 9) && (radioButtonSelected == "AM") ) {

                counter++;
                listID.add(med);

            }
            else if ((convert == 0 || convert == 1) && (genderNumber >= 0 && genderNumber <= 4) && (radioButtonSelected == "7F")) {
                addYear1 = "20";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if ((patientAge >= 0 || patientAge <= 17 )  ) {


                    counter++;
                    listID.add(med);
                }
            }
            else if ((convert == 0 || convert == 1) && (genderNumber >= 0 && genderNumber <= 4) && (radioButtonSelected == "3F")) {
                addYear1 = "20";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if (patientAge >= 18 && patientAge <= 35 ) {

                    counter++;
                    listID.add(med);
                }
            }
            else if ((convert >= 2) && (genderNumber >= 0 && genderNumber <= 4) && (radioButtonSelected == "3F")) {
                addYear2 = 19;
                mediPackPatientYear = addYear2 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                int patientAge = getCurrentYear() - convertYear;

                if ( (patientAge >= 18 && patientAge <= 35 ) ) {
                    counter++;
                    listID.add(med);
                }
            }
            else if ((convert >= 2) && (genderNumber >= 0 && genderNumber <= 4) && (radioButtonSelected == "6F")) {
                addYear1 = "19";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if ( (patientAge >= 36 && patientAge <= 65 )  ) {
                    counter++;
                    listID.add(med);
                }
            } else if ( (convert >= 2) && (genderNumber >= 0 && genderNumber <= 4)  && (radioButtonSelected == "A6F")) {
                addYear1 = "19";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if ( patientAge >= 66  ) {
                    counter++;
                    listID.add(med);
                }
            }
            else if ( (genderNumber >= 0 && genderNumber <= 4) && (radioButtonSelected == "AF") ) {
//
                counter++;
                listID.add(med);
//
            }
            if ((convert == 0 || convert == 1) && (genderNumber >= 0 && genderNumber <= 9) && (radioButtonSelected == "7A") ) {
                addYear1 = "20";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if ((patientAge >= 0 || patientAge <= 17 )  ) {


                    counter++;
                    listID.add(med);
                }
            }
            else if ((convert == 0 || convert == 1) && (genderNumber >= 0 && genderNumber <= 9) && (radioButtonSelected == "3A")) {
                addYear1 = "20";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if (patientAge >= 18 && patientAge <= 35 ) {


                    counter++;
                    listID.add(med);
                }
            }
            else if ((convert >= 2) && (genderNumber >= 0 && genderNumber <= 9) && (radioButtonSelected == "3A") ) {
                addYear2 = 19;
                mediPackPatientYear = addYear2 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                int patientAge = getCurrentYear() - convertYear;

                if ( (patientAge >= 18 && patientAge <= 35 ) ) {
                    counter++;
                    listID.add(med);
                }
            }
            else if ((convert >= 2) && (genderNumber >= 0 && genderNumber <= 9) && (radioButtonSelected == "6A")) {
                addYear1 = "19";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if ( (patientAge >= 36 && patientAge <= 65 )  ) {
                    counter++;
                    listID.add(med);
                }
            } else if ( (convert >= 2) && (genderNumber >= 0 && genderNumber <= 9) && (radioButtonSelected == "A6A")) {
                addYear1 = "19";
                mediPackPatientYear = addYear1 + checkingid;
                convertYear = Integer.parseInt(mediPackPatientYear);
                patientAge = getCurrentYear() - convertYear;

                if ( patientAge >= 66  ) {

                    counter++;
                    listID.add(med);
                }
            }
            else if ( (radioButtonSelected == "AA")) {

                counter++;
                listID.add(med);


            }

        }
        getAdapter(listID);
        btnCount.setText(String.valueOf(counter));

    }

}

package com.example.aviwe.pelebox.ReturnParcels;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aviwe.pelebox.DataBaseHelpe;
import com.example.aviwe.pelebox.R;
import com.example.aviwe.pelebox.pojos.MediPackClient;
import com.example.aviwe.pelebox.report.ReportAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class ReturnPacelsAdapter extends RecyclerView.Adapter<ReturnPacelsAdapter.ViewHolder> implements Filterable {

    private ArrayList<MediPackClient> mArrayList;
    private ArrayList<MediPackClient> mFilteredList;
    private Context mContext;
    private DataBaseHelpe helper;
    MediPackClient mediPackClient;

    public ReturnPacelsAdapter(ArrayList<MediPackClient> arrayList, Context mContext) {
        this.mArrayList = arrayList;
        this.mFilteredList = arrayList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.return_parcels_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String fname =mFilteredList.get(position).getPatientLastName().substring(0,1);

        //Getting all the user data from the database
        holder.setIsRecyclable(false);

        String s= mFilteredList.get(position).getMediPackDueDateTime();
        holder.surname.setText("Patient Surname          : "  + mFilteredList.get(position).getPatientLastName());
        holder.name.setText("Patient Name               : "  + mFilteredList.get(position).getPatientFisrtName() );
        holder.id.setText("Patient ID Number       : " + mFilteredList.get(position).getPatientRSA());
        holder.cellphone.setText("Patient Cellphone        : " + mFilteredList.get(position).getPatientCellphone());
        holder.nhiNumber.setText("Patient NHI Number    : " + mFilteredList.get(position).getMediPackBarcode());
        holder.duedate.setText("Parcel Due Date          :  "  + s);
        holder.firstLetter.setText(fname);

        if (mFilteredList.get(position).getMediPackStatusId()> 3 )
        {
            holder.decline.setVisibility(View.VISIBLE);
            //Toast.makeText(mContext, "TICK" + mFilteredList.get(position).getPatientFisrtName(), Toast.LENGTH_SHORT).show();
        }

        //Getting the position of the item on the recycler view
        mediPackClient = mFilteredList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Button firstLetter = itemView.findViewById(R.id. btnFirstLetter);
        TextView name = itemView.findViewById(R.id.txtname);
        TextView surname = itemView.findViewById(R.id.txtsurname);
        TextView id = itemView.findViewById(R.id.txtID);
        TextView duedate = itemView.findViewById(R.id.txtDueDate);
        TextView accept = itemView.findViewById(R.id.txtAccept);
        TextView decline = itemView.findViewById(R.id.txtDecline);
        TextView edit = itemView.findViewById(R.id.txtEdit);
        TextView cellphone = itemView.findViewById(R.id.cellphone);
        TextView nhiNumber = itemView.findViewById(R.id.txtNhi);

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expensemanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BudgetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ExpenseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExpenseFragment newInstance(String param1, String param2) {
        ExpenseFragment fragment = new ExpenseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //Firebase database
    private FirebaseAuth mAuth;
    private DatabaseReference mExpenseDatabase;

    //Recyclerview
    private RecyclerView recyclerView;

    //TextView
    private TextView expenseSumResult;


    //Edit data item
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;

    //Data variable
    private String type;
    private String note;
    private float amount;
    private String post_key;
    private FloatingActionButton fab_budget_plus_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View myview =  inflater.inflate(R.layout.fragment_budget, container, false);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        expenseSumResult = myview.findViewById(R.id.sum_budget_expense);
        recyclerView = myview.findViewById(R.id.recycler_budget);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        mExpenseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalvalue = 0;
                for(DataSnapshot mysnapshot: snapshot.getChildren()){
                    Data data = mysnapshot.getValue(Data.class);
                    totalvalue += data.getAmount();
                    String stotal = String.valueOf(totalvalue);
                    expenseSumResult.setText(stotal+".00");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        fab_budget_plus_btn = myview.findViewById(R.id.fab_budget_plus_btn);
        return myview;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.budget_recycler_data,
                MyViewHolder.class,
                mExpenseDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());
                viewHolder.setProgress(model.getAmount(), Float.parseFloat((String) expenseSumResult.getText()));

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        type = model.getType();
                        note = model.getNote();
                        amount = model.getAmount();
                        updateDataItem();
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);
        fab_budget_plus_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDataItem();
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }
        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_budget);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote = mView.findViewById(R.id.date_txt_budget_end);
            mNote.setText(note);
        }
        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_budget_start);
            mDate.setText(date);
        }
        private void setAmount(float amount){
            TextView mAmount = mView.findViewById(R.id.amount_txt_budget);
            String smAmount = String.valueOf(amount);
            mAmount.setText(smAmount);
        }
        private void setProgress(float amount, float total){
            ProgressBar progressBar = mView.findViewById(R.id.progressBar);
            int percent = (int)(amount / total)*100;
            progressBar.setProgress(percent);
        }
    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);
        String[] transaction = getResources().getStringArray(R.array.typesOfTransactions);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, transaction);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                myview.findViewById(R.id.autoCompleteTextView);
        textView.setAdapter(arrayAdapter);
        edtAmount = myview.findViewById(R.id.amount);
        edtNote = myview.findViewById(R.id.note_edt);
        edtType = myview.findViewById(R.id.autoCompleteTextView);

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtNote.setText(note);
        edtNote.setSelection(note.length());

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        btnUpdate = myview.findViewById(R.id.btnUpdUpdate);
        btnDelete = myview.findViewById(R.id.btnUpdDelete);

        final AlertDialog dialog = mydialog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();
                String stamount = String.valueOf(amount);
                stamount = edtAmount.getText().toString().trim();
                int intamount = Integer.parseInt(stamount);

                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data = new Data(intamount, type, note, post_key, mDate);
                mExpenseDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpenseDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void insertDataItem(){
        AlertDialog.Builder mydialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_budgetdata, null);
        mydialog.setView(myview);

        final AlertDialog dialog=mydialog.create();
        dialog.setCancelable(false);
        EditText edtamount=myview.findViewById(R.id.amount_budget);
        EditText edttype=myview.findViewById(R.id.autoCompleteTextView_budget);
        EditText edtnote=myview.findViewById(R.id.note_edt_budget);

        Button saveBtn=myview.findViewById(R.id.btnSave_budget);
        Button cancelBtn=myview.findViewById(R.id.btnCancel_budget);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount=edtamount.getText().toString().trim();
                String type=edttype.getText().toString().trim();
                String note=edtnote.getText().toString().trim();

                if(TextUtils.isEmpty(type)){
                    edttype.setError("Please Enter A Type");
                    return;
                }
                if(TextUtils.isEmpty(amount)){
                    edtamount.setError("Please Enter Amount");
                    return;
                }
                if(TextUtils.isEmpty(note)){
                    edtnote.setError("Please Enter A Note");
                    return;
                }
                int amountInInt= Integer.parseInt(amount);
                //Create random ID inside database
                String id = mExpenseDatabase.push().getKey();
                String mDate = DateFormat.getDateInstance().format(new Date());
                Data data=new Data(amountInInt, type, note, id, mDate);
                mExpenseDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Transaction Added Successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        String[] transaction = getResources().getStringArray(R.array.typesOfTransactions);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, transaction);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                myview.findViewById(R.id.autoCompleteTextView_budget);
        textView.setAdapter(arrayAdapter);
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { // first item selected
                    textView.setInputType(InputType.TYPE_CLASS_TEXT); // set input type to number
                    textView.setText(""); // set text to empty string
                    textView.setHint("Thêm"); // set hint to "more"
                }else{
                    textView.setInputType(InputType.TYPE_NULL);
                }
            }
        });
    }
}
package com.example.expensemanager;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Dialog;
import android.app.Activity;
import android.app.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.example.expensemanager.Model.Category;
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

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExpenseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BudgetFragment extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BudgetFragment() {

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
    private DatabaseReference mBudgetDatabase;

    //Recyclerview
    private RecyclerView recyclerView;

    //TextView
    private TextView expenseSumResult;

    //Calender
    private Button pickDateButton_start;
    private Button pickDateButton_end;
    private TextView selectedDateTextView_start;
    private TextView selectedDateTextView_end;
    private Calendar calendar;

    //Edit data item
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtDateStart;
    private EditText edtDateEnd;

    private Button btnUpdate;
    private Button btnDelete;
    private Button btnCancel;
    private Button btnSave;

    //Data variable
    private String type;
    private String dateStart;
    private String dateEnd;
    private int amount;
    private String post_key;
    private FloatingActionButton fab_budget_plus_btn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview =  inflater.inflate(R.layout.fragment_budget, container, false);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mBudgetDatabase = FirebaseDatabase.getInstance().getReference().child("BudgetExpense").child(uid);
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
                    expenseSumResult.setText(stotal);
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
        FirebaseRecyclerAdapter<Category, MyViewHolder>adapter = new FirebaseRecyclerAdapter<Category, MyViewHolder>(
                Category.class,
                R.layout.budget_recycler_data,
                MyViewHolder.class,
                mBudgetDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Category category, final int position) {
                viewHolder.setType(category.getType());
                viewHolder.setDateEnd(category.getDateEnd());
                viewHolder.setDateStart(category.getDateStart());
                viewHolder.setAmount(category.getLimitAmount());
                viewHolder.setProgress(category.getLimitAmount(), 10000000);

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key = getRef(position).getKey();
                        amount = category.getLimitAmount();
                        type = category.getType();
                        dateEnd = category.getDateEnd();
                        dateStart = category.getDateStart();
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
        private void setDateStart(String dateStart){
            TextView mDate = mView.findViewById(R.id.date_txt_budget_start);
            mDate.setText(dateStart);
        }
        private void setDateEnd(String dateEnd){
            TextView mNote = mView.findViewById(R.id.date_txt_budget_end);
            mNote.setText(dateEnd);
        }
        private void setAmount(int amount){
            TextView mAmount = mView.findViewById(R.id.amount_txt_budget);
            String smAmount = String.valueOf(amount);
            mAmount.setText(smAmount);
        }
        private void setProgress(int amount, int total){
            ProgressBar progressBar = mView.findViewById(R.id.progressBar);
            TextView percent_txt_budget = mView.findViewById(R.id.percent_txt_budget);
            int percent = (int)(amount * 100 / total);
            progressBar.setProgress(percent);
            percent_txt_budget.setText(Integer.toString(percent) + "%");
        }
    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item_budget, null);
        mydialog.setView(myview);
        String[] transaction = getResources().getStringArray(R.array.typesOfTransactions);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, transaction);
        AutoCompleteTextView textView = (AutoCompleteTextView) myview.findViewById(R.id.autoCompleteTextView_budget_update);
        textView.setAdapter(arrayAdapter);

        selectDateUpdateData(myview);

        edtAmount = myview.findViewById(R.id.amount_budget_update);
        edtType = myview.findViewById(R.id.autoCompleteTextView_budget_update);
        edtDateStart = myview.findViewById(R.id.set_date_start_update);
        edtDateEnd = myview.findViewById(R.id.set_date_end_update);

        edtAmount.setText(String.valueOf(amount));
        edtAmount.setSelection(String.valueOf(amount).length());

        edtType.setText(type);
        edtType.setSelection(type.length());

        edtDateStart.setText(dateStart);
        edtDateStart.setSelection(dateStart.length());

        edtDateEnd.setText(dateEnd);
        edtDateEnd.setSelection(dateEnd.length());

        btnUpdate = myview.findViewById(R.id.btnUpdate_budget);
        btnDelete = myview.findViewById(R.id.btnDelete_budget);

        final AlertDialog dialog = mydialog.create();
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stamount = String.valueOf(amount);
                stamount = edtAmount.getText().toString().trim();
                int intamount = Integer.parseInt(stamount);
                type = edtType.getText().toString().trim();
                dateStart = edtDateStart.getText().toString().trim();
                dateEnd = edtDateEnd.getText().toString().trim();

                Category data = new Category(post_key, intamount, type, dateStart, dateEnd);
                mBudgetDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBudgetDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void insertDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.custom_layout_for_budgetdata, null);
        mydialog.setView(myview);

        selectDateInsertData(myview);

        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);
        EditText edtamount = myview.findViewById(R.id.amount_budget);
        EditText edttype = myview.findViewById(R.id.autoCompleteTextView_budget);
        EditText edtDateStart = myview.findViewById(R.id.set_date_start);
        EditText edtDateEnd = myview.findViewById(R.id.set_date_end);

        btnSave = myview.findViewById(R.id.btnSave_budget);
        btnCancel = myview.findViewById(R.id.btnCancel_budget);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = edtamount.getText().toString().trim();
                String type = edttype.getText().toString().trim();
                String dateStart = edtDateStart.getText().toString().trim();
                String dateEnd = edtDateEnd.getText().toString().trim();

                if(TextUtils.isEmpty(amount)){
                    edtamount.setError("Please Enter Amount");
                    return;
                }
                if(TextUtils.isEmpty(type)){
                    edttype.setError("Please Enter A Type");
                    return;
                }
                if(TextUtils.isEmpty(dateStart)){
                    edtDateStart.setError("Please Enter A Note");
                    return;
                }
                if(TextUtils.isEmpty(dateEnd)){
                    edtDateEnd.setError("Please Enter A Note");
                    return;
                }

                int amountInInt= Integer.parseInt(amount);
                //Create random ID inside database
                String id = mBudgetDatabase.push().getKey();
                Category data = new Category(id, amountInInt, type, dateStart, dateEnd);
                mBudgetDatabase.child(id).setValue(data);
                Toast.makeText(getActivity(), "Added Successfully!", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        String[] transaction = getResources().getStringArray(R.array.typesOfTransactions);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, transaction);
        AutoCompleteTextView textView = (AutoCompleteTextView) myview.findViewById(R.id.autoCompleteTextView_budget);
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

    //Set date start and end
    public void selectDateUpdateData(View myview){
        pickDateButton_start = myview.findViewById(R.id.setDateBtn_start_update);
        selectedDateTextView_start = myview.findViewById(R.id.set_date_start_update);
        pickDateButton_end = myview.findViewById(R.id.setDateBtn_end_update);
        selectedDateTextView_end = myview.findViewById(R.id.set_date_end_update);
        calendar = Calendar.getInstance();

        pickDateButton_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the DatePickerDialog class
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the selected date text view
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateSelectedDateTextView_start();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                // Show the dialog
                datePicker.show();
            }
        });

        pickDateButton_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the DatePickerDialog class
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the selected date text view
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateSelectedDateTextView_end();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                // Show the dialog
                datePicker.show();
            }
        });
    }
    public void selectDateInsertData(View myview){
        pickDateButton_start = myview.findViewById(R.id.setDateBtn_start);
        selectedDateTextView_start = myview.findViewById(R.id.set_date_start);
        pickDateButton_end = myview.findViewById(R.id.setDateBtn_end);
        selectedDateTextView_end = myview.findViewById(R.id.set_date_end);
        calendar = Calendar.getInstance();

        pickDateButton_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the DatePickerDialog class
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the selected date text view
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateSelectedDateTextView_start();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                // Show the dialog
                datePicker.show();
            }
        });

        pickDateButton_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new instance of the DatePickerDialog class
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Update the selected date text view
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateSelectedDateTextView_end();
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

                // Show the dialog
                datePicker.show();
            }
        });
    }
    private void updateSelectedDateTextView_start() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        selectedDateTextView_start.setText(dateFormat.format(calendar.getTime()));
    }

    private void updateSelectedDateTextView_end() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
        selectedDateTextView_end.setText(dateFormat.format(calendar.getTime()));
    }
}
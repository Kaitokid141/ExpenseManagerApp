package com.example.expensemanager;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.provider.SearchRecentSuggestions;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.expensemanager.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class IncomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // parameters (eg: ARG_ITEM_NUMBER)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public IncomeFragment() {

    }

    /**
     * Method to create a new instance of
     * Fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IncomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static IncomeFragment newInstance(String param1, String param2) {
        IncomeFragment fragment = new IncomeFragment();
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
    private DatabaseReference mIncomeDatabase;

    //Recyclerview
    private RecyclerView recyclerView;

    //Text view
    private TextView incomeTotalSum;

    //Calender
    private Button pickDateButton_start;
    private Button pickDateButton_end;
    private TextView selectedDateTextView_start;
    private TextView selectedDateTextView_end;
    private Calendar calendar;

    //Update edit text
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    //button for update and delete
    private Button btnUpdate;
    private Button btnDelete;

    private Button btnFilter;
    private Button btnCancel;

    private ImageButton btn_filter_income;

    //Data item value
    private String type;
    private String note;
    private int amount;
    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myview = inflater.inflate(R.layout.fragment_income, container, false);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();
        mIncomeDatabase = FirebaseDatabase.getInstance().getReference().child("IncomeData").child(uid);
        incomeTotalSum = myview.findViewById(R.id.income_txt_result);
        recyclerView = myview.findViewById(R.id.recycler_id_income);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        mIncomeDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalvalue = 0;
                for (DataSnapshot mysnapshot: snapshot.getChildren()){
                    Data data = mysnapshot.getValue(Data.class);
                    totalvalue += data.getAmount();
                    String sTotalvalue = String.valueOf(totalvalue);
                    incomeTotalSum.setText(sTotalvalue);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btn_filter_income = myview.findViewById(R.id.btn_filter_income);

        btn_filter_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertDataFilterItem();
            }
        });
        return myview;
    }

    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseRecyclerAdapter<Data, MyViewHolder>adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class,
                R.layout.income_recycler_data,
                MyViewHolder.class,
                mIncomeDatabase
        ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());

                Drawable drawable;
                String[] transaction = getResources().getStringArray(R.array.typesOfIncome);
                for(int i = 0; i < transaction.length; i++){
                    if(model.getType().equals(transaction[i])){
                        switch (i){
                            case 1:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.money);
                                viewHolder.setIcon(drawable);
                                break;
                            case 2:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.part_time);
                                viewHolder.setIcon(drawable);
                                break;
                            case 3:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.borrow);
                                viewHolder.setIcon(drawable);
                                break;
                            default:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.transaction);
                                viewHolder.setIcon(drawable);
                                break;
                        }
                    }
                }
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{
         View mView;

        public MyViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }
        private void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_income);
            mType.setText(type);
        }
        private void setNote(String note){
            TextView mNote = mView.findViewById(R.id.note_txt_income);
            mNote.setText(note);
        }
        private void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_income);
            mDate.setText(date);
        }
        private void setAmount(int amount){
            TextView mAmount = mView.findViewById(R.id.amount_txt_income);
            String smAmount = String.valueOf(amount);
            mAmount.setText(smAmount + " đ");
        }

        private void setIcon(Drawable drawable){
            ImageView mIcon = mView.findViewById(R.id.icon_income);
            mIcon.setBackground(drawable);
        }

    }
     private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item,  null);
        mydialog.setView(myview);
        String[] transaction = getResources().getStringArray(R.array.typesOfTransactions);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, transaction);
        AutoCompleteTextView textView = (AutoCompleteTextView) myview.findViewById(R.id.autoCompleteTextView_update);
        textView.setAdapter(arrayAdapter);
        edtAmount = myview.findViewById(R.id.amount);
        edtType = myview.findViewById(R.id.autoCompleteTextView_update);
        edtNote = myview.findViewById(R.id.note_edt);

        //Set data to edit text
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
            public void onClick(View view) {
                type = edtType.getText().toString().trim();
                note = edtNote.getText().toString().trim();

                if(TextUtils.isEmpty(note)){
                    edtNote.setText("none");
                    return;
                }

                String mdamount = String.valueOf(amount);
                mdamount = edtAmount.getText().toString().trim();
                int myAmount = Integer.parseInt(mdamount);

                String mDate = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(myAmount, type, note, post_key, mDate);
                mIncomeDatabase.child(post_key).setValue(data);
                dialog.dismiss();
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                mIncomeDatabase.child(post_key).removeValue();
                dialog.dismiss();
            }
        });
        dialog.show();
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

    public void insertDataFilterItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.filter_searching, null);
        mydialog.setView(myview);


        final AlertDialog dialog = mydialog.create();
        dialog.setCancelable(false);
        EditText edtamount = myview.findViewById(R.id.amount_filter);
        EditText edttype = myview.findViewById(R.id.autoCompleteTextView_filter);
        EditText edtDateStart = myview.findViewById(R.id.set_date_start_filter);
        EditText edtDateEnd = myview.findViewById(R.id.set_date_end_filter);

        btnFilter = myview.findViewById(R.id.btnFilter);
        btnCancel = myview.findViewById(R.id.btnCancel_filter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amount = edtamount.getText().toString().trim();
                String type = edttype.getText().toString().trim();
                String dateStart = edtDateStart.getText().toString().trim();
                String dateEnd = edtDateEnd.getText().toString().trim();

//                if(TextUtils.isEmpty(amount)){
//                    edtamount.setError("Please Enter Amount");
//                    return;
//                }
//                if(TextUtils.isEmpty(type)){
//                    edttype.setError("Please Enter A Type");
//                    return;
//                }
//                if(TextUtils.isEmpty(dateStart)){
//                    edtDateStart.setError("Please Enter A Note");
//                    return;
//                }
//                if(TextUtils.isEmpty(dateEnd)){
//                    edtDateEnd.setError("Please Enter A Note");
//                    return;
//                }

                int amountInInt= Integer.parseInt(amount);
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
        AutoCompleteTextView textView = (AutoCompleteTextView) myview.findViewById(R.id.autoCompleteTextView_filter);
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
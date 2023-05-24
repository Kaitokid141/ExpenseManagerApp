package com.example.expensemanager;

import static com.example.expensemanager.Model.DateCalculator.checkDays;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.expensemanager.Adapter.MyAdapterIncome;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExpenseFragment() {

    }

    /**
     * Method to create a new instance of
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

    //Calender
    private Button pickDateButton_start;
    private Button pickDateButton_end;
    private TextView selectedDateTextView_start;
    private TextView selectedDateTextView_end;
    private Calendar calendar;


    //Edit data item
    private EditText edtAmount;
    private EditText edtType;
    private EditText edtNote;

    private Button btnUpdate;
    private Button btnDelete;
    private Button btnFilter;
    private Button btnCancel;
    private ImageButton btn_filter_expense;

    //Data variable
    private String type;
    private String note;
    private int amount;
    private String post_key;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myview =  inflater.inflate(R.layout.fragment_expense, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();
        String uid = mUser.getUid();

        mExpenseDatabase = FirebaseDatabase.getInstance().getReference().child("ExpenseData").child(uid);
        expenseSumResult = myview.findViewById(R.id.expense_txt_result);
        recyclerView = myview.findViewById(R.id.recycler_id_expense);

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

        btn_filter_expense = myview.findViewById(R.id.btn_filter_expense);
        btn_filter_expense.setOnClickListener(new View.OnClickListener() {
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
        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                        Data.class,
                        R.layout.expense_recycler_data,
                        MyViewHolder.class,
                        mExpenseDatabase
                ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                viewHolder.setType(model.getType());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());
                viewHolder.setAmount(model.getAmount());

                Drawable drawable;
                String[] transaction = getResources().getStringArray(R.array.typesOfTransactions);
                for(int i = 0; i < transaction.length; i++){
                    if(model.getType().equals(transaction[i])) {
                        switch (i) {
                            case 1:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.restaurant);
                                viewHolder.setIcon(drawable);
                                break;
                            case 2:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.shipped);
                                viewHolder.setIcon(drawable);
                                break;
                            case 3:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.house);
                                viewHolder.setIcon(drawable);
                                break;
                            case 4:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.faucet);
                                viewHolder.setIcon(drawable);
                                break;
                            case 5:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.electricity_bill);
                                viewHolder.setIcon(drawable);
                                break;
                            case 6:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.phone);
                                viewHolder.setIcon(drawable);
                                break;
                            case 7:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.flame);
                                viewHolder.setIcon(drawable);
                                break;
                            case 8:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.television);
                                viewHolder.setIcon(drawable);
                                break;
                            case 9:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.internet);
                                viewHolder.setIcon(drawable);
                                break;
                            case 10:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.bill);
                                viewHolder.setIcon(drawable);
                                break;
                            case 11:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.support);
                                viewHolder.setIcon(drawable);
                                break;
                            case 12:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.car);
                                viewHolder.setIcon(drawable);
                                break;
                            case 13:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.heart_beat);
                                viewHolder.setIcon(drawable);
                                break;
                            case 14:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.insurance);
                                viewHolder.setIcon(drawable);
                                break;
                            case 15:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.education);
                                viewHolder.setIcon(drawable);
                                break;
                            case 16:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.storage_box);
                                viewHolder.setIcon(drawable);
                                break;
                            case 17:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.furniture);
                                viewHolder.setIcon(drawable);
                                break;
                            case 18:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.pawprint);
                                viewHolder.setIcon(drawable);
                                break;
                            case 19:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.public_service);
                                viewHolder.setIcon(drawable);
                                break;
                            case 20:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.expenses);
                                viewHolder.setIcon(drawable);
                                break;
                            case 21:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.sports);
                                viewHolder.setIcon(drawable);
                                break;
                            case 22:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.salon);
                                viewHolder.setIcon(drawable);
                                break;
                            case 23:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.solidarity);
                                viewHolder.setIcon(drawable);
                                break;
                            case 24:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.customer_support);
                                viewHolder.setIcon(drawable);
                                break;
                            case 25:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.party);
                                viewHolder.setIcon(drawable);
                                break;
                            case 26:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.party);
                                viewHolder.setIcon(drawable);
                                break;
                            case 27:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.investment);
                                viewHolder.setIcon(drawable);
                                break;
                            case 28:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.party);
                                viewHolder.setIcon(drawable);
                                break;
                            case 29:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.lending);
                                viewHolder.setIcon(drawable);
                                break;
                            case 30:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.lending);
                                viewHolder.setIcon(drawable);
                                break;
                            case 31:
                                drawable = ContextCompat.getDrawable(getContext(), R.drawable.lending);
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
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setType(String type){
            TextView mType = mView.findViewById(R.id.type_txt_expense);
            mType.setText(type);
        }

        public void setNote(String note){
            TextView mNote = mView.findViewById(R.id.note_txt_expense);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate = mView.findViewById(R.id.date_txt_expense);
            mDate.setText(date);
        }

        public void setAmount(int amount){
            TextView mAmount = mView.findViewById(R.id.amount_txt_expense);
            String smAmount = String.valueOf(amount);
            mAmount.setText(smAmount + " đ");
        }

        public void setIcon(Drawable drawable){
            ImageView mIcon = mView.findViewById(R.id.icon_expense);
            mIcon.setBackground(drawable);
        }
    }

    private void updateDataItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myview = inflater.inflate(R.layout.update_data_item, null);
        mydialog.setView(myview);
        String[] transaction = getResources().getStringArray(R.array.typesOfTransactions);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext(), R.layout.dropdown_item, transaction);
        AutoCompleteTextView textView = (AutoCompleteTextView) myview.findViewById(R.id.autoCompleteTextView_update);
        textView.setAdapter(arrayAdapter);

        edtAmount = myview.findViewById(R.id.amount);
        edtNote = myview.findViewById(R.id.note_edt);
        edtType = myview.findViewById(R.id.autoCompleteTextView_update);

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

                if(TextUtils.isEmpty(note)){
                    edtNote.setText("none");
                    return;
                }

                String stamount = String.valueOf(amount);
                stamount = String.valueOf(edtAmount.getText());
                System.out.print(stamount);
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

    public void insertDataFilterItem(){
        AlertDialog.Builder mydialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View myview=inflater.inflate(R.layout.filter_searching, null);
        mydialog.setView(myview);

        selectDateFilterData(myview);

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
                String amount1 = edtamount.getText().toString().trim();
                String type1 = edttype.getText().toString().trim();
                String dateStart = edtDateStart.getText().toString().trim();
                String dateEnd = edtDateEnd.getText().toString().trim();

                List<Data> myList = filterDate(mExpenseDatabase, amount1, type1, dateStart, dateEnd);
                MyAdapterIncome myAdapter = new MyAdapterIncome(myList);
                recyclerView.setAdapter(myAdapter);

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

    public List<Data> filterDate(DatabaseReference mDatabase, String amount1, String type1, String dateStart1, String dateEnd1) {
        List<Data> list = new ArrayList<Data>();
        final String[] temp = new String[4];
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Data data = dataSnapshot.getValue(Data.class);
                    if(amount1.isEmpty())
                        temp[0] = "0";
                    else
                        temp[0] = amount1;
                    if(type1.equals("Chọn loại"))
                        temp[1] = data.getType();
                    else
                        temp[1] = type1;
                    if(dateStart1.isEmpty())
                        temp[2] = "May 1, 2023";
                    else
                        temp[2] = dateStart1;
                    if(dateEnd1.isEmpty())
                        temp[3] = "May 30, 2023";
                    else
                        temp[3] = dateEnd1;
                    if (data.getAmount() >= Integer.parseInt(temp[0]) && data.getType().equals(temp[1]) && checkDays(data.getDate(), temp[2], temp[3]))
                        list.add(data);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return list;
    }

    public void selectDateFilterData(View myview){
        pickDateButton_start = myview.findViewById(R.id.setDateBtn_start_filter);
        selectedDateTextView_start = myview.findViewById(R.id.set_date_start_filter);
        pickDateButton_end = myview.findViewById(R.id.setDateBtn_end_filter);
        selectedDateTextView_end = myview.findViewById(R.id.set_date_end_filter);
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
package com.example.expensemanager.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expensemanager.ExpenseFragment.MyViewHolder;
import com.example.expensemanager.Model.Data;
import com.example.expensemanager.R;

import java.util.List;

public class MyAdapterExpense extends RecyclerView.Adapter<MyViewHolder> {
    private List<Data> myList;

    public MyAdapterExpense(List<Data> list) {
        this.myList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.expense_recycler_data, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Data myObject = myList.get(position);
        holder.setType(myObject.getType());
        holder.setNote(myObject.getNote());
        holder.setDate(myObject.getDate());
        holder.setAmount(myObject.getAmount());
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }
}


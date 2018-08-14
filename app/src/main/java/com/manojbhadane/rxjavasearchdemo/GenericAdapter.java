package com.manojbhadane.rxjavasearchdemo;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class GenericAdapter<T, D> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Context mContext;
    private ArrayList<T> mArrayList;

    public abstract int getLayoutResId();

    //    public abstract void onBindData(RecyclerView.ViewHolder holder, T model, int position, D dataBinding);
    public abstract void onBindData(T model, int position, D dataBinding);

    public abstract void onItemClick(T model, int position);


    public GenericAdapter(Context context, ArrayList<T> arrayList) {
        this.mContext = context;
        this.mArrayList = arrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding dataBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getLayoutResId(), parent, false);
        RecyclerView.ViewHolder holder = new ItemViewHolder(dataBinding);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        onBindData(mArrayList.get(position), position, ((ItemViewHolder) holder).mDataBinding);

        ((ViewDataBinding) ((ItemViewHolder) holder).mDataBinding).getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick(mArrayList.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void addItems(ArrayList<T> arrayList) {
        mArrayList = arrayList;
        this.notifyDataSetChanged();
    }

    public T getItem(int position) {
        return mArrayList.get(position);
    }

    public Context getContext(){
        return mContext;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        protected D mDataBinding;

        public ItemViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            mDataBinding = (D) binding;
        }
    }
}

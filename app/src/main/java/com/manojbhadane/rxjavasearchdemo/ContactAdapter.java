package com.manojbhadane.rxjavasearchdemo;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.manojbhadane.rxjavasearchdemo.databinding.ContactRowItemBinding;
import com.manojbhadane.rxjavasearchdemo.model.Contact;

import java.util.ArrayList;

public class ContactAdapter extends GenericAdapter<Contact, ContactRowItemBinding> {

    public ContactAdapter(Context context, ArrayList<Contact> arrayList) {
        super(context, arrayList);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.contact_row_item;
    }

    @Override
    public void onBindData(Contact model, int position, ContactRowItemBinding dataBinding) {

        dataBinding.name.setText(model.getName());
        dataBinding.phone.setText(model.getPhone());

        Glide.with(mContext)
                .load(model.getProfileImage())
                .apply(RequestOptions.circleCropTransform())
                .into(dataBinding.thumbnail);
    }

    @Override
    public void onItemClick(Contact model, int position) {

    }
}

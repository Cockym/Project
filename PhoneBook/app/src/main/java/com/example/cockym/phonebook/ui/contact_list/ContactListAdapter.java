package com.example.cockym.phonebook.ui.contact_list;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.cockym.phonebook.bean.Contact;
import com.example.cockym.phonebook.R;

import java.util.ArrayList;
import java.util.List;

public class ContactListAdapter extends BaseAdapter {

    private List<Contact> items = new ArrayList<>();
    private final LayoutInflater inflater;

    public ContactListAdapter(Activity context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setItems(List<Contact> items) {
        if (items == null) {
            return;
        }
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Contact getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return items.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = inflater.inflate(R.layout.contact_list_row, viewGroup, false);
        Contact item = getItem(i);
        ((TextView) v.findViewById(R.id.tv_name)).setText("Name: " + item.name);
        ((TextView) v.findViewById(R.id.tv_phone)).setText("Phone: " + item.number);
        ((TextView) v.findViewById(R.id.tv_email)).setText("Email: " + item.email);
        return v;
    }
}

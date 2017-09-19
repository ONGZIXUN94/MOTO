package com.example.dqw648.moto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<User> {

    private LayoutInflater mInflater;
    private ArrayList<User> users;
    private int mViewResourceId;

    public ListAdapter(Context context, int textViewResourceId, ArrayList<User> users) {
        super(context, textViewResourceId, users);
        this.users = users;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mViewResourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);

        User user = users.get(position);

        if (user != null) {
            TextView txt_name = (TextView) convertView.findViewById(R.id.txt_name);
            TextView txt_id = (TextView) convertView.findViewById(R.id.txt_id);
            TextView txt_identity = (TextView) convertView.findViewById(R.id.txt_identity);
            if (txt_name != null) {
                txt_name.setText(user.get_name());
            }
            if (txt_id != null) {
                txt_id.setText((user.get_coreid()));
            }
            if (txt_identity != null) {
                txt_identity.setText((user.get_identity()));
            }
        }

        return convertView;
    }
}

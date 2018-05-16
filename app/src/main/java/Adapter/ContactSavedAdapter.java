package Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.halaat.halaat.R;

import java.util.ArrayList;

import Modal.Contact;

/**
 * Created by Dell on 12/25/2017.
 */

public class ContactSavedAdapter extends ArrayAdapter {

    ArrayList<Contact> contacts;

    public ContactSavedAdapter(@NonNull Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_item_saved_contacts, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        name.setText(contacts.get(position).getName());




        TextView number = convertView.findViewById(R.id.phone);
        number.setText(contacts.get(position).getPhone());
        return convertView;
    }
}

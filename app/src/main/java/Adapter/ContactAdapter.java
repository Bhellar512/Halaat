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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.halaat.halaat.R;

import java.util.ArrayList;

import Modal.Contact;
import helpers.BasePreferenceHelper;

/**
 * Created by Dell on 12/25/2017.
 */

public class ContactAdapter extends ArrayAdapter {

    ArrayList<Contact> contacts;
    BasePreferenceHelper prefHelper;
    private ArrayList<Contact> savedContacts = new ArrayList<>();

    public ContactAdapter(@NonNull Context context, int resource, ArrayList<Contact> contacts, BasePreferenceHelper prefHelper) {
        super(context, 0, contacts);
        this.contacts = contacts;
        this.prefHelper = prefHelper;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_item, parent, false);
        }
        TextView name = convertView.findViewById(R.id.name);
        name.setText(contacts.get(position).getName());

        CheckBox cb_tick = convertView.findViewById(R.id.cb_tick);
        cb_tick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!contacts.get(position).isSelected()) {
                    contacts.get(position).setSelected(true);
                } else {
                    contacts.get(position).setSelected(false);

                }
               // contacts.get(position).setSelected(true);

            }
        });

      /*  Gson gson = new Gson();
        savedContacts = gson.fromJson(prefHelper.getSavedContacts(), new TypeToken<ArrayList<Contact>>() {
        }.getType());
        if (savedContacts != null && savedContacts.size() > 0) {
            for (Contact item : savedContacts) {
                if (item.getPhone().equals(contacts.get(position).getPhone())) {
                    cb_tick.setChecked(true);
                }
            }
        }*/


        TextView number = convertView.findViewById(R.id.phone);
        number.setText(contacts.get(position).getPhone());
        return convertView;
    }

    public void clearList() {
        contacts.clear();
        notifyDataSetChanged();
    }
}

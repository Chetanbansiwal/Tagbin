package com.faddensoft.breakout;

import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class NameFragment extends Fragment {
    public EditText userName;
    public EditText mobile;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.name_fragment, container, false);
        userName = (EditText) view.findViewById(R.id.nameText);
        mobile = (EditText) view.findViewById(R.id.mobileText);
        final Button register = (Button) view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                ButtonClicked(v);
            }

        });
        return view;
    }
    public void ButtonClicked(View v)
    {
        if(userName.getText().toString()!=null && mobile.getText().toString()!= null) {
            BreakoutActivity.name = userName.getText().toString();
            BreakoutActivity.mobile = mobile.getText().toString();
            Fragment fr = new RunsSlabFragment();
            getFragmentManager().beginTransaction().remove(this).commit();
           // getFragmentManager().beginTransaction().add(R.id.fragment, fr).commit();
            getFragmentManager().beginTransaction().replace(R.id.fragment, fr).commit();
        }
        else
        {
            Toast.makeText(getActivity(),"Enter", Toast.LENGTH_LONG).show();
            Fragment fr = new RunsSlabFragment();
            getFragmentManager().beginTransaction().remove(this).commit();
           // getFragmentManager().beginTransaction().add(R.id.fragment, fr).commit();
            getFragmentManager().beginTransaction().replace(R.id.fragment, fr).commit();

        }

    }
}

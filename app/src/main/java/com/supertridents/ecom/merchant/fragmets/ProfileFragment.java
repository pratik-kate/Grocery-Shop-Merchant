package com.supertridents.ecom.merchant.fragmets;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.supertridents.ecom.merchant.MainActivity;
import com.supertridents.ecom.merchant.R;

import static android.content.Context.MODE_PRIVATE;


public class ProfileFragment extends Fragment {


    TextView name,email;
    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        name = view.findViewById(R.id.pUser);
        email = view.findViewById(R.id.pEmail);

        SharedPreferences preferences = getActivity().getSharedPreferences(MainActivity.INFO,MODE_PRIVATE);
        String uname = preferences.getString(MainActivity.USER,"user");
        String uemail = preferences.getString(MainActivity.EMAIL,"user@email.com");

        name.setText(uname);
        email.setText(uemail);
        return view;
    }
}
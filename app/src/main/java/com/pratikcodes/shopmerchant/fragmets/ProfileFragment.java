package com.pratikcodes.shopmerchant.fragmets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.pratikcodes.shopmerchant.LoginActivity;
import com.pratikcodes.shopmerchant.MainActivity;
import com.pratikcodes.shopmerchant.R;

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
        String uname = preferences.getString(MainActivity.USER,"Merchant");

        String uemail = preferences.getString(MainActivity.EMAIL,"user@email.com");

        name.setText(uname);
        email.setText(uemail);

        TextView v = view.findViewById(R.id.logout);

        v.setOnClickListener(v1 -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });
        return view;
    }
}
package com.supertridents.ecom.merchant.fragmets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.supertridents.ecom.merchant.R;
import com.supertridents.ecom.merchant.adapter.MenuViewHolder;
import com.supertridents.ecom.merchant.adapter.OrdersViewHolder;
import com.supertridents.ecom.merchant.model.HomeModel;
import com.supertridents.ecom.merchant.model.OrderModel;

import java.util.ArrayList;


public class CartFragment extends Fragment {



    RecyclerView list;
    private DatabaseReference myref;
    private ArrayList<OrderModel> modelArrayList;
    private OrdersViewHolder menuViewHolder;
    String customer;
    ArrayList<String> s;
    TextView empty;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_cart, container, false);

        empty = view.findViewById(R.id.textView3);

        list = view.findViewById(R.id.orders_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);

        myref = FirebaseDatabase.getInstance().getReference();

        s = new ArrayList<>();
        modelArrayList = new ArrayList<>();
        if(modelArrayList.isEmpty()){
            view.findViewById(R.id.textView3).setVisibility(View.VISIBLE);
        }
        clearAll();
        getDataFromFirebase();


        return view;
    }


    private void getDataFromFirebase() {


        Query query = myref.child("orders");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearAll();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    OrderModel mode = new OrderModel();
                    mode.setName(snapshot.child("name").getValue().toString());
                    mode.setAddress(snapshot.child("address").getValue().toString());
                    mode.setPincode(snapshot.child("pincode").getValue().toString());
                    mode.setPhone(snapshot.child("phone").getValue().toString());
                    mode.setLandmark(snapshot.child("landmark").getValue().toString());
                    mode.setNo(snapshot.child("paymentId").getValue().toString());
                    mode.setAmount(snapshot.child("amount").getValue().toString());
                    mode.setProducts(snapshot.child("products").getValue().toString());


                    modelArrayList.add(mode);
                }
                menuViewHolder = new OrdersViewHolder(getContext(),modelArrayList);
                list.setAdapter(menuViewHolder);
                menuViewHolder.notifyDataSetChanged();
                empty.setVisibility(View.INVISIBLE);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private  void clearAll(){
        if(modelArrayList != null){
            modelArrayList.clear();

            if(menuViewHolder != null){
                menuViewHolder.notifyDataSetChanged();
            }
        }else{

            modelArrayList = new ArrayList<>();
        }
    }

}
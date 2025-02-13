package com.pratikcodes.shopmerchant.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pratikcodes.shopmerchant.R;
import com.pratikcodes.shopmerchant.model.ProductModel;

import java.util.ArrayList;

public class ProductViewHolder extends RecyclerView.Adapter<ProductViewHolder.ViewHolder> {

    private  static  final String tag = "Recycler";
    private Context context;
    private ArrayList<ProductModel> modelArrayList;
    FirebaseStorage storage;
    StorageReference storageReference;
    TextInputLayout productName,productPrice,productDescription;
    TextView chooseImage,additem;
    Uri saveUri;
    String test;
    private final int PICK_IMAGE_REQUEST = 1;

    public ProductViewHolder(Context context, ArrayList<ProductModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }


    @NonNull
    @Override
    public ProductViewHolder.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.design_home,parent,false);


        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(modelArrayList.get(position).getName());
        holder.price.setText(modelArrayList.get(position).getPrice());

        Glide.with(context)
                .load(modelArrayList.get(position).getImage())
                .override(250,250)
                .into(holder.imageView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                final Dialog dialog2 = new Dialog(context);
//                dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
//                dialog2.setContentView(R.layout.product);
//                dialog2.setCancelable(true);
//                WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
//                lp2.copyFrom(dialog2.getWindow().getAttributes());
//                lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
//                lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//                TextInputLayout productName = (dialog2.findViewById(R.id.item_name));
//                TextInputLayout productDescription = (dialog2.findViewById(R.id.item_des));
//                TextInputLayout productPrice = (dialog2.findViewById(R.id.item_price));
//
//               TextView chooseImage = dialog2.findViewById(R.id.chooseImage);
//               TextView additem = dialog2.findViewById(R.id.additem);
//               productName.getEditText().setText(modelArrayList.get(position).getName());
//               productName.getEditText().setClickable(false);
//               chooseImage.setVisibility(View.GONE);
//
//                additem.setOnClickListener(v1 -> {
//
//                        String name = modelArrayList.get(position).getName().trim();
//                        String desc = productDescription.getEditText().getText().toString().trim();
//                        String price = productPrice.getEditText().getText().toString().trim();
//                        ProgressDialog progressDialog = new ProgressDialog(context);
//                        progressDialog.setMessage("Uploading Product....");
//                        progressDialog.show();
//
//                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("products").child("name");
//                    reference.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                test = snapshot.getValue(String.class);
//                                HashMap<String, Object> map = new HashMap<>();
//                                //map.put("image",download.toString());
//                                //map.put("name",name);
//                                map.put("description", desc);
//                                map.put("price", price);
//
//                                if(test.equals(name)){
//                                FirebaseDatabase.getInstance().getReference().child("products").setValue(map)
//                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Void> task) {
//                                                //Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//                                        progressDialog.dismiss();
//                                        dialog2.dismiss();
//
//                                    }
//                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
//                                        progressDialog.dismiss();
//                                        dialog2.dismiss();
//                                    }
//                                });
//                                break;
//                            }
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                        }
//                    });
//
//                });
//
//
//                dialog2.show();
//                dialog2.getWindow().setAttributes(lp2);
//
//            }
//        });


    }

    private void startActivityForResult(Intent select_picture, int pick_image_request) {
        //saveUri = select_picture.getData();
        saveUri = Uri.parse("products");
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        ImageView imageView;
        TextView textView,price;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.productImage);
            textView = itemView.findViewById(R.id.productName);
            price = itemView.findViewById(R.id.price);

        }
    }



}

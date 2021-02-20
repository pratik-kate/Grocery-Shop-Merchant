package com.supertridents.ecom.merchant;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.supertridents.ecom.merchant.adapter.ProductViewHolder;
import com.supertridents.ecom.merchant.model.ProductModel;

import java.util.ArrayList;
import java.util.HashMap;

public class ShopActivity extends AppCompatActivity {

    int catId;
    //String categoryId;
    RecyclerView recyclerView;
    private DatabaseReference myref;
    private ArrayList<ProductModel> modelArrayList;
    private ProductViewHolder menuViewHolder;
    private ShimmerFrameLayout shimmerFrameLayout;
    public static String categoryId;

    FloatingActionButton addCat;
    FirebaseStorage storage;
    StorageReference storageReference;
    TextInputLayout productName,productPrice,productDescription;
    TextView chooseImage,additem;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        getSupportActionBar().setTitle("Products");
        shimmerFrameLayout = findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();


        Intent intent = getIntent();
        catId = intent.getIntExtra(MainActivity.CATID,0);
        categoryId = String.valueOf(catId+1);

        myref = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        modelArrayList = new ArrayList<>();
        clearAll();
        getDataFromFirebase();


        //Adding Products

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        additem = findViewById(R.id.addCat);
        addCat = findViewById(R.id.addProduct);
        addCat.setOnClickListener(v -> {

            final Dialog dialog2 = new Dialog(ShopActivity.this);
            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog2.setContentView(R.layout.product);
            dialog2.setCancelable(true);
            WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
            lp2.copyFrom(dialog2.getWindow().getAttributes());
            lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;

            productName = (dialog2.findViewById(R.id.item_name));
            productDescription = (dialog2.findViewById(R.id.item_des));
            productPrice = (dialog2.findViewById(R.id.item_price));


            chooseImage = dialog2.findViewById(R.id.chooseImage);
            additem = dialog2.findViewById(R.id.additem);

            chooseImage.setOnClickListener(v1 -> {
                Intent intentimg = new Intent();
                intentimg.setType("image/*");
                intentimg.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentimg,"select Picture"),PICK_IMAGE_REQUEST);
            });
            additem.setOnClickListener(v1 -> {
                if(saveUri != null){
                    String name = productName.getEditText().getText().toString().trim();
                    String desc = productDescription.getEditText().getText().toString().trim();
                    String price = productPrice.getEditText().getText().toString().trim();
                    ProgressDialog progressDialog = new ProgressDialog(ShopActivity.this);
                    progressDialog.setMessage("Uploading Product....");
                    progressDialog.show();

                    SharedPreferences preferences = getSharedPreferences(MainActivity.CATEGORIES, Context.MODE_PRIVATE);
                    int count = Integer.parseInt(preferences.getString(MainActivity.ITEMCOUNTER,"25"));
                    count++;

                    String nCategory = String.valueOf(count);

                    SharedPreferences.Editor editor =getSharedPreferences(MainActivity.CATEGORIES,Context.MODE_PRIVATE).edit();
                    editor.putString(MainActivity.CATCOUNTER,nCategory);
                    editor.apply();
                    editor.commit();
                    StorageReference filepath = storageReference.child("products").child(nCategory);
                    filepath.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri download=uri;

                                    HashMap<String ,Object> map = new HashMap<>();
                                    map.put("image",download.toString());
                                    map.put("name",name);
                                    map.put("description",desc);
                                    map.put("price",price);
                                    map.put("catId",categoryId);

                                    FirebaseDatabase.getInstance().getReference().child("products").child(nCategory).setValue(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ShopActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            dialog2.dismiss();

                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(ShopActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            dialog2.dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    });


                }

            });


            dialog2.show();
            dialog2.getWindow().setAttributes(lp2);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData() != null){
            saveUri = data.getData();
            chooseImage.setText("Image Selected");
            chooseImage.setClickable(false);
        }
    }

    private void getDataFromFirebase() {

        Query query = myref.child("products")
                .orderByChild("catId").equalTo(categoryId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clearAll();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ProductModel mode = new ProductModel();

                    mode.setName(snapshot.child("name").getValue().toString());
                    mode.setPrice(snapshot.child("price").getValue().toString());
                    mode.setDescription(snapshot.child("description").getValue().toString());
                    mode.setImage(snapshot.child("image").getValue().toString());

                    modelArrayList.add(mode);
                }
                menuViewHolder = new ProductViewHolder(ShopActivity.this,modelArrayList);
                recyclerView.setAdapter(menuViewHolder);
                menuViewHolder.notifyDataSetChanged();
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.INVISIBLE);

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
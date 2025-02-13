package com.pratikcodes.shopmerchant.fragmets;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
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
import com.pratikcodes.shopmerchant.MainActivity;
import com.pratikcodes.shopmerchant.R;
import com.pratikcodes.shopmerchant.adapter.MenuViewHolder;
import com.pratikcodes.shopmerchant.model.HomeModel;

import java.util.ArrayList;
import java.util.HashMap;
import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    SliderLayout sliderLayout ;
    HashMap<String, String> HashMapForURL ;
    HashMap<String, Integer> HashMapForLocalRes ;
    RecyclerView list;
    private DatabaseReference myref;
    private ArrayList<HomeModel> modelArrayList;
    private MenuViewHolder menuViewHolder;
    private Context mcontext;
    private ShimmerFrameLayout shimmerFrameLayout;

    FloatingActionButton addCat;
    FirebaseStorage storage;
    StorageReference storageReference;
    TextInputLayout catName;
    TextView chooseImage,addcat;
    Uri saveUri;
    private final int PICK_IMAGE_REQUEST = 1;
    public static ArrayList<String> names;
    public static ArrayList<String> images;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        shimmerFrameLayout = view.findViewById(R.id.shimmerLayout);
        shimmerFrameLayout.startShimmer();
        myref = FirebaseDatabase.getInstance().getReference();
        images = new ArrayList<>();
        names = new ArrayList<>();

        //banner
        sliderLayout = (SliderLayout)view.findViewById(R.id.slider);
        AddImagesUrlOnline();

        //Showing Categories
        list = view.findViewById(R.id.list);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        list.setLayoutManager(layoutManager);
        list.setHasFixedSize(true);
        modelArrayList = new ArrayList<>();
        clearAll();
        getDataFromFirebase();


        //Adding Categories
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        addCat = view.findViewById(R.id.addCat);
        addCat.setOnClickListener(v -> {

            final Dialog dialog2 = new Dialog(getContext());
            dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog2.setContentView(R.layout.category);
            dialog2.setCancelable(true);
            WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
            lp2.copyFrom(dialog2.getWindow().getAttributes());
            lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;

            catName = (dialog2.findViewById(R.id.cat_name));


            chooseImage = dialog2.findViewById(R.id.chooseImage);
            addcat = dialog2.findViewById(R.id.add);

            chooseImage.setOnClickListener(v1 -> {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"select Picture"),PICK_IMAGE_REQUEST);
            });
            addcat.setOnClickListener(v1 -> {
                if(saveUri != null){
                    String name = catName.getEditText().getText().toString().trim();
                    ProgressDialog progressDialog = new ProgressDialog(getContext());
                    progressDialog.setMessage("Uploading Category....");
                    progressDialog.show();

                    SharedPreferences preferences = getActivity().getSharedPreferences(MainActivity.CATEGORIES,Context.MODE_PRIVATE);
                    int count = Integer.parseInt(preferences.getString(MainActivity.CATCOUNTER,"5"));
                    count++;

                    String nCategory = String.valueOf(count);

                    SharedPreferences.Editor editor = getActivity().getSharedPreferences(MainActivity.CATEGORIES,Context.MODE_PRIVATE).edit();
                    editor.putString(MainActivity.CATCOUNTER,nCategory);
                    editor.apply();
                    editor.commit();
                    StorageReference filepath = storageReference.child("category").child(nCategory);
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

                                    FirebaseDatabase.getInstance().getReference().child("category").child(nCategory).setValue(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            dialog2.dismiss();

                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
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

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data!=null && data.getData() != null){
            saveUri = data.getData();
        }
    }

    private void getDataFromFirebase() {

        Query query = myref.child("category");
       query.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               clearAll();
               for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                   HomeModel mode = new HomeModel();
                   mode.setImageUrl(snapshot.child("image").getValue().toString());
                   mode.setName(snapshot.child("name").getValue().toString());

                   modelArrayList.add(mode);
               }
               menuViewHolder = new MenuViewHolder(getContext(),modelArrayList);
               list.setAdapter(menuViewHolder);
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


    @Override
    public void onStart() {
        super.onStart();
        //adapter.startListening();
    }
    @Override
    public void onStop()
    {
        super.onStop();
        sliderLayout.stopAutoCycle();
        //adapter.stopListening();
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
    public void onPageSelected(int position) {

        Log.d("Slider Demo", "Page Changed: " + position);

    }
    @Override
    public void onPageScrollStateChanged(int state) {

    }
    public void AddImagesUrlOnline(){

        HashMapForURL = new HashMap<String, String>();
//        HashMapForURL.put("CupCake", "https://github.com/pratik-kate/SampleData/blob/main/Farsan_dryfruits.jpg?raw=true");
//        HashMapForURL.put("Donut", "https://github.com/pratik-kate/SampleData/blob/main/Farsan_dryfruits.jpg?raw=true");
//        HashMapForURL.put("Eclair", "https://github.com/pratik-kate/SampleData/blob/main/Farsan_dryfruits.jpg?raw=true");


        FirebaseDatabase.getInstance().getReference().child("offers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
//                    images.add(snapshot.child("image").getValue().toString());
//                    names.add(snapshot.child("description").getValue().toString());
                    if(HashMapForURL.size()>=3){
                        HashMapForURL.clear();
                    }
                    HashMapForURL.put(snapshot.child("description").getValue().toString(),snapshot.child("image").getValue().toString());
                }

                for(String name : HashMapForURL.keySet()){

                    TextSliderView textSliderView = new TextSliderView(getContext());
                    textSliderView
                            .description(name)
                            .image(HashMapForURL.get(name))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    //
                                    final Dialog dialog2 = new Dialog(getContext());
                                    dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                                    dialog2.setContentView(R.layout.offers);
                                    dialog2.setCancelable(true);
                                    WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
                                    lp2.copyFrom(dialog2.getWindow().getAttributes());
                                    lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
                                    lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
                                    TextView img = dialog2.findViewById(R.id.chooseImage);
                                    TextView additem = dialog2.findViewById(R.id.additem);
                                    TextInputLayout des = dialog2.findViewById(R.id.item_des);

                                    img.setOnClickListener(v1 -> {
                                        Intent intentimg = new Intent();
                                        intentimg.setType("image/*");
                                        intentimg.setAction(Intent.ACTION_GET_CONTENT);
                                        startActivityForResult(Intent.createChooser(intentimg,"select Picture"),PICK_IMAGE_REQUEST);
                                    });
                                    additem.setOnClickListener(v1 -> {
                                        if(saveUri != null){

                                            String s = des.getEditText().getText().toString().trim();

                                            ProgressDialog progressDialog = new ProgressDialog(getContext());
                                            progressDialog.setMessage("Uploading Product....");
                                            progressDialog.show();

                                            StorageReference filepath = storageReference.child("offers").child(name);
                                            filepath.putFile(saveUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
                                                            Uri download=uri;

                                                            HashMap<String ,Object> map = new HashMap<>();
                                                            map.put("image",download.toString());
                                                            map.put("description",s);


                                                            FirebaseDatabase.getInstance().getReference().child("offers").child(name).setValue(map)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                    dialog2.dismiss();

                                                                }
                                                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
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

                                    //Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                                }
                            });

                    textSliderView.bundle(new Bundle());

                    textSliderView.getBundle()
                            .putString("extra",name);

                    sliderLayout.addSlider(textSliderView);
                }
                sliderLayout.setPresetTransformer(SliderLayout.Transformer.DepthPage);
                sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                sliderLayout.setCustomAnimation(new DescriptionAnimation());
                sliderLayout.setDuration(3000);

                HashMapForURL.clear();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }
    public void AddImageUrlFormLocalRes(){

        HashMapForLocalRes = new HashMap<String, Integer>();

        HashMapForLocalRes.put("product1", R.drawable.demo3);
        HashMapForLocalRes.put("product2", R.drawable.demo3);
        HashMapForLocalRes.put("product3", R.drawable.demo3);

    }
    @Override
    public void onSliderClick(BaseSliderView slider) {

    }
}
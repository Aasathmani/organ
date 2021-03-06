package com.organmanage.thirumalai;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestFragment extends Fragment {


    private ProgressBar progressBar;
    private TextView tv_fname, tv_email, tv_city, tv_country, tv_phone,tv_availDate,tv_bldGrp;

    private Button btn_submit;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    String uid, name, city, phone, country,userType,email, oType;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public TestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TestFragment newInstance(String param1, String param2) {
        TestFragment fragment = new TestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_test, container, false);
        auth = FirebaseAuth.getInstance();
        // User is logged in
        if (auth.getCurrentUser() != null)  user =  auth.getCurrentUser();


        uid = user.getUid();


        tv_fname=(TextView)rootView.findViewById(R.id.name);
        tv_city=(TextView)rootView.findViewById(R.id.city);
        tv_country=(TextView)rootView.findViewById(R.id.country);
        tv_email=(TextView)rootView.findViewById(R.id.email);
        tv_phone=(TextView)rootView.findViewById(R.id.phone);
        btn_submit=(Button) rootView.findViewById(R.id.btn_smbt_recnt_req);
        progressBar=(ProgressBar) rootView.findViewById(R.id.progressBar);
        tv_availDate=(TextView)rootView.findViewById(R.id.dateAvailOn);
        tv_bldGrp=(TextView)rootView.findViewById(R.id.bloodgrp);

        btn_submit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                if(TextUtils.isEmpty(tv_availDate.getText().toString())){

                    Toast.makeText(getActivity(),"Enter organ available date",Toast.LENGTH_LONG).show();
                    return;
                }
                if(TextUtils.isEmpty(tv_bldGrp.getText().toString())){

                    Toast.makeText(getActivity(),"Enter blood group",Toast.LENGTH_LONG).show();
                    return;
                }


                progressBar.setVisibility(View.VISIBLE);



                donar donar= new donar(name,oType,tv_availDate.getText().toString(),false,city,tv_bldGrp.getText().toString());
                FirebaseDatabase.getInstance().getReference("Donars").child(auth.getCurrentUser().getUid()).push().setValue(donar).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);

                        if(task.isSuccessful()){

                            Toast.makeText(getActivity(),"Organ successfully requested",Toast.LENGTH_LONG).show();

                        }
                        else {
                            Toast.makeText(getActivity(),"Something went wrong try again..! please check network connection",Toast.LENGTH_LONG).show();
                        }

                    }
                });

            }
        });

        databaseReference=firebaseDatabase.getInstance().getReference();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("Users").child(uid).child("name").getValue(String.class);
                email = dataSnapshot.child("Users").child(uid).child("email").getValue(String.class);
                city = dataSnapshot.child("Users").child(uid).child("city").getValue(String.class);
                country = dataSnapshot.child("Users").child(uid).child("country").getValue(String.class);
                phone = dataSnapshot.child("Users").child(uid).child("phone").getValue(String.class);
                tv_fname.setText(name);
                tv_city.setText(city);
                tv_country.setText(country);
                tv_phone.setText(phone);
                tv_email.setText(email);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        Spinner dropdown = rootView.findViewById(R.id.spinner);

        // Spinner Drop down elements
        final List<String> organType = new ArrayList<String>();
        organType.add("Kideny");
        organType.add("Lungs");
        organType.add("liver");
        organType.add("heart");
        organType.add("intestines");
        organType.add("Corneal ");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, organType);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        dropdown.setAdapter(dataAdapter);
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                Log.e("data", "onItemSelected from test Frgament: "+(String)adapterView.getItemAtPosition(i));

                oType=(String)adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        return rootView; // inflater.inflate(R.layout.fragment_recepint_request, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

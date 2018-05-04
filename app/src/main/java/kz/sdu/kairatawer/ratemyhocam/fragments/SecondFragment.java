package kz.sdu.kairatawer.ratemyhocam.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.activities.ViewAllActivity;
import kz.sdu.kairatawer.ratemyhocam.adapters.RatingsAdapter;
import kz.sdu.kairatawer.ratemyhocam.adapters.TeachersAdapter;
import kz.sdu.kairatawer.ratemyhocam.models.Rating;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {

    ArrayList<Rating> ratings = new ArrayList<>();
    RatingsAdapter adapter;
    DatabaseReference ratingRef;

    RecyclerView mRatingsList;

    public SecondFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);

        mRatingsList = view.findViewById(R.id.recyclerView_ratings);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        ratingRef = FirebaseDatabase.getInstance().getReference("Rating");

        if(ratings.isEmpty()){
            ratingRef.orderByChild("teacherId").equalTo(getArguments().getString("teacherId"))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                Rating rating = ds.getValue(Rating.class);
                                if(rating.getStatus() == 1) ratings.add(rating);
                                adapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

            adapter = new RatingsAdapter(getActivity(), ratings);
            mRatingsList.setAdapter(adapter);
            mRatingsList.setLayoutManager(new LinearLayoutManager(getContext()));
        }

        super.onActivityCreated(savedInstanceState);
    }
}

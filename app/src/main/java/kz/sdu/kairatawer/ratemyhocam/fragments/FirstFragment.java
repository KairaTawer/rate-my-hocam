package kz.sdu.kairatawer.ratemyhocam.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.models.Rating;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;

/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {

    Teacher teacher;
    String TEACHER_ID = "";
    float OVERALL_RATING = -1;
    int DIFFICULTY = 999999;
    int WOULD_TAKE, WOULD_TAKE_TOTAL = 0;

    DatabaseReference teacherRef, ratingRef;

    View view;
    RatingBar mOverallRating, mDifficultyRating;
    TextView mWouldTakeAgainPercent;

    public FirstFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_first, container, false);

        init();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if(OVERALL_RATING == -1) {
            teacherRef.orderByKey().equalTo(TEACHER_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        teacher = ds.getValue(Teacher.class);
                        teacher.setId(TEACHER_ID);
                        OVERALL_RATING = teacher.getRatingOverall();
                        mOverallRating.setRating(OVERALL_RATING);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if(DIFFICULTY == 999999) {
            ratingRef.orderByChild("teacherId").equalTo(TEACHER_ID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {
                        Rating rating = ds.getValue(Rating.class);
                        rating.setId(ds.getKey());
                        DIFFICULTY += rating.getDifficulty();
                        if(rating.getTakeAgain() == 1) WOULD_TAKE++;
                        WOULD_TAKE_TOTAL++;
                    }
                    if(dataSnapshot.getChildrenCount() != 0) {
                        mDifficultyRating.setRating(DIFFICULTY / (dataSnapshot.getChildrenCount() * 100));
                    }
                    Log.e("Difficulty", "" + mDifficultyRating.getRating());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        if(WOULD_TAKE_TOTAL != 0) mWouldTakeAgainPercent.setText(Math.round((float) WOULD_TAKE / WOULD_TAKE_TOTAL));

        super.onActivityCreated(savedInstanceState);
    }

    private void init() {
        teacherRef = FirebaseDatabase.getInstance().getReference("Teacher");
        ratingRef = FirebaseDatabase.getInstance().getReference("Rating");

        mOverallRating = view.findViewById(R.id.ratingBar_overall);
        mDifficultyRating = view.findViewById(R.id.ratingBar_difficulty);
        mWouldTakeAgainPercent = view.findViewById(R.id.textView_wouldTakePercent);

        TEACHER_ID = getArguments().getString("teacherId");
    }

}

package kz.sdu.kairatawer.ratemyhocam.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.activities.LoginActivity;
import kz.sdu.kairatawer.ratemyhocam.activities.MyRatingsActivity;
import kz.sdu.kairatawer.ratemyhocam.activities.RatingAcceptActivity;
import kz.sdu.kairatawer.ratemyhocam.activities.SavedTeachersActivity;
import kz.sdu.kairatawer.ratemyhocam.models.FacultyUtil;
import kz.sdu.kairatawer.ratemyhocam.models.Users;

public class ProfileFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference ratingRef, userRef;
    Users userUtil;

    Button mAcceptCommentButton;
    Button mMyRatingsButton;
    Button mSavedTeachersButton;
    Button mSignOutButton;
    TextView mUserFaculty;
    TextView mUserGraduateYear;
    TextView mSignInInfo;

    public ProfileFragment() {

    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("Users");
        ratingRef = database.getReference("Rating");

        init(view);

        return view;
    }

    public void init(View view) {

        mAcceptCommentButton = view.findViewById(R.id.button_notAcceptedComments);
        mSavedTeachersButton = view.findViewById(R.id.button_savedTeachers);
        mMyRatingsButton = view.findViewById(R.id.button_myRatings);
        mSignOutButton = view.findViewById(R.id.button_signOut);
        mUserFaculty = view.findViewById(R.id.textView_userFaculty);
        mUserGraduateYear = view.findViewById(R.id.textView_userGraduateYear);
        mSignInInfo = view.findViewById(R.id.textView_signInInfo);

        checkIfAdmin();

        mAcceptCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RatingAcceptActivity.class);
                startActivity(intent);
            }
        });

        mMyRatingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyRatingsActivity.class);
                startActivity(intent);
            }
        });

        mSavedTeachersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SavedTeachersActivity.class);
                startActivity(intent);
            }
        });

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignout();
            }
        });

    }

    public void fillAccountInfo() {
        FacultyUtil facultyUtil = new FacultyUtil();
        mUserFaculty.setText(facultyUtil.facultyList.get(userUtil.getFaculty()));
        mUserGraduateYear.setText(userUtil.getGraduateYear() + "");
        mSignInInfo.setText("You signed in as " + mAuth.getCurrentUser().getEmail());
    }

    public void checkIfAdmin() {

        userRef.orderByKey().equalTo(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    userUtil = ds.getValue(Users.class);
                    if (userUtil.isAdmin()) mAcceptCommentButton.setVisibility(View.VISIBLE);
                    fillAccountInfo();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void startSignout() {
        mAuth.signOut();
        Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(loginIntent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);

    }

}

package kz.sdu.kairatawer.ratemyhocam.activities;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.models.FacultyUtil;
import kz.sdu.kairatawer.ratemyhocam.models.Rating;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;
import kz.sdu.kairatawer.ratemyhocam.models.Users;

public class TeacherProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView_ratings)
    RecyclerView mRatingList;
    @BindView(R.id.textView_teacher_rating)
    TextView mRating;
    @BindView(R.id.ratingBar_rating)
    RatingBar mRatingBar;
    @BindView(R.id.textView_ratingCount)
    TextView mRatingCount;

    Teacher teacher;
    String key;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference teacherRef, ratingRef, savedRef;
    FirebaseRecyclerAdapter adapter;

    Menu mOptionsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(TeacherProfileActivity.this, InitialActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        init();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void init() {

        mRatingList.setLayoutManager(new LinearLayoutManager(this));

        teacherRef = FirebaseDatabase.getInstance().getReference("Teacher");
        ratingRef = FirebaseDatabase.getInstance().getReference("Rating");
        savedRef = FirebaseDatabase.getInstance().getReference("SavedTeacher");

        teacherRef.orderByKey().equalTo(getIntent().getStringExtra("teacherId"))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FacultyUtil facultyUtil = new FacultyUtil();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            teacher = ds.getValue(Teacher.class);
                            getSupportActionBar().setTitle(teacher.getName());
                            getSupportActionBar().setSubtitle(facultyUtil.getFacultyName(teacher.getFacultyId()));
                            mRating.setText(String.valueOf(String.format("%.1f", teacher.getRating())));
                            mRatingBar.setRating(teacher.getRating());
                            mRatingCount.setText(teacher.getRatingCount() + "");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        Query ratingQuery = ratingRef.orderByChild("teacherId").equalTo(getIntent().getStringExtra("teacherId"));

        FirebaseRecyclerOptions<Rating> options =
                new FirebaseRecyclerOptions.Builder<Rating>()
                        .setQuery(ratingQuery, Rating.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Rating, RatingAcceptActivity.RatingViewHolder>(options) {
            @NonNull
            @Override
            public RatingAcceptActivity.RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rating_list_item, parent, false);

                return new RatingAcceptActivity.RatingViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final RatingAcceptActivity.RatingViewHolder holder, int position, @NonNull final Rating model) {
                holder.setRating(String.valueOf(model.getRating()));
                holder.setComment(model.getComment());
                holder.mStatusTextView = holder.itemView.findViewById(R.id.textView_status);
                holder.mStatusTextView.setVisibility(View.GONE);

                teacherRef.orderByKey().equalTo(model.getTeacherId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    teacher = ds.getValue(Teacher.class);
                                    teacher.setId(ds.getKey());
                                    holder.setName(teacher.getName());
                                    holder.setImage(teacher.getImage());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }
        };

        mRatingList.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        adapter.startListening();
        super.onStart();
    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mOptionsMenu = menu;

        menu.add("Rate");
        menu.add("Add to Favourite");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }  else if(item.getTitle().equals("Rate")) {
            Intent rateIntent = new Intent(TeacherProfileActivity.this, RateTeacherActivity.class);
            rateIntent.putExtra("teacherId", teacher.getId());
            startActivity(rateIntent);
        } else if(item.getTitle().equals("Add to Favourite")) {
            key =  savedRef.push().getKey();
            savedRef.child(key).child("userId").setValue(mAuth.getCurrentUser().getUid());
            savedRef.child(key).child("teacherId").setValue(teacher.getId());
            mOptionsMenu.clear();
            mOptionsMenu.add("Rate");
            mOptionsMenu.add("Remove from Favourite");
            super.onCreateOptionsMenu(mOptionsMenu);
        } else {
            Query applesQuery = savedRef.orderByKey().equalTo(key);

            applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("OnCanceled", "onCancelled", databaseError.toException());
                }
            });
            mOptionsMenu.clear();
            mOptionsMenu.add("Rate");
            mOptionsMenu.add("Add to Favourite");
            super.onCreateOptionsMenu(mOptionsMenu);
        }
        return super.onOptionsItemSelected(item);
    }
}

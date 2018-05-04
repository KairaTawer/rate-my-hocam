package kz.sdu.kairatawer.ratemyhocam.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.models.Rating;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;

public class MyRatingsActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView_ratings)
    RecyclerView mRatingList;

    FirebaseRecyclerAdapter adapter;
    DatabaseReference teacherRef, ratingRef;

    FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_ratings);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My ratings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MyRatingsActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        init();
    }

    public void init() {
        mRatingList.setLayoutManager(new LinearLayoutManager(this));

        ratingRef = FirebaseDatabase.getInstance().getReference("Rating");
        teacherRef = FirebaseDatabase.getInstance().getReference("Teacher");

        Query ratingQuery = ratingRef.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());

        FirebaseRecyclerOptions<Rating> options =
                new FirebaseRecyclerOptions.Builder<Rating>()
                        .setQuery(ratingQuery, Rating.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Rating, RatingAcceptActivity.RatingViewHolder>(options) {
            @NonNull
            @Override
            public RatingAcceptActivity.RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.my_rating_list_item, parent, false);

                return new RatingAcceptActivity.RatingViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final RatingAcceptActivity.RatingViewHolder holder, int position, @NonNull final Rating model) {
                holder.setRating(String.valueOf(model.getRating()));
                holder.setComment(model.getComment());
                holder.mStatusTextView = holder.itemView.findViewById(R.id.textView_status);
                int status = model.getStatus();
                if (status == -1) {
                    holder.mStatusTextView.setText("REJECTED");
                    holder.mStatusTextView.setTextColor(Color.RED);
                } else if (status == 0)
                    holder.mStatusTextView.setText("IN PROCESSING");
                else {
                    holder.mStatusTextView.setText("ACCEPTED");
                    holder.mStatusTextView.setTextColor(Color.parseColor("#0C9D58"));
                }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
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
}

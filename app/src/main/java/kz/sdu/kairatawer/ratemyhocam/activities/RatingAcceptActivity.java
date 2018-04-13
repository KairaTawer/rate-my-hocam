package kz.sdu.kairatawer.ratemyhocam.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.models.Rating;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;
import kz.sdu.kairatawer.ratemyhocam.ui.CircleTransform;
import kz.sdu.kairatawer.ratemyhocam.ui.ExpandableTextView;

public class RatingAcceptActivity extends AppCompatActivity {

    @BindView(R.id.recyclerView_ratings)
    RecyclerView mRatingsList;
    @BindView(R.id.toolbar_actionbar)
    Toolbar mToolbar;

    DatabaseReference ratingRef, teacherRef;
    FirebaseRecyclerAdapter adapter;

    Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_accept);

        ButterKnife.bind(this);

        mRatingsList.setLayoutManager(new LinearLayoutManager(this));

        Picasso.get().setLoggingEnabled(true);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Accept Ratings");

        ratingRef = FirebaseDatabase.getInstance().getReference("Rating");
        teacherRef = FirebaseDatabase.getInstance().getReference("Teacher");

        Query ratingQuery = ratingRef.orderByChild("status").equalTo(0);

        FirebaseRecyclerOptions<Rating> options =
                new FirebaseRecyclerOptions.Builder<Rating>()
                        .setQuery(ratingQuery, Rating.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Rating, RatingViewHolder>(options) {
            @NonNull
            @Override
            public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rating_list_item, parent, false);

                return new RatingViewHolder(view);

            }

            @Override
            protected void onBindViewHolder(@NonNull final RatingViewHolder holder, int position, @NonNull final Rating model) {
                holder.setRating(String.valueOf(model.getRating()));
                holder.setComment(model.getComment());

                holder.mRejectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ratingRef.child(model.getId()).child("status").setValue(-1);
                    }
                });

                teacherRef.orderByKey().equalTo(model.getTeacherId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds : dataSnapshot.getChildren()) {
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

                holder.mAcceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ratingRef.child(model.getId()).child("status").setValue(1);

                        teacherRef.child(model.getTeacherId()).child("ratingCount").setValue(teacher.getRatingCount());
                    }
                });
            }
        };

        mRatingsList.setAdapter(adapter);

    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;

        public ImageButton mAcceptButton, mRejectButton;
        public RatingViewHolder(View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.imageView_teacher);
            mAcceptButton = itemView.findViewById(R.id.button_acceptRating);
            mRejectButton = itemView.findViewById(R.id.button_rejectRating);
        }

        public void setRating(String rating) {
            TextView mRating = itemView.findViewById(R.id.textView_teacher_rating);

            mRating.setText(rating);
        }

        public void setName(String name) {
            TextView mRating = itemView.findViewById(R.id.textView_teacher_name);

            mRating.setText(name);
        }

        public void setImage(String url) {
            mImage = itemView.findViewById(R.id.imageView_teacher);

            Picasso.get().load(url).resize(56,56).centerCrop().transform(new CircleTransform()).into(mImage);
        }

        public void setComment(String comment) {
            ExpandableTextView mComment = itemView.findViewById(R.id.textView_comment);

            mComment.setText(comment);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        adapter.stopListening();
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
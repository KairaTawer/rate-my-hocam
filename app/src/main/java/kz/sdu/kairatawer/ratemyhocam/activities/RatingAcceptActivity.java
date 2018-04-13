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
import android.view.View;
import android.view.ViewGroup;
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

        Query ratingQuery = ratingRef.orderByChild("accepted").equalTo(false);

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
            protected void onBindViewHolder(@NonNull final RatingViewHolder holder, int position, @NonNull Rating model) {
                holder.setRating(String.valueOf(model.getRating()));
                holder.setComment(model.getComment());

                teacherRef.orderByKey().equalTo(model.getTeacherId())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Teacher teacher = null;
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
            }
        };
        mRatingsList.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    public static class RatingViewHolder extends RecyclerView.ViewHolder {

        public ImageView mImage;

        public RatingViewHolder(View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.imageView_teacher);
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

}
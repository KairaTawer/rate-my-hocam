package kz.sdu.kairatawer.ratemyhocam.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TimeUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wefika.horizontalpicker.HorizontalPicker;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import kz.sdu.kairatawer.ratemyhocam.R;
import kz.sdu.kairatawer.ratemyhocam.models.Course;
import kz.sdu.kairatawer.ratemyhocam.models.Rating;
import kz.sdu.kairatawer.ratemyhocam.models.Teacher;
import kz.sdu.kairatawer.ratemyhocam.ui.AuthDialog;

public class CreateTeacherActivity extends AppCompatActivity {

    @BindView(R.id.toolbar_actionbar)
    Toolbar mToolbar;
    @BindView(R.id.imageView_teacher)
    CircleImageView mImageTeacher;
    @BindView(R.id.editText_teacher_name)
    EditText mTeacherName;
    @BindView(R.id.picker_faculty)
    HorizontalPicker mFacultyPicker;
    @BindView(R.id.picker_position)
    HorizontalPicker mPositionPicker;
    @BindView(R.id.spinner_courses)
    MaterialSpinner mSpinnerCourses;
    @BindView(R.id.listView_selectedCourses)
    ListView mSelectedCourses;

    DatabaseReference courseRef, teacherRef, teacherCourseRef;
    StorageReference storageRef;

    String imageUrl = "";
    ArrayList<Course> coursesList = new ArrayList<>();
    ArrayList<String> courseNames = new ArrayList<>();
    ArrayList<String> selectedCourses = new ArrayList<>();
    public static final int PICK_IMAGE_REQUEST = 1;
    private Uri filePath;

    ArrayAdapter<String> arrayAdapter;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_teacher);

        ButterKnife.bind(this);

        init();
        getCourses();
        inflateCourseSpinner();
    }

    private void init() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Добавить учителя");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        courseRef = FirebaseDatabase.getInstance().getReference("/Courses");
        teacherRef = FirebaseDatabase.getInstance().getReference("/Teacher");
        teacherCourseRef = FirebaseDatabase.getInstance().getReference("/TeacherCourse");
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://ratemyhocam-b5554.appspot.com/");

        mImageTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                selectedCourses);

        mSelectedCourses.setAdapter(arrayAdapter);
    }

    private void addCourse(int position) {
        selectedCourses.add(coursesList.get(position).getName());

        arrayAdapter.notifyDataSetChanged();
    }

    private void inflateCourseSpinner() {
        mSpinnerCourses.setItems(courseNames);
        mSpinnerCourses.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                addCourse(position);
                Log.e("courseAdded", arrayAdapter.getCount() + "");
                Log.e("Added item", arrayAdapter.getItem(arrayAdapter.getCount() - 1).toString());
            }
        });
    }

    private void getCourses() {
        courseRef.orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Course course = ds.getValue(Course.class);
                    course.setId(ds.getKey());
                    coursesList.add(course);
                    courseNames.add(course.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showErrorDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateTeacherActivity.this);
        builder.setTitle("Ошибка")
                .setMessage(error)
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void uploadImage() {
        StorageReference mountainImagesRef = storageRef.child("images/" + UUID.randomUUID() + ".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                imageUrl = downloadUrl + "";
                Log.e("downloadUrl -> ", imageUrl);
                addTeacherToDatabase();
            }
        });
    }

    private void addTeacherToDatabase() {
        String key = teacherRef.push().getKey();

        Teacher teacherRecord = new Teacher(mTeacherName.getText().toString(),
                mPositionPicker.getValues()[mPositionPicker.getSelectedItem()].toString(),
                imageUrl,
                mFacultyPicker.getSelectedItem(),
                0, 0, 0);

        teacherRef.child(key).setValue(teacherRecord);

        String sKey = teacherCourseRef.push().getKey();

        for (String selectedCourse : selectedCourses) {
            teacherCourseRef.child(sKey).child("teacherId").setValue(key);
            for (Course course : coursesList) {
                if (selectedCourse.equals(course.getName())) {
                    teacherCourseRef.child(sKey).child("courseId").setValue(course.getId());
                }
            }
        }

        Intent intent = new Intent(CreateTeacherActivity.this, ViewAllActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, Menu.NONE, "Ok")
                .setIcon(R.drawable.ic_check_black_24dp)
                .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(CreateTeacherActivity.this, ViewAllActivity.class);
                startActivity(intent);
            case 1:
                if (mTeacherName.getText().toString().equals("") || mTeacherName.getText().toString().isEmpty()) {
                    showErrorDialog("Поле имя не заполнено");
                    break;
                }
                if (selectedCourses.isEmpty()) {
                    showErrorDialog("Курсы не выбраны");
                    break;
                }
                if (bitmap != null) uploadImage();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mImageTeacher.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

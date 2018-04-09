package kz.sdu.kairatawer.ratemyhocam.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import kz.sdu.kairatawer.ratemyhocam.R;

public class TeacherProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        Log.e("teacherId",getIntent().getStringExtra("teacherId"));
    }
}

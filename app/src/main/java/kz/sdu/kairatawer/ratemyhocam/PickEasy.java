package kz.sdu.kairatawer.ratemyhocam;

import android.app.Application;

import kz.sdu.kairatawer.ratemyhocam.ui.TypefaceUtil;

/**
 * Created by асус on 11.02.2018.
 */

public class PickEasy extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/OpenSans-Regular.ttf");
    }
}

package kz.sdu.kairatawer.ratemyhocam.fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;

import java.util.List;

import kz.sdu.kairatawer.ratemyhocam.R;

public class AboutUsFragment extends Fragment {

    public static final String APP_URL = "http://bit.ly/get-zheti-soz-android";

    private OnFragmentInteractionListener listener;

    public static AboutUsFragment newInstance() {
        return new AboutUsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_us, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TableRow linkRate = (TableRow) view.findViewById(R.id.link_rate);
        TableRow linkEmail = (TableRow) view.findViewById(R.id.link_email);
        TableRow linkInstagram = (TableRow) view.findViewById(R.id.link_instagram);
        TableRow linkFacebook = (TableRow) view.findViewById(R.id.link_facebook);

        linkFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "Rate Professors in PickEasy app \n"
                        + APP_URL);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

//        linkInstagram.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
//                shareIntent.setType("image/*");
//                shareIntent
//                        .putExtra(
//                                Intent.EXTRA_STREAM,
//                                Uri.parse("android.resource://com.zerotoone.n17r.zhetisoz/drawable/commercial/"));
//                shareIntent.setPackage("com.instagram.android");
//                try {
//                    startActivity(shareIntent);
//                } catch (ActivityNotFoundException e) {
//                    Snackbar.make(view,"Instagram тіркелмеген",Snackbar.LENGTH_LONG).show();
//                }
//            }
//        });

        linkEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/html");
                final PackageManager pm = getContext().getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
                String className = null;
                for (final ResolveInfo info : matches) {
                    if (info.activityInfo.packageName.equals("com.google.android.gm")) {
                        className = info.activityInfo.name;

                        if(className != null && !className.isEmpty()){
                            break;
                        }
                    }
                }
                emailIntent.setClassName("com.google.android.gm", className);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"kairatawer@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Easy Pick");
                startActivity(emailIntent);
            }
        });

//        linkRate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.zerotoone.n17r.zhetisoz");
//                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
//                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
//                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
//                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//                try {
//                    startActivity(goToMarket);
//                } catch (ActivityNotFoundException e) {
//                    startActivity(new Intent(Intent.ACTION_VIEW,
//                            Uri.parse(APP_URL)));
//                }
//            }
//        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFragmentInteractionListener {
    }
}
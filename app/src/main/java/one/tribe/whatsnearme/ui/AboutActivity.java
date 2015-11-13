package one.tribe.whatsnearme.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import one.tribe.whatsnearme.BuildConfig;
import one.tribe.whatsnearme.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView version = (TextView) findViewById(R.id.aboutAppVersionTxt);
        version.setText("Version " + BuildConfig.VERSION_NAME);
    }
}

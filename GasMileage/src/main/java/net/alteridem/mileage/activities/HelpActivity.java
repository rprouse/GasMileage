package net.alteridem.mileage.activities;

import android.app.Activity;
import android.widget.TextView;

import net.alteridem.mileage.R;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FromHtml;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_help)
public class HelpActivity extends Activity {

    @ViewById(R.id.help_text)
    @FromHtml(R.string.help_text)
    TextView textView;

    @OptionsItem
    void homeSelected() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
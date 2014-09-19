package net.alteridem.mileage;

import android.app.Activity;
import android.widget.TextView;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FromHtml;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Robert Prouse on 07/09/13.
 */
@EActivity(R.layout.activity_help)
public class HelpActivity extends Activity {

    @ViewById(R.id.help_text)
    @FromHtml(R.string.help_text)
    TextView textView;
}
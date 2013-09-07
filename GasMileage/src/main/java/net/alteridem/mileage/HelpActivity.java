package net.alteridem.mileage;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by Robert Prouse on 07/09/13.
 */
public class HelpActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        TextView textView = (TextView)findViewById(R.id.help_text);
        CharSequence help = getText(R.string.help_text);
        textView.setText(Html.fromHtml(help.toString()));
    }
}
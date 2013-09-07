package net.alteridem.mileage.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import net.alteridem.mileage.MileageApplication;
import net.alteridem.mileage.R;
import net.alteridem.mileage.data.Entry;

import java.text.Format;
import java.util.Date;
import java.util.List;

/**
 * Created by Robert Prouse on 07/09/13.
 */
public class EntriesAdapter extends BaseAdapter implements ListAdapter {

    private List<Entry> _entries;
    private Context _context;
    private LayoutInflater _inflater;

    public EntriesAdapter( Context context, List<Entry> entries ){
        _context = context;
        _entries = entries;
        _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return _entries.size();
    }

    @Override
    public Object getItem(int i) {
        if ( i < _entries.size() ){
            return _entries.get(i);
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        if ( i < _entries.size() ){
            return _entries.get(i).getId();
        }
        return -1;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if ( view == null ) {
            view = _inflater.inflate(R.layout.entry, viewGroup, false);
        }
        Entry entry = (Entry)getItem(i);
        if ( view != null && entry != null) {
            TextView date = (TextView) view.findViewById(R.id.entry_date);
            TextView kilometers = (TextView) view.findViewById(R.id.entry_kilometers);
            TextView liters = (TextView) view.findViewById(R.id.entry_liters);
            TextView mileage = (TextView) view.findViewById(R.id.entry_mileage);

            // Use user pref date format
            Date d = entry.getFillup_date();
            Format df = DateFormat.getDateFormat(MileageApplication.getApplication());
            String dateStr = df.format(d);
            date.setText(dateStr);
            kilometers.setText(entry.getDistanceString());
            liters.setText(entry.getVolumeString());
            mileage.setText(entry.getMileageString());
        }
        return view;
    }
}

package net.alteridem.mileage.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import net.alteridem.mileage.utilities.Convert;
import net.alteridem.mileage.R;
import net.alteridem.mileage.data.Entry;
import net.alteridem.mileage.fragments.EntriesFragment;

import java.text.Format;
import java.util.List;

/**
 * Loads Entries into a ListView
 */
public class EntriesAdapter extends BaseAdapter implements ListAdapter {

    private List<Entry> _entries;
    private Context _context;
    private EntriesFragment _fragment;
    private LayoutInflater _inflater;
    private Convert _convert;

    public EntriesAdapter( EntriesFragment fragment, List<Entry> entries, Convert convert ){
        _fragment = fragment;
        _context = _fragment.getActivity();
        _entries = entries;
        _inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        _convert = convert;
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
            View color = view.findViewById(R.id.entry_color);
            TextView date = (TextView) view.findViewById(R.id.entry_date);
            TextView kilometers = (TextView) view.findViewById(R.id.entry_kilometers);
            TextView liters = (TextView) view.findViewById(R.id.entry_liters);
            TextView mileage = (TextView) view.findViewById(R.id.entry_mileage);

            Format df = DateFormat.getDateFormat(_context);
            date.setText(entry.getFillupDateString(df));
            kilometers.setText(entry.getDistanceString(_convert));
            liters.setText(entry.getVolumeString(_convert));
            mileage.setText(entry.getMileageString(_convert));
            color.setBackgroundColor(entry.getColor());

            // Issue #27: Add notes to entries
            ImageView note_icon = (ImageView) view.findViewById(R.id.entry_note_icon);
            int visibility = entry.hasNote() ? View.VISIBLE : View.INVISIBLE;
            note_icon.setVisibility(visibility);
            note_icon.setTag( entry );
            note_icon.setClickable( true );
            note_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Entry entry = (Entry) view.getTag();
                    if ( entry != null ) {
                        Format df = DateFormat.getDateFormat(_context);
                        new AlertDialog.Builder(_context)
                                .setTitle(entry.getFillupDateString(df))
                                .setMessage(entry.getNote())
                                .setPositiveButton(R.string.ok, null)
                                .show();
                    }
                }
            });
            note_icon.setLongClickable( true );
            note_icon.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    _fragment.getActivity().openContextMenu(view);
                    return true;
                }
            });
        }
        return view;
    }
}

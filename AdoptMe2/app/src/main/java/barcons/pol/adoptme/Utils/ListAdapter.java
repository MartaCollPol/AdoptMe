package barcons.pol.adoptme.Utils;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

import barcons.pol.adoptme.R;

/**
 * Created by alsina on 18/12/2017.
 */

public class ListAdapter extends ArrayAdapter<String> {
    public ListAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;
        if (result == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.anunci, null);
        }
        //  CheckBox checkbox = (CheckBox) result.findViewById(R.id.chk_anunci);
        //  String item_text = getItem(position);
        return result;

        //  }
    }
}


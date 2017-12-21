package barcons.pol.adoptme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

/**
 * Created by sense on 21/12/2017.
 */

public class ListAdapter extends ArrayAdapter<String> {

    public ListAdapter(@NonNull Context context, int resource, List objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;
        if(result == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.anunci, null);
        }

        CheckBox chk = (CheckBox) result.findViewById(R.id.chk_anunci);
        String item_txt = getItem(position);
        chk.setText(item_txt);
        return result;
    }
}

package barcons.pol.adoptme;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by sense on 21/12/2017.
 */

public class ListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] imatgeid;
    private final String[] dist;

    public ListAdapter(@NonNull Activity context, String[] imatgeid, String[] dist) {
        super(context,R.layout.anunci, imatgeid);
        this.context = context;
        this.dist = dist;
        this.imatgeid = imatgeid;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View result = convertView;
        if(result == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.anunci, null);
        }

        CheckBox chk = (CheckBox) result.findViewById(R.id.chk_anunci);
        TextView txt = (TextView) result.findViewById(R.id.txt_anunci);
        ImageView img = (ImageView) result.findViewById(R.id.img_anunci);
        txt.setText(dist[position]);
        Picasso.with(context).load(imatgeid[position]).into(img);
        return result;
    }
}


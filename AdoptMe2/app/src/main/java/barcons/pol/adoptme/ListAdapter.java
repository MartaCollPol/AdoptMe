package barcons.pol.adoptme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
        Button btn = (Button) result.findViewById(R.id.btn_info);
        txt.setText(dist[position]);
        Picasso.with(context).load(imatgeid[position]).into(img);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showinfo(v);
            }
        });

        return result;
    }
    public void showinfo(View view){ //anar al layout i assignar aquest metode a un botó per a iniciar la infoactivity
        Context ctx = getContext();
        Intent intent = new Intent(ctx, InfoActivity.class);
        String adid= "-L0jyixqafS4T9GeOO8W"; //id de l'anunci "query de key de l'anunci clicat"
        intent.putExtra("ad",adid);
        ctx.startActivity(intent);
    }
}





package barcons.pol.adoptme.Utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import barcons.pol.adoptme.InfoActivity;
import barcons.pol.adoptme.R;

/**
 * Created by sense on 21/12/2017.
 */

public class ListAdapter extends ArrayAdapter<String> {
    private final Activity mContext;
    private final ArrayList<String> mImatgeid;
    private final ArrayList<String> mDist;
    private final String mDeviceid;

    StorageReference StorageRef = FirebaseStorage.getInstance().getReference();
    StorageReference ImgRef;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersRef = database.getReference(FirebaseReferences.usersRef);

    ImageView img;



    public ListAdapter(@NonNull Activity context, ArrayList<String> imatgeid, ArrayList<String> dist,String mDeviceid) {
        super(context, R.layout.anunci, imatgeid);
        this.mContext = context;
        this.mDist = dist;
        this.mImatgeid = imatgeid;
        this.mDeviceid = mDeviceid;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View result = convertView;
        if(result == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            result = inflater.inflate(R.layout.anunci, null);
        }

        final CheckBox chk = (CheckBox) result.findViewById(R.id.chk_anunci);
        TextView txt = (TextView) result.findViewById(R.id.txt_anunci);
        img = (ImageView) result.findViewById(R.id.img_anunci);
        Button btn = (Button) result.findViewById(R.id.btn_info);

        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetUserId SaveAd = new GetUserId(mContext,mDeviceid,chk,mImatgeid.get(position));
                SaveAd.GetUser(1);
            }
        });


        txt.setText(mDist.get(position));
        ImgRef = StorageRef.child(mImatgeid.get(position));
        ImgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                new DownloadImage().execute(uri);
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showinfo(v,position);
            }
        });

        return result;
    }



    private void showinfo(View view,int position){
        Intent intent = new Intent(mContext, InfoActivity.class);
        String adid= mImatgeid.get(position); //id de l'anunci "query de key de l'anunci clicat"
        intent.putExtra("ad",adid);
        mContext.startActivity(intent);
    }

    //Asynctask per descarregar la imatge des de l'url
    private class DownloadImage extends AsyncTask<Uri, Void, String> {
        private static final String TAG = "DownloadImageMain";
        @Override
        protected String doInBackground(Uri... params) {
            return params[0].toString();

        }

        @Override
        protected void onPostExecute(String result){
            if(result!=null){
                Picasso.with(mContext).load(result).into(img);
            }
            else Log.i(TAG,"Could not set the image");
        }
    }

}





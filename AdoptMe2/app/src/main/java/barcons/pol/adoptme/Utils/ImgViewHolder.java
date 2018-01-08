package barcons.pol.adoptme.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import barcons.pol.adoptme.R;

/**
 * Created by Marta on 08/01/2018.
 */

public class ImgViewHolder extends RecyclerView.ViewHolder {
    public TextView nameView;
    public ImageView imageView;
    public CheckBox saveCheck;
    public Button btn_info;
    public Button btn_edit;
    public Button btn_delete;

    public ImgViewHolder(View itemView) {
        super(itemView);

        nameView = (TextView) itemView.findViewById(R.id.txt_anunci);
        imageView = (ImageView) itemView.findViewById(R.id.img_anunci);
        saveCheck =(CheckBox)itemView.findViewById(R.id.chk_anunci);
        btn_info = (Button) itemView.findViewById(R.id.btn_info);
        btn_edit = (Button) itemView.findViewById(R.id.btn_editar);
        btn_delete = (Button) itemView.findViewById(R.id.btn_borrar);
    }
}

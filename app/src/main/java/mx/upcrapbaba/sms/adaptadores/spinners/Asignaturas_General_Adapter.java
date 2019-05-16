package mx.upcrapbaba.sms.adaptadores.spinners;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.models.Asignatura;

public class Asignaturas_General_Adapter extends ArrayAdapter {

    private Context mContext;
    private List<Asignatura> dataSet;

    public Asignaturas_General_Adapter(Context context, int resource, List<Asignatura> data) {
        super(context, resource);
        this.mContext = context;
        this.dataSet = data;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        Asignatura asignatura_actual = dataSet.get(position);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.asignatura_item, parent, false);
            mViewHolder.txtNombre_Materia = convertView.findViewById(R.id.txtNombre_Asignatura);
            mViewHolder.imgImagen_Materia = convertView.findViewById(R.id.imgAlumno_Photo);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }


        mViewHolder.txtNombre_Materia.setText(asignatura_actual.getNombre_materia());
        if (asignatura_actual.getImagen_materia().isEmpty() || asignatura_actual.getImagen_materia() == null) {
            Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(mContext.getResources().getDrawable(R.drawable.materia_holder)).into(mViewHolder.imgImagen_Materia);
        } else {
            String url = new ApiWeb().getBASE_URL_GLITCH() + "/" + asignatura_actual.getImagen_materia();
            Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(mViewHolder.imgImagen_Materia);

        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        TextView txtNombre_Materia;
        ImageView imgImagen_Materia;
    }

}

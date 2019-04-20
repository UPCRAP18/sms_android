package mx.upcrapbaba.sms.adaptadores;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.models.Alumno;

public class Alumnos_Adapter implements ListAdapter {
    private ArrayList<Alumno> dataSet;
    private Context mContext;

    public Alumnos_Adapter(ArrayList<Alumno> dataSet, Context mContext) {
        this.dataSet = dataSet;
        this.mContext = mContext;
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Alumno alumno_actual = dataSet.get(position);

        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.alumnos_list_content, null);
            convertView.setOnClickListener(v -> {
                Toasty.success(mContext, "Se ha clickeado en el alumno " + alumno_actual.getNombre_alumno()).show();

            });

            TextView txtMatricula = convertView.findViewById(R.id.txtMatricula_Alumno);
            TextView txtNombre = convertView.findViewById(R.id.txtNombre_Alumno);
            TextView txtApellidos = convertView.findViewById(R.id.txtApellidos_Alumno);
            ImageView imgAlumno_Photo = convertView.findViewById(R.id.imgAlumno_Photo);
            ImageButton imgbEdit = convertView.findViewById(R.id.imgbEdit_Alumno);

            txtMatricula.setText(alumno_actual.getMatricula_alumno());
            txtNombre.setText(alumno_actual.getNombre_alumno());
            txtApellidos.setText(alumno_actual.getApellidos_alumno());

            imgbEdit.setOnClickListener(v -> {
                Toasty.success(mContext, "Se ha clickeado en el alumno " + alumno_actual.getNombre_alumno()).show();
            });

        }
        return convertView;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return dataSet.size();
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}

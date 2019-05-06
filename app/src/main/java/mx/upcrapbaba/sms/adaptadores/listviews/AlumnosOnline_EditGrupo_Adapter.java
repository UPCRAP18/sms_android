package mx.upcrapbaba.sms.adaptadores.listviews;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;

import java.util.List;

import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.models.Alumno;

public class AlumnosOnline_EditGrupo_Adapter implements ListAdapter {

    private Context mContext;
    private List<Alumno> dataSet;
    private AlumnosOnline_EditGrupo_Adapter.AlumnoOnlineListener setOnAlumnoOnlineListener;

    public AlumnosOnline_EditGrupo_Adapter(Context mContext, List<Alumno> dataSet, AlumnoOnlineListener alumnoListener) {
        this.mContext = mContext;
        this.dataSet = dataSet;
        this.setOnAlumnoOnlineListener = alumnoListener;
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
            convertView = layoutInflater.inflate(R.layout.alumnos_list_grupo_content, null);

            CheckBox cboAlumno = convertView.findViewById(R.id.cboAlumno);

            String alumno = String.format("%s - %s %s", alumno_actual.getMatricula_alumno(), alumno_actual.getNombre_alumno(), alumno_actual.getApellidos());
            cboAlumno.setText(alumno);


            cboAlumno.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    setOnAlumnoOnlineListener.OnAlumnoOnlineSelected(alumno_actual);
                } else {
                    setOnAlumnoOnlineListener.OnAlumnoOnlineDeselected(alumno_actual);
                }
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

    public interface AlumnoOnlineListener {
        void OnAlumnoOnlineSelected(Alumno alumno_seleccionado);

        void OnAlumnoOnlineDeselected(Alumno alumno_seleccionado);
    }


}

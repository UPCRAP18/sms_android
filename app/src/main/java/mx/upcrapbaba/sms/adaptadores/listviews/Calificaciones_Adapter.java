package mx.upcrapbaba.sms.adaptadores.listviews;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.models.Calificacion;

public class Calificaciones_Adapter implements ListAdapter {
    private Context mContext;
    private List<Calificacion> dataSet;

    public Calificaciones_Adapter(Context mContext, List<Calificacion> dataSet) {
        this.mContext = mContext;
        this.dataSet = dataSet;
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
        Calificacion calificacion_actual = dataSet.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.calificaciones_content, null);
            TextView lblNombre_Actividad = convertView.findViewById(R.id.lblNombre_Actividad);
            Spinner spCalificacion = convertView.findViewById(R.id.spCalificacion);

            lblNombre_Actividad.setText(calificacion_actual.getNombre_actividad());

            String[] calificaciones = mContext.getResources().getStringArray(R.array.calificaciones);

            for (int i = 0; i < calificaciones.length; i++) {
                if (calificacion_actual.getObtenido().equals(calificaciones[i])) {
                    spCalificacion.setSelection(i);
                    break;
                } else {
                    spCalificacion.setSelection(0);
                }
            }

            spCalificacion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    calificacion_actual.setObtenido(calificaciones[position]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

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

    public List<Calificacion> getDataSet() {
        return dataSet;
    }

}

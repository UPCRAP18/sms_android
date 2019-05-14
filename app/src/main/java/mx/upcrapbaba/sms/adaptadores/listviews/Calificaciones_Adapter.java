package mx.upcrapbaba.sms.adaptadores.listviews;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.models.Calificacion;

public class Calificaciones_Adapter implements ListAdapter {
    private Context mContext;
    private List<Calificacion> dataSet;
    private Calificaciones_Adapter.ItemSelected setOnDataChanged;

    public Calificaciones_Adapter(Context mContext, List<Calificacion> dataSet, Calificaciones_Adapter.ItemSelected setOnDataChanged) {
        this.mContext = mContext;
        this.dataSet = dataSet;
        this.setOnDataChanged = setOnDataChanged;
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
            EditText etCalificacion = convertView.findViewById(R.id.etCalificacion);

            lblNombre_Actividad.setText(calificacion_actual.getNombre_actividad());
            String[] calificaciones_array = new Gson().fromJson(calificacion_actual.getCalificacion_obtenida(), new TypeToken<String[]>() {
            }.getType());

            List<String> calificaciones_refinada = new LinkedList<>(Arrays.asList(calificaciones_array));

            if (!calificaciones_refinada.isEmpty()) {
                etCalificacion.setText(calificaciones_refinada.toString());
            }

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

    public interface ItemSelected {
        void onDataChanged(Calificacion calificacion);
    }
}

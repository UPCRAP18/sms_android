package mx.upcrapbaba.sms.adaptadores;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;

import java.util.List;

import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.models.Asignatura;

public class Asignaturas_Adapter implements ListAdapter {

    private Context mContext;
    private List<Asignatura> dataSet;
    private Asignaturas_Adapter.AsignaturaListener setOnItemAsignaturaSelected;


    public Asignaturas_Adapter(Context mContext, List<Asignatura> dataSet, AsignaturaListener setOnItemAsignaturaSelected) {
        this.mContext = mContext;
        this.dataSet = dataSet;
        this.setOnItemAsignaturaSelected = setOnItemAsignaturaSelected;
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

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Asignatura asignatura_actual = dataSet.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.asignaturas_list_content, null);

            CheckBox cboAsignatura = convertView.findViewById(R.id.cboAsignatura);

            cboAsignatura.setText(String.format("%s - %s", asignatura_actual.getCodigo_materia(), asignatura_actual.getNombre_materia()));
            cboAsignatura.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    setOnItemAsignaturaSelected.OnAsignaturaSelected(asignatura_actual);
                } else {
                    setOnItemAsignaturaSelected.OnAsignaturaDeselected(asignatura_actual);
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

    public interface AsignaturaListener {
        void OnAsignaturaSelected(Asignatura asignatura_seleccionado);

        void OnAsignaturaDeselected(Asignatura asignatura_seleccionado);
    }


}

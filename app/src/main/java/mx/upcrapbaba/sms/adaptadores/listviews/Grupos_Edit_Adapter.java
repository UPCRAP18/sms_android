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
import mx.upcrapbaba.sms.models.Grupo;

public class Grupos_Edit_Adapter implements ListAdapter {

    Context mContext;
    List<Grupo> dataSet;
    List<String> nombres_asignatura;
    Grupos_Edit_Adapter.GroupListener setOnItemGroupSelected;


    public Grupos_Edit_Adapter(Context mContext, List<Grupo> dataSet, List<String> nombres_asignatura, GroupListener setOnItemGroupSelected) {
        this.mContext = mContext;
        this.dataSet = dataSet;
        this.nombres_asignatura = nombres_asignatura;
        this.setOnItemGroupSelected = setOnItemGroupSelected;
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
        Grupo grupo_actual = dataSet.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.grupos_list_content, null);

            CheckBox cboGrupo = convertView.findViewById(R.id.cboGrupo);

            cboGrupo.setText(String.format("%s - %s", grupo_actual.getNombre_grupo(), nombres_asignatura.get(position)));
            cboGrupo.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    setOnItemGroupSelected.OnItemGroupSelected(grupo_actual, nombres_asignatura.get(position));
                } else {
                    setOnItemGroupSelected.OnItemGroupDeselected(grupo_actual, nombres_asignatura.get(position));
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

    public interface GroupListener {
        void OnItemGroupSelected(Grupo grupo_seleccionado, String grupo_asignatura);

        void OnItemGroupDeselected(Grupo grupo_seleccionado, String grupo_asignatura);
    }

}

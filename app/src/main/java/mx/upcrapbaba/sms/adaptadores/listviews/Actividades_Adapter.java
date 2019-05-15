package mx.upcrapbaba.sms.adaptadores.listviews;

import android.content.Context;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;

import java.util.List;

import es.dmoral.toasty.Toasty;
import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.models.Actividad;

public class Actividades_Adapter implements ListAdapter {
    private Context mContext;
    private List<Actividad> dataSet;
    private Actividades_Adapter.ItemSelected setOnItemSelected;


    public Actividades_Adapter(Context mContext, List<Actividad> dataSet, ItemSelected setOnItemSelected) {
        this.mContext = mContext;
        this.dataSet = dataSet;
        this.setOnItemSelected = setOnItemSelected;
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
        Actividad actividad_actual = dataSet.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.actividades_list_content, null);
            Button btnActividad = convertView.findViewById(R.id.btnActividad);
            LinearLayout layActividad = convertView.findViewById(R.id.layActivity_Info);
            EditText etNombre_Actividad = convertView.findViewById(R.id.etNombre_Actividad);
            EditText etValor_Actividad = convertView.findViewById(R.id.etValor_Actividad);
            Spinner spParcial = convertView.findViewById(R.id.spParcial);
            ImageButton deleteActividad = convertView.findViewById(R.id.imgbDelete);

            btnActividad.setText(mContext.getResources().getString(R.string.btnActividad, actividad_actual.getNombre_actividad()));
            etNombre_Actividad.setText(actividad_actual.getNombre_actividad());
            etValor_Actividad.setText(actividad_actual.getValor_actividad());

            btnActividad.setOnClickListener(v -> {
                if (layActividad.getVisibility() == View.VISIBLE) {
                    layActividad.setVisibility(View.GONE);
                } else {
                    layActividad.setVisibility(View.VISIBLE);
                }
            });

            switch (actividad_actual.getParcial()) {
                case "Primer Parcial":
                    spParcial.setSelection(0);
                    break;
                case "Segundo Parcial":
                    spParcial.setSelection(1);
                    break;
                case "Tercer Parcial":
                    spParcial.setSelection(2);
                    break;
            }

            spParcial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            actividad_actual.setParcial("Primer Parcial");
                            break;
                        case 1:
                            actividad_actual.setParcial("Segundo Parcial");
                            break;
                        case 2:
                            actividad_actual.setParcial("Tercer Parcial");
                            break;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            etNombre_Actividad.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().trim().isEmpty()) {
                        actividad_actual.setNombre_actividad(s.toString().trim());
                        btnActividad.setText(mContext.getResources().getString(R.string.btnActividad, s.toString()));
                    } else {
                        Toasty.warning(mContext, "Por favor, no deje este campo vacio").show();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            etValor_Actividad.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().trim().isEmpty()) {
                        actividad_actual.setValor_actividad(s.toString().trim());
                    } else {
                        Toasty.warning(mContext, "Por favor, no deje este campo vacio").show();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            deleteActividad.setOnClickListener(v -> setOnItemSelected.onDeleteActividad(actividad_actual));

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

    public List<Actividad> getData() {
        return dataSet;
    }

    public interface ItemSelected {
        void onDeleteActividad(Actividad actividad_eliminada);
    }
}

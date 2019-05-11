package mx.upcrapbaba.sms.adaptadores.listviews;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.LinkedList;
import java.util.List;

import mx.upcrapbaba.sms.R;
import mx.upcrapbaba.sms.api.ApiWeb;
import mx.upcrapbaba.sms.models.Alumno;

public class Alumnos_GeneralList_Adapter implements ListAdapter {
    private List<Alumno> dataSet;
    private Context mContext;
    private FilterStudentsAdapterListener studentListener;

    public Alumnos_GeneralList_Adapter(List<Alumno> dataSet, Context mContext, FilterStudentsAdapterListener student_Listener) {
        this.dataSet = dataSet;
        this.mContext = mContext;
        this.studentListener = student_Listener;

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
            convertView.setOnClickListener(v -> studentListener.onStudentSelected(alumno_actual));

            TextView txtMatricula = convertView.findViewById(R.id.txtMatricula_Alumno);
            TextView txtNombre = convertView.findViewById(R.id.txtNombre_Alumno);
            TextView txtApellidos = convertView.findViewById(R.id.txtApellidos_Alumno);
            TextView txtNoLista = convertView.findViewById(R.id.txtNo_Lista);
            ImageView imgAlumno_Photo = convertView.findViewById(R.id.imgAlumno_Photo);

            String url = new ApiWeb().getBASE_URL_GLITCH() + "/" + alumno_actual.getImagen_alumno();
            Glide.with(mContext).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(url).into(imgAlumno_Photo);

            txtMatricula.setText(alumno_actual.getMatricula_alumno());
            txtNombre.setText(alumno_actual.getNombre_alumno());
            txtApellidos.setText(alumno_actual.getApellidos());
            txtNoLista.setText(String.valueOf(position + 1));

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

    public List<Alumno> getfilterData(String charText) {
        //charText = charText.toLowerCase(Locale.getDefault());
        List<Alumno> filterResults = new LinkedList<>();
        if (charText.length() == 0) {
            filterResults.addAll(dataSet);
        } else {
            for (Alumno alumno : dataSet) {
                if (alumno.getNombre_alumno().toLowerCase().contains(charText)
                        || alumno.getMatricula_alumno().contains(charText)) {
                    filterResults.add(alumno);
                }
            }
        }

        return filterResults;
    }

    public interface FilterStudentsAdapterListener {
        void onStudentSelected(Alumno alumno_seleccionado);
    }

}

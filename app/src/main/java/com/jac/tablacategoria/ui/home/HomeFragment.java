package com.jac.tablacategoria.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jac.tablacategoria.MySingleton;
import com.jac.tablacategoria.R;
import com.jac.tablacategoria.Setting_VAR;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

//this class is a Entity for Table categoria
public class HomeFragment extends Fragment implements View.OnClickListener {

    private Button btnNew;
    private Button cancel, btnSave;
    private Spinner spEstado;
    private EditText edtId, edtNombre;
    private HomeViewModel homeViewModel;

    String datoSelect = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final ConstraintLayout frameLayout1 = root.findViewById(R.id.constraintLayout);
        final TextView frameLayout2 = root.findViewById(R.id.fm2);
        btnNew = root.findViewById(R.id.btnNuevo);
        edtId = root.findViewById(R.id.edtCategoria);
        edtNombre = root.findViewById(R.id.edtNombreCategoria);
        cancel = root.findViewById(R.id.btnCancel);
        spEstado = root.findViewById(R.id.sp_estado);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.estadoCategorias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spEstado.setAdapter(adapter);

        spEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spEstado.getSelectedItemPosition()>0){
                    datoSelect = spEstado.getSelectedItem().toString();
                } else {
                    datoSelect = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnNew.isClickable() == true) {
                    frameLayout1.setVisibility(view.VISIBLE);
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cancel.isClickable() == true){
                    frameLayout1.setVisibility(view.GONE);
                }
            }
        });

        btnSave = root.findViewById(R.id.btnGuardarCate);
        btnSave.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnGuardarCate:
                String id = edtId.getText().toString();
                String nombre = edtNombre.getText().toString();
                if (id.length() == 0){
                    edtId.setError("Por favor introduzca el Id");
                } else if (nombre.length() == 0){
                    edtNombre.setError("Por favor escriba el nombre de la categoria");
                } else if (spEstado.getSelectedItemPosition() > 0){
                    //this action save in the BD
                    save_server(getContext(), Integer.parseInt(id), nombre, Integer.parseInt(datoSelect));
                } else {
                    Toast.makeText(getContext(), "Seleccione un estado para la categoria", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //metodo encargado de gestionar la comunicacion con el servidor y guardar en la base de datos MySql
    private void save_server(final Context context, final int idCat, final String nombreCat, final int estadoCat){
        StringRequest request = new StringRequest(Request.Method.POST, Setting_VAR.URL_GUARDAR_CATEGORIAS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject requestJSON = null;
                try {
                    requestJSON = new JSONObject(response.toString());
                    String estado = requestJSON.getString("estado");
                    String mensaje = requestJSON.getString("mensaje");
                    if (estado.equals("1")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();

                    } else if (estado.equals("2")){
                        Toast.makeText(context, ""+mensaje, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e){
                    String logit = e.toString();
                    Log.i("JsonExceprtion*********", logit);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String bas = volleyError.toString();
                Log.i("No guarda nada ********", bas);
                Toast.makeText(context, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id", String.valueOf(idCat));
                map.put("nombre", nombreCat);
                map.put("estado", String.valueOf(estadoCat));
                return map;
            }
        };

        //tiempo de respuesta, establece politica de reintentos
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(context).addToRequestQueue(request);
    }

}
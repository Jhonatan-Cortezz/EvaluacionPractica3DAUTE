package com.jac.tablacategoria.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jac.tablacategoria.MainActivity;
import com.jac.tablacategoria.MySingleton;
import com.jac.tablacategoria.R;
import com.jac.tablacategoria.Setting_VAR;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditarCategoria extends AppCompatActivity {
    private EditText edtCode, edtNombre;
    private Spinner spinner;
    private Button btnUpdate;

    String datoSelect = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_categoria);
        //referencias
        edtCode = findViewById(R.id.edtCategoriaUp);
        edtNombre = findViewById(R.id.edtNombreCategoriaUp);
        spinner = findViewById(R.id.sp_estadoUp);
        btnUpdate = findViewById(R.id.btnUpdate);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
                R.array.estadoCategorias, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItemPosition()>0){
                    datoSelect = spinner.getSelectedItem().toString();
                } else {
                    datoSelect = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String senal = "";
        String codigo = "";
        String nombre = "";
        String estado = "";

        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                codigo = bundle.getString("codigo");
                senal = bundle.getString("senal");
                nombre = bundle.getString("nombre");
                estado = bundle.getString("estado");

                if (senal.equals("1")){
                    edtCode.setText(codigo);
                    edtNombre.setText(nombre);
                    //edtEstado.setText(estado);
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = edtCode.getText().toString();
                String nombre = edtNombre.getText().toString();
                if (id.length() == 0){
                    edtCode.setError("Por favor introduzca el Id");
                } else if (nombre.length() == 0){
                    edtNombre.setError("Por favor escriba el nombre de la categoria");
                } else if (spinner.getSelectedItemPosition() > 0){
                    //this action save in the BD
                    update_server(getApplicationContext(), Integer.parseInt(id), nombre, Integer.parseInt(datoSelect));
                } else {
                    Toast.makeText(getApplicationContext(), "Seleccione un estado para la categoria", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void update_server(final Context context, final int idCat, final String nombreCat, final int estadoCat){
        StringRequest request = new StringRequest(Request.Method.POST, Setting_VAR.URL_UPDATE_CATEGORIA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject requestJSON = null;
                try {
                    requestJSON = new JSONObject(response.toString());
                    String estado = requestJSON.getString("estado");
                    String mensaje = requestJSON.getString("mensaje");
                    if (estado.equals("1")){
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditarCategoria.this, MainActivity.class);
                        startActivity(intent);
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
                map.put("codigo", String.valueOf(idCat));
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
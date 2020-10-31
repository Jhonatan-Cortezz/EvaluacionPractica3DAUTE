package com.jac.tablacategoria;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jac.tablacategoria.ui.home.DetalleCategoria;
import com.jac.tablacategoria.ui.home.DtoCategoria;
import com.jac.tablacategoria.ui.home.DtoUsuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Autorizacion extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUserName, edtPassword;
    private Button btnSing;
    ArrayList<String> lista = null;
    ArrayList<DtoUsuario> listaCategoria;

    private boolean inputC = false;
    private boolean inputN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autorizacion);

        edtUserName = findViewById(R.id.usernameLog);
        edtPassword = findViewById(R.id.passwordLog);
        btnSing = findViewById(R.id.loginLog);

        btnSing.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String nom = edtUserName.getText().toString();
        String calve = edtPassword.getText().toString();

        recibirLog(this, nom, calve);
    }

    private void recibirLog(final Context context, final String idCat, final String nombreCat){
        StringRequest request = new StringRequest(Request.Method.POST, Setting_VAR.URL_LOGIN_USUARIO, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject requestJSON = null;
                try {
                    requestJSON = new JSONObject(response.toString());
                    String estado = requestJSON.getString("estado");
                    String mensaje = requestJSON.getString("mensaje");
                    if (estado.equals(idCat) && mensaje.equals(nombreCat)){
                        Intent intent = new Intent(context, MainActivity.class);
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
                map.put("nombre", String.valueOf(idCat));
                map.put("clave", nombreCat);
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
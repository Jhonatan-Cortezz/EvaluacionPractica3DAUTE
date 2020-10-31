package com.jac.tablacategoria.ui.home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class DetalleCategoria extends AppCompatActivity implements View.OnClickListener {
    private TextView tvCodigo, tvNombre, tvEstado;
    private Button btnEditarCat, btnBorrarCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_categoria);
        tvCodigo = findViewById(R.id.tvCodigoCatDetalle);
        tvNombre = findViewById(R.id.tvNombreCatDetalle);
        tvEstado = findViewById(R.id.tvEstadoCatDetalle);
        btnEditarCat = findViewById(R.id.btnEditarCat);
        btnBorrarCat = findViewById(R.id.btnBorrarCat);

        Bundle bundle = getIntent().getExtras();
        String id = bundle.getString("codigo");
        String noma = bundle.getString("nombre");
        String esta = bundle.getString("estado");

        tvCodigo.setText(id);
        tvNombre.setText(noma);
        tvEstado.setText(esta);

        btnEditarCat.setOnClickListener(this);
        btnBorrarCat.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String id = tvCodigo.getText().toString();
        if (view.getId() == R.id.btnBorrarCat){
            delete_cate(this, Integer.parseInt(id));
            //Toast.makeText(this, "Hole Borrado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if(view.getId() == R.id.btnEditarCat){
            //Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            String code = tvCodigo.getText().toString();
            String name = tvNombre.getText().toString();
            String estado = tvEstado.getText().toString();

            Intent intent = new Intent(getApplicationContext(), EditarCategoria.class);
            intent.putExtra("senal", "1");
            intent.putExtra("codigo", code);
            intent.putExtra("nombre", name);
            intent.putExtra("estado", estado);
            startActivity(intent);
        }

        //Intent intent = new Intent(this, HomeFragment.class);
        //startActivity(intent);
    }

    private void delete_cate(final Context context, final int idCat){
        StringRequest request = new StringRequest(Request.Method.POST, Setting_VAR.URL_DELETE_CATEGORIA, new Response.Listener<String>() {
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
                Toast.makeText(context, "Error No hay registro", Toast.LENGTH_SHORT).show();
            }
        }){
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("Content-Type", "application/json; charset=utf-8");
                map.put("Accept", "application/json");
                map.put("id", String.valueOf(idCat));
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
package com.jac.tablacategoria.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jac.tablacategoria.MySingleton;
import com.jac.tablacategoria.R;
import com.jac.tablacategoria.Setting_VAR;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Recyclerviewm extends AppCompatActivity {
    ArrayList<String> lista = null;
    ArrayList<DtoCategoria> listaCategoria;

    private RecyclerView recyclerView;
    private AdaptadorProductos adaptadorProductos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerviewm);

        recyclerView = findViewById(R.id.rvProd);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



    }

    private void recibirAllCat(){
        listaCategoria = new ArrayList<DtoCategoria>();
        lista = new ArrayList<String>();
        String urlConsultaCategoria = Setting_VAR.URL_CONSULTAR_CATEGORIAS;
        StringRequest request = new StringRequest(Request.Method.POST, urlConsultaCategoria, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray respuestaJSOn = new JSONArray(response);
                    int totalEnct = respuestaJSOn.length();

                    DtoCategoria objCategorias = null;
                    for (int i = 0; i < respuestaJSOn.length(); i++){
                        JSONObject categoriaObj = respuestaJSOn.getJSONObject(i);
                        int idC = categoriaObj.getInt("id_categoria");
                        String name = categoriaObj.getString("nom_categoria");
                        int stado = categoriaObj.getInt("estado_categoria");

                        objCategorias = new DtoCategoria(idC, name, stado);

                        listaCategoria.add(objCategorias);


                        lista.add(listaCategoria.get(i).getIdCategoria() + " - " + listaCategoria.get(i).getNombre());
                        adaptadorProductos = new AdaptadorProductos(Recyclerviewm.this, listaCategoria);

                        recyclerView.setAdapter(adaptadorProductos);

                        Log.i("Id Categoria:    ", String.valueOf(objCategorias.getIdCategoria()));
                        Log.i("Nombre:    ", String.valueOf(objCategorias.getNombre()));

                    }
                    //resultado.setText("Datos: " + response.toString());
                    //Toast.makeText(getContext(), "Id: " + idCategori + "\nNombre: " + nombreCat + "\nEstado: " + estadoCat, Toast.LENGTH_SHORT).show();

                } catch (JSONException ex){
                    String none = ex.toString();
                    Log.i("NO consulta ***** ", none);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                String err = volleyError.toString();
                Log.i("No se pudo **********", err);
            }
        });
        //tiempo de respuesta, establece politica de reintentos
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}
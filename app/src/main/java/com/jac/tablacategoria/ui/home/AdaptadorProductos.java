package com.jac.tablacategoria.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jac.tablacategoria.R;

import java.util.List;


public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ProductosViewHolder> {

    private Context mCtx;
    private List<DtoCategoria> productoList;

    public AdaptadorProductos(Context mCtx, List<DtoCategoria> productoListList) {
        this.mCtx = mCtx;
        this.productoList = productoListList;
    }

    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_recyclerview, null);
        return new ProductosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductosViewHolder holder, int position) {
        DtoCategoria producto = productoList.get(position);
        holder.tvCodigoProducto.setText(String.valueOf(producto.getIdCategoria()));
        holder.tvNombreProducto.setText(producto.getNombre());
        holder.tvPrecioProducto.setText(String.valueOf(producto.getEstado()));
    }

    @Override
    public int getItemCount() {
        return productoList.size();
    }

    public class ProductosViewHolder extends RecyclerView.ViewHolder {

        TextView tvCodigoProducto, tvNombreProducto, tvPrecioProducto;
        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCodigoProducto = itemView.findViewById(R.id.textViewCodigo1);
            tvNombreProducto = itemView.findViewById(R.id.textViewDescripcion1);
            tvPrecioProducto = itemView.findViewById(R.id.textViewPrecio1);

        }
    }
}

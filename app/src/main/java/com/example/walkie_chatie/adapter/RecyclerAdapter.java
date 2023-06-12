package com.example.walkie_chatie.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.walkie_chatie.R;
import com.example.walkie_chatie.model.Mensage;

import java.util.ArrayList;

/**
 * recycler adapter sencillo para ajustar la lista de mensajes al recyclerView
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerHolder> {

    private ArrayList<Mensage> msgList;

    /**
     * Constructor
     * @param msgList lista de elementos a representar
     */
    public RecyclerAdapter( ArrayList<Mensage> msgList){
        this.msgList =msgList;
    }

    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler,parent , false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);

        return recyclerHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        Mensage msg = msgList.get(position);
        holder.userName.setText(msg.getName()+":");
        holder.msg.setText(msg.getMsg());
    }

    @Override
    public int getItemCount() {return msgList.size();}

    /**
     * clase adaptada a las cualidades de nuestro recyclerView. Extiende de RecyclerView.ViewHolder
     */
    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView msg;

        /**
         * Constructor que asocia el RecyclerHolder con la vista
         * @param itemView
         */
        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.txtUser);
            msg= (TextView) itemView.findViewById(R.id.txtmainMensage);
        }
    }
}

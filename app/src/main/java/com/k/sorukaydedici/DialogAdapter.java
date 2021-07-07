package com.k.sorukaydedici;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.ViewHolder> {

   private List<KlasorModel> klasorModelList;
   private KlasorListener klasorListener;
   private String durum;
   private Context context;

    public DialogAdapter(List<KlasorModel> klasorModelList,KlasorListener klasorListener,String durum,Context context) {
        this.klasorModelList = klasorModelList;
        this.klasorListener = klasorListener;
        this.durum = durum;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(durum.equals("asil")){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_baslik_item_iki,parent,false);
        } else if (durum.equals("dialog")){
           view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_baslik_item,parent,false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DialogAdapter.ViewHolder holder, int position) {
     holder.setData(klasorModelList.get(position).getIsim(),position);
    }

    @Override
    public int getItemCount() {
        return klasorModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageButton imageButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.textBaslik); //Burada kaldık.
            if(durum.equals("asil")){
                imageButton=itemView.findViewById(R.id.silButon);
            }
        }

        public void setData(final String isim, final int position) {
            textView.setText(isim);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    klasorListener.onKlasorListener(klasorModelList.get(position),position);
                }
            });

            if(durum.equals("asil")){
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        uyari(position);
                    }
                });
            }
        }

        private void uyari(final int position) {
            String[] items = {"Evet","İptal"};
            final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle("Silmek istediğinize emin misiniz?");
            dialog.setCancelable(true);
            dialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i==0){
                        klasorModelList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,klasorModelList.size());
                    }
                    if(i==1){
                      dialog.create().cancel();
                    }
                }
            });
            dialog.create().show();
        }
    }
}

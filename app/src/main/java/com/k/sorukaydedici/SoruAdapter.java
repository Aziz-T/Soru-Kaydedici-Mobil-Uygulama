package com.k.sorukaydedici;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class SoruAdapter extends RecyclerView.Adapter<SoruAdapter.ViewHolder> {

    private List<SoruModel> soruModelList;
    private SoruListener soruListener;
    private Context context;



    public SoruAdapter(List<SoruModel> soruModelList,SoruListener soruListener,Context context) {
        this.soruModelList = soruModelList;
        this.soruListener = soruListener;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.soru_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(soruModelList.get(position).getBaslik(),soruModelList.get(position).getSoru(),soruModelList.get(position).getCevap(),position);

    }

    @Override
    public int getItemCount() {
        return soruModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView baslikTxt, soruTxt;
        private ImageButton menuButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            baslikTxt=itemView.findViewById(R.id.textBaslik);
            soruTxt=itemView.findViewById(R.id.textKonu);
            menuButton=itemView.findViewById(R.id.imageSet);
        }

        public void setData(final String baslik, final String soru, final String cevap, final int position) {
            baslikTxt.setText(baslik);
            soruTxt.setText(soru);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    soruListener.onSoruListener(soruModelList.get(position),position);
                }
            });


            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(view,position);
                }
            });
        }
        private void showPopupMenu(View view, final int position) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
            popupMenu.inflate(R.menu.soru_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.silMenu:
                            soruModelList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,soruModelList.size());
                            return true;
                        case R.id.paylas:
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("text/plain");
                            intent.putExtra(Intent.EXTRA_SUBJECT, "Soru Kaydedici");
                            intent.putExtra(Intent.EXTRA_TEXT, soruModelList.get(position).getSoru());
                            context.startActivity(Intent.createChooser(intent, "Seçiniz"));
                            return true;

                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        }


    }
}

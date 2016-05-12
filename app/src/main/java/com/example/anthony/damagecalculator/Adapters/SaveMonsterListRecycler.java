package com.example.anthony.damagecalculator.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anthony.damagecalculator.Data.Element;
import com.example.anthony.damagecalculator.Data.Monster;
import com.example.anthony.damagecalculator.Data.OrbMatch;
import com.example.anthony.damagecalculator.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by DragonLotus on 11/4/2015.
 */
public class SaveMonsterListRecycler extends RecyclerView.Adapter<SaveMonsterListRecycler.ViewHolder> {

    private ArrayList<Monster> monsterList;
    private View.OnClickListener monsterListOnClickListener;
    private Context mContext;
    private LayoutInflater inflater;
    private Toast toast;
    private ArrayList<Integer> latentList;
    private int expandedPosition = -1;

    private View.OnClickListener onItemClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int previous;
            ViewHolder holder = (ViewHolder) v.getTag();
            if(holder.getAdapterPosition() != expandedPosition){
                if (expandedPosition > 0){
                    previous = expandedPosition;
                    notifyItemChanged(previous);
                }
                expandedPosition = holder.getAdapterPosition();
                notifyItemChanged(expandedPosition);
            } else {
                previous = expandedPosition;
                expandedPosition = -1;
                notifyItemChanged(previous);
            }
        }
    };

//    @Override
//    public void onClick(View v) {
//        ViewHolder holder = (ViewHolder) v.getTag();
//        if (expandedPosition >=0){
//            int previous = expandedPosition;
//            notifyItemChanged(previous);
//        }
//        expandedPosition = holder.getPosition();
//        notifyItemChanged(expandedPosition);
//        Toast.makeText(mContext, "Clicked position: " + expandedPosition, Toast.LENGTH_SHORT).show();
//    }

    public SaveMonsterListRecycler(Context context, ArrayList<Monster> monsterList, View.OnClickListener monsterListOnClickListener){
        mContext = context;
        this.monsterList = monsterList;
        this.monsterListOnClickListener = monsterListOnClickListener;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.monsterName.setText(monsterList.get(position).getName());
        viewHolder.monsterLevel.setText("Lv. " + monsterList.get(position).getCurrentLevel() + " - ");
        viewHolder.monsterPlus.setText(" +"+ monsterList.get(position).getTotalPlus() + " ");
        viewHolder.monsterATK.setText(Integer.toString(monsterList.get(position).getTotalAtk()) + " / ");
        viewHolder.monsterRCV.setText(Integer.toString(monsterList.get(position).getTotalRcv()));
        viewHolder.monsterHP.setText(Integer.toString(monsterList.get(position).getTotalHp()) + " / ");
        viewHolder.monsterPicture.setImageResource(monsterList.get(position).getMonsterPicture());
        viewHolder.monsterAwakenings.setText(" " + Integer.toString(monsterList.get(position).getCurrentAwakenings()));
        if (monsterList.get(position).getCurrentAwakenings() == monsterList.get(position).getMaxAwakenings()) {
            viewHolder.monsterAwakenings.setBackgroundResource(R.drawable.awakening_max);
            viewHolder.monsterAwakenings.setText("");
        }
        if (monsterList.get(position).getMonsterId() == 0) {
            viewHolder.type1.setVisibility(View.GONE);
            viewHolder.type2.setVisibility(View.GONE);
            viewHolder.type3.setVisibility(View.GONE);
            viewHolder.monsterPlus.setVisibility(View.GONE);
            viewHolder.monsterAwakenings.setVisibility(View.GONE);
            viewHolder.monsterHP.setVisibility(View.GONE);
            viewHolder.monsterATK.setVisibility(View.GONE);
            viewHolder.monsterRCV.setVisibility(View.GONE);
            viewHolder.monsterLevel.setVisibility(View.GONE);
            viewHolder.favorite.setVisibility(View.INVISIBLE);
            viewHolder.favoriteOutline.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.type1.setVisibility(View.VISIBLE);
            viewHolder.type2.setVisibility(View.VISIBLE);
            viewHolder.type3.setVisibility(View.VISIBLE);
            viewHolder.monsterPlus.setVisibility(View.VISIBLE);
            viewHolder.monsterPicture.setVisibility(View.VISIBLE);
            viewHolder.monsterAwakenings.setVisibility(View.VISIBLE);
            viewHolder.monsterHP.setVisibility(View.VISIBLE);
            viewHolder.monsterATK.setVisibility(View.VISIBLE);
            viewHolder.monsterRCV.setVisibility(View.VISIBLE);
            viewHolder.monsterLevel.setVisibility(View.VISIBLE);
            viewHolder.favorite.setVisibility(View.VISIBLE);
            viewHolder.favoriteOutline.setVisibility(View.VISIBLE);
        }

        viewHolder.favorite.setColorFilter(0xFFFFAADD);
        if(monsterList.get(position).isFavorite()){
            viewHolder.favorite.setVisibility(View.VISIBLE);
        }else {
            viewHolder.favorite.setVisibility(View.INVISIBLE);
        }

        if(latentList == null){
            latentList = new ArrayList<>();
        } else {
            latentList.clear();
        }

        for(int i = 0; i < monsterList.get(position).getLatents().size(); i++){
            if(monsterList.get(position).getLatents().get(i) != 0){
                latentList.add(1);
            }
        }
        if(latentList.size() == 5){
            viewHolder.monsterLatents.setBackgroundResource(R.drawable.latent_max);
            viewHolder.monsterLatents.setText("");
            viewHolder.monsterLatents.setVisibility(View.VISIBLE);
        }else if(latentList.size() == 0) {
            viewHolder.monsterLatents.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.monsterLatents.setText(" "+latentList.size());
            viewHolder.monsterLatents.setVisibility(View.VISIBLE);
        }

        if (monsterList.get(position).getTotalPlus() == 0) {
            viewHolder.monsterPlus.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.monsterPlus.setVisibility(View.VISIBLE);
        }
        if (monsterList.get(position).getCurrentAwakenings() == 0) {
            viewHolder.monsterAwakenings.setVisibility(View.INVISIBLE);
            if(latentList.size() != 0){
                ViewGroup.LayoutParams z = viewHolder.monsterAwakenings.getLayoutParams();
                viewHolder.monsterLatents.setLayoutParams(z);
            }
        } else {
            viewHolder.monsterAwakenings.setVisibility(View.VISIBLE);
        }

        switch(monsterList.get(position).getType1()){
            case 0:
                viewHolder.type1.setImageResource(R.drawable.type_evo_material);
                break;
            case 1:
                viewHolder.type1.setImageResource(R.drawable.type_balanced);
                break;
            case 2:
                viewHolder.type1.setImageResource(R.drawable.type_physical);
                break;
            case 3:
                viewHolder.type1.setImageResource(R.drawable.type_healer);
                break;
            case 4:
                viewHolder.type1.setImageResource(R.drawable.type_dragon);
                break;
            case 5:
                viewHolder.type1.setImageResource(R.drawable.type_god);
                break;
            case 6:
                viewHolder.type1.setImageResource(R.drawable.type_attacker);
                break;
            case 7:
                viewHolder.type1.setImageResource(R.drawable.type_devil);
                break;
            case 8:
                viewHolder.type1.setImageResource(R.drawable.type_machine);
                break;
            case 12:
                viewHolder.type1.setImageResource(R.drawable.type_awoken);
                break;
            case 13:
                viewHolder.type1.setVisibility(View.INVISIBLE);
                break;
            case 14:
                viewHolder.type1.setImageResource(R.drawable.type_enhance_material);
                break;
            default:
                viewHolder.type1.setVisibility(View.INVISIBLE);
                break;
        }
        switch(monsterList.get(position).getType2()){
            case 0:
                viewHolder.type2.setImageResource(R.drawable.type_evo_material);
                break;
            case 1:
                viewHolder.type2.setImageResource(R.drawable.type_balanced);
                break;
            case 2:
                viewHolder.type2.setImageResource(R.drawable.type_physical);
                break;
            case 3:
                viewHolder.type2.setImageResource(R.drawable.type_healer);
                break;
            case 4:
                viewHolder.type2.setImageResource(R.drawable.type_dragon);
                break;
            case 5:
                viewHolder.type2.setImageResource(R.drawable.type_god);
                break;
            case 6:
                viewHolder.type2.setImageResource(R.drawable.type_attacker);
                break;
            case 7:
                viewHolder.type2.setImageResource(R.drawable.type_devil);
                break;
            case 8:
                viewHolder.type2.setImageResource(R.drawable.type_machine);
                break;
            case 12:
                viewHolder.type2.setImageResource(R.drawable.type_awoken);
                break;
            case 13:
                viewHolder.type2.setVisibility(View.INVISIBLE);
                break;
            case 14:
                viewHolder.type2.setImageResource(R.drawable.type_enhance_material);
                break;
            default:
                viewHolder.type2.setVisibility(View.INVISIBLE);
                break;
        }
        switch(monsterList.get(position).getType3()){
            case 0:
                viewHolder.type3.setImageResource(R.drawable.type_evo_material);
                break;
            case 1:
                viewHolder.type3.setImageResource(R.drawable.type_balanced);
                break;
            case 2:
                viewHolder.type3.setImageResource(R.drawable.type_physical);
                break;
            case 3:
                viewHolder.type3.setImageResource(R.drawable.type_healer);
                break;
            case 4:
                viewHolder.type3.setImageResource(R.drawable.type_dragon);
                break;
            case 5:
                viewHolder.type3.setImageResource(R.drawable.type_god);
                break;
            case 6:
                viewHolder.type3.setImageResource(R.drawable.type_attacker);
                break;
            case 7:
                viewHolder.type3.setImageResource(R.drawable.type_devil);
                break;
            case 8:
                viewHolder.type3.setImageResource(R.drawable.type_machine);
                break;
            case 12:
                viewHolder.type3.setImageResource(R.drawable.type_awoken);
                break;
            case 13:
                viewHolder.type3.setVisibility(View.INVISIBLE);
                break;
            case 14:
                viewHolder.type3.setImageResource(R.drawable.type_enhance_material);
                break;
            default:
                viewHolder.type3.setVisibility(View.INVISIBLE);
                break;
        }
        viewHolder.favorite.setTag(R.string.index, position);
        viewHolder.favoriteOutline.setTag(R.string.index, position);
        viewHolder.favorite.setOnClickListener(favoriteOnClickListener);
        viewHolder.favoriteOutline.setOnClickListener(favoriteOnClickListener);
        viewHolder.itemView.setOnClickListener(onItemClickListener);

        viewHolder.monsterName.setSelected(true);
        viewHolder.itemView.setTag(viewHolder);
        viewHolder.itemView.setTag(R.string.index, position);
        viewHolder.choose.setTag(R.string.index, position);
        viewHolder.choose.setOnClickListener(monsterListOnClickListener);
        if(position == expandedPosition && expandedPosition != 0){
            viewHolder.expandLayout.setVisibility(View.VISIBLE);
            viewHolder.rarity.setVisibility(View.VISIBLE);
            viewHolder.rarityStar.setVisibility(View.VISIBLE);
            viewHolder.monsterHP.setVisibility(View.GONE);
            viewHolder.monsterATK.setVisibility(View.GONE);
            viewHolder.monsterRCV.setVisibility(View.GONE);
            for(int i = 0; i < 9; i++){
                if(i >= monsterList.get(position).getMaxAwakenings()){
                    viewHolder.awakeningHolder.getChildAt(i).setVisibility(View.GONE);
                }else {
                    viewHolder.awakeningHolder.getChildAt(i).setVisibility(View.VISIBLE);
                }
            }
            for (int j = 0; j < monsterList.get(position).getCurrentAwakenings(); j++) {
                switch (monsterList.get(position).getAwokenSkills().get(j)) {
                    case 1:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_1);
                        break;
                    case 2:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_2);
                        break;
                    case 3:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_3);
                        break;
                    case 4:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_4);
                        break;
                    case 5:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_5);
                        break;
                    case 6:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_6);
                        break;
                    case 7:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_7);
                        break;
                    case 8:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_8);
                        break;
                    case 9:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_9);
                        break;
                    case 10:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_10);
                        break;
                    case 11:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_11);
                        break;
                    case 12:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_12);
                        break;
                    case 13:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_13);
                        break;
                    case 14:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_14);
                        break;
                    case 15:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_15);
                        break;
                    case 16:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_16);
                        break;
                    case 17:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_17);
                        break;
                    case 18:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_18);
                        break;
                    case 19:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_19);
                        break;
                    case 20:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_20);
                        break;
                    case 21:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_21);
                        break;
                    case 22:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_22);
                        break;
                    case 23:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_23);
                        break;
                    case 24:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_24);
                        break;
                    case 25:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_25);
                        break;
                    case 26:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_26);
                        break;
                    case 27:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_27);
                        break;
                    case 28:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_28);
                        break;
                    case 29:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_29);
                        break;
                    case 30:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_30);
                        break;
                    case 31:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_31);
                        break;
                    case 32:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_32);
                        break;
                    case 33:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_33);
                        break;
                    case 34:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_34);
                        break;
                    case 35:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_35);
                        break;
                    case 36:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_36);
                        break;
                    case 37:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_37);
                        break;
                }
            }

            for (int j = monsterList.get(position).getCurrentAwakenings(); j < monsterList.get(position).getMaxAwakenings(); j++) {
                switch (monsterList.get(position).getAwokenSkills().get(j)) {
                    case 1:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_1_disabled);
                        break;
                    case 2:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_2_disabled);
                        break;
                    case 3:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_3_disabled);
                        break;
                    case 4:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_4_disabled);
                        break;
                    case 5:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_5_disabled);
                        break;
                    case 6:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_6_disabled);
                        break;
                    case 7:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_7_disabled);
                        break;
                    case 8:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_8_disabled);
                        break;
                    case 9:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_9_disabled);
                        break;
                    case 10:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_10_disabled);
                        break;
                    case 11:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_11_disabled);
                        break;
                    case 12:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_12_disabled);
                        break;
                    case 13:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_13_disabled);
                        break;
                    case 14:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_14_disabled);
                        break;
                    case 15:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_15_disabled);
                        break;
                    case 16:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_16_disabled);
                        break;
                    case 17:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_17_disabled);
                        break;
                    case 18:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_18_disabled);
                        break;
                    case 19:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_19_disabled);
                        break;
                    case 20:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_20_disabled);
                        break;
                    case 21:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_21_disabled);
                        break;
                    case 22:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_22_disabled);
                        break;
                    case 23:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_23_disabled);
                        break;
                    case 24:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_24_disabled);
                        break;
                    case 25:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_25_disabled);
                        break;
                    case 26:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_26_disabled);
                        break;
                    case 27:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_27_disabled);
                        break;
                    case 28:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_28_disabled);
                        break;
                    case 29:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_29_disabled);
                        break;
                    case 30:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_30_disabled);
                        break;
                    case 31:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_31_disabled);
                        break;
                    case 32:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_32_disabled);
                        break;
                    case 33:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_33_disabled);
                        break;
                    case 34:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_34_disabled);
                        break;
                    case 35:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_35_disabled);
                        break;
                    case 36:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_36_disabled);
                        break;
                    case 37:
                        viewHolder.awakeningHolder.getChildAt(j).setBackgroundResource(R.drawable.awakening_37_disabled);
                        break;
                }
            }
            if(monsterList.get(position).getType1() == 0 || monsterList.get(position).getType1() == 14){
                viewHolder.latentHolder.setVisibility(View.GONE);
            }else {
                viewHolder.latentHolder.setVisibility(View.VISIBLE);
                for (int j = 0; j < monsterList.get(position).getLatents().size(); j++) {
                    switch (monsterList.get(position).getLatents().get(j)) {
                        case 1:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_1);
                            break;
                        case 2:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_2);
                            break;
                        case 3:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_3);
                            break;
                        case 4:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_4);
                            break;
                        case 5:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_5);
                            break;
                        case 6:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_6);
                            break;
                        case 7:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_7);
                            break;
                        case 8:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_8);
                            break;
                        case 9:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_9);
                            break;
                        case 10:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_10);
                            break;
                        case 11:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_11);
                            break;
                        default:
                            viewHolder.latentHolder.getChildAt(j).setBackgroundResource(R.drawable.latent_awakening_blank);
                            break;

                    }
                }
            }
            viewHolder.rarity.setText("" + monsterList.get(position).getRarity());
            viewHolder.rarityStar.setColorFilter(0xFFD4D421);
            viewHolder.hpBase.setText("" + monsterList.get(position).getCurrentHp());
            viewHolder.atkBase.setText("" + monsterList.get(position).getCurrentAtk());
            viewHolder.rcvBase.setText("" + monsterList.get(position).getCurrentRcv());
            viewHolder.hpPlus.setText("+" + monsterList.get(position).getHpPlus());
            viewHolder.atkPlus.setText("+" + monsterList.get(position).getAtkPlus());
            viewHolder.rcvPlus.setText("+" + monsterList.get(position).getRcvPlus());
            viewHolder.hpTotal.setText("" + monsterList.get(position).getTotalHp());
            viewHolder.atkTotal.setText("" + monsterList.get(position).getTotalAtk());
            viewHolder.rcvTotal.setText("" + monsterList.get(position).getTotalRcv());
        } else {
            viewHolder.expandLayout.setVisibility(View.GONE);
            viewHolder.rarity.setVisibility(View.GONE);
            viewHolder.rarityStar.setVisibility(View.GONE);
            if(monsterList.get(position).getType1() != -1){
                viewHolder.monsterHP.setVisibility(View.VISIBLE);
                viewHolder.monsterATK.setVisibility(View.VISIBLE);
                viewHolder.monsterRCV.setVisibility(View.VISIBLE);
            }
        }
    }

    private View.OnClickListener favoriteOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.string.index);
            if (!monsterList.get(position).isFavorite()){
                monsterList.get(position).setFavorite(true);
                monsterList.get(position).save();
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(mContext, "Monster favorited", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                monsterList.get(position).setFavorite(false);
                monsterList.get(position).save();
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(mContext, "Monster unfavorited", Toast.LENGTH_SHORT);
                toast.show();
            }
            notifyItemChanged(position);
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.save_monster_list_row, parent, false));
    }

    @Override
    public int getItemCount() {
        return monsterList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView monsterName, monsterPlus, monsterAwakenings, monsterHP, monsterATK, monsterRCV, monsterLevel, monsterLatents, hpBase, hpPlus, hpTotal, atkBase, atkPlus, atkTotal, rcvBase, rcvPlus, rcvTotal, rarity;
        ImageView monsterPicture, type1, type2, type3, favorite, favoriteOutline, awakening1, awakening2, awakening3, awakening4, awakening5, awakening6, awakening7, awakening8, awakening9, latent1, latent2, latent3, latent4, latent5, rarityStar;
        RelativeLayout expandLayout;
        LinearLayout awakeningHolder, latentHolder;
        Button choose;

        public ViewHolder(View convertView){
            super(convertView);
            monsterName = (TextView) convertView.findViewById(R.id.monsterName);
            monsterPlus = (TextView) convertView.findViewById(R.id.monsterPlus);
            monsterAwakenings = (TextView) convertView.findViewById(R.id.monsterAwakenings);
            monsterLatents = (TextView) convertView.findViewById(R.id.monsterLatents);
            monsterATK = (TextView) convertView.findViewById(R.id.monsterATK);
            monsterRCV = (TextView) convertView.findViewById(R.id.monsterRCV);
            monsterHP = (TextView) convertView.findViewById(R.id.monsterHP);
            monsterLevel = (TextView) convertView.findViewById(R.id.monsterLevel);
            type1 = (ImageView) convertView.findViewById(R.id.type1);
            type2 = (ImageView) convertView.findViewById(R.id.type2);
            type3 = (ImageView) convertView.findViewById(R.id.type3);
            favorite = (ImageView) convertView.findViewById(R.id.favorite);
            favoriteOutline = (ImageView) convertView.findViewById(R.id.favoriteOutline);
            monsterPicture = (ImageView) convertView.findViewById(R.id.monsterPicture);
            expandLayout = (RelativeLayout) convertView.findViewById(R.id.expandLayout);
            hpBase = (TextView) convertView.findViewById(R.id.hpBase);
            atkBase = (TextView) convertView.findViewById(R.id.atkBase);
            rcvBase = (TextView) convertView.findViewById(R.id.rcvBase);
            hpPlus = (TextView) convertView.findViewById(R.id.hpPlus);
            atkPlus = (TextView) convertView.findViewById(R.id.atkPlus);
            rcvPlus = (TextView) convertView.findViewById(R.id.rcvPlus);
            hpTotal = (TextView) convertView.findViewById(R.id.hpTotal);
            atkTotal = (TextView) convertView.findViewById(R.id.atkTotal);
            rcvTotal = (TextView) convertView.findViewById(R.id.rcvTotal);
            awakening1 = (ImageView) convertView.findViewById(R.id.awakening1);
            awakening2 = (ImageView) convertView.findViewById(R.id.awakening2);
            awakening3 = (ImageView) convertView.findViewById(R.id.awakening3);
            awakening4 = (ImageView) convertView.findViewById(R.id.awakening4);
            awakening5 = (ImageView) convertView.findViewById(R.id.awakening5);
            awakening6 = (ImageView) convertView.findViewById(R.id.awakening6);
            awakening7 = (ImageView) convertView.findViewById(R.id.awakening7);
            awakening8 = (ImageView) convertView.findViewById(R.id.awakening8);
            awakening9 = (ImageView) convertView.findViewById(R.id.awakening9);
            latent1 = (ImageView) convertView.findViewById(R.id.latent1);
            latent2 = (ImageView) convertView.findViewById(R.id.latent2);
            latent3 = (ImageView) convertView.findViewById(R.id.latent3);
            latent4 = (ImageView) convertView.findViewById(R.id.latent4);
            latent5 = (ImageView) convertView.findViewById(R.id.latent5);
            awakeningHolder = (LinearLayout) convertView.findViewById(R.id.awakeningHolder);
            latentHolder = (LinearLayout) convertView.findViewById(R.id.latentHolder);
            rarity = (TextView) convertView.findViewById(R.id.rarity);
            rarityStar = (ImageView) convertView.findViewById(R.id.rarityStar);
            choose = (Button) convertView.findViewById(R.id.choose);
        }
    }

    public void notifyDataSetChanged(ArrayList<Monster> monsterList) {
        this.monsterList = monsterList;
        notifyDataSetChanged();
    }

    public ArrayList<Monster> getMonsterList() {
        return monsterList;
    }

    public void setMonsterList(ArrayList<Monster> monsterList) {
        this.monsterList = monsterList;
    }

    public Monster getItem(int position){
        return monsterList.get(position);
    }
}

package com.example.anthony.damagecalculator.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.anthony.damagecalculator.Data.Element;
import com.example.anthony.damagecalculator.Data.Monster;
import com.example.anthony.damagecalculator.R;

import java.util.ArrayList;

/**
 * Created by Anthony on 7/14/2015.
 */
public class MonsterListAdapter extends ArrayAdapter<Monster> {
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Monster> monsterList;
    private int resourceId;

    public MonsterListAdapter(Context context, int textViewResourceId, ArrayList<Monster> monsterList) {
        super(context, textViewResourceId, monsterList);
        mContext = context;
        this.monsterList = monsterList;
        this.resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.monsterName = (TextView) convertView.findViewById(R.id.monsterName);
            viewHolder.monsterPlus = (TextView) convertView.findViewById(R.id.monsterPlus);
            viewHolder.monsterAwakenings = (TextView) convertView.findViewById(R.id.monsterAwakenings);
            viewHolder.monsterATK = (TextView) convertView.findViewById(R.id.monsterATK);
            viewHolder.monsterRCV = (TextView) convertView.findViewById(R.id.monsterRCV);
            viewHolder.monsterHP = (TextView) convertView.findViewById(R.id.monsterHP);
            viewHolder.monsterLevelValue = (TextView) convertView.findViewById(R.id.monsterLevelValue);
            viewHolder.monsterLevel = (TextView) convertView.findViewById(R.id.monsterLevel);
            viewHolder.monsterPicture = (ImageView) convertView.findViewById(R.id.monsterPicture);
            viewHolder.type1 = (TextView) convertView.findViewById(R.id.type1);
            viewHolder.type2 = (TextView) convertView.findViewById(R.id.type2);

            convertView.setTag(R.string.viewHolder, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.string.viewHolder);
        }
        viewHolder.monsterPicture.setImageResource(monsterList.get(position).getMonsterPicture());
        viewHolder.monsterATK.setText(Integer.toString(monsterList.get(position).getTotalAtk()) + " / ");
        viewHolder.monsterRCV.setText(Integer.toString(monsterList.get(position).getTotalRcv()));
        viewHolder.monsterHP.setText(Integer.toString(monsterList.get(position).getTotalHp()) + " / ");
        viewHolder.monsterAwakenings.setText(" " + Integer.toString(monsterList.get(position).getCurrentAwakenings()));
        viewHolder.monsterName.setText(monsterList.get(position).getName());
        viewHolder.monsterLevelValue.setText(Integer.toString(monsterList.get(position).getCurrentLevel()));
        viewHolder.monsterPlus.setText(" +" + Integer.toString(monsterList.get(position).getTotalPlus()) + " ");
        if (monsterList.get(position).getCurrentAwakenings() == monsterList.get(position).getMaxAwakenings()) {
            viewHolder.monsterAwakenings.setBackgroundResource(R.drawable.awakening_max);
            viewHolder.monsterAwakenings.setText("");
        }
        viewHolder.type1.setText(monsterList.get(position).getType1String());
        viewHolder.type2.setText(monsterList.get(position).getType2String());
        if (monsterList.get(position).getTotalPlus() == 0) {
            viewHolder.monsterPlus.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.monsterPlus.setVisibility(View.VISIBLE);
        }
        if (monsterList.get(position).getCurrentAwakenings() == 0) {
            viewHolder.monsterAwakenings.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.monsterAwakenings.setVisibility(View.VISIBLE);
        }
        if(monsterList.get(position).getMonsterId() == 0){
            viewHolder.monsterLevelValue.setVisibility(View.INVISIBLE);
            viewHolder.monsterHP.setVisibility(View.INVISIBLE);
            viewHolder.monsterATK.setVisibility(View.INVISIBLE);
            viewHolder.monsterRCV.setVisibility(View.INVISIBLE);
            viewHolder.monsterLevel.setVisibility(View.INVISIBLE);
            viewHolder.type1.setVisibility(View.INVISIBLE);
            viewHolder.type2.setVisibility(View.INVISIBLE);
        }else {
            viewHolder.monsterLevelValue.setVisibility(View.VISIBLE);
            viewHolder.monsterHP.setVisibility(View.VISIBLE);
            viewHolder.monsterATK.setVisibility(View.VISIBLE);
            viewHolder.monsterRCV.setVisibility(View.VISIBLE);
            viewHolder.monsterLevel.setVisibility(View.VISIBLE);
            viewHolder.type1.setVisibility(View.VISIBLE);
            viewHolder.type2.setVisibility(View.VISIBLE);
        }


        return convertView;
    }


    static class ViewHolder {
        TextView monsterName, monsterPlus, monsterAwakenings, monsterLevelValue, monsterHP, monsterATK, monsterRCV, monsterLevel, type1, type2;
        ImageView monsterPicture;

    }
    public void updateList(ArrayList<Monster> monsterList){
        this.monsterList = monsterList;
        notifyDataSetChanged();
    }
}

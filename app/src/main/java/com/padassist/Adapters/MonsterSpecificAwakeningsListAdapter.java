package com.padassist.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.padassist.Data.Monster;
import com.padassist.Data.RealmInt;
import com.padassist.Graphics.ExpandableHeightGridView;
import com.padassist.R;

import java.util.ArrayList;

import io.realm.RealmList;

public class MonsterSpecificAwakeningsListAdapter extends ArrayAdapter<Monster> {
    private Context mContext;
    private LayoutInflater inflater;
    private RealmList<Monster> monsterList;
    private ArrayList<Integer> awakeningList, awakeningListAll, monsterSpecificFilter, latentList, latentListAll, latentMonsterSpecificFilter;
    private AwakeningGridAdapter monsterAwakeningsGridAdapter;
    private int resourceId;
    private int teamBadge;

    public MonsterSpecificAwakeningsListAdapter(Context context, int textViewResourceId,
                                                RealmList<Monster> monsterList,
                                                ArrayList<Integer> monsterSpecificFilter,
                                                ArrayList<Integer> latentMonsterSpecificFilter,
                                                int teamBadge) {
        super(context, textViewResourceId, monsterList);
        mContext = context;
        this.monsterList = monsterList;
        this.resourceId = textViewResourceId;
        awakeningList = new ArrayList<>();
        awakeningListAll = new ArrayList<>();
        this.monsterSpecificFilter = monsterSpecificFilter;
        latentList = new ArrayList<>();
        latentListAll = new ArrayList<>();
        this.latentMonsterSpecificFilter = latentMonsterSpecificFilter;
        this.teamBadge = teamBadge;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.monsterPicture = (ImageView) convertView.findViewById(R.id.monsterPicture);
            viewHolder.monsterAwakenings = (ExpandableHeightGridView) convertView.findViewById(R.id.monsterAwakenings);
            viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout);
            trimAwakenings(position);
            monsterAwakeningsGridAdapter = new AwakeningGridAdapter(mContext, monsterList, awakeningList, latentList, true, teamBadge);
            viewHolder.monsterAwakenings.setAdapter(monsterAwakeningsGridAdapter);
            viewHolder.monsterAwakenings.setExpanded(true);
            if (position % 2 == 1) {
                viewHolder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.background_alternate));
            } else {
                viewHolder.relativeLayout.setBackgroundColor(mContext.getResources().getColor(R.color.background));
            }
            convertView.setTag(R.string.viewHolder, viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag(R.string.viewHolder);
        }
        viewHolder.monsterPicture.setImageBitmap(monsterList.get(position).getMonsterPicture());
        return convertView;
    }

    static class ViewHolder {
        ImageView monsterPicture;
        ExpandableHeightGridView monsterAwakenings;
        RelativeLayout relativeLayout;
    }

    private void trimAwakenings(int position) {
        if (awakeningList.size() != 0) {
            awakeningList.clear();
        }
        if (awakeningListAll.size() != 0) {
            awakeningListAll.clear();
        }
        if (monsterList.get(position).getCurrentAwakenings() < monsterList.get(position).getMaxAwakenings()) {
            for (int i = 0; i < monsterList.get(position).getCurrentAwakenings(); i++) {
                awakeningListAll.add(monsterList.get(position).getAwokenSkills(i));
            }
        } else {
            for (int i = 0; i < monsterList.get(position).getMaxAwakenings(); i++) {
                awakeningListAll.add(monsterList.get(position).getAwokenSkills(i));
            }
        }

        for (int i = 0; i < awakeningListAll.size(); i++) {
            if (monsterSpecificFilter.contains(awakeningListAll.get(i))) {
                awakeningList.add(awakeningListAll.get(i));
            }
        }
        if (latentList.size() != 0) {
            latentList.clear();
        }
        if (latentListAll.size() != 0) {
            latentListAll.clear();
        }

        if(monsterList.get(position).getTotalPlus() == 297){
            for(int i = 0; i < monsterList.get(position).getLatents().size(); i++){
                if(monsterList.get(position).getLatents().get(i).getValue() != 0){
                    latentListAll.add(monsterList.get(position).getLatents().get(i).getValue());
                }
            }
        } else {
            for(int i = 0; i < monsterList.get(position).getLatents().size() - 1; i++){
                if(monsterList.get(position).getLatents().get(i).getValue() != 0){
                    latentListAll.add(monsterList.get(position).getLatents().get(i).getValue());
                }
            }
        }
        for (int i = 0; i < latentListAll.size(); i++) {
            if (latentMonsterSpecificFilter.contains(latentListAll.get(i))) {
                latentList.add(latentListAll.get(i));
            }
        }

    }
}

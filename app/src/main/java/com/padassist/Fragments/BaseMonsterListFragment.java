package com.padassist.Fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.padassist.Adapters.BaseMonsterListRecycler;
import com.padassist.Data.BaseMonster;
import com.padassist.Data.Monster;
import com.padassist.Data.Team;
import com.padassist.MainActivity;
import com.padassist.R;
import com.padassist.BaseFragments.BaseMonsterListBase;

import org.parceler.Parcels;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.padassist.Fragments.MonsterTabLayoutFragment.INHERIT;
import static com.padassist.Fragments.MonsterTabLayoutFragment.SUB;


public class BaseMonsterListFragment extends BaseMonsterListBase {
    public static final String TAG = BaseMonsterListFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private Toast toast;

    public static BaseMonsterListFragment newInstance(boolean replaceAll, long replaceMonsterId, int monsterPosition) {
        BaseMonsterListFragment fragment = new BaseMonsterListFragment();
        Bundle args = new Bundle();
        args.putLong("replaceMonsterId", replaceMonsterId);
        args.putBoolean("replaceAll", replaceAll);
        args.putInt("monsterPosition", monsterPosition);
        args.putInt("selection", SUB);
        fragment.setArguments(args);
        return fragment;
    }

    public static BaseMonsterListFragment newInstance(Parcelable monster) {
        BaseMonsterListFragment fragment = new BaseMonsterListFragment();
        Bundle args = new Bundle();
        args.putParcelable("monster", monster);
        args.putInt("selection", MonsterTabLayoutFragment.INHERIT);
        fragment.setArguments(args);
        return fragment;
    }

    public BaseMonsterListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isGrid) {
            monsterListView.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
        } else {
            monsterListView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        }

        if(selection == SUB){
            baseMonsterListRecycler = new BaseMonsterListRecycler(getActivity(), monsterList, monsterListView, monsterListOnClickListener, monsterListOnLongClickListener, isGrid, clearTextFocus, realm);
        } else if(selection == INHERIT) {
            baseMonsterListRecycler = new BaseMonsterListRecycler(getActivity(), monsterList, monsterListView, inheritOnClickListener, inheritOnLongClickListener, isGrid, clearTextFocus, realm);
        }
        monsterListView.setAdapter(baseMonsterListRecycler);

    }



    private View.OnClickListener inheritOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(R.string.index);
            inheritMonster(monster, monsterList.get(position));
            getActivity().getSupportFragmentManager().popBackStack(MonsterPageFragment.TAG, 0);
        }
    };

    private View.OnLongClickListener inheritOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            int position = (int) view.getTag(R.string.index);
            inheritMonster(monster, monsterList.get(position));
            getActivity().getSupportFragmentManager().popBackStack(MonsterPageFragment.TAG, 0);
            return true;
        }
    };

    private void inheritMonster(Monster monster, BaseMonster inheritMonster){
        if(inheritMonster.getMonsterId() != 0){
            Monster newMonster = new Monster(inheritMonster);
            long lastMonsterId = realm.where(Monster.class).findAllSorted("monsterId").last().getMonsterId();
            newMonster.setMonsterId(lastMonsterId + 1);
            realm.beginTransaction();
            newMonster = realm.copyToRealm(newMonster);
            realm.commitTransaction();
            monster.setMonsterInherit(realm.copyFromRealm(newMonster));
        } else {
            monster.setMonsterInherit(null);
        }

    }

    private View.OnClickListener monsterListOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.string.index);
            Team newTeam = realm.where(Team.class).equalTo("teamId", 0).findFirst();
            Monster newMonster;
            if (monsterList.get(position).getMonsterId() == 0 && monsterPosition == 0) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getActivity(), "Leader cannot be empty", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                if (monsterList.get(position).getMonsterId() == 0) {
                    newMonster = realm.where(Monster.class).equalTo("monsterId", 0).findFirst();
                } else {
//                    RealmResults<Monster> results = realm.where(Monster.class).findAllSorted("monsterId");
//                    Log.d("BaseMonsterList", "results is: " + results);
//                    long lastMonsterId = results.get(results.size() - 1).getMonsterId();
                    long lastMonsterId = realm.where(Monster.class).findAllSorted("monsterId").last().getMonsterId();

                    newMonster = new Monster(monsterList.get(position));
                    if (monsterPosition == 5) {
                        newMonster.setHelper(true);
                    }
                    newMonster.setMonsterId(lastMonsterId + 1);
                    realm.beginTransaction();
                    newMonster = realm.copyToRealm(newMonster);
                    realm.commitTransaction();
                }
                if (replaceAll) {
                    ArrayList<Team> teamList = new ArrayList<>();
                    RealmResults results = realm.where(Team.class).findAll();
                    teamList.addAll(results);
                    for (int i = 0; i < teamList.size(); i++) {
                        for (int j = 0; j < teamList.get(i).getMonsters().size(); j++) {
                            if (teamList.get(i).getMonsters().get(j).getMonsterId() == replaceMonsterId) {
                                realm.beginTransaction();
                                teamList.get(i).setMonsters(j, newMonster);
                                realm.commitTransaction();
                            }
                        }
                    }
                } else {
                    realm.beginTransaction();
                    switch (monsterPosition) {
                        case 0:
                            newTeam.setLead(newMonster);
                            break;
                        case 1:
                            newTeam.setSub1(newMonster);
                            break;
                        case 2:
                            newTeam.setSub2(newMonster);
                            break;
                        case 3:
                            newTeam.setSub3(newMonster);
                            break;
                        case 4:
                            newTeam.setSub4(newMonster);
                            break;
                        case 5:
                            newTeam.setHelper(newMonster);
                            break;
                    }
                    realm.commitTransaction();
                }
                Log.d("BaseMonsterList", "newTeam monsters is: " + newTeam.getMonsters());
                getActivity().getSupportFragmentManager().popBackStack(MainTabLayoutFragment.TAG, 0);
//                if (newMonster.getMonsterId() != 0) {
//                    Parcelable monsterParcel = Parcels.wrap(newMonster);
//                    ((MainActivity)getActivity()).switchFragment(MonsterPageFragment.newInstance(newMonster.getMonsterId(), monsterPosition, monsterParcel), MonsterPageFragment.TAG, "good");
//                } else {
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getActivity(), "Monster created", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    };

    private View.OnLongClickListener monsterListOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int position =  ((RecyclerView.ViewHolder)v.getTag()).getAdapterPosition();
            Team newTeam = realm.where(Team.class).equalTo("teamId", 0).findFirst();
            Monster newMonster;
            if (monsterList.get(position).getMonsterId() == 0 && monsterPosition == 0) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getActivity(), "Leader cannot be empty", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                if (monsterList.get(position).getMonsterId() == 0) {
                    newMonster = realm.where(Monster.class).equalTo("monsterId", 0).findFirst();
                } else {
                    RealmResults<Monster> results = realm.where(Monster.class).findAllSorted("monsterId");
                    long lastMonsterId = results.get(results.size() - 1).getMonsterId();
                    newMonster = new Monster(monsterList.get(position));
                    if (monsterPosition == 5) {
                        newMonster.setHelper(true);
                    }
                    newMonster.setMonsterId(lastMonsterId + 1);
                    realm.beginTransaction();
                    newMonster = realm.copyToRealm(newMonster);
                    realm.commitTransaction();
                }
                if (replaceAll) {
                    ArrayList<Team> teamList = new ArrayList<>();
                    RealmResults results = realm.where(Team.class).findAll();
                    teamList.addAll(results);
                    for (int i = 0; i < teamList.size(); i++) {
                        for (int j = 0; j < teamList.get(i).getMonsters().size(); j++) {
                            if (teamList.get(i).getMonsters().get(j).getMonsterId() == replaceMonsterId) {
                                realm.beginTransaction();
                                teamList.get(i).setMonsters(j, newMonster);
                                realm.commitTransaction();
                            }
                        }
                    }
                } else {
                    realm.beginTransaction();
                    switch (monsterPosition) {
                        case 0:
                            newTeam.setLead(newMonster);
                            break;
                        case 1:
                            newTeam.setSub1(newMonster);
                            break;
                        case 2:
                            newTeam.setSub2(newMonster);
                            break;
                        case 3:
                            newTeam.setSub3(newMonster);
                            break;
                        case 4:
                            newTeam.setSub4(newMonster);
                            break;
                        case 5:
                            newTeam.setHelper(newMonster);
                            break;
                    }
                    realm.commitTransaction();
                }
                getActivity().getSupportFragmentManager().popBackStack(MainTabLayoutFragment.TAG, 0);
//                if (newMonster.getMonsterId() != 0) {
//                    Parcelable monsterParcel = Parcels.wrap(newMonster);
//                    ((MainActivity)getActivity()).switchFragment(MonsterPageFragment.newInstance(newMonster.getMonsterId(), monsterPosition, monsterParcel), MonsterPageFragment.TAG, "good");
//                } else {
//                    getActivity().getSupportFragmentManager().popBackStack();
//                }
            }
            return false;
        }
    };
}

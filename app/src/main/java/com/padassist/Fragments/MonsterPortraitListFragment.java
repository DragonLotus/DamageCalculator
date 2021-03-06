package com.padassist.Fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.padassist.Adapters.EnemyListRecycler;
import com.padassist.Adapters.MonsterPortraitListRecycler;
import com.padassist.Comparators.BaseMonsterAlphabeticalComparator;
import com.padassist.Comparators.BaseMonsterAtkComparator;
import com.padassist.Comparators.BaseMonsterAwakeningComparator;
import com.padassist.Comparators.BaseMonsterElement1Comparator;
import com.padassist.Comparators.BaseMonsterElement2Comparator;
import com.padassist.Comparators.BaseMonsterHpComparator;
import com.padassist.Comparators.BaseMonsterNumberComparator;
import com.padassist.Comparators.BaseMonsterRarityComparator;
import com.padassist.Comparators.BaseMonsterRcvComparator;
import com.padassist.Comparators.BaseMonsterType1Comparator;
import com.padassist.Comparators.BaseMonsterType2Comparator;
import com.padassist.Comparators.BaseMonsterType3Comparator;
import com.padassist.Data.BaseMonster;
import com.padassist.Data.Enemy;
import com.padassist.Data.Monster;
import com.padassist.Data.RealmElement;
import com.padassist.Data.RealmInt;
import com.padassist.Graphics.FastScroller;
import com.padassist.MainActivity;
import com.padassist.R;
import com.padassist.Util.Singleton;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class MonsterPortraitListFragment extends Fragment {
    public static final String TAG = MonsterPortraitListFragment.class.getSimpleName();
    private OnFragmentInteractionListener mListener;
    private RecyclerView monsterListView;
    private ArrayList<BaseMonster> filteredMonsters = new ArrayList<>();
    private ArrayList<BaseMonster> monsterList;
    private ArrayList<BaseMonster> monsterListAll;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    private MonsterPortraitListRecycler monsterPortraitListRecycler;
    private Toast toast;
    private boolean firstRun = true;
    private SortElementDialogFragment sortElementDialogFragment;
    private SortTypeDialogFragment sortTypeDialogFragment;
    private SortStatsDialogFragment sortStatsDialogFragment;
    private Comparator<BaseMonster> monsterNumberComparator = new BaseMonsterNumberComparator();
    private Comparator<BaseMonster> monsterElement1Comparator = new BaseMonsterElement1Comparator();
    private Comparator<BaseMonster> monsterElement2Comparator = new BaseMonsterElement2Comparator();
    private Comparator<BaseMonster> monsterType1Comparator = new BaseMonsterType1Comparator();
    private Comparator<BaseMonster> monsterType2Comparator = new BaseMonsterType2Comparator();
    private Comparator<BaseMonster> monsterType3Comparator = new BaseMonsterType3Comparator();
    private Comparator<BaseMonster> monsterAlphabeticalComparator = new BaseMonsterAlphabeticalComparator();
    private Comparator<BaseMonster> monsterHpComparator = new BaseMonsterHpComparator();
    private Comparator<BaseMonster> monsterAtkComparator = new BaseMonsterAtkComparator();
    private Comparator<BaseMonster> monsterRcvComparator = new BaseMonsterRcvComparator();
    private Comparator<BaseMonster> monsterRarityComparator = new BaseMonsterRarityComparator();
    private Comparator<BaseMonster> monsterAwakeningComparator = new BaseMonsterAwakeningComparator();
    private Realm realm;
    private FilterDialogFragment filterDialogFragment;
    private TextView noResults;
    private Enemy enemy;
    private Monster monster;

    private SharedPreferences preferences;
    private boolean isGrid;

    private FastScroller fastScroller;

    public MonsterPortraitListFragment() {
    }

    public static MonsterPortraitListFragment newInstance(Parcelable parcelable) {
        MonsterPortraitListFragment fragment = new MonsterPortraitListFragment();
        Bundle args = new Bundle();
        args.putParcelable("parcelable", parcelable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchMenuItem != null) {
            if (MenuItemCompat.isActionViewExpanded(searchMenuItem)) {
                MenuItemCompat.collapseActionView(searchMenuItem);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.setGroupVisible(R.id.sortGroup, true);
        menu.findItem(R.id.search).setVisible(true);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchMenuItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                searchFilter(newText);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                return true;
            }
        });
        searchView.setOnFocusChangeListener(searchViewOnFocusChangeListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toggleCoop:
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case R.id.reverseList:
                reverseArrayList();
                break;
            case R.id.sortAlphabetical:
                sortArrayList(0);
                break;
            case R.id.sortId:
                sortArrayList(1);
                break;
            case R.id.sortElement:
                sortArrayList(2);
                break;
            case R.id.sortType:
                sortArrayList(3);
                break;
            case R.id.sortStat:
                sortArrayList(4);
                break;
            case R.id.sortRarity:
                sortArrayList(5);
                break;
            case R.id.sortAwakenings:
                sortArrayList(6);
                break;
            case R.id.sortPlus:
                sortArrayList(7);
                break;
            case R.id.sortFavorite:
                sortArrayList(8);
                break;
            case R.id.sortLevel:
                sortArrayList(9);
                break;
            case R.id.sortLead:
                sortArrayList(10);
                break;
            case R.id.sortHelper:
                sortArrayList(11);
                break;
            case R.id.filterList:
                if (filterDialogFragment == null) {
                    filterDialogFragment = FilterDialogFragment.newInstance(saveTeam, false);
                }
                if (!filterDialogFragment.isAdded() && !firstRun) {
                    filterDialogFragment.show(getChildFragmentManager(), false, "Filter");
                }
                break;
            case R.id.toggleGrid:
                preferences.edit().putBoolean("isGrid", !isGrid).apply();
                isGrid = preferences.getBoolean("isGrid", true);
                if (isGrid) {
                    monsterListView.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
                } else {
                    monsterListView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
                }
                monsterPortraitListRecycler.notifyDataSetChanged(isGrid);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_base_monster_list, container, false);
        monsterListView = (RecyclerView) rootView.findViewById(R.id.monsterListView);
        fastScroller = (FastScroller) rootView.findViewById(R.id.fastScroller);
        noResults = (TextView) rootView.findViewById(R.id.noResults);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
                enemy = Parcels.unwrap(getArguments().getParcelable("parcelable"));
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(Singleton.getInstance().getContext());
        isGrid = preferences.getBoolean("isGrid", true);
        realm = Realm.getDefaultInstance();

        if (monsterListAll == null) {
            monsterListAll = new ArrayList<>();
        }
        monsterListAll.clear();

        if (monsterList == null) {
            monsterList = new ArrayList<>();
        }

            monsterListAll.addAll(realm.where(BaseMonster.class).greaterThan("monsterId", 0).findAll());
            monsterPortraitListRecycler = new MonsterPortraitListRecycler(getContext(), monsterList, monsterListView, isGrid, enemyOnClickListener);


        fastScroller.setRecyclerView(monsterListView);

        if (isGrid) {
            monsterListView.setLayoutManager(new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL));
        } else {
            monsterListView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        }

        monsterListView.setAdapter(monsterPortraitListRecycler);
        getActivity().setTitle("Select Monster");
    }

    private View.OnClickListener enemyOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int position = (int) view.getTag(R.string.index);
            realm.beginTransaction();
            enemy.setMonsterIdPicture(monsterList.get(position).getMonsterId());
            enemy.getTargetElement().set(0, new RealmElement(monsterList.get(position).getElement1Int()));
//            if (monsterList.get(position).getElement2Int() == -1) {
//                enemy.getTargetElement().set(1, new RealmElement(monsterList.get(position).getElement1Int()));
//            } else {
            enemy.getTargetElement().set(1, new RealmElement(monsterList.get(position).getElement2Int()));
//            }
            enemy.getTypes().set(0, new RealmInt(monsterList.get(position).getType1()));
            enemy.getTypes().set(1, new RealmInt(monsterList.get(position).getType2()));
            enemy.getTypes().set(2, new RealmInt(monsterList.get(position).getType3()));
            enemy.setEnemyName(monsterList.get(position).getName());

            enemy = realm.copyToRealmOrUpdate(enemy);
            realm.commitTransaction();

            getActivity().getSupportFragmentManager().popBackStack();
        }
    };

    private MonsterPortraitListRecycler.ClearTextFocus clearTextFocus = new MonsterPortraitListRecycler.ClearTextFocus() {
        @Override
        public void doThis() {
            searchView.clearFocus();
        }
    };

    private View.OnFocusChangeListener searchViewOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            hideKeyboard(v);
        }
    };

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // TODO: Rename method, updateAwakenings argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private SortElementDialogFragment.SortBy sortByElement = new SortElementDialogFragment.SortBy() {
        @Override
        public void sortElement1() {
            Singleton.getInstance().setBaseSortMethod(201);
            Collections.sort(monsterList, monsterElement1Comparator);
            monsterPortraitListRecycler.notifyDataSetChanged();
        }

        @Override
        public void sortElement2() {
            Singleton.getInstance().setBaseSortMethod(202);
            Collections.sort(monsterList, monsterElement2Comparator);
            monsterPortraitListRecycler.notifyDataSetChanged();
        }
    };

    private SortTypeDialogFragment.SortBy sortByType = new SortTypeDialogFragment.SortBy() {
        @Override
        public void sortType1() {
            Singleton.getInstance().setBaseSortMethod(301);
            Collections.sort(monsterList, monsterType1Comparator);
            monsterPortraitListRecycler.notifyDataSetChanged();
        }

        @Override
        public void sortType2() {
            Singleton.getInstance().setBaseSortMethod(302);
            Collections.sort(monsterList, monsterType2Comparator);
            monsterPortraitListRecycler.notifyDataSetChanged();
        }

        @Override
        public void sortType3() {
            Singleton.getInstance().setBaseSortMethod(303);
            Collections.sort(monsterList, monsterType3Comparator);
            monsterPortraitListRecycler.notifyDataSetChanged();
        }
    };

    private SortStatsDialogFragment.SortBy sortByStats = new SortStatsDialogFragment.SortBy() {
        @Override
        public void sortHp() {
            Singleton.getInstance().setBaseSortMethod(401);
            Collections.sort(monsterList, monsterHpComparator);
            monsterPortraitListRecycler.notifyDataSetChanged();
        }

        @Override
        public void sortAtk() {
            Singleton.getInstance().setBaseSortMethod(402);
            Collections.sort(monsterList, monsterAtkComparator);
            monsterPortraitListRecycler.notifyDataSetChanged();
        }

        @Override
        public void sortRcv() {
            Singleton.getInstance().setBaseSortMethod(403);
            Collections.sort(monsterList, monsterRcvComparator);
            monsterPortraitListRecycler.notifyDataSetChanged();
        }
    };

    public void sortArrayList(int sortMethod) {
        switch (sortMethod) {
            case 0:
                Singleton.getInstance().setBaseSortMethod(sortMethod);
                Collections.sort(monsterList, monsterAlphabeticalComparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 1:
                Singleton.getInstance().setBaseSortMethod(sortMethod);
                Collections.sort(monsterList, monsterNumberComparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 2:
                if (sortElementDialogFragment == null) {
                    sortElementDialogFragment = SortElementDialogFragment.newInstance(sortByElement);
                }
                if (!sortElementDialogFragment.isAdded() && !firstRun) {
                    sortElementDialogFragment.show(getChildFragmentManager(), "Sort by Element");
                }
                break;
            case 3:
                if (sortTypeDialogFragment == null) {
                    sortTypeDialogFragment = SortTypeDialogFragment.newInstance(sortByType);
                }
                if (!sortTypeDialogFragment.isAdded() && !firstRun) {
                    sortTypeDialogFragment.show(getChildFragmentManager(), "Sort by Type");
                }
                break;
            case 4:
                if (sortStatsDialogFragment == null) {
                    sortStatsDialogFragment = SortStatsDialogFragment.newInstance(sortByStats);
                }
                if (!sortStatsDialogFragment.isAdded() && !firstRun) {
                    sortStatsDialogFragment.show(getChildFragmentManager(), "Sort by Stats");
                }
                break;
            case 5:
                Singleton.getInstance().setBaseSortMethod(sortMethod);
                Collections.sort(monsterList, monsterRarityComparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 6:
                Singleton.getInstance().setBaseSortMethod(sortMethod);
                Collections.sort(monsterList, monsterAwakeningComparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 201:
                Collections.sort(monsterList, monsterElement1Comparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 202:
                Collections.sort(monsterList, monsterElement2Comparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 301:
                Collections.sort(monsterList, monsterType1Comparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 302:
                Collections.sort(monsterList, monsterType2Comparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 303:
                Collections.sort(monsterList, monsterType3Comparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 401:
                Collections.sort(monsterList, monsterHpComparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 402:
                Collections.sort(monsterList, monsterAtkComparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 403:
                Collections.sort(monsterList, monsterRcvComparator);
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
        }
    }

    public void reverseArrayList() {
        switch (Singleton.getInstance().getBaseSortMethod()) {
            case 202:
                element2Reverse();
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 302:
                type2Reverse();
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            case 303:
                type3Reverse();
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;
            default:
                defaultReverse();
                monsterPortraitListRecycler.notifyDataSetChanged();
                break;

        }
    }

    private void defaultReverse() {
        Collections.reverse(monsterList);
    }

    private void element2Reverse() {
        ArrayList<BaseMonster> sorting = new ArrayList<>();
        for (int i = 0; i < monsterList.size(); i++) {
            if (monsterList.get(i).getElement2Int() >= 0) {
                sorting.add(monsterList.get(i));
                monsterList.remove(i);
                i--;
            }
        }
        Collections.reverse(sorting);
        for (int i = 0; i < sorting.size(); i++) {
            monsterList.add(i, sorting.get(i));
        }
        if (monsterList.contains(realm.where(BaseMonster.class).equalTo("monsterId", 0).findFirst())) {
            monsterList.remove(realm.where(BaseMonster.class).equalTo("monsterId", 0).findFirst());
            monsterList.add(0, realm.where(BaseMonster.class).equalTo("monsterId", 0).findFirst());
        }
    }

    private void type2Reverse() {
        ArrayList<BaseMonster> sorting = new ArrayList<>();
        for (int i = 0; i < monsterList.size(); i++) {
            if (monsterList.get(i).getType2() >= 0) {
                sorting.add(monsterList.get(i));
                monsterList.remove(i);
                i--;
            }
        }
        Collections.reverse(sorting);
        for (int i = 0; i < sorting.size(); i++) {
            monsterList.add(i, sorting.get(i));
        }
        if (monsterList.contains(realm.where(BaseMonster.class).equalTo("monsterId", 0).findFirst())) {
            monsterList.remove(realm.where(BaseMonster.class).equalTo("monsterId", 0).findFirst());
            monsterList.add(0, realm.where(BaseMonster.class).equalTo("monsterId", 0).findFirst());
        }
    }

    private void type3Reverse() {
        ArrayList<BaseMonster> sorting = new ArrayList<>();
        for (int i = 0; i < monsterList.size(); i++) {
            if (monsterList.get(i).getType3() >= 0) {
                sorting.add(monsterList.get(i));
                monsterList.remove(i);
                i--;
            }
        }
        Collections.reverse(sorting);
        for (int i = 0; i < sorting.size(); i++) {
            monsterList.add(i, sorting.get(i));
        }
        if (monsterList.contains(realm.where(BaseMonster.class).equalTo("monsterId", 0).findFirst())) {
            monsterList.remove(realm.where(BaseMonster.class).equalTo("monsterId", 0).findFirst());
            monsterList.add(0, realm.where(BaseMonster.class).equalTo("monsterId", 0).findFirst());
        }
    }

    public void searchFilter(String query) {
        if (monsterPortraitListRecycler != null) {
            if (query != null && query.length() > 0) {
                if (!monsterList.isEmpty()) {
                    monsterList.clear();
                }
                if(query.length() == 1){
                    filterMonsters(query);
                } else {
                    RealmResults<BaseMonster> results;

                        results = realm.where(BaseMonster.class)
                                .beginGroup()
                                .contains("name", query, Case.INSENSITIVE)
                                .or()
                                .contains("type1String", query, Case.INSENSITIVE)
                                .or()
                                .contains("type2String", query, Case.INSENSITIVE)
                                .or()
                                .contains("type3String", query, Case.INSENSITIVE)
                                .or()
                                .contains("monsterIdString", query, Case.INSENSITIVE)
                                .or()
                                .contains("activeSkillString", query, Case.INSENSITIVE)
                                .or()
                                .contains("leaderSkillString", query, Case.INSENSITIVE)
                                .endGroup()
                                .greaterThan("monsterId", 0).findAll();

                    monsterList.addAll(results);
                }

            } else {
                monsterList.clear();
                monsterList.addAll(monsterListAll);
            }
            sortArrayList(Singleton.getInstance().getBaseSortMethod());
            firstRun = false;
            if (monsterList.size() == 0) {
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.INVISIBLE);
            }
//            if(fastScroller != null){
//                fastScroller.resizeScrollBar(monsterPortraitListRecycler.expanded(), FastScroller.BASE_MONSTER_LIST);
//            }
        }
    }

    private void filterMonsters(String query) {
        for (BaseMonster monster : monsterListAll) {
            if (monster.getName().toLowerCase().contains(query.toLowerCase())) {
                monsterList.add(monster);
            } else if (String.valueOf(monster.getMonsterId()).toLowerCase().contains(query.toLowerCase())) {
                monsterList.add(monster);
            } else if (monster.getType1String().toLowerCase().contains(query.toLowerCase())) {
                monsterList.add(monster);
            } else if (monster.getType2String().toLowerCase().contains(query.toLowerCase())) {
                monsterList.add(monster);
            } else if (monster.getType3String().toLowerCase().contains(query.toLowerCase())) {
                monsterList.add(monster);
            }
        }
    }

    private FilterDialogFragment.SaveTeam saveTeam = new FilterDialogFragment.SaveTeam() {
        @Override
        public void filter() {
            boolean remove = true;
            if (filteredMonsters.size() > 0) {
                for (int i = 0; i < filteredMonsters.size(); i++) {
                    monsterList.add(filteredMonsters.get(i));
                }
                filteredMonsters.clear();
            }
            Iterator<BaseMonster> iter = monsterList.iterator();
            if (Singleton.getInstance().getFilterElement1().size() != 0 || Singleton.getInstance().getFilterElement2().size() != 0 || Singleton.getInstance().getFilterTypes().size() != 0 || Singleton.getInstance().getFilterAwakenings().size() != 0 || Singleton.getInstance().getFilterLatents().size() != 0) {
                while (iter.hasNext()) {
                    BaseMonster monster = iter.next();
                    if (Singleton.getInstance().getFilterElement1().size() != 0) {
                        if (Singleton.getInstance().getFilterElement1().contains(monster.getElement1())) {
                            remove = false;
                        }
                    }
                    if (Singleton.getInstance().getFilterElement2().size() != 0 && remove) {
                        if (Singleton.getInstance().getFilterElement2().contains(monster.getElement2())) {
                            remove = false;
                        }
                    }
                    if (Singleton.getInstance().getFilterTypes().size() != 0 && remove) {
                        for (int i = 0; i < monster.getTypes().size(); i++) {
                            if (Singleton.getInstance().getFilterTypes().contains(monster.getTypes().get(i)) && remove) {
                                remove = false;
                            }
                        }
                    }
                    if (Singleton.getInstance().getFilterAwakenings().size() != 0 && remove) {
                        for (int i = 0; i < monster.getAwokenSkills().size(); i++) {
                            if (Singleton.getInstance().getFilterAwakenings().contains(monster.getAwokenSkills(i).getValue()) && remove) {
                                remove = false;
                            }
                        }
                    }
                    if (remove) {
                        filteredMonsters.add(monster);
                        iter.remove();
                    }
                    remove = true;
                }
            }
            sortArrayList(Singleton.getInstance().getBaseSortMethod());
            monsterPortraitListRecycler.notifyDataSetChanged();
            if (monsterList.size() == 0) {
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void filterRequirements() {
            boolean match = true;
            int counter = 0;
            if (filteredMonsters.size() > 0) {
                for (int i = 0; i < filteredMonsters.size(); i++) {
                    monsterList.add(filteredMonsters.get(i));
                }
                filteredMonsters.clear();
            }
            Iterator<BaseMonster> iter = monsterList.iterator();
            if (Singleton.getInstance().getFilterElement1().size() != 0 || Singleton.getInstance().getFilterElement2().size() != 0 || Singleton.getInstance().getFilterTypes().size() != 0 || Singleton.getInstance().getFilterAwakenings().size() != 0 || Singleton.getInstance().getFilterLatents().size() != 0) {
                while (iter.hasNext()) {
                    BaseMonster monster = iter.next();
                    if (Singleton.getInstance().getFilterElement1().size() != 0) {
                        if (!Singleton.getInstance().getFilterElement1().contains(monster.getElement1())) {
                            match = false;
                        }
                    }
                    if (Singleton.getInstance().getFilterElement2().size() != 0 && match) {
                        if (!Singleton.getInstance().getFilterElement2().contains(monster.getElement2())) {
                            match = false;
                        }
                    }
                    if (Singleton.getInstance().getFilterTypes().size() != 0 && match) {
                        for (int i = 0; i < monster.getTypes().size(); i++) {
                            if (Singleton.getInstance().getFilterTypes().contains(monster.getTypes().get(i))) {
                                counter++;
                            }
                        }
                        if (counter != Singleton.getInstance().getFilterTypes().size()) {
                            match = false;
                        }
                        counter = 0;
                    }
                    if (Singleton.getInstance().getFilterAwakenings().size() != 0 && match) {
                        ArrayList<Integer> trimmedAwakenings = new ArrayList<>();
                        for (int i = 0; i < monster.getAwokenSkills().size(); i++) {
                            if (!trimmedAwakenings.contains(monster.getAwokenSkills(i).getValue())) {
                                trimmedAwakenings.add(monster.getAwokenSkills(i).getValue());
                            }
                        }
                        for (int i = 0; i < trimmedAwakenings.size(); i++) {
                            if (Singleton.getInstance().getFilterAwakenings().contains(trimmedAwakenings.get(i))) {
                                counter++;
                            }
                        }
                        if (counter != Singleton.getInstance().getFilterAwakenings().size()) {
                            match = false;
                        }
                        counter = 0;
                    }
                    if (!match) {
                        filteredMonsters.add(monster);
                        iter.remove();
                    }
                    match = true;
                }
            }
            sortArrayList(Singleton.getInstance().getBaseSortMethod());
            monsterPortraitListRecycler.notifyDataSetChanged();
            if (monsterList.size() == 0) {
                noResults.setVisibility(View.VISIBLE);
            } else {
                noResults.setVisibility(View.INVISIBLE);
            }
        }
    };
}

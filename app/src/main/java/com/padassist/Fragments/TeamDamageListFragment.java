package com.padassist.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.padassist.Adapters.MonsterDamageListRecycler;
import com.padassist.BroadcastReceivers.JustAnotherBroadcastReceiver;
import com.padassist.Data.Element;
import com.padassist.Data.Enemy;
import com.padassist.Data.Monster;
import com.padassist.Data.OrbMatch;
import com.padassist.Data.RealmElement;
import com.padassist.Data.Team;
import com.padassist.Graphics.TooltipText;
import com.padassist.R;
import com.padassist.TextWatcher.MyTextWatcher;
import com.padassist.Util.DamageCalculationUtil;
import com.padassist.Util.Singleton;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TeamDamageListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TeamDamageListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TeamDamageListFragment extends Fragment {
    public static final String TAG = MonsterPageFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;
    private RecyclerView monsterListView;
    private EditText additionalComboValue, damageThresholdValue, damageImmunityValue, reductionValue;
    private ImageView damageThreshold, damageImmunity, targetReduction, targetAbsorb, hasAwakenings, activeUsed, hasLeaderSkill;
    private RelativeLayout enemyHPHolder, enemyAttributeHolder, teamAttributeHolder;
    private MonsterDamageListRecycler monsterListAdapter;
    private ArrayList<Long> monsterElement1Damage;
    private ArrayList<Long> monsterElement2Damage;
    private ArrayList<Long> monsterElement1DamageEnemy;
    private ArrayList<Long> monsterElement2DamageEnemy;
    private ArrayList<Long> monsterElement1DamageEffective;
    private ArrayList<Long> monsterElement2DamageEffective;
    private Enemy enemy;
    private Team team;
    private Toast toast;
    private boolean hasEnemy;
    //private ArrayList<Monster> monsterList;
    private int additionalCombos, totalCombos = 0;
    private long totalDamage = 0, temp = 0, tempCurrent = 0;
    private TextView enemyHP, enemyHPValue, enemyHPPercent, enemyHPPercentValue, totalDamageValue, hpRecoveredValue, teamHpValue, reductionPercent;
    private RadioGroup reductionRadioGroup;
    private Button monsterListToggle;
    private CheckBox redOrbReduction, blueOrbReduction, greenOrbReduction, lightOrbReduction, darkOrbReduction, redOrbAbsorb, blueOrbAbsorb, greenOrbAbsorb, lightOrbAbsorb, darkOrbAbsorb;
    private CheckBox absorbCheck, reductionCheck, damageThresholdCheck, damageImmunityCheck, hasAwakeningsCheck, activeUsedCheck, hasLeaderSkillCheck1, hasLeaderSkillCheck2;
    private RadioGroup absorbRadioGroup;
    private Button optionButton;
    private SeekBar teamHp;
    private DecimalFormat df = new DecimalFormat("#.##");
    private DecimalFormatSymbols dfs = new DecimalFormatSymbols();
    private DecimalFormat dfSpace;
    private ExtraMultiplierDialogFragment extraMultiplierDialogFragment;
    private Realm realm;
    private JustAnotherBroadcastReceiver broadcastReceiver;
    private RealmResults<OrbMatch> orbMatchList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TeamDamageListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TeamDamageListFragment newInstance(String param1, String param2) {
        TeamDamageListFragment fragment = new TeamDamageListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static TeamDamageListFragment newInstance(Parcelable team, Parcelable enemy) {
        TeamDamageListFragment fragment = new TeamDamageListFragment();
        Bundle args = new Bundle();
        args.putParcelable("team", team);
        args.putParcelable("enemy", enemy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onDeselect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public TeamDamageListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.setGroupVisible(R.id.teamDamage, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                onSelect();
                break;
            case R.id.toggleCoop:
                onSelect();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_team_damage_list, container, false);
        monsterListView = (RecyclerView) rootView.findViewById(R.id.monsterListView);
        additionalComboValue = (EditText) rootView.findViewById(R.id.additionalComboValue);
        monsterListToggle = (Button) rootView.findViewById(R.id.monsterListToggle);
        enemyHP = (TextView) rootView.findViewById(R.id.enemyHP);
        enemyHPValue = (TextView) rootView.findViewById(R.id.enemyHPValue);
        enemyHPPercent = (TextView) rootView.findViewById(R.id.enemyHPPercent);
        enemyHPPercentValue = (TextView) rootView.findViewById(R.id.enemyHPPercentValue);
        totalDamageValue = (TextView) rootView.findViewById(R.id.totalDamageValue);
        hpRecoveredValue = (TextView) rootView.findViewById(R.id.hpRecoveredValue);
        targetReduction = (ImageView) rootView.findViewById(R.id.targetReduction);
        targetAbsorb = (ImageView) rootView.findViewById(R.id.elementAbsorb);
        hasAwakenings = (ImageView) rootView.findViewById(R.id.hasAwakenings);
        optionButton = (Button) rootView.findViewById(R.id.options);
        reductionRadioGroup = (RadioGroup) rootView.findViewById(R.id.reductionOrbRadioGroup);
        absorbRadioGroup = (RadioGroup) rootView.findViewById(R.id.absorbOrbRadioGroup);
        redOrbReduction = (CheckBox) rootView.findViewById(R.id.redOrbReduction);
        blueOrbReduction = (CheckBox) rootView.findViewById(R.id.blueOrbReduction);
        greenOrbReduction = (CheckBox) rootView.findViewById(R.id.greenOrbReduction);
        darkOrbReduction = (CheckBox) rootView.findViewById(R.id.darkOrbReduction);
        lightOrbReduction = (CheckBox) rootView.findViewById(R.id.lightOrbReduction);
        redOrbAbsorb = (CheckBox) rootView.findViewById(R.id.redOrbAbsorb);
        blueOrbAbsorb = (CheckBox) rootView.findViewById(R.id.blueOrbAbsorb);
        greenOrbAbsorb = (CheckBox) rootView.findViewById(R.id.greenOrbAbsorb);
        lightOrbAbsorb = (CheckBox) rootView.findViewById(R.id.lightOrbAbsorb);
        darkOrbAbsorb = (CheckBox) rootView.findViewById(R.id.darkOrbAbsorb);
        reductionCheck = (CheckBox) rootView.findViewById(R.id.reductionCheck);
        damageThresholdValue = (EditText) rootView.findViewById(R.id.damageThresholdValue);
        damageThresholdCheck = (CheckBox) rootView.findViewById(R.id.damageThresholdCheck);
        damageThreshold = (ImageView) rootView.findViewById(R.id.damageThreshold);
        absorbCheck = (CheckBox) rootView.findViewById(R.id.absorbCheck);
        hasAwakeningsCheck = (CheckBox) rootView.findViewById(R.id.hasAwakeningsCheck);
        activeUsed = (ImageView) rootView.findViewById(R.id.activeUsed);
        activeUsedCheck = (CheckBox) rootView.findViewById(R.id.activeUsedCheck);
        teamHp = (SeekBar) rootView.findViewById(R.id.teamHpSeekBar);
        teamHpValue = (TextView) rootView.findViewById(R.id.teamHpValue);
        reductionValue = (EditText) rootView.findViewById(R.id.reductionValue);
        damageImmunityValue = (EditText) rootView.findViewById(R.id.damageImmunityValue);
        damageImmunityCheck = (CheckBox) rootView.findViewById(R.id.damageImmunityCheck);
        damageImmunity = (ImageView) rootView.findViewById(R.id.damageImmunity);
        reductionPercent = (TextView) rootView.findViewById(R.id.reductionPercent);
        hasLeaderSkill = (ImageView) rootView.findViewById(R.id.hasLeaderSkill);
        hasLeaderSkillCheck1 = (CheckBox) rootView.findViewById(R.id.hasLeaderSkillCheck1);
        hasLeaderSkillCheck2 = (CheckBox) rootView.findViewById(R.id.hasLeaderSkillCheck2);
        enemyHPHolder = (RelativeLayout) rootView.findViewById(R.id.enemyHPHolder);
        enemyAttributeHolder = (RelativeLayout) rootView.findViewById(R.id.enemyAttributeHolder);
        teamAttributeHolder = (RelativeLayout) rootView.findViewById(R.id.teamAttributeHolder);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            team = Parcels.unwrap(getArguments().getParcelable("team"));
//            enemy = Parcels.unwrap(getArguments().getParcelable("enemy"));
        }
        realm = Realm.getDefaultInstance();
        hasEnemy = Singleton.getInstance().hasEnemy();
//        enemy = realm.copyFromRealm(realm.where(Enemy.class).equalTo("enemyId", 0).findFirst());
        enemy = realm.where(Enemy.class).equalTo("enemyId", 0).findFirst();
        orbMatchList = realm.where(OrbMatch.class).findAllSorted("matchId");
        if (hasEnemy) {
            temp = enemy.getCurrentHp();
            tempCurrent = enemy.getCurrentHp();
            setReductionOrbs();
            setAbsorbOrbs();
            setDamageThreshold();
        }
        dfs.setGroupingSeparator(' ');
        dfSpace = new DecimalFormat("###,###", dfs);
        setCheckBoxes();

        additionalCombos = Singleton.getInstance().getAdditionalCombos();
        totalCombos = additionalCombos + realm.where(OrbMatch.class).findAll().size();
        additionalComboValue.setText("" + totalCombos);

        monsterElement1Damage = new ArrayList<>();
        monsterElement2Damage = new ArrayList<>();
        monsterElement1DamageEnemy = new ArrayList<>();
        monsterElement2DamageEnemy = new ArrayList<>();
        monsterElement1DamageEffective = new ArrayList<>();
        monsterElement2DamageEffective = new ArrayList<>();

        monsterListAdapter = new MonsterDamageListRecycler(getActivity(), hasEnemy, team,
                monsterElement1Damage, monsterElement2Damage, monsterElement1DamageEnemy,
                monsterElement2DamageEnemy, monsterElement1DamageEffective, monsterElement2DamageEffective, bindMonsterOnClickListener);
        monsterListView.setAdapter(monsterListAdapter);
        monsterListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        monsterListToggle.setOnClickListener(monsterListToggleOnClickListener);
        additionalComboValue.addTextChangedListener(totalComboTextWatcher);
        additionalComboValue.setOnFocusChangeListener(editTextOnFocusChange);
//        monsterListView.setOnItemClickListener(bindMonsterOnClickListener);
        optionButton.setOnClickListener(optionsButtonOnClickListener);
//        absorbCheck.setOnCheckedChangeListener(checkBoxOnChangeListener);
//        reductionCheck.setOnCheckedChangeListener(checkBoxOnChangeListener);
//        damageThresholdCheck.setOnCheckedChangeListener(checkBoxOnChangeListener);
//        damageThresholdValue.addTextChangedListener(damageThresholdWatcher);
//        damageThresholdValue.setOnFocusChangeListener(editTextOnFocusChange);
//        damageImmunityCheck.setOnCheckedChangeListener(checkBoxOnChangeListener);
//        damageImmunityValue.addTextChangedListener(damageImmunityWatcher);
//        damageImmunityValue.setOnFocusChangeListener(editTextOnFocusChange);
//        reductionValue.setOnFocusChangeListener(editTextOnFocusChange);
//        reductionValue.addTextChangedListener(reductionWatcher);
//
//        redOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
//        blueOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
//        greenOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
//        darkOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
//        lightOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
//
//        redOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);
//        blueOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);
//        greenOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);
//        darkOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);
//        lightOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);

        hasAwakeningsCheck.setOnCheckedChangeListener(checkBoxOnChangeListener);
        activeUsedCheck.setOnCheckedChangeListener(checkBoxOnChangeListener);
        hasLeaderSkillCheck1.setOnCheckedChangeListener(checkBoxOnChangeListener);
        hasLeaderSkillCheck2.setOnCheckedChangeListener(checkBoxOnChangeListener);
        teamHp.setOnSeekBarChangeListener(teamHpSeekBarListener);
//        if (hasEnemy) {
//            getActivity().setTitle("Team Damage (with target)");
//        } else {
//            getActivity().setTitle("Team Damage (no target)");
//        }

        targetAbsorb.setOnClickListener(tooltipOnClickListener);
        targetReduction.setOnClickListener(tooltipOnClickListener);
        damageThreshold.setOnClickListener(tooltipOnClickListener);
        damageImmunity.setOnClickListener(tooltipOnClickListener);
        hasAwakenings.setOnClickListener(tooltipOnClickListener);
        activeUsed.setOnClickListener(tooltipOnClickListener);
        hasLeaderSkill.setOnClickListener(tooltipOnClickListener);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private View.OnClickListener monsterListToggleOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (monsterListView.getVisibility() == View.GONE) {
                monsterListView.setVisibility(View.VISIBLE);
                monsterListToggle.setText("Hide Damage Breakdown");
            } else if (monsterListView.getVisibility() == View.VISIBLE) {
                monsterListView.setVisibility(View.GONE);
                monsterListToggle.setText("Show Damage Breakdown");
            }
        }
    };

    public void calculateDamage() {
        totalDamage = 0;
        monsterElement1Damage.clear();
        monsterElement2Damage.clear();
        Log.d("TeamDamageListFrag", "Enemy element is: " + enemy.getCurrentElement());
        monsterElement1DamageEnemy.clear();
        monsterElement2DamageEnemy.clear();
        monsterElement1DamageEffective.clear();
        monsterElement2DamageEffective.clear();
        for (int i = 0; i < team.getMonsters().size(); i++) {
            monsterElement1Damage.add(0L);
            monsterElement2Damage.add(0L);
            monsterElement1DamageEnemy.add(0L);
            monsterElement2DamageEnemy.add(0L);
            monsterElement1Damage.set(i, (long) DamageCalculationUtil.monsterElement1Damage(team, orbMatchList, team.getMonsters(i), i, totalCombos));
            monsterElement2Damage.set(i, (long) DamageCalculationUtil.monsterElement2Damage(team, orbMatchList, team.getMonsters(i), i, totalCombos));
            if (hasEnemy) {
                monsterElement1DamageEffective.add(0L);
                monsterElement2DamageEffective.add(0L);
                if (enemy.isHasDamageThreshold()) {
                    monsterElement1DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement1DamageThreshold(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                    monsterElement2DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement2DamageThreshold(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                } else if (enemy.hasDamageImmunity()) {
                    monsterElement1DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement1DamageImmunity(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                    monsterElement2DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement2DamageImmunity(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                } else if (enemy.isHasAbsorb()) {
                    monsterElement1DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement1DamageAbsorb(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                    monsterElement2DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement2DamageAbsorb(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                } else if (enemy.isHasReduction()) {
                    monsterElement1DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement1DamageReduction(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                    monsterElement2DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement2DamageReduction(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                } else {
                    monsterElement1DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement1DamageEnemy(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                    monsterElement2DamageEnemy.set(i, (long) DamageCalculationUtil.monsterElement2DamageEnemy(team, orbMatchList, team.getMonsters(i), i, totalCombos, enemy));
                }
            } else {
                monsterElement1DamageEffective.addAll(monsterElement1Damage);
                monsterElement2DamageEffective.addAll(monsterElement2Damage);
            }
        }

//            Effective Damage calculations
        if (hasEnemy) {
            for (int i = 0; i < monsterElement1DamageEffective.size(); i++) {
                if (!team.getIsBound().get(i)) {
                    temp -= monsterElement1DamageEnemy.get(i);
                    if (temp > enemy.getTargetHp()) {
                        for (int j = 0; j <= i; j++) {
                            monsterElement1DamageEffective.set(j, 0L);
                        }
                        temp = enemy.getTargetHp();
                        totalDamage = 0;
                    } else {
                        totalDamage += monsterElement1DamageEnemy.get(i);
                        monsterElement1DamageEffective.set(i, monsterElement1DamageEnemy.get(i));
                    }
                } else {
                    monsterElement1DamageEffective.set(i, 0L);
                }
            }
            for (int i = 0; i < monsterElement2DamageEffective.size(); i++) {
                if (!team.getIsBound().get(i)) {
                    temp -= monsterElement2DamageEnemy.get(i);
                    if (temp > enemy.getTargetHp()) {
                        for (int j = 0; j <= i; j++) {
                            monsterElement2DamageEffective.set(j, 0L);
                        }
                        for (int j = 0; j < monsterElement1DamageEffective.size(); j++) {
                            monsterElement1DamageEffective.set(j, 0L);
                        }
                        temp = enemy.getTargetHp();
                        totalDamage = 0;
                    } else {
                        totalDamage += monsterElement2DamageEnemy.get(i);
                        monsterElement2DamageEffective.set(i, monsterElement2DamageEnemy.get(i));
                    }
                } else {
                    monsterElement2DamageEffective.set(i, 0L);
                }

            }
        } else {
            for (int i = 0; i < monsterElement1Damage.size(); i++){
                if(!team.getIsBound().get(i)){
                    totalDamage += monsterElement1Damage.get(i);
                    monsterElement1DamageEffective.set(i, monsterElement1Damage.get(i));
                    totalDamage += monsterElement2Damage.get(i);
                    monsterElement2DamageEffective.set(i, monsterElement2Damage.get(i));
                } else {
                    monsterElement1DamageEffective.set(i, 0L);
                    monsterElement2DamageEffective.set(i, 0L);
                }
            }
        }

        team.setTotalDamage(totalDamage);
        monsterListAdapter.notifyDataSetChanged();
        if(hasEnemy){
            realm.beginTransaction();
            enemy.setCurrentHp(tempCurrent - totalDamage);
            enemy.setDamaged(true);
            if ((double) enemy.getCurrentHp() / (double) enemy.getTargetHp() > .5) {
                enemy.setCurrentElement(enemy.getTargetElement().get(0));
            } else {
                if (enemy.getTargetElement().get(1).getValue() > -1) {
                    enemy.setCurrentElement(enemy.getTargetElement().get(1));
                }
            }
            realm.commitTransaction();
            Log.d("TeamDamageListFrag", "Enemy after element is: " + enemy.getCurrentElement());
        }
    }


    public void updateTextView() {
        if (!hasEnemy) {
            enemyHPHolder.setVisibility(View.GONE);
            enemyAttributeHolder.setVisibility(View.GONE);

//            for (int i = 0; i < team.sizeMonsters(); i++) {
//                if (team.getIsBound().get(i)) {
//                } else {
//                    totalDamage += (long) DamageCalculationUtil.monsterElement1Damage(team, orbMatchList, team.getMonsters(i), i, totalCombos);
//                    totalDamage += (long) DamageCalculationUtil.monsterElement2Damage(team, orbMatchList, team.getMonsters(i), i, totalCombos);
//                }
//            }
        } else {
            enemyHPHolder.setVisibility(View.VISIBLE);
            enemyAttributeHolder.setVisibility(View.GONE);


            //Need to set colors of each enemy element stuff

//            enemy.setCurrentHp(enemy.getCurrentHp() - totalDamage);
//            if (enemy.getCurrentHp() != temp) {
//                enemy.setBeforeGravityHP(enemy.getCurrentHp());
//                enemy.setDamaged(true);
//            }
//            if (totalDamage == 0 && enemy.getCurrentHp() == enemy.getTargetHp()) {
//                enemy.setBeforeGravityHP(enemy.getCurrentHp());
//            }
            enemyHPValue.setText(" " + dfSpace.format(enemy.getCurrentHp()) + " ");
            enemyHPPercentValue.setText(String.valueOf(df.format((double) enemy.getCurrentHp() / enemy.getTargetHp() * 100) + "%"));
            setTextColors();
        }
        totalDamageValue.setText(" " + dfSpace.format(totalDamage) + " ");
        hpRecoveredValue.setText(" " + dfSpace.format((int) DamageCalculationUtil.hpRecovered(team, orbMatchList, totalCombos)) + " ");
        additionalComboValue.setText(String.valueOf(totalCombos));
        if (totalDamage < 0) {
            totalDamageValue.setTextColor(Color.parseColor("#FFBBBB"));
        } else {
            totalDamageValue.setTextColor(Color.parseColor("#BBBBBB"));
        }
        if (DamageCalculationUtil.hpRecovered(team, orbMatchList, totalCombos) < 0) {
            hpRecoveredValue.setTextColor(Color.parseColor("#c737fd"));
        } else {
            hpRecoveredValue.setTextColor(Color.parseColor("#FF9999"));
        }
    }

    private View.OnClickListener bindMonsterOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.string.index);
            clearTextFocus();
            if (position == 0 && team.getTeamBadge() == 8) {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getActivity(), "Team Badge immunity", Toast.LENGTH_SHORT);
                toast.show();
            } else if (team.getIsBound().get(position)) {
                team.getIsBound().set(position, false);
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(getActivity(), "Monster unbound", Toast.LENGTH_SHORT);
                toast.show();
            } else {

                int counter = 0;
                if (Singleton.getInstance().hasAwakenings()) {
                    for (int i = 0; i < team.getMonsters(position).getCurrentAwakenings(); i++) {
                        if (team.getMonsters(position).getAwokenSkills().get(i).getValue() == 10) {
                            counter++;
                        }
                    }
                }
                if (counter == 2) {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(getActivity(), "Monster cannot be bound", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    team.getIsBound().set(position, true);
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(getActivity(), "Monster bound", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            team.updateAwakenings();
            team.updateOrbs(orbMatchList);
            team.setAtkMultiplierArrays(orbMatchList, totalCombos);
            calculateDamage();
            updateTextView();
            monsterListAdapter.notifyDataSetChanged();
        }
    };

    private void setTextColors() {
        Element currentElement = enemy.getTargetElement().get(0).getElement();
        if ((double) enemy.getCurrentHp() / (double) enemy.getTargetHp() < .5) {
            if (enemy.getTargetElement().get(1).getValue() > -1) {
                currentElement = enemy.getTargetElement().get(1).getElement();
            }
        }

        switch (currentElement) {
            case RED:
                enemyHPValue.setTextColor(Color.parseColor("#FF0000"));
                break;
            case BLUE:
                enemyHPValue.setTextColor(Color.parseColor("#4444FF"));
                break;
            case GREEN:
                enemyHPValue.setTextColor(Color.parseColor("#00CC00"));
                break;
            case LIGHT:
                enemyHPValue.setTextColor(Color.parseColor("#F0F000"));
                break;
            case DARK:
                enemyHPValue.setTextColor(Color.parseColor("#AA00FF"));
                break;
            default:
                enemyHPValue.setTextColor(Color.parseColor("#FFFFFF"));
                break;
        }
    }

    private MyTextWatcher.ChangeStats changeStats = new MyTextWatcher.ChangeStats() {
        @Override
        public void changeMonsterAttribute(int statToChange, int statValue) {
            if (statToChange == MyTextWatcher.DAMAGE_THRESHOLD) {
                enemy.setDamageThreshold(statValue);
            } else if (statToChange == MyTextWatcher.DAMAGE_IMMUNITY) {
                enemy.setDamageImmunity(statValue);
            } else if (statToChange == MyTextWatcher.REDUCTION_VALUE) {
                enemy.setReductionValue(statValue);
                if (statValue > 100) {
                    enemy.setReductionValue(100);
                    reductionValue.setText("100");
                }
            }
        }
    };

    private MyTextWatcher damageThresholdWatcher = new MyTextWatcher(MyTextWatcher.DAMAGE_THRESHOLD, changeStats);
    private MyTextWatcher damageImmunityWatcher = new MyTextWatcher(MyTextWatcher.DAMAGE_IMMUNITY, changeStats);
    private MyTextWatcher reductionWatcher = new MyTextWatcher(MyTextWatcher.REDUCTION_VALUE, changeStats);

    private TextWatcher totalComboTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals("")) {
                totalCombos = Integer.valueOf(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnFocusChangeListener editTextOnFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
                if ((additionalComboValue.getText().toString().equals(""))) {
                    additionalComboValue.setText("" + orbMatchList.size());
                } else if (Integer.valueOf(additionalComboValue.getText().toString()) < orbMatchList.size()) {
                    additionalComboValue.setText("" + orbMatchList.size());
                } else if (damageThresholdValue.getText().toString().equals("")) {
                    damageThresholdValue.setText("0");
                    enemy.setDamageThreshold(0);
                }
                if(v.equals(additionalComboValue)){
                    calculateDamage();
                    updateTextView();
                }
            }
        }
    };

    private Button.OnClickListener optionsButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearTextFocus();
            if (extraMultiplierDialogFragment == null) {
                extraMultiplierDialogFragment = extraMultiplierDialogFragment.newInstance(saveTeam);
            }
            if (!extraMultiplierDialogFragment.isAdded()) {
                extraMultiplierDialogFragment.show(getChildFragmentManager(), team, "Show extra multiplier Dialog");
            }
        }
    };

    ExtraMultiplierDialogFragment.SaveTeam saveTeam = new ExtraMultiplierDialogFragment.SaveTeam() {
        @Override
        public void update() {
            hasEnemy = Singleton.getInstance().hasEnemy();
            calculateDamage();
            updateTextView();
            monsterListAdapter.setHasEnemy(hasEnemy);
            monsterListAdapter.notifyDataSetChanged();
        }
    };

    public void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setReductionOrbs() {
        reductionValue.setText(String.valueOf(enemy.getReductionValue()));
        if (enemy.isHasReduction()) {
            reductionCheck.setChecked(true);
            for (int i = 0; i < reductionRadioGroup.getChildCount(); i++) {
                reductionRadioGroup.getChildAt(i).setEnabled(true);
            }
            if (enemy.reductionContains(Element.RED)) {
                redOrbReduction.setChecked(true);
            } else {
                redOrbReduction.setChecked(false);
            }
            if (enemy.reductionContains(Element.BLUE)) {
                blueOrbReduction.setChecked(true);
            } else {
                blueOrbReduction.setChecked(false);
            }
            if (enemy.reductionContains(Element.GREEN)) {
                greenOrbReduction.setChecked(true);
            } else {
                greenOrbReduction.setChecked(false);
            }
            if (enemy.reductionContains(Element.DARK)) {
                darkOrbReduction.setChecked(true);
            } else {
                darkOrbReduction.setChecked(false);
            }
            if (enemy.reductionContains(Element.LIGHT)) {
                lightOrbReduction.setChecked(true);
            } else {
                lightOrbReduction.setChecked(false);
            }
        } else {
            reductionValue.setEnabled(false);
            reductionCheck.setChecked(false);
//            clearReduction();
        }
    }

    private void clearReduction() {
        redOrbReduction.setOnCheckedChangeListener(null);
        blueOrbReduction.setOnCheckedChangeListener(null);
        greenOrbReduction.setOnCheckedChangeListener(null);
        darkOrbReduction.setOnCheckedChangeListener(null);
        lightOrbReduction.setOnCheckedChangeListener(null);
        redOrbReduction.setChecked(false);
        blueOrbReduction.setChecked(false);
        greenOrbReduction.setChecked(false);
        lightOrbReduction.setChecked(false);
        darkOrbReduction.setChecked(false);
        redOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
        blueOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
        greenOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
        darkOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
        lightOrbReduction.setOnCheckedChangeListener(reductionCheckedChangedListener);
        for (int i = 0; i < reductionRadioGroup.getChildCount(); i++) {
            reductionRadioGroup.getChildAt(i).setEnabled(false);
        }
    }

    private void setAbsorbOrbs() {
        if (enemy.isHasAbsorb()) {
            absorbCheck.setChecked(true);
            for (int i = 0; i < absorbRadioGroup.getChildCount(); i++) {
                absorbRadioGroup.getChildAt(i).setEnabled(true);
            }
            if (enemy.absorbContains(Element.RED)) {
                redOrbAbsorb.setChecked(true);
            } else {
                redOrbAbsorb.setChecked(false);
            }
            if (enemy.absorbContains(Element.BLUE)) {
                blueOrbAbsorb.setChecked(true);
            } else {
                blueOrbAbsorb.setChecked(false);
            }
            if (enemy.absorbContains(Element.GREEN)) {
                greenOrbAbsorb.setChecked(true);
            } else {
                greenOrbAbsorb.setChecked(false);
            }
            if (enemy.absorbContains(Element.DARK)) {
                darkOrbAbsorb.setChecked(true);
            } else {
                darkOrbAbsorb.setChecked(false);
            }
            if (enemy.absorbContains(Element.LIGHT)) {
                lightOrbAbsorb.setChecked(true);
            } else {
                lightOrbAbsorb.setChecked(false);
            }

        } else {
            absorbCheck.setChecked(false);
//            clearAbsorb();
        }
    }

    private void clearAbsorb() {
        redOrbAbsorb.setOnCheckedChangeListener(null);
        blueOrbAbsorb.setOnCheckedChangeListener(null);
        greenOrbAbsorb.setOnCheckedChangeListener(null);
        lightOrbAbsorb.setOnCheckedChangeListener(null);
        darkOrbAbsorb.setOnCheckedChangeListener(null);
        redOrbAbsorb.setChecked(false);
        blueOrbAbsorb.setChecked(false);
        greenOrbAbsorb.setChecked(false);
        lightOrbAbsorb.setChecked(false);
        darkOrbAbsorb.setChecked(false);
        redOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);
        blueOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);
        greenOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);
        darkOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);
        lightOrbAbsorb.setOnCheckedChangeListener(absorbCheckedChangedListener);
        for (int i = 0; i < absorbRadioGroup.getChildCount(); i++) {
            absorbRadioGroup.getChildAt(i).setEnabled(false);
        }
    }

    private void setDamageThreshold() {
        damageThresholdValue.setText(String.valueOf(enemy.getDamageThreshold()));
        damageImmunityValue.setText(String.valueOf(enemy.getDamageImmunity()));
        if (enemy.isHasDamageThreshold()) {
            damageThresholdValue.setEnabled(true);
            damageThresholdCheck.setChecked(true);
        }
        if (enemy.hasDamageImmunity()) {
            damageImmunityCheck.setChecked(true);
            damageImmunityValue.setEnabled(true);
        }
    }

    private CompoundButton.OnCheckedChangeListener checkBoxOnChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            clearTextFocus();
            if (buttonView.equals(damageThresholdCheck)) {
                enemy.setHasDamageThreshold(isChecked);
                if (isChecked) {
                    damageThresholdValue.setEnabled(true);
                    damageImmunityValue.setEnabled(false);
                    damageImmunityCheck.setChecked(false);
                } else {
                    damageThresholdValue.setEnabled(false);
                }
            } else if (buttonView.equals(damageImmunityCheck)) {
                enemy.setHasDamageImmunity(isChecked);
                if (isChecked) {
                    damageImmunityValue.setEnabled(true);
                    damageThresholdValue.setEnabled(false);
                    damageThresholdCheck.setChecked(false);
                } else {
                    damageImmunityValue.setEnabled(false);
                }
            } else if (buttonView.equals(reductionCheck)) {
                enemy.setHasReduction(isChecked);
                setReductionOrbs();
            } else if (buttonView.equals(absorbCheck)) {
                enemy.setHasAbsorb(isChecked);
                setAbsorbOrbs();
            } else if (buttonView.equals(hasAwakeningsCheck)) {
                Singleton.getInstance().setHasAwakenings(!isChecked);
                team.updateAwakenings();
                team.setAtkMultiplierArrays(orbMatchList, totalCombos);
                calculateDamage();
                updateTextView();
            } else if (buttonView.equals(activeUsedCheck)) {
                Singleton.getInstance().setActiveSkillUsed(isChecked);
                team.updateAwakenings();
                team.setAtkMultiplierArrays(orbMatchList, totalCombos);
                calculateDamage();
                updateTextView();
            } else if (buttonView.equals(hasLeaderSkillCheck1)) {
                Singleton.getInstance().setHasLeaderSkill(isChecked);
                team.setTeamStats(realm);
                team.setAtkMultiplierArrays(orbMatchList, totalCombos);
                calculateDamage();
                updateTextView();
            } else if (buttonView.equals(hasLeaderSkillCheck2)) {
                Singleton.getInstance().setHasHelperSkill(isChecked);
                team.setTeamStats(realm);
                team.setAtkMultiplierArrays(orbMatchList, totalCombos);
                calculateDamage();
                updateTextView();
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener reductionCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setElementReduction(isChecked, buttonView.getId());
        }
    };

    private void setElementReduction(boolean isChecked, int buttonId) {
        clearTextFocus();
        int element = -1;
        Boolean contains = false;
        switch (buttonId) {
            case R.id.redOrbReduction:
                element = 0;
                break;
            case R.id.blueOrbReduction:
                element = 1;
                break;
            case R.id.greenOrbReduction:
                element = 2;
                break;
            case R.id.lightOrbReduction:
                element = 3;
                break;
            case R.id.darkOrbReduction:
                element = 4;
                break;
        }

        if (isChecked) {
            for (int i = 0; i < enemy.getReduction().size(); i++) {
                if (enemy.getReduction().get(i).getValue() == element && !contains) {
                    contains = true;
                }
            }
            if (element >= 0 && !contains) {
                enemy.getReduction().add(new RealmElement(element));
            }
        } else {
            for (int i = 0; i < enemy.getReduction().size(); i++) {
                if (enemy.getReduction().get(i).getValue() == element) {
                    enemy.getReduction().remove(i);
                    break;
                }
            }
        }
    }

    private CompoundButton.OnCheckedChangeListener absorbCheckedChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            setElementAbsorb(isChecked, buttonView.getId());
        }
    };

    private void setElementAbsorb(boolean isChecked, int buttonId) {
        clearTextFocus();
        int element = -1;
        Boolean contains = false;
        switch (buttonId) {
            case R.id.redOrbAbsorb:
                element = 0;
                break;
            case R.id.blueOrbAbsorb:
                element = 1;
                break;
            case R.id.greenOrbAbsorb:
                element = 2;
                break;
            case R.id.lightOrbAbsorb:
                element = 3;
                break;
            case R.id.darkOrbAbsorb:
                element = 4;
                break;
        }

        if (isChecked) {
            for (int i = 0; i < enemy.getAbsorb().size(); i++) {
                if (enemy.getAbsorb().get(i).getValue() == element && !contains) {
                    contains = true;
                }
            }
            if (element >= 0 && !contains) {
                enemy.getAbsorb().add(new RealmElement(element));
            }
        } else {
            for (int i = 0; i < enemy.getAbsorb().size(); i++) {
                if (enemy.getAbsorb().get(i).getValue() == element) {
                    enemy.getAbsorb().remove(i);
                    break;
                }
            }
        }
    }

    private void setCheckBoxes() {
        hasAwakeningsCheck.setChecked(!Singleton.getInstance().hasAwakenings());
        activeUsedCheck.setChecked(Singleton.getInstance().isActiveSkillUsed());
        hasLeaderSkillCheck1.setChecked(Singleton.getInstance().hasLeaderSkill());
        hasLeaderSkillCheck2.setChecked(Singleton.getInstance().hasHelperSkill());
    }

    private void setupHpSeekBar() {
        int position = 0;
        if (team.getLeadSkill().getHpPercent().isEmpty()) {
            if (team.getHelperSkill().getHpPercent().isEmpty()) {
                team.setTeamHp(100);
            } else {
                for (int i = 0; i < team.getHelperSkill().getAtkData().size(); i++) {
                    if (i == 0) {
                        position = i;
                    } else {
                        if (team.getHelperSkill().getAtkData().get(i).getValue() > team.getHelperSkill().getAtkData().get(i - 1).getValue()) {
                            position = i;
                        }
                    }
                }
                team.setTeamHp(team.getHelperSkill().getHpPercent().get(2 * position).getValue());
            }
        } else {
            for (int i = 0; i < team.getLeadSkill().getAtkData().size(); i++) {
                if (i == 0) {
                    position = i;
                } else {
                    if (team.getLeadSkill().getAtkData().get(i).getValue() > team.getLeadSkill().getAtkData().get(i - 1).getValue()) {
                        position = i;
                    }
                }
            }
            Log.d("TeamDamageListFragment", "team hpPercent is: " + team.getLeadSkill().getHpPercent());
            team.setTeamHp(team.getLeadSkill().getHpPercent().get(2 * position).getValue());
        }
//        team.save();
        teamHp.setProgress(team.getTeamHp());
        team.setAtkMultiplierArrays(orbMatchList, totalCombos);
        updateTextView();
        teamHpValue.setText("" + teamHp.getProgress());
        Log.d("TeamDamageList", "TeamHp is: " + team.getTeamHp());
    }

    private SeekBar.OnSeekBarChangeListener teamHpSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                team.setTeamHp(progress);
            }
            teamHpValue.setText("" + progress);
//            team.save();
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            team.setAtkMultiplierArrays(orbMatchList, totalCombos);
            monsterListAdapter.notifyDataSetChanged();
        }
    };

    private void clearTextFocus() {
        additionalComboValue.clearFocus();
        damageThresholdValue.clearFocus();
        damageImmunityValue.clearFocus();
        reductionValue.clearFocus();
    }

    private View.OnClickListener tooltipOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            TooltipText tooltipText;
            switch (id) {
                case R.id.elementAbsorb:
                    tooltipText = new TooltipText(getContext(), "Enemy will absorb all selected elements");
                    break;
                case R.id.targetReduction:
                    tooltipText = new TooltipText(getContext(), "Enemy will reduce a percentage amount of damage from all selected elements");
                    break;
                case R.id.damageThreshold:
                    tooltipText = new TooltipText(getContext(), "Enemy will absorb any hit over the specified value");
                    break;
                case R.id.damageImmunity:
                    tooltipText = new TooltipText(getContext(), "Enemy will take 0 damage for any hit over the specified value");
                    break;
                case R.id.hasAwakenings:
                    tooltipText = new TooltipText(getContext(), "Disable awakenings");
                    break;
                case R.id.activeUsed:
                    tooltipText = new TooltipText(getContext(), "Active skill was used this turn");
                    break;
                case R.id.hasLeaderSkill:
                    tooltipText = new TooltipText(getContext(), "Enable Leader skills for leader and helper");
                    break;
                default:
                    tooltipText = new TooltipText(getContext(), "How did you get this tooltip?");
            }
            tooltipText.show(v);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        broadcastReceiver = new JustAnotherBroadcastReceiver(receiverMethods);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("REFRESH_TEAM_DAMAGE"));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("ONDESELECT_TEAM_DAMAGE"));
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            additionalCombos = Singleton.getInstance().getAdditionalCombos();
            totalCombos = additionalCombos + orbMatchList.size();
            additionalComboValue.setText("" + totalCombos);
            Log.d("TeamDamageListFrag", "orbMatchList setMenuVisibility is: " + orbMatchList.size());
            Log.d("TeamDamageListFrag", "totalCombos setMenuVisibility is: " + totalCombos);
            setupHpSeekBar();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    private JustAnotherBroadcastReceiver.receiverMethods receiverMethods = new JustAnotherBroadcastReceiver.receiverMethods() {
        @Override
        public void onReceiveMethod(Intent intent) {
            switch (intent.getAction()) {
                case "ONDESELECT_TEAM_DAMAGE":
                    TeamDamageListFragment.this.onDeselect();
                    break;
                case "REFRESH_TEAM_DAMAGE":
                    TeamDamageListFragment.this.onSelect();
                    break;
            }
        }
    };

    public void onSelect() {
//        enemy = realm.where(Enemy.class).equalTo("enemyId", 0).findFirst();
//        enemy = realm.copyFromRealm(enemy);
        if (hasEnemy) {
            temp = enemy.getCurrentHp();
            tempCurrent = enemy.getCurrentHp();
            setReductionOrbs();
            setAbsorbOrbs();
            setDamageThreshold();
        }
        clearTextFocus();
        team.updateAwakenings();
        team.setHpRcvMultiplierArrays(orbMatchList, totalCombos);
        monsterListAdapter.setCombos(totalCombos);
        team.setAtkMultiplierArrays(orbMatchList, totalCombos);
        calculateDamage();
        updateTextView();
        monsterListAdapter.notifyDataSetChanged();
    }

    public void onDeselect() {
        Singleton.getInstance().setAdditionalCombos(totalCombos - orbMatchList.size());
        if (enemy != null) {
            realm.beginTransaction();

//            realm.copyToRealmOrUpdate(enemy);
            enemy.setDamaged(false);
            tempCurrent = enemy.getCurrentHp();
            realm.commitTransaction();
        }
    }

}

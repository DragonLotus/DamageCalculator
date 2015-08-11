package com.example.anthony.damagecalculator.Data;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.anthony.damagecalculator.Util.DamageCalculationUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Anthony on 7/13/2015.
 */
public class Monster implements Parcelable {
    public static final int HP_MULTIPLIER = 10;
    public static final int ATK_MULTIPLIER = 5;
    public static final int RCV_MULTIPLIER = 3;
    private int atkMax, atkMin, hpMax, hpMin, maxLevel, rcvMax, rcvMin, type1, type2, currentLevel, atkPlus, hpPlus, rcvPlus, maxAwakenings, currentAwakenings;
    private Color element1, element2;
    private ArrayList<String> awokenSkills;
    private String activeSkill, leaderSkill, name;
    private double atkScale, rcvScale, hpScale, currentAtk, currentRcv, currentHp;
    private boolean isBound;
    DecimalFormat format = new DecimalFormat("0.00");

    public Monster() {
        currentLevel = 1;
        atkMax = 1370;
        atkMin = 913;
        hpMin = 1271;
        hpMax = 3528;
        rcvMin = 256;
        rcvMax = 384;
        maxLevel = 99;
        atkScale = 1;
        rcvScale = 1;
        hpScale = 1;
        maxAwakenings = 7;
        hpPlus = 99;
        atkPlus = 99;
        rcvPlus = 99;
        currentHp = DamageCalculationUtil.monsterStatCalc(hpMin, hpMax, currentLevel, maxLevel, hpScale);
        currentAtk = DamageCalculationUtil.monsterStatCalc(atkMin, atkMax, currentLevel, maxLevel, atkScale);
        currentRcv = DamageCalculationUtil.monsterStatCalc(rcvMin, rcvMax, currentLevel, maxLevel, rcvScale);
        currentAwakenings = 7;
        element1 = Color.LIGHT;
        element2 = Color.LIGHT;
        isBound = false;
        name = "Kirin of the Sacred Gleam, Sakuya";
    }

    public int getElement1Damage(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, int combos) {
        return (int) DamageCalculationUtil.monsterElement1Damage(this, orbMatches, orbPlusAwakenings, combos);
    }

    public String getElement1DamageString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, int combos) {
        return String.valueOf(getElement1Damage(orbMatches, orbPlusAwakenings, combos));
    }

    public int getElement1DamageEnemy(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return (int) Math.ceil(DamageCalculationUtil.monsterElement1DamageEnemy(this, orbMatches, orbPlusAwakenings, combos, enemy));
    }

    public String getElement1DamageEnemyString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return String.valueOf(getElement1DamageEnemy(orbMatches, orbPlusAwakenings, enemy, combos));
    }

    public int getElement2Damage(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, int combos) {
        return (int) DamageCalculationUtil.monsterElement2Damage(this, orbMatches, orbPlusAwakenings, combos);
    }

    public String getElement2DamageString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, int combos) {
        return String.valueOf(getElement2Damage(orbMatches, orbPlusAwakenings, combos));
    }

    public int getElement2DamageEnemy(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return (int) Math.ceil(DamageCalculationUtil.monsterElement2DamageEnemy(this, orbMatches, orbPlusAwakenings, combos, enemy));
    }

    public String getElement2DamageEnemyString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return String.valueOf(getElement2DamageEnemy(orbMatches, orbPlusAwakenings, enemy, combos));
    }

    public int getElement1DamageReduction(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos){
        return (int) DamageCalculationUtil.monsterElement1DamageReduction(this, orbMatches, orbPlusAwakenings, combos, enemy);
    }

    public String getElement1DamageReductionString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return String.valueOf(getElement1DamageReduction(orbMatches, orbPlusAwakenings, enemy, combos));
    }

    public int getElement2DamageReduction(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos){
        return (int) DamageCalculationUtil.monsterElement2DamageReduction(this, orbMatches, orbPlusAwakenings, combos, enemy);
    }

    public String getElement2DamageReductionString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return String.valueOf(getElement2DamageReduction(orbMatches, orbPlusAwakenings, enemy, combos));
    }

    public int getElement1DamageAbsorb(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos){
        return (int) DamageCalculationUtil.monsterElement1DamageAbsorb(this, orbMatches, orbPlusAwakenings, combos, enemy);
    }

    public String getElement1DamageAbsorbString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return String.valueOf(getElement1DamageAbsorb(orbMatches, orbPlusAwakenings, enemy, combos));
    }

    public int getElement2DamageAbsorb(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos){
        return (int) DamageCalculationUtil.monsterElement2DamageAbsorb(this, orbMatches, orbPlusAwakenings, combos, enemy);
    }

    public String getElement2DamageAbsorbString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return String.valueOf(getElement2DamageAbsorb(orbMatches, orbPlusAwakenings, enemy, combos));
    }

    public int getElement1DamageThreshold(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos){
        return (int) DamageCalculationUtil.monsterElement1DamageThreshold(this, orbMatches, orbPlusAwakenings, combos, enemy);
    }

    public String getElement1DamageThresholdString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return String.valueOf(getElement1DamageThreshold(orbMatches, orbPlusAwakenings, enemy, combos));
    }

    public int getElement2DamageThreshold(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos){
        return (int) DamageCalculationUtil.monsterElement2DamageThreshold(this, orbMatches, orbPlusAwakenings, combos, enemy);
    }

    public String getElement2DamageThresholdString(ArrayList<OrbMatch> orbMatches, int orbPlusAwakenings, Enemy enemy, int combos) {
        return String.valueOf(getElement2DamageThreshold(orbMatches, orbPlusAwakenings, enemy, combos));
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
        setCurrentHp(DamageCalculationUtil.monsterStatCalc(hpMin, hpMax, currentLevel, maxLevel, hpScale));
        setCurrentAtk(DamageCalculationUtil.monsterStatCalc(atkMin, atkMax, currentLevel, maxLevel, atkScale));
        setCurrentRcv(DamageCalculationUtil.monsterStatCalc(rcvMin, rcvMax, currentLevel, maxLevel, rcvScale));
    }

    public int getCurrentAtk() {
        return (int) currentAtk;
    }

    public int getCurrentHp() {
        return (int) currentHp;
    }

    public int getTotalHp() {
        return (int) currentHp + hpPlus * HP_MULTIPLIER;
    }

    public int getTotalAtk() {
        return (int) currentAtk + atkPlus * ATK_MULTIPLIER;
    }

    public int getTotalRcv() {
        return (int) currentRcv + rcvPlus * RCV_MULTIPLIER;
    }

    public String getWeightedString() {
        return format.format(getWeighted());
    }

    public double getWeighted() {
        return currentHp / HP_MULTIPLIER + currentAtk / ATK_MULTIPLIER + currentRcv / RCV_MULTIPLIER;
    }

    public String getTotalWeightedString() {
        return format.format(getTotalWeighted());
    }

    public double getTotalWeighted() {
        return getWeighted() + hpPlus + atkPlus + rcvPlus;
    }

    public void setCurrentAtk(double currentAtk) {
        this.currentAtk = currentAtk;
    }

    public int getCurrentRcv() {
        return (int) currentRcv;
    }

    public void setCurrentRcv(double currentRcv) {
        this.currentRcv = currentRcv;
    }

    public int getAtkMax()

    {
        return atkMax;
    }

    public void setAtkMax(int atkMax) {
        this.atkMax = atkMax;
    }

    public int getAtkMin() {
        return atkMin;
    }

    public void setAtkMin(int atkMin) {
        this.atkMin = atkMin;
    }

    public int getHpMax() {
        return hpMax;
    }

    public void setHpMax(int hpMax) {
        this.hpMax = hpMax;
    }

    public int getHpMin() {
        return hpMin;
    }

    public void setHpMin(int hpMin) {
        this.hpMin = hpMin;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public int getRcvMax() {
        return rcvMax;
    }

    public void setRcvMax(int rcvMax) {
        this.rcvMax = rcvMax;
    }

    public int getRcvMin() {
        return rcvMin;
    }

    public void setRcvMin(int rcvMin) {
        this.rcvMin = rcvMin;
    }

    public int getType1() {
        return type1;
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }

    public int getType2() {
        return type2;
    }

    public void setType2(int type2) {
        this.type2 = type2;
    }

    public Color getElement1() {
        return element1;
    }

    public void setElement1(Color element1) {
        this.element1 = element1;
    }

    public Color getElement2() {
        return element2;
    }

    public void setElement2(Color element2) {
        this.element2 = element2;
    }

    public ArrayList<String> getAwokenSkills() {
        return awokenSkills;
    }

    public void setAwokenSkills(ArrayList<String> awokenSkills) {
        this.awokenSkills = awokenSkills;
    }

    public String getActiveSkill() {
        return activeSkill;
    }

    public void setActiveSkill(String activeSkill) {
        this.activeSkill = activeSkill;
    }

    public String getLeaderSkill() {
        return leaderSkill;
    }

    public void setLeaderSkill(String leaderSkill) {
        this.leaderSkill = leaderSkill;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAtkScale() {
        return atkScale;
    }

    public void setAtkScale(double atkScale) {
        this.atkScale = atkScale;
    }

    public double getRcvScale() {
        return rcvScale;
    }

    public void setRcvScale(double rcvScale) {
        this.rcvScale = rcvScale;
    }

    public double getHpScale() {
        return hpScale;
    }

    public void setHpScale(double hpScale) {
        this.hpScale = hpScale;
    }

    public void setCurrentHp(double currentHp) {
        this.currentHp = currentHp;
    }

    public int getAtkPlus() {
        return atkPlus;
    }

    public void setAtkPlus(int atkPlus) {
        this.atkPlus = atkPlus;
    }

    public int getHpPlus() {
        return hpPlus;
    }

    public void setHpPlus(int hpPlus) {
        this.hpPlus = hpPlus;
    }

    public int getRcvPlus() {
        return rcvPlus;
    }

    public void setRcvPlus(int rcvPlus) {
        this.rcvPlus = rcvPlus;
    }

    public int getMaxAwakenings() {
        return maxAwakenings;
    }

    public void setMaxAwakenings(int maxAwakenings) {
        this.maxAwakenings = maxAwakenings;
    }

    public int getCurrentAwakenings() {
        return currentAwakenings;
    }

    public void setCurrentAwakenings(int currentAwakenings) {
        this.currentAwakenings = currentAwakenings;
    }

    public boolean isBound() {
        return isBound;
    }

    public void setIsBound(boolean isBound) {
        this.isBound = isBound;
    }

    public int getTPA(){
        //Loop through awakenings and count Double prongs
        return 0;
    }

    public int getRowAwakenings(Color color){
        //Loop through awakenings and count Row Awakenings depending on element
        return 0;
    }

    public Monster(Parcel source) {
        atkMax = source.readInt();
        atkMin = source.readInt();
        hpMax = source.readInt();
        hpMin = source.readInt();
        maxLevel = source.readInt();
        rcvMax = source.readInt();
        rcvMin = source.readInt();
        type1 = source.readInt();
        type2 = source.readInt();
        currentLevel = source.readInt();
        atkPlus = source.readInt();
        hpPlus = source.readInt();
        rcvPlus = source.readInt();
        maxAwakenings = source.readInt();
        currentAwakenings = source.readInt();
        element1 = (Color) source.readSerializable();
        element2 = (Color) source.readSerializable();
        awokenSkills = source.readArrayList(String.class.getClassLoader());
        activeSkill = source.readString();
        leaderSkill = source.readString();
        name = source.readString();
        atkScale = source.readDouble();
        rcvScale = source.readDouble();
        hpScale = source.readDouble();
        currentAtk = source.readDouble();
        currentHp = source.readDouble();
        isBound = source.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(atkMax);
        dest.writeInt(atkMin);
        dest.writeInt(hpMax);
        dest.writeInt(hpMin);
        dest.writeInt(maxLevel);
        dest.writeInt(rcvMax);
        dest.writeInt(rcvMin);
        dest.writeInt(type1);
        dest.writeInt(type2);
        dest.writeInt(currentLevel);
        dest.writeInt(atkPlus);
        dest.writeInt(hpPlus);
        dest.writeInt(rcvPlus);
        dest.writeInt(maxAwakenings);
        dest.writeInt(currentAwakenings);
        dest.writeSerializable(element1);
        dest.writeSerializable(element2);
        dest.writeList(awokenSkills);
        dest.writeString(activeSkill);
        dest.writeString(leaderSkill);
        dest.writeString(name);
        dest.writeDouble(atkScale);
        dest.writeDouble(rcvScale);
        dest.writeDouble(hpScale);
        dest.writeDouble(currentAtk);
        dest.writeDouble(currentHp);
        dest.writeByte((byte) (isBound ? 1 : 0));
    }

    public static final Parcelable.Creator<Monster> CREATOR = new Creator<Monster>() {
        public Monster createFromParcel(Parcel source) {
            return new Monster(source);
        }

        public Monster[] newArray(int size) {
            return new Monster[size];
        }
    };
}



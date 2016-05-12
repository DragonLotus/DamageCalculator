package com.example.anthony.damagecalculator.Data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.example.anthony.damagecalculator.R;
import com.example.anthony.damagecalculator.Util.DamageCalculationUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anthony on 7/13/2015.
 */
@Table(name = "Monster")
public class Monster extends Model implements Parcelable {
    public static final int HP_MULTIPLIER = 10;
    public static final int ATK_MULTIPLIER = 5;
    public static final int RCV_MULTIPLIER = 3;
    @Column(name = "monsterId", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE, onUpdate = Column.ForeignKeyAction.NO_ACTION, onDelete = Column.ForeignKeyAction.NO_ACTION)
    private long monsterId;
    @Column(name = "baseMonster", onDelete = Column.ForeignKeyAction.NO_ACTION, onUpdate = Column.ForeignKeyAction.CASCADE, onUniqueConflict = Column.ConflictAction.REPLACE, index = true)
    private BaseMonster baseMonster;
    @Column(name = "favorite")
    private boolean favorite;
    @Column(name = "priority")
    private int priority;
    @Column(name = "currentLevel")
    private int currentLevel;
    @Column(name = "atkPlus")
    private int atkPlus;
    @Column(name = "hpPlus")
    private int hpPlus;
    @Column(name = "rcvPlus")
    private int rcvPlus;
    @Column(name = "currentAwakenings")
    private int currentAwakenings;
    @Column(name = "currentAtk")
    private double currentAtk;
    @Column(name = "currentRcv")
    private double currentRcv;
    @Column(name = "currentHp")
    private double currentHp;
    @Column(name = "helper")
    private boolean helper;
    @Column(name = "latents")
    private ArrayList<Integer> latents;
    @Column(name = "killerAwakenings")
    private ArrayList<Integer> killerAwakenings;
    DecimalFormat format = new DecimalFormat("0.00");

    public Monster() {
    }

    public Monster(long baseMonsterId) {
        currentLevel = 1;
        monsterId = 0;
        baseMonster = BaseMonster.getMonsterId(baseMonsterId);
        hpPlus = 0;
        atkPlus = 0;
        rcvPlus = 0;
        currentHp = 0;
        currentAtk = 0;
        currentRcv = 0;
        currentAwakenings = 0;
        priority = 2;
        favorite = false;
        helper = false;
        latents = new ArrayList<>();
        latents.add(0);
        latents.add(0);
        latents.add(0);
        latents.add(0);
        latents.add(0);
        killerAwakenings = new ArrayList<>();
        if(baseMonsterId != 0){
            setCurrentHp(DamageCalculationUtil.monsterStatCalc(baseMonster.getHpMin(), baseMonster.getHpMax(), currentLevel, baseMonster.getMaxLevel(), baseMonster.getHpScale()));
            setCurrentAtk(DamageCalculationUtil.monsterStatCalc(baseMonster.getAtkMin(), baseMonster.getAtkMax(), currentLevel, baseMonster.getMaxLevel(), baseMonster.getAtkScale()));
            setCurrentRcv(DamageCalculationUtil.monsterStatCalc(baseMonster.getRcvMin(), baseMonster.getRcvMax(), currentLevel, baseMonster.getMaxLevel(), baseMonster.getRcvScale()));
        }
    }

    public int getElement1Damage(Team team, int combos) {
        return (int) DamageCalculationUtil.monsterElement1Damage(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement1()), combos, team);
    }

    public int getElement1DamageEnemy(Team team, Enemy enemy, int combos) {
        return (int) Math.ceil(DamageCalculationUtil.monsterElement1DamageEnemy(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement1()), combos, enemy, team));
    }

    public int getElement2Damage(Team team, int combos) {
        return (int) DamageCalculationUtil.monsterElement2Damage(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement2()), combos, team);
    }


    public int getElement2DamageEnemy(Team team, Enemy enemy, int combos) {
        return (int) Math.ceil(DamageCalculationUtil.monsterElement2DamageEnemy(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement2()), combos, enemy, team));
    }

    public int getElement1DamageReduction(Team team, Enemy enemy, int combos) {
        return (int) DamageCalculationUtil.monsterElement1DamageReduction(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement1()), combos, enemy, team);
    }

    public int getElement2DamageReduction(Team team, Enemy enemy, int combos) {
        return (int) DamageCalculationUtil.monsterElement2DamageReduction(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement2()), combos, enemy, team);
    }

    public int getElement1DamageAbsorb(Team team, Enemy enemy, int combos) {
        return (int) DamageCalculationUtil.monsterElement1DamageAbsorb(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement1()), combos, enemy, team);
    }

    public int getElement2DamageAbsorb(Team team, Enemy enemy, int combos) {
        return (int) DamageCalculationUtil.monsterElement2DamageAbsorb(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement2()), combos, enemy, team);
    }

    public int getElement1DamageThreshold(Team team, Enemy enemy, int combos) {
        return (int) DamageCalculationUtil.monsterElement1DamageThreshold(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement1()), combos, enemy, team);
    }

    public int getElement2DamageThreshold(Team team, Enemy enemy, int combos) {
        return (int) DamageCalculationUtil.monsterElement2DamageThreshold(this, team.getOrbMatches(), team.getOrbPlusAwakenings(baseMonster.getElement2()), combos, enemy, team);
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
        setCurrentHp(DamageCalculationUtil.monsterStatCalc(baseMonster.getHpMin(), baseMonster.getHpMax(), currentLevel, baseMonster.getMaxLevel(), baseMonster.getHpScale()));
        setCurrentAtk(DamageCalculationUtil.monsterStatCalc(baseMonster.getAtkMin(), baseMonster.getAtkMax(), currentLevel, baseMonster.getMaxLevel(), baseMonster.getAtkScale()));
        setCurrentRcv(DamageCalculationUtil.monsterStatCalc(baseMonster.getRcvMin(), baseMonster.getRcvMax(), currentLevel, baseMonster.getMaxLevel(), baseMonster.getRcvScale()));
    }

    public int getCurrentAtk() {
        return (int) currentAtk;
    }

    public int getCurrentHp() {
        return (int) currentHp;
    }

    public int getTotalHp() {
        int totalHp = (int) currentHp + hpPlus * HP_MULTIPLIER;
        int counter = 0;
        for (int i = 0; i < currentAwakenings; i++){
            if(getAwokenSkills().get(i) == 1){
                counter++;
            }
        }
        int counter2 = 0;
        for(int i = 0; i < latents.size(); i++){
            if(latents.get(i) == 1){
                counter2++;
            }
        }
        int latentHp = (int) Math.floor(currentHp * counter2 * 0.015 + .5);
        totalHp = totalHp + (200*counter) + latentHp;
        return totalHp;
    }

    public int getTotalAtk() {
        int totalAtk = (int) currentAtk + atkPlus * ATK_MULTIPLIER;
        int counter = 0;
        for (int i = 0; i < currentAwakenings; i++){
            if(getAwokenSkills().get(i) == 2){
                counter++;
            }
        }
        int counter2 = 0;
        for(int i = 0; i < latents.size(); i++){
            if(latents.get(i) == 2){
                counter2++;
            }
        }
        int latentAtk = (int) Math.floor(currentAtk * counter2 * 0.01 + .5);
        totalAtk = totalAtk + (100*counter) + latentAtk;

        return totalAtk;
    }

    public int getTotalRcv() {
        int totalRcv = (int) currentRcv + rcvPlus * RCV_MULTIPLIER;
        int counter = 0;
        for (int i = 0; i < currentAwakenings; i++){
            if(getAwokenSkills().get(i) == 3){
                counter++;
            }
        }
        int counter2 = 0;
        for(int i = 0; i < latents.size(); i++){
            if(latents.get(i) == 3){
                counter2++;
            }
        }
        int latentRcv = (int) Math.floor(currentRcv * counter2 * 0.04 + .5);
        totalRcv = totalRcv + (50*counter) + latentRcv;
        return totalRcv;
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

    public int getAtkMax() {
        return baseMonster.getAtkMax();
    }

    public int getAtkMin() {
        return baseMonster.getAtkMin();
    }

    public int getHpMax() {
        return baseMonster.getHpMax();
    }

    public int getHpMin() {
        return baseMonster.getHpMin();
    }

    public int getMaxLevel() {
        return baseMonster.getMaxLevel();
    }

    public int getRcvMax() {
        return baseMonster.getRcvMax();
    }

    public int getRcvMin() {
        return baseMonster.getRcvMin();
    }

    public int getType1() {
        return baseMonster.getType1();
    }

    public int getType2() {
        return baseMonster.getType2();
    }

    public int getType3() {
        return baseMonster.getType3();
    }

    public String getType1String() {
        return baseMonster.getType1String();
    }

    public String getType2String() {
        return baseMonster.getType2String();
    }

    public String getType3String() {
        return baseMonster.getType3String();
    }

    public Element getElement1() {
        return baseMonster.getElement1();
    }

    public Element getElement2() {
        return baseMonster.getElement2();
    }

    public int getElement1Int(){
        return baseMonster.getElement1Int();
    }

    public int getElement2Int(){
        return baseMonster.getElement2Int();
    }

    public ArrayList<Integer> getAwokenSkills() {
        Log.d("Monster", "Monster: " + baseMonster);
        return baseMonster.getAwokenSkills();
    }

    public int getAwokenSkills(int position) {
        Log.d("Awakening List", "" + baseMonster.getAwokenSkills());
        return baseMonster.getAwokenSkills(position);
    }

    public String getActiveSkill() {
        return baseMonster.getActiveSkill();
    }

    public String getLeaderSkill() {
        return baseMonster.getLeaderSkill();
    }

    public String getName() {
        return baseMonster.getName();
    }

    public double getAtkScale() {
        return baseMonster.getAtkScale();
    }

    public double getRcvScale() {
        return baseMonster.getRcvScale();
    }

    public double getHpScale() {
        return baseMonster.getHpScale();
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

    public int getTotalPlus() {
        return hpPlus + atkPlus + rcvPlus;
    }

    public int getMaxAwakenings() {
        return baseMonster.getMaxAwakenings();
    }

    public int getCurrentAwakenings() {
        return currentAwakenings;
    }

    public void setCurrentAwakenings(int currentAwakenings) {
        if (getAwokenSkills().contains(31) || getAwokenSkills().contains(32) || getAwokenSkills().contains(33) || getAwokenSkills().contains(34) || getAwokenSkills().contains(35) || getAwokenSkills().contains(36) || getAwokenSkills().contains(37)){
            killerAwakenings.clear();
            if(currentAwakenings > getMaxAwakenings()){
                for (int i = 0; i < getMaxAwakenings(); i++){
                    switch(baseMonster.getAwokenSkills(i)){
                        case 31:
                            killerAwakenings.add(31);
                            break;
                        case 32:
                            killerAwakenings.add(32);
                            break;
                        case 33:
                            killerAwakenings.add(33);
                            break;
                        case 34:
                            killerAwakenings.add(34);
                            break;
                        case 35:
                            killerAwakenings.add(35);
                            break;
                        case 36:
                            killerAwakenings.add(36);
                            break;
                        case 37:
                            killerAwakenings.add(37);
                            break;
                    }
                }
            } else {
                for (int i = 0; i < currentAwakenings; i++){
                    switch(baseMonster.getAwokenSkills(i)){
                        case 31:
                            killerAwakenings.add(31);
                            break;
                        case 32:
                            killerAwakenings.add(32);
                            break;
                        case 33:
                            killerAwakenings.add(33);
                            break;
                        case 34:
                            killerAwakenings.add(34);
                            break;
                        case 35:
                            killerAwakenings.add(35);
                            break;
                        case 36:
                            killerAwakenings.add(36);
                            break;
                        case 37:
                            killerAwakenings.add(37);
                            break;
                    }
                }
            }

            Log.d("KillerAwakenings","killerAwakenings is: " + killerAwakenings);
        }
        this.currentAwakenings = currentAwakenings;
    }

    public long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(long monsterId) {
        this.monsterId = monsterId;
    }

    public long getBaseMonsterId(){
        return baseMonster.getMonsterId();
    }

    public int getMonsterPicture() {
        return baseMonster.getMonsterPicture();
    }

    public int getXpCurve() {
        return baseMonster.getXpCurve();
    }

    public int getRarity() {
        return baseMonster.getRarity();
    }

    public int getTeamCost() {
        return baseMonster.getTeamCost();
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public ArrayList<Long> getEvolutions() {
        return baseMonster.getEvolutions();
    }

    public BaseMonster getBaseMonster() {
        return baseMonster;
    }

    public void setBaseMonster(BaseMonster baseMonster) {
        this.baseMonster = baseMonster;
    }

    public boolean isHelper() {
        return helper;
    }

    public void setHelper(boolean helper) {
        this.helper = helper;
    }

    public ArrayList<Integer> getLatents() {
        return latents;
    }

    public void setLatents(ArrayList<Integer> latents) {
        this.latents = latents;
    }

    public ArrayList<Integer> getKillerAwakenings(){
        return killerAwakenings;
    }

    public int getTPA() {
        int numOfDoubleProngs = 0;
        if(!Team.getTeamById(0).hasAwakenings()){
            return numOfDoubleProngs;
        }else {

            for (int i = 0; i < currentAwakenings; i++) {
                if (baseMonster.getAwokenSkills(i) == 27) {
                    numOfDoubleProngs++;
                }
            }
        }

        return numOfDoubleProngs;
    }

    public Monster(Parcel source) {
        monsterId = source.readLong();

        currentLevel = source.readInt();
        atkPlus = source.readInt();
        hpPlus = source.readInt();
        rcvPlus = source.readInt();
        currentAwakenings = source.readInt();
        currentAtk = source.readDouble();
        currentHp = source.readDouble();
        //isBound = source.readByte() == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(monsterId);
        dest.writeInt(currentLevel);
        dest.writeInt(atkPlus);
        dest.writeInt(hpPlus);
        dest.writeInt(rcvPlus);
        dest.writeInt(currentAwakenings);
        dest.writeDouble(currentAtk);
        dest.writeDouble(currentHp);
        //dest.writeByte((byte) (isBound ? 1 : 0));
    }

    public static final Parcelable.Creator<Monster> CREATOR = new Creator<Monster>() {
        public Monster createFromParcel(Parcel source) {
            return new Monster(source);
        }

        public Monster[] newArray(int size) {
            return new Monster[size];
        }
    };

    public static List<Monster> getAllMonsters() {
        return new Select().from(Monster.class).execute();
    }

    public static List<Monster> getAllSavedMonsters() {
        return new Select().from(Monster.class).where("helper = ?", false).execute();
    }

    public static List<Monster> getAllHelperMonsters() {
        return new Select().from(Monster.class).where("helper = ?", true).execute();
    }

    public static void deleteAllMonsters() {
        new Delete().from(Monster.class).execute();
    }

    public static void deleteMonsterById(long id){
        new Delete().from(Monster.class).where("monsterId = ?", id).executeSingle();
    }

    public static List<Monster> getAllMonstersLevel(int level) {
        return new Select().from(Monster.class).where("currentLevel = ?", level).execute();
    }

    public static Monster getMonsterId(long id) {
        return new Select().from(Monster.class).where("monsterId = ?", id).executeSingle();
    }
//
//    public static List<Monster> getMonsterAtkMax(int attack){
//        return new Select().from(Monster.class).where("atkMax = ?", attack).execute();
//    }
//
//    public static List<Monster> getMonsterAtkMin(int attack){
//        return new Select().from(Monster.class).where("atkMin = ?", attack).execute();
//    }
//
//    public static List<Monster> getMonsterHpMax(int hp){
//        return new Select().from(Monster.class).where("hpMax = ?", hp).execute();
//    }
//
//    public static List<Monster> getMonsterHpMin(int hp){
//        return new Select().from(Monster.class).where("hpMin = ?", hp).execute();
//    }
//
//    public static List<Monster> getMonsterMaxLevel(int level){
//        return new Select().from(Monster.class).where("maxLevel = ?", level).execute();
//    }
//
//    public static List<Monster> getMonsterRcvMax(int rcv){
//        return new Select().from(Monster.class).where("maxRcv = ?", rcv).execute();
//    }
//
//    public static List<Monster> getMonsterRcvMin(int rcv){
//        return new Select().from(Monster.class).where("minRcv = ?", rcv).execute();
//    }
//
//    public static List<Monster> getMonsterType1(int type){
//        return new Select().from(Monster.class).where("type1 = ?", type).execute();
//    }
//
//    public static List<Monster> getMonsterType2(int type){
//        return new Select().from(Monster.class).where("type2 = ?", type).execute();
//    }
//
//    public static List<Monster> getMonsterMaxAwakenings(int awakenings){
//        return new Select().from(Monster.class).where("maxAwakenings = ?", awakenings).execute();
//    }
//
//    public static List<Monster> getMonsterCurrentAwakenings(int awakenings){
//        return new Select().from(Monster.class).where("currentAwakenings = ?", awakenings).execute();
//    }
//
//    public static List<Monster> getMonsterElement1(Element element){
//        return new Select().from(Monster.class).where("element1 = ?", element).execute();
//    }
//
//    public static List<Monster> getMonsterElement2(Element element){
//        return new Select().from(Monster.class).where("element2 = ?", element).execute();
//    }
//
//    private static List<Monster> getMonsterCurrentHp(int hp){
//        return new Select().from(Monster.class).where("currentHp = ?", hp).execute();
//    }
//
//    private static List<Monster> getMonsterCurrentAtk(int attack){
//        return new Select().from(Monster.class).where("currentAtk = ?", attack).execute();
//    }
//
//    private static List<Monster> getMonsterCurrentRcv(int rcv){
//        return new Select().from(Monster.class).where("currentRcv = ?", rcv).execute();
//    }
}



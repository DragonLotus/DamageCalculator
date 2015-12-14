package com.example.anthony.damagecalculator.Data;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.example.anthony.damagecalculator.R;
import com.fasterxml.jackson.databind.JsonNode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by DragonLotus on 9/15/2015.
 */
@Table(name = "BaseMonster")
public class BaseMonster extends Model {
    @Column(name = "monsterId", unique = true, index = true, onUniqueConflict = Column.ConflictAction.REPLACE, onUpdate = Column.ForeignKeyAction.NO_ACTION, onDelete = Column.ForeignKeyAction.NO_ACTION)
    private long monsterId;
    @Column(name = "monsterNumber")
    private long monsterNumber;
    @Column(name = "atkMax")
    private int atkMax;
    @Column(name = "atkMin")
    private int atkMin;
    @Column(name = "hpMax")
    private int hpMax;
    @Column(name = "hpMin")
    private int hpMin;
    @Column(name = "maxLevel")
    private int maxLevel;
    @Column(name = "rcvMin")
    private int rcvMin;
    @Column(name = "rcvMax")
    private int rcvMax;
    @Column(name = "type1")
    private int type1;
    @Column(name = "type2")
    private int type2;
    @Column(name = "type3")
    private int type3;
    @Column(name = "maxAwakenings")
    private int maxAwakenings;
    @Column(name = "element1")
    private Element element1;
    @Column(name = "element2")
    private Element element2;
    @Column(name = "awokenSkills")
    private ArrayList<Integer> awokenSkills;
    @Column(name = "activeSkill")
    private String activeSkill;
    @Column(name = "leaderSkill")
    private String leaderSkill;
    @Column(name = "name")
    private String name;
    @Column(name = "atkScale")
    private double atkScale;
    @Column(name = "rcvScale")
    private double rcvScale;
    @Column(name = "hpScale")
    private double hpScale;
    @Column(name = "monsterPicture")
    private int monsterPicture;
    @Column(name = "rarity")
    private int rarity;
    @Column(name = "teamCost")
    private int teamCost;
    @Column(name = "xpCurve")
    private int xpCurve;
    @Column(name = "evolutions")
    private ArrayList<Long> evolutions;
    DecimalFormat format = new DecimalFormat("0.00");

    public BaseMonster() {
        monsterId = 0;
        monsterNumber = 0;
        monsterPicture = R.drawable.monster_blank;
        atkMax = 0;
        atkMin = 0;
        hpMin = 0;
        hpMax = 0;
        rcvMin = 0;
        rcvMax = 0;
        maxLevel = 0;
        atkScale = 0;
        rcvScale = 0;
        hpScale = 0;
        maxAwakenings = 0;
        element1 = Element.BLANK;
        element2 = Element.BLANK;
        name = "Empty";
        leaderSkill = "Blank";
        type1 = -1;
        type2 = -1;
        type3 = -1;
        awokenSkills = new ArrayList<>();
        evolutions = new ArrayList<>();
    }

    public String getActiveSkill() {
        return activeSkill;
    }

    public void setActiveSkill(String activeSkill) {
        this.activeSkill = activeSkill;
    }

    public int getAtkMax() {
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

    public double getAtkScale() {
        return atkScale;
    }

    public void setAtkScale(double atkScale) {
        this.atkScale = atkScale;
    }

    public ArrayList<Integer> getAwokenSkills() {
        Log.d("BaseMonster", awokenSkills.toString());
        return awokenSkills;
    }

    public int getAwokenSkills(int position) {
        return awokenSkills.get(position);
    }

    public void addAwokenSkills(int awakening) {
        awokenSkills.add(awakening);
    }

    public void setAwokenSkills(ArrayList<Integer> awokenSkills) {
        this.awokenSkills = awokenSkills;
    }

    public void setAwokenSkills(JsonNode awokenNode) {
        this.awokenSkills = new ArrayList<>();
        for(JsonNode awakening: awokenNode) {
            this.awokenSkills.add(awakening.asInt());
        }
    }

    public Element getElement1() {
        return element1;
    }


    public void setElement1(int element1) {
        if(element1 == -1) {
            this.element1 = Element.BLANK;
        }
        if(element1 == 0) {
            this.element1 = Element.RED;
        }
        if(element1 == 1) {
            this.element1 = Element.BLUE;
        }
        if(element1 == 2) {
            this.element1 = Element.GREEN;
        }
        if(element1 == 3) {
            this.element1 = Element.LIGHT;
        }
        if(element1 == 4) {
            this.element1 = Element.DARK;
        }
    }

    public void setElement1(Element element1) {
        this.element1 = element1;
    }

    public Element getElement2() {
        return element2;
    }

    public void setElement2(int element2) {
        if(element2 == -1) {
            this.element2 = Element.BLANK;
        }
        if(element2 == 0) {
            this.element2 = Element.RED;
        }
        if(element2 == 1) {
            this.element2 = Element.BLUE;
        }
        if(element2 == 2) {
            this.element2 = Element.GREEN;
        }
        if(element2 == 3) {
            this.element2 = Element.LIGHT;
        }
        if(element2 == 4) {
            this.element2 = Element.DARK;
        }
    }

    public void setElement2(Element element2) {
        this.element2 = element2;
    }

    public int getElement1Int(){
        if (element1 == Element.BLANK){
            return -1;
        }else if (element1 == Element.RED){
            return 0;
        }else if(element1 == Element.BLUE) {
            return 1;
        }else if(element1 == Element.GREEN) {
            return 2;
        }else if(element1 == Element.LIGHT) {
            return 3;
        }else if(element1 == Element.DARK) {
            return 4;
        }else {
            return 5;
        }
    }

    public int getElement2Int(){
        if (element2 == Element.BLANK){
            return -1;
        }else if (element2 == Element.RED){
            return 0;
        }else if(element2 == Element.BLUE) {
            return 1;
        }else if(element2 == Element.GREEN) {
            return 2;
        }else if(element2 == Element.LIGHT) {
            return 3;
        }else if(element2 == Element.DARK) {
            return 4;
        }else {
            return 5;
        }
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

    public double getHpScale() {
        return hpScale;
    }

    public void setHpScale(double hpScale) {
        this.hpScale = hpScale;
    }

    public String getLeaderSkill() {
        return leaderSkill;
    }

    public void setLeaderSkill(String leaderSkill) {
        this.leaderSkill = leaderSkill;
    }

    public int getMaxAwakenings() {
        return awokenSkills.size();
    }

    public void setMaxAwakenings(int maxAwakenings) {
        this.maxAwakenings = maxAwakenings;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }

    public long getMonsterId() {
        return monsterId;
    }

    public void setMonsterId(long monsterId) {
        this.monsterId = monsterId;
    }

    public int getMonsterPicture() {
        return monsterPicture;
    }

    public void setMonsterPicture(int monsterPicture) {
        this.monsterPicture = monsterPicture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getRcvScale() {
        return rcvScale;
    }

    public void setRcvScale(double rcvScale) {
        this.rcvScale = rcvScale;
    }

    public int getType1() {
        if(type1 == 0){
            return 11;
        } else {
            return type1;
        }
    }

    public void setType1(int type1) {
        this.type1 = type1;
    }

    public int getType2() {
        if(type1 == 0){
            return 11;
        } else {
            return type2;
        }
    }

    public void setType2(int type2) {
        this.type2 = type2;
    }

    public int getType3() {
        if(type1 == 0){
            return 11;
        } else {
            return type3;
        }
    }

    public void setType3(int type3) {
        this.type3 = type3;
    }

    public String getType1String() {
        if (type1 == 0) {
            return "Evo Material";
        } else if (type1 == 1) {
            return "Balanced";
        } else if (type1 == 2) {
            return "Physical";
        } else if (type1 == 3) {
            return "Healer";
        } else if (type1 == 4) {
            return "Dragon";
        } else if (type1 == 5) {
            return "God";
        } else if (type1 == 6) {
            return "Attacker";
        } else if (type1 == 7) {
            return "Devil";
        } else if (type1 == 8) {
            return "Machine";
        } else if (type1 == 12) {
            return "Awoken Skill Material";
        } else if (type1 == 13) {
            return "Protected";
        } else if (type1 == 14) {
            return "Enhance Material";
        } else return "";
    }

    public String getType2String() {
        if (type2 == 0) {
            return "/Evo Material";
        } else if (type2 == 1) {
            return "/Balanced";
        } else if (type2 == 2) {
            return "/Physical";
        } else if (type2 == 3) {
            return "/Healer";
        } else if (type2 == 4) {
            return "/Dragon";
        } else if (type2 == 5) {
            return "/God";
        } else if (type2 == 6) {
            return "/Attacker";
        } else if (type2 == 7) {
            return "/Devil";
        } else if (type2 == 8) {
            return "/Machine";
        } else if (type2 == 12) {
            return "/Awoken Skill Material";
        } else if (type2 == 13) {
            return "/Protected";
        } else if (type2 == 14) {
            return "/Enhance Material";
        } else return "";
    }

    public String getType3String() {
        if (type3 == 0) {
            return "/Evo Material";
        } else if (type3 == 1) {
            return "/Balanced";
        } else if (type3 == 2) {
            return "/Physical";
        } else if (type3 == 3) {
            return "/Healer";
        } else if (type3 == 4) {
            return "/Dragon";
        } else if (type3 == 5) {
            return "/God";
        } else if (type3 == 6) {
            return "/Attacker";
        } else if (type3 == 7) {
            return "/Devil";
        }else if (type3 == 8) {
            return "/Machine";
        } else if (type3 == 12) {
            return "/Awoken Skill Material";
        } else if (type3 == 13) {
            return "/Protected";
        } else if (type3 == 14) {
            return "/Enhance Material";
        } else return "";
    }

    public int getXpCurve() {
        return xpCurve;
    }

    public void setXpCurve(int xpCurve) {
        this.xpCurve = xpCurve;
    }

    public int getRarity() {
        return rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    public int getTeamCost() {
        return teamCost;
    }

    public void setTeamCost(int teamCost) {
        this.teamCost = teamCost;
    }

    public ArrayList<Long> getEvolutions() {
        return evolutions;
    }

    public void setEvolutions(ArrayList<Long> evolutions) {
        this.evolutions = evolutions;
    }

    public static List<Monster> getAllMonsters() {
        return new Select().from(BaseMonster.class).execute();
    }

    public static BaseMonster getMonsterId(long id) {
        return new Select().from(BaseMonster.class).where("monsterId = ?", id).executeSingle();
    }


}

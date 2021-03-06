package com.padassist.BaseFragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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

import com.padassist.Constants;
import com.padassist.Data.BaseMonster;
import com.padassist.Data.Monster;
import com.padassist.Data.Team;
import com.padassist.Graphics.TextStroke;
import com.padassist.Graphics.TooltipSameSkill;
import com.padassist.R;
import com.padassist.Util.ImageResourceUtil;
import com.padassist.Util.ScoreMonsterUtil;

import org.parceler.Parcel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.padassist.Fragments.MonsterTabLayoutFragment.SUB;

/**
 * Created by DragonLotus on 11/4/2015.
 */
public abstract class SaveMonsterListRecyclerBase extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int GRID = 0, LINEAR = 1;
    protected ArrayList<Monster> monsterList;
    protected View.OnClickListener monsterListOnClickListener;
    protected View.OnLongClickListener monsterListOnLongClickListener;
    protected Context mContext;
    protected LayoutInflater inflater;
    private Toast toast;
    protected ArrayList<Integer> latentList;
    protected int expandedPosition = -1;
    protected RecyclerView monsterListView;
    protected View.OnClickListener deleteOnClickListener;
    protected View.OnClickListener expandOnClickListener;
    protected Realm realm = Realm.getDefaultInstance();
    protected boolean isGrid;
    protected int sixteenDp;
    protected int fortyEightDp;
    protected int fiftyFourDp;
    protected int eightDp;
    protected ClearTextFocus clearTextFocus;
    protected TooltipSameSkill tooltipSameSkill;
    protected boolean isManageMonsters;
    protected boolean isInherit;
    protected int monsterPosition;
    protected Team team;
    private ArrayList<Integer> teamAwakeningsSansReplace;
    private ArrayList<Integer> awakeningCount;
    private ArrayList<Double> elementDamage;
    private ArrayList<Double> elementDamageReplace;
    private double totalDamage;
    private double totalDamageReplace;
    private DecimalFormat df = new DecimalFormat("#.##");

    public interface ClearTextFocus {
        void doThis();
    }

    private View.OnClickListener expandOnItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clearTextFocus.doThis();
            int previous;
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            if (holder.getAdapterPosition() != expandedPosition) {
                if (expandedPosition >= 0) {
                    previous = expandedPosition;
                    notifyItemChanged(previous);
                }
                expandedPosition = holder.getAdapterPosition();
                notifyItemChanged(expandedPosition);
                monsterListView.smoothScrollToPosition(expandedPosition);
            } else {
                previous = expandedPosition;
                expandedPosition = -1;
                notifyItemChanged(previous);
            }

        }
    };


    private View.OnClickListener sameSkillToolTipOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RelativeLayout layout = (RelativeLayout) v.getTag();
            Monster monster = monsterList.get(expandedPosition);
            if (v.getId() == R.id.skill1Holder) {
                if (!monster.getActiveSkillString().equals("Blank")) {
                    RealmResults<BaseMonster> results = realm.where(BaseMonster.class).equalTo("activeSkillString", monster.getActiveSkillString()).findAllSorted("monsterId");
                    tooltipSameSkill = new TooltipSameSkill(mContext, "Monsters with the same skill:", results);
                    tooltipSameSkill.show(layout.getChildAt(1));
                }
            } else if (v.getId() == R.id.skill2Holder) {
                if (!monster.getMonsterInherit().getActiveSkillString().equals("Blank")) {
                    RealmResults<BaseMonster> results = realm.where(BaseMonster.class).equalTo("activeSkillString", monster.getMonsterInherit().getActiveSkillString()).findAllSorted("monsterId");
                    tooltipSameSkill = new TooltipSameSkill(mContext, "Monsters with the same skill:", results);
                    tooltipSameSkill.show(layout.getChildAt(1));
                }
            }


        }
    };

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (isGrid && position != expandedPosition) {
            setGridLayout(viewHolder, position);
        } else {
            setLinearLayout(viewHolder, position);
        }

    }

    private void setLinearLayout(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolderLinear viewHolderLinear = (ViewHolderLinear) viewHolder;


        if (position % 2 == 1) {
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background_alternate));
        } else {
            viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background));
        }

        if (monsterList.get(position).getMonsterInherit() != null && monsterList.get(position).getMonsterInherit().getMonsterId() != 0) {
            viewHolderLinear.monsterPicture.setBackgroundResource(R.drawable.portrait_stroke_small);
        } else {
            viewHolderLinear.monsterPicture.setBackgroundResource(0);
        }

//        viewHolderLinear.monsterPicture.setImageResource(monsterList.get(position).getMonsterPicture());
        viewHolderLinear.monsterPicture.setImageBitmap(monsterList.get(position).getMonsterPicture());
        viewHolderLinear.monsterPlus.setText(" +" + monsterList.get(position).getTotalPlus() + " ");
        viewHolderLinear.monsterName.setText(monsterList.get(position).getName());
        viewHolderLinear.monsterLevel.setText("Lv. " + monsterList.get(position).getCurrentLevel() + " - ");
        viewHolderLinear.monsterATK.setText(Integer.toString(monsterList.get(position).getTotalAtk()) + " / ");
        viewHolderLinear.monsterRCV.setText(Integer.toString(monsterList.get(position).getTotalRcv()));
        viewHolderLinear.monsterHP.setText(Integer.toString(monsterList.get(position).getTotalHp()) + " / ");
        viewHolderLinear.weightedBase.setText(String.valueOf(monsterList.get(position).getWeightedString()));
        viewHolderLinear.weightedTotal.setText(String.valueOf(monsterList.get(position).getTotalWeightedString()));
        viewHolderLinear.monsterAwakenings.setText(" " + Integer.toString(monsterList.get(position).getCurrentAwakenings()));

        if (monsterList.get(position).getMonsterId() == 0) {
            viewHolderLinear.type1.setVisibility(View.GONE);
            viewHolderLinear.type2.setVisibility(View.GONE);
            viewHolderLinear.type3.setVisibility(View.GONE);
            viewHolderLinear.monsterPlus.setVisibility(View.GONE);
            viewHolderLinear.monsterAwakenings.setVisibility(View.GONE);
            viewHolderLinear.monsterHP.setVisibility(View.GONE);
            viewHolderLinear.monsterATK.setVisibility(View.GONE);
            viewHolderLinear.monsterRCV.setVisibility(View.GONE);
            viewHolderLinear.monsterLevel.setVisibility(View.GONE);
            viewHolderLinear.favorite.setVisibility(View.INVISIBLE);
            viewHolderLinear.favoriteOutline.setVisibility(View.INVISIBLE);
        } else {
            viewHolderLinear.type1.setVisibility(View.VISIBLE);
            viewHolderLinear.type2.setVisibility(View.VISIBLE);
            viewHolderLinear.type3.setVisibility(View.VISIBLE);
            viewHolderLinear.monsterPlus.setVisibility(View.VISIBLE);
            viewHolderLinear.monsterPicture.setVisibility(View.VISIBLE);
            viewHolderLinear.monsterAwakenings.setVisibility(View.VISIBLE);
            viewHolderLinear.monsterHP.setVisibility(View.VISIBLE);
            viewHolderLinear.monsterATK.setVisibility(View.VISIBLE);
            viewHolderLinear.monsterRCV.setVisibility(View.VISIBLE);
            viewHolderLinear.monsterLevel.setVisibility(View.VISIBLE);
            viewHolderLinear.favorite.setVisibility(View.VISIBLE);
            viewHolderLinear.favoriteOutline.setVisibility(View.VISIBLE);
        }

        if (monsterList.get(position).getCurrentAwakenings() >= monsterList.get(position).getMaxAwakenings()) {
            viewHolderLinear.monsterAwakenings.setBackgroundResource(R.drawable.awakening_max);
            viewHolderLinear.monsterAwakenings.setText("");
        }

        if (latentList == null) {
            latentList = new ArrayList<>();
        } else {
            latentList.clear();
        }

        if (monsterList.get(position).getTotalPlus() == 297) {
            for (int i = 0; i < monsterList.get(position).getLatents().size(); i++) {
                if (monsterList.get(position).getLatents().get(i).getValue() != 0) {
                    latentList.add(1);
                }
            }
//            viewHolderLinear.latentHolder.getChildAt(viewHolderLinear.latentHolder.getChildCount() - 1).setVisibility(View.VISIBLE);
        } else {
            for (int i = 0; i < monsterList.get(position).getLatents().size() - 1; i++) {
                if (monsterList.get(position).getLatents().get(i).getValue() != 0) {
                    latentList.add(1);
                }
            }
//            viewHolderLinear.latentHolder.getChildAt(viewHolderLinear.latentHolder.getChildCount() - 1).setVisibility(View.GONE);
        }
        for (int i = 0; i < viewHolderLinear.latentHolder.getChildCount(); i++) {
            if (monsterList.get(position).getLatents().get(i).getValue() > 11) {
                viewHolderLinear.latentHolder.getChildAt(i + 1).setVisibility(View.GONE);
                i++;
            } else if (monsterList.get(position).getTotalPlus() == 297 && i == 5) {
                viewHolderLinear.latentHolder.getChildAt(i).setVisibility(View.VISIBLE);
            } else if (monsterList.get(position).getTotalPlus() != 297 && i == 5) {
                viewHolderLinear.latentHolder.getChildAt(i).setVisibility(View.GONE);
            } else {
                viewHolderLinear.latentHolder.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }
        if (latentList.size() == 0) {
            viewHolderLinear.monsterLatents.setVisibility(View.INVISIBLE);
        } else if (monsterList.get(position).getTotalPlus() == 297) {
            if (latentList.size() == 6) {
                viewHolderLinear.monsterLatents.setBackgroundResource(R.drawable.latent_max);
                viewHolderLinear.monsterLatents.setText("");
                viewHolderLinear.monsterLatents.setVisibility(View.VISIBLE);
            } else {
                viewHolderLinear.monsterLatents.setText(" " + latentList.size());
                viewHolderLinear.monsterLatents.setVisibility(View.VISIBLE);
            }
        } else {
            if (latentList.size() == 5) {
                viewHolderLinear.monsterLatents.setBackgroundResource(R.drawable.latent_max);
                viewHolderLinear.monsterLatents.setText("");
                viewHolderLinear.monsterLatents.setVisibility(View.VISIBLE);
            } else {
                viewHolderLinear.monsterLatents.setText(" " + latentList.size());
                viewHolderLinear.monsterLatents.setVisibility(View.VISIBLE);
            }
        }

        if (monsterList.get(position).getTotalPlus() == 0) {
            viewHolderLinear.monsterPlus.setVisibility(View.INVISIBLE);
        } else {
            viewHolderLinear.monsterPlus.setVisibility(View.VISIBLE);
        }

        if (monsterList.get(position).getCurrentAwakenings() == 0) {
            viewHolderLinear.monsterAwakenings.setVisibility(View.INVISIBLE);
            if (latentList.size() != 0) {
                ViewGroup.LayoutParams z = viewHolderLinear.monsterAwakenings.getLayoutParams();
                viewHolderLinear.monsterLatents.setLayoutParams(z);
            }
        } else {
            viewHolderLinear.monsterAwakenings.setVisibility(View.VISIBLE);
        }

        if (monsterList.get(position).getType1() >= 0) {
            viewHolderLinear.type1.setImageResource(ImageResourceUtil.monsterType(monsterList.get(position).getType1()));
        } else {
            viewHolderLinear.type1.setVisibility(View.INVISIBLE);
        }

        if (monsterList.get(position).getType2() >= 0) {
            viewHolderLinear.type2.setImageResource(ImageResourceUtil.monsterType(monsterList.get(position).getType2()));
        } else {
            viewHolderLinear.type2.setVisibility(View.INVISIBLE);
        }

        if (monsterList.get(position).getType3() >= 0) {
            viewHolderLinear.type3.setImageResource(ImageResourceUtil.monsterType(monsterList.get(position).getType3()));
        } else {
            viewHolderLinear.type3.setVisibility(View.INVISIBLE);
        }

        viewHolderLinear.favorite.setTag(R.string.index, position);
        viewHolderLinear.itemView.setTag(R.string.index, position);
        viewHolderLinear.choose.setTag(R.string.index, position);
        viewHolderLinear.delete.setTag(R.string.index, position);
        viewHolderLinear.favoriteOutline.setTag(R.string.index, position);

        if (monsterList.get(position).getMonsterId() == 0) {
            viewHolderLinear.itemView.setOnClickListener(monsterListOnClickListener);
        } else {
            viewHolderLinear.itemView.setOnClickListener(expandOnClickListener);
        }
        if (position == expandedPosition && monsterList.get(position).getMonsterId() != 0) {
            viewHolderLinear.expandLayout.setVisibility(View.VISIBLE);
            viewHolderLinear.rarity.setVisibility(View.VISIBLE);
            viewHolderLinear.rarityStar.setVisibility(View.VISIBLE);
            viewHolderLinear.monsterHP.setVisibility(View.GONE);
            viewHolderLinear.monsterATK.setVisibility(View.GONE);
            viewHolderLinear.monsterRCV.setVisibility(View.GONE);
            viewHolderLinear.monsterName.setVisibility(View.VISIBLE);
            viewHolderLinear.monsterLevel.setVisibility(View.VISIBLE);

            if (monsterList.get(position).isFavorite()) {
                viewHolderLinear.favorite.setVisibility(View.VISIBLE);
                viewHolderLinear.delete.setEnabled(false);
            } else {
                viewHolderLinear.favorite.setVisibility(View.INVISIBLE);
                viewHolderLinear.delete.setEnabled(true);
            }
            viewHolderLinear.favoriteOutline.setVisibility(View.VISIBLE);
            if (monsterList.get(position).getType1() > -1) {
                viewHolderLinear.type1.setVisibility(View.VISIBLE);
                viewHolderLinear.type2.setVisibility(View.INVISIBLE);
                viewHolderLinear.type3.setVisibility(View.INVISIBLE);
            }
            if (monsterList.get(position).getType2() > -1) {
                viewHolderLinear.type2.setVisibility(View.VISIBLE);
                viewHolderLinear.type3.setVisibility(View.INVISIBLE);
            }
            if (monsterList.get(position).getType3() > -1) {
                viewHolderLinear.type3.setVisibility(View.VISIBLE);
            }
//            if (monsterList.get(position).getMaxAwakenings() == 0 && monsterList.get(position).getMonsterId() != 14) {
//                RelativeLayout.LayoutParams z = (RelativeLayout.LayoutParams) viewHolderLinear.activeSkill.getLayoutParams();
//                z.addRule(RelativeLayout.BELOW, R.id.latentHolder);
//                RelativeLayout.LayoutParams x = (RelativeLayout.LayoutParams) viewHolderLinear.activeSkillName.getLayoutParams();
//                x.addRule(RelativeLayout.BELOW, R.id.latentHolder);
//            }
            for (int i = 0; i < 9; i++) {
                if (i >= monsterList.get(position).getMaxAwakenings()) {
                    viewHolderLinear.awakeningHolder.getChildAt(i).setVisibility(View.GONE);
                } else {
                    viewHolderLinear.awakeningHolder.getChildAt(i).setVisibility(View.VISIBLE);
                }
            }
            if (monsterList.get(position).getCurrentAwakenings() < monsterList.get(position).getMaxAwakenings()) {
                for (int j = 0; j < monsterList.get(position).getCurrentAwakenings(); j++) {
                    ((ImageView) viewHolderLinear.awakeningHolder.getChildAt(j)).setImageResource(ImageResourceUtil.monsterAwakening(monsterList.get(position).getAwokenSkills().get(j).getValue()));
                }

                for (int j = monsterList.get(position).getCurrentAwakenings(); j < monsterList.get(position).getMaxAwakenings(); j++) {
                    ((ImageView) viewHolderLinear.awakeningHolder.getChildAt(j)).setImageResource(ImageResourceUtil.monsterAwakeningDisabled(monsterList.get(position).getAwokenSkills().get(j).getValue()));
                }
            } else {
                for (int j = 0; j < monsterList.get(position).getMaxAwakenings(); j++) {
                    ((ImageView) viewHolderLinear.awakeningHolder.getChildAt(j)).setImageResource(ImageResourceUtil.monsterAwakening(monsterList.get(position).getAwokenSkills().get(j).getValue()));
                }
            }

            if (monsterList.get(position).getType1() == 0 || monsterList.get(position).getType1() == 14) {
                viewHolderLinear.latentHolder.setVisibility(View.GONE);
            } else {
                viewHolderLinear.latentHolder.setVisibility(View.VISIBLE);
                for (int j = 0; j < monsterList.get(position).getLatents().size(); j++) {
                    ((ImageView) viewHolderLinear.latentHolder.getChildAt(j)).setImageResource(ImageResourceUtil.monsterLatent(monsterList.get(position).getLatents().get(j).getValue()));
                }
            }
            viewHolderLinear.rarity.setText("" + monsterList.get(position).getRarity());
            viewHolderLinear.rarityStar.setColorFilter(0xFFD4D421);
            viewHolderLinear.hpBase.setText("" + monsterList.get(position).getCurrentHpInt());
            viewHolderLinear.atkBase.setText("" + monsterList.get(position).getCurrentAtkInt());
            viewHolderLinear.rcvBase.setText("" + monsterList.get(position).getCurrentRcvInt());
            viewHolderLinear.hpPlus.setText("+" + monsterList.get(position).getHpPlus());
            viewHolderLinear.atkPlus.setText("+" + monsterList.get(position).getAtkPlus());
            viewHolderLinear.rcvPlus.setText("+" + monsterList.get(position).getRcvPlus());
            viewHolderLinear.hpTotal.setText("" + monsterList.get(position).getTotalHp());
            viewHolderLinear.atkTotal.setText("" + monsterList.get(position).getTotalAtk());
            viewHolderLinear.rcvTotal.setText("" + monsterList.get(position).getTotalRcv());
            if (monsterList.get(position).getActiveSkillString().equals("Blank")) {
                viewHolderLinear.activeSkillName.setText("None");
                viewHolderLinear.activeSkillDesc.setVisibility(View.GONE);
                viewHolderLinear.activeSkillCooldown.setVisibility(View.GONE);
            } else {
                if (monsterList.get(position).getActiveSkill() == null) {
                    viewHolderLinear.activeSkillDesc.setText("This monster has an active skill but the author of this app has not realized that it doesn't exist in his database yet.");
                    viewHolderLinear.activeSkillCooldown.setVisibility(View.GONE);
                } else {
                    viewHolderLinear.activeSkillDesc.setText(monsterList.get(position).getActiveSkill().getDescription());
                    if (monsterList.get(position).getActiveSkillLevel() == monsterList.get(position).getActiveSkill().getMaxLevel()) {
                        viewHolderLinear.activeSkillCooldown.setText("(CD " + monsterList.get(position).getActiveSkill().getMinimumCooldown() + " (Max))");
                    } else {
                        viewHolderLinear.activeSkillCooldown.setText("(CD " + (monsterList.get(position).getActiveSkill().getMaximumCooldown() - monsterList.get(position).getActiveSkillLevel() + 1) + ")");
                    }
                    viewHolderLinear.activeSkillCooldown.setVisibility(View.VISIBLE);
                }
                viewHolderLinear.activeSkillName.setVisibility(View.VISIBLE);
                viewHolderLinear.activeSkillDesc.setVisibility(View.VISIBLE);
                viewHolderLinear.activeSkillName.setText(monsterList.get(position).getActiveSkillString());
            }

            if (monsterList.get(position).getMonsterInherit() != null && monsterList.get(position).getMonsterInherit().getMonsterId() != 0) {
                viewHolderLinear.skill2Holder.setVisibility(View.VISIBLE);
                viewHolderLinear.activeSkill.setImageResource(R.drawable.active_skill_1);
                if (monsterList.get(position).getMonsterInherit().getActiveSkill() == null) {
                    viewHolderLinear.activeSkill2Desc.setText("This monster has an active skill but the author of this app has not realized that it doesn't exist in his database yet.");
                    viewHolderLinear.activeSkill2Cooldown.setVisibility(View.GONE);
                } else {
                    viewHolderLinear.activeSkill2Desc.setText("" + monsterList.get(position).getMonsterInherit().getActiveSkill().getDescription());
                    if (monsterList.get(position).getMonsterInherit().getActiveSkillLevel() == monsterList.get(position).getMonsterInherit().getActiveSkill().getMaxLevel()) {
                        viewHolderLinear.activeSkill2Cooldown.setText("(CD " + (monsterList.get(position).getMonsterInherit().getActiveSkill().getMinimumCooldown() + monsterList.get(position).getActiveSkill().getMaximumCooldown() - monsterList.get(position).getActiveSkillLevel() + 1) + " (Max))");
                    } else {
                        viewHolderLinear.activeSkill2Cooldown.setText("(CD " + (monsterList.get(position).getMonsterInherit().getActiveSkill().getMaximumCooldown() - monsterList.get(position).getMonsterInherit().getActiveSkillLevel() + monsterList.get(position).getActiveSkill().getMaximumCooldown() - monsterList.get(position).getActiveSkillLevel() + 2) + ")");
                    }
                    viewHolderLinear.activeSkill2Cooldown.setVisibility(View.VISIBLE);
                }
                viewHolderLinear.activeSkill2Name.setText(monsterList.get(position).getMonsterInherit().getActiveSkillString());
            } else {
                viewHolderLinear.skill2Holder.setVisibility(View.GONE);
                viewHolderLinear.activeSkill.setImageResource(R.drawable.active_skill);
            }

            if (monsterList.get(position).getLeaderSkillString().equals("Blank")) {
                viewHolderLinear.leaderSkillName.setText("None");
                viewHolderLinear.leaderSkillDesc.setVisibility(View.GONE);
            } else {
                if (monsterList.get(position).getLeaderSkill() == null) {
                    viewHolderLinear.leaderSkillDesc.setText("This monster has a leader skill but the author of this app has not realized that it doesn't exist in his database yet.\n(Hint: This leader skill does nothing.)");
                } else {
                    viewHolderLinear.leaderSkillDesc.setText(monsterList.get(position).getLeaderSkill().getDescription());
                }
                viewHolderLinear.leaderSkillName.setVisibility(View.VISIBLE);
                viewHolderLinear.leaderSkillDesc.setVisibility(View.VISIBLE);
                viewHolderLinear.leaderSkillName.setText("" + monsterList.get(position).getLeaderSkillString() + "");
            }
            if (isGrid) {
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) viewHolderLinear.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
                viewHolderLinear.monsterPicture.getLayoutParams().height = fortyEightDp;
                viewHolderLinear.monsterPicture.getLayoutParams().width = fortyEightDp;
                viewHolderLinear.monsterPicture.requestLayout();
                viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background));
            }
        } else {
            viewHolderLinear.expandLayout.setVisibility(View.GONE);
            viewHolderLinear.rarity.setVisibility(View.GONE);
            viewHolderLinear.rarityStar.setVisibility(View.GONE);
            if (monsterList.get(position).getType1() != -1 && !isGrid) {
                viewHolderLinear.monsterHP.setVisibility(View.VISIBLE);
                viewHolderLinear.monsterATK.setVisibility(View.VISIBLE);
                viewHolderLinear.monsterRCV.setVisibility(View.VISIBLE);
            }
            if (isGrid) {
                ((StaggeredGridLayoutManager.LayoutParams) viewHolderLinear.itemView.getLayoutParams()).setFullSpan(false);
                viewHolderLinear.monsterPicture.getLayoutParams().height = fiftyFourDp;
                viewHolderLinear.monsterPicture.getLayoutParams().width = fiftyFourDp;
                viewHolderLinear.monsterPicture.requestLayout();
            }
        }
        if (monsterList.get(position).isFavorite()) {
            viewHolderLinear.favorite.setVisibility(View.VISIBLE);
        } else {
            viewHolderLinear.favorite.setVisibility(View.INVISIBLE);
        }
    }

    private void setGridLayout(RecyclerView.ViewHolder viewHolder, int position) {
        ViewHolderGrid viewHolderGrid = (ViewHolderGrid) viewHolder;
        viewHolder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.background));

        Log.d("SaveMonsterListRec", "monster is: " + monsterList.get(position));
        if (isManageMonsters) {
            viewHolderGrid.monsterScore.setVisibility(View.GONE);
        } else if (position != 0 && monsterPosition != 0 && monsterPosition != 5) {

            elementDamageReplace.clear();
            elementDamageReplace.addAll(elementDamage);
            totalDamageReplace = totalDamage;

            switch (monsterList.get(position).getElement1Int()) {
                case 0:
                    elementDamageReplace.set(0, elementDamageReplace.get(0) + monsterList.get(position).getTotalAtk());
                    totalDamageReplace += monsterList.get(position).getTotalAtk();
                    break;
                case 1:
                    elementDamageReplace.set(1, elementDamageReplace.get(1) + monsterList.get(position).getTotalAtk());
                    totalDamageReplace += monsterList.get(position).getTotalAtk();
                    break;
                case 2:
                    elementDamageReplace.set(2, elementDamageReplace.get(2) + monsterList.get(position).getTotalAtk());
                    totalDamageReplace += monsterList.get(position).getTotalAtk();
                    break;
                case 3:
                    elementDamageReplace.set(3, elementDamageReplace.get(3) + monsterList.get(position).getTotalAtk());
                    totalDamageReplace += monsterList.get(position).getTotalAtk();
                    break;
                case 4:
                    elementDamageReplace.set(4, elementDamageReplace.get(4) + monsterList.get(position).getTotalAtk());
                    totalDamageReplace += monsterList.get(position).getTotalAtk();
                    break;
            }
            if (monsterList.get(position).getElement1Int() == monsterList.get(position).getElement2Int()) {
                switch (monsterList.get(position).getElement2Int()) {
                    case 0:
                        elementDamageReplace.set(0, elementDamageReplace.get(0) + (double) monsterList.get(position).getTotalAtk() / 10);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 10;
                        break;
                    case 1:
                        elementDamageReplace.set(1, elementDamageReplace.get(1) + (double) monsterList.get(position).getTotalAtk() / 10);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 10;
                        break;
                    case 2:
                        elementDamageReplace.set(2, elementDamageReplace.get(2) + (double) monsterList.get(position).getTotalAtk() / 10);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 10;
                        break;
                    case 3:
                        elementDamageReplace.set(3, elementDamageReplace.get(3) + (double) monsterList.get(position).getTotalAtk() / 10);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 10;
                        break;
                    case 4:
                        elementDamageReplace.set(4, elementDamageReplace.get(4) + (double) monsterList.get(position).getTotalAtk() / 10);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 10;
                        break;
                }
            } else {
                switch (monsterList.get(position).getElement2Int()) {
                    case 0:
                        elementDamageReplace.set(0, elementDamageReplace.get(0) + (double) monsterList.get(position).getTotalAtk() / 3);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 3;
                        break;
                    case 1:
                        elementDamageReplace.set(1, elementDamageReplace.get(1) + (double) monsterList.get(position).getTotalAtk() / 3);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 3;
                        break;
                    case 2:
                        elementDamageReplace.set(2, elementDamageReplace.get(2) + (double) monsterList.get(position).getTotalAtk() / 3);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 3;
                        break;
                    case 3:
                        elementDamageReplace.set(2, elementDamageReplace.get(3) + (double) monsterList.get(position).getTotalAtk() / 3);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 3;
                        break;
                    case 4:
                        elementDamageReplace.set(4, elementDamageReplace.get(4) + (double) monsterList.get(position).getTotalAtk() / 3);
                        totalDamageReplace += monsterList.get(position).getTotalAtk() / 3;
                        break;
                }
            }

            for (int i = 0; i < elementDamageReplace.size(); i++) {
                elementDamageReplace.set(i, elementDamageReplace.get(i) / totalDamageReplace);
            }

            Log.d("SaveMonsterListRec", "Team awakenings are: " + teamAwakeningsSansReplace);
            Log.d("SaveMonsterListRec", "Awakening count is: " + awakeningCount);
            Log.d("SaveMonsterListRec", "elementDamage is: " + elementDamageReplace);

            viewHolderGrid.monsterScore.setVisibility(View.VISIBLE);
            viewHolderGrid.monsterScore.setText("" + df.format(ScoreMonsterUtil.scoreMonster(team, awakeningCount, elementDamageReplace, monsterList.get(position))));
        }

        if (monsterList.get(position).getMonsterInherit() != null && monsterList.get(position).getMonsterInherit().getMonsterId() != 0) {
            viewHolderGrid.monsterPicture.setBackgroundResource(R.drawable.portrait_stroke_small);
        } else {
            viewHolderGrid.monsterPicture.setBackgroundResource(0);
        }

        viewHolderGrid.itemView.setTag(R.string.index, position);
        if (monsterList.get(position).getMonsterId() == 0) {
            viewHolderGrid.itemView.setOnClickListener(monsterListOnClickListener);
        } else {
            viewHolderGrid.itemView.setOnClickListener(expandOnClickListener);
        }

        if (monsterList.get(position).getMonsterId() == 0) {
            viewHolderGrid.monsterPlus.setVisibility(View.GONE);
            viewHolderGrid.monsterAwakenings.setVisibility(View.GONE);
        } else {
            viewHolderGrid.monsterPlus.setVisibility(View.VISIBLE);
            viewHolderGrid.monsterPicture.setVisibility(View.VISIBLE);
            viewHolderGrid.monsterAwakenings.setVisibility(View.VISIBLE);
        }

//        viewHolderGrid.monsterPicture.setImageResource(monsterList.get(position).getMonsterPicture());
        viewHolderGrid.monsterPicture.setImageBitmap(monsterList.get(position).getMonsterPicture());
        if (monsterList.get(position).getTotalPlus() != 0) {
            viewHolderGrid.monsterPlus.setText(" +" + monsterList.get(position).getTotalPlus() + " ");
        } else {
            viewHolderGrid.monsterPlus.setVisibility(View.GONE);
        }
        viewHolderGrid.monsterAwakenings.setText(" " + Integer.toString(monsterList.get(position).getCurrentAwakenings()));
        if (monsterList.get(position).getCurrentAwakenings() >= monsterList.get(position).getMaxAwakenings()) {
            viewHolderGrid.monsterAwakenings.setBackgroundResource(R.drawable.awakening_max);
            viewHolderGrid.monsterAwakenings.setText("");
        }

        if (latentList == null) {
            latentList = new ArrayList<>();
        } else {
            latentList.clear();
        }

        if (monsterList.get(position).getTotalPlus() == 297) {
            for (int i = 0; i < monsterList.get(position).getLatents().size(); i++) {
                if (monsterList.get(position).getLatents().get(i).getValue() != 0) {
                    latentList.add(1);
                }
            }
        } else {
            for (int i = 0; i < monsterList.get(position).getLatents().size() - 1; i++) {
                if (monsterList.get(position).getLatents().get(i).getValue() != 0) {
                    latentList.add(1);
                }
            }
        }
        if (latentList.size() == 0) {
            viewHolderGrid.monsterLatents.setVisibility(View.INVISIBLE);
        } else if (monsterList.get(position).getTotalPlus() == 297) {
            if (latentList.size() == 6) {
                viewHolderGrid.monsterLatents.setBackgroundResource(R.drawable.latent_max);
                viewHolderGrid.monsterLatents.setText("");
                viewHolderGrid.monsterLatents.setVisibility(View.VISIBLE);
            } else {
                viewHolderGrid.monsterLatents.setText(" " + latentList.size());
                viewHolderGrid.monsterLatents.setVisibility(View.VISIBLE);
            }
        } else {
            if (latentList.size() == 5) {
                viewHolderGrid.monsterLatents.setBackgroundResource(R.drawable.latent_max);
                viewHolderGrid.monsterLatents.setText("");
                viewHolderGrid.monsterLatents.setVisibility(View.VISIBLE);
            } else {
                viewHolderGrid.monsterLatents.setText(" " + latentList.size());
                viewHolderGrid.monsterLatents.setVisibility(View.VISIBLE);
            }
        }

        if (monsterList.get(position).getTotalPlus() == 0) {
            viewHolderGrid.monsterPlus.setVisibility(View.INVISIBLE);
        } else {
            viewHolderGrid.monsterPlus.setVisibility(View.VISIBLE);
        }

        if (monsterList.get(position).getCurrentAwakenings() == 0) {
            viewHolderGrid.monsterAwakenings.setVisibility(View.INVISIBLE);
            if (latentList.size() != 0) {
                ViewGroup.LayoutParams z = viewHolderGrid.monsterAwakenings.getLayoutParams();
                viewHolderGrid.monsterLatents.setLayoutParams(z);
            }
        } else {
            viewHolderGrid.monsterAwakenings.setVisibility(View.VISIBLE);
        }

        if (monsterList.get(position).isFavorite()) {
            viewHolderGrid.favorite.setVisibility(View.VISIBLE);
            viewHolderGrid.favoriteOutline.setVisibility(View.VISIBLE);
        } else {
            viewHolderGrid.favorite.setVisibility(View.INVISIBLE);
            viewHolderGrid.favoriteOutline.setVisibility(View.INVISIBLE);
        }
    }

    private View.OnClickListener favoriteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (int) v.getTag(R.string.index);
            realm.beginTransaction();
//            Monster monster = realm.copyToRealmOrUpdate(monsterList.get(position));
            Monster monster = monsterList.get(position);
            if (!monster.isFavorite()) {
                monster.setFavorite(true);
                monsterList.get(position).setFavorite(true);
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(mContext, "Monster favorited", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                monster.setFavorite(false);
                monsterList.get(position).setFavorite(false);
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(mContext, "Monster unfavorited", Toast.LENGTH_SHORT);
                toast.show();
            }
            realm.commitTransaction();
            notifyItemChanged(position);
        }
    };

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case GRID:
                ViewHolderGrid viewHolderGrid = new ViewHolderGrid(inflater.inflate(R.layout.save_monster_list_grid, parent, false));
//                viewHolderGrid.itemView.setTag(viewHolderGrid);
                viewHolderGrid.itemView.setOnLongClickListener(monsterListOnLongClickListener);
                viewHolderGrid.favorite.setColorFilter(0xFFFFAADD);
                return viewHolderGrid;
            default:
                ViewHolderLinear viewHolderLinear = new ViewHolderLinear(inflater.inflate(R.layout.save_monster_list_row, parent, false));

                viewHolderLinear.favorite.setColorFilter(0xFFFFAADD);

                viewHolderLinear.favorite.setOnClickListener(favoriteOnClickListener);
                viewHolderLinear.favoriteOutline.setOnClickListener(favoriteOnClickListener);

                viewHolderLinear.monsterName.setHorizontallyScrolling(true);
                viewHolderLinear.leaderSkillName.setHorizontallyScrolling(true);
                viewHolderLinear.activeSkillName.setHorizontallyScrolling(true);
                viewHolderLinear.monsterName.setSelected(true);
                viewHolderLinear.leaderSkillName.setSelected(true);
                viewHolderLinear.activeSkillName.setSelected(true);

//                viewHolderLinear.itemView.setTag(viewHolderLinear);
                viewHolderLinear.choose.setOnClickListener(monsterListOnClickListener);
                viewHolderLinear.delete.setOnClickListener(deleteOnClickListener);
                viewHolderLinear.itemView.setOnLongClickListener(monsterListOnLongClickListener);
                viewHolderLinear.skill1Holder.setOnClickListener(sameSkillToolTipOnClickListener);
                viewHolderLinear.skill1Holder.setTag(viewHolderLinear.skill1Holder);
                viewHolderLinear.skill2Holder.setOnClickListener(sameSkillToolTipOnClickListener);
                viewHolderLinear.skill2Holder.setTag(viewHolderLinear.skill2Holder);

                return viewHolderLinear;

        }


    }

    @Override
    public int getItemViewType(int position) {
        if (isGrid && position != expandedPosition) {
            return GRID;
        } else {
            return LINEAR;
        }
    }

    @Override
    public int getItemCount() {
        return monsterList.size();
    }

    static class ViewHolderLinear extends RecyclerView.ViewHolder {
        TextView monsterName, monsterPlus, monsterAwakenings, monsterHP, monsterATK, monsterRCV,
                monsterLevel, monsterLatents, hpBase, hpPlus, hpTotal, atkBase, atkPlus, atkTotal,
                rcvBase, rcvPlus, rcvTotal, rarity, leaderSkillName, leaderSkillDesc,
                activeSkillName, activeSkillDesc, activeSkillCooldown, weightedBase, weightedTotal,
                activeSkill2Name, activeSkill2Cooldown, activeSkill2Desc;
        ImageView monsterPicture, type1, type2, type3, favorite, favoriteOutline,
                awakening1, awakening2, awakening3, awakening4, awakening5, awakening6, awakening7,
                awakening8, awakening9, latent1, latent2, latent3, latent4, latent5, rarityStar,
                leaderSkill, activeSkill, activeSkill2;
        RelativeLayout expandLayout;
        LinearLayout awakeningHolder, latentHolder;
        Button choose, delete;
        RelativeLayout relativeLayout, skill1Holder, skill2Holder, leaderSkillHolder;

        public ViewHolderLinear(View convertView) {
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
            delete = (Button) convertView.findViewById(R.id.delete);
            leaderSkill = (ImageView) convertView.findViewById(R.id.leaderSkill);
            leaderSkillDesc = (TextView) convertView.findViewById(R.id.leaderSkillDesc);
            leaderSkillName = (TextView) convertView.findViewById(R.id.leaderSkillName);
            relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout);
            activeSkill = (ImageView) convertView.findViewById(R.id.activeSkill);
            activeSkillDesc = (TextView) convertView.findViewById(R.id.activeSkillDesc);
            activeSkillName = (TextView) convertView.findViewById(R.id.activeSkillName);
            activeSkillCooldown = (TextView) convertView.findViewById(R.id.activeSkillCooldown);
            skill1Holder = (RelativeLayout) convertView.findViewById(R.id.skill1Holder);
            activeSkill2 = (ImageView) convertView.findViewById(R.id.activeSkill2);
            activeSkill2Desc = (TextView) convertView.findViewById(R.id.activeSkill2Desc);
            activeSkill2Name = (TextView) convertView.findViewById(R.id.activeSkill2Name);
            activeSkill2Cooldown = (TextView) convertView.findViewById(R.id.activeSkill2Cooldown);
            skill2Holder = (RelativeLayout) convertView.findViewById(R.id.skill2Holder);
            leaderSkillHolder = (RelativeLayout) convertView.findViewById(R.id.leaderSkillHolder);
            weightedBase = (TextView) convertView.findViewById(R.id.weightedBase);
            weightedTotal = (TextView) convertView.findViewById(R.id.weightedTotal);
        }
    }

    static class ViewHolderGrid extends RecyclerView.ViewHolder {
        TextView monsterPlus;
        TextStroke monsterAwakenings, monsterLatents, monsterScore;
        ImageView monsterPicture, favorite, favoriteOutline;

        public ViewHolderGrid(View convertView) {
            super(convertView);
            monsterPlus = (TextView) convertView.findViewById(R.id.monsterPlus);
            monsterAwakenings = (TextStroke) convertView.findViewById(R.id.monsterAwakenings);
            monsterLatents = (TextStroke) convertView.findViewById(R.id.monsterLatents);
            monsterScore = (TextStroke) convertView.findViewById(R.id.monsterScore);
            monsterPicture = (ImageView) convertView.findViewById(R.id.monsterPicture);
            favorite = (ImageView) convertView.findViewById(R.id.favorite);
            favoriteOutline = (ImageView) convertView.findViewById(R.id.favoriteOutline);
        }
    }

    public void notifyDataSetChanged(ArrayList<Monster> monsterList) {
        this.monsterList = monsterList;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged(boolean isGrid) {
        this.isGrid = isGrid;
        notifyDataSetChanged();
    }

    public int getExpandedPosition() {
        return expandedPosition;
    }

    public ArrayList<Monster> getMonsterList() {
        return monsterList;
    }

    public void setMonsterList(ArrayList<Monster> monsterList) {
        this.monsterList = monsterList;
    }

    public Monster getItem(int position) {
        return monsterList.get(position);
    }

    public boolean expanded() {
        if (expandedPosition != -1) {
            return true;
        } else return false;
    }

    public void setExpandedPosition(int expandedPosition) {
        int previous = this.expandedPosition;
        this.expandedPosition = expandedPosition;
        notifyItemChanged(previous);
        if (expandedPosition >= 0) {
            notifyItemChanged(expandedPosition);
        }
    }

    public void expandItem(int position) {
        int previous;
        if (position != expandedPosition) {
            if (expandedPosition >= 0) {
                previous = expandedPosition;
                notifyItemChanged(previous);
            }
            expandedPosition = position;
            notifyItemChanged(expandedPosition);
            monsterListView.smoothScrollToPosition(expandedPosition);
        } else {
            previous = expandedPosition;
            expandedPosition = -1;
            notifyItemChanged(previous);
        }
    }

    protected void setUpTeam(Team team) {
        if (teamAwakeningsSansReplace == null) {
            teamAwakeningsSansReplace = new ArrayList<>();
        }
        awakeningCount = new ArrayList<>();
        elementDamage = new ArrayList<>();
        elementDamageReplace = new ArrayList<>();
        for (int i = 0; i <= Constants.numOfAwakenings; i++) {
            awakeningCount.add(0);
        }
        for (int i = 0; i < 5; i++) {
            elementDamage.add(0d);
            elementDamageReplace.add(0d);
        }
        teamAwakeningsSansReplace.clear();
        totalDamage = 0;
        for (int i = 0; i < team.getMonsters().size(); i++) {
            if (i != monsterPosition) {
                for (int j = 0; j < team.getMonsters().get(i).getCurrentAwakenings(); j++) {
                    teamAwakeningsSansReplace.add(team.getMonsters().get(i).getAwokenSkills(j));
                }
                switch (team.getMonsters().get(i).getElement1Int()) {
                    case 0:
                        elementDamage.set(0, elementDamage.get(0) + team.getMonsters().get(i).getTotalAtk());
                        totalDamage += team.getMonsters().get(i).getTotalAtk();
                        break;
                    case 1:
                        elementDamage.set(1, elementDamage.get(1) + team.getMonsters().get(i).getTotalAtk());
                        totalDamage += team.getMonsters().get(i).getTotalAtk();
                        break;
                    case 2:
                        elementDamage.set(2, elementDamage.get(2) + team.getMonsters().get(i).getTotalAtk());
                        totalDamage += team.getMonsters().get(i).getTotalAtk();
                        break;
                    case 3:
                        elementDamage.set(3, elementDamage.get(3) + team.getMonsters().get(i).getTotalAtk());
                        totalDamage += team.getMonsters().get(i).getTotalAtk();
                        break;
                    case 4:
                        elementDamage.set(4, elementDamage.get(4) + team.getMonsters().get(i).getTotalAtk());
                        totalDamage += team.getMonsters().get(i).getTotalAtk();
                        break;
                }
                if (team.getMonsters().get(i).getElement1Int() == team.getMonsters().get(i).getElement2Int()) {
                    switch (team.getMonsters().get(i).getElement2Int()) {
                        case 0:
                            elementDamage.set(0, elementDamage.get(0) + (double) team.getMonsters().get(i).getTotalAtk() / 10);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 10;
                            break;
                        case 1:
                            elementDamage.set(1, elementDamage.get(1) + (double) team.getMonsters().get(i).getTotalAtk() / 10);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 10;
                            break;
                        case 2:
                            elementDamage.set(2, elementDamage.get(2) + (double) team.getMonsters().get(i).getTotalAtk() / 10);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 10;
                            break;
                        case 3:
                            elementDamage.set(3, elementDamage.get(3) + (double) team.getMonsters().get(i).getTotalAtk() / 10);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 10;
                            break;
                        case 4:
                            elementDamage.set(4, elementDamage.get(4) + (double) team.getMonsters().get(i).getTotalAtk() / 10);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 10;
                            break;
                    }
                } else {
                    switch (team.getMonsters().get(i).getElement2Int()) {
                        case 0:
                            elementDamage.set(0, elementDamage.get(0) + (double) team.getMonsters().get(i).getTotalAtk() / 3);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 3;
                            break;
                        case 1:
                            elementDamage.set(1, elementDamage.get(1) + (double) team.getMonsters().get(i).getTotalAtk() / 3);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 3;
                            break;
                        case 2:
                            elementDamage.set(2, elementDamage.get(2) + (double) team.getMonsters().get(i).getTotalAtk() / 3);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 3;
                            break;
                        case 3:
                            elementDamage.set(2, elementDamage.get(3) + (double) team.getMonsters().get(i).getTotalAtk() / 3);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 3;
                            break;
                        case 4:
                            elementDamage.set(4, elementDamage.get(4) + (double) team.getMonsters().get(i).getTotalAtk() / 3);
                            totalDamage += team.getMonsters().get(i).getTotalAtk() / 3;
                            break;
                    }
                }

            }
        }
        Collections.sort(teamAwakeningsSansReplace);

        int counter = 0;
        for (int i = 0; i < teamAwakeningsSansReplace.size(); i++) {
            if (i == 0) {
                counter++;
            } else {
                if (!teamAwakeningsSansReplace.get(i).equals(teamAwakeningsSansReplace.get(i - 1))) {
                    awakeningCount.set(teamAwakeningsSansReplace.get(i - 1) - 1, counter);
                    counter = 1;
                } else {
                    counter++;
                }
            }
            if (i == teamAwakeningsSansReplace.size() - 1) {
                awakeningCount.set(teamAwakeningsSansReplace.get(i) - 1, counter);
            }
        }
    }
}

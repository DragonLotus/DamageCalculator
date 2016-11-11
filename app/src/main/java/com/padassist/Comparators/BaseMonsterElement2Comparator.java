package com.padassist.Comparators;

import com.padassist.Data.BaseMonster;

import java.util.Comparator;

/**
 * Created by DragonLotus on 10/4/2015.
 */
public class BaseMonsterElement2Comparator implements Comparator<BaseMonster> {
    @Override
    public int compare(BaseMonster lhs, BaseMonster rhs) {
        if (lhs.getMonsterId() == 0) {
            return -1;
        } else if (rhs.getMonsterId() == 0) {
            return 1;
        } else if (lhs.getElement2Int() == -1 && rhs.getElement2Int() == -1) {
            if (lhs.getRarity() < rhs.getRarity()) {
                return 1;
            } else if (lhs.getRarity() == rhs.getRarity()) {
                if (lhs.getElement1Int() > rhs.getElement1Int()) {
                    return 1;
                } else if (lhs.getElement1Int() == rhs.getElement2Int()) {
                    if (lhs.getRarity() < rhs.getRarity()) {
                        return 1;
                    } else if (lhs.getRarity() == rhs.getRarity()) {
                        if (lhs.getMonsterId() > rhs.getMonsterId()) {
                            return 1;
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        } else if (lhs.getElement2Int() == -1) {
            return 1;
        } else if (rhs.getElement2Int() == -1) {
            return -1;
        } else {
            if (lhs.getElement2Int() > rhs.getElement2Int()) {
                return 1;
            } else if (lhs.getElement2Int() == rhs.getElement2Int()) {
                if (lhs.getRarity() < rhs.getRarity()) {
                    return 1;
                } else if (lhs.getRarity() == rhs.getRarity()) {
                    if (lhs.getElement1Int() > rhs.getElement1Int()) {
                        return 1;
                    } else if (lhs.getElement1Int() == rhs.getElement2Int()) {
                        if (lhs.getRarity() < rhs.getRarity()) {
                            return 1;
                        } else if (lhs.getRarity() == rhs.getRarity()) {
                            if (lhs.getMonsterId() > rhs.getMonsterId()) {
                                return 1;
                            } else {
                                return -1;
                            }
                        } else {
                            return -1;
                        }
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }
    }
}
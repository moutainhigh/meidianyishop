package com.meidianyi.shop.service.shop.order.action.base;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 回溯+剪枝+位运算
 * https://leetcode-cn.com/problems/smallest-sufficient-team/solution/java-hui-su-jian-zhi-wei-yun-suan-by-mxnhujryvx/
 * @author 孔德成
 * @date 2020/7/21 15:12
 */
public class OrderGoodsPrescriptionCalculate {




    public static int[] smallestSufficientTeam(List<Integer> reqSkills, List<List<Integer>> people) {
        int[] result = null;
        int skillLen = reqSkills.size(), mustSkill = (1 << skillLen) - 1, peopleSize = people.size();
        //技能对应二进制，用1，10，100来表示
        Map<Integer, Integer> skillMap = new HashMap<Integer, Integer>(skillLen << 1);
        for (int i = 0; i < skillLen; i++) {
            skillMap.put(reqSkills.get(i), 1 << i);
        }
        //员工技能对应二进制数
        int[] peopleSkillNums = new int[peopleSize];
        int[] peopleSubscript = new int[1 << skillLen];
        for (int i = 0; i < people.size(); i++) {
            int skillNum = 0;
            List<Integer> skills = people.get(i);
            for (Integer skill : skills) {
                skillNum += skillMap.get(skill);
            }
            peopleSkillNums[i] = skillNum;
            peopleSubscript[skillNum] = i;
        }
        //技能或运算结果
        int comSkills = 0;
        //和其他所有员工没交集对应员工数量
        int aloneCount = 0;
        //和其他所有员工没交集对应员工数组下标
        int[] noIntersectionArr = new int[skillLen];
        for (int i = 0; i < peopleSize; i++) {
            if (peopleSkillNums[i] == 0) {
                continue;
            }
            //是否无交集
            boolean isNoIntersection = true;
            for (int j = 0; j < peopleSize; j++) {
                if (peopleSkillNums[j] == 0 || i == j) {
                    continue;
                }
                //重复的，去重
                if (peopleSkillNums[i] == peopleSkillNums[j]) {
                    peopleSkillNums[j] = 0;
                    continue;
                }
                //若一个员工技能是另外一个员工子集，则必定不在最优解中，去除
                if ((peopleSkillNums[i] | peopleSkillNums[j]) == peopleSkillNums[j]) {
                    peopleSkillNums[i] = 0;
                    isNoIntersection = false;
                    break;
                } else if ((peopleSkillNums[i] | peopleSkillNums[j]) == peopleSkillNums[i]) {
                    peopleSkillNums[j] = 0;
                    continue;
                }
                if ((peopleSkillNums[i] & peopleSkillNums[j]) != 0) {
                    isNoIntersection = false;
                    break;
                }
            }
            //无交集员工提前保存，方便之后回溯（降低后续回溯深度）
            if (isNoIntersection) {
                comSkills |= peopleSkillNums[i];
                noIntersectionArr[aloneCount] = peopleSubscript[peopleSkillNums[i]];
                peopleSkillNums[i] = 0;
                aloneCount++;
            }
        }
        //员工技能数字排序
        Arrays.sort(peopleSkillNums);
        //最小回溯深度，由小到大，则第一个得到结果就为最优解
        int minDepth = aloneCount;
        //若无交集员工技能组成等于必须技能，则输出结果，否则开始回溯深度+1
        if (comSkills == mustSkill) {
            result = new int[aloneCount];
            System.arraycopy(noIntersectionArr, 0, result, 0, aloneCount);
            return result;
        } else {
            minDepth++;
        }
        //从minDepth回溯深度开始回溯，noIntersectionArr肯定在该结果中，回溯深度从无交集员工数量开始
        for (int i = minDepth; i < skillLen; i++) {
            result = new int[i];
            System.arraycopy(noIntersectionArr, 0, result, 0, aloneCount);
            if (addNextPeople(peopleSubscript, i,mustSkill, comSkills, result, peopleSkillNums, aloneCount)) {
                break;
            }
        }
        return result;
    }

    private static boolean addNextPeople(int[] peopleSubscript,int currentDepth,int mustSkill, int comSkills, int[] result, int[] peopleSkillNums, int count) {
        //判断是否为解
        if (mustSkill == comSkills) {
            return true;
        }
        //大于回溯深度，则不存在
        if (count >= currentDepth) {
            return false;
        }
        for (int i = peopleSkillNums.length - 1; i >= 0; i--) {
            int skillNum = peopleSkillNums[i];
            //由于排序，则技能为0则后续都为0，直接结束
            if (skillNum == 0) {
                break;
            }
            //组合技能已包含该技能，则跳过
            if ((comSkills | skillNum) == comSkills) {
                continue;
            }
            result[count] = peopleSubscript[peopleSkillNums[i]];
            peopleSkillNums[i] = 0;
            if (addNextPeople(peopleSubscript,currentDepth,mustSkill, comSkills | skillNum, result, peopleSkillNums, count + 1)) {
                return true;
            }
            peopleSkillNums[i] = skillNum;
        }
        return false;
    }


}

package net.librec.eval.ranking;

import net.librec.math.structure.SparseMatrix;
import net.librec.recommender.AbstractRecommender;
import net.librec.recommender.item.ItemEntry;
import net.librec.recommender.item.RecommendedList;

import java.util.*;

import net.librec.eval.AbstractRecommenderEvaluator;
import net.librec.math.structure.SparseMatrix;

import java.util.List;
import java.util.Set;


/**
 * Created by himanabdollahpouri on 10/30/17.
 */
public class LongTailUtilityEvaluator extends AbstractRecommenderEvaluator {

/**
 * Copyright (C) 2016 LibRec
 * <p>
 * This file is part of LibRec.
 * LibRec is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * LibRec is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with LibRec. If not, see <http://www.gnu.org/licenses/>.
 */


/**
 * PrecisionEvaluator, calculate precision@n
 *
 * @author WangYuFeng
 */

    /**
     * Evaluate on the test set with the the list of recommended items.
     *
     * @param testMatrix      the given test set
     * @param recommendedList the list of recommended items
     * @return evaluate result
     */
    public double evaluate(SparseMatrix trainMatrix, SparseMatrix testMatrix, RecommendedList recommendedList) {
        double totalUtility = 0.0;
        int numUsers = testMatrix.numRows();
        int numItems = testMatrix.numColumns();

        int nonZeroNumUsers = 0;
        List<Float> item_priorityList = new ArrayList<>();

        for (int itemIdx = 0; itemIdx < numItems; ++itemIdx) {

            if (trainMatrix.columnSize(itemIdx)>100)
                item_priorityList.add((float)1.0);
            else if (trainMatrix.columnSize(itemIdx)<=100 && trainMatrix.columnSize(itemIdx)>6)
                item_priorityList.add((float)5.0);
            else item_priorityList.add((float)2.0);
        }

        for (int userID = 0; userID < numUsers; userID++) {
            Set<Integer> testSetByUser = testMatrix.getColumnsSet(userID);
            if (testSetByUser.size() > 0) {
                List<ItemEntry<Integer, Double>> recommendListByUser = recommendedList.getItemIdxListByUserIdx(userID);

                int numHits = 0;

                float sumOfItemPopularity = 0;


                int topK = this.topN <= recommendListByUser.size() ? this.topN : recommendListByUser.size();
                for (int indexOfItem = 0; indexOfItem < topK; indexOfItem++) {

                    int itemID = recommendListByUser.get(indexOfItem).getKey();
                    float item_priority = item_priorityList.get(itemID);
                    //numHits += item_priority;
                    if (testSetByUser.contains(itemID)) {
                        sumOfItemPopularity+=item_priority;
                    }
                }
                totalUtility += (float)sumOfItemPopularity/(5*this.topN);
                nonZeroNumUsers++;
            }
        }
        return nonZeroNumUsers > 0 ? (float)totalUtility / nonZeroNumUsers : 0.0d;
    }
}

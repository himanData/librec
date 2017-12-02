package net.librec.eval.ranking;

import net.librec.eval.AbstractRecommenderEvaluator;
import net.librec.math.structure.SparseMatrix;
import net.librec.recommender.item.ItemEntry;
import net.librec.recommender.item.RecommendedList;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by himanabdollahpouri on 11/5/17.
 */
public class LongTailPercentageEvaluator extends AbstractRecommenderEvaluator {


    public double evaluate(SparseMatrix trainMatrix, SparseMatrix testMatrix, RecommendedList recommendedList) {
        double totalUtility = 0.0;
        int numUsers = testMatrix.numRows();
        int numItems = testMatrix.numColumns();

        int nonZeroNumUsers = 0;


        float total_longtail = 0;
        for (int userID = 0; userID < numUsers; userID++) {
            Set<Integer> testSetByUser = testMatrix.getColumnsSet(userID);
            if (testSetByUser.size() > 0) {
                List<ItemEntry<Integer, Double>> recommendListByUser = recommendedList.getItemIdxListByUserIdx(userID);

                int numHits = 0;
                int longt = 0;
                float sumOfItemPopularity = 0;


                int topK = this.topN <= recommendListByUser.size() ? this.topN : recommendListByUser.size();
                for (int indexOfItem = 0; indexOfItem < topK; indexOfItem++) {
                    int itemID = recommendListByUser.get(indexOfItem).getKey();
                    if (testSetByUser.contains(itemID)) {
                        numHits += 1;
                    }

                    //System.out.println(trainMatrix.columnSize(itemID));
                    if (trainMatrix.columnSize(itemID) < 100)
                        longt += 1;
                }
                total_longtail +=(float) longt/this.topN;
                nonZeroNumUsers++;
            }
        }
         return (float) total_longtail / nonZeroNumUsers;
        //return total_longtail;
    }
}

package net.librec.tool.driver;

import com.google.common.collect.BiMap;
import net.librec.conf.Configuration;
import net.librec.data.DataModel;
import net.librec.data.model.TextDataModel;
import net.librec.eval.RecommenderEvaluator;
import net.librec.eval.ranking.NormalizedDCGEvaluator;
import net.librec.eval.ranking.PrecisionEvaluator;
import net.librec.eval.rating.MAEEvaluator;
import net.librec.filter.GenericRecommendedFilter;
import net.librec.filter.RecommendedFilter;
import net.librec.job.RecommenderJob;
import net.librec.recommender.AbstractRecommender;
import net.librec.recommender.Recommender;
import net.librec.recommender.RecommenderContext;
import net.librec.recommender.cf.UserKNNRecommender;
import net.librec.recommender.cf.rating.BPMFRecommender;
import net.librec.similarity.PCCSimilarity;
import net.librec.similarity.RecommenderSimilarity;
import net.librec.util.DriverClassUtil;

import javax.imageio.plugins.bmp.BMPImageWriteParam;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by himanabdollahpouri on 10/21/17.
 */
public class MainDriver {

    public static void main(String[] args) throws Exception {

        // recommender configuration
        Configuration conf = new Configuration();
        Configuration.Resource resource = new Configuration.Resource("rec/librec-default.properties");
        conf.addResource(resource);

        // build data model
        DataModel dataModel = new TextDataModel(conf);
        dataModel.buildDataModel();

        // set recommendation context
        RecommenderContext context = new RecommenderContext(conf, dataModel);
        RecommenderSimilarity similarity = new PCCSimilarity();
        similarity.buildSimilarityMatrix(dataModel);
        context.setSimilarity(similarity);

        // training
        BPMFRecommender recommender = new BPMFRecommender();

        Set<String> userIds = dataModel.getUserMappingData().keySet();
        Set<String> itemIds = dataModel.getItemMappingData().keySet();
        recommender.recommend(context);

        System.out.println(dataModel.getDataSplitter().getTestData().getColumnsSet(dataModel.getUserMappingData().get("912")));
        double rating;

/*
        for (String user : userIds) {
            for (String item : itemIds) {
                rating = dataModel.getDataSplitter().getTrainData().get(dataModel.getUserMappingData().get(user), dataModel.getItemMappingData().get(item));
                if (rating != 0) {

                }

            }
        }*/
        recommender.recommend(context);


        // evaluation
        RecommenderEvaluator precision = new PrecisionEvaluator();
        RecommenderEvaluator ndcg = new NormalizedDCGEvaluator();
        precision.setTopN(conf.getInt("rec.recommender.ranking.topn", 100000));
        ndcg.setTopN(conf.getInt("rec.recommender.ranking.topn", 100000));

        System.out.println("Precision= " + recommender.evaluate(precision));
        System.out.println("NDCG= " + recommender.evaluate(ndcg));

        // recommendation results
        List recommendedItemList = recommender.getRecommendedList();
        RecommendedFilter filter = new GenericRecommendedFilter();
        recommendedItemList = filter.filter(recommendedItemList);

       //Save results
        RecommenderJob recommenderJob = new RecommenderJob(conf);
        recommenderJob.saveResult(recommendedItemList, context);
    }
}

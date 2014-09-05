package org.mymahout.recommendation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.TanimotoCoefficientSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.CosineSimilarity;


/**
 * 协同过滤userCF
 * @author shijie
 * Ref: 
 * http://blog.fens.me/hadoop-mahout-maven-eclipse/
 */
public class UserCF {
    final static int NEIGHBORHOOD_NUM = 2;
    final static int RECOMMENDER_NUM = 3;

    public static void main(String[] args) throws IOException, TasteException {
        String file = "datafile/item.csv";
        DataModel model = new FileDataModel(new File(file));
        
        // what's EuclideanDistanceSimilarity ? difference between PearsonCorrelationSimilarity, UncenteredCosineSimilarity
        UserSimilarity userSimilarity = new EuclideanDistanceSimilarity(model);
//        new PearsonCorrelationSimilarity(model);
//        new PearsonCorrelationSimilarity(model, Weighting.WEIGHTED);
//        new EuclideanDistanceSimilarity(model);
//        new UncenteredCosineSimilarity(model);
//        new SpearmanCorrelationSimilarity(model);
        new TanimotoCoefficientSimilarity(model);
        
        NearestNUserNeighborhood neighbor = new NearestNUserNeighborhood(NEIGHBORHOOD_NUM, userSimilarity, model);
        
        // userId = 1
        long length = neighbor.getUserNeighborhood(1).length;
        for (int i = 0; i < length; i++) {
            long neighborhood = neighbor.getUserNeighborhood(1)[i];
            System.out.println("neighbor = " + neighborhood + "; Similarity = " + userSimilarity.userSimilarity(1, neighborhood));
        }
        // create recommender engine
        Recommender r = new GenericUserBasedRecommender(model, neighbor, userSimilarity);
        // recommender two items for user1 
        //r.recommend(1, 2); (userId, how many items)
        
        // iterator all the users
        LongPrimitiveIterator iter = model.getUserIDs();

        while (iter.hasNext()) {
            long uid = iter.nextLong();
            List<RecommendedItem> list = r.recommend(uid, RECOMMENDER_NUM);
            System.out.printf("uid:%s", uid);
            for (RecommendedItem ritem : list) {
                System.out.printf("(%s,%f)", ritem.getItemID(), ritem.getValue());
            }
            System.out.println();
        }
    }
    
}

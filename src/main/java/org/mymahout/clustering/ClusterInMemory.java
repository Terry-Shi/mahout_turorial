package org.mymahout.clustering;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.clustering.Cluster;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.math.Vector;

public class ClusterInMemory {
    // 基于内存的 K 均值聚类算法实现
    public static void kMeansClusterInMemoryKMeans(){ 
        // 指定需要聚类的个数，这里选择 2 类
        int k = 2; 
        // 指定 K 均值聚类算法的最大迭代次数
        int maxIter = 3; 
        // 指定 K 均值聚类算法的最大距离阈值
        double distanceThreshold = 0.01; 
        // 声明一个计算距离的方法，这里选择了欧几里德距离
        DistanceMeasure measure = new EuclideanDistanceMeasure(); 
        // 这里构建向量集，使用的是清单 1 里的二维点集
        List<Vector> pointVectors = SimpleDataSet.getPointVectors(SimpleDataSet.points); 
        // 从点集向量中随机的选择 k 个作为簇的中心
        List<Vector> randomPoints = RandomSeedGenerator.chooseRandomPoints(pointVectors, k); 
        // 基于前面选中的中心构建簇
        List<Cluster> clusters = new ArrayList<Cluster>(); 
        int clusterId = 0; 
        for(Vector v : randomPoints){ 
            clusters.add(new Cluster(v, clusterId ++, measure)); 
        } 
        // 调用 KMeansClusterer.clusterPoints 方法执行 K 均值聚类
        List<List<Cluster>> finalClusters = KMeansClusterer.clusterPoints(pointVectors, 
        clusters, measure, maxIter, distanceThreshold); 
    
        // 打印最终的聚类结果
        for(Cluster cluster : finalClusters.get(finalClusters.size() -1)){ 
            System.out.println("Cluster id: " + cluster.getId() + " center: " + cluster.getCenter().asFormatString()); 
            System.out.println("       Points: " + cluster.getNumPoints());    
        }
    }
}

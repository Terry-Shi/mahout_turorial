package org.mymahout.recommendation.hadoop;

import org.apache.hadoop.util.ToolRunner;
import org.apache.mahout.cf.taste.hadoop.item.RecommenderJob;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.conf.YarnConfiguration;

public class RecommenderJobTest {
    
    private static final String HDFS = "hdfs://c0004649.itcs.hp.com:9000";
    
    public static void main(String[] args) {
        Configuration conf = getConf();
        String localFile = "datafile/item.csv";
        String inPath = HDFS + "/user/hdfs/userCF";
        String inFile = inPath + "/item.csv";
        String outPath = HDFS + "/user/hdfs/userCF/result/";
        String outFile = outPath + "/part-r-00000";
        String tmpPath = HDFS + "/tmp/" + System.currentTimeMillis();
        try {
            HdfsUtils hdfs = new HdfsUtils(HDFS, conf);
            hdfs.rmr(inPath);
            hdfs.mkdirs(inPath);
            hdfs.copyFile(localFile, inPath);
            hdfs.ls(inPath);
            hdfs.cat(inFile);

//        StringBuilder sb = new StringBuilder();
//        sb.append("--input ").append(inPath);
//        sb.append(" --output ").append(outPath);
//        sb.append(" --booleanData false");
//        sb.append(" --similarityClassname org.apache.mahout.math.hadoop.similarity.cooccurrence.measures.EuclideanDistanceSimilarity");
//        sb.append(" --tempDir ").append(tmpPath);
//        args = sb.toString().split(" ");

        String[] jobArgs=new String[]{  
                "-i",inPath,  
                "-o",outPath,  
                "-n","3","-b","false","-s","SIMILARITY_EUCLIDEAN_DISTANCE",  
                "--maxPrefsPerUser","7","--minPrefsPerUser","2",  
                "--maxPrefsInItemSimilarity","7",  
                "--outputPathForSimilarityMatrix", "hdfs://c0004649.itcs.hp.com:9000/output/matrix/rec001",
                "--tempDir",tmpPath};  
        
//        String[] jobArgs = { "-i", "hdfs://c0004649.itcs.hp.com:9000/input/user.csv", "-o",
//                "hdfs://c0004649.itcs.hp.com:9000/output/rec001", "-n", "3", "-b", "false", "-s",
//                "SIMILARITY_EUCLIDEAN_DISTANCE", "--maxPrefsPerUser", "7", "--minPrefsPerUser",
//                "2", "--maxPrefsInItemSimilarity", "7", "--outputPathForSimilarityMatrix",
//                "hdfs://c0004649.itcs.hp.com:9000/output/matrix/rec001", "--tempDir",
//                "hdfs://c0004649.itcs.hp.com:9000/output/temp/rec001" };
        
            ToolRunner.run(conf , new RecommenderJob(), jobArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final String YARN_RESOURCE = "c0004650.itcs.hp.com:8032";
    private static final String DEFAULT_FS = "hdfs://c0004649.itcs.hp.com:9000";

    public static Configuration getConf() {
        Configuration conf = new YarnConfiguration();
        conf.set("fs.defaultFS", DEFAULT_FS);
        conf.set("mapreduce.framework.name", "yarn");
        conf.set("yarn.resourcemanager.address", YARN_RESOURCE);
        
        conf.addResource("classpath:/hadoop/yarn-site.xml");
        conf.addResource("classpath:/hadoop/mapred-site.xml");
        return conf;
    }
}

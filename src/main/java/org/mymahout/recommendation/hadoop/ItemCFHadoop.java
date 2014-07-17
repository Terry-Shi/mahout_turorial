package org.mymahout.recommendation.hadoop;

import org.apache.hadoop.mapred.JobConf;
import org.apache.mahout.cf.taste.hadoop.item.RecommenderJob;
import org.apache.mahout.cf.taste.impl.model.jdbc.AbstractJDBCDataModel;
import org.apache.mahout.cf.taste.impl.model.jdbc.MySQLJDBCDataModel;
import org.apache.hadoop.conf.Configuration;

public class ItemCFHadoop {

    private static final String HDFS = "hdfs://c0004649.itcs.hp.com:9000";

    public static void main(String[] args) throws Exception {
        AbstractJDBCDataModel m = null;
        
        String localFile = "datafile/item.csv";
        String inPath = HDFS + "/user/hdfs/userCF";
        String inFile = inPath + "/item.csv";
        String outPath = HDFS + "/user/hdfs/userCF/result/";
        String outFile = outPath + "/part-r-00000";
        String tmpPath = HDFS + "/tmp/" + System.currentTimeMillis();

        Configuration conf = config();
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

        String[] arg=new String[]{  
                "-i",inPath,  
                "-o",outPath,  
                "-n","3","-b","false","-s","SIMILARITY_EUCLIDEAN_DISTANCE",  
                "--maxPrefsPerUser","7","--minPrefsPerUser","2",  
                "--maxPrefsInItemSimilarity","7",  
                "--tempDir",tmpPath};  
        
        RecommenderJob job = new RecommenderJob();
        job.setConf(conf);
        job.run(arg);
        hdfs.cat(outFile);
    }

    public static Configuration config() {
        JobConf conf = new JobConf(ItemCFHadoop.class);
        conf.setJobName("ItemCFHadoop");
        conf.addResource("classpath:/hadoop/core-site.xml");
        conf.addResource("classpath:/hadoop/yarn-site.xml");
        conf.addResource("classpath:/hadoop/mapred-site.xml");
        return conf;
    }
}
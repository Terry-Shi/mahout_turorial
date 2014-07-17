package org.mymahout.clustering;

import java.io.File;

import org.apache.mahout.text.SequenceFilesFromDirectory;
import org.apache.mahout.vectorizer.SparseVectorsFromSequenceFiles;
import org.apache.lucene.benchmark.utils.ExtractReuters;  


public class DocumentClustering {

    public static void documentVectorize(String[] args) throws Exception {
        // 1. 将路透的数据解压缩 , Mahout 提供了专门的方法
        DocumentClustering.extractReuters();
        // 2. 将数据存储成 SequenceFile，因为这些工具类就是在 Hadoop 的基础上做的，所以首先我们需要将数据写
        // 成 SequenceFile，以便读取和计算
        DocumentClustering.transformToSequenceFile();
        // 3. 将 SequenceFile 文件中的数据，基于 Lucene 的工具进行向量化
        DocumentClustering.transformToVector();
    }

    public static void extractReuters() {
        // ExtractReuters 是基于 Hadoop 的实现，所以需要将输入输出的文件目录传给它，这里我们可以直接把它映
        // 射到我们本地的一个文件夹，解压后的数据将写入输出目录下
        File inputFolder = new File("clustering/reuters");
        File outputFolder = new File("clustering/reuters-extracted");
        ExtractReuters extractor = new ExtractReuters(inputFolder, outputFolder);
        extractor.extract();
    }

    public static void transformToSequenceFile() {
        // SequenceFilesFromDirectory 实现将某个文件目录下的所有文件写入一个 SequenceFiles 的功能
        // 它其实本身是一个工具类，可以直接用命令行调用，这里直接调用了它的 main 方法
        String[] args = { "-c", "UTF-8", "-i", "clustering/reuters-extracted/", "-o",
                "clustering/reuters-seqfiles" };
        // 解释一下参数的意义：
        // -c: 指定文件的编码形式，这里用的是"UTF-8"
        // -i: 指定输入的文件目录，这里指到我们刚刚导出文件的目录
        // -o: 指定输出的文件目录

        try {
            SequenceFilesFromDirectory.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void transformToVector() {
        // SparseVectorsFromSequenceFiles 实现将 SequenceFiles 中的数据进行向量化。
        // 它其实本身是一个工具类，可以直接用命令行调用，这里直接调用了它的 main 方法
        String[] args = { "-i", "clustering/reuters-seqfiles/", "-o",
                "clustering/reuters-vectors-bigram", "-a",
                "org.apache.lucene.analysis.WhitespaceAnalyzer", "-chunk", "200", "-wt", "tfidf",
                "-s", "5", "-md", "3", "-x", "90", "-ng", "2", "-ml", "50", "-seq" };
        // 解释一下参数的意义：
        // -i: 指定输入的文件目录，这里指到我们刚刚生成 SequenceFiles 的目录
        // -o: 指定输出的文件目录
        // -a: 指定使用的 Analyzer，这里用的是 lucene 的空格分词的 Analyzer
        // -chunk: 指定 Chunk 的大小，单位是 M。对于大的文件集合，我们不能一次 load 所有文件，所以需要
        // 对数据进行切块
        // -wt: 指定分析时采用的计算权重的模式，这里选了 tfidf
        // -s: 指定词语在整个文本集合出现的最低频度，低于这个频度的词汇将被丢掉
        // -md: 指定词语在多少不同的文本中出现的最低值，低于这个值的词汇将被丢掉
        // -x: 指定高频词汇和无意义词汇（例如 is，a，the 等）的出现频率上限，高于上限的将被丢掉
        // -ng: 指定分词后考虑词汇的最大长度，例如 1-gram 就是，coca，cola，这是两个词，
        // 2-gram 时，coca cola 是一个词汇，2-gram 比 1-gram 在一定情况下分析的更准确。
        // -ml: 指定判断相邻词语是不是属于一个词汇的相似度阈值，当选择 >1-gram 时才有用，其实计算的是
        // Minimum Log Likelihood Ratio 的阈值
        // -seq: 指定生成的向量是 SequentialAccessSparseVectors，没设置时默认生成还是
        // RandomAccessSparseVectors

        try {
            SparseVectorsFromSequenceFiles.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

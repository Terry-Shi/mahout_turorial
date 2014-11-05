package org.mymahout.classification;

import java.io.IOException;

import org.apache.mahout.classifier.df.data.DescriptorException;
import org.apache.mahout.classifier.df.tools.Describe;
import org.apache.mahout.classifier.df.BreimanExample;


/**
 * Ref link:
 * http://www.cnblogs.com/kemaswill/archive/2012/09/21/2697188.html
 * @author shijie
 *
 */
public class PlayTennis {
    public static void main(String[] args) {
        // bin/mahout org.apache.mahout.classifier.df.tools.Describe -p /path/to/glass.data -f /path/to/glass.info -d I 9 N L
        String[] describeArgs = new String[]{"-p","datafile/classification/glass.data", "-f", "datafile/classification/glass.info", "-d", "I", "9", "N", "L"};
        try {
            Describe.main(describeArgs);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DescriptorException e) {
            e.printStackTrace();
        }
    }
}

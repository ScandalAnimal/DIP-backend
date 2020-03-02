package cz.vutbr.fit.maros.dip.services;

import java.util.Random;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

@Service
public class EvaluationService {

    final FileFactory fileFactory;

    public EvaluationService(FileFactory fileFactory) {
        this.fileFactory = fileFactory;
    }

    public String evaluateData(Instances data, Classifier classifier) throws Exception {
        return evaluateData(data, classifier, 10);
    }

    public String evaluateData(Instances data, Classifier classifier, int numberOfFolds) throws Exception {
        return evaluateData(data, classifier, numberOfFolds, null);
    }

    public String evaluateData(Instances data, Classifier classifier, Instances testData) throws Exception {
        return evaluateData(data, classifier, 10, testData);
    }

    public String evaluateData(Instances data, Classifier classifier, int numberOfFolds, Instances testData) throws Exception {
        Evaluation evaluation = new Evaluation(data);
        if (testData == null) {
            evaluation.crossValidateModel(classifier, data, numberOfFolds, new Random(1));
        } else {
            evaluation.evaluateModel(classifier, testData);
        }
        String retString = "";
        retString += evaluation.toSummaryString() + " \n";
        retString += evaluation.toClassDetailsString() + " \n";
        retString += evaluation.toMatrixString() + " \n";
        return retString;
    }
}

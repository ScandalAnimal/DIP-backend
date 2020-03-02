package cz.vutbr.fit.maros.dip.services;

import cz.vutbr.fit.maros.dip.models.ML;
import cz.vutbr.fit.maros.dip.models.NearestNeighbor;
import cz.vutbr.fit.maros.dip.models.Options;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instances;
import weka.core.neighboursearch.BallTree;
import weka.core.neighboursearch.CoverTree;
import weka.core.neighboursearch.LinearNNSearch;
import weka.core.neighboursearch.NearestNeighbourSearch;

@Service
public class KNNService {

    private final FileFactory fileFactory;

    private final EvaluationService evaluationService;

    final LoadData loadData;

    public KNNService(FileFactory fileFactory, EvaluationService evaluationService, LoadData loadData) {
        this.fileFactory = fileFactory;
        this.evaluationService = evaluationService;
        this.loadData = loadData;
    }

    public String handleKNNService(NearestNeighbor nearestNeighbor) throws Exception {
        FileFactory.TrainTest instances = fileFactory.getInstancesFromFile(nearestNeighbor.getFileName(), new Options(nearestNeighbor.isFeatureSelection()));
        Classifier ixBk = handleIBK(nearestNeighbor, instances.train);
        return evaluateKNN(nearestNeighbor, instances, ixBk);
    }

    public Classifier handleIBK(NearestNeighbor nearestNeighbor, Instances instances) throws Exception {
        IBk ixBk = new IBk();
        ixBk.setKNN(nearestNeighbor.getK());
        ixBk.setNearestNeighbourSearchAlgorithm(getNearestNeighborAlgorithm(nearestNeighbor));
        ixBk.setCrossValidate(nearestNeighbor.isHoldOneOut());
        ixBk.setMeanSquared(nearestNeighbor.isUseMeanError());
        if (nearestNeighbor.isBoost()) {
            AdaBoostM1 adaBoostM1 = new AdaBoostM1();
            adaBoostM1.setClassifier(ixBk);
            adaBoostM1.buildClassifier(instances);
            return adaBoostM1;
        }
        ixBk.buildClassifier(instances);
        return ixBk;
    }

    public String evaluateKNN(NearestNeighbor nearestNeighbor, FileFactory.TrainTest instances, Classifier ixBk) throws Exception {
        if (nearestNeighbor.getTestType() == ML.TestType.TestData) {
            return evaluationService.evaluateData(instances.train, ixBk, instances.test) + "\n \n " + ixBk.toString();
        } else if (nearestNeighbor.getTestType() == ML.TestType.Train) {
            return evaluationService.evaluateData(instances.train, ixBk, instances.train) + "\n \n " + ixBk.toString();
        }
        return evaluationService.evaluateData(instances.train, ixBk) + "\n \n " + ixBk.toString();
    }

    public void createModel(NearestNeighbor nearestNeighbor) throws Exception {
        FileFactory.TrainTest data = fileFactory.getInstancesFromFile(nearestNeighbor.getFileName(), new Options(nearestNeighbor.isFeatureSelection()));
        Classifier cls = handleIBK(nearestNeighbor, data.train);
        loadData.saveModel(cls, getString(nearestNeighbor));
    }

    public String getModel(NearestNeighbor nearestNeighbor) throws Exception {
        FileFactory.TrainTest data = fileFactory.getInstancesFromFile(nearestNeighbor.getFileName(), new Options(nearestNeighbor.isFeatureSelection()));
        Classifier cls = loadData.getModel(getString(nearestNeighbor));
        nearestNeighbor.setTestType(ML.TestType.TestData);
        return evaluateKNN(nearestNeighbor, data, cls);
    }

    private String getString(NearestNeighbor nearestNeighbor) {
        String nn = "KNearestNeighbor-k=" + nearestNeighbor.getK() + "-fileName=" + nearestNeighbor.getFileName();
        if (nearestNeighbor.isFeatureSelection()) {
            nn += "-feature=true";
        }
        return nn + ".model";
    }

    public String handleSplitData(NearestNeighbor nearestNeighbor, int num, String retString) throws Exception {
        if (num <= 100) {
            retString += "Amount " + Integer.toString(num) + "\n";
            FileFactory.TrainTest data;
            if (nearestNeighbor.getFileName() == ML.Files.Census) {
                data = fileFactory.handlePublicCensus(num, new Options(nearestNeighbor.isFeatureSelection()));
            } else {
                data = fileFactory.handlePublicCar(num);
            }
            Classifier cls = handleIBK(nearestNeighbor, data.train);
            Instances d;
            if (nearestNeighbor.getTestType() == ML.TestType.Train) {
                if (nearestNeighbor.getFileName() == ML.Files.Car) {
                    d = fileFactory.handlePublicCar(0).train;
                } else {
                    d = fileFactory.handlePublicCensus(0, new Options(nearestNeighbor.isFeatureSelection())).train;
                }
            } else {
                d = data.test;
            }
            return handleSplitData(nearestNeighbor, num == 1 ? num + 9 : num + 10, retString + "\n \n" + evaluationService.evaluateData(data.train, cls, d));
        }
        return retString;
    }

    private NearestNeighbourSearch getNearestNeighborAlgorithm(NearestNeighbor nearestNeighbor) {
        if (nearestNeighbor.getTreeTypes() == NearestNeighbor.TreeTypes.Linear) {
            return new LinearNNSearch();
        } else if (nearestNeighbor.getTreeTypes() == NearestNeighbor.TreeTypes.BallTree) {
            return new BallTree();
        } else if (nearestNeighbor.getTreeTypes() == NearestNeighbor.TreeTypes.CoverTree) {
            return new CoverTree();
        }
        return null;
    }

}

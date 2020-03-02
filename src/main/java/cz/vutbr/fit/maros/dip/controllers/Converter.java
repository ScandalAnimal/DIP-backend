package cz.vutbr.fit.maros.dip.controllers;

import cz.vutbr.fit.maros.dip.models.Options;
import cz.vutbr.fit.maros.dip.services.FileFactory;
import cz.vutbr.fit.maros.dip.services.LoadData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.NominalToBinary;

@Controller
public class Converter {

    private final FileFactory fileFactory;

    private final LoadData loadData;

    public Converter(FileFactory fileFactory, LoadData loadData) {
        this.fileFactory = fileFactory;
        this.loadData = loadData;
    }

    @ResponseBody
    @RequestMapping(value = "/convert", method = { RequestMethod.GET })
    public void doConvert() throws Exception {
        NominalToBinary nominalToBinary = new NominalToBinary();
        FileFactory.TrainTest data = fileFactory.handlePublicCensus(new Options(false, false));
        nominalToBinary.setInputFormat(data.test);
        Instances instances = Filter.useFilter(data.test, nominalToBinary);
        loadData.saveToArff(instances, "justATest2.arff");
    }
}

package cz.vutbr.fit.maros.dip.controllers;

import cz.vutbr.fit.maros.dip.models.NeuralNetworkModel;
import cz.vutbr.fit.maros.dip.services.NeuralNetworkService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class NeuralNetworkController {

    private final NeuralNetworkService neuralNetworkService;

    public NeuralNetworkController(NeuralNetworkService neuralNetworkService) {
        this.neuralNetworkService = neuralNetworkService;
    }

    @ResponseBody
    @RequestMapping(value = "/neuralnetwork", method = { RequestMethod.GET })
    public String neuralNetwork(NeuralNetworkModel neuralNetworkModel) throws Exception {
        return neuralNetworkService.handleNeuralNetwork(neuralNetworkModel);
    }

    @ResponseBody
    @RequestMapping(value = "/neuralnetwork/test", method = { RequestMethod.GET })
    public String neuralnetworkTest(NeuralNetworkModel neuralNetworkModel) throws Exception {
        return neuralNetworkService.handleSplitData(neuralNetworkModel, 1, "");
    }

    @ResponseBody
    @RequestMapping(value = "/neuralnetwork/model", method = { RequestMethod.POST })
    public void createModel(NeuralNetworkModel nn) throws Exception {
        neuralNetworkService.createModel(nn);
    }

    @ResponseBody
    @RequestMapping(value = "/neuralnetwork/model", method = { RequestMethod.GET })
    public String getModel(NeuralNetworkModel nn) throws Exception {
        return neuralNetworkService.getModel(nn);
    }

    @ResponseBody
    @RequestMapping(value = "/neuralnetwork/reduction", method = { RequestMethod.GET })
    public String reduction() throws Exception {
        return neuralNetworkService.neuralNetworkWithReduction();
    }
}

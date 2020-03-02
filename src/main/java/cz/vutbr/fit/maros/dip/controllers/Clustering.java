package cz.vutbr.fit.maros.dip.controllers;

import cz.vutbr.fit.maros.dip.models.Cluster;
import cz.vutbr.fit.maros.dip.services.ClusterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class Clustering {

    private final ClusterService clusterService;

    public Clustering(ClusterService clusterService) {
        this.clusterService = clusterService;
    }

    @ResponseBody
    @RequestMapping(value = "/kMeans", method = { RequestMethod.GET })
    public String handleKmeans(Cluster cluster) throws Exception {
        return clusterService.handleKmeans(cluster);
    }

    @ResponseBody
    @RequestMapping(value = "/em", method = { RequestMethod.GET })
    public String handleEM(Cluster emModel) throws Exception {
        return clusterService.handleEM(emModel);
    }

    @ResponseBody
    @RequestMapping(value = "/em/plot", method = { RequestMethod.GET })
    public void handleEMPlot() throws Exception {
        clusterService.plotEMWithFeature();
    }

    @ResponseBody
    @RequestMapping(value = "/kMeans/plot", method = { RequestMethod.GET })
    public void handleKMPlot() throws Exception {
        clusterService.plotKMWithFeature();
    }
}

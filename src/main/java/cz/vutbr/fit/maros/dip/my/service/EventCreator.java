package cz.vutbr.fit.maros.dip.my.service;

import java.io.IOException;
import java.time.LocalDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component

public class EventCreator {

    private static final Logger LOG = LoggerFactory.getLogger(EventCreator.class);

    public EventCreator() {
    }

    //    @Scheduled(cron = "0 0 12 * * ?")
    @Scheduled(fixedRate = 1000)
    public void create() {

        Document doc = null;
        try {
            doc = Jsoup.connect("https://en.wikipedia.org/").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(doc.title());
        Elements newsHeadlines = doc.select("#mp-itn b a");
        for (Element headline : newsHeadlines) {
            System.out.format(headline.attr("title") + "\n\t" + headline.absUrl("href"));
        }

    }

}

package com.chowdury.demo.services;

import com.chowdury.demo.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoronaVirusDataServices {
    public static String  VIRUS_DATA_URL= "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

     private List<LocationStats> allStats = new ArrayList<>();
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public  void  fetchVirusData() throws IOException, InterruptedException {
         List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request,HttpResponse.BodyHandlers.ofString());
//        System.out.println(httpResponse.body());
        StringReader csvBodyReader = new StringReader(httpResponse.body());
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            LocationStats locationStat = new LocationStats();
             locationStat.setState(record.get("Province/State"));
             locationStat.setCountry(record.get("Country/Region"));
            int lastDayCases = Integer.parseInt(record.get(record.size() - 1));
            int previousDayCases = Integer.parseInt(record.get(record.size() - 2));
//            System.out.println(locationStat);
            locationStat.setLatestTotalCase(lastDayCases);
            locationStat.setDiffFromPreviousDay(lastDayCases - previousDayCases);
            newStats.add(locationStat);
        }
        this.allStats = newStats;
    }

    public List<LocationStats> getAllStats() {
        return allStats.stream()
                .sorted(Comparator.comparing(LocationStats::getDiffFromPreviousDay).reversed())
                .collect(Collectors.toList());
    }
}
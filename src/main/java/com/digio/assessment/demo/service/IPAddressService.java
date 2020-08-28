package com.digio.assessment.demo.service;

import com.digio.assessment.demo.model.IPDetail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class IPAddressService {

    public List<IPDetail> loadFile(String fileName) throws Exception {
        Path path = Paths.get(getClass().getClassLoader().getResource(fileName).toURI());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss");

        List<IPDetail> ipDetails = Collections.EMPTY_LIST;
        try (Stream<String> stream = Files.lines(path)) {
            ipDetails = stream
                    .filter(s -> s.split("\"")[0].split("-").length == 3) //Filter the Admin records, as it doesn't look like legitimate. For example, its returning resources like .js, .css
                    .map(s -> {
                        String dateComponent = s.split("\"")[0].split("-")[2].trim();
                        String ipAddress = s.split("\"")[0].split("-")[0];
                        String endPoint = s.split("\"")[1].split(" ")[1];

                        return IPDetail.builder()
                                .ipAddress(ipAddress.trim())
                                .visitedDate(LocalDateTime.parse(dateComponent.substring(1, dateComponent.length() - 7), formatter))
                                .endPoint(endPoint.trim())
                                .build();
                    })
                    .collect(toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ipDetails;
    }

    public List<String> getUniqueIPAddresses(List<IPDetail> ipDetails) {
        return ipDetails.stream()
                .filter(distinctByKey(ipDetail -> ipDetail.getIpAddress()))
                .map(ipDetail -> ipDetail.getIpAddress())
                .collect(toList());
    }

    public List<String> getMostVisitedURLs(List<IPDetail> ipDetails) {
        List<String> listOfEndpoints = ipDetails.stream()
                .collect(groupingBy(IPDetail::getEndPoint, counting()))
                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(toList());

        return listOfEndpoints;
    }

    public List<String> getMostActiveIPAddresses(List<IPDetail> ipDetails) {
        List<String> listOfEndpoints = ipDetails.stream()
                .sorted(Comparator.comparing(IPDetail::getVisitedDate).reversed())
                .limit(3)
                .map(IPDetail::getIpAddress)
                .collect(toList());

        return listOfEndpoints;
    }

    public List<String> getMostOriginatedIPAddresses(List<IPDetail> ipDetails) {
        List<String> listOfEndpoints = ipDetails.stream()
                .collect(groupingBy(IPDetail::getIpAddress, counting()))
                .entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(toList());

        return listOfEndpoints;
    }


    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}

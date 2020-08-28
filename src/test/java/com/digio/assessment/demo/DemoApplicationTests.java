package com.digio.assessment.demo;

import com.digio.assessment.demo.model.IPDetail;
import com.digio.assessment.demo.service.IPAddressService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@RunWith(MockitoJUnitRunner.class)
public class DemoApplicationTests {

    private List<IPDetail> ipDetails = Collections.EMPTY_LIST;

    @InjectMocks
    IPAddressService ipAddressService;

    @Before
    public void setUp() throws Exception{
        Path path = Paths.get(getClass().getClassLoader().getResource("ipaddress.txt").toURI());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss");

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
    }

    @Test
    public void testTotalIPAddresses() {
        Assert.assertEquals(ipDetails.size(), 20);
    }

    @Test
    public void testUniqueIPAddresses() {
        Assert.assertEquals(ipAddressService.getUniqueIPAddresses(ipDetails).size(), 10);
    }

    @Test
    public void testMostVisitedURLs() {
        Assert.assertEquals(ipAddressService.getMostVisitedURLs(ipDetails).size(), 3);
        Assert.assertEquals(ipAddressService.getMostVisitedURLs(ipDetails).get(0), "/docs/manage-websites/");
    }

    @Test
    public void testMostActiveIPAddresses() {
        Assert.assertEquals(ipAddressService.getMostActiveIPAddresses(ipDetails).size(), 3);
        Assert.assertEquals(ipAddressService.getMostActiveIPAddresses(ipDetails).get(0), "168.41.191.43");
    }

    @Test
    public void testOriginIPAddresses() {
        Assert.assertEquals(ipAddressService.getMostOriginatedIPAddresses(ipDetails).size(), 3);
        Assert.assertEquals(ipAddressService.getMostOriginatedIPAddresses(ipDetails).get(0), "168.41.191.40");
    }
}

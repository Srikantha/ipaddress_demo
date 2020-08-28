package com.digio.assessment.demo;

import com.digio.assessment.demo.model.IPDetail;
import com.digio.assessment.demo.service.IPAddressService;

import java.util.List;

public class DemoApplication {

    public static void main(String[] args) throws Exception {

		IPAddressService ipAddressService = new IPAddressService();
        List<IPDetail> ipDetails = ipAddressService.loadFile("ipaddress.txt");

        System.out.println("Unique IP Address Size:" + ipAddressService.getUniqueIPAddresses(ipDetails).size());
        System.out.println("Top 3 Visited URLs:" + ipAddressService.getMostVisitedURLs(ipDetails));
        System.out.println("Top 3 Originated IP Address:" + ipAddressService.getMostOriginatedIPAddresses(ipDetails));
        System.out.println("Top 3 Active IP Address:" + ipAddressService.getMostActiveIPAddresses(ipDetails));

    }
}

package com.whk.springboothello1;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
public class HelloController {

	private final Logger logger = Logger.getLogger(getClass());

	@Autowired
	private DiscoveryClient client;

	@RequestMapping(value = "/hello", method = RequestMethod.GET)
	public String index() {
		List<ServiceInstance> instance = client.getInstances("hello-service");
		if (instance.size()>0){
			System.out.println("instance:"+instance.get(0));
		}
		return "Hello World";
	}


	public static void main(String[] args) {
		LocalDate localDate = LocalDate.now();
		LocalDate yesterday = localDate.plusDays(-1);
		System.out.println(yesterday.getDayOfMonth());
		System.out.println(yesterday.getMonthValue());
		System.out.println(yesterday.getYear());
		System.out.println(yesterday.compareTo(localDate));
		System.out.println(localDate.compareTo(yesterday));
	}
}

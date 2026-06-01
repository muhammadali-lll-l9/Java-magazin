package com.example.internet_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.net.InetAddress;

@SpringBootApplication
public class InternetShopApplication {

	public static void main(String[] args) throws Exception {
		var app = new SpringApplication(InternetShopApplication.class);
		var context = app.run(args);

		var env = context.getEnvironment();
		var port = env.getProperty("server.port", "8080");
		var address = InetAddress.getLocalHost().getHostAddress();

		System.out.println("\n----------------------------------------------------------");
		System.out.println("\tПриложение запущено! Доступно по адресу:");
		System.out.println("\tLocal: \thttp://localhost:" + port);
		System.out.println("\tExternal: \thttp://" + address + ":" + port);
		System.out.println("----------------------------------------------------------\n");
	}
}




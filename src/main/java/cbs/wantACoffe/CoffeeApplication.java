package cbs.wantACoffe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoffeeApplication {

	public static void main(String[] args) {
		System.out.println("Starting Server App\nA COFFEE 4 everyone!!!");
		try{
			SpringApplication.run(CoffeeApplication.class, args);
		} catch (Exception e) {
			System.out.println("pepo");
		}
		
	}

}

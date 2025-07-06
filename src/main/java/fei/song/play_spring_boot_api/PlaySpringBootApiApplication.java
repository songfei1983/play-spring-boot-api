package fei.song.play_spring_boot_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class PlaySpringBootApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlaySpringBootApiApplication.class, args);
	}

}

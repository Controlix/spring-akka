package be.ict.mb.spring.akka.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import akka.NotUsed;
import akka.stream.javadsl.Source;

@RestController
public class HelloController {
	
	@GetMapping("/hello")
	public Source<String, NotUsed> sayHello(@RequestParam(defaultValue = "World") String name) {
		return Source.single(String.format("Hello, %s!", name));
	}

}

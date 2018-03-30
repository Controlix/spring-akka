package be.ict.mb.spring.akka.demo.library;

import lombok.Data;

@Data
public class CreateBookCommand {

	String title;
	String aithor;
}

package be.ict.mb.spring.akka.demo.library;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Book {

	int id;
	String title;
	String author;
}

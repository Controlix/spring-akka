package be.ict.mb.spring.akka.demo.library;

import java.util.UUID;

import lombok.Value;

@Value
public class BookDetails {
	UUID id;
	String title;
	String author;
}

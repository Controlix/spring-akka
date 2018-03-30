package be.ict.mb.spring.akka.demo.library;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

@Service
public class AkkaBookService implements BookService {

	@Override
	public Book create(String title, String author) {
		return Book.builder().id(RandomUtils.nextInt()).title(title).author(author).build();
	}

}

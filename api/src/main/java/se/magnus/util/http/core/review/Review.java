package se.magnus.util.http.core.review;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class Review {
	int productId;
	int reviewId;
	String author;
	String subject;
	String content;
	String serviceAddress;

	public Review() {
		productId = 0;
		reviewId = 0;
		author = null;
		subject = null;
		content = null;
		serviceAddress = null;
	}
}

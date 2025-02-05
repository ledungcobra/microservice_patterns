package se.magnus.util.http.core.recommendation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
@Getter
@AllArgsConstructor
public class Recommendation {
	int productId;
	int recommendationId;
	String author;
	int rate;
	String content;
	String serviceAddress;

	public Recommendation() {
		productId = 0;
		recommendationId = 0;
		author = null;
		rate = 0;
		content = null;
		serviceAddress = null;
	}
}

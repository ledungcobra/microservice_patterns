package se.magnus.util.http.core.product;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@AllArgsConstructor
public class Product {
	int productId;
	String name;
	int weight;
	String serviceAddress;

	public Product() {
		productId = 0;
		name = null;
		weight = 0;
		serviceAddress = null;
	}
}

package se.magnus.microservices.core.product.services;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.http.core.product.Product;
import se.magnus.util.http.core.product.ProductService;
import se.magnus.util.http.exceptions.InvalidInputException;
import se.magnus.util.http.exceptions.NotFoundException;

@RestController
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

	ServiceUtil serviceUtil;

	@Override
	public Product getProduct(int productId) {
		log.debug( "/product return the found product for productId={}", productId );

		if ( productId < 1 ) {
			throw new InvalidInputException( "Invalid productId: " + productId );
		}

		if ( productId == 13 ) {
			throw new NotFoundException( "No product found for productId: " + productId );
		}
		return new Product(productId, "name" + productId, 123, serviceUtil.getServiceAddress());
	}
}

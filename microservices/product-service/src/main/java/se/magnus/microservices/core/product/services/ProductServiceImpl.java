package se.magnus.microservices.core.product.services;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.http.core.product.Product;
import se.magnus.util.http.core.product.ProductService;

@RestController
@FieldDefaults(makeFinal = true)
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

	ServiceUtil serviceUtil;

	@Override
	public Product getProduct(int productId) {
		return new Product(productId, "name" + productId, 123, serviceUtil.getServiceAddress());
	}
}

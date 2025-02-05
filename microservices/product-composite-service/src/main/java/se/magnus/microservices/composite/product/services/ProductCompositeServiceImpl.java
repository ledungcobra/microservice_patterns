package se.magnus.microservices.composite.product.services;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RestController;
import se.magnus.util.http.ServiceUtil;
import se.magnus.util.http.core.product.Product;
import se.magnus.util.http.core.recommendation.Recommendation;
import se.magnus.util.http.core.review.Review;
import se.magnus.util.http.exceptions.NotFoundException;
import se.magnus.util.http.product.ProductAggregate;
import se.magnus.util.http.product.ProductCompositeService;
import se.magnus.util.http.product.RecommendationSummary;
import se.magnus.util.http.product.ReviewSummary;
import se.magnus.util.http.product.ServiceAddresses;

@RestController
@FieldDefaults(makeFinal = true)
@AllArgsConstructor
public class ProductCompositeServiceImpl implements ProductCompositeService {

	ServiceUtil serviceUtil;
	ProductCompositeIntegration integration;

	@Override
	public ProductAggregate getProduct(int productId) {
		Product product = integration.getProduct( productId );
		if(product == null) {
			throw new NotFoundException("No product found for productId: " +  productId);
		}

		List<Recommendation> recommendations = integration.getRecommendations( productId );
		List<Review> reviews = integration.getReviews( productId );
		return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
	}

	private ProductAggregate createProductAggregate(
			Product product,
			List<Recommendation> recommendations,
			List<Review> reviews,
			String serviceAddress) {
		int productId = product.getProductId();
		String name = product.getName();
		int weight = product.getWeight();

		List<RecommendationSummary> recommendationSummaries = recommendations.stream()
				.map( r-> new RecommendationSummary( r.getRecommendationId(), r.getAuthor(), r.getRate()  ) )
				.toList();

		List<ReviewSummary> reviewSummaries = reviews.stream()
				.map( r -> new ReviewSummary( r.getReviewId(), r.getAuthor(), r.getSubject() ) )
				.toList();

		String productAddress = product.getServiceAddress();
		String reviewAddress = !reviews.isEmpty() ? reviews.get( 0 ).getServiceAddress() : "";
		String recommendationAddress = !recommendations.isEmpty() ? recommendations.get( 0 ).getServiceAddress(): "";
		ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);
		return new ProductAggregate( productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses );
	}
}

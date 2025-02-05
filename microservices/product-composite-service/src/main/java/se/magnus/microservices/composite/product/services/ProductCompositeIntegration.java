package se.magnus.microservices.composite.product.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import se.magnus.util.http.HttpErrorInfo;
import se.magnus.util.http.core.product.Product;
import se.magnus.util.http.core.product.ProductService;
import se.magnus.util.http.core.recommendation.Recommendation;
import se.magnus.util.http.core.recommendation.RecommendationService;
import se.magnus.util.http.core.review.Review;
import se.magnus.util.http.core.review.ReviewService;
import se.magnus.util.http.exceptions.InvalidInputException;
import se.magnus.util.http.exceptions.NotFoundException;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {

	RestTemplate restTemplate;
	ObjectMapper mapper;

	String productServiceUrl;
	String recommendationServiceUrl;
	String reviewServiceUrl;

	public ProductCompositeIntegration(
			RestTemplate restTemplate,
			ObjectMapper objectMapper,
			@Value("${app.product-service.host}") String productServiceHost,
			@Value("${app.product-service.port}") int productServicePort,
			@Value("${app.recommendation-service.host}") String recommendationServiceHost,
			@Value("${app.recommendation-service.port}") int recommendationServicePort,
			@Value("${app.review-service.host}") String reviewServiceHost,
			@Value("${app.review-service.port}") int reviewServicePort
	) {
		this.restTemplate = restTemplate;
		this.mapper = objectMapper;

		productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/product/";
		recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
		reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
	}

	@Override
	public Product getProduct(int productId) {
		try {
			String url = productServiceUrl + productId;
			log.debug( "Will call getProduct API on URL: {}", url );
			Product product = restTemplate.getForObject( url, Product.class );
			log.debug( "Found product with id: {}", product.getProductId() );
			return product;
		}
		catch (HttpClientErrorException ex) {
			switch ( Objects.requireNonNull( HttpStatus.resolve( ex.getStatusCode().value() ) ) ) {
				case NOT_FOUND -> throw new NotFoundException( getErrorMessage( ex ) );
				case UNPROCESSABLE_ENTITY -> throw new InvalidInputException( getErrorMessage( ex ) );
				default -> {
					log.warn( "Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode() );
					log.warn( "Error body: {}", ex.getResponseBodyAsString() );
					throw ex;
				}
			}
		}
	}


	private String getErrorMessage(HttpClientErrorException ex) {
		try {
			return mapper.readValue( ex.getResponseBodyAsString(), HttpErrorInfo.class ).getMessage();
		}
		catch (IOException ioex) {
			return ex.getMessage();
		}
	}

	@Override
	public List<Recommendation> getRecommendations(int productId) {
		try {
			String url = recommendationServiceUrl + productId;
			log.debug( "Will call getRecommendations API on URL: {}", url );

			List<Recommendation> recommendations = Optional.ofNullable( restTemplate.exchange(
					url,
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<List<Recommendation>>() {
					}
			).getBody() ).orElseGet( Collections::emptyList );
			log.debug( "Found {} recommendations for product id: {}", recommendations.size(), productId );
			return recommendations;
		}
		catch (HttpClientErrorException ex) {
			log.warn(
					"Got an exception while requesting recommendations, return zero recommendations: {}",
					ex.getMessage()
			);
			return Collections.emptyList();
		}
	}

	@Override
	public List<Review> getReviews(int productId) {

		try {
			String url = reviewServiceUrl + productId;

			log.debug( "Will call getReviews API on URL: {}", url );
			List<Review> nullableReviews = restTemplate
					.exchange(
							url,
							HttpMethod.GET,
							null,
							new ParameterizedTypeReference<List<Review>>() {
							}
					)
					.getBody();
			List<Review> reviews = Optional.ofNullable( nullableReviews )
					.orElseGet( Collections::emptyList );
			log.debug( "Found {} reviews for a product with id: {}", reviews.size(), productId );
			return reviews;

		}
		catch (Exception ex) {
			log.warn( "Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage() );
			return new ArrayList<>();
		}
	}
}

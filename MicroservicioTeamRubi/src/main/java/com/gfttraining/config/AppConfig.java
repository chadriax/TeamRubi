package com.gfttraining.config;

import java.util.Collection;

import org.modelmapper.Condition;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import lombok.Data;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Data
@Component
public class AppConfig {

	@Value("${paths.user}")
	private String userPath;

	@Value("${paths.favorite}")
	private String favoritePath;

	@Value("${paths.user-carts}")
	private String userCartsPath;

	@Bean
	public WebClient webClient() {
		return WebClient.create();
	}

	@Bean
	public ModelMapper modelMapper() {

		ModelMapper modelMapper = new ModelMapper();

		modelMapper.getConfiguration()
		.setPropertyCondition(Conditions.isNotNull())
		.setPropertyCondition(skipEmptyLists());

		return modelMapper;

	}

	private Condition<?, ?> skipEmptyLists() {
		return ctx -> {
			Object sourceValue = ctx.getSource();
			if (sourceValue instanceof Collection) {
				return !((Collection<?>) sourceValue).isEmpty();
			} else {
				return sourceValue != null;
			}
		};
	}

}

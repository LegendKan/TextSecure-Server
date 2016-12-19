package org.whispersystems.textsecuregcm.configuration;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

public class YunpianConfiguration {
	
	@NotEmpty
	@JsonProperty
	private String apiKey;
	
	public String getApiKey() {
		return apiKey;
	}

}

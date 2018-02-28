package com.redhat.utility;

import org.apache.camel.builder.RouteBuilder;
import org.apache.http.ProtocolException;

public class FetchJWKSetRouteBuilder extends RouteBuilder {
	
	private String urlString;
	
	public FetchJWKSetRouteBuilder(String urlString) {
		this.urlString = urlString;
	}

	@Override
	public void configure() throws Exception {
				
		onException(ProtocolException.class)
			.handled(true)
			.log("This is the exception: ${exception.message}");

		onException(Exception.class)
			.handled(true)
			.log("This is the exception: ${exception.message}");
	
		from("direct:obtainKeyFromHttp")
			.routeId("ObtainPublicKeyFromHttp")
			.log("Invoke HTTP Address")
			.to(urlString)
			.to("direct:obtainKey");
		
	}

}

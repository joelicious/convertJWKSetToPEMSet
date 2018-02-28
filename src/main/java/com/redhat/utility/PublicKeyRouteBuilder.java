package com.redhat.utility;

import org.apache.camel.builder.RouteBuilder;

public class PublicKeyRouteBuilder extends RouteBuilder {
	
	@Override
	public void configure() throws Exception {
	
		from("direct:obtainKey")
			.routeId("ObtainPublicKey")
			.log("Transforming Payload to PEM")
			.process(new PublicKeyProcessor());
		
	}

}

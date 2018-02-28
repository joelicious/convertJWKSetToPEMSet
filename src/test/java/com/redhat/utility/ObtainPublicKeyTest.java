package com.redhat.utility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:jbutler@redhat.com">Joseph S. Butler</a>
 */
public class ObtainPublicKeyTest extends CamelTestSupport {

	private static final Logger LOG = LoggerFactory.getLogger(ObtainPublicKeyTest.class);

	@EndpointInject(uri = "mock:success")
	protected MockEndpoint successEndpoint;

	@EndpointInject(uri = "mock:authorizationException")
	protected MockEndpoint failureEndpoint;

	@Test
	public void testPublicKeyCreationTest() throws Exception {

		Path path = Paths.get(getClass().getClassLoader().getResource("JWKSet.json").toURI());

		String jwkJsonKeys = Files.lines(path).collect(Collectors.joining());
		
		LOG.info("stringFromFile: " + jwkJsonKeys);
		

		successEndpoint.expectedMessageCount(1);
		failureEndpoint.expectedMessageCount(0);

		String result = template.requestBody("direct:testPublicKey", jwkJsonKeys, String.class);

		LOG.info("Result: " + result);

		assertTrue(result.contains(
				"Zwccwo08daa34sQPUwblJ3"));
		
		assertTrue(result.contains("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5ShJuYfZN7eOp95QZWDqDsSksI9cx9vaLkdlPwC/2oaQCyGTGoGITD2n+1dkFMo2rwmZfEQ0hbW6BnxejYHWiC5zPUp3Lia6qOmldljJTBbeuPcG9DIjEeb1BfsBsUIUa4I1DvowokjotturmBaqKaEUZNQrX0Qv1+4tkPAbjCwb7SHtjR4SR4MM/vMQdPHd0FusaXz/r3OettC58C8McLc6b4TOGRySFw8RDlq6lL1d0E3mCtOSWe0iYS2Ow2J4bu5QQXBFioqj9L4cxctaK8EANZwUoktfjEl4rmEwdpB5GBdlFImJs1zm1O3fFR8CBPo8qcKTz8QFEsGrBh+bBwIDAQAB"));

		successEndpoint.assertIsSatisfied();
		failureEndpoint.assertIsSatisfied();
	}

	@Override
	protected RouteBuilder[] createRouteBuilders() throws Exception {

		return new RouteBuilder[] {

				new RouteBuilder() {

					public void configure() {

						onException(Exception.class).to("mock:authorizationException");

						from("direct:testPublicKey").routeId("PKTest").log("Invoking Obtain Key").to("direct:obtainKey")
								.log("This is the Body ${body}").to("mock:success");

					}

				}, new PublicKeyRouteBuilder() };
	}

}
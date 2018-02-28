/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.utility;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * @author <a href="mailto:jbutler@redhat.com">Joseph S. Butler</a>
 */
public class ObtainPublicKey {

	public static void main(final String[] args) throws Exception {

		String jwkSetURL = null;

		if (args.length != 1) {
			throw new Exception("Format:\njava -jar obtainPublicKey.jar <URL>");
		}

		jwkSetURL = args[0];

		CamelContext context = new DefaultCamelContext();

		String url = null;

		if (args[0].startsWith("https://") || args[0].startsWith("HTTPS://")) {
			url = jwkSetURL.replaceFirst("^https://", "https4://");
		} else {
			url = jwkSetURL.replaceFirst("^http://", "http4://");
		}

		context.addRoutes(new PublicKeyRouteBuilder());
		context.addRoutes(new FetchJWKSetRouteBuilder(url));

		ProducerTemplate template = context.createProducerTemplate();

		context.start();

		String publicKeyStr = template.requestBody("direct:obtainKeyFromHttp", null, String.class);

		System.out.println(publicKeyStr);

	}

}
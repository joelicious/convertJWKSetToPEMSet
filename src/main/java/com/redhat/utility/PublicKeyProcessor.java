package com.redhat.utility;

import java.io.IOException;
import java.io.StringWriter;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.keycloak.jose.jwk.JSONWebKeySet;
import org.keycloak.jose.jwk.JWK;
import org.keycloak.jose.jwk.JWKParser;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.redhat.utility.entities.KidToPublicKey;

/**
 * @author <a href="mailto:jbutler@redhat.com">Joseph S. Butler</a>
 */
public class PublicKeyProcessor implements Processor {

	private static final Logger LOG = LoggerFactory.getLogger(PublicKeyProcessor.class);

	private List<KidToPublicKey> listOfPublicKeys = new ArrayList<KidToPublicKey>();

	public void process(Exchange exchange) throws Exception {

		String jwkTokens = exchange.getIn().getBody(String.class);

		LOG.info("In PublicKeyProcessor, this is body: " + jwkTokens);

		JSONWebKeySet webKeySet = JsonSerialization.readValue(jwkTokens, JSONWebKeySet.class);
		JWK[] jwkArray = webKeySet.getKeys();

		for (JWK jwk : jwkArray) {

			final KidToPublicKey kidToPublicKey = new KidToPublicKey();

			final JWKParser jwkParser = JWKParser.create(jwk);

			if (jwkParser.getJwk().getKeyType().equalsIgnoreCase("RSA")) {

				final PublicKey publicKey = jwkParser.toPublicKey();

				kidToPublicKey.setKid(jwkParser.getJwk().getKeyId());

				StringWriter sw = new StringWriter();
				JcaPEMWriter writer = new JcaPEMWriter(sw);
				try {
					writer.writeObject(publicKey);
					writer.flush();
				} catch (IOException e) {
					throw new RuntimeException(e);
				} finally {
					writer.close();
				}

				kidToPublicKey.setPublicKey(trimAndSlurp(sw.toString()));

				listOfPublicKeys.add(kidToPublicKey);

			}

		}

		String publicKeyJSON = "{\"publicKeys\":[";

		int count = 0;

		for (KidToPublicKey kidToPublicKey : listOfPublicKeys) {

			publicKeyJSON = publicKeyJSON + "{\"kid\":\"" + kidToPublicKey.getKid() + "\",\"publicKey\":\""
					+ kidToPublicKey.getPublicKey() + "\"}";

			if (count != (listOfPublicKeys.size() - 1)) {
				publicKeyJSON = publicKeyJSON + ",";
				count++;
			}

		}

		publicKeyJSON = publicKeyJSON + "]}";

		exchange.getIn().setBody(publicKeyJSON);

	}

	private String trimAndSlurp(String publicKeyStr) {

		String returnableStr = "";

		String[] lines = publicKeyStr.split("\\r?\\n");
		for (int i = 1; i < lines.length - 1; i++) {
			returnableStr = returnableStr + lines[i];
		}

		return returnableStr;

	}

}

# Convert JWK to PEM
A simple utility written in Java + Camel to fetch a JWK Set from an IDP URL and then produce the Public Key

Usage: 

	java -jar obtain-publickeys-1.0.0-SNAPSHOT.jar <URL>

Both http and https address are supported. For https, this utility assumes that the cert as been imported into the JDK cacerts.

As an example, the procedure to import a cert is the following.  The cert itself can be obtained from the browser or the original keystore.

	sudo keytool -import -trustcacerts -file ${CERT_LOCATION}/openam.mydomain.com.crt -alias open.mydomain.com -keystore ${JAVA_HOME}/jre/lib/security/cacerts

	keytool -list -v -keystore ./cacerts | grep openam.mydomain
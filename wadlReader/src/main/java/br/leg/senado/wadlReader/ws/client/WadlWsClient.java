package br.leg.senado.wadlReader.ws.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import br.leg.senado.wadlReader.wadl.WadlApplication;

public class WadlWsClient {
	public WadlApplication getWadlApplication(String url) {
		Client client = ClientBuilder.newClient();

		WebTarget target = client.target(url);

		WadlApplication application = target.request(MediaType.TEXT_XML).get(WadlApplication.class);

		return application;

	}
}

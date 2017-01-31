package br.leg.senado.webservicesDiff.ws;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import br.leg.senado.webservicesDiff.diff.XMLDiff;
import br.leg.senado.webservicesDiff.dto.ResultadoDTO;
import freemarker.core.ParseException;
import freemarker.template.TemplateException;

public class XMLWs {
	private static final Logger debugLog = Logger.getLogger("debugLogger");
	private static XMLWs instance;

	public List<ResultadoDTO> compareUrlsResults(List<ResultadoDTO> urls) throws ParseException, IOException, TemplateException {
		List<ResultadoDTO> resultados = new ArrayList<ResultadoDTO>();
		for (ResultadoDTO resultado : urls) {

			try {
				debugLog.info(String.format("Comparando %s com %s", resultado.getUrl1(), resultado.getUrl2()));
				XMLDiff xmlDiff = XMLDiff.getInstance();
				Diff diff = xmlDiff.compareResults(getXmlInputStreamFromUrl(resultado.getUrl1()), getXmlInputStreamFromUrl(resultado.getUrl2()));
				Iterable<Difference> differences = diff.getDifferences();
				List<Difference> diferencas = new ArrayList<Difference>();

				for (Difference difference : differences) {
					diferencas.add(difference);
					debugLog.debug(difference);

				}
				if (diferencas.size() == 0) {
					debugLog.info(String.format("No differences were found"));
				} else if (diferencas.size() == 1) {
					debugLog.info(String.format("No differences were found"));
				} else {
					debugLog.info(String.format("%s differences were found", diferencas.size()));
				}
				resultado.setDiferencas(diferencas);

				resultado.setNumeroDiferencas(diferencas.size());

				resultados.add(resultado);
			} catch (Exception e) {
				debugLog.error(e.getMessage(), e);

			}

		}
		return resultados;
	}

	public String getXmlInputStreamFromUrl(String urlString) throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL(urlString);
		debugLog.info(String.format("Lendo dados de  %s", urlString));
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept", "application/xml");
		InputStream content = connection.getInputStream();
		String string = IOUtils.toString(content, "UTF-8");
		connection.disconnect();
		content.close();
		return string;
	}

	public static XMLWs getInstance() {
		if (instance == null) {
			instance = new XMLWs();
		}
		return instance;
	}

}

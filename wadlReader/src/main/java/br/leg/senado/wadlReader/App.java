package br.leg.senado.wadlReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import br.leg.senado.wadlReader.wadl.WadlApplication;
import br.leg.senado.wadlReader.wadl.WadlResource;
import br.leg.senado.wadlReader.wadl.WadlResources;
import br.leg.senado.wadlReader.ws.client.WadlWsClient;

public class App {
	private static final Logger debugLog = Logger.getLogger("debugLogger");

	public static void main(String[] args) throws Exception {
		try {
			String path = Paths.get(".").toAbsolutePath().normalize().toString();

			String baseUrl1 = "http://www6ghml.senado.gov.br/dadosabertos";
			String baseUrl2 = "http://legis.senado.gov.br/dadosabertos";
			WadlWsClient wsClient = new WadlWsClient();
			WadlApplication wadlsApplication = wsClient.getWadlApplication("http://www6g.senado.leg.br/dadosabertos/docs/application.wadl");

			File fileParametros = new File(String.format("%s%sparametros.txt", path, File.separator));

			PrintWriter printerParametros = null;
			Boolean existeArquivoParametros = fileParametros.exists();
			if (existeArquivoParametros) {
				debugLog.info("Arquivo de parâmetros já existe. Caso queira gerá-lo novamente, apague o arquivo existente");

			} else {
				debugLog.info("Criando arquivo de parâmetros.");
				printerParametros = new PrintWriter(String.format("%s%sparametros.txt", path, File.separator), "UTF-8");
			}

			if (!existeArquivoParametros) {

				for (WadlResources resources : wadlsApplication.getResources()) {

					String base = resources.getBase().replaceFirst(".", "");
					debugLog.info(base);
					for (WadlResource resource : resources.getResource()) {
						String resourcePath = resource.getPath();
						debugLog.info(resource.getPath());

						String[] paths = resourcePath.split("/");
						for (String string : paths) {
							if (string.contains("{")) {
								printerParametros.println(String.format("%s,%s,", resourcePath, string));
								// resourcePath = resource.getPath().replaceFirst("\\{([A-z])*\\}", s);
							}
						}

					}

				}
				printerParametros.close();

			}
			PrintWriter printerUrls = new PrintWriter(String.format("%s%surls-geradas.txt", path, File.separator), "UTF-8");

			debugLog.info("Lendo parâmetros");
			BufferedReader reader = new BufferedReader(new FileReader(fileParametros));
			Map<String, Object> mapaParametros = new HashMap<String, Object>();
			String linhaCorrente = null;

			linhaCorrente = reader.readLine();
			while (linhaCorrente != null) {
				String[] linha = linhaCorrente.split(",");

				mapaParametros.put(linha[0] + linha[1], linhaCorrente);

				linhaCorrente = reader.readLine();

			}

			reader.close();
			for (WadlResources resources : wadlsApplication.getResources()) {

				String base = resources.getBase().replaceFirst(".", "");
				debugLog.info(base);
				for (WadlResource resource : resources.getResource()) {
					String resourcePath = resource.getPath();
					debugLog.info(resource.getPath());

					String[] paths = resourcePath.split("/");
					for (String string : paths) {
						if (string.contains("{")) {
							debugLog.info(String.format("Identificado parâmetro %s", string));
							String linha = (String) mapaParametros.get(resource.getPath() + string);
							if (linha != null) {
								String[] valores = linha.split(",");
								if (valores.length > 2) {
									debugLog.info(String.format("Substituindo parâmetro %s por %s em %s", string, valores[2], resourcePath));
									resourcePath = resourcePath.replace(string, valores[2]);
								}
							}
							// printerParametros.println(String.format("%s,%s,", resourcePath, string));
							// resourcePath = resource.getPath().replaceFirst("\\{([A-z])*\\}", s);
						}
					}
					if (!resourcePath.contains("{")) {
						printerUrls.println(String.format("%s%s%s,%s%s%s", baseUrl1, base, resourcePath, baseUrl2, base, resourcePath));
					}
				}

			}
			printerUrls.close();
		} catch (Exception e) {
			debugLog.error(e.getMessage(), e);
		}
	}

}

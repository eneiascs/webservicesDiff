package br.leg.senado.webservicesDiff;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import br.leg.senado.webservicesDiff.dto.ResultadoDTO;
import br.leg.senado.webservicesDiff.io.Files;
import br.leg.senado.webservicesDiff.templates.FreemakerConfiguration;
import br.leg.senado.webservicesDiff.ws.XMLWs;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class App {
	private static final Logger debugLog = Logger.getLogger("debugLogger");
	private static Configuration cfg;

	public static void main(String[] args) throws Exception {
		String path = Paths.get(".").toAbsolutePath().normalize().toString();

		cfg = FreemakerConfiguration.getConfiguration();
		cfg.setClassForTemplateLoading(App.class, "/html/");

		debugLog.info("Iniciando processamento");

		String fileName = String.format("%s%surls.txt", path, File.separator);

		if (args.length > 0) {
			fileName = args[0];
		}

		if (args.length > 1) {
			path = args[1];
		}
		if (!new File(fileName).canRead()) {
			debugLog.error(String.format("Não foi possível ler o arquivo %s", fileName));
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("YYYYMMddHHmmss");
		File results = new File(String.format("%s%sresults-%s", path, File.separator, sdf2.format(new Date())));

		results.mkdir();

		Files files = new Files();
		List<ResultadoDTO> urls = files.getUrlsFromInput(new FileReader(fileName));
		XMLWs xmlWs = XMLWs.getInstance();
		List<ResultadoDTO> resultados = xmlWs.compareUrlsResults(urls);

		resultados = files.printToTxt(resultados, results.getAbsolutePath());
		resultados = files.printToHtml(resultados, results.getAbsolutePath());

		File fileResultado = new File(String.format("%s%sresultados.html", results, File.separator));
		Template temp = cfg.getTemplate("resultados.html");
		Writer out = new OutputStreamWriter(new FileOutputStream(fileResultado));
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("resultados", resultados);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYY HH:mm:ss");

		root.put("data", sdf.format(new Date()));
		temp.process(root, out);
		debugLog.info("Fim do processamento");
		Desktop.getDesktop().browse(fileResultado.toURI());

	}

}

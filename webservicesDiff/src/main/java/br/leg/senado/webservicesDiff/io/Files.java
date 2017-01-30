package br.leg.senado.webservicesDiff.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.xmlunit.diff.Difference;

import br.leg.senado.webservicesDiff.dto.ResultadoDTO;
import br.leg.senado.webservicesDiff.templates.FreemakerConfiguration;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

public class Files {
	private static final Logger debugLog = Logger.getLogger("debugLogger");

	public List<ResultadoDTO> getUrlsFromInput(InputStreamReader inputReader) throws FileNotFoundException, IOException {
		List<ResultadoDTO> resultados = new ArrayList<ResultadoDTO>();

		debugLog.info("Obtendo lista de webservices");

		BufferedReader reader = new BufferedReader(inputReader);

		String linhaCorrente = null;

		linhaCorrente = reader.readLine();
		while (linhaCorrente != null) {
			String[] url = linhaCorrente.split(",");
			ResultadoDTO resultado = new ResultadoDTO();
			resultado.setUrl1(url[0]);
			resultado.setUrl2(url[1]);

			linhaCorrente = reader.readLine();
			resultados.add(resultado);
		}

		reader.close();
		return resultados;
	}

	public List<ResultadoDTO> printToTxt(List<ResultadoDTO> resultados, String path) throws FileNotFoundException, UnsupportedEncodingException {

		for (ResultadoDTO resultado : resultados) {

			String nomeArquivo = String.format("%s%s%s", path, File.separator, resultado.getUrl1().replaceAll("[^A-Za-z0-9]", "-"));
			PrintWriter printer = new PrintWriter(String.format("%s.txt", nomeArquivo), "UTF-8");
			printer.println((String.format("Comparing url %s with %s", resultado.getUrl1(), resultado.getUrl2())));
			resultado.setLinkResultado(String.format("%s.txt", nomeArquivo));

			for (Difference difference : resultado.getDiferencas()) {

				printer.println(difference);

			}
			if (resultado.getNumeroDiferencas() == 0) {
				printer.println(String.format("No differences were found"));
				debugLog.info(String.format("No differences were found"));
			} else if (resultado.getNumeroDiferencas() == 1) {
				printer.println(String.format("1 difference was found"));
				debugLog.info(String.format("No differences were found"));
			} else {
				printer.println(String.format("%s differences were found", resultado.getNumeroDiferencas()));
				debugLog.info(String.format("%s differences were found", resultado.getNumeroDiferencas()));
			}

			printer.close();
		}
		return resultados;

	}

	public List<ResultadoDTO> printToHtml(List<ResultadoDTO> resultados, String path) throws TemplateNotFoundException, MalformedTemplateNameException,
	        ParseException, IOException, TemplateException {

		for (ResultadoDTO resultado : resultados) {

			String nomeArquivo = String.format("%s%s%s", path, File.separator, resultado.getUrl1().replaceAll("[^A-Za-z0-9]", "-"));

			resultado.setLinkResultado(String.format("%s.html", nomeArquivo));

			Template temp = FreemakerConfiguration.getConfiguration().getTemplate("resultado.html");

			Writer out = new OutputStreamWriter(new FileOutputStream(String.format("%s.html", nomeArquivo)));
			Map<String, Object> root = new HashMap<String, Object>();
			root.put("resultados", resultados);

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYY HH:mm:ss");
			root.put("resultado", resultado);
			root.put("data", sdf.format(new Date()));
			root.put("diferencas", resultado.getDiferencas());

			temp.process(root, out);

		}
		return resultados;
	}
}

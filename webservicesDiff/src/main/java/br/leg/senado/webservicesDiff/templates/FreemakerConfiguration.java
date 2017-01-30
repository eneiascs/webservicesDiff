package br.leg.senado.webservicesDiff.templates;

import freemarker.template.Configuration;
import freemarker.template.Version;

public class FreemakerConfiguration {
	public static Configuration config;
	private static Version version = Configuration.VERSION_2_3_23;
	private static String encoding = "UTF-8";

	private static boolean logTemplateExceptions = false;

	public static Configuration getConfiguration() {
		if (config == null) {

			config = new Configuration(version);

			config.setDefaultEncoding(encoding);

			config.setLogTemplateExceptions(logTemplateExceptions);
		}
		return config;
	}
}

package br.leg.senado.webservicesDiff.dto;

import java.util.List;

import org.xmlunit.diff.Difference;

public class ResultadoDTO {
	private String url1;
	private String url2;
	private String linkResultado;
	private int numeroDiferencas;
	private List<Difference> diferencas;

	public String getUrl1() {
		return url1;
	}

	public void setUrl1(String url1) {
		this.url1 = url1;
	}

	public String getUrl2() {
		return url2;
	}

	public void setUrl2(String url2) {
		this.url2 = url2;
	}

	public String getLinkResultado() {
		return linkResultado;
	}

	public void setLinkResultado(String linkResultado) {
		this.linkResultado = linkResultado;
	}

	public int getNumeroDiferencas() {
		return numeroDiferencas;
	}

	public void setNumeroDiferencas(int numeroDiferencas) {
		this.numeroDiferencas = numeroDiferencas;
	}

	public List<Difference> getDiferencas() {
		return diferencas;
	}

	public void setDiferencas(List<Difference> diferencas) {
		this.diferencas = diferencas;
	}
}

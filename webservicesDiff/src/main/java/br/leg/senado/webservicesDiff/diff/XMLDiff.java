package br.leg.senado.webservicesDiff.diff;

import org.apache.log4j.Logger;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Comparison;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.ComparisonType;
import org.xmlunit.diff.DefaultNodeMatcher;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.DifferenceEvaluator;
import org.xmlunit.diff.ElementSelectors;

public class XMLDiff {
	private static final Logger debugLog = Logger.getLogger("debugLogger");
	private static XMLDiff instance;

	public Diff compareResults(String xml1, String xml2) {

		Diff myDiff = DiffBuilder.compare(xml1).withTest(xml2).ignoreComments().ignoreWhitespace().normalizeWhitespace()
		        .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byName)).withDifferenceEvaluator(new DifferenceEvaluator() {

			        @Override
			        public ComparisonResult evaluate(Comparison comparison, ComparisonResult outcome) {
				        if (outcome == ComparisonResult.DIFFERENT && comparison.getType() == ComparisonType.CHILD_NODELIST_SEQUENCE) {
					        return ComparisonResult.EQUAL;
				        }

				        return outcome;
			        }
		        }).build();

		return myDiff;
	}

	public static XMLDiff getInstance() {
		if (instance == null) {
			instance = new XMLDiff();
		}
		return instance;
	}

}

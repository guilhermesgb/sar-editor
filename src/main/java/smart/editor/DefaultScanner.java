package smart.editor;

import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.WhitespaceRule;

public class DefaultScanner extends RuleBasedScanner {

	public DefaultScanner(ColorManager manager) {
		
		IRule[] rules = new IRule[1];
		
		// Add generic whitespace rule.
		rules[0] = new WhitespaceRule(new WhitespaceDetector());

		setRules(rules);
	}
}
package smart.editor;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

import smart.editor.keywords.SARDeclarationKeyword;
import smart.editor.keywords.SAREquipmentTypeKeyword;
import smart.editor.rules.KeywordRule;
import smart.editor.rules.ParenRule;
import smart.editor.rules.PunctRule;
import smart.editor.rules.RegwordRule;
import smart.editor.rules.VarrefRule;

public class PartitionScanner extends RuleBasedPartitionScanner {
	public final static String SAR_STRING = "__sar_string";
	public final static String SAR_PUNCT = "__sar_punct";
	public final static String SAR_PAREN = "__sar_paren";
	public final static String SAR_COMMENT = "__sar_comment";
	public final static String SAR_KEYWORD = "__sar_keyword";
	public final static String SAR_EQUIPTYPE = "__sar_equiptype";
	public final static String SAR_VARREF = "__sar_varref";
	public final static String SAR_REGWORD = "__sar_regword";

	public PartitionScanner() {

		List<IPredicateRule> rules = new LinkedList<IPredicateRule>();
		rules.add(new MultiLineRule("\"", "\"", new Token(SAR_STRING), '\\'));
		rules.add(new PunctRule(new Token(SAR_PUNCT)));
		rules.add(new ParenRule(new Token(SAR_PAREN)));
		rules.add(new SingleLineRule("//", "//", new Token(SAR_COMMENT), '/', true));
		rules.addAll(KeywordRule.getKeywordRules(SARDeclarationKeyword.values(), SAR_KEYWORD));
		rules.addAll(KeywordRule.getKeywordRules(SAREquipmentTypeKeyword.values(), SAR_EQUIPTYPE));
		rules.add(new VarrefRule(new Token(SAR_VARREF)));
		rules.add(new RegwordRule(new Token(SAR_REGWORD)));
		setPredicateRules(rules.toArray(new IPredicateRule[rules.size()]));
	}
}
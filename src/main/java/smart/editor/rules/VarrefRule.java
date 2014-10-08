package smart.editor.rules;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class VarrefRule implements IPredicateRule {

	private IToken token;
	private String regex;
	private int min;
	private int rewind;
	protected String input;
	
	public VarrefRule(IToken token){
		this.token = token;
		this.regex = "(\\$[^\\s\\{\\}\\|\\(\\)\\.=&\\:;!/]+[\\s\\{\\}\\|\\(\\)\\.=&\\:;!/])";
		this.min = 1;
		this.rewind = 1;
	}
	@Override
	public IToken evaluate(ICharacterScanner scanner) {
		input = "";
		if(min>1){
			scanner.unread();	
		}			
		for(int i=0;i<min;i++){
			input += (char)scanner.read();
		}
		int unreads = 0;
		int next;
		do{
			next = scanner.read();
			unreads++;
			input += (char)next;
		}while(!(isPunctuationOrBlank((char)next) || next == ICharacterScanner.EOF));
		if(!input.matches(regex)){
			for(int i=0;i<min-(min>1?1:0);i++){
				scanner.unread();
			}
			for (int i=0;i<unreads;i++){
				scanner.unread();
			}
			return Token.UNDEFINED;
		}
		else {
			for(int i=0;i<rewind;i++){
				scanner.unread();
			}
			return token;
		}
	}
	private boolean isPunctuationOrBlank(char next) {
		return next == '{' || next == '}' || next == '|' || next == '(' || next == ':'
						   || next == ')' || next == '.' || next == ' ' || next == '!'
						   || next == '=' || next == '&' || next == ';' || next == '/';
	}
	@Override
	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}
	protected void rewind(ICharacterScanner scanner){
		int size = input.length()-2;
		for(int i=0;i<size;i++){
			scanner.unread();
		}
	}
	@Override
	public IToken getSuccessToken() {
		return token;
	}
}
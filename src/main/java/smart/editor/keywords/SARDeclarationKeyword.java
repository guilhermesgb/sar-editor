package smart.editor.keywords;

public enum SARDeclarationKeyword implements SARKeyword {

	MACROS_SECTION("Macros para", SARKeywordLevel.SECTION_LEVEL),
	MACRO_EXPRESSION("Macro", SARKeywordLevel.SECTION_ITEM_LEVEL),
	
	RULES_SECTION("Regras para", SARKeywordLevel.SECTION_LEVEL),
	RULE_TITLE("Titulo", SARKeywordLevel.SECTION_ITEM_LEVEL),
	RULE_CODE("Codigo", SARKeywordLevel.SECTION_ITEM_LEVEL),
	RULE_IS_OVERWRITTENABLE("PodeSerSobrescrita", SARKeywordLevel.SECTION_ITEM_LEVEL),
	RULE_TYPE("Tipo", SARKeywordLevel.SECTION_ITEM_LEVEL),
	RULE_PRIORITY("Prioridade", SARKeywordLevel.SECTION_ITEM_LEVEL),
	RULE_CONDITIONAL("Existe se", SARKeywordLevel.SECTION_ITEM_LEVEL),
	RULE_EXPRESSION("Regra", SARKeywordLevel.SECTION_ITEM_LEVEL),
	
	ATTRIBUTES_SECTION("Atributos para", SARKeywordLevel.SECTION_LEVEL),
	ATTRIBUTE_ID("ID", SARKeywordLevel.SECTION_ITEM_LEVEL),
	ATTRIBUTE_DECLARATION("Atributo", SARKeywordLevel.SECTION_ITEM_LEVEL),
	ATTRIBUTE_RESTART("Religamento", SARKeywordLevel.SECTION_ITEM_LEVEL),
	ATTRIBUTE_VIEWABLE("Visualizavel", SARKeywordLevel.SECTION_ITEM_LEVEL),
	ATTRIBUTE_EXPRESSION("Expressao", SARKeywordLevel.SECTION_ITEM_LEVEL);

	private String keyword;
	private int level;
	
	private SARDeclarationKeyword(String keyword, int level) {
		this.keyword = keyword;
		this.level = level;
	}

	@Override
	public String getKeyword() {
		return keyword;
	}

	@Override
	public Object getKeywordData() {
		return level;
	}
}
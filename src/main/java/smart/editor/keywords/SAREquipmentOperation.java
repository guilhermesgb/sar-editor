package smart.editor.keywords;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import smart.alarms.equipment.DecoratedEquipment;
import smart.editor.KeywordCompletionProcessor;
import smart.model.topology.ConductingEquipment;

public class SAREquipmentOperation {

	public static String[] getOperations(IDocument doc, int offset, boolean withinMacroScope){

		Map<String, Boolean> operationsFound = new HashMap<String, Boolean>();

		PriorityQueue<String> operations =
				new PriorityQueue<String>(9, new Comparator<String>() {

					@Override
					public int compare(String operationName1, String operationName2) {
						return operationName1.compareToIgnoreCase(operationName2);
					}
				});

		for ( Method method : DecoratedEquipment.class.getMethods() ){
			if ( method.getReturnType().isEnum() || method.getReturnType().toString().equals("boolean") ){
				if ( !operationsFound.containsKey(method.getName()) ){
					operationsFound.put(method.getName(), true);
					operations.add(method.getName() + "()");
				}
			}
		}

		StringBuilder previous = new StringBuilder();
		try{
			boolean macroBeingDefinedPassed = false;
			boolean withinAttributeContext = false;
			int n = offset - 1;
			for ( ; n >= 0; ) {

				char c = doc.getChar(n);
				if ( Character.isJavaIdentifierPart(c) ){

					String lastWord = KeywordCompletionProcessor.lastWord(doc, n + 1);
					boolean continueThis = true;

					for ( int i=0; i<SARDeclarationKeyword.values().length; i++ ){

						String keyword = SARDeclarationKeyword.values()[i].getKeyword();
						int level = (int) SARDeclarationKeyword.values()[i].getKeywordData();

						if ( (lastWord + " " + previous.toString().trim()).startsWith(keyword) ){

							if ( level == SARKeywordLevel.SECTION_LEVEL && previous.toString().contains(":") ){

								continueThis = false;
								String targetExpression = previous.toString().split(":")[1].trim();
								if ( targetExpression.startsWith("getEquipmentType()")
										&& targetExpression.contains("=") ){

									String targetEquipmentType = targetExpression.split("=")[1].split("\\{")[0].trim();
									SARKeyword[] values = SAREquipmentTypeKeyword.values();
									for ( int j=0; j<values.length; j++ ){

										String equipmentType = values[j].getKeyword();
										if ( equipmentType.equals(targetEquipmentType) ){

											if ( !withinAttributeContext ){

												Class<?> equipmentClass = (Class<?>) values[j].getKeywordData();
												try{
													for ( Method method : equipmentClass.getMethods() ){

														if ( method.getReturnType().isEnum()
																|| method.getReturnType().toString().equals("boolean")
																|| ConductingEquipment.class.isAssignableFrom(method.getReturnType()) ){

															if ( !operationsFound.containsKey(method.getName()) ){
																operationsFound.put(method.getName(), true);
																operations.add(method.getName() + "()");
															}
														}
													}
												}
												catch(NoClassDefFoundError e){
													System.out.println("DID NOT FETCH METHODS LIST OF CLASS  " + equipmentClass.getSimpleName() + " DUE TO NCDF ERROR");
												}

												try{
													for ( Field field : equipmentClass.getDeclaredFields() ){

														if ( ConductingEquipment.class.isAssignableFrom(field.getType()) ){

															if ( !operationsFound.containsKey(field.getName()) ){
																operationsFound.put(field.getName(), true);
																operations.add(field.getName() + ".");
															}
														}
													}
												}
												catch(NoClassDefFoundError e){
													System.out.println("DID NOT FETCH FIELDS LIST OF CLASS  " + equipmentClass.getSimpleName() + " DUE TO NCDF ERROR");
												}
											}
											else{
												Class<?> currentClass = (Class<?>) values[j].getKeywordData();

												String[] pieces;
												if ( withinMacroScope ){
													pieces = previous.toString().split("\\{");
												}
												else{
													pieces = previous.toString().split("\\:");
												}
												String expression = pieces[pieces.length - 1].trim() + " ";
												String[] names = expression.split("[\\|=&]");
												names = names[names.length-1].split("\\.");
												
												for ( int k=0; k<(names.length - 1); k++ ){

													names[k] = names[k].replace("(", "").replace(")", "").trim();
													boolean fieldFound = false;
													try{
														for ( Field field : currentClass.getDeclaredFields() ){

															if ( ConductingEquipment.class.isAssignableFrom(field.getType()) ){

																if ( field.getName().equals(names[k]) ){
																	currentClass = field.getType();
																	fieldFound = true;
																	break;
																}
															}
														}
													}
													catch(NoClassDefFoundError e){
														System.out.println("DID NOT FETCH FIELDS LIST OF CLASS  " + currentClass.getSimpleName() + " DUE TO NCDF ERROR");
													}

													if ( fieldFound ){
														continue;
													}

													boolean methodFound = false;
													try{
														for ( Method method : currentClass.getMethods() ){

															if ( method.getReturnType().isEnum()
																	|| method.getReturnType().toString().equals("boolean")
																	|| ConductingEquipment.class.isAssignableFrom(method.getReturnType()) ){

																if ( method.getName().equals(names[k]) ){
																	currentClass = method.getReturnType();
																	methodFound = true;
																	break;
																}
															}
														}
													}
													catch(NoClassDefFoundError e){
														System.out.println("DID NOT FETCH FIELDS LIST OF CLASS  " + currentClass.getSimpleName() + " DUE TO NCDF ERROR");
													}

													if ( !methodFound ){
														currentClass = null;
														operations.clear();
														break;
													}
												}

												if ( currentClass != null ){

													try{
														for ( Method method : currentClass.getMethods() ){
															
															if ( method.getReturnType().isEnum()
																	|| method.getReturnType().toString().equals("boolean")
																	|| ConductingEquipment.class.isAssignableFrom(method.getReturnType()) ){
																
																if ( !operationsFound.containsKey(method.getName()) ){
																	operationsFound.put(method.getName(), true);
																	operations.add(method.getName() + "()");
																}
															}
														}
													}
													catch(NoClassDefFoundError e){
														System.out.println("DID NOT FETCH METHODS LIST OF CLASS  " + currentClass.getSimpleName() + " DUE TO NCDF ERROR");
													}

													try{
														for ( Field field : currentClass.getDeclaredFields() ){
															
															if ( ConductingEquipment.class.isAssignableFrom(field.getType()) ){
																
																if ( !operationsFound.containsKey(field.getName()) ){
																	operationsFound.put(field.getName(), true);
																	operations.add(field.getName() + ".");
																}
															}
														}
													}
													catch(NoClassDefFoundError e){
														System.out.println("DID NOT FETCH FIELDS LIST OF CLASS  " + currentClass.getSimpleName() + " DUE TO NCDF ERROR");
													}
												}
											}
											break;
										}
									}
								}
								break;
							}
							else if ( !macroBeingDefinedPassed && withinMacroScope
									&& level == SARKeywordLevel.SECTION_ITEM_LEVEL ){
								macroBeingDefinedPassed = true;
							}
							else if ( macroBeingDefinedPassed && withinMacroScope
									&& level == SARKeywordLevel.SECTION_ITEM_LEVEL && !withinAttributeContext ){
								String operationName = previous.toString().trim().split(" ")[1];
								if ( !operationsFound.containsKey(operationName) ){
									operationsFound.put(operationName, true);
									operations.add(operationName);
								}
							}
						}
					}
					previous.insert(0, lastWord);
					n -= lastWord.length();
					if (!continueThis){
						break;
					}
				}
				else{
					if ( c == '.' && !macroBeingDefinedPassed ){
						withinAttributeContext = true;
					}
					previous.insert(0, c);
					n -= 1;
				}
			}
			if ( withinMacroScope && !withinAttributeContext ){
				boolean nextWordIsOperation = false;
				n = offset;
				for ( ; n <= doc.getLength() ; ){
					char c = doc.getChar(n);
					if ( Character.isJavaIdentifierPart(c) ){
						String nextWord = KeywordCompletionProcessor.nextWord(doc, n + 1);
						boolean continueAfterThis = true;
						if ( nextWordIsOperation ){
							if ( !operationsFound.containsKey(nextWord) ){
								operationsFound.put(nextWord, true);
								operations.add(nextWord + "()");
							}
							nextWordIsOperation = false;
						}
						else{
							for ( int i=0; i<SARDeclarationKeyword.values().length; i++ ){
								String keyword = SARDeclarationKeyword.values()[i].getKeyword();
								int level = (int) SARDeclarationKeyword.values()[i].getKeywordData();
								if ( nextWord.equals(keyword) ){
									if ( level == SARKeywordLevel.SECTION_LEVEL ){
										continueAfterThis = false;
										break;
									}
									else if ( level == SARKeywordLevel.SECTION_ITEM_LEVEL ){
										nextWordIsOperation = true;
									}
								}
							}
						}
						previous.append(nextWord);
						n += nextWord.length();
						if (!continueAfterThis){
							break;
						}
					}
					else{
						previous.append(c);
						n += 1;
					}
				}
			}
		}
		catch(BadLocationException e){}
		List<String> ordered = new ArrayList<String>(operations.size());
		while ( !operations.isEmpty() ){
			ordered.add(operations.poll());
		}
		return ordered.toArray(new String[ordered.size()]);
	}
}
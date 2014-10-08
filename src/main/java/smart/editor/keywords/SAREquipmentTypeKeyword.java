package smart.editor.keywords;

import java.util.Comparator;
import java.util.PriorityQueue;

import smart.model.topology.EquipmentTypeEnum;

public class SAREquipmentTypeKeyword implements SARKeyword {

	private String keyword;
	private Class<?> relatedClass;
	private static SARKeyword[] values;

	private SAREquipmentTypeKeyword(String keyword, Class<?> relatedClass) {
		this.keyword = keyword;
		this.relatedClass = relatedClass;
	}

	@Override
	public String getKeyword() {
		return keyword;
	}

	@Override
	public Object getKeywordData() {
		return relatedClass;
	}

	public static SARKeyword[] values(){
		if ( values == null ){
			PriorityQueue<SAREquipmentTypeKeyword> keywords =
					new PriorityQueue<SAREquipmentTypeKeyword>(EquipmentTypeEnum.values().length,
							new Comparator<SAREquipmentTypeKeyword>() {
						
						@Override
						public int compare(SAREquipmentTypeKeyword keyword1,
								SAREquipmentTypeKeyword keyword2) {
							return keyword2.getKeyword().length() - keyword1.getKeyword().length();
						}
					});
			for ( EquipmentTypeEnum equipmentType : EquipmentTypeEnum.values() ){
				keywords.add(new SAREquipmentTypeKeyword(equipmentType.toString(), equipmentType.getRelatedClass()));
			}
			values = keywords.toArray(new SAREquipmentTypeKeyword[keywords.size()]);
		}
		return values;
	}
}
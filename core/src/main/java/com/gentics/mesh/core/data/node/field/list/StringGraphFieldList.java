package com.gentics.mesh.core.data.node.field.list;

import static com.gentics.mesh.core.rest.error.Errors.error;
import static io.netty.handler.codec.http.HttpResponseStatus.BAD_REQUEST;

import com.gentics.mesh.core.data.node.field.FieldGetter;
import com.gentics.mesh.core.data.node.field.FieldTransformator;
import com.gentics.mesh.core.data.node.field.FieldUpdater;
import com.gentics.mesh.core.data.node.field.GraphField;
import com.gentics.mesh.core.data.node.field.StringGraphField;
import com.gentics.mesh.core.rest.node.field.list.impl.StringFieldListImpl;

import rx.Single;

public interface StringGraphFieldList extends ListGraphField<StringGraphField, StringFieldListImpl, String> {

	String TYPE = "string";

	FieldTransformator STRING_LIST_TRANSFORMATOR = (container, ac, fieldKey, fieldSchema, languageTags, level, parentNode) -> {
		StringGraphFieldList stringFieldList = container.getStringList(fieldKey);
		if (stringFieldList == null) {
			return Single.just(null);
		} else {
			return stringFieldList.transformToRest(ac, fieldKey, languageTags, level);
		}
	};

	FieldUpdater STRING_LIST_UPDATER = (container, ac, fieldMap, fieldKey, fieldSchema, schema) -> {
		StringGraphFieldList graphStringList = container.getStringList(fieldKey);
		StringFieldListImpl stringList = fieldMap.getStringFieldList(fieldKey);
		boolean isStringListFieldSetToNull = fieldMap.hasField(fieldKey) && (stringList == null || stringList.getItems() == null);
		GraphField.failOnDeletionOfRequiredField(graphStringList, isStringListFieldSetToNull, fieldSchema, fieldKey, schema);
		boolean restIsNull = stringList == null;
		GraphField.failOnMissingRequiredField(graphStringList, restIsNull, fieldSchema, fieldKey, schema);

		// Handle Deletion
		if (isStringListFieldSetToNull && graphStringList != null) {
			graphStringList.removeField(container);
			return;
		}

		// Rest model is empty or null - Abort
		if (restIsNull) {
			return;
		}

		// Always create a new list. 
		// This will effectively unlink the old list and create a new one. 
		// Otherwise the list which is linked to old versions would be updated. 
		graphStringList = container.createStringList(fieldKey);

		// Handle Update
		graphStringList.removeAll();
		for (String item : stringList.getItems()) {
			if (item == null) {
				throw error(BAD_REQUEST, "field_list_error_null_not_allowed", fieldKey);
			}
			graphStringList.createString(item);
		}
	};

	FieldGetter STRING_LIST_GETTER = (container, fieldSchema) -> {
		return container.getStringList(fieldSchema.getName());
	};

	/**
	 * Create a new string field and add it to the list.
	 * 
	 * @param string
	 * @return
	 */
	StringGraphField createString(String string);

	/**
	 * Return the string item at the given position.
	 * 
	 * @param index
	 * @return
	 */
	StringGraphField getString(int index);

}

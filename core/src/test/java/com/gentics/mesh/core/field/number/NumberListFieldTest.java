package com.gentics.mesh.core.field.number;

import static com.gentics.mesh.core.field.number.NumberListFieldTestHelper.CREATE_EMPTY;
import static com.gentics.mesh.core.field.number.NumberListFieldTestHelper.FETCH;
import static com.gentics.mesh.core.field.number.NumberListFieldTestHelper.FILLNUMBERS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.gentics.mesh.context.InternalActionContext;
import com.gentics.mesh.core.data.NodeGraphFieldContainer;
import com.gentics.mesh.core.data.container.impl.NodeGraphFieldContainerImpl;
import com.gentics.mesh.core.data.node.Node;
import com.gentics.mesh.core.data.node.field.GraphField;
import com.gentics.mesh.core.data.node.field.list.NumberGraphFieldList;
import com.gentics.mesh.core.field.AbstractFieldTest;
import com.gentics.mesh.core.rest.node.NodeResponse;
import com.gentics.mesh.core.rest.node.field.Field;
import com.gentics.mesh.core.rest.node.field.list.impl.DateFieldListImpl;
import com.gentics.mesh.core.rest.node.field.list.impl.NumberFieldListImpl;
import com.gentics.mesh.core.rest.schema.ListFieldSchema;
import com.gentics.mesh.core.rest.schema.impl.ListFieldSchemaImpl;

public class NumberListFieldTest extends AbstractFieldTest<ListFieldSchema> {

	private static final String NUMBER_LIST = "numberList";

	protected ListFieldSchema createFieldSchema(boolean isRequired) {
		ListFieldSchema schema = new ListFieldSchemaImpl();
		schema.setListType("number");
		schema.setName(NUMBER_LIST);
		schema.setRequired(isRequired);
		return schema;
	}

	@Test
	@Override
	public void testFieldTransformation() throws Exception {

		Node node = folder("2015");
		prepareNode(node, "numberList", "number");

		NodeGraphFieldContainer container = node.getGraphFieldContainer(english());
		NumberGraphFieldList numberList = container.createNumberList("numberList");
		numberList.createNumber(1);
		numberList.createNumber(1.11);

		NodeResponse response = transform(node);
		assertList(2, "numberList", "number", response);

	}

	@Test
	@Override
	public void testFieldUpdate() throws Exception {
		NodeGraphFieldContainer container = tx.getGraph().addFramedVertex(NodeGraphFieldContainerImpl.class);
		NumberGraphFieldList list = container.createNumberList("dummyList");

		list.createNumber(1);
		assertEquals(1, list.getList().size());

		list.createNumber(2);
		assertEquals(2, list.getList().size());
		list.removeAll();
		assertEquals(0, list.getSize());
		assertEquals(0, list.getList().size());
	}

	@Test
	@Override
	public void testClone() {
		NodeGraphFieldContainer container = tx.getGraph().addFramedVertex(NodeGraphFieldContainerImpl.class);
		NumberGraphFieldList testField = container.createNumberList("testField");
		testField.createNumber(47);
		testField.createNumber(11);

		NodeGraphFieldContainerImpl otherContainer = tx.getGraph().addFramedVertex(NodeGraphFieldContainerImpl.class);
		testField.cloneTo(otherContainer);

		assertThat(otherContainer.getNumberList("testField")).as("cloned field").isEqualToComparingFieldByField(testField);
	}

	@Test
	@Override
	public void testEquals() {
		NodeGraphFieldContainerImpl container = tx.getGraph().addFramedVertex(NodeGraphFieldContainerImpl.class);
		NumberGraphFieldList fieldA = container.createNumberList("fieldA");
		NumberGraphFieldList fieldB = container.createNumberList("fieldB");
		assertTrue("The field should  be equal to itself", fieldA.equals(fieldA));
		fieldA.addItem(fieldA.createNumber(42L));
		assertTrue("The field should  still be equal to itself", fieldA.equals(fieldA));

		assertFalse("The field should not be equal to a non-string field", fieldA.equals("bogus"));
		assertFalse("The field should not be equal since fieldB has no value", fieldA.equals(fieldB));
		fieldB.addItem(fieldB.createNumber(42L));
		assertTrue("Both fields have the same value and should be equal", fieldA.equals(fieldB));
	}

	@Test
	@Override
	public void testEqualsNull() {
		NodeGraphFieldContainerImpl container = tx.getGraph().addFramedVertex(NodeGraphFieldContainerImpl.class);
		NumberGraphFieldList fieldA = container.createNumberList("fieldA");
		assertFalse(fieldA.equals((Field) null));
		assertFalse(fieldA.equals((GraphField) null));
	}

	@Test
	@Override
	public void testEqualsRestField() {
		NodeGraphFieldContainer container = tx.getGraph().addFramedVertex(NodeGraphFieldContainerImpl.class);
		Long dummyValue = 42L;

		// rest null - graph null
		NumberGraphFieldList fieldA = container.createNumberList(NUMBER_LIST);

		NumberFieldListImpl restField = new NumberFieldListImpl();
		assertTrue("Both fields should be equal to eachother since both values are null", fieldA.equals(restField));

		// rest set - graph set - different values
		fieldA.addItem(fieldA.createNumber(dummyValue));
		restField.add(dummyValue + 1L);
		assertFalse("Both fields should be different since both values are not equal", fieldA.equals(restField));

		// rest set - graph set - same value
		restField.getItems().clear();
		restField.add(dummyValue);
		assertTrue("Both fields should be equal since values are equal", fieldA.equals(restField));

		DateFieldListImpl otherTypeRestField = new DateFieldListImpl();
		otherTypeRestField.add(dummyValue);
		// rest set - graph set - same value different type
		assertFalse("Fields should not be equal since the type does not match.", fieldA.equals(otherTypeRestField));

	}

	@Test
	@Override
	public void testUpdateFromRestNullOnCreate() {
		invokeUpdateFromRestTestcase(NUMBER_LIST, FETCH, CREATE_EMPTY);

	}

	@Test
	@Override
	public void testUpdateFromRestNullOnCreateRequired() {
		invokeUpdateFromRestNullOnCreateRequiredTestcase(NUMBER_LIST, FETCH, CREATE_EMPTY);

	}

	@Test
	@Override
	public void testRemoveFieldViaNullValue() {
		InternalActionContext ac = getMockedInternalActionContext("");
		invokeRemoveFieldViaNullValueTestcase(NUMBER_LIST, FETCH, CREATE_EMPTY, (node) -> {
			NumberFieldListImpl field = null;
			updateContainer(ac, node, NUMBER_LIST, field);
		});
	}

	@Test
	@Override
	public void testDeleteRequiredFieldViaNullValue() {
		InternalActionContext ac = getMockedInternalActionContext("");
		invokeDeleteRequiredFieldViaNullValueTestcase(NUMBER_LIST, FETCH, FILLNUMBERS, (container) -> {
			NumberFieldListImpl field = null;
			updateContainer(ac, container, NUMBER_LIST, field);
		});
	}

	@Test
	@Override
	public void testUpdateFromRestValidSimpleValue() {
		InternalActionContext ac = getMockedInternalActionContext("");
		invokeUpdateFromRestValidSimpleValueTestcase(NUMBER_LIST, FILLNUMBERS, (container) -> {
			NumberFieldListImpl field = new NumberFieldListImpl();
			field.getItems().add(42L);
			field.getItems().add(43L);
			updateContainer(ac, container, NUMBER_LIST, field);
		} , (container) -> {
			NumberGraphFieldList field = container.getNumberList(NUMBER_LIST);
			assertNotNull("The graph field {" + NUMBER_LIST + "} could not be found.", field);
			assertEquals("The list of the field was not updated.", 2, field.getList().size());
			assertEquals("The list item of the field was not updated.", 42L, field.getList().get(0).getNumber());
			assertEquals("The list item of the field was not updated.", 43L, field.getList().get(1).getNumber());
		});
	}

}

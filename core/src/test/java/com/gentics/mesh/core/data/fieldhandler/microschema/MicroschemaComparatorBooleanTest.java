package com.gentics.mesh.core.data.fieldhandler.microschema;

import org.springframework.beans.factory.annotation.Autowired;

import com.gentics.mesh.core.data.fieldhandler.AbstractComparatorBooleanTest;
import com.gentics.mesh.core.data.schema.handler.AbstractFieldSchemaContainerComparator;
import com.gentics.mesh.core.data.schema.handler.MicroschemaComparator;
import com.gentics.mesh.core.rest.microschema.impl.MicroschemaImpl;
import com.gentics.mesh.core.rest.schema.Microschema;

public class MicroschemaComparatorBooleanTest extends AbstractComparatorBooleanTest<Microschema> {

	@Autowired
	protected MicroschemaComparator comparator;

	@Override
	public AbstractFieldSchemaContainerComparator<Microschema> getComparator() {
		return comparator;
	}

	@Override
	public Microschema createContainer() {
		return new MicroschemaImpl();
	}
}

/*
 * Copyright 2013-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.nyc.doitt.gis.geoclient.config.xml;

import static gov.nyc.doitt.gis.geoclient.config.xml.GeoclientXmlReader.*;
import gov.nyc.doitt.gis.geoclient.function.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FieldConverter implements Converter
{
	private static final Logger log = LoggerFactory.getLogger(FieldConverter.class);

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type)
	{
		return Field.class.isAssignableFrom(type);
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context)
	{
		throw new UnsupportedOperationException("Marshalling back to XML is not implemented");
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
	{
		String id = reader.getAttribute(XML_FIELD_ATTRIBUTE_ID);
		// The start value from XML is 1-indexed and needs to be adjusted for 
		// the Field class which expects a zero-indexed start value.
		int start = Integer.valueOf(reader.getAttribute(XML_FIELD_ATTRIBUTE_START)) - 1;
		int length = Integer.valueOf(reader.getAttribute(XML_FIELD_ATTRIBUTE_LENGTH));
		boolean isComposite = XML_FIELD_VALUE_COMPOSITE_TYPE.equalsIgnoreCase(reader.getAttribute(XML_FIELD_ATTRIBUTE_TYPE));
		boolean isInput = "true".equalsIgnoreCase(reader.getAttribute(XML_FIELD_ATTRIBUTE_INPUT));
		String alias = reader.getAttribute(XML_FIELD_ATTRIBUTE_ALIAS);
		boolean whitespace = "true".equalsIgnoreCase(reader.getAttribute(XML_FIELD_ATTRIBUTE_WHITESPACE));
		Field field = new Field(id,start,length,isComposite,isInput,alias,whitespace);
		log.trace("Created {}", field);
		return field;
	}

}

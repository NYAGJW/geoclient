package gov.nyc.doitt.gis.geoclient.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import gov.nyc.doitt.gis.geoclient.function.Field;
import gov.nyc.doitt.gis.geoclient.function.Filter;
import gov.nyc.doitt.gis.geoclient.function.WorkArea;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

public class WorkAreaConfigTest
{
	private String id;
	private int length;
	private boolean isWorkAreaOne;
	private WorkAreaConfig workAreaConfig;
	private List<Field> fields;
	private List<Filter> outputFilters;

	@Before
	public void setUp() throws Exception
	{
		this.id = "WA1";
		this.length = 12;
		this.isWorkAreaOne = true;
		this.fields = TestData.newFieldList(TestData.fieldOne, TestData.fieldTwo);
		this.outputFilters = new ArrayList<Filter>();
		this.outputFilters.add(new Filter(TestData.fieldOne.getId())); 
		this.workAreaConfig = new WorkAreaConfig(this.id, this.length, this.isWorkAreaOne, this.fields, this.outputFilters);		
	}
	
	@Test
	public void testValidateList()
	{
		Field duplicate = new Field("duppy",TestData.fieldOne.getStart(),TestData.fieldOne.getLength());
		assertEquals(TestData.fieldOne, duplicate);
		assertTrue(TestData.fieldOne.compareTo(duplicate) == 0);
		this.workAreaConfig.getFields().add(duplicate);
		assertEquals(3, this.workAreaConfig.getFields().size());
		SortedSet<Field> uniqueFields = new TreeSet<Field>();
		List<Field> dupes = this.workAreaConfig.validate(this.workAreaConfig.getFields(), uniqueFields);
		assertEquals(1, dupes.size());
		assertTrue(dupes.contains(duplicate));
		assertEquals(2, uniqueFields.size());
		assertTrue(uniqueFields.contains(TestData.fieldOne));
		assertTrue(uniqueFields.contains(TestData.fieldTwo));
	}
	
	@Test
	public void testConstructor_valid()
	{
		assertEquals(this.id, this.workAreaConfig.getId());
		assertTrue(this.length==this.workAreaConfig.getLength());
		assertTrue(this.workAreaConfig.isWorkAreaOne());
		assertEquals(this.outputFilters, this.workAreaConfig.getOutputFilters());
	}

	@Test
	public void testCreateWorkArea()
	{
		Registry.clearWorkAreas();
		WorkArea workArea = this.workAreaConfig.createWorkArea();
		assertTrue(Registry.containsWorkArea(workArea.getId()));
		assertTrue(this.length==workArea.length());
		assertTrue(workArea.isFiltered(TestData.fieldOne));
	}

	@Test(expected=InvalidWorkAreaLengthException.class)
	public void testCreateWorkArea_invalidLength()
	{
		Registry.clearWorkAreas();
		new WorkAreaConfig(this.id, 13, this.isWorkAreaOne,this.fields,this.outputFilters).createWorkArea();
	}

}
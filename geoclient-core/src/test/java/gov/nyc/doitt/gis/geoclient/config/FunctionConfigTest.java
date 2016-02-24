package gov.nyc.doitt.gis.geoclient.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import gov.nyc.doitt.gis.geoclient.function.Filter;
import gov.nyc.doitt.gis.geoclient.function.Function;
import gov.nyc.doitt.gis.geoclient.function.DefaultConfiguration;
import gov.nyc.doitt.gis.geoclient.jni.GeoclientStub;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

// TODO Cleanup this test and move FunctionConfig creation into each test case
public class FunctionConfigTest
{
	private WorkAreaConfig wa1Config;
	private WorkAreaConfig wa2Config;
	private FunctionConfig oneWorkAreaFunction;
	private FunctionConfig twoWorkAreaFunction;
    private FunctionConfig twoWorkAreaDefaultArgsFunction;
	private DefaultConfiguration configuration;

	@Before
	public void setUp() throws Exception
	{
		this.wa1Config = new WorkAreaConfig("WW1", 12, true, TestData.newFieldList(TestData.fieldOne, TestData.fieldTwo), Collections.<Filter>emptyList());
		this.wa2Config = new WorkAreaConfig("WW2_F1B", 4, false, TestData.newFieldList(TestData.makeField("pqr", 1, 4)), Collections.<Filter>emptyList());
        this.configuration = new DefaultConfiguration();
		// Don't use the names of real functions. Previously, using function "1B" 
		// was causing the real 1B in GeosupportConfigIntegration test to fail 
		// when all tests were run from Maven;  basically, the 1B that was 
		// returned by the integration test was the mock one created in 
		// FunctionConfigTest. Issue did not occur within Eclipse or when 
		// test was run individually.
		this.oneWorkAreaFunction = new FunctionConfig("EG", this.wa1Config, null);
		this.twoWorkAreaFunction = new FunctionConfig("9B", this.wa1Config, this.wa2Config);
        this.twoWorkAreaDefaultArgsFunction = new FunctionConfig("12", this.wa1Config, this.wa2Config, this.configuration);
	}
	
	@After
	public void tearDown()
	{
		this.wa1Config = null;
		this.wa2Config = null;
		this.oneWorkAreaFunction = null;
		this.twoWorkAreaFunction = null;
        this.twoWorkAreaDefaultArgsFunction = null;
	}

	@Test
    public void testCreateUsesGivenDefaultConfiguration()
    {
        DefaultConfiguration config = new DefaultConfiguration();
        FunctionConfig functionConfig = new FunctionConfig("XX", this.wa1Config, this.wa2Config,config);
        Function function = functionConfig.createFunction(new GeoclientStub());
        assertSame(config, function.getConfiguration());
    }

    @Test
    public void testCreateNewDefaultConfigurationEvenIfFieldIsNull()
    {
        FunctionConfig functionConfig = new FunctionConfig("XX", this.wa1Config, this.wa2Config);
        Function function = functionConfig.createFunction(new GeoclientStub());
        assertNotNull(function.getConfiguration());
    }


	@Test
	public void testCreateOneWorkAreaFunction()
	{
		Function function = this.oneWorkAreaFunction.createFunction(new GeoclientStub());
		assertEquals("EG", function.getId());
	}

	@Test
	public void testCreateTwoWorkAreaFunction()
	{
		Function function = this.twoWorkAreaFunction.createFunction(new GeoclientStub());
		assertEquals("9B", function.getId());
	}

	@Test
	public void testOneWorkAreaConstructor()
	{
		assertSame(this.wa1Config, this.oneWorkAreaFunction.getWorkAreaOneConfig());
		assertNull(this.oneWorkAreaFunction.getWorkAreaTwoConfig());
        assertNull(this.oneWorkAreaFunction.getConfiguration());
        assertFalse(this.oneWorkAreaFunction.isTwoWorkAreaFunction());
        assertFalse(this.oneWorkAreaFunction.hasDefaultArguments());
	}

	@Test
	public void testTwoWorkAreaConstructor()
	{
		assertSame(this.wa1Config, this.twoWorkAreaFunction.getWorkAreaOneConfig());
		assertSame(this.wa2Config, this.twoWorkAreaFunction.getWorkAreaTwoConfig());
        assertNull(this.oneWorkAreaFunction.getConfiguration());
        assertTrue(this.twoWorkAreaFunction.isTwoWorkAreaFunction());
        assertFalse(this.twoWorkAreaFunction.hasDefaultArguments());
	}
	
    @Test
    public void testTwoWorkAreaArgumentsConstructor()
    {
        assertSame(this.wa1Config, this.twoWorkAreaDefaultArgsFunction.getWorkAreaOneConfig());
        assertSame(this.wa2Config, this.twoWorkAreaDefaultArgsFunction.getWorkAreaTwoConfig());
        assertSame(this.configuration, this.twoWorkAreaDefaultArgsFunction.getConfiguration());
        assertTrue(this.twoWorkAreaDefaultArgsFunction.isTwoWorkAreaFunction());
        assertTrue(this.twoWorkAreaDefaultArgsFunction.hasDefaultArguments());
    }
    
	@Test//(expected=DuplicateFieldNameException.class)
	public void testCreateTwoWorkAreaFunction_duplicateFields()
	{
		WorkAreaConfig duplicateFieldWa2 = new WorkAreaConfig("DUP", TestData.fieldDuplicateIdOfOne.getLength(), true, TestData.newFieldList(TestData.fieldDuplicateIdOfOne),Collections.<Filter>emptyList());
		FunctionConfig badFun = new FunctionConfig("DUP", this.wa1Config, duplicateFieldWa2);
		badFun.createFunction(new GeoclientStub());
	}

	
}
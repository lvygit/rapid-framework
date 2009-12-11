package cn.org.rapid_framework.pipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ResourceUtils;


public class PipelineTest {
	
	VelocityEngine engine = new VelocityEngine();
	Pipeline p = new Pipeline();
	
	@Before
	public void setUp() throws FileNotFoundException, Exception {
		File dir = ResourceUtils.getFile("classpath:fortest_velocity");

		Properties props = new Properties();
		props.setProperty("userdirective","cn.org.rapid_framework.velocity.directive.BlockDirective,cn.org.rapid_framework.velocity.directive.OverrideDirective,cn.org.rapid_framework.velocity.directive.ExtendsDirective");
		props.put(Velocity.FILE_RESOURCE_LOADER_PATH, dir.getAbsolutePath());
		engine.init(props);
	}
	
	@Test
	public void test()  throws Exception  {
		
		Pipeline p = new Pipeline();
		StringWriter sw = new StringWriter();
//		p.pipeline(engine,new String[] {"first.vm","second.vm","three.vm"}, new HashMap(), sw);
		p.pipeline(engine,"first.vm|second.vm | three.vm", new HashMap(), sw);
		System.out.println(sw.toString());
		String expected = "<html><head>second_override_content</head><body>first_override_content<three><second>first</second></three></body></html>";
		Assert.assertEquals(expected,sw.toString().replaceAll("\\s+", ""));
	}

}

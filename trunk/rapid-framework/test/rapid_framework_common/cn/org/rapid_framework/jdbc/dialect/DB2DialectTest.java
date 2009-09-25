package cn.org.rapid_framework.jdbc.dialect;

import junit.framework.Assert;

import org.junit.Test;


public class DB2DialectTest {
	
	Dialect dialect = new DB2Dialect();
	@Test
	public void getLimitString() {
		Assert.assertEquals("select * from ( select rownumber() over() as rownumber_, * from user ) as temp_ where rownumber_ <= 0", dialect.getLimitString("select * from user", 0, 0));
		Assert.assertEquals("select * from ( select rownumber() over() as rownumber_, * from user ) as temp_ where rownumber_ <= 12", dialect.getLimitString("select * from user", 0, 12));
		Assert.assertEquals("select * from ( select rownumber() over() as rownumber_, * from user ) as temp_ where rownumber_ between 12+1 and 12", dialect.getLimitString("select * from user", 12, 0));
		Assert.assertEquals("select * from ( select rownumber() over() as rownumber_, * from user ) as temp_ where rownumber_ between 12+1 and 46", dialect.getLimitString("select * from user", 12, 34));
	}
	
}

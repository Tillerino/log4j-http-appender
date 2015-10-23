package fr.bettinger.log4j;

import static org.junit.Assert.*;

import org.apache.log4j.Category;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.hamcrest.core.StringContains;
import org.junit.Test;

public class HttpLayoutTest {
	class SimpleCategory extends Category {
		SimpleCategory(String name) {
			super(name);
		}
	}

	@Test
	public void testBasicFunctionality() throws Exception {
		HttpLayout layout = new HttpLayout("?first=leftpad%crightpad&message=%m");

		Category category = new SimpleCategory("CATEGORYNAME");
		LoggingEvent event = new LoggingEvent(null, category, 0, null, "MESSAGE", null, null, null, null, null);
		String formatted = layout.format(event);
		assertEquals("?first=leftpadCATEGORYNAMErightpad&message=MESSAGE", formatted);
	}

	@Test
	public void testThrowable() throws Exception {
		HttpLayout layout = new HttpLayout("?exception=%throwable{2}");

		ThrowableInformation t = new ThrowableInformation(new Throwable());
		LoggingEvent event = new LoggingEvent(null, null, 0, null, null, null, t, null, null, null);
		String formatted = layout.format(event);
		assertThat(formatted, new StringContains(getClass().getName()));
	}
}

package org.projectfk.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;

public class JsonCreatorTestJava {

	static class stru {

		public stru() {
			System.out.println("constructor called! from: " + Thread.currentThread().getStackTrace()[2]);
		}

		public int id = 0;

		public String msg = "233";


		@JsonCreator
		public stru jsonEntry(
				@JsonProperty("id")
						int id,
				@JsonProperty("msg")
						String msg
		) {
			System.out.println("non-static  non-constructor JsonCreator called");
			return new stru();
		}

		@JsonCreator
		public static stru staticJsonEntry(
				@JsonProperty("id")
						int id,
				@JsonProperty("msg")
						String msg
		) {
			System.out.println("static  non-constructor JsonCreator called");
			return new stru();
		}


	}

	@Test
	public void test() throws Throwable {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter writer = objectMapper.writer();
		String jsonString = writer.writeValueAsString(new stru());
		stru stru = objectMapper.convertValue(objectMapper.readTree(jsonString), stru.class);
		System.out.println(stru);
	}

}

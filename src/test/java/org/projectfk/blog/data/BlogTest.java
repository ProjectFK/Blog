package org.projectfk.blog.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.DateUtil;
import org.junit.Test;

import java.io.IOException;

public class BlogTest {

	@Test
	public void JsonTest() throws IOException {

		Blog target = Blog.createNewPost(new User("name"), "content", "title");

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(DateUtil.newIsoDateFormat());
		String s = objectMapper.writeValueAsString(target);

		System.out.println(s);

		Blog blog = objectMapper.readValue(s, Blog.class);

		System.out.println(blog);

	}

}
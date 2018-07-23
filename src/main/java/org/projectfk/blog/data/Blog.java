package org.projectfk.blog.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;
import static org.projectfk.blog.UtilKt.fasterStringCompare;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "Blog")
public class Blog {

	//	JPA
	protected Blog() {
	}

	Blog(User author, String title, String content, LocalDateTime modifyDate, LocalDateTime createdDate) {
		requireNonNull(author);
		requireNonNull(title);
		requireNonNull(content);
		requireNonNull(modifyDate);
		requireNonNull(createdDate);
		assert modifyDate.isEqual(createdDate) || modifyDate.isAfter(createdDate)
				: "modify date should not be before createNewPost date";

		this.author = author;
		this.title = title;
		this.content = content;
		this.modifyDate = modifyDate;
		this.createdDate = createdDate;
	}

	@JsonProperty(access = READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id = 0;

	@JsonProperty
	@Column(nullable = false)
	private User author;

	@JsonProperty
	@Column(nullable = false)
	private String title;

	@JsonProperty
	@Column(nullable = false)
	private String content;

	@JsonProperty
	@Column(nullable = false)
	private LocalDateTime modifyDate = LocalDateTime.now();

	@JsonProperty(access = READ_ONLY)
	@Column(nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	public int getId() {
		return id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		String old = this.title;
		this.title = title;
		if (fasterStringCompare(old, title))
			refreshModifyDate();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		String old = this.content;
		this.content = content;
		if (fasterStringCompare(old, content))
			refreshModifyDate();
	}

	public LocalDateTime getModifyDate() {
		return modifyDate;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	private void refreshModifyDate() {
		modifyDate = LocalDateTime.now();
	}

	@JsonCreator
	public static Blog createNewPost(
			@JsonProperty("author")
					User author,
			@JsonProperty("content")
					String content,
			@JsonProperty("title")
					String title) {
		LocalDateTime now = now();
		return new Blog(author, title, content, now, now);
	}

	@Override
	public String toString() {
		return "Blog{" +
				"id=" + id +
				", author=" + author +
				", title='" + title + '\'' +
				", content='" + content + '\'' +
				", modifyDate=" + modifyDate +
				", createdDate=" + createdDate +
				'}';
	}
}

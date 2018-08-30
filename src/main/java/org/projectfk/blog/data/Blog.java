package org.projectfk.blog.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.projectfk.blog.common.NotFoundException;
import org.projectfk.blog.services.BlogService;

import javax.persistence.*;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity(name = "Blog")
public class Blog {

	//	JPA
	protected Blog() {
	}

	public Blog(
			@NotNull
					User author,
			@NotNull
					String title,
			@NotNull
					String content
	) {
		this.author = author;
		this.title = title;
		this.content = content;
	}

	@JsonProperty(access = READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id = 0;

	@JsonProperty(access = READ_ONLY)
	@JoinColumn(name = "user")
	@ManyToOne
	private User author;

	@JsonProperty
	@Column(nullable = false)
	private String title;

	@JsonProperty
	@Column(nullable = false)
	private String content;

	@JsonProperty(access = READ_ONLY)
	@Column(columnDefinition = "TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP")
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
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		String old = this.content;
		this.content = content;
	}

	public boolean alreadyLoaded() {
		return id == 0;
	}

	public LocalDateTime getModifyDate() {
		return modifyDate;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public static Blog jsonIDEntry(int id) throws NotFoundException {
		return BlogService
				.getService()
				.blogByID(id)
				.orElseThrow(() -> new NotFoundException("There's no blog with id:" + id + " in database"));
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
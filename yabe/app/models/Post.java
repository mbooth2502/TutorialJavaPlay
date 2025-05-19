package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Post extends Model {
 
    public String title;
    public Date postedAt;
    
    @Lob
    public String content;
    
    @ManyToOne
    public User author;

    // bi-directional relation
    // Chose Comment class to maintain relation - as comments belong to a post
    @OneToMany(mappedBy="post", cascade=CascadeType.ALL)
    public List<Comment> comments;

    // Link Tags Model to Post model
    @ManyToMany(cascade=CascadeType.PERSIST)
    public Set<Tag> tags;

    // TreeSet used to keep tag list in predictable (alphabetical) order for compareTo method
    public Post(User author, String title, String content) { 
        this.comments = new ArrayList<Comment>();
        this.tags = new TreeSet<Tag>();
        this.author = author;
        this.title = title;
        this.content = content;
        this.postedAt = new Date();
    }


    // Simplify adding comments
    public Post addComment(String author, String content) {
        Comment newComment = new Comment(this, author, content).save();
        this.comments.add(newComment);
        this.save();
        return this;
    }
    

    // Adding Pagination - easy navigation through posts
    public Post previous() {
        return Post.find("postedAt < ?1 order by postedAt desc", postedAt).first();
    }
    
    public Post next() {
        return Post.find("postedAt < ?1 order by postedAt desc", postedAt).first();
    }


    // Simplify Tag management
    public Post tagItWith(String name) {
        tags.add(Tag.findOrCreateByName(name));
        return this;
    }

    // Retrieve all posts with a specific tag
    public static List<Post> findTaggedWith(String... tags) {
        return Post.find(
            "select distinct p from Post p join p.tags as t " +
            "where t.name in (:tags) group by p.id, p.author, p.title, " +
            "p.content, p.postedAt having count(t.id) = :size"
        ).bind("tags", tags).bind("size", tags.length).fetch();
    }
    
}
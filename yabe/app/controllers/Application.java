package controllers;
 
import java.util.*;
 
import play.*;
import play.mvc.*;
import play.Play;
 
import models.*;
 
public class Application extends Controller {

    @Before
    static void addDefaults() {
        renderArgs.put("blogTitle", Play.configuration.getProperty("blog.title"));
        renderArgs.put("blogBaseline", Play.configuration.getProperty("blog.baseline"));
    }
 
    public static void index() {
        Post frontPost = Post.find("order by postedAt desc").first();
        List<Post> olderPosts = Post.find(
            "order by postedAt desc"
        ).from(1).fetch(10);
        render(frontPost, olderPosts);
    }


    // Method to display pos details page
    // id to be extracted from from query string, from URL path, or from request body 
    public static void show(Long id) {
        Post post = Post.findById(id);
        render(post);
    }


    // uses previous addComment() method
    public static void postComment(Long postId, String author, String content) {
        Post post = Post.findById(postId);
        post.addComment(author, content);
        show(postId);
    }
 
}
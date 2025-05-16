package controllers;
 
import java.util.*;
 
import play.*;
import play.mvc.*;
import play.Play;
import play.data.validation.*;
import play.libs.*; 
import play.cache.*;

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
    // validate fields aren't passed as empty using play validation mechanism - @Required
    // Use Flash for success message - flash passes messages from one action to next
    public static void postComment(Long postId, @Required String author, @Required String content) {
        Post post = Post.findById(postId);
        if(validation.hasErrors()) {
            render("Application/show.html", post);
        }
        post.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        show(postId);
    }


    // Captcha Image production
    // secret key stored in cache
    // to retrieve code later, need to generate a unique ID - added to each form as a hidden field
    public static void captcha(String id) {
        Images.Captcha captcha = Images.captcha();
        String code = captcha.getText("#3FA7D6");
        Cache.set(id, code, "10mn");
        renderBinary(captcha);
    }
 
}
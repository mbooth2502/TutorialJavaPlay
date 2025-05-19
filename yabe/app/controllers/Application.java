package controllers;
 
import java.util.*;

import org.omg.IOP.Codec;

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


    // Method to display post details page
    // id to be extracted from from query string, from URL path, or from request body 
    // unique ID created every time comment shown
    public static void show(Long id) {
        Post post = Post.findById(id);
        String randomID = java.util.UUID.randomUUID().toString();
        render(post, randomID);
    }


    // uses previous addComment() method
    // validate fields aren't passed as empty using play validation mechanism - @Required
    // Use Flash for success message - flash passes messages from one action to next

    // Validation of Captcha - Retrieve randomID, retrieve actual code from cache and compare against submitted one
    public static void postComment(
            Long postId, 
            @Required(message="Author is required") String author, 
            @Required(message="A message is required") String content, 
            @Required(message="Please type the code") String code, 
            String randomID) 
    {
        Post post = Post.findById(postId);
        validation.equals(
            code, Cache.get(randomID)
        ).message("Invalid code. Please type it again");
        if(validation.hasErrors()) {
            render("Application/show.html", post, randomID);
        }
        post.addComment(author, content);
        flash.success("Thanks for posting %s", author);
        Cache.delete(randomID);
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
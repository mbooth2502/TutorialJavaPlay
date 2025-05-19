package models;
 
import java.util.*;
import javax.persistence.*;
 
import play.db.jpa.*;
 
@Entity
public class Tag extends Model implements Comparable<Tag> {
 
    public String name;
    
    private Tag(String name) {
        this.name = name;
    }
    
    public String toString() {
        return name;
    }
    
    public int compareTo(Tag otherTag) {
        return name.compareTo(otherTag.name);
    }


    // We want close to lazy tag creation - delaying instantiation until actually needed
    // Will always get tags using this 'Factory method' - create objects without specifying exact class 
    public static Tag findOrCreateByName(String name) {
        Tag tag = Tag.find("byName", name).first();
        if(tag == null) {
            tag = new Tag(name);
        }
        return tag;
    }


    // Hibernate feature used to return a custom object from JPA query 
    // Returns a list of maps with two attributes: name for the tag, and pound for the tag count
    public static List<Map> getCloud() {
        List<Map> result = Tag.find(
            "select new map(t.name as tag, count(p.id) as pound) " +
            "from Post p join p.tags as t group by t.name order by t.name"
        ).fetch();
        return result;
    }
 
}
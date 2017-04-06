package cb.attributes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class Contact {
    public String prefix;
    public String first_name;
    public String middle_name;
    public String last_name;
    public String full_name;
    public String suffix;
    public String nickname;
    public String company;
    public String title;

    //set empty defaults
    public Set<String> emails = new TreeSet<>();
    public Collection<Phone> phone_numbers = new ArrayList<>();
    public Collection<Address> addresses = new ArrayList<>();
    public Set<String> ims = new TreeSet<>();  // This is no longer included in the response so don't update.
                             //  Keep for posterity
    public Collection<SocialProfile> social_profiles = new ArrayList<>();
    public Set<String> urls = new TreeSet<>();
}
package cb;

import static cb.AttributeUtils.cleanPhoneNumber;
import static cb.AttributeUtils.getValueOrDefault;
import cb.attributes.Address;
import cb.attributes.Contact;
import cb.attributes.Phone;
import cb.attributes.SocialProfile;
import java.util.Collection;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.util.Pair;

public class Reducers {
    public static BinaryOperator<Phone> mergePhone = (src, dst) -> {
        Phone phone = new Phone();
        phone.phone_number = getValueOrDefault(dst.phone_number, src.phone_number);
        phone.type = getValueOrDefault(dst.type, src.type);

        return phone;
    };

    public static Function<Phone, String> comparePhone = f -> cleanPhoneNumber(f.phone_number);

    // Assume users only have one social network profile.
    //  Update the url if it changes.
    public static BinaryOperator<SocialProfile> mergeSocial = (src, dst) -> {
        SocialProfile social = new SocialProfile();
        social.url = getValueOrDefault(dst.url, src.url);
        social.network = getValueOrDefault(dst.network, src.network);

        return social;
    };

    public static Function<SocialProfile, String> compareSocial = f -> f.network;

    public static BinaryOperator<Address> mergeAddress = (src, dst) -> {
        Address social = new Address();
        social.city = getValueOrDefault(dst.city, src.city);
        social.state = getValueOrDefault(dst.state, src.state);
        social.street1 = getValueOrDefault(dst.street1, src.street1);
        social.street2 = getValueOrDefault(dst.street2, src.street2);
        social.zip = getValueOrDefault(dst.zip, src.zip);
        social.country = getValueOrDefault(dst.country, src.country);

        return social;
    };

    //Possible overlap but unlikely
    public static Function<Address, String> compareAddress = f -> "city" + f.city + "state" + f.state;

    public static BinaryOperator<Contact> mergeContact = (src, dst) -> {
        //To avoid using reflection, just merge manually
        // There will be some maintenance overhead for adding properties..
        // Evaluate if beanutils copyproperties is an appropriate tool
        // The other way would be to create a bucket of attribute and merging would be easier
        //  but the tradeoff would include moving away from object oriented styles.
        Contact contact = new Contact();
        contact.prefix = getValueOrDefault(dst.prefix, src.prefix);
        contact.first_name = getValueOrDefault(dst.first_name, src.first_name);
        contact.middle_name = getValueOrDefault(dst.middle_name, src.middle_name);
        contact.last_name = getValueOrDefault(dst.last_name, src.last_name);
        contact.full_name = getValueOrDefault(dst.full_name, src.full_name);
        contact.suffix = getValueOrDefault(dst.suffix, src.suffix);
        contact.nickname = getValueOrDefault(dst.nickname, src.nickname);
        contact.company = getValueOrDefault(dst.company, src.company);
        contact.title = getValueOrDefault(dst.title, src.title);


        contact.phone_numbers = Stream.concat(src.phone_numbers.stream(), dst.phone_numbers.stream())
                .collect(Collectors.toMap(Reducers.comparePhone, f -> f, Reducers.mergePhone)).values();

        // I am assuming the response strips test+something@example.com and they are
        //  treated different in a user context.
        contact.emails.addAll(src.emails);
        contact.emails.addAll(dst.emails);

        // merge just on city state. It would be not optimal but better than nothing.
        contact.addresses = Stream.concat(src.addresses.stream(), dst.addresses.stream())
                .collect(Collectors.toMap(Reducers.compareAddress, f -> f, Reducers.mergeAddress)).values();

        contact.social_profiles = Stream.concat(src.social_profiles.stream(), dst.social_profiles.stream())
                .collect(Collectors.toMap(Reducers.compareSocial, f -> f, Reducers.mergeSocial)).values();

        contact.urls.addAll(src.urls);
        contact.urls.addAll(dst.urls);
        return contact;
    };

    // Merge contact by pair
    public static Function<Pair<String, Contact>, String> comparePair = Pair::getKey;

    public static Collection<Contact> fastContactMerge(List<Contact> originalList, List<Contact> updatedList) {
        Collection<Contact> mergedByContact = Stream.concat(originalList.stream(), updatedList.stream())
                .map(c -> c.phone_numbers.stream()
                        .map(ph -> new Pair<>("fname:" + c.first_name + "lname:" + c.last_name + "ph:" + ph.phone_number, c))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(comparePair, Pair::getValue, mergeContact)).values();

        return mergedByContact.stream()
                .map(c -> c.emails.stream()
                        .map(e -> new Pair<>(e, c))
                        .collect(Collectors.toList()))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(comparePair, Pair::getValue, mergeContact)).values();
    }
}

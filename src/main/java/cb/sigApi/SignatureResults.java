package cb.sigApi;

import cb.attributes.Contact;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SignatureResults {
    public List<Contact> contacts;
}

package cb;
import static cb.Reducers.fastContactMerge;
import cb.attributes.Contact;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import cb.sigApi.SignatureRequest;
import cb.sigApi.SignatureResponse;

public class MainTest {
    private Main main = new Main();

    @Test
    public void testContactResolution() throws IOException {
        System.out.println(main);
        List<Contact> contacts = main.resolveCurrentContacts();
        assertNotNull(contacts);
        assertEquals(8, contacts.size());
    }

    @Test
    public void testEmailsToPost() throws Exception {
        SignatureRequest messages = main.emailsToPost();
        assertNotNull(messages);
        assertEquals(5, messages.messages.size());
    }

    @Test
    public void assureDeserializeResponse() throws IOException {
        SignatureResponse signatureResponse = getCannedSignatureResponse();

        assertNotNull(signatureResponse);
        assertEquals(8, signatureResponse.contact_count);
        assertEquals(5, signatureResponse.results.size());
        long counts = signatureResponse.results.stream()
                .mapToInt(e->e.contacts.size())
                .sum();
        assertEquals(8, counts);
    }

    private SignatureResponse getCannedSignatureResponse() throws IOException {
        byte[] response = Files.readAllBytes(Paths.get("testResponseSerialize.json"));
        return Main.createObjectMapper().readValue(response, SignatureResponse.class);
    }


    public static Function<Contact, String> compareByFirstNameContact = f -> f.first_name;
    @Test
    public void testMergeContacts() throws IOException {
        List<Contact> contact = main.resolveCurrentContacts();
        List<Contact> updated = getCannedSignatureResponse().results.stream()
                .map(e->e.contacts)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Collection<Contact> contacts = Stream.concat(contact.stream(), updated.stream())
                .collect(Collectors.toMap(compareByFirstNameContact, f -> f, Reducers.mergeContact)).values();

        //count the number expected
        long expected = Stream.concat(contact.stream(), updated.stream()).map(m->m.first_name)
                .distinct()
                .count();
        assertEquals(expected, contacts.size());
        assertEquals(8, contacts.size());
    }
    @Test
    public void testFastMergeContacts() throws IOException {
        List<Contact> contact = main.resolveCurrentContacts();
        List<Contact> updated = getCannedSignatureResponse().results.stream()
                .map(e->e.contacts)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Collection<Contact> mergedContacts = fastContactMerge(contact, updated);

        System.out.println(Main.objectMapper.writeValueAsString(mergedContacts));
    }
}
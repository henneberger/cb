package cb;

import cb.attributes.Contact;
import cb.sigApi.SignatureRequest;
import cb.sigApi.SignatureResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    SignatureClient signatureClient;
    public static final ObjectMapper objectMapper = createObjectMapper();

    Main() {
        signatureClient = new SignatureClient("YzU3ZDVmMWYtMGY0Zi00MDA2LWE5NmMtYmFkNGQ5YTEwOWVi");
    }

    public static void main(String args[]) throws Exception {
        new Main().run();
    }

    private void run() throws Exception {
        List<Contact> contacts = resolveCurrentContacts();
        List<Contact> responseContacts = parseContacts(signatureClient.resolveSignatures(emailsToPost()));

        Collection<Contact> mergedContacts = Reducers.fastContactMerge(contacts, responseContacts);

        writeUpdatedContacts(mergedContacts);
    }

    private void writeUpdatedContacts(final Collection<Contact> contacts) throws IOException {
        String json = objectMapper.writeValueAsString(contacts);
        Files.write(Paths.get("output_contacts.json"), json.getBytes());
    }

    private List<Contact> parseContacts(SignatureResponse response) {
        return response.results.stream()
                .map(e->e.contacts)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    SignatureRequest emailsToPost() throws IOException {
        byte[] contacts = Files.readAllBytes(Paths.get("email_bodies_to_post.json"));
        return objectMapper.readValue(contacts, SignatureRequest.class);
    }

    List<Contact> resolveCurrentContacts() throws IOException {

        byte[] contacts = Files.readAllBytes(Paths.get("contact_list_to_merge_with.json"));
        return objectMapper.readValue(contacts, new TypeReference<List<Contact>>(){});
    }

    static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        //Add a module to do LocalDateTime correctly
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        return objectMapper;
    }
}

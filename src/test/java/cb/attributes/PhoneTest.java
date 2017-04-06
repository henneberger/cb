package cb.attributes;

import cb.Reducers;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static junit.framework.Assert.assertEquals;
import org.junit.Test;

public class PhoneTest {
    @Test
    public void merge() throws Exception {
        Phone phone1 = createPhone("1234", "home");
        Phone phone2 = createPhone("+1,123", "home");
        Phone up1 = createPhone("1234", "office");
        Phone up2 = createPhone("12345", "home");
        Phone up3 = createPhone("1", "office");
        List<Phone> phoneSet1 = Arrays.asList(phone1, phone2);
        List<Phone> updated = Arrays.asList(up1, up2, up3);
        Collection<Phone> p = Stream.concat(phoneSet1.stream(), updated.stream())
                .collect(Collectors.toMap(Reducers.comparePhone, f -> f, Reducers.mergePhone)).values();

        String overlapType = p.stream().filter(ph->ph.phone_number.equals("1234"))
                .map(ph->ph.type)
                .findFirst()
                .get();
        assertEquals("office", overlapType);
    }

    private Phone createPhone(String num, String type) {
        Phone phone = new Phone();
        phone.phone_number = num;
        phone.type = type;
        return phone;
    }

    @Test
    public void testUSPhone() {
        Phone phone1 = createPhone("+15558675309", "home");
        Phone phone2 = createPhone("5558675309", "office");
        List<Phone> phone = Arrays.asList(phone1, phone2);
        Collection<Phone> p = phone.stream()
                .collect(Collectors.toMap(Reducers.comparePhone, f -> f, Reducers.mergePhone)).values();
        assertEquals(1, p.size());
        assertEquals("office", p.iterator().next().type);
        assertEquals("5558675309", p.iterator().next().phone_number);
    }
}
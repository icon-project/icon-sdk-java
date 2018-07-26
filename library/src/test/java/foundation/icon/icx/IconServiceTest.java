package foundation.icon.icx;

import org.junit.Test;
import static org.junit.Assert.*;

public class IconServiceTest {
    @Test public void testIconServiceInit() {
        IconService iconService = new IconService(new Provider() {
            @Override
            public <T> Call<T> request(Request request) {
                return null;
            }
        });
        assertNotNull(iconService);
    }
}

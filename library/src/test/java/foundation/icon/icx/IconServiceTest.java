package foundation.icon.icx;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class IconServiceTest {
    @Test
    void testIconServiceInit() {
        IconService iconService = new IconService(new Provider() {
            @Override
            public <T> Call<T> request(Request request) {
                return null;
            }
        });
        assertNotNull(iconService);
    }
}

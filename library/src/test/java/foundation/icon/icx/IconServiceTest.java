package foundation.icon.icx;

import org.junit.jupiter.api.Test;

import foundation.icon.icx.transport.jsonrpc.Request;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class IconServiceTest {
    @Test
    void testIconServiceInit() {
        IconService iconService = new IconService(new Provider() {
            @Override
            public <I, O> Call<O> request(Request<I> request, Class<O> responseType) {
                return null;
            }
        });
        assertNotNull(iconService);
    }
}

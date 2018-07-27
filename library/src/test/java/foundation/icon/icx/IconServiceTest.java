package foundation.icon.icx;

import org.junit.Test;
import static org.junit.Assert.*;

public class IconServiceTest {
    @Test public void testIconServiceInit() {
        IconService iconService = new IconService();
        assertNotNull(iconService);
    }
}

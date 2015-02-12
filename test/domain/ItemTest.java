package domain;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class ItemTest {

    private Item book;

    public ItemTest() {
    }

    @Before
    public void setUp() {
        book = new Book("theme", "category", "location", Damage.NO_DAMAGE, "title", "author", "publisher");
    }

    @Test
    public void testGetTranslate() {
        String translation = book.getDamage().getTranslation();
        assertEquals(true, translation.equals("Geen schade"));
    }

}

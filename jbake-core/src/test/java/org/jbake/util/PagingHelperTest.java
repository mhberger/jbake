package org.jbake.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PagingHelperTest {

    @Test
    public void getNumberOfPages() throws Exception {
        int expected = 3;
        int total = 5;
        int perPage = 2;

        PagingHelper helper = new PagingHelper(total,perPage);

        assertEquals( expected, helper.getNumberOfPages() );
    }

    @Test
    public void shouldReturnRootIndexPage() throws Exception {
        PagingHelper helper = new PagingHelper(5,2);

        String previousFileName = helper.getPreviousFileName(2);

        assertEquals("", previousFileName );
    }

    @Test
    public void shouldReturnPreviousFileName() throws Exception {
        PagingHelper helper = new PagingHelper(5,2);

        String previousFileName = helper.getPreviousFileName(3);

        assertEquals("2/", previousFileName );
    }

    @Test
    public void shouldReturnNullIfNoPreviousPageAvailable() throws Exception {
        PagingHelper helper = new PagingHelper(5,2);

        String previousFileName = helper.getPreviousFileName(1);

        Assertions.assertNull( previousFileName );
    }

    @Test
    public void shouldReturnNullIfNextPageNotAvailable() throws Exception {
        PagingHelper helper = new PagingHelper(5,2);

        String nextFileName = helper.getNextFileName(3);

        Assertions.assertNull( nextFileName );
    }

    @Test
    public void shouldReturnNextFileName() throws Exception {
        PagingHelper helper = new PagingHelper(5,2);

        String nextFileName = helper.getNextFileName(2);

        assertEquals("3/",  nextFileName );
    }
}

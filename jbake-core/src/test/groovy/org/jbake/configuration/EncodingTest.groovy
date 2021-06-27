package org.jbake.configuration

import org.jbake.TestUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EncodingTest {

    @Test
    void checkLastLineInFileContainsLatinEncodedString() {
        String encodedString = "site.about=Latin1 encoded file äöü"
        String f = "${TestUtils.getTestResourcesAsSourceFolder("/fixtureLatin1")}/jbake.properties"

        List<String> content = new File(f).getText("ISO-8859-1").split(/\n/)
        Assertions.assertEquals(encodedString, content[-1])

        List<String> contentUtf8 = new File(f).getText("UTF-8").split(/\n/)
        Assertions.assertNotEquals(encodedString, contentUtf8[-1])
    }
}

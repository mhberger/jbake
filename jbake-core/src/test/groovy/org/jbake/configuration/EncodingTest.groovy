package org.jbake.configuration

import org.jbake.TestUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EncodingTest {

    @Test
    void testEncoding() {
        String f = "${TestUtils.getTestResourcesAsSourceFolder("/fixtureLatin1")}/jbake.properties"
        List<String> content = new File(f).getText("ISO-8859-1").split(/\n/)

        Assertions.assertTrue(content[-1].contains("Latin1 encoded file äöü"))
    }
}

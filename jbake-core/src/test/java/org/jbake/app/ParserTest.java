package org.jbake.app;

import org.jbake.TestUtils;
import org.jbake.app.configuration.ConfigUtil;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.jbake.model.DocumentModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbake.app.configuration.PropertyList.TAG_SANITIZE;

public class ParserTest {

    @TempDir
    public static Path folder;

    private DefaultJBakeConfiguration config;
    private Parser parser;
    private File rootPath;

    private File validHTMLFile;
    private File invalidHTMLFile;
    private File validMarkdownFileWithCustomHeader;
    private File validMarkdownFileWithDefaultStatus;
    private File validMarkdownFileWithDefaultTypeAndStatus;
    private File invalidMarkdownFileWithoutDefaultStatus;
    private File invalidMDFile;
    private File invalidExtensionFile;
    private File validHTMLWithJSONFile;
    private File validAsciiDocWithJSONFile;
    private File validAsciiDocWithADHeaderJSONFile;
    private File validaAsciidocWithUnsanitizedHeader;

    private String validHeader = "title=This is a Title = This is a valid Title\nstatus=draft\ntype=post\ndate=2013-09-02\n~~~~~~";
    private String invalidHeader = "title=This is a Title\n~~~~~~";
    private String sampleJsonData = "{\"numberValue\": 42, \"stringValue\": \"Answer to live, the universe and everything\", \"nullValue\": null, \"arrayValue\": [1, 2], \"objectValue\": {\"val1\": 1, \"val2\": 2}}";

    private String unsanitizedKeys = " title= Title \n status= draft \n   type= post   \ndate=2020-02-30\ncustom=custom without bom's\ntags= jbake, java    , tag with space   \n~~~~~~";

    private String customHeaderSeparator;


    @BeforeEach
    public void createSampleFile() throws Exception {
        rootPath = TestUtils.getTestResourcesAsSourceFolder();
        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(rootPath);
        parser = new Parser(config);

        validHTMLFile = folder.resolve("valid.html").toFile();
        PrintWriter out = new PrintWriter(validHTMLFile);
        out.println(validHeader);
        out.println("<p>This is a test.</p>");
        out.close();

        invalidHTMLFile = folder.resolve("invalid.html").toFile();
        out = new PrintWriter(invalidHTMLFile);
        out.println(invalidHeader);
        out.close();

        validMarkdownFileWithCustomHeader = folder.resolve("validMdCustomHeader.md").toFile();

        customHeaderSeparator = "---------------------------------------";
        out = new PrintWriter(validMarkdownFileWithCustomHeader);
        out.println("title=Custom Header separator");
        out.println("type=post");
        out.println("status=draft");
        out.println(customHeaderSeparator);
        out.println("# Hello Markdown!");
        out.println("");
        out.println("A paragraph");
        out.println("");
        out.println("* And");
        out.println("* A");
        out.println("* List");
        out.close();

        validMarkdownFileWithDefaultStatus = folder.resolve("validMdDefaultStatus.md").toFile();

        out = new PrintWriter(validMarkdownFileWithDefaultStatus);
        out.println("title=Custom Header separator");
        out.println("type=post");
        out.println(config.getHeaderSeparator());
        out.println("# Hello Markdown!");
        out.println("");
        out.println("A paragraph");
        out.println("");
        out.println("* And");
        out.println("* A");
        out.println("* List");
        out.close();

        validMarkdownFileWithDefaultTypeAndStatus = folder.resolve("validMdDefaultTypeAndStatus.md").toFile();

        out = new PrintWriter(validMarkdownFileWithDefaultTypeAndStatus);
        out.println("title=Custom Header separator");
        out.println("cached=false");
        out.println(config.getHeaderSeparator());
        out.println("# Hello Markdown!");
        out.println("");
        out.println("A paragraph");
        out.println("");
        out.println("* And");
        out.println("* A");
        out.println("* List");
        out.close();

        invalidMarkdownFileWithoutDefaultStatus = folder.resolve("invalidMdWithoutDefaultStatus.md").toFile();

        out = new PrintWriter(invalidMarkdownFileWithoutDefaultStatus);
        out.println("title=Custom Header separator");
        out.println("type=page");
        out.println(config.getHeaderSeparator());
        out.println("# Hello Markdown!");
        out.println("");
        out.println("A paragraph");
        out.println("");
        out.println("* And");
        out.println("* A");
        out.println("* List");
        out.close();

        invalidMDFile = folder.resolve("invalidMd.md").toFile();

        out = new PrintWriter(invalidMDFile);
        out.println(invalidHeader);
        out.println("# Hello Markdown!");
        out.println("");
        out.println("A paragraph");
        out.println("");
        out.println("* And");
        out.println("* A");
        out.println("* List");
        out.close();

        invalidExtensionFile = folder.resolve("invalid.invalid").toFile();
        out = new PrintWriter(invalidExtensionFile);
        out.println("invalid content");
        out.close();

        validHTMLWithJSONFile = folder.resolve("validHTMLWithJSONFile.html").toFile();
        out = new PrintWriter(validHTMLWithJSONFile);
        out.println("title=This is a Title = This is a valid Title");
        out.println("status=draft");
        out.println("type=post");
        out.println("date=2013-09-02");
        out.print("jsondata=");
        out.println(sampleJsonData);
        out.println("~~~~~~");
        out.println("Sample Body");
        out.close();

        validAsciiDocWithJSONFile = folder.resolve("validAsciiDocWithJSONFile.ad").toFile();
        out = new PrintWriter(validAsciiDocWithJSONFile);
        out.println("title=This is a Title = This is a valid Title");
        out.println("status=draft");
        out.println("type=post");
        out.println("date=2013-09-02");
        out.print("jsondata=");
        out.println(sampleJsonData);
        out.println("~~~~~~");
        out.println("= Hello, AsciiDoc!");
        out.println("Test User <user@test.org>");
        out.println("");
        out.println("JBake now supports AsciiDoc.");
        out.close();

        validAsciiDocWithADHeaderJSONFile = folder.resolve("validAsciiDocWithADHeaderJSONFile.ad").toFile();
        out = new PrintWriter(validAsciiDocWithADHeaderJSONFile);
        out.println("= Hello: AsciiDoc!");
        out.println("Test User <user@test.org>");
        out.println("2013-09-02");
        out.println(":jbake-status: published");
        out.println(":jbake-type: page");
        out.print(":jbake-jsondata: ");
        out.println(sampleJsonData);
        out.println("");
        out.println("JBake now supports AsciiDoc.");
        out.close();

        validaAsciidocWithUnsanitizedHeader = folder.resolve("validAsciidocWithUnsanitizedHeader.adoc").toFile();
        out = new PrintWriter(validaAsciidocWithUnsanitizedHeader, "UTF-8");
        // Simulating a \uFEFF Byte order Marker in utf-8
        out.print("\uFEFF");
        out.println(unsanitizedKeys);
        out.println("= Hello: AsciiDoc!");
        out.println("Test User <user@test.org>");
        out.println("2013-09-02");
        out.println(":jbake-status: published");
        out.println(":jbake-type: page");
        out.print(":jbake-jsondata: ");
        out.println(sampleJsonData);
        out.println("");
        out.println("JBake now supports AsciiDoc.");
        out.close();

    }

    @Test
    public void parseValidHTMLFile() {
        DocumentModel documentModel = parser.processFile(validHTMLFile);
        Assertions.assertNotNull(documentModel);
        Assertions.assertEquals("draft", documentModel.getStatus());
        Assertions.assertEquals("post", documentModel.getType());
        Assertions.assertEquals("This is a Title = This is a valid Title", documentModel.getTitle());
        Assertions.assertNotNull(documentModel.getDate());
        Calendar cal = Calendar.getInstance();
        cal.setTime(documentModel.getDate());
        Assertions.assertEquals(8, cal.get(Calendar.MONTH));
        Assertions.assertEquals(2, cal.get(Calendar.DAY_OF_MONTH));
        Assertions.assertEquals(2013, cal.get(Calendar.YEAR));

    }

    @Test
    public void parseInvalidHTMLFile() {
        DocumentModel documentModel = parser.processFile(invalidHTMLFile);
        Assertions.assertNull(documentModel);
    }

    @Test
    public void parseInvalidExtension(){
        DocumentModel documentModel = parser.processFile(invalidExtensionFile);
        Assertions.assertNull(documentModel);
    }


    @Test
    public void parseMarkdownFileWithCustomHeaderSeparator() {
        config.setHeaderSeparator(customHeaderSeparator);

        DocumentModel documentModel = parser.processFile(validMarkdownFileWithCustomHeader);
        Assertions.assertNotNull(documentModel);
        Assertions.assertEquals("draft", documentModel.getStatus());
        Assertions.assertEquals("post", documentModel.getType());
        assertThat(documentModel.getBody())
                .contains("<p>A paragraph</p>");

    }

    @Test
    public void parseMarkdownFileWithDefaultStatus() {
        config.setDefaultStatus("published");

        DocumentModel documentModel = parser.processFile(validMarkdownFileWithDefaultStatus);
        Assertions.assertNotNull(documentModel);
        Assertions.assertEquals("published", documentModel.getStatus());
        Assertions.assertEquals("post", documentModel.getType());
        Assertions.assertEquals(true, documentModel.getCached());
    }

    @Test
    public void parseMarkdownFileWithDefaultTypeAndStatus() {
        config.setDefaultStatus("published");
        config.setDefaultType("page");

        DocumentModel documentModel = parser.processFile(validMarkdownFileWithDefaultTypeAndStatus);
        Assertions.assertNotNull(documentModel);
        Assertions.assertEquals("published", documentModel.getStatus());
        Assertions.assertEquals("page", documentModel.getType());
    }

    @Test
    public void parseMarkdownFileWithDisabledCache() {
        config.setDefaultStatus("published");
        config.setDefaultType("page");

        DocumentModel documentModel = parser.processFile(validMarkdownFileWithDefaultTypeAndStatus);
        Assertions.assertEquals(false, documentModel.getCached());
    }

    @Test
    public void parseInvalidMarkdownFileWithoutDefaultStatus() {
        config.setDefaultStatus("");
        config.setDefaultType("page");

        DocumentModel documentModel = parser.processFile(invalidMarkdownFileWithoutDefaultStatus);
        Assertions.assertNull(documentModel);
    }

    @Test
    public void parseInvalidMarkdownFile() {
        DocumentModel documentModel = parser.processFile(invalidMDFile);
        Assertions.assertNull(documentModel);
    }

    @Test
    public void sanitizeKeysAndValues() {
        DocumentModel map = parser.processFile(validaAsciidocWithUnsanitizedHeader);

        assertThat(map.getStatus()).isEqualTo("draft");
        assertThat(map.getTitle()).isEqualTo("Title");
        assertThat(map.getType()).isEqualTo("post");
        assertThat(map.get("custom")).isEqualTo("custom without bom's");
        assertThat(map.getTags()).isEqualTo(Arrays.asList("jbake", "java", "tag with space").toArray());
    }

    @Test
    public void sanitizeTags() {
        config.setProperty(TAG_SANITIZE.getKey(), true);
        DocumentModel map = parser.processFile(validaAsciidocWithUnsanitizedHeader);

        assertThat(map.getTags()).isEqualTo(Arrays.asList("jbake", "java", "tag-with-space").toArray());
    }


    @Test
    public void parseValidHTMLWithJSONFile() {
        DocumentModel documentModel = parser.processFile(validHTMLWithJSONFile);
        assertJSONExtracted(documentModel.get("jsondata"));
    }

    @Test
    public void parseValidAsciiDocWithJSONFile() {
        DocumentModel documentModel = parser.processFile(validAsciiDocWithJSONFile);
        assertJSONExtracted(documentModel.get("jsondata"));
    }

    @Test
    public void testValidAsciiDocWithADHeaderJSONFile() {
        DocumentModel documentModel = parser.processFile(validAsciiDocWithADHeaderJSONFile);
        assertJSONExtracted(documentModel.get("jsondata"));
    }

    private void assertJSONExtracted(Object jsonDataEntry) {
        assertThat(jsonDataEntry).isInstanceOf(JSONObject.class);
        JSONObject jsonData = (JSONObject) jsonDataEntry;
        assertThat(jsonData.containsKey("numberValue")).isTrue();
        assertThat(jsonData.get("numberValue")).isInstanceOf(Number.class);
        assertThat(((Number)jsonData.get("numberValue")).intValue()).isEqualTo(42);
        assertThat(jsonData.containsKey("stringValue")).isTrue();
        assertThat(jsonData.get("stringValue")).isInstanceOf(String.class);
        assertThat((String)jsonData.get("stringValue")).isEqualTo("Answer to live, the universe and everything");
        assertThat(jsonData.containsKey("nullValue")).isTrue();
        assertThat(jsonData.get("nullValue")).isNull();
        assertThat(jsonData.containsKey("arrayValue")).isTrue();
        assertThat(jsonData.get("arrayValue")).isInstanceOf(JSONArray.class);
        assertThat((JSONArray)jsonData.get("arrayValue")).contains(1L,2L);
        assertThat(jsonData.containsKey("objectValue")).isTrue();
        assertThat(jsonData.get("objectValue")).isInstanceOf(JSONObject.class);
        assertThat((JSONObject)jsonData.get("objectValue")).contains(new SimpleEntry("val1", 1L), new SimpleEntry("val2", 2L));
    }
}

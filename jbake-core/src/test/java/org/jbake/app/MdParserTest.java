package org.jbake.app;

import org.jbake.TestUtils;
import org.jbake.app.configuration.ConfigUtil;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.jbake.model.DocumentModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests basic Markdown syntax and the extensions supported by the Markdown
 * processor (Pegdown).
 *
 * @author Jonathan Bullock <jonbullock@gmail.com>
 * @author Kevin S. Clarke <ksclarke@gmail.com>
 */
public class MdParserTest {

    @TempDir
    public static Path folder;

    public DefaultJBakeConfiguration config;

    private File validMdFileBasic;

    private File validMdFileBasicWithCustomHeader;

    private File invalidMdFileBasic;

    private File mdFileHardWraps;

    private File mdFileAbbreviations;

    private File mdFileAutolinks;

    private File mdFileDefinitions;

    private File mdFileFencedCodeBlocks;

    private File mdFileQuotes;

    private File mdFileSmarts;

    private File mdFileSmartypants;

    private File mdFileSuppressAllHTML;

    private File mdFileSuppressHTMLBlocks;

    private File mdFileSuppressInlineHTML;

    private File mdFileTables;

    private File mdFileWikilinks;

    private File mdFileAtxheaderspace;

    private File mdFileForcelistitempara;

    private File mdFileRelaxedhrules;

    private File mdTasklistitems;

    private File mdExtanchorlinks;

    private String validHeader = "title=Title\nstatus=draft\ntype=post\n~~~~~~";

    private String validHeaderWithCustomHeader = "title=Title\nstatus=draft\ncategory=news\ntype=post\nid=232\nsummary=This is a custom summary\nog={\"og:type\" : \"article\"}\n~~~~~~";

    private String invalidHeader = "title=Title\n~~~~~~";

    @BeforeEach
    public void createSampleFile() throws Exception {

        File configFile = TestUtils.getTestResourcesAsSourceFolder();
        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(configFile);

        validMdFileBasic = folder.resolve("validBasic.md").toFile();
        PrintWriter out = new PrintWriter(validMdFileBasic);
        out.println(validHeader);
        out.println("# This is a test");
        out.close();

        validMdFileBasicWithCustomHeader = folder.resolve("validBasicCustomHeader.md").toFile();
        out = new PrintWriter(validMdFileBasicWithCustomHeader);
        out.println(validHeaderWithCustomHeader);
        out.println("# This is a file with a custom header");
        out.close();


        invalidMdFileBasic = folder.resolve("invalidBasic.md").toFile();
        out = new PrintWriter(invalidMdFileBasic);
        out.println(invalidHeader);
        out.println("# This is a test");
        out.close();

        mdFileHardWraps = folder.resolve("hardWraps.md").toFile();
        out = new PrintWriter(mdFileHardWraps);
        out.println(validHeader);
        out.println("First line");
        out.println("Second line");
        out.close();

        mdFileAbbreviations = folder.resolve("abbreviations.md").toFile();
        out = new PrintWriter(mdFileAbbreviations);
        out.println(validHeader);
        out.println("*[HTML]: Hyper Text Markup Language");
        out.println("HTML");
        out.close();

        mdFileAutolinks = folder.resolve("autolinks.md").toFile();
        out = new PrintWriter(mdFileAutolinks);
        out.println(validHeader);
        out.println("http://github.com");
        out.close();

        mdFileDefinitions = folder.resolve("definitions.md").toFile();
        out = new PrintWriter(mdFileDefinitions);
        out.println(validHeader);
        out.println("Apple");
        out.println(":   Pomaceous fruit");
        out.close();

        mdFileFencedCodeBlocks = folder.resolve("fencedCodeBlocks.md").toFile();
        out = new PrintWriter(mdFileFencedCodeBlocks);
        out.println(validHeader);
        out.println("```");
        out.println("function test() {");
        out.println("  console.log(\"!\");");
        out.println("}");
        out.println("```");
        out.close();

        mdFileQuotes = folder.resolve("quotes.md").toFile();
        out = new PrintWriter(mdFileQuotes);
        out.println(validHeader);
        out.println("\"quotes\"");
        out.close();

        mdFileSmarts = folder.resolve("smarts.md").toFile();
        out = new PrintWriter(mdFileSmarts);
        out.println(validHeader);
        out.println("...");
        out.close();

        mdFileSmartypants = folder.resolve("smartypants.md").toFile();
        out = new PrintWriter(mdFileSmartypants);
        out.println(validHeader);
        out.println("\"...\"");
        out.close();

        mdFileSuppressAllHTML = folder.resolve("suppressAllHTML.md").toFile();
        out = new PrintWriter(mdFileSuppressAllHTML);
        out.println(validHeader);
        out.println("<div>!</div><em>!</em>");
        out.close();

        mdFileSuppressHTMLBlocks = folder.resolve("suppressHTMLBlocks.md").toFile();
        out = new PrintWriter(mdFileSuppressHTMLBlocks);
        out.println(validHeader);
        out.println("<div>!</div><em>!</em>");
        out.close();

        mdFileSuppressInlineHTML = folder.resolve("suppressInlineHTML.md").toFile();
        out = new PrintWriter(mdFileSuppressInlineHTML);
        out.println(validHeader);
        out.println("This is the first paragraph. <span> with </span> inline html");
        out.close();

        mdFileTables = folder.resolve("tables.md").toFile();
        out = new PrintWriter(mdFileTables);
        out.println(validHeader);
        out.println("First Header|Second Header");
        out.println("-------------|-------------");
        out.println("Content Cell|Content Cell");
        out.println("Content Cell|Content Cell");
        out.close();

        mdFileWikilinks = folder.resolve("wikilinks.md").toFile();
        out = new PrintWriter(mdFileWikilinks);
        out.println(validHeader);
        out.println("[[Wiki-style links]]");
        out.close();

        mdFileAtxheaderspace = folder.resolve("atxheaderspace.md").toFile();
        out = new PrintWriter(mdFileAtxheaderspace);
        out.println(validHeader);
        out.println("#Test");
        out.close();

        mdFileForcelistitempara = folder.resolve("forcelistitempara.md").toFile();
        out = new PrintWriter(mdFileForcelistitempara);
        out.println(validHeader);
        out.println("1. Item 1");
        out.println("Item 1 lazy continuation");
        out.println("");
        out.println("    Item 1 paragraph 1");
        out.println("Item 1 paragraph 1 lazy continuation");
        out.println("    Item 1 paragraph 1 continuation");
        out.close();

        mdFileRelaxedhrules = folder.resolve("releaxedhrules.md").toFile();
        out = new PrintWriter(mdFileRelaxedhrules);
        out.println(validHeader);
        out.println("Hello World");
        out.println("---");
        out.println("***");
        out.println("___");
        out.println("");
        out.println("Hello World");
        out.println("***");
        out.println("---");
        out.println("___");
        out.println("");
        out.println("Hello World");
        out.println("___");
        out.println("---");
        out.println("***");
        out.close();

        mdTasklistitems = folder.resolve("tasklistsitem.md").toFile();
        out = new PrintWriter(mdTasklistitems);
        out.println(validHeader);
        out.println("* loose bullet item 3");
        out.println("* [ ] open task item");
        out.println("* [x] closed task item");
        out.close();

        mdExtanchorlinks = folder.resolve("mdExtanchorlinks.md").toFile();
        out = new PrintWriter(mdExtanchorlinks);
        out.println(validHeader);
        out.println("# header & some *formatting* ~~chars~~");
        out.close();
    }

    @Test
    public void parseValidMarkdownFileBasicCustomHeader() {
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(validMdFileBasicWithCustomHeader);
        Assertions.assertNotNull(documentModel);
        Assertions.assertEquals("This is a custom summary", documentModel.get("summary"));
        Assertions.assertEquals("232", documentModel.get("id"));
        Assertions.assertEquals("{\"og:type\":\"article\"}", documentModel.get("og").toString());
        Assertions.assertEquals("draft", documentModel.getStatus());
        Assertions.assertEquals("post", documentModel.getType());
        Assertions.assertEquals("news", documentModel.getCategory());
        Assertions.assertEquals("<h1>This is a file with a custom header</h1>\n", documentModel.getBody());
    }

    @Test
    public void parseValidMarkdownFileBasic() {
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(validMdFileBasic);
        Assertions.assertNotNull(documentModel);
        Assertions.assertEquals("draft", documentModel.getStatus());
        Assertions.assertEquals("post", documentModel.getType());
        Assertions.assertEquals("<h1>This is a test</h1>\n", documentModel.getBody());
    }

    @Test
    public void parseInvalidMarkdownFileBasic() {
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(invalidMdFileBasic);
        Assertions.assertNull(documentModel);
    }

    @Test
    public void parseValidMdFileHardWraps() {
        config.setMarkdownExtensions("HARDWRAPS");

        // Test with HARDWRAPS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileHardWraps);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>First line<br />\nSecond line</p>\n");

        // Test without HARDWRAPS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileHardWraps);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>First line Second line</p>");
    }

    @Test
    public void parseWithInvalidExtension() {
        config.setMarkdownExtensions("HARDWRAPS,UNDEFINED_EXTENSION");

        // Test with HARDWRAPS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileHardWraps);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>First line<br />\nSecond line</p>\n");
    }

    @Test
    public void parseValidMdFileAbbreviations() {
        config.setMarkdownExtensions("ABBREVIATIONS");

        // Test with ABBREVIATIONS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileAbbreviations);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<p><abbr title=\"Hyper Text Markup Language\">HTML</abbr></p>"
        );

        // Test without ABBREVIATIONS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileAbbreviations);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>*[HTML]: Hyper Text Markup Language HTML</p>");
    }

    @Test
    public void parseValidMdFileAutolinks() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("AUTOLINKS");

        // Test with AUTOLINKS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileAutolinks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<p><a href=\"http://github.com\">http://github.com</a></p>"
        );

        // Test without AUTOLINKS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileAutolinks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>http://github.com</p>");
    }

    @Test
    public void parseValidMdFileDefinitions() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("DEFINITIONS");

        // Test with DEFINITIONS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileDefinitions);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<dl>\n<dt>Apple</dt>\n<dd>Pomaceous fruit</dd>\n</dl>"
        );

        // Test without DEFNITIONS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileDefinitions);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>Apple :   Pomaceous fruit</p>");
    }

    @Test
    public void parseValidMdFileFencedCodeBlocks() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("FENCED_CODE_BLOCKS");

        // Test with FENCED_CODE_BLOCKS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileFencedCodeBlocks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<pre><code>function test() {\n  console.log(&quot;!&quot;);\n}\n</code></pre>"
        );

        // Test without FENCED_CODE_BLOCKS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileFencedCodeBlocks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<p><code>function test() { console.log(&quot;!&quot;); }</code></p>"
        );
    }

    @Test
    public void parseValidMdFileQuotes() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("QUOTES");

        // Test with QUOTES
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileQuotes);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>&ldquo;quotes&rdquo;</p>");

        // Test without QUOTES
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileQuotes);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>&quot;quotes&quot;</p>");
    }

    @Test
    public void parseValidMdFileSmarts() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("SMARTS");

        // Test with SMARTS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileSmarts);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>&hellip;</p>");

        // Test without SMARTS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileSmarts);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>...</p>");
    }

    @Test
    public void parseValidMdFileSmartypants() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("SMARTYPANTS");

        // Test with SMARTYPANTS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileSmartypants);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>&ldquo;&hellip;&rdquo;</p>");

        // Test without SMARTYPANTS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileSmartypants);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>&quot;...&quot;</p>");
    }

    @Test
    public void parseValidMdFileSuppressAllHTML() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("SUPPRESS_ALL_HTML");

        // Test with SUPPRESS_ALL_HTML
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileSuppressAllHTML);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("");

        // Test without SUPPRESS_ALL_HTML
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileSuppressAllHTML);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<div>!</div><em>!</em>");
    }

    @Test
    public void parseValidMdFileSuppressHTMLBlocks() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("SUPPRESS_HTML_BLOCKS");

        // Test with SUPPRESS_HTML_BLOCKS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileSuppressHTMLBlocks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("");

        // Test without SUPPRESS_HTML_BLOCKS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileSuppressHTMLBlocks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<div>!</div><em>!</em>");
    }

    @Test
    public void parseValidMdFileSuppressInlineHTML() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("SUPPRESS_INLINE_HTML");

        // Test with SUPPRESS_INLINE_HTML
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileSuppressInlineHTML);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>This is the first paragraph.  with  inline html</p>");

        // Test without SUPPRESS_INLINE_HTML
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileSuppressInlineHTML);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>This is the first paragraph. <span> with </span> inline html</p>");
    }

    @Test
    public void parseValidMdFileTables() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("TABLES");

        // Test with TABLES
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileTables);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<table>\n" +
                "<thead>\n" +
                "<tr><th>First Header</th><th>Second Header</th></tr>\n" +
                "</thead>\n" +
                "<tbody>\n" +
                "<tr><td>Content Cell</td><td>Content Cell</td></tr>\n" +
                "<tr><td>Content Cell</td><td>Content Cell</td></tr>\n" +
                "</tbody>\n" +
                "</table>"
        );

        // Test without TABLES
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileTables);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<p>First Header|Second Header -------------|------------- Content Cell|Content Cell Content Cell|Content Cell</p>"
        );
    }

    @Test
    public void parseValidMdFileWikilinks() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("WIKILINKS");

        // Test with WIKILINKS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileWikilinks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<p><a href=\"Wiki-style-links\">Wiki-style links</a></p>"
        );

        // Test without WIKILINKS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileWikilinks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>[[Wiki-style links]]</p>");
    }

    @Test
    public void parseValidMdFileAtxheaderspace() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("ATXHEADERSPACE");

        // Test with ATXHEADERSPACE
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileAtxheaderspace);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<p>#Test</p>");

        // Test without ATXHEADERSPACE
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileAtxheaderspace);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<h1>Test</h1>");
    }

    @Test
    public void parseValidMdFileForcelistitempara() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("FORCELISTITEMPARA");

        // Test with FORCELISTITEMPARA
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileForcelistitempara);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<ol>\n" +
                "<li>\n" +
                "<p>Item 1 Item 1 lazy continuation</p>\n" +
                "<p>Item 1 paragraph 1 Item 1 paragraph 1 lazy continuation Item 1 paragraph 1 continuation</p>\n" +
                "</li>\n" +
                "</ol>");

        // Test without FORCELISTITEMPARA
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileForcelistitempara);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<ol>\n" +
                "<li>Item 1 Item 1 lazy continuation\n" +
                "<p>Item 1 paragraph 1 Item 1 paragraph 1 lazy continuation Item 1 paragraph 1 continuation</p>\n" +
                "</li>\n" +
                "</ol>"
        );
    }

    @Test
    public void parseValidMdFileRelaxedhrules() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("RELAXEDHRULES");

        // Test with RELAXEDHRULES
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdFileRelaxedhrules);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<h2>Hello World</h2>\n" +
                "<hr />\n" +
                "<hr />\n" +
                "<p>Hello World</p>\n" +
                "<hr />\n" +
                "<hr />\n" +
                "<hr />\n" +
                "<p>Hello World</p>\n" +
                "<hr />\n" +
                "<hr />\n" +
                "<hr />"
        );

        // Test without RELAXEDHRULES
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdFileRelaxedhrules);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<h2>Hello World</h2>\n" +
                "<hr />\n" +
                "<hr />\n" +
                "<h2>Hello World ***</h2>\n" +
                "<hr />\n" +
                "<h2>Hello World ___</h2>\n" +
                "<hr />"
        );
    }

    @Test
    public void parseValidMdFileTasklistitems() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("TASKLISTITEMS");

        // Test with TASKLISTITEMS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdTasklistitems);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<ul>\n" +
                "<li>loose bullet item 3</li>\n" +
                "<li class=\"task-list-item\"><input type=\"checkbox\" class=\"task-list-item-checkbox\" disabled=\"disabled\" readonly=\"readonly\" />&nbsp;open task item</li>\n" +
                "<li class=\"task-list-item\"><input type=\"checkbox\" class=\"task-list-item-checkbox\" checked=\"checked\" disabled=\"disabled\" readonly=\"readonly\" />&nbsp;closed task item</li>\n" +
                "</ul>"
        );

        // Test without TASKLISTITEMS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdTasklistitems);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<ul>\n" +
                "<li>loose bullet item 3</li>\n" +
                "<li>[ ] open task item</li>\n" +
                "<li>[x] closed task item</li>\n" +
                "</ul>");
    }

    @Test
    public void parseValidMdFileExtanchorlinks() {
        config.setMarkdownExtensions("");
        config.setMarkdownExtensions("EXTANCHORLINKS");

        // Test with EXTANCHORLINKS
        Parser parser = new Parser(config);
        DocumentModel documentModel = parser.processFile(mdExtanchorlinks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains(
            "<h1><a href=\"#header-some-formatting-chars\" id=\"header-some-formatting-chars\"></a>header &amp; some <em>formatting</em> ~~chars~~</h1>"
        );

        // Test without EXTANCHORLINKS
        config.setMarkdownExtensions("");
        parser = new Parser(config);
        documentModel = parser.processFile(mdExtanchorlinks);
        Assertions.assertNotNull(documentModel);
        assertThat(documentModel.getBody()).contains("<h1>header &amp; some <em>formatting</em> ~~chars~~</h1>");
    }
}

package org.jbake.template

import org.apache.commons.io.FileUtils
import org.jbake.TestUtils
import org.jbake.app.Crawler
import org.jbake.app.Parser
import org.jbake.app.Renderer
import org.jbake.app.configuration.ConfigUtil
import org.jbake.app.configuration.DefaultJBakeConfiguration
import org.jbake.db.ContentStoreSqlite
import org.jbake.model.DocumentModel
import org.jbake.model.DocumentTypes
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import java.nio.charset.Charset
import java.nio.file.Path

import static org.assertj.core.api.Assertions.assertThat
import static org.junit.jupiter.api.Assertions.assertTrue
import static org.junit.jupiter.api.Assertions.assertEquals

public abstract class AbstractTemplateEngineRenderingIntegrationTest {

    @TempDir
    public static Path folder

    protected static ContentStoreSqlite contentStoreSqlite

    protected static DefaultJBakeConfiguration config;
    protected String templateDir
    protected static File sourceFolder
    protected String templateExtension
    protected final Map<String, List<String>> outputStrings = new HashMap<>()

    protected File destinationFolder
    protected File templateFolder
    protected Renderer renderer
    private Parser parser
    protected Locale currentLocale

    public AbstractTemplateEngineRenderingIntegrationTest(String templateDir, String templateExtension) {
        this.templateDir = templateDir
        this.templateExtension = templateExtension
    }

    @BeforeAll
    public static void setUpClass() throws Exception {
        contentStoreSqlite = new ContentStoreSqlite()
        contentStoreSqlite.createTables()

        sourceFolder = TestUtils.getTestResourcesAsSourceFolder()
        assertTrue(sourceFolder.exists(),"Cannot find sample data structure!")

        config = (DefaultJBakeConfiguration) new ConfigUtil().loadConfig(sourceFolder);
        config.setSourceFolder(sourceFolder);

        assertEquals(".html", config.getOutputExtension());

    }

    @BeforeEach
    public void setup() throws Exception {
        currentLocale = Locale.getDefault()
        Locale.setDefault(Locale.ENGLISH)

        ModelExtractorsDocumentTypeListener listener = new ModelExtractorsDocumentTypeListener()
        DocumentTypes.addListener(listener)

        templateFolder = new java.io.File(sourceFolder, templateDir)
        if (!templateFolder.exists()) {
            throw new Exception("Cannot find template folder!")
        }

        destinationFolder = folder.toFile()
        config.setDestinationFolder(destinationFolder)
        config.setTemplateFolder(templateFolder)

        for (String docType : DocumentTypes.getDocumentTypes()) {
            File templateFile = config.getTemplateFileByDocType(docType)

            if (templateFile != null) {
                String fileName = templateFile.getName()
                String fileBaseName = fileName.substring(0, fileName.lastIndexOf("."))
                config.setTemplateFileNameForDocType(docType, fileBaseName + "." + templateExtension)
            }
        }

        config.setTemplateFileNameForDocType("paper", "paper." + templateExtension)
        DocumentTypes.addDocumentType("paper")
        contentStoreSqlite.updateSchema()

        assertEquals(".html", config.getOutputExtension())

        Crawler crawler = new Crawler(contentStoreSqlite, config)
        crawler.crawl()
        parser = new Parser(config)
        renderer = new Renderer(contentStoreSqlite, config)

        setupExpectedOutputStrings()
    }

    private void setupExpectedOutputStrings() {

        outputStrings.put("post", Arrays.asList("<h2>Second Post</h2>",
            "<p class=\"post-date\">28",
            "2013</p>",
            "Lorem ipsum dolor sit amet",
            "<h5>Published Posts</h5>",
            "blog/2012/first-post.html"))

        outputStrings.put("page", Arrays.asList("<h4>About</h4>",
            "All about stuff!",
            "<h5>Published Pages</h5>",
            "/projects.html"))

        outputStrings.put("index", Arrays.asList("<a href=\"blog/2016/another-post.html\"",
            ">Another Post</a>",
            "<a href=\"blog/2013/second-post.html\"",
            ">Second Post</a>"))

        outputStrings.put("feed", Arrays.asList("<description>My corner of the Internet</description>",
            "<title>Second Post</title>",
            "<title>First Post</title>"))

        outputStrings.put("archive", Arrays.asList("<a href=\"blog/2013/second-post.html\"",
            ">Second Post</a>",
            "<a href=\"blog/2012/first-post.html\"",
            ">First Post</a>"))

        outputStrings.put("tags", Arrays.asList("<a href=\"blog/2013/second-post.html\"",
            ">Second Post</a>",
            "<a href=\"blog/2012/first-post.html\"",
            ">First Post</a>"))

        outputStrings.put("tags-index", Arrays.asList("<h1>Tags</h1>",
            "<h2><a href=\"../tags/blog.html\">blog</a>",
            "3</h2>"))

        outputStrings.put("sitemap", Arrays.asList("blog/2013/second-post.html",
            "blog/2012/first-post.html",
            "papers/published-paper.html"))

    }

    @AfterEach
    public void cleanup() {
        DocumentTypes.resetDocumentTypes()
        ModelExtractors.getInstance().reset()
        Locale.setDefault(currentLocale)
    }

    protected List<String> getOutputStrings(String type) {
        return outputStrings.get(type);
    }

    @Test
    public void renderPost() throws Exception {
        // setup
        String filename = "second-post.html"

        File sampleFile = new File(sourceFolder.getPath() + File.separator + "content"
            + File.separator + "blog" + File.separator + "2013" + File.separator + filename)
        DocumentModel content = parser.processFile(sampleFile)
        content.setUri("/" + filename)
        renderer.render(content)
        File outputFile = new File(destinationFolder, filename)
        assertTrue(outputFile.exists())

        // verify
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset())
        for (String string : getOutputStrings("post")) {
            assertThat(output).contains(string)
        }
    }

    @Test
    public void renderPage() throws Exception {
        // setup
        String filename = "about.html";

        File sampleFile = new File(sourceFolder.getPath() + File.separator + "content" + File.separator + filename);
        DocumentModel content = parser.processFile(sampleFile);
        content.setUri("/" + filename);
        renderer.render(content);
        File outputFile = new File(destinationFolder, filename);
        assertTrue(outputFile.exists());

        // verify
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset());
        for (String string : getOutputStrings("page")) {
            assertThat(output).contains(string);
        }
    }

    @Test
    public void renderIndex() throws Exception {
        //exec
        renderer.renderIndex("index.html");

        //validate
        File outputFile = new File(destinationFolder, "index.html");
        assertTrue(outputFile.exists());

        // verify
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset());
        for (String string : getOutputStrings("index")) {
            assertThat(output).contains(string);
        }
    }

    @Test
    public void renderFeed() throws Exception {
        renderer.renderFeed("feed.xml");
        File outputFile = new File(destinationFolder, "feed.xml");
        assertTrue(outputFile.exists());

        // verify
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset());
        for (String string : getOutputStrings("feed")) {
            assertThat(output).contains(string);
        }
    }

    @Test
    public void renderArchive() throws Exception {
        renderer.renderArchive("archive.html");
        File outputFile = new File(destinationFolder, "archive.html");
        assertTrue(outputFile.exists());

        // verify
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset());
        for (String string : getOutputStrings("archive")) {
            assertThat(output).contains(string);
        }
    }

    @Test
    public void renderTags() throws Exception {
        renderer.renderTags("tags");

        // verify
        String destinationPath = destinationFolder.toString() + File.separator + "tags" + File.separator + "blog.html"
        File outputFile = new File(destinationPath);
        assertTrue(outputFile.exists());
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset());
        for (String string : getOutputStrings("tags")) {
            assertThat(output).contains(string);
        }
    }

    @Test
    public void renderTagsIndex() throws Exception {
        config.setRenderTagsIndex(true);

        renderer.renderTags("tags");

        def destinationPath = destinationFolder.toString() + File.separator + "tags" + File.separator + "index.html"
        File outputFile = new File(destinationPath);
        assertTrue(outputFile.exists());
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset());
        for (String string : getOutputStrings("tags-index")) {
            assertThat(output).contains(string);
        }
    }

    @Test
    public void renderSitemap() throws Exception {
        DocumentTypes.addDocumentType("paper");

        renderer.renderSitemap("sitemap.xml");
        File outputFile = new File(destinationFolder, "sitemap.xml");
        assertTrue(outputFile.exists());

        // verify
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset());
        for (String string : getOutputStrings("sitemap")) {
            assertThat(output).contains(string);
        }
        assertThat(output).doesNotContain("draft-paper.html");
    }

    @Test
    public void checkDbTemplateModelIsPopulated() throws Exception {

        config.setPaginateIndex(true);
        config.setPostsPerPage(1);

        outputStrings.put("dbSpan", Arrays.asList("<span>3</span>"));

        contentStoreSqlite.deleteAllByDocType("post");

        renderer.renderIndexPaging("index.html");

        File outputFile = new File(destinationFolder, "index.html");
        String output = FileUtils.readFileToString(outputFile, Charset.defaultCharset());

        for (String string : getOutputStrings("dbSpan")) {
            assertThat(output).contains(string);
        }

    }
}

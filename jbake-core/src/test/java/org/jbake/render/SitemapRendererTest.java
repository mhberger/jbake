package org.jbake.render;

import org.jbake.db.ContentStoreOrientDb;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.template.RenderingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EnabledIfSystemProperty(named = "jbake.db.implementation", matches = "OrientDB")
public class SitemapRendererTest {

    @Test
    public void returnsZeroWhenConfigDoesNotRenderSitemaps() throws RenderingException {
        SitemapRenderer renderer = new SitemapRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderSiteMap()).thenReturn(false);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);

        Renderer mockRenderer = mock(Renderer.class);
        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        assertThat(renderResponse).isEqualTo(0);
    }

    @Test
    public void doesNotRenderWhenConfigDoesNotRenderSitemaps() throws Exception {
        SitemapRenderer renderer = new SitemapRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderSiteMap()).thenReturn(false);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, never()).renderSitemap(anyString());
    }

    @Test
    public void returnsOneWhenConfigRendersSitemaps() throws RenderingException {
        SitemapRenderer renderer = new SitemapRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderSiteMap()).thenReturn(true);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);

        Renderer mockRenderer = mock(Renderer.class);

        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        assertThat(renderResponse).isEqualTo(1);
    }

    @Test
    public void doesRenderWhenConfigDoesRenderSitemaps() throws Exception {
        SitemapRenderer renderer = new SitemapRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderSiteMap()).thenReturn(true);
        when(configuration.getSiteMapFileName()).thenReturn("mocksitemap.html");

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, times(1)).renderSitemap(anyString());
    }

    @Test
    public void propogatesRenderingException() throws Exception {
        SitemapRenderer renderer = new SitemapRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderSiteMap()).thenReturn(true);
        when(configuration.getSiteMapFileName()).thenReturn("mocksitemap.html");

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        doThrow(new Exception()).when(mockRenderer).renderSitemap(anyString());

        renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, never()).renderSitemap(anyString());
    }

}



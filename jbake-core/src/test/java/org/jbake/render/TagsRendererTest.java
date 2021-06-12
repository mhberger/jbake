package org.jbake.render;

import org.jbake.app.Renderer;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.db.ContentStoreOrientDb;
import org.jbake.template.RenderingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@EnabledIfSystemProperty(named = "jbake.db.implementation", matches = "OrientDB")
public class TagsRendererTest {

    @Test
    public void returnsZeroWhenConfigDoesNotRenderTags() throws RenderingException {
        TagsRenderer renderer = new TagsRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderTags()).thenReturn(false);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);

        Renderer mockRenderer = mock(Renderer.class);
        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        assertThat(renderResponse).isEqualTo(0);
    }

    @Test
    public void doesNotRenderWhenConfigDoesNotRenderTags() throws Exception {
        TagsRenderer renderer = new TagsRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderTags()).thenReturn(false);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, never()).renderTags(anyString());
    }

    @Test
    public void returnsOneWhenConfigRendersIndices() throws Exception {
        TagsRenderer renderer = new TagsRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderTags()).thenReturn(true);
        when(configuration.getTagPathName()).thenReturn("mocktagpath");

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        Set<String> tags = new HashSet<String>(Arrays.asList("tag1", "tags2"));
        when(contentStore.getTags()).thenReturn(tags);

        when(mockRenderer.renderTags(anyString())).thenReturn(1);

        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        assertThat(renderResponse).isEqualTo(1);
    }

    @Test
    public void doesRenderWhenConfigDoesRenderIndices() throws Exception {
        TagsRenderer renderer = new TagsRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderTags()).thenReturn(true);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        Set<String> tags = new HashSet<String>(Arrays.asList("tag1", "tags2"));
        when(contentStore.getTags()).thenReturn(tags);
        when(configuration.getTagPathName()).thenReturn("mockTagfile.html");

        renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, times(1)).renderTags(anyString());
    }

    @Test
    public void propagatesRenderingException() throws Exception {
        TagsRenderer renderer = new TagsRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderTags()).thenReturn(true);
        when(configuration.getTagPathName()).thenReturn("mocktagpath/tag");

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        doThrow(new Exception()).when(mockRenderer).renderTags(anyString());

        renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, never()).renderTags(anyString());
    }
}



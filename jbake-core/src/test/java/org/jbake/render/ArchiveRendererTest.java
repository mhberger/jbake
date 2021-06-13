package org.jbake.render;

import org.jbake.db.ContentStoreOrientDb;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.template.RenderingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@EnabledIfEnvironmentVariable(named = "jbake_db_implementation", matches = "OrientDB")
public class ArchiveRendererTest {

    @Test
    public void returnsZeroWhenConfigDoesNotRenderArchives() throws RenderingException {
        ArchiveRenderer renderer = new ArchiveRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderArchive()).thenReturn(false);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);

        Renderer mockRenderer = mock(Renderer.class);
        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        assertThat(renderResponse).isEqualTo(0);
    }

    @Test
    public void doesNotRenderWhenConfigDoesNotRenderArchives() throws Exception {
        ArchiveRenderer renderer = new ArchiveRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderArchive()).thenReturn(false);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, never()).renderArchive(anyString());
    }

    @Test
    public void returnsOneWhenConfigRendersArchives() throws RenderingException {
        ArchiveRenderer renderer = new ArchiveRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderArchive()).thenReturn(true);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);

        Renderer mockRenderer = mock(Renderer.class);

        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        assertThat(renderResponse).isEqualTo(1);
    }

    @Test
    public void doesRenderWhenConfigDoesRenderArchives() throws Exception {
        ArchiveRenderer renderer = new ArchiveRenderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderArchive()).thenReturn(true);
        when(configuration.getArchiveFileName()).thenReturn("mockarchive.html");

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, times(1)).renderArchive(anyString());
    }

    @Test
    public void propogatesRenderingException() throws Exception {
        Assertions.assertThrows(RenderingException.class, () -> {
            ArchiveRenderer renderer = new ArchiveRenderer();

            JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
            when(configuration.getRenderArchive()).thenReturn(true);
            when(configuration.getArchiveFileName()).thenReturn("mockarchive.html");

            ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
            Renderer mockRenderer = mock(Renderer.class);

            doThrow(new Exception()).when(mockRenderer).renderArchive(anyString());

            renderer.render(mockRenderer, contentStore, configuration);

            verify(mockRenderer, never()).renderArchive("random string");
        });
    }

}



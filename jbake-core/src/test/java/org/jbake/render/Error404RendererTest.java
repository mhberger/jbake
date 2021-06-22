package org.jbake.render;

import org.jbake.db.ContentStoreOrientDb;
import org.jbake.app.Renderer;
import org.jbake.app.configuration.DefaultJBakeConfiguration;
import org.jbake.app.configuration.JBakeConfiguration;
import org.jbake.template.RenderingException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class Error404RendererTest {
    @Test
    public void returnsZeroWhenConfigDoesNotRenderError404() throws RenderingException {
        Error404Renderer renderer = new Error404Renderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderError404()).thenReturn(false);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);

        Renderer mockRenderer = mock(Renderer.class);
        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        assertThat(renderResponse).isEqualTo(0);
    }

    @Test
    public void doesNotRenderWhenConfigDoesNotRenderError404() throws Exception {
        Error404Renderer renderer = new Error404Renderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderError404()).thenReturn(false);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, never()).renderError404(anyString());
    }

    @Test
    public void returnsOneWhenConfigRendersError404() throws RenderingException {
        Error404Renderer renderer = new Error404Renderer();

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderError404()).thenReturn(true);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        assertThat(renderResponse).isEqualTo(1);
    }

    @Test
    public void doesRenderWhenConfigDoesNotRenderError404() throws Exception {
        Error404Renderer renderer = new Error404Renderer();
        String error404file = "mock404file.html";

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderError404()).thenReturn(true);
        when(configuration.getError404FileName()).thenReturn(error404file);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, times(1)).renderError404(error404file);
    }

    @Test(expected = RenderingException.class)
    public void propogatesRenderingException() throws Exception {
        Error404Renderer renderer = new Error404Renderer();
        String error404file = "mock404file.html";

        JBakeConfiguration configuration = mock(DefaultJBakeConfiguration.class);
        when(configuration.getRenderError404()).thenReturn(true);
        when(configuration.getError404FileName()).thenReturn(error404file);

        ContentStoreOrientDb contentStore = mock(ContentStoreOrientDb.class);
        Renderer mockRenderer = mock(Renderer.class);

        doThrow(new Exception()).when(mockRenderer).renderError404(anyString());

        int renderResponse = renderer.render(mockRenderer, contentStore, configuration);

        verify(mockRenderer, never()).renderError404(error404file);
    }
}

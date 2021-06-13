package org.jbake.app.template;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

@EnabledIfEnvironmentVariable(named = "jbake_db_implementation", matches = "OrientDB")
public class JadeTemplateEngineRenderingTest extends AbstractTemplateEngineRenderingTest {

    public JadeTemplateEngineRenderingTest() {
        super("jadeTemplates", "jade");
    }
}

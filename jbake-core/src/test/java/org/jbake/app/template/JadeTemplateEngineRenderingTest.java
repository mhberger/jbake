package org.jbake.app.template;

import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@EnabledIfSystemProperty(named = "jbake.db.implementation", matches = "OrientDB")
public class JadeTemplateEngineRenderingTest extends AbstractTemplateEngineRenderingTest {

    public JadeTemplateEngineRenderingTest() {
        super("jadeTemplates", "jade");
    }
}

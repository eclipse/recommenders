package org.eclipse.recommenders.rcp.utils;

import static org.junit.Assert.*;

import org.junit.Test;

public class ProxiesTest {

    @Test
    public void testUserDomain() {
        assertEquals("mydomain", Proxies.getUserDomain("mydomain\\\\user").orNull());

        // System.getenv().put(ProxyUtils.ENV_USERDOMAIN, "mydomain_env\\\\user");
        // assertEquals(null, ProxyUtils.getUserDomain("mydomain_env\\\\user").orNull());

        System.getProperties().put(Proxies.PROP_HTTP_AUTH_NTLM_DOMAIN, "mydomain_props");
        assertEquals("mydomain_props", Proxies.getUserDomain("mydomain_props").orNull());

    }

    @Test
    public void testWorkstation() {
        assertNotNull(Proxies.getWorkstation().orNull());
    }

    @Test
    public void testUserName() {
        assertEquals("user", Proxies.getUserName("\\\\user").get());
        assertEquals("user2", Proxies.getUserName("domain\\\\user2").get());
        assertEquals("user3", Proxies.getUserName("user3").get());
    }
}

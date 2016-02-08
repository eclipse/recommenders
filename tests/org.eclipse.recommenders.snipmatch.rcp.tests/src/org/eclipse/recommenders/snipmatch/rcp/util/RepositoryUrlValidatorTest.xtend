package org.eclipse.recommenders.snipmatch.rcp.util

import org.junit.Test

import static org.junit.Assert.*

class RepositoryUrlValidatorTest {    

    @Test
    def void testUnhandlableUri() {
        assertFalse(RepositoryUrlValidator.isValidUri(""));
        assertFalse(RepositoryUrlValidator.isValidUri("http://"));
        assertFalse(RepositoryUrlValidator.isValidUri("http:// foo.com"));
        assertFalse(RepositoryUrlValidator.isValidUri("https:///www.foo.bar/"));
        assertFalse(RepositoryUrlValidator.isValidUri("http://.."));
        assertFalse(RepositoryUrlValidator.isValidUri("ssh://serverexample.com@example.com:/home/git.example.com/example.git"));
    }
    
    @Test
    def void testHandlableUri() {
        assertTrue(RepositoryUrlValidator.isValidUri("http://foo.com/bar_bar"));
        assertTrue(RepositoryUrlValidator.isValidUri("https://userid@example.com/"));
        assertTrue(RepositoryUrlValidator.isValidUri("http://foo.xz/bar_bar_(foo)_(again)"));
        assertTrue(RepositoryUrlValidator.isValidUri("git://host.xz:8001/path/to/repo.git/")); 
        assertTrue(RepositoryUrlValidator.isValidUri("ssh://git@git.example.com/foo/example.git/"));        
    }

}

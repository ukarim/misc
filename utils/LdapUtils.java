import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import javax.naming.Context;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.PartialResultException;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.LdapName;

public final class LdapUtils {

    private static final String LDAP_URL = "ldaps://ldap.example.com:636";
    private static final String DOMAIN = "example.com";
    private static final String SEARCH_ROOT = "dc=example,dc=com";
    private static final String SEARCH_FILTER = "(&(objectClass=user)(objectCategory=person)(|(userPrincipalName={0})(sAMAccountName={1})))";

    private LdapUtils() {}

    public static void main(String[] args) {
        for (var g : loginAndFetchGroups(args[0], args[1])) {
            System.out.println(g);
        }
    }

    public static Set<String> loginAndFetchGroups(String login, String password) {
        LdapContext ldapContext = null;
        try {
            Hashtable<String, Object> env = new Hashtable<>();
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            String bindPrincipal = createBindPrincipal(login);
            env.put(Context.SECURITY_PRINCIPAL, bindPrincipal);
            env.put(Context.PROVIDER_URL, LDAP_URL);
            env.put(Context.SECURITY_CREDENTIALS, password);
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");

            ldapContext = new InitialLdapContext(env, null);

            SearchControls searchControls = new SearchControls();
            String[] attrIDs = { "memberOf" };
            searchControls.setReturningAttributes(attrIDs);
            searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

            var searchParams = new Object[] { bindPrincipal, login };
            var searchResult = ldapContext.search(SEARCH_ROOT, SEARCH_FILTER, searchParams, searchControls);

            var roles = new HashSet<String>();
            try {
                while (searchResult.hasMore()) {
                    var next = searchResult.next();
                    var attributes = next.getAttributes();
                    var memberOf = attributes.get("memberOf");
                    NamingEnumeration<?> all = memberOf.getAll();
                    while (all.hasMore()) {
                        String role = parseCN((String) all.next());
                        if (role != null) {
                            roles.add(role);
                        }
                    }
                }
            } catch (PartialResultException e) {
                // ignore this exception
            } finally {
                closeWithCatch(searchResult);
            }

            return roles;
        } catch (Exception e) {
            throw new RuntimeException("LDAP bind error", e);
        } finally {
            closeWithCatch(ldapContext);
        }
    }

    private static String createBindPrincipal(String login) {
        if (login.contains("@") || login.endsWith(DOMAIN)) {
            return login;
        }
        return login + "@" + DOMAIN;
    }

    private static void closeWithCatch(LdapContext ctx) {
        try {
            if (ctx != null) {
                ctx.close();
            }
        } catch (Exception e) {
            // noop
        }
    }

    private static void closeWithCatch(NamingEnumeration<?> o) {
        try {
            if (o != null) {
                o.close();
            }
        } catch (Exception e) {
            // noop
        }
    }

    private static String parseCN(String in) throws InvalidNameException {
        var ldapName = new LdapName(in);
        for (var rdn : ldapName.getRdns()) {
            if (rdn.getType().equalsIgnoreCase("CN")) {
                return (String) rdn.getValue();
            }
        }
        return null;
    }
}

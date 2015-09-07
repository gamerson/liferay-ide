package com.liferay.ide.server.core.portal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.osgi.framework.dto.BundleDTO;

/**
 * @author Terry Jia
 */
public class PluginDeployer {

    private final String _framework = ":type=Deployer,*";
    private String _mBeanRegex = "Catalina";

    private MBeanServerConnection _mBeanServerConnection;

    public PluginDeployer() {
        this(null, -1);
    }

    public PluginDeployer(int port) {
        this("service:jmx:rmi:///jndi/rmi://:" + port + "/jmxrmi");
    }

    public PluginDeployer(String serviceURL) {
        try {
            final JMXServiceURL jmxServiceUrl = new JMXServiceURL(serviceURL);
            final JMXConnector jmxConnector = JMXConnectorFactory.connect(
                jmxServiceUrl, null);

            _mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                "Unable to get JMX connection", e);
        }
    }

    public PluginDeployer(String mBeanRegex, int port) {
        this(port);
        _mBeanRegex = mBeanRegex;
    }

    public PluginDeployer(String mBeanRegex, String serviceURL) {
        this(serviceURL);
        _mBeanRegex = mBeanRegex;
    }

    public long deployBundle(String bsn, File bundle) throws Exception {
        final ObjectName framework = getFramework(_mBeanServerConnection);

        long bundleId = -1;

        for (BundleDTO BundleDTO : listBundles()) {
            if (BundleDTO.symbolicName.equals(bsn)) {
                bundleId = BundleDTO.id;
                break;
            }
        }

//        if (bundleId > -1) {
//            _mBeanServerConnection.invoke(framework, "stopBundle",
//                    new Object[] { bundleId }, new String[] { "long" });
//
//            _mBeanServerConnection.invoke(framework, "updateBundleFromURL",
//                    new Object[] { bundleId,
//                    bundle.toURI().toURL().toExternalForm() },
//                    new String[] { "long", String.class.getName() });
//
//            _mBeanServerConnection.invoke(framework, "refreshBundle",
//                    new Object[] { bundleId }, new String[] { "long" });
//        } else {
//            Object installed = _mBeanServerConnection.invoke(
//                    framework,
//                    "installBundleFromURL",
//                    new Object[] { bundle.getAbsolutePath(),
//                            bundle.toURI().toURL().toExternalForm() },
//                    new String[] { String.class.getName(),
//                            String.class.getName() });
//
//            bundleId = Long.parseLong(installed.toString());
//        }
//
//        _mBeanServerConnection.invoke(framework, "startBundle",
//            new Object[] { bundleId }, new String[] { "long" });

        return bundleId;
    }

    private ObjectName getBundleState()
        throws MalformedObjectNameException, IOException {

        return _mBeanServerConnection
            .queryNames(new ObjectName(_mBeanRegex + ":type=bundleState,*"),
                null).iterator().next();
    }

    private ObjectName getFramework(
            MBeanServerConnection mBeanServerConnection)
        throws MalformedObjectNameException, IOException {

        final Set<ObjectName> objectNames =
            mBeanServerConnection.queryNames(
                new ObjectName(_mBeanRegex + _framework), null);

        if (objectNames != null && objectNames.size() > 0) {
            return objectNames.iterator().next();
        }

        return null;
    }

    public BundleDTO[] listBundles() {
        final List<BundleDTO> retval = new ArrayList<BundleDTO>();

        try {
            final ObjectName bundleState = getBundleState();

            final Object[] params = new Object[] {
                new String[] {
                    "Identifier",
                    "SymbolicName",
                    "State",
                    "Version",
                }
            };

            final String[] signature = new String[] {
                String[].class.getName()
            };

            final TabularData data = (TabularData) _mBeanServerConnection
                    .invoke(bundleState, "listBundles", params, signature);

            for (Object value : data.values()) {
                final CompositeData cd = (CompositeData) value;

                try {
                    retval.add(newFromData(cd));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return retval.toArray(new BundleDTO[0]);
    }

    private static BundleDTO newFromData(CompositeData cd) {
        final BundleDTO bundle = new BundleDTO();
        bundle.id = Long.parseLong(cd.get("Identifier").toString());
        bundle.symbolicName = cd.get("SymbolicName").toString();

        return bundle;
    }

    public void uninstallBundle(String bsn) throws Exception {
        for (BundleDTO BundleDTO : listBundles()) {
            if (BundleDTO.symbolicName.equals(bsn)) {
                uninstallBundle(BundleDTO.id);

                return;
            }
        }

        throw new IllegalStateException("Unable to uninstall " + bsn);
    }

    public void uninstallBundle( long id ) throws Exception {
        final ObjectName framework = getFramework(_mBeanServerConnection);

        _mBeanServerConnection.invoke( framework, "uninstallBundle",
            new Object[] { id }, new String[] { "long" } );
    }

    public boolean ping() {
        try {
            return _mBeanServerConnection != null &&
                _mBeanServerConnection.queryNames( new ObjectName( "osgi.core:type=bundleState,*" ), null ) != null;
        }
        catch( Exception e ) {
            return false;
        }
    }
}

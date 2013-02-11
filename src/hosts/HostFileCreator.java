/*
 * File: HostFileCreator.java Author: Robert Bittle <guywithnose@gmail.com>
 */
package hosts;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import data.FileWriter;

/**
 * The Class HostFileCreator.
 */
public class HostFileCreator
{
  /** The sites. */
    private JSONObject sites;

    /** The aliases. */
    private JSONObject aliases;

    /** The ips. */
    private JSONObject ips;

    /** The errors. */
    private ArrayList<String> errors = new ArrayList<String>();

    /** The all ip info. */
    private ArrayList<IPInfo> allIPInfo;

    /** The conf. */
    private JSONObject conf;

    /**
     * The Class IPInfo.
     */
    public class IPInfo implements Comparable<IPInfo>
    {
        /** The ip. */
        public String ip;

        /** The ip name. */
        public String ipName;

        /** The alias. */
        public String alias;

        /** The host name. */
        public String hostName;

        /**
         * Instantiates a new iP info.
         * 
         * @param newIp
         *            the new ip
         * @param newIpName
         *            the new ip name
         * @param newAlias
         *            the new alias
         * @param newHostName
         *            the new host name
         */
        public IPInfo(String newIp, String newIpName, String newAlias, String newHostName)
        {
            ip = newIp;
            ipName = newIpName;
            alias = newAlias;
            hostName = newHostName;
        }

        @Override
        public String toString()
        {
            return hostName;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(IPInfo other)
        {
            return hostName.compareTo(other.hostName);
        }
    }

    /**
     * Instantiates a new host file creator.
     * 
     * @param conf
     *            the conf
     * @throws JSONException
     *             the jSON exception
     */
    public HostFileCreator(JSONObject newConf) throws JSONException
    {
        conf = newConf;
        sites = conf.getJSONObject("sites");
        aliases = conf.getJSONObject("aliases");
        ips = conf.getJSONObject("IPs");
    }

    public boolean setSiteToAlias(String site, String alias)
    {
        try {
            if (aliases.has(alias)) {
                sites.put(site, alias);
            }
            conf.put("sites", sites);
        } catch (JSONException e) {
            return false;
        }
        allIPInfo = null;
        return true;
    }

    public boolean setSiteToServer(String site, String server)
    {
        try {
            if (ips.has(server) || "dns".equals(server)) {
                sites.put(site, server);
            }
            conf.put("sites", sites);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        allIPInfo = null;
        return true;
    }

    /**
     * Gets the all ip info.
     * 
     * @return the all ip info
     */
    public List<IPInfo> getAllIPInfo()
    {
        if (allIPInfo == null) {
            allIPInfo = new ArrayList<IPInfo>();
            @SuppressWarnings("unchecked")
            Iterator<String> siteIterator = sites.keys();
            while (siteIterator.hasNext()) {
                String siteName = siteIterator.next();
                IPInfo info = getIPInfo(siteName);
                if (info != null) {
                    allIPInfo.add(info);
                }
            }
        }
        return allIPInfo;
    }

    /**
     * Gets the all ip info.
     * 
     * @return the all ip info
     */
    public List<String> getAllAliases()
    {
        List<String> allAliases = new ArrayList<String>();
        @SuppressWarnings("unchecked")
        Iterator<String> aliasIterator = aliases.keys();
        while (aliasIterator.hasNext()) {
            allAliases.add(aliasIterator.next());
        }
        return allAliases;
    }

    /**
     * Gets the all ip info.
     * 
     * @return the all ip info
     */
    public List<String> getAllServers()
    {
        List<String> allIps = new ArrayList<String>();
        @SuppressWarnings("unchecked")
        Iterator<String> aliasIterator = ips.keys();
        while (aliasIterator.hasNext()) {
            allIps.add(aliasIterator.next());
        }
        allIps.add("dns");
        return allIps;
    }

    /**
     * Builds the host file.
     * 
     * @return the string
     */
    public String buildHostFile()
    {
        StringBuilder hostbuilder = new StringBuilder();
        for (IPInfo info : getAllIPInfo()) {
            if (info.ip != null) {
                hostbuilder.append(info.ip);
                hostbuilder.append(" ");
                hostbuilder.append(info.hostName);
                hostbuilder.append("\n");
            }
        }
        return hostbuilder.toString();
    }

    /**
     * Gets the iP info.
     * 
     * @param hostName
     *            the host name
     * @return the iP info
     */
    public IPInfo getIPInfo(String hostName)
    {
        try {
            String destination = sites.getString(hostName);
            String ipName = destination;
            String ip = getIP(destination);
            String alias = null;
            if (ip == null) {
                alias = destination;
                ipName = getAliasIpName(destination);
                ip = getIP(ipName);
            }
            return new IPInfo(ip, ipName, alias, hostName);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Gets the alias ip.
     * 
     * @param alias
     *            the alias
     * @return the alias ip
     */
    public String getAliasIpName(String alias)
    {
        String aliasDestination = getAlias(alias);
        String ip = getIP(aliasDestination);
        String lastAlias = aliasDestination;
        String nextAlias = getAlias(aliasDestination);
        while (ip == null && nextAlias != null && "dns" != nextAlias) {
            lastAlias = nextAlias;
            ip = getIP(nextAlias);
            nextAlias = getAlias(nextAlias);
        }
        return lastAlias;
    }

    /**
     * Gets the ip.
     * 
     * @param hostName
     *            the ip name
     * @return the ip
     */
    private String getIP(String hostName)
    {
        if ("dns".equalsIgnoreCase(hostName)) {
            return null;
        }
        try {
            return ips.getString(hostName);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Gets the alias.
     * 
     * @param ipName
     *            the ip name
     * @return the alias
     */
    private String getAlias(String ipName)
    {
        try {
            return aliases.getString(ipName);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Get Errors.
     * 
     * @return the errors
     */
    public ArrayList<String> getErrors()
    {
        return errors;
    }

    /**
     * Save.
     */
    public boolean save(File file)
    {
        try {
            return FileWriter.putFileContents(file, conf.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param selectedFile
     * @return
     */
    public boolean export(File file)
    {
        String hostFile = buildHostFile();
        if (errors.size() != 0) {
            for (String error : errors) {
              System.err.println(error);
            }
            return false;
        }
        return FileWriter.putFileContents(file, hostFile);
    }
}
package launcher.client;

import launcher.LauncherAPI;
import launcher.hasher.FileNameMatcher;
import launcher.helper.IOHelper;
import launcher.helper.VerifyHelper;
import launcher.serialize.HInput;
import launcher.serialize.config.ConfigObject;
import launcher.serialize.config.entry.*;
import launcher.serialize.config.entry.ConfigEntry.Type;
import launcher.serialize.stream.StreamObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ComparableImplementedButEqualsNotOverridden")
public final class ClientProfile extends ConfigObject implements Comparable<ClientProfile>
{
    @LauncherAPI
    public static final StreamObject.Adapter<ClientProfile> RO_ADAPTER = input -> new ClientProfile(input, true);
    private static final FileNameMatcher ASSET_MATCHER = new FileNameMatcher(
            new String[0], new String[]{"indexes", "objects"}, new String[0]);

    // Version
    private final StringConfigEntry version;
    private final StringConfigEntry assetIndex;

    // Client
    private final IntegerConfigEntry sortIndex;
    private final StringConfigEntry title;
    private final StringConfigEntry serverAddress;
    private final IntegerConfigEntry serverPort;

    //  Updater and client watch service
    private final ListConfigEntry update;
    private final ListConfigEntry updateExclusions;
    private final ListConfigEntry updateVerify;
    private final BooleanConfigEntry updateFastCheck;

    // Client launcher
    private final StringConfigEntry mainClass;
    private final ListConfigEntry jvmArgs;
    private final ListConfigEntry classPath;
    private final ListConfigEntry clientArgs;

    @LauncherAPI
    public ClientProfile(BlockConfigEntry block)
    {
        super(block);

        // Version
        version = block.getEntry("version", StringConfigEntry.class);
        assetIndex = block.getEntry("assetIndex", StringConfigEntry.class);

        // Client
        sortIndex = block.getEntry("sortIndex", IntegerConfigEntry.class);
        title = block.getEntry("title", StringConfigEntry.class);
        serverAddress = block.getEntry("serverAddress", StringConfigEntry.class);
        serverPort = block.getEntry("serverPort", IntegerConfigEntry.class);

        //  Updater and client watch service
        update = block.getEntry("update", ListConfigEntry.class);
        updateVerify = block.getEntry("updateVerify", ListConfigEntry.class);
        updateExclusions = block.getEntry("updateExclusions", ListConfigEntry.class);
        updateFastCheck = block.getEntry("updateFastCheck", BooleanConfigEntry.class);

        // Client launcher
        mainClass = block.getEntry("mainClass", StringConfigEntry.class);
        classPath = block.getEntry("classPath", ListConfigEntry.class);
        jvmArgs = block.getEntry("jvmArgs", ListConfigEntry.class);
        clientArgs = block.getEntry("clientArgs", ListConfigEntry.class);
    }

    @LauncherAPI
    public ClientProfile(HInput input, boolean ro) throws IOException
    {
        this(new BlockConfigEntry(input, ro));
    }

    @Override
    public int compareTo(ClientProfile o)
    {
        return Integer.compare(getSortIndex(), o.getSortIndex());
    }

    @Override
    public String toString()
    {
        return title.getValue();
    }

    @LauncherAPI
    public String getAssetIndex()
    {
        return assetIndex.getValue();
    }

    @LauncherAPI
    public FileNameMatcher getAssetUpdateMatcher()
    {
        return getVersion().compareTo(Version.MC1710) >= 0 ? ASSET_MATCHER : null;
    }

    @LauncherAPI
    public String[] getClassPath()
    {
        return classPath.stream(StringConfigEntry.class).toArray(String[]::new);
    }

    @LauncherAPI
    public String[] getClientArgs()
    {
        return clientArgs.stream(StringConfigEntry.class).toArray(String[]::new);
    }

    @LauncherAPI
    public FileNameMatcher getClientUpdateMatcher()
    {
        String[] updateArray = update.stream(StringConfigEntry.class).toArray(String[]::new);
        String[] verifyArray = updateVerify.stream(StringConfigEntry.class).toArray(String[]::new);
        String[] exclusionsArray = updateExclusions.stream(StringConfigEntry.class).toArray(String[]::new);
        return new FileNameMatcher(updateArray, verifyArray, exclusionsArray);
    }

    @LauncherAPI
    public String[] getJvmArgs()
    {
        return jvmArgs.stream(StringConfigEntry.class).toArray(String[]::new);
    }

    @LauncherAPI
    public String getMainClass()
    {
        return mainClass.getValue();
    }

    @LauncherAPI
    public String getServerAddress()
    {
        return serverAddress.getValue();
    }

    @LauncherAPI
    public int getServerPort()
    {
        return serverPort.getValue();
    }

    @LauncherAPI
    public InetSocketAddress getServerSocketAddress()
    {
        return InetSocketAddress.createUnresolved(getServerAddress(), getServerPort());
    }

    @LauncherAPI
    public int getSortIndex()
    {
        return sortIndex.getValue();
    }

    @LauncherAPI
    public String getTitle()
    {
        return title.getValue();
    }

    @LauncherAPI
    public void setTitle(String title)
    {
        this.title.setValue(title);
    }

    @LauncherAPI
    public Version getVersion()
    {
        return Version.byName(version.getValue());
    }

    @LauncherAPI
    public void setVersion(Version version)
    {
        this.version.setValue(version.name);
    }

    @LauncherAPI
    public boolean isUpdateFastCheck()
    {
        return updateFastCheck.getValue();
    }

    @LauncherAPI
    public void verify()
    {
        // Version
        getVersion();
        IOHelper.verifyFileName(getAssetIndex());

        // Client
        VerifyHelper.verify(getTitle(), VerifyHelper.NOT_EMPTY, "Profile title can't be empty");
        VerifyHelper.verify(getServerAddress(), VerifyHelper.NOT_EMPTY, "Server address can't be empty");
        VerifyHelper.verifyInt(getServerPort(), VerifyHelper.range(0, 65535), "Illegal server port: " + getServerPort());

        //  Updater and client watch service
        update.verifyOfType(Type.STRING);
        updateVerify.verifyOfType(Type.STRING);
        updateExclusions.verifyOfType(Type.STRING);

        // Client launcher
        jvmArgs.verifyOfType(Type.STRING);
        classPath.verifyOfType(Type.STRING);
        clientArgs.verifyOfType(Type.STRING);
        VerifyHelper.verify(getTitle(), VerifyHelper.NOT_EMPTY, "Main class can't be empty");
    }

    @LauncherAPI
    public enum Version
    {
        // На всякий случай
        // https://minecraft.gamepedia.com/Protocol_version

        // 1.4.x
        MC147("1.4.7", 51),

        // 1.5.x
        MC152("1.5.2", 61),

        // 1.6.x
        MC164("1.6.4", 78),

        // 1.7.x
        MC17("1.7", 3),
        MC171("1.7.1", 3),
        MC172("1.7.2", 4),
        MC173("1.7.3", 4),
        MC174("1.7.4", 4),
        MC175("1.7.5", 4),
        MC176("1.7.6", 5),
        MC177("1.7.7", 5),
        MC178("1.7.8", 5),
        MC179("1.7.9", 5),
        MC1710("1.7.10", 5),

        // 1.8.x
        MC18("1.8", 47),
        MC181("1.8.1", 47),
        MC182("1.8.2", 47),
        MC183("1.8.3", 47),
        MC184("1.8.4", 47),
        MC185("1.8.5", 47),
        MC186("1.8.6", 47),
        MC187("1.8.7", 47),
        MC188("1.8.8", 47),
        MC189("1.8.9", 47),

        // 1.9.x
        MC19("1.9", 107),
        MC191("1.9.1", 108),
        MC192("1.9.2", 109),
        MC193("1.9.3", 110),
        MC194("1.9.4", 110),

        // 1.10.x
        MC110("1.10", 210),
        MC1101("1.10.1", 210),
        MC1102("1.10.2", 210),

        // 1.11.x
        MC111("1.11", 315),
        MC1111("1.11.1", 316),
        MC1112("1.11.2", 316),

        // 1.12.x
        MC112("1.12", 335),
        MC1121("1.12.1", 338),
        MC1122("1.12.2", 340),

        // 1.13.x
        MC113("1.13", 393),
        MC1131("1.13.1", 401),
        MC1132("1.13.2", 404),

        // 1.14.x
        MC114("1.14", 477),
        MC1141("1.14.1", 480),
        MC1142("1.14.2", 485),
        MC1143("1.14.3", 490),
        MC1144("1.14.4", 498),

        // 1.15.x
        MC115("1.15", 573),
        MC1151("1.15.1", 575),
        MC1152("1.15.2", 578),

        // 1.16.x
        MC1160("1.16", 735),
        MC1161("1.16.1", 736),
        MC1162("1.16.2", 751);

        private static final Map<String, Version> VERSIONS;

        static
        {
            Version[] versionsValues = values();
            VERSIONS = new HashMap<>(versionsValues.length);
            for (Version version : versionsValues)
            {
                VERSIONS.put(version.name, version);
            }
        }

        public final String name;
        public final int protocol;

        Version(String name, int protocol)
        {
            this.name = name;
            this.protocol = protocol;
        }

        public static Version byName(String name)
        {
            return VerifyHelper.getMapValue(VERSIONS, name, String.format("Unknown client version: '%s'", name));
        }

        @Override
        public String toString()
        {
            return "Minecraft " + name;
        }
    }
}

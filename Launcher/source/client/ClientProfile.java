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
        // На всякий случай протоколы:
        // https://minecraft.gamepedia.com/Protocol_version
        // Официальные версии с аргументами и т.д.:
        // https://launchermeta.mojang.com/mc/game/version_manifest.json

        // 1.4.x
        MC147("1.4.7"),

        // 1.5.x
        MC152("1.5.2"),

        // 1.6.x
        MC164("1.6.4"),

        // 1.7.x
        MC17("1.7"),
        MC171("1.7.1"),
        MC172("1.7.2"),
        MC173("1.7.3"),
        MC174("1.7.4"),
        MC175("1.7.5"),
        MC176("1.7.6"),
        MC177("1.7.7"),
        MC178("1.7.8"),
        MC179("1.7.9"),
        MC1710("1.7.10"),

        // 1.8.x
        MC18("1.8"),
        MC181("1.8.1"),
        MC182("1.8.2"),
        MC183("1.8.3"),
        MC184("1.8.4"),
        MC185("1.8.5"),
        MC186("1.8.6"),
        MC187("1.8.7"),
        MC188("1.8.8"),
        MC189("1.8.9"),

        // 1.9.x
        MC19("1.9"),
        MC191("1.9.1"),
        MC192("1.9.2"),
        MC193("1.9.3"),
        MC194("1.9.4"),

        // 1.10.x
        MC110("1.10"),
        MC1101("1.10.1"),
        MC1102("1.10.2"),

        // 1.11.x
        MC111("1.11"),
        MC1111("1.11.1"),
        MC1112("1.11.2"),

        // 1.12.x
        MC112("1.12"),
        MC1121("1.12.1"),
        MC1122("1.12.2"),

        // 1.13.x
        MC113("1.13"),
        MC1131("1.13.1"),
        MC1132("1.13.2"),

        // 1.14.x
        MC114("1.14"),
        MC1141("1.14.1"),
        MC1142("1.14.2"),
        MC1143("1.14.3"),
        MC1144("1.14.4"),

        // 1.15.x
        MC115("1.15"),
        MC1151("1.15.1"),
        MC1152("1.15.2"),

        // 1.16.x
        MC1160("1.16"),
        MC1161("1.16.1"),
        MC1162("1.16.2"),
        MC1163("1.16.3"),
        MC1164("1.16.4"),
        MC1165("1.16.5");

        // Да и json тут к слову нахуй не сдался XD
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

        Version(String name)
        {
            this.name = name;
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

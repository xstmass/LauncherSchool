package launchserver.auth.hwid;

import java.util.Arrays;
import java.util.List;

import launcher.serialize.config.entry.BlockConfigEntry;

public class AcceptHWIDHandler extends HWIDHandler
{
    public AcceptHWIDHandler(BlockConfigEntry block) { super(block); }

    @Override
    public void ban(List<HWID> hwid) {}

    @Override
    public void check0(HWID hwid, String username) {}

    @Override
    public void close() {}

    @Override
    public List<HWID> getHwid(String username) { return Arrays.asList(nullHWID); }

    @Override
    public void unban(List<HWID> hwid) {}
}


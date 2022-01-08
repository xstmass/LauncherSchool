package com.mojang.authlib.yggdrasil;

import com.google.common.collect.ImmutableSet;
import com.mojang.authlib.Environment;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.minecraft.TelemetrySession;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.yggdrasil.response.BlockListResponse;
import com.mojang.authlib.yggdrasil.response.UserAttributesResponse;

import javax.annotation.Nullable;
import java.net.Proxy;
import java.net.URL;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

public class YggdrasilUserApiService implements UserApiService {
    private static final long BLOCKLIST_REQUEST_COOLDOWN_SECONDS = 120;
    private static final UUID ZERO_UUID = new UUID(0, 0);

//    private final URL routePrivileges;
//    private final URL routeBlocklist;
//
//    private final MinecraftClient minecraftClient;
//    private final Environment environment;
    private UserProperties properties = OFFLINE_PROPERTIES;

    @Nullable
    private Instant nextAcceptableBlockRequest;

    @Nullable
    private Set<UUID> blockList;

    public YggdrasilUserApiService(final String accessToken, final Proxy proxy, final Environment env) throws AuthenticationException {
        // Заглушка
    }

    @Override
    public UserProperties properties() {
        return properties;
    }

    @Override
    public TelemetrySession newTelemetrySession(final Executor executor) {
        return TelemetrySession.DISABLED;
    }

    @Override
    public boolean isBlockedPlayer(final UUID playerID) {
        return false;
    }

    @Override
    public void refreshBlockList() {}

    @Nullable
    private Set<UUID> fetchBlockList() {
        return null;
    }

    private boolean canMakeBlockListRequest() {
        return nextAcceptableBlockRequest == null || Instant.now().isAfter(nextAcceptableBlockRequest);
    }

    @Nullable
    private Set<UUID> forceFetchBlockList() {
        return null;
    }

    private void fetchProperties() throws AuthenticationException {
        // Заглушка
    }

    private static void addFlagIfUserHasPrivilege(final boolean privilege, final UserFlag value, final ImmutableSet.Builder<UserFlag> output) {
        if (privilege) {
            output.add(value);
        }
    }
}

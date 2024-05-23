package org.by1337.bairx.hook.papi;

import com.google.common.base.Joiner;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.by1337.bairx.BAirDropX;
import org.by1337.bairx.airdrop.AirDrop;
import org.by1337.blib.util.NameKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PapiHook extends PlaceholderExpansion {
    private final Placeholder placeholder;

    public PapiHook() {
        placeholder = new Placeholder("bairx");
        placeholder.addSubPlaceholder(new Placeholder("placeholder")
                .executor((pl, args) -> {
                    String str = Joiner.on('_').join(args);
                    String[] param = new String[2];
                    param[0] = str.split("_")[0];
                    param[1] = str.substring(param[0].length() + 1);
                    AirDrop airDrop = BAirDropX.getAirdropById(new NameKey(param[0]));
                    if (airDrop == null) return "unknown airdrop " + param[0];
                    return airDrop.replace(String.format("{%s}", param[1]));
                }));
        placeholder.build();
    }


    @Override
    public @NotNull String getIdentifier() {
        return "BAirDropX";
    }

    @Override
    public @NotNull String getAuthor() {
        return "By1337";
    }

    @Override
    public @NotNull String getVersion() {
        return BAirDropX.getInstance().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    @Nullable
    public String onPlaceholderRequest(final Player player, @NotNull final String params) {
        return placeholder.process(player, params.split("_"));
    }

    public Placeholder getPlaceholder() {
        return placeholder;
    }
}

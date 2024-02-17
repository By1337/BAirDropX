package org.by1337.bairx.nbt.nms;

import org.by1337.blib.util.Version;
import org.by1337.bairx.nbt.api.ParseCompoundTag;
import org.by1337.bairx.nbt.nms.v1_16_5.ParseCompoundTagV165;
import org.by1337.bairx.nbt.nms.v1_17_1.ParseCompoundTagV171;
import org.by1337.bairx.nbt.nms.v1_18_2.ParseCompoundTagV182;
import org.by1337.bairx.nbt.nms.v1_19_4.ParseCompoundTagV194;
import org.by1337.bairx.nbt.nms.v1_20_1.ParseCompoundTagV201;
import org.by1337.bairx.nbt.nms.v1_20_2.ParseCompoundTagV202;
import org.by1337.bairx.nbt.nms.v1_20_4.ParseCompoundTagV204;

public class ParseCompoundTagManager {
    private final static ParseCompoundTag parseCompoundTag = switch (Version.VERSION){
        case V1_16_5 -> new ParseCompoundTagV165();
        case V1_17_1 -> new ParseCompoundTagV171();
        case V1_18_2 -> new ParseCompoundTagV182();
        case V1_19_4 -> new ParseCompoundTagV194();
        case V1_20_1 -> new ParseCompoundTagV201();
        case V1_20_2 -> new ParseCompoundTagV202();
        case V1_20_4 -> new ParseCompoundTagV204();
        default -> throw new UnsupportedOperationException("Unsupported version! " + Version.getGameVersion());
    };

    public static ParseCompoundTag get(){
        return parseCompoundTag;
    }
}

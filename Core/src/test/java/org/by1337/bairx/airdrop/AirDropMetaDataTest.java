package org.by1337.bairx.airdrop;

import junit.framework.TestCase;
import org.by1337.blib.nbt.impl.CompoundTag;
import org.by1337.blib.io.ByteBuffer;
import org.junit.Test;
;import static org.junit.Assert.*;

public class AirDropMetaDataTest   {

    @Test
    public void testWriteAndRead() {
        int version = 1;
        String type = "TestType";
        CompoundTag extra = new CompoundTag();
        extra.putString("key", "value");

        AirDropMetaData originalMetaData = new AirDropMetaData(version, type, extra);


        ByteBuffer buffer = new ByteBuffer();
        originalMetaData.write(buffer);


        AirDropMetaData readMetaData = AirDropMetaData.read(new ByteBuffer(buffer.toByteArray()));


        assertEquals(version, readMetaData.getVersion());
        assertEquals(type, readMetaData.getType());
        assertEquals(extra, readMetaData.getExtra());
    }

    @Test
    public void testCreateEmpty() {
        AirDropMetaData emptyMetaData = AirDropMetaData.createEmpty();

        assertEquals(0, emptyMetaData.getVersion());
        assertEquals("", emptyMetaData.getType());
        assertNotNull(emptyMetaData.getExtra());
        assertTrue(emptyMetaData.getExtra().isEmpty());
    }

    @Test
    public void testInvalidMagicNumbers() {
        ByteBuffer buffer = new ByteBuffer();

        byte[] invalidMagicNumbers = {1, 2, 3, 4, 5, 6};
        buffer.writeBytes(invalidMagicNumbers);

        assertThrows(IllegalArgumentException.class, () -> AirDropMetaData.read(new ByteBuffer(buffer.toByteArray())));
    }

    @Test
    public void testExtraBytes() {
        int version = 1;
        String type = "TestType";
        CompoundTag extra = new CompoundTag();
        extra.putString("key", "value");

        AirDropMetaData metaData = new AirDropMetaData(version, type, extra);

        ByteBuffer buffer = new ByteBuffer();
        metaData.write(buffer);

        buffer.writeByte(42);

        assertThrows(IllegalArgumentException.class, () -> AirDropMetaData.read(new ByteBuffer(buffer.toByteArray())));
    }
}
package cn.afternode.simpleprotocol.netty;

import cn.afternode.simpleprotocol.core.IPacketBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public record NettyPacketBuffer(ByteBuf src) implements IPacketBuffer<ByteBuf> {
    @Override
    public void writeBlock(byte[] block) {
        src.writeIntLE(block.length);
        src.writeBytes(block);
    }

    @Override
    public byte[] readBlock() {
        byte[] block = new byte[src.readIntLE()];
        src.readBytes(block);
        return block;
    }

    @Override
    public void writeString(String val) {
        src.writeBytes(val.getBytes(StandardCharsets.UTF_8));
        src.writeByte(0x00);
    }

    @Override
    public String readString() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte read;
        while ((read = src.readByte()) != 0x00)
            out.write(read);
        return out.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void writeInt(int val) {
        src.writeInt(val);
    }

    @Override
    public int readInt() {
        return src.readInt();
    }

    @Override
    public void writeShort(short val) {
        src.writeShort(val);
    }

    @Override
    public short readShort() {
        return src.readShort();
    }

    @Override
    public void writeLong(long val) {
        src.writeLong(val);
    }

    @Override
    public long readLong() {
        return src.readLong();
    }

    @Override
    public <E extends Enum<?>> void writeEnum(E val) {
        src.writeByte(val.ordinal());
    }

    @Override
    public <E extends Enum<?>> E readEnum(Class<E> type) {
        return type.getEnumConstants()[src.readByte()];
    }

    @Override
    public ByteBuf src() {
        return Unpooled.copiedBuffer(src);
    }

    @Override
    public byte[] array() {
        return src.array();
    }
}

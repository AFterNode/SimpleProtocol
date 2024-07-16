package cn.afternode.simpleprotocol.simple;

import cn.afternode.simpleprotocol.core.IPacketBuffer;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class SimplePacketBuffer implements IPacketBuffer<ByteBuffer> {
    public static final int DEFAULT_CAPACITY = 32767;
    public static final ByteOrder DEFAULT_ORDER = ByteOrder.BIG_ENDIAN;

    private final ByteBuffer buf;

    public SimplePacketBuffer(ByteBuffer buf) {
        this.buf = buf;
    }

    public SimplePacketBuffer(int capacity, ByteOrder order) {
        this(ByteBuffer.allocate(capacity).order(order));
    }

    public SimplePacketBuffer(int capacity) {
        this(capacity, DEFAULT_ORDER);
    }

    public SimplePacketBuffer(ByteOrder order) {
        this(DEFAULT_CAPACITY, order);
    }

    public SimplePacketBuffer() {
        this(DEFAULT_CAPACITY, DEFAULT_ORDER);
    }

    @Override
    public void writeBlock(byte[] block) {
        if (block.length > Short.MAX_VALUE)
            throw new OutOfMemoryError("Block too large");
        this.buf.putShort((short) block.length);
        this.buf.put(block);
    }

    @Override
    public byte[] readBlock() {
        byte[] block = new byte[this.buf.getShort()];
        this.buf.get(block);
        return block;
    }

    @Override
    public void writeBlockL(byte[] block) {
        this.buf.putInt(block.length);
        this.buf.put(block);
    }

    @Override
    public byte[] readBlockL() {
        byte[] block = new byte[this.buf.getInt()];
        this.buf.get(block);
        return block;
    }

    @Override
    public void writeString(String val) {
        buf.put(val.getBytes(StandardCharsets.UTF_8));
        buf.put((byte) 0x00);
    }

    @Override
    public String readString() {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        byte b;
        while ((b = buf.get()) != 0x00) {
            s.write(b);
        }
        return s.toString(StandardCharsets.UTF_8);
    }

    @Override
    public void writeInt(int val) {
        this.buf.putInt(val);
    }

    @Override
    public int readInt() {
        return this.buf.getInt();
    }

    @Override
    public void writeShort(short val) {
        this.buf.putShort(val);
    }

    @Override
    public short readShort() {
        return this.buf.getShort();
    }

    @Override
    public void writeLong(long val) {
        this.buf.putLong(val);
    }

    @Override
    public long readLong() {
        return this.buf.getLong();
    }

    @Override
    public <E extends Enum<?>> void writeEnum(E val) {
        this.buf.put((byte) val.ordinal());
    }

    @Override
    public <E extends Enum<?>> E readEnum(Class<E> type) {
        return type.getEnumConstants()[this.buf.get()];
    }

    @Override
    public ByteBuffer src() {
        return buf;
    }

    @Override
    public byte[] array() {
        return src().array();
    }
}

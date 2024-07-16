package cn.afternode.simpleprotocol.core;

public interface IPacketBuffer<T> {
    void writeBlock(byte[] block);
    byte[] readBlock();

    void writeBlockL(byte[] block);
    byte[] readBlockL();

    void writeString(String val);
    String readString();

    void writeInt(int val);
    int readInt();

    void writeShort(short val);
    short readShort();

    void writeLong(long val);
    long readLong();

    <E extends Enum<?>> void writeEnum(E val);
    <E extends Enum<?>> E readEnum(Class<E> type);

    T src();
    byte[] array();
}

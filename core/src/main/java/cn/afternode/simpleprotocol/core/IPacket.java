package cn.afternode.simpleprotocol.core;

public interface IPacket<ID, BUF> {
    ID id();
    void read(BUF buffer);
    void write(BUF buffer);
}

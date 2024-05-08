package cn.afternode.simpleprotocol.simple;

public interface IPacketEncryptor {
    byte[] encrypt(byte[] data, Class<? extends ISimplePacket> type);
    byte[] decrypt(byte[] data, Class<? extends ISimplePacket> type);
}

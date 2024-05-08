package cn.afternode.simpleprotocol.simple;

import java.nio.ByteOrder;

public class EncryptedProtocolManager extends SimpleProtocolManager {
    private final IPacketEncryptor encryptor;

    public EncryptedProtocolManager(IPacketEncryptor encryptor, int bufferCapacity, ByteOrder bufferOrder) {
        super(bufferCapacity, bufferOrder);
        this.encryptor = encryptor;
    }

    @Override
    public SimplePacketBuffer encode(ISimplePacket packet) {
        SimplePacketBuffer encoded = this.createBuffer();
        packet.write(encoded);
        byte[] enc = encryptor.encrypt(encoded.array(), packet.getClass());

        SimplePacketBuffer buf = this.createBuffer();
        buf.writeString(packet.id());
        buf.writeBlock(enc);

        return buf;
    }

    @Override
    public AbstractClientPacket decodeClient(SimplePacketBuffer buffer) {
        AbstractClientPacket packet = getClient(buffer.readString());
        byte[] dec = encryptor.decrypt(buffer.readBlock(), packet.getClass());

        SimplePacketBuffer decBuf = createBuffer(dec);
        packet.read(decBuf);

        return packet;
    }

    @Override
    public AbstractServerPacket decodeServer(SimplePacketBuffer buffer) {
        AbstractServerPacket packet = getServer(buffer.readString());
        byte[] dec = encryptor.decrypt(buffer.readBlock(), packet.getClass());

        SimplePacketBuffer decBuf = createBuffer(dec);
        packet.read(decBuf);

        return packet;
    }
}

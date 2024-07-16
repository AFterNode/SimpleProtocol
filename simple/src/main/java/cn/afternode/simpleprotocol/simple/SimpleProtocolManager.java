package cn.afternode.simpleprotocol.simple;

import cn.afternode.simpleprotocol.core.IProtocolManager;
import cn.afternode.simpleprotocol.core.IRequestPacket;
import cn.afternode.simpleprotocol.core.IResponsePacket;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.function.Supplier;

public class SimpleProtocolManager implements IProtocolManager<String, SimplePacketBuffer, ISimplePacket, AbstractClientPacket, AbstractServerPacket> {
    private final int byteBufferSize;
    private final ByteOrder byteOrder;

    protected final Map<String, Supplier<AbstractClientPacket>> client = new LinkedHashMap<>();
    protected final Map<String, Supplier<AbstractServerPacket>> server = new LinkedHashMap<>();
    protected final Map<UUID, IRequestPacket> request = new LinkedHashMap<>();

    public SimpleProtocolManager(int byteBufferSize, ByteOrder byteOrder) {
        this.byteBufferSize = byteBufferSize;
        this.byteOrder = byteOrder;
    }

    public SimpleProtocolManager() {
        this(SimplePacketBuffer.DEFAULT_CAPACITY, SimplePacketBuffer.DEFAULT_ORDER);
    }

    public static SimpleProtocolManager createSystem() {
        Properties prop = System.getProperties();
        return new SimpleProtocolManager(
                prop.contains("afternode.protocol.simple.capacity") ? Integer.parseInt(prop.getProperty("afternode.protocol.simple.capacity")) : 32767,
                ByteOrder.nativeOrder()
        );
    }

    @Override
    public SimplePacketBuffer createBuffer() {
        return new SimplePacketBuffer(byteBufferSize, byteOrder);
    }

    @Override
    public SimplePacketBuffer createBuffer(byte[] data) {
        return new SimplePacketBuffer(ByteBuffer.wrap(data).order(byteOrder));
    }

    @Override
    public void registerClient(String s, Supplier<AbstractClientPacket> supplier) {
        client.put(s, supplier);
    }

    @Override
    public void registerServer(String s, Supplier<AbstractServerPacket> supplier) {
        server.put(s, supplier);
    }

    @Override
    public AbstractClientPacket getClient(String id) {
        return Objects.requireNonNull(client.get(id), "Cannot find packet supplier with ID %s".formatted(id)).get();
    }

    @Override
    public AbstractServerPacket getServer(String id) {
        return Objects.requireNonNull(server.get(id), "Cannot find packet supplier with ID %s".formatted(id)).get();
    }

    @Override
    public SimplePacketBuffer encode(ISimplePacket packet) {
        SimplePacketBuffer buf = new SimplePacketBuffer(byteBufferSize, byteOrder);
        buf.writeString(packet.id());
        packet.write(buf);
        return buf;
    }

    @Override
    public AbstractClientPacket decodeClient(SimplePacketBuffer buffer) {
        AbstractClientPacket pkt = getClient(buffer.readString());
        pkt.read(buffer);
        return pkt;
    }

    @Override
    public AbstractServerPacket decodeServer(SimplePacketBuffer buffer) {
        AbstractServerPacket pkt = getServer(buffer.readString());
        pkt.read(buffer);
        return pkt;
    }

    @Override
    public void registerRequest(IRequestPacket packet) {
        request.put(packet.requestId(), packet);
    }

    @Override
    public IRequestPacket getRequest(UUID id) {
        return request.get(id);
    }

    @Override
    public void removeRequest(UUID id) {
        request.remove(id);
    }

    @Override
    public void handleResponse(IResponsePacket packet) {
        Objects.requireNonNull(request.get(packet.requestId()), "Cannot find request with ID %s".formatted(packet.requestId().toString())).onResponse(packet);
    }
}

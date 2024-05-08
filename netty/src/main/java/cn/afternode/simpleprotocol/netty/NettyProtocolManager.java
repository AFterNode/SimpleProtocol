package cn.afternode.simpleprotocol.netty;

import cn.afternode.simpleprotocol.core.IProtocolManager;
import cn.afternode.simpleprotocol.core.IRequestPacket;
import cn.afternode.simpleprotocol.core.IResponsePacket;
import io.netty.buffer.Unpooled;

import java.util.*;
import java.util.function.Supplier;

public class NettyProtocolManager implements IProtocolManager<Short, NettyPacketBuffer, INettyPacket, AbstractNettyClientPacket, AbstractNettyServerPacket> {
    private boolean registryClosed = false;

    private Map<Short, Supplier<AbstractNettyClientPacket>> client = new LinkedHashMap<>();
    private Map<Short, Supplier<AbstractNettyServerPacket>> server = new LinkedHashMap<>();
    private final Map<UUID, IRequestPacket> requests = new LinkedHashMap<>();

    @Override
    public NettyPacketBuffer createBuffer() {
        return new NettyPacketBuffer(Unpooled.buffer());
    }

    @Override
    public NettyPacketBuffer createBuffer(byte[] data) {
        return new NettyPacketBuffer(Unpooled.copiedBuffer(data));
    }

    @Override
    public void registerClient(Short id, Supplier<AbstractNettyClientPacket> supplier) {
        client.put(id, supplier);
    }

    @Override
    public void registerServer(Short id, Supplier<AbstractNettyServerPacket> supplier) {
        server.put(id, supplier);
    }

    @Override
    public AbstractNettyClientPacket getClient(Short id) {
        return Objects.requireNonNull(client.get(id), "Cannot find client packet supplier with ID %s".formatted(id)).get();

    }

    @Override
    public AbstractNettyServerPacket getServer(Short id) {
        return Objects.requireNonNull(server.get(id), "Cannot find server packet supplier with ID %s".formatted(id)).get();
    }

    @Override
    public NettyPacketBuffer encode(INettyPacket packet) {
        NettyPacketBuffer buffer = createBuffer();
        buffer.writeShort(packet.id());
        packet.write(buffer);
        return buffer;
    }

    @Override
    public AbstractNettyClientPacket decodeClient(NettyPacketBuffer buffer) {
        AbstractNettyClientPacket packet = getClient(buffer.readShort());
        packet.read(buffer);
        return packet;
    }

    @Override
    public AbstractNettyServerPacket decodeServer(NettyPacketBuffer buffer) {
        AbstractNettyServerPacket packet = getServer(buffer.readShort());
        packet.read(buffer);
        return packet;
    }

    @Override
    public void registerRequest(IRequestPacket packet) {
        requests.put(packet.requestId(), packet);
    }

    @Override
    public IRequestPacket getRequest(UUID id) {
        return requests.get(id);
    }

    @Override
    public void removeRequest(UUID id) {
        requests.remove(id);
    }

    @Override
    public void handleResponse(IResponsePacket packet) {
        Objects.requireNonNull(requests.get(packet.requestId()), () -> "Cannot find request packet with id %s".formatted(packet.requestId().toString())).onResponse(packet);
    }

    @Override
    public void closeRegistry() {
        this.registryClosed = true;
        client = Collections.unmodifiableMap(client);
        server = Collections.unmodifiableMap(server);
    }

    @Override
    public boolean canRegister() {
        return this.registryClosed;
    }
}

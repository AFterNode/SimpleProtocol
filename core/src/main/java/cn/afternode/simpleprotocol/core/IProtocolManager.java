package cn.afternode.simpleprotocol.core;

import java.util.UUID;
import java.util.function.Supplier;

public interface IProtocolManager<ID, BUF,
        P extends IPacket<ID, BUF>,
        C extends P,
        S extends P> {
    BUF createBuffer();
    BUF createBuffer(byte[] data);

    void registerClient(ID id, Supplier<C> supplier);
    void registerServer(ID id, Supplier<S> supplier);
    C getClient(ID id);
    S getServer(ID id);

    BUF encode(P packet);
    C decodeClient(BUF buffer);
    S decodeServer(BUF buffer);

    void registerRequest(IRequestPacket packet);
    IRequestPacket getRequest(UUID id);
    void removeRequest(UUID id);
    void handleResponse(IResponsePacket packet);
}

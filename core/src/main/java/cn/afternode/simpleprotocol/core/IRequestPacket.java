package cn.afternode.simpleprotocol.core;

import java.util.UUID;

public interface IRequestPacket {
    UUID requestId();
    void onResponse(IResponsePacket response);
}

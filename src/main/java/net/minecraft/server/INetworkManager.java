package net.minecraft.server;

import java.net.SocketAddress;

public interface INetworkManager {

    void a(Connection connection);

    void queue(Packet packet);

    void a();

    void b();

    SocketAddress getSocketAddress();

    void d();

    int e();

    void a(String s, Object... aobject);

    // Poweruser start
    java.net.Socket getSocket();

    void setSocketAddress(java.net.SocketAddress address);
    // Poweruser end
}

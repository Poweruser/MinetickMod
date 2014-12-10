package de.minetick.packetbuilder.jobs;

import net.minecraft.server.PacketPlayOutMapChunkBulk;
import net.minecraft.server.PlayerConnection;
import de.minetick.PlayerChunkSendQueue;
import de.minetick.packetbuilder.PacketBuilderBuffer;
import de.minetick.packetbuilder.PacketBuilderChunkDataBulk;
import de.minetick.packetbuilder.PacketBuilderJobInterface;
import de.minetick.packetbuilder.PacketBuilderThreadPool;

public class PBJobPlayOutMapChunkBulk implements PacketBuilderJobInterface {

    private PlayerConnection connection;
    private PlayerChunkSendQueue chunkQueue;
    private PacketBuilderChunkDataBulk chunkData;

    private PacketBuilderBuffer pbb;

    public PBJobPlayOutMapChunkBulk(PlayerConnection connection, PacketBuilderChunkDataBulk chunkData, PlayerChunkSendQueue chunkQueue) {
        this.connection = connection;
        this.chunkData = chunkData;
        this.chunkQueue = chunkQueue;
    }

    @Override
    public void run() {
        if(this.chunkQueue == null || this.connection == null) {
            this.chunkData.clear();
            this.clear();
        }
        PacketPlayOutMapChunkBulk packet = new PacketPlayOutMapChunkBulk(this.pbb, this.chunkData.getChunks());
        boolean allStillListed = this.chunkData.verifyLoadedChunks(this.chunkQueue);
        if(allStillListed) {
            packet.setPendingUses(1);
            this.connection.sendPacket(packet);
            this.chunkData.sendTileEntities(this.connection);
            this.chunkData.queueChunksForTracking(this.connection.player, this.chunkQueue);
            this.chunkData.clear();
        } else {
            packet.discard();
        }
        if(!allStillListed && !this.chunkData.isEmpty()) {
            PacketBuilderThreadPool.addJobStatic(new PBJobPlayOutMapChunkBulk(this.connection, this.chunkData, this.chunkQueue));
        }
        this.clear();
    }

    private void clear() {
        this.chunkData = null;
        this.connection = null;
        this.chunkQueue = null;
        this.pbb = null;
    }

    @Override
    public void assignBuildBuffer(PacketBuilderBuffer pbb) {
        this.pbb = pbb;
    }
}

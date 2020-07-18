package net.minestom.server.listener;

import net.minestom.server.MinecraftServer;
import net.minestom.server.data.Data;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerUseItemOnBlockEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.instance.block.BlockManager;
import net.minestom.server.instance.block.CustomBlock;
import net.minestom.server.instance.block.rule.BlockPlacementRule;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.StackingRule;
import net.minestom.server.network.packet.client.play.ClientPlayerBlockPlacementPacket;
import net.minestom.server.utils.BlockPosition;
import net.minestom.server.utils.chunk.ChunkUtils;

import java.util.Set;

public class BlockPlacementListener {

    public static void listener(ClientPlayerBlockPlacementPacket packet, Player player) {
        PlayerInventory playerInventory = player.getInventory();
        Player.Hand hand = packet.hand;
        BlockFace blockFace = packet.blockFace;
        BlockPosition blockPosition = packet.blockPosition;

        Instance instance = player.getInstance();
        if (instance == null)
            return;

        // Interact at block
        PlayerBlockInteractEvent playerBlockInteractEvent = new PlayerBlockInteractEvent(blockPosition, hand, blockFace);
        player.callCancellableEvent(PlayerBlockInteractEvent.class, playerBlockInteractEvent, () -> {
            CustomBlock customBlock = instance.getCustomBlock(blockPosition);
            if (customBlock != null) {
                Data data = instance.getBlockData(blockPosition);
                boolean blocksItem = customBlock.onInteract(player, hand, blockPosition, data);
                if(blocksItem) {
                    playerBlockInteractEvent.setBlockingItemUse(true);
                }
            }
        });

        if(playerBlockInteractEvent.isBlockingItemUse()) {
            return;
        }

        // Check if item at hand is a block
        ItemStack usedItem = hand == Player.Hand.MAIN ? playerInventory.getItemInMainHand() : playerInventory.getItemInOffHand();
        Material material = Material.fromId(usedItem.getMaterialId());
        if(material == Material.AIR) {
            return;
        }

        // Get the newly placed block position
        int offsetX = blockFace == BlockFace.WEST ? -1 : blockFace == BlockFace.EAST ? 1 : 0;
        int offsetY = blockFace == BlockFace.BOTTOM ? -1 : blockFace == BlockFace.TOP ? 1 : 0;
        int offsetZ = blockFace == BlockFace.NORTH ? -1 : blockFace == BlockFace.SOUTH ? 1 : 0;

        blockPosition.add(offsetX, offsetY, offsetZ);


        Chunk chunk = instance.getChunkAt(blockPosition);
        boolean refreshChunk = false;

        if (material.isBlock()) {
            Block block = material.getBlock();
            Set<Entity> entities = instance.getChunkEntities(chunk);
            boolean intersect = false;
            if (block.isSolid()) {
                for (Entity entity : entities) {
                    intersect = entity.getBoundingBox().intersect(blockPosition);
                    if (intersect)
                        break;
                }
            }

            if (!intersect) {
                // BlockPlacementRule check
                BlockManager blockManager = MinecraftServer.getBlockManager();
                BlockPlacementRule blockPlacementRule = blockManager.getBlockPlacementRule(block);
                short blockid = block.getBlockId();
                if (blockPlacementRule != null) {
                    blockid = blockPlacementRule.blockPlace(instance, block, blockFace, player);
                }
                PlayerBlockPlaceEvent playerBlockPlaceEvent = new PlayerBlockPlaceEvent(player, blockid, (short) 0, blockPosition, packet.hand);
                playerBlockPlaceEvent.consumeBlock(player.getGameMode() != GameMode.CREATIVE);

                // BlockPlacementRule check
                boolean canPlace = true;
                if (blockPlacementRule != null) {
                    canPlace = blockPlacementRule.canPlace(instance, blockPosition);
                }

                player.callEvent(PlayerBlockPlaceEvent.class, playerBlockPlaceEvent);
                if (!playerBlockPlaceEvent.isCancelled() && canPlace) {
                    short customBlockId = playerBlockPlaceEvent.getCustomBlockId();
                    if(customBlockId != 0) {
                        instance.setSeparateBlocks(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), playerBlockPlaceEvent.getBlockId(), playerBlockPlaceEvent.getCustomBlockId());
                    } else {
                        instance.setBlock(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(), playerBlockPlaceEvent.getBlockId());
                    }
                    if (playerBlockPlaceEvent.doesConsumeBlock()) {

                        StackingRule stackingRule = usedItem.getStackingRule();
                        ItemStack newUsedItem = stackingRule.apply(usedItem, stackingRule.getAmount(usedItem) - 1);

                        if (hand == Player.Hand.OFF) {
                            playerInventory.setItemInOffHand(newUsedItem);
                        } else { // Main
                            playerInventory.setItemInMainHand(newUsedItem);
                        }
                    }
                } else {
                    refreshChunk = true;
                }
            } else {
                refreshChunk = true;
            }
        } else {
            PlayerUseItemOnBlockEvent event = new PlayerUseItemOnBlockEvent(player, hand, usedItem, blockPosition, blockFace.toDirection());
            player.callEvent(PlayerUseItemOnBlockEvent.class, event);
            refreshChunk = true;
        }

        // Refresh chunk section if needed
        if (refreshChunk) {
            instance.sendChunkSectionUpdate(chunk, ChunkUtils.getSectionAt(blockPosition.getY()), player);
        }

        player.getInventory().refreshSlot(player.getHeldSlot());
    }

}

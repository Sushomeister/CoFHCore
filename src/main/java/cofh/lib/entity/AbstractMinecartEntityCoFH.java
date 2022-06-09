package cofh.lib.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Map;

import static cofh.lib.util.constants.NBTTags.TAG_ENCHANTMENTS;
import static cofh.lib.util.references.CoreReferences.HOLDING;
import static net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND;

public abstract class AbstractMinecartEntityCoFH extends AbstractMinecartEntity {

    protected ListNBT enchantments = new ListNBT();

    protected AbstractMinecartEntityCoFH(EntityType<?> type, World worldIn) {

        super(type, worldIn);
    }

    protected AbstractMinecartEntityCoFH(EntityType<?> type, World worldIn, double posX, double posY, double posZ) {

        super(type, worldIn, posX, posY, posZ);
    }

    public AbstractMinecartEntityCoFH onPlaced(ItemStack stack) {

        this.enchantments = stack.getEnchantmentTags();
        return this;
    }

    protected float getHoldingMod(Map<Enchantment, Integer> enchantmentMap) {

        int holding = enchantmentMap.getOrDefault(HOLDING, 0);
        return 1 + holding / 2F;
    }

    public ItemStack createItemStackTag(ItemStack stack) {

        if (this.hasCustomName()) {
            stack.setHoverName(this.getCustomName());
        }
        if (!this.enchantments.isEmpty()) {
            stack.addTagElement(TAG_ENCHANTMENTS, enchantments);
        }
        return stack;
    }

    @Override
    public void readAdditionalSaveData(CompoundNBT compound) {

        super.readAdditionalSaveData(compound);

        enchantments = compound.getList(TAG_ENCHANTMENTS, TAG_COMPOUND);
    }

    @Override
    public void addAdditionalSaveData(CompoundNBT compound) {

        super.addAdditionalSaveData(compound);

        compound.put(TAG_ENCHANTMENTS, enchantments);
    }

    @Override
    public void destroy(DamageSource source) {

        this.remove();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            ItemStack stack = createItemStackTag(getCartItem());
            this.spawnAtLocation(stack);
        }
    }

    @Override
    public IPacket<?> getAddEntityPacket() {

        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
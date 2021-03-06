package net.dblsaiko.rswires.common.init

import net.dblsaiko.hctm.common.block.BaseWireBlockEntity
import net.dblsaiko.hctm.common.util.delegatedNotNull
import net.dblsaiko.rswires.MOD_ID
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.block.entity.BlockEntityType.Builder
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import java.util.function.Supplier
import kotlin.properties.ReadOnlyProperty

object BlockEntityTypes {

  private val tasks = mutableListOf<() -> Unit>()

  val RED_ALLOY_WIRE by create("red_alloy_wire", ::BaseWireBlockEntity)
  val INSULATED_WIRE by create("insulated_wire", ::BaseWireBlockEntity)
  val BUNDLED_CABLE by create("bundled_cable", ::BaseWireBlockEntity)

  private fun <T : BlockEntity> create(name: String, builder: () -> T, vararg blocks: Block): ReadOnlyProperty<BlockEntityTypes, BlockEntityType<T>> {
    var regType: BlockEntityType<T>? = null
    tasks += { regType = Registry.register(Registry.BLOCK_ENTITY_TYPE, Identifier(MOD_ID, name), Builder.create(Supplier(builder), *blocks).build(null)) }
    return delegatedNotNull { regType }
  }

  private fun <T : BlockEntity> create(name: String, builder: (BlockEntityType<T>) -> T): ReadOnlyProperty<BlockEntityTypes, BlockEntityType<T>> {
    var regType: BlockEntityType<T>? = null
    tasks += {
      var type: BlockEntityType<T>? = null
      val s = Supplier { builder(type!!) }
      type = Builder.create(s).build(null)
      regType = Registry.register(Registry.BLOCK_ENTITY_TYPE, Identifier(MOD_ID, name), type)
    }
    return delegatedNotNull { regType }
  }

  internal fun register() {
    tasks.forEach { it() }
    tasks.clear()
  }

}
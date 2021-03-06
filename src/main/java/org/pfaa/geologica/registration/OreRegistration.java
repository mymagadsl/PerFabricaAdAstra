package org.pfaa.geologica.registration;

import org.pfaa.chemica.item.IndustrialMaterialItem;
import org.pfaa.chemica.model.Aggregate;
import org.pfaa.chemica.model.IndustrialMaterial;
import org.pfaa.chemica.processing.Form.Forms;
import org.pfaa.chemica.registration.OreDictUtils;
import org.pfaa.geologica.GeoMaterial;
import org.pfaa.geologica.GeologicaBlocks;
import org.pfaa.geologica.GeologicaItems;
import org.pfaa.geologica.block.BrickGeoBlock;
import org.pfaa.geologica.block.BrokenGeoBlock;
import org.pfaa.geologica.block.GeoBlock;
import org.pfaa.geologica.block.IntactGeoBlock;
import org.pfaa.geologica.block.LooseGeoBlock;
import org.pfaa.geologica.block.ProxyBlock;
import org.pfaa.geologica.block.SlabBlock;
import org.pfaa.geologica.block.StairsBlock;
import org.pfaa.geologica.block.VanillaOreOverrideBlock;
import org.pfaa.geologica.block.WallBlock;
import org.pfaa.geologica.processing.Crude;
import org.pfaa.geologica.processing.IndustrialMineral.IndustrialMinerals;
import org.pfaa.geologica.processing.Ore;
import org.pfaa.geologica.processing.OreMineral;
import org.pfaa.geologica.processing.OreMineral.Ores;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreRegistration {
	
	public static void init() {
		oreDictifyGeoBlocks();
		oreDictifyMaterialItems();
		oreDictifyStoneBrick();
		registerDyes();
	}

	private static void registerDyes() {
		registerDyes(Ores.values());
		registerDyes(GeoMaterial.values());
		OreDictUtils.registerDye("blue", IndustrialMinerals.AZURITE);
		OreDictUtils.registerDye("blue", IndustrialMinerals.LAZURITE);
		OreDictUtils.registerDye("gray", IndustrialMinerals.GRAPHITE);
	}

	private static void registerDyes(Ores[] ores) {
		for (OreMineral ore : ores) {
			registerDye(ore.getConcentrate(), ore);
		}
	}

	private static void registerDyes(GeoMaterial[] materials) {
		for (GeoMaterial material : materials) {
			IndustrialMaterial composition = material.getComposition();
			if (composition instanceof Ore) {
				registerDye(((Ore)composition).getConcentrate(), material);
			}
		}
	}
	
	private static void registerDye(IndustrialMaterial concentrate, IndustrialMaterial material) {
		ItemStack itemStack = OreDictUtils.lookupBest(Forms.DUST, concentrate);
		for (int id : OreDictionary.getOreIDs(itemStack)) {
			String name = OreDictionary.getOreName(id);
			if (name.startsWith("dye")) {
				ItemStack materialStack = OreDictUtils.lookupBest(Forms.DUST, material);
				if (materialStack == null) {
					materialStack = OreDictUtils.lookupBest(Forms.CLUMP, material);
				}
				OreDictionary.registerOre(name, materialStack);
			}
		}
	}

	private static void oreDictifyMaterialItems() {
		for (IndustrialMaterialItem<?> item : GeologicaItems.getIndustrialMaterialItems()) {
			OreDictUtils.register(item);
		}
	}
	
	// FIXME: remove this hack when we get this into Forge
	private static void oreDictifyStoneBrick() {
		OreDictionary.registerOre("stoneBricks", Blocks.stonebrick);
	}

	private static void oreDictifyGeoBlocks() {
		for (Block block : GeologicaBlocks.getBlocks()) {
			oreDictify(block);
		}
	}
	
	private static void oreDictify(Block block) {
		if (block instanceof GeoBlock) {
			oreDictify((GeoBlock)block);
		} else if (block instanceof ProxyBlock) {
			oreDictify((ProxyBlock)block);
		} else if (block instanceof VanillaOreOverrideBlock) {
			oreDictify((VanillaOreOverrideBlock)block);
		}
	}
	
	private static void oreDictify(GeoBlock block) {
		for (GeoMaterial material : block.getGeoMaterials()) {
			if (block.hasComposition(Aggregate.class)) {
				oreDictifyAggregate(block, material);
			} else if (block.hasComposition(Ore.class) || 
				(block.hasComposition(Crude.class) && block.getMaterial() == Material.rock)) {
				oreDictifyOre(block, material);
			}
		}
	}

	private static void oreDictifyOre(GeoBlock block, GeoMaterial substance) {
		ItemStack oreStack = block.getItemStack(substance);
		String key = OreDictUtils.makeKey("ore", substance.getOreDictKey());
		OreDictionary.registerOre(key, oreStack);
	}
	
	private static String getAggregateOreDictKey(GeoBlock block) {
		if (block.getMaterial() == Material.clay && block instanceof IntactGeoBlock) {
			return "blockClay";
		} else if (block.getMaterial() == Material.sand) {
			return "sand";
		} else if (block instanceof BrokenGeoBlock) {
			return "cobblestone";
		} else if (block instanceof BrickGeoBlock) {
			return "stoneBricks";
		} else if (block instanceof LooseGeoBlock) {
			return "rubble";
		}
		return "stone";
	}
	
	private static void oreDictify(ProxyBlock block) {
		String prefix = null;
		if (block instanceof StairsBlock) {
			prefix = "stair";
		} else if (block instanceof SlabBlock) {
			prefix = "slab";
		} else if (block instanceof WallBlock) {
			prefix = "wall";
		}
		String postfix = getAggregateOreDictKey((GeoBlock)block.getModelBlock());
		String key = OreDictUtils.makeKey(prefix, postfix);
		OreDictionary.registerOre(key, new ItemStack((Block)block, 1, OreDictionary.WILDCARD_VALUE));
	}

	private static void oreDictifyAggregate(GeoBlock block, GeoMaterial material) {
		String key = getAggregateOreDictKey(block);
		ItemStack itemStack = block.getItemStack(material);
		OreDictionary.registerOre(key, itemStack);
	}
	
	private static void oreDictify(VanillaOreOverrideBlock block) {
		String name = Block.blockRegistry.getNameForObject(block);
		if (name != null) {
			String material = name.substring(name.indexOf(':') + 1, name.length() - 3);
			OreDictionary.registerOre(OreDictUtils.makeKey("ore", material), new ItemStack(block, 1, OreDictionary.WILDCARD_VALUE));
		}
	}

}

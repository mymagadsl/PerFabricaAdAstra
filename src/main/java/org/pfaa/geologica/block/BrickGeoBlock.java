package org.pfaa.geologica.block;

import org.pfaa.chemica.model.IndustrialMaterial;
import org.pfaa.chemica.model.Strength;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BrickGeoBlock extends GeoBlock {

	public BrickGeoBlock(Strength strength, Class<? extends IndustrialMaterial> composition, Material material) {
		super(strength, composition, material, true);
	}

	@Override
	protected float determineHardness() {
		float hardness = super.determineHardness();
		if (blockMaterial == Material.clay)
			hardness = hardness * 3.0F; 
		return hardness;
	}

	@Override
	protected IIcon registerOverlayIcon(IIconRegister registry, int i) {
		return registry.registerIcon("geologica:brickOverlay");
	}
	
	@Override
	protected IIcon registerUnderlayIcon(IIconRegister registry, int i) {
		return getNative(this.getGeoMaterial(i)).block.registerUnderlayIcon(registry, i);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean useMultipassRendering() {
		return true;
	}

	@Override
	public GeoBlock getBrokenRockBlock() {
		return IntactGeoBlock.getBrokenRockBlock(this.getStrength());
	}
}

package igentuman.mt.block;

import igentuman.mt.setup.ModRegistration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MolecularTransformerBlockEntity extends AbstractMolecularTransformerBlockEntity {
    private static final int MAX_ENERGY = 100000; // 100k FE capacity
    private static final int OUTPUT_COUNT = 1; // Produces 1 item

    public MolecularTransformerBlockEntity(BlockPos pos, BlockState blockState, String identifier) {
        super(ModRegistration.getEntry(identifier).blockEntity().get(), pos, blockState, MAX_ENERGY, OUTPUT_COUNT);
    }
}